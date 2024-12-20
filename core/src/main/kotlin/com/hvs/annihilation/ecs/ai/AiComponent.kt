package com.hvs.annihilation.ecs.ai

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import com.hvs.annihilation.state.AiEntity

data class AiComponent(
    val nearByEntities: MutableSet<Entity> = mutableSetOf(),
    var treePath: String = ""
) {
    lateinit var behaviorTree: BehaviorTree<AiEntity>
    var target: Entity = NO_TARGET

    companion object {
        var NO_TARGET = Entity(-1)

        class AiComponentListener(
            private val world: World,
            private val stage: Stage
        ): ComponentListener<AiComponent> {
            private val treeParser = BehaviorTreeParser<AiEntity>()

            override fun onComponentAdded(entity: Entity, component: AiComponent) {
                if (component.treePath.isNotBlank()) {
                    component.behaviorTree = treeParser.parse(
                        Gdx.files.internal(component.treePath),
                        AiEntity(entity, world, stage)
                    )
                }
            }
            override fun onComponentRemoved(entity: Entity, component: AiComponent) = Unit
        }
    }
}
