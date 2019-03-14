package xyz.jvmejiro.fishing_game201903_core.systems

import com.badlogic.ashley.core.Family.all
import xyz.jvmejiro.fishing_game201903_core.Player
import xyz.jvmejiro.fishing_game201903_core.StateComponent
import xyz.jvmejiro.fishing_game201903_core.states.EntityState
import xyz.jvmejiro.fishing_game201903_core.states.EventBus
import xyz.jvmejiro.fishing_game201903_core.states.EventInterface
import xyz.jvmejiro.fishing_game201903_core.states.StateMachineSystem

class PlayerSystem(eventBus: EventBus) :
    StateMachineSystem(eventBus, all(Player::class.java, StateComponent::class.java).get()) {
    override fun describeMachine() {

    }
}

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
