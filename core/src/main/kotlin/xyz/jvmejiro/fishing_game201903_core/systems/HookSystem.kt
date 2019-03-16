package xyz.jvmejiro.fishing_game201903_core.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.has
import ktx.ashley.mapperFor
import xyz.jvmejiro.fishing_game201903_core.Hook
import xyz.jvmejiro.fishing_game201903_core.Move
import xyz.jvmejiro.fishing_game201903_core.Position
import xyz.jvmejiro.fishing_game201903_core.StateComponent
import xyz.jvmejiro.fishing_game201903_core.states.*

class HookSystem(eventBus: EventBus) :
    StateMachineSystem(eventBus, allOf(Hook::class, StateComponent::class, Position::class).get()) {
    override fun describeMachine() {
        startWith(HookState.SINKING)
        onState(HookState.SINKING).on(HookEvent.ARRIVE_TARGET) { entity, event ->
            go(
                HookState.STAY,
                entity,
                event
            )
        }
        onState(HookState.STAY).on(HookEvent.START_FLOAT) { entity, event -> go(HookState.FLOATING, entity, event) }
    }
}

sealed class HookState : EntityState() {
    companion object {
        private val HOOK_MAPPER: ComponentMapper<Hook> = mapperFor()
        private val MOVE_MAPPER: ComponentMapper<Move> = mapperFor()
        private val STATE_MAPPER: ComponentMapper<StateComponent> = mapperFor()
    }

    object SINKING : HookState() {
        override fun update(entity: Entity, machine: StateMachineSystem, delta: Float) {
            if (!entity.has(MOVE_MAPPER)) machine.emit(HookEvent.ARRIVE_TARGET)
        }
    }

    object STAY : HookState() {
        private val STAY_DURATION = 1.0f

        override fun update(entity: Entity, machine: StateMachineSystem, delta: Float) {
            val state = entity[STATE_MAPPER] ?: return
            if (state.elapsedTime > STAY_DURATION) machine.emit(HookEvent.START_FLOAT)
        }
    }

    object FLOATING : HookState() {
        private const val FLOATING_DURATION = 5.0f

        override fun enter(entity: Entity, machine: StateMachineSystem, eventData: EventData) {
            val hook = entity[HOOK_MAPPER] ?: return
            entity.add(
                Move(
                    duration = FLOATING_DURATION, from = hook.target, target = hook.start
                )
            )
        }

        override fun update(entity: Entity, machine: StateMachineSystem, delta: Float) {
            if (!entity.has(MOVE_MAPPER)) {
                val eventData = machine.eventBus.createEventData().apply {
                    event = HookEvent.FINISH
                    target = null
                    body = entity[HOOK_MAPPER]?.caughtFish
                }
                machine.engine.removeEntity(entity)
                machine.eventBus.emit(HookEvent.FINISH)
            }
        }
    }

    object WAIT : HookState()
}

enum class HookEvent(private val priority: Int) : EventInterface {
    ARRIVE_TARGET(1),
    START_FLOAT(1),
    FINISH(1);

    override fun getPriority(): Int = priority
}
