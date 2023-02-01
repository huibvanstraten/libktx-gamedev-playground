package com.hvs.annihilation.ecs.state

import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.hvs.annihilation.state.DefaultState

@AllOf([StateComponent::class])
class StateSystem(
    private val stateComponents: ComponentMapper<StateComponent>
): IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        with(stateComponents[entity]) {
            if (nextState != stateMachine.currentState) { stateMachine.changeState(nextState) }
            if (stateMachine.currentState == DefaultState.JUMP) { stateTime += deltaTime }
            else { stateTime = 0f }

            stateMachine.update()
        }
    }
}
