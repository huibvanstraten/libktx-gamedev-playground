package com.hvs.annihilation.screen

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.quillraven.fleks.World
import com.hvs.annihilation.Annihilation
import com.hvs.annihilation.assets.MapAssets
import com.hvs.annihilation.assets.MusicAssets
import com.hvs.annihilation.assets.SoundAssets
import com.hvs.annihilation.assets.TextureAtlasAssets
import com.hvs.annihilation.assets.loadAsync
import com.hvs.annihilation.ecs.ai.AiComponent
import com.hvs.annihilation.ecs.ai.AiSystem
import com.hvs.annihilation.ecs.animation.AnimationSystem
import com.hvs.annihilation.ecs.attack.AttackSystem
import com.hvs.annihilation.ecs.audio.AudioSystem
import com.hvs.annihilation.ecs.camera.CameraSystem
import com.hvs.annihilation.ecs.collision.CollisionDespawnSystem
import com.hvs.annihilation.ecs.collision.CollisionSpawnSystem
import com.hvs.annihilation.ecs.dead.DeadSystem
import com.hvs.annihilation.ecs.debug.DebugSystem
import com.hvs.annihilation.ecs.floatingtext.FloatingTextComponent
import com.hvs.annihilation.ecs.floatingtext.FloatingTextSystem
import com.hvs.annihilation.ecs.image.ImageComponent
import com.hvs.annihilation.ecs.jump.PhysicJumpSystem
import com.hvs.annihilation.ecs.life.LifeSystem
import com.hvs.annihilation.ecs.loot.LootSystem
import com.hvs.annihilation.ecs.move.MoveSystem
import com.hvs.annihilation.ecs.physics.PhysicsComponent
import com.hvs.annihilation.ecs.physics.PhysicsSystem
import com.hvs.annihilation.ecs.render.RenderSystem
import com.hvs.annihilation.ecs.spawn.EntitySpawnSystem
import com.hvs.annihilation.ecs.state.StateComponent
import com.hvs.annihilation.ecs.state.StateSystem
import com.hvs.annihilation.state.PlayerEntity
import com.hvs.annihilation.ui.createSkin
import com.hvs.annihilation.ui.widget.LoadingBarWidget
import com.hvs.annihilation.ui.widget.loadingBarWidget
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.actors.centerPosition
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.box2d.createWorld
import ktx.box2d.earthGravity
import ktx.log.logger
import ktx.scene2d.actors

class LoadingScreen(
    private val game: Annihilation,
    private val assets: AssetStorage,
    private val bundle: I18NBundle
) : KtxScreen {

    private val textureAtlas = TextureAtlas(TextureAtlasAssets.GAME_OBJECTS.filePath)
    private val batch = SpriteBatch()
    private val gameStage: Stage = Stage(ExtendViewport(32f, 18f), batch)
    private val uiStage: Stage = Stage(ExtendViewport(1280f, 720f), batch)

    lateinit var loadingBar: LoadingBarWidget
    private var loaded = false

    override fun show() {
        log.debug { "This is the loading screen" }

        // queue all assets that should be loaded
        val assetReferences = listOf(
            MusicAssets.values().map { assets.loadAsync(it) },
            SoundAssets.values().filter { it != SoundAssets.UNKNOWN }.map { assets.loadAsync(it) },
            TextureAtlasAssets.values().filter { it != TextureAtlasAssets.UI }
                .map { assets.loadAsync(it) }, //TODO: assets do not get injected
            MapAssets.values().map { assets.loadAsync(it) }
        ).flatten()

        uiStage.actors {
            loadingBar = loadingBarWidget(bundle, createSkin(TextureAtlas(TextureAtlasAssets.UI.filePath))).apply {
                centerPosition(uiStage.width * 0.5f, uiStage.height * 0.15f)
            }
        }

        KtxAsync.launch {
            // Awaiting until all assets are loaded:
            assetReferences.joinAll()
            initiateResources()
        }
    }

    override fun resize(width: Int, height: Int) {
        gameStage.viewport.update(width, height, true)
        uiStage.viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        loadingBar.scaleTo(assets.progress.percent)

        if (loaded) {
            uiStage.clear()
            // go to the menu screen once everything is loaded
            game.setScreen<TitleScreen>()
            // cleanup loading screen stuff
            game.removeScreen<LoadingScreen>()
            dispose()
        }

        uiStage.act()
        uiStage.draw()
    }

    private fun initiateResources() {
        val tempUiStage = Stage(ExtendViewport(720f, 400f))
        val skin = createSkin(TextureAtlas(TextureAtlasAssets.GAMEUI.filePath))
        val physicsWorld = createWorld(gravity = earthGravity).apply { autoClearForces = false }
        val world: World = entityWorld(gameStage, tempUiStage, physicsWorld)
        val playerEntity = PlayerEntity(world.entity(), world)

        // all assets are loaded -> add remaining screens to our game now because
        // now they can access the different assets that they need
        game.addScreen(
            TitleScreen(
                game,
                gameStage,
                uiStage,
                bundle,
                world,
                playerEntity
            )
        )
        game.addScreen(
            GameScreen(
                gameStage,
                tempUiStage,
                skin,
                world,
                physicsWorld,
                playerEntity
            )
        )
        loaded = true
    }

    private fun entityWorld(
        gameStage: Stage,
        uiStage: Stage,
        physicsWorld: com.badlogic.gdx.physics.box2d.World,
    ): World = World {
        inject(gameStage)
        inject("uiStage", uiStage)
        inject(textureAtlas)
        inject(physicsWorld)

        componentListener<PhysicsComponent.Companion.PhysicsComponentListener>()
        componentListener<ImageComponent.Companion.ImageComponentListener>()
        componentListener<FloatingTextComponent.Companion.FloatingTextComponentListener>()
        componentListener<StateComponent.Companion.StateComponentListener>()
        componentListener<AiComponent.Companion.AiComponentListener>()

        system<EntitySpawnSystem>()
        system<CollisionSpawnSystem>()
        system<CollisionDespawnSystem>()
        system<MoveSystem>()
        system<AttackSystem>()
        system<LootSystem>()
        system<DeadSystem>()
        system<LifeSystem>()
        system<PhysicsSystem>()
        system<PhysicJumpSystem>()
        system<AnimationSystem>()
        system<StateSystem>()
        system<AiSystem>()
        system<CameraSystem>()
        system<FloatingTextSystem>()
        system<RenderSystem>()
        system<AudioSystem>()
        system<DebugSystem>()
    }

    companion object {
        private val log = logger<GameScreen>()
    }
}
