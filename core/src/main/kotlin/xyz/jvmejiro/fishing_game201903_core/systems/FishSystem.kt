package xyz.jvmejiro.fishing_game201903_core.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.math.vec2
import xyz.jvmejiro.fishing_game201903_core.components.*
import xyz.jvmejiro.fishing_game201903_core.states.*

class FishSystem(eventBus: EventBus, val gameViewport: Viewport) : StateMachineSystem(
    eventBus,
    allOf(
        Fish::class,
        Position::class,
        StateComponent::class,
        Size::class
    ).get(),
    5
) {
    override fun describeMachine() {
        startWith(FishState.SWIMMING)
        onState(FishState.SWIMMING).on(FishEvent.BE_CAUGHT) { entity, event ->
            go(FishState.CAUGHT, entity, event)
        }
        onState(FishState.SWIMMING).on(PropellingLogicEvent.CHANGE_LOGIC) { entity, event ->
            go(FishState.SWIMMING_IDLE, entity, event)
        }
        onState(FishState.SWIMMING_IDLE).on(FishEvent.FINISH_SWIMMING_IDLE) { entity, event ->
            go(FishState.SWIMMING, entity, event)
        }
        onState(FishState.SWIMMING_IDLE).on(FishEvent.INTERRUPTION) { entity, event ->
            engine.removeEntity(entity)
        }
    }
}


sealed class FishState : EntityState() {
    companion object {
        private val STATE_MAPPER: ComponentMapper<StateComponent> = mapperFor()
        private val HITBOX_MAPPER: ComponentMapper<Hitbox> = mapperFor()
    }

    object IDLE : FishState()

    object SWIMMING : FishState() {
        val leftSwimLogic: PropellingLogic =
            { deltaTime, elapsedTime -> vec2(-1.0f, MathUtils.sin(elapsedTime) * 0.1f) }
        val rightSwimLogic: PropellingLogic =
            { deltaTime, elapsedTime -> vec2(1.0f, MathUtils.sin(elapsedTime) * 0.1f) }

        override fun enter(entity: Entity, machine: StateMachineSystem, eventData: EventData) {
            when (eventData.event) {
                FishEvent.FINISH_SWIMMING_IDLE -> {
                    val body = eventData.body
                    if (body is PropellingLogicMessage) {
                        entity[STATE_MAPPER]?.resetTime(body.maintainedElapsedTime)
                        entity.add(PropellingComponent(body.nextLogic))
                    }
                }
            }
        }

        override fun exit(entity: Entity, machine: StateMachineSystem, eventData: EventData) {
            entity.remove(PropellingComponent::class.java)
        }
    }

    object SWIMMING_IDLE : FishState() {
        private val IDLE_TIME_MAP = mutableMapOf<Entity, PropellingLogicMessage>()

        override fun enter(entity: Entity, machine: StateMachineSystem, eventData: EventData) {
            val swimmingIdleData = eventData.body
            if (swimmingIdleData is PropellingLogicMessage) {
                IDLE_TIME_MAP += entity to swimmingIdleData
            } else {
                throw FailToTransitSwimmingIdleException()
            }
        }

        override fun update(entity: Entity, machine: StateMachineSystem, delta: Float) {
            val propellingLogicMessage = IDLE_TIME_MAP[entity] ?: return
            val elapsedTime = entity[STATE_MAPPER]?.elapsedTime ?: return

            // 経過時間チェック
            if (elapsedTime > propellingLogicMessage.delayTime) {
                val eventData = machine.eventBus.createEventData()
                eventData.body = propellingLogicMessage
                machine.eventBus.emit(FishEvent.FINISH_SWIMMING_IDLE, entity, eventData)
            } else {
                // 遅延中に遷移条件を満たさなくなった場合、中断イベントを発信
                if (!propellingLogicMessage.nextLogicTiming(entity, (machine as FishSystem).gameViewport)) {
                    machine.eventBus.emit(FishEvent.INTERRUPTION, entity)
                }
            }
        }

        override fun exit(entity: Entity, machine: StateMachineSystem, eventData: EventData) {
            IDLE_TIME_MAP -= entity
        }
    }

    object CAUGHT : FishState() {
        override fun enter(entity: Entity, machine: StateMachineSystem, eventData: EventData) {
            entity[HITBOX_MAPPER]?.isEnable = false  // ヒットボックスの無効化
        }
    }
}

enum class FishEvent(private val priority: Int) : EventInterface {
    BE_CAUGHT(1), FINISH_SWIMMING_IDLE(1),
    INTERRUPTION(2);

    override fun getPriority(): Int = priority
}


class FailToTransitSwimmingIdleException(override val message: String? = "") : RuntimeException()