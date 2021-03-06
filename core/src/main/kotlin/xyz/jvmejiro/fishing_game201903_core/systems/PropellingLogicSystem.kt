package xyz.jvmejiro.fishing_game201903_core.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.mapperFor
import xyz.jvmejiro.fishing_game201903_core.components.*
import xyz.jvmejiro.fishing_game201903_core.states.EventBus
import xyz.jvmejiro.fishing_game201903_core.states.EventInterface

class PropellingLogicSystem(private val eventBus: EventBus, private val gameViewport: Viewport) :
    IteratingSystem(
        allOf(PropellingLogicComponent::class, Position::class, Size::class, StateComponent::class).get(),
        PropellingSystem.PROPELLING_SYSTEM_PRIORITY + 1
    ) {

    companion object {
        private val PROPELLING_LOGIC_MAPPER: ComponentMapper<PropellingLogicComponent> = mapperFor()
        private val STATE_MAPPER: ComponentMapper<StateComponent> = mapperFor()
        private val POSITION_MAPPER: ComponentMapper<Position> = mapperFor()
        private val SIZE_MAPPER: ComponentMapper<Size> = mapperFor()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity[PROPELLING_LOGIC_MAPPER]?.let { plc ->
            try {
                // 直前に適応された移動ロジックの適応タイミングが再び適応される場合、更新処理を無視
                plc.lastPropellingData?.timing?.let {
                    if (it(entity, gameViewport)) return
                }
                // 移動ロジックの更新
                plc.logic.first { it.timing(entity, gameViewport) }.let { pd ->
                    val eventData = eventBus.createEventData()
                    val maintainedElapsedTime = entity[STATE_MAPPER]?.elapsedTime ?: return
                    eventData.body = PropellingLogicMessage(
                        pd.delay,
                        maintainedElapsedTime,
                        pd.timing,
                        pd.logic
                    )
                    plc.lastPropellingData = pd
                    eventBus.emit(PropellingLogicEvent.CHANGE_LOGIC, entity, eventData)
                }
            } catch (e: NoSuchElementException) {
            }
        }
    }
}

data class PropellingLogicMessage(
    val delayTime: Float,
    val maintainedElapsedTime: Float,
    val nextLogicTiming: PropellingTiming,
    val nextLogic: PropellingLogic
)


enum class PropellingLogicEvent(private val priority: Int) : EventInterface {
    CHANGE_LOGIC(1);

    override fun getPriority(): Int = priority
}