package xyz.jvmejiro.fishing_game201903_core.systems

import xyz.jvmejiro.fishing_game201903_core.states.EntityState
import xyz.jvmejiro.fishing_game201903_core.states.EventInterface


sealed class PlayerState : EntityState() {
    object IDLE : PlayerState()
    object WAIT : PlayerState()
    object SHINKING_HOOK : PlayerState()
}

enum class PlayerEvent(private val priority: Int) : EventInterface {
    THROW_HOOK(1),
    CATCH_HOOK(1),
    WAIT(1);

    override fun getPriority(): Int = priority
}
