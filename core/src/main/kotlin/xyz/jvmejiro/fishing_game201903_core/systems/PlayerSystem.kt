package xyz.jvmejiro.fishing_game201903_core.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Input
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.mapperFor
import xyz.jvmejiro.fishing_game201903_core.components.Fish
import xyz.jvmejiro.fishing_game201903_core.components.Player
import xyz.jvmejiro.fishing_game201903_core.components.StateComponent
import xyz.jvmejiro.fishing_game201903_core.states.*

class PlayerSystem(eventBus: EventBus) :
    StateMachineSystem(eventBus, allOf(Player::class, StateComponent::class).get()) {
    companion object {
        val FISH_MAPPER: ComponentMapper<Fish> = mapperFor()
    }

    override fun describeMachine() {
        startWith(PlayerState.IDLE)
        onState(PlayerState.IDLE).on(StateMachineSystem.Companion.SystemEvent.EVENT_KEY) { entity, event ->
            if (event.body == Input.Keys.SPACE) {
                eventBus.emit(PlayerEvent.THROW_HOOK)
            }
        }
        onState(PlayerState.IDLE).on(FishingRodEvent.COLLECT_ALL_HOOKS) { entity, event ->
            val caughtFishesList = event.body as? List<Entity> ?: throw IllegalBodyException()
            val eventData = EventData().apply { body = caughtFishesList.sumBy { it[FISH_MAPPER]?.point ?: 0 } }
            caughtFishesList.forEach { engine.removeEntity(it) }
            eventBus.emit(GameStateEvent.GET_POINT, eventData)
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
