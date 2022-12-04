package com.hvs.annihilation.screen

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
import com.hvs.annihilation.audio.AudioSystem
import com.hvs.annihilation.ecs.ai.AiComponent
import com.hvs.annihilation.ecs.ai.AiSystem
import com.hvs.annihilation.ecs.animation.AnimationSystem
import com.hvs.annihilation.ecs.attack.AttackSystem
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
import com.hvs.annihilation.input.InputConverter
import com.hvs.annihilation.input.InputHandler
import com.hvs.annihilation.state.PlayerEntity
import com.hvs.annihilation.ui.createSkin
import com.hvs.annihilation.ui.widget.LoadingBarWidget
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.actors.centerPosition
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import ktx.assets.disposeSafely
import ktx.async.KtxAsync
import ktx.box2d.createWorld
import ktx.box2d.earthGravity
import ktx.log.logger

class LoadingScreen(
    private val game: Annihilation,
    private val assets: AssetStorage,
    private val bundle: I18NBundle
) : KtxScreen {

    private val textureAtlasUi = TextureAtlas(TextureAtlasAssets.UI.filePath)
    private val textureAtlas = TextureAtlas(TextureAtlasAssets.GAME_OBJECTS.filePath)

    private val startStage: Stage = Stage(ExtendViewport(32f, 18f))
    private val startUiStage: Stage = Stage(ExtendViewport(1280f, 720f))

    private val loadingBar = LoadingBarWidget(bundle, createSkin(textureAtlasUi))
    private var loaded = false


    override fun show() {
        log.debug { "This is the loading screen" }

        // queue all assets that should be loaded
        val assetReferences = listOf(
            MusicAssets.values().map { assets.loadAsync(it) },
            SoundAssets.values().filter { it != SoundAssets.UNKNOWN }.map { assets.loadAsync(it) },
//            TextureAtlasAssets.values().filter { it != TextureAtlasAssets.UI }.map { assets.loadAsync(it) }, //TODO: assets do not get injected
            MapAssets.values().map { assets.loadAsync(it) }
        ).flatten()


        startUiStage.clear()
        startUiStage.addActor(loadingBar)
        loadingBar.centerPosition(startUiStage.width * 0.5f, startUiStage.height * 0.15f)

        KtxAsync.launch {
            // Awaiting until all assets are loaded:
            assetReferences.joinAll()
            initiateResources()
        }
    }


    override fun resize(width: Int, height: Int) {
        startStage.viewport.update(width, height, true)
        startUiStage.viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        loadingBar.scaleTo(assets.progress.percent)

        if (loaded) {
            // go to the menu screen once everything is loaded
            game.setScreen<StartScreen>()
            // cleanup loading screen stuff
            game.removeScreen<LoadingScreen>()
            dispose()
        }

        startUiStage.act()
        startUiStage.draw()
    }

    override fun dispose() {
        startStage.disposeSafely()
        startUiStage.disposeSafely()
        textureAtlasUi.disposeSafely()
    }

    private fun initiateResources() {
        val stage = Stage(ExtendViewport(32f, 18f))
        val uiStage = Stage(ExtendViewport(1280f, 720f))
        val physicsWorld = createWorld(gravity = earthGravity).apply { autoClearForces = false }
        val world: World = entityWorld(stage, uiStage, physicsWorld)

        // all assets are loaded -> add remaining screens to our game now because
        // now they can access the different assets that they need
        game.addScreen(
            StartScreen(
                game,
                stage,
                uiStage,
                bundle
            )
        )
        game.addScreen(
            GameScreen(
                stage,
                uiStage,
                world,
                physicsWorld
            )
        )

        loaded = true
    }

    private fun entityWorld(
        gameStage: Stage,
        uiStage: Stage,
        physicsWorld: com.badlogic.gdx.physics.box2d.World
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
