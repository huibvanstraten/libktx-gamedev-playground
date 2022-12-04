package com.hvs.annihilation.state

import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram

interface EntityState: State<PlayerEntity> {

    override fun enter(entity: PlayerEntity) = Unit

    override fun update(entity: PlayerEntity) = Unit

    override fun exit(entity: PlayerEntity) = Unit

    override fun onMessage(entity: PlayerEntity, telegram: Telegram) = false
}
