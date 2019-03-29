package xyz.jvmejiro.fishing_game201903_core.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Input
import ktx.ashley.allOf
import xyz.jvmejiro.fishing_game201903_core.components.Player
import xyz.jvmejiro.fishing_game201903_core.components.StateComponent
import xyz.jvmejiro.fishing_game201903_core.states.EntityState
import xyz.jvmejiro.fishing_game201903_core.states.EventBus
import xyz.jvmejiro.fishing_game201903_core.states.EventInterface
import xyz.jvmejiro.fishing_game201903_core.states.StateMachineSystem

class PlayerSystem(eventBus: EventBus) :
    StateMachineSystem(eventBus, allOf(Player::class, StateComponent::class).get()) {
    override fun describeMachine() {
        startWith(PlayerState.IDLE)
        onState(PlayerState.IDLE).on(Companion.SystemEvent.EVENT_KEY) { entity, event ->
            if (event.body == Input.Keys.SPACE) {
                eventBus.emit(PlayerEvent.THROW_HOOK)
            }
        }
        onState(PlayerState.IDLE).on(HookEvent.FINISH) { entity, event ->

        }
    }
}

sealed class PlayerState : EntityState() {
    object IDLE : PlayerState() {
        override fun update(entity: Entity, machine: StateMachineSystem, delta: Float) {

        }
    }

    object THROW_ANIMATION : PlayerState()
    object WAIT : PlayerState()
}

enum class PlayerEvent(private val priority: Int) : EventInterface {
    THROW_HOOK(1),
    CATCH_HOOK(1),
    WAIT(1);

    override fun getPriority(): Int = priority
}
