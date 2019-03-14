package xyz.jvmejiro.fishing_game201903_core.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family.all
import com.badlogic.gdx.math.MathUtils
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.math.vec2
import xyz.jvmejiro.fishing_game201903_core.*
import xyz.jvmejiro.fishing_game201903_core.states.*

class FishSystem(eventBus: EventBus) : StateMachineSystem(
    eventBus,
    all(
        Fish::class.java,
        Position::class.java,
        Propelling::class.java,
        StateComponent::class.java,
        Size::class.java
    ).get()
) {

    companion object {
        private val FISH_MAPPER: ComponentMapper<Fish> = mapperFor()
        private val POSITION_MAPPER: ComponentMapper<Position> = mapperFor()
        private val STATE_MAPPER: ComponentMapper<StateComponent> = mapperFor()
        private val PROPELLING_MAPPER: ComponentMapper<Propelling> = mapperFor()
        private val SIZE_MAPPER: ComponentMapper<Size> = mapperFor()
    }

    override fun describeMachine() {
        startWith(FishState.SWIMMING)
    }
}


sealed class FishState : EntityState() {
    companion object {
        private val POSITION_MAPPER: ComponentMapper<Position> = mapperFor()
        private val SIZE_MAPPER: ComponentMapper<Size> = mapperFor()
        private val PROPELLING_MAPPER: ComponentMapper<Propelling> = mapperFor()
    }

    object IDLE : FishState() {
        override fun enter(entity: Entity, machine: StateMachineSystem, eventData: EventData) {

        }

        override fun update(entity: Entity, machine: StateMachineSystem, delta: Float) {

            machine.emit(FishEvent.BE_CAUGHT)
            println(entity[POSITION_MAPPER]?.value)
        }
    }

    object SWIMMING : FishState() {
        val leftSwimLogic: PropellingLogic =
            { deltaTime, elapsedTime -> vec2(-0.5f, MathUtils.sin(elapsedTime) * 0.1f) }
        val rightSwimLogic: PropellingLogic =
            { deltaTime, elapsedTime -> vec2(0.5f, MathUtils.sin(elapsedTime) * 0.1f) }

        override fun update(entity: Entity, machine: StateMachineSystem, delta: Float) {
            val position = entity[POSITION_MAPPER] ?: return
            val size = entity[SIZE_MAPPER] ?: return
        }
    }

    object CAUGHT : FishState()
}

enum class FishEvent(private val priority: Int) : EventInterface {
    BE_CAUGHT(1);

    override fun getPriority(): Int = priority
}