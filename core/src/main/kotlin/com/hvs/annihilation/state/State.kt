package com.hvs.annihilation.state

import com.badlogic.gdx.graphics.g2d.Animation
import com.hvs.annihilation.ecs.jump.JumpOrder
import com.hvs.annihilation.enums.AnimationType

enum class DefaultState : EntityState {

    IDLE {
        override fun enter(entity: PlayerEntity) {
            entity.animation(AnimationType.IDLE)
        }

        override fun update(entity: PlayerEntity) {
            when {
                entity.wantsToAttack -> entity.setState(ATTACK)
                entity.wantsToRun -> entity.setState(RUN)
                entity.jumpComp.order == JumpOrder.JUMP -> entity.setState(JUMP)
            }
        }
    },

    RUN {
        override fun enter(entity: PlayerEntity) {
            entity.animation(AnimationType.RUN)
        }

        override fun update(entity: PlayerEntity) {
            when {
                entity.wantsToAttack -> entity.setState(ATTACK)
                !entity.wantsToRun -> entity.setState(IDLE)
                entity.jumpComp.order == JumpOrder.JUMP -> entity.setState(JUMP)
            }
        }
    },

    ATTACK {
        override fun enter(entity: PlayerEntity) {
            entity.animation(AnimationType.ATTACK, Animation.PlayMode.NORMAL)
            entity.root(true)
            entity.startAttack()
        }

        override fun update(entity: PlayerEntity) {
            val attackComp = entity.attackComp
            if (attackComp.isReady && !attackComp.doAttack) {
                entity.changeToPreviousState()
            } else if (attackComp.isReady) {
                entity.animation(AnimationType.ATTACK, Animation.PlayMode.NORMAL, true)
                entity.startAttack()
            }
        }

        override fun exit(entity: PlayerEntity) {
            entity.root(false)
        }
    },

    JUMP {
        override fun enter(entity: PlayerEntity) {
            entity.animation(AnimationType.IDLE, Animation.PlayMode.NORMAL)
        }
        override fun update(entity: PlayerEntity) {
            val collision = entity.collComp
            with(entity.stateComp) {
                if (entity.isFalling(entity.physicsComp, collision) || stateTime >= entity.jumpComp.maxJumpTime) {
                    // player is in mid-air and falling down OR player exceeds maximum jump time
                    stateMachine.changeState(FALL)
                } else if (collision.numGroundContacts > 0 && entity.jumpComp.order == JumpOrder.NONE) {
                    // player is on ground again
                    stateMachine.changeState(IDLE)
                } else {
                    return
                }
            }
        }

        override fun exit(entity: PlayerEntity) {
            entity.jumpComp.order = JumpOrder.NONE
        }
    },

    FALL {
        override fun enter(entity: PlayerEntity) {
            entity.animation(AnimationType.RUN)
        }
        override fun update(entity: PlayerEntity) {
            if (entity.collComp.numGroundContacts > 0) {
                // reached ground again
                entity.setState(IDLE)
            }
        }
    },

    //TODO: first time i die i can keep moving
    DEAD {
        override fun enter(entity: PlayerEntity) {
            entity.root(true)
        }

        override fun update(entity: PlayerEntity) {
            if (!entity.isDead) {
                entity.setState(RESURRECT)
            }
        }
    },

    RESURRECT {
        override fun enter(entity: PlayerEntity) {
            entity.enableGlobalState(true)
            entity.animation(AnimationType.DEATH, Animation.PlayMode.REVERSED, true)
        }

        override fun update(entity: PlayerEntity) {
            if (entity.isAnimationDone) {
                entity.setState(IDLE)
                entity.root(false)
            }
        }
    }
}

enum class DefaultGlobalState : EntityState {

    CHECK_ALIVE {
        override fun update(entity: PlayerEntity) {
            if (entity.isDead) {
                entity.enableGlobalState(false)
                entity.setState(DefaultState.DEAD, true)
            }
        }
    }
}
