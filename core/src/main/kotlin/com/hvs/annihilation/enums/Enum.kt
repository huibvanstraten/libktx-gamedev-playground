package com.hvs.annihilation.enums

enum class AnimationModel {
    PLAYER, SLIME, CHEST, UNDEFINED;

    val atlasKey: String = this.toString().lowercase()
}

enum class AnimationType {
    IDLE, RUN, JUMP, LICK, SHOCK, ATTACK, OPEN, DEATH;

    val atlasKey: String = this.toString().lowercase()
    companion object {
        const val NO_ANIMATION = "no animation"
    }
}

enum class AttackState {
    READY, PREPARE, ATTACKING, DEAL_DAMAGE
}

enum class SpawnType {

}
