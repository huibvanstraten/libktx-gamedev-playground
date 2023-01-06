package com.hvs.annihilation.ecs.render

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.FloatArray
import com.hvs.annihilation.event.MapChangeEvent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.Qualifier
import com.github.quillraven.fleks.collection.compareEntity
import com.hvs.annihilation.Annihilation.Companion.UNIT_SCALE
import com.hvs.annihilation.ecs.image.ImageComponent
import ktx.assets.disposeSafely
import ktx.graphics.use
import ktx.tiled.forEachLayer
import ktx.tiled.property

@AllOf([ImageComponent::class])
class RenderSystem(
    private val gameStage: Stage,
    @Qualifier("uiStage") private val uiStage: Stage,
    private val imageComponents: ComponentMapper<ImageComponent>
) : EventListener,
    IteratingSystem(comparator = compareEntity { e1, e2 -> imageComponents[e1].compareTo(imageComponents[e2]) }
    ) {

    private val backgroundLayers = mutableListOf<TiledMapTileLayer>()
    private val foregroundLayers = mutableListOf<TiledMapTileLayer>()
    private val mapRenderer = OrthogonalTiledMapRenderer(null, UNIT_SCALE, gameStage.batch)
    private val orthoCam = gameStage.camera as OrthographicCamera
    private val mapParallaxValues = FloatArray()

    override fun onTick() {
        super.onTick()

        with(gameStage) {
            viewport.apply()

            AnimatedTiledMapTile.updateAnimationBaseTime() //for animations in the map. mapRenderer.Render() does this automatically
            mapRenderer.setView(orthoCam)

            val parallaxMinWidth = camera.viewportWidth * 0.5f

            //TODO: refactor
            foregroundLayers.forEach { layer ->
                mapParallaxValues.add(layer.property("ParallaxValue", 0f))
            }

            backgroundLayers.forEach { layer ->
                mapParallaxValues.add(layer.property("ParallaxValue", 0f))
            }

            if (backgroundLayers.isNotEmpty()) {
                gameStage.batch.use(orthoCam.combined) {
                    backgroundLayers.forEachIndexed { index, layer ->
                        renderTileLayer(layer, index, parallaxMinWidth)
                    }
                }
            }

            act(deltaTime)
            draw()

            if (foregroundLayers.isNotEmpty()) {
                gameStage.batch.use(orthoCam.combined) {
                    foregroundLayers.forEachIndexed { index, layer ->
                        renderTileLayer(layer, index + backgroundLayers.size, parallaxMinWidth)
                    }
                }
            }

            //render UI
            uiStage.run {
                viewport.apply()
                act(deltaTime)
                draw()
            }
        }
    }

    override fun onTickEntity(entity: Entity) {
        imageComponents[entity].image.toFront()
    }

    override fun handle(event: Event): Boolean {
        when (event) {
            is MapChangeEvent -> {
                backgroundLayers.clear()
                foregroundLayers.clear()

                event.map.forEachLayer<TiledMapTileLayer> { layer ->
                    if (layer.name.startsWith("fore")) {
                        foregroundLayers.add(layer)
                    } else {
                        backgroundLayers.add(layer)
                    }
                }
                return true
            }
        }
        return false
    }

    override fun onDispose() {
        mapRenderer.disposeSafely()
    }

    //TODO: refactor
    private fun renderTileLayer(layer: TiledMapTileLayer, parallaxIndex: Int, minWidth: Float) {
        val parallaxValue = mapParallaxValues[parallaxIndex]
        val camPos = orthoCam.position
        if (parallaxValue == 0f || camPos.x <= minWidth) {
            // tile layer has no parallax value or minimum width is not yet reached to trigger
            // the parallax effect
            mapRenderer.renderTileLayer(layer)
        } else {
            // make parallax effect by drawing the layer offset to its original value and
            // therefore creating a sort of "move" effect for the user
            val origVal = camPos.x
            camPos.x += (minWidth - camPos.x) * parallaxValue
            orthoCam.update()
            mapRenderer.setView(orthoCam)
            mapRenderer.renderTileLayer(layer)
            // reset the camera to its original position to draw remaining stuff with original values
            camPos.x = origVal
            orthoCam.update()
            mapRenderer.setView(orthoCam)
        }
    }
}
