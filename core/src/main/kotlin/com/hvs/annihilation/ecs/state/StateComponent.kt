package com.hvs.annihilation.ecs.state

import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import com.hvs.annihilation.state.DefaultState
import com.hvs.annihilation.state.EntityState
import com.hvs.annihilation.state.PlayerEntity

data class StateComponent(
    var stateTime: Float = 0f,
    var nextState: EntityState = DefaultState.IDLE,
    val stateMachine: DefaultStateMachine<PlayerEntity, EntityState> = DefaultStateMachine()
) {

    companion object {
        class StateComponentListener(
            private val world: World
        ): ComponentListener<StateComponent> {
            override fun onComponentAdded(entity: Entity, component: StateComponent) {
                component.stateMachine.owner = PlayerEntity(entity, world)
            }

            override fun onComponentRemoved(entity: Entity, component: StateComponent) = Unit
        }
    }
}
