package xyz.jvmejiro.fishing_game201903_core.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.math.plus
import xyz.jvmejiro.fishing_game201903_core.components.*

class PropellingSystem(interval: Float) :
    IntervalIteratingSystem(
        allOf(
            PropellingComponent::class,
            Position::class,
            StateComponent::class
        ).get(), interval, PROPELLING_SYSTEM_PRIORITY
    ) {

    companion object {
        private val PROPELLING_COMPONENT_MAPPER: ComponentMapper<PropellingComponent> = mapperFor()
        private val STATE_MAPPER: ComponentMapper<StateComponent> = mapperFor()
        private val POSITION_MAPPER: ComponentMapper<Position> = mapperFor()
        val PROPELLING_SYSTEM_PRIORITY = 5
    }

    override fun processEntity(entity: Entity) {
        entity[PROPELLING_COMPONENT_MAPPER]?.also { pc ->

            // 座標更新
            updatePosition(pc, entity, interval)
        }
    }

    private fun updatePosition(
        propelling: PropellingComponent,
        entity: Entity,
        interval: Float
    ) {
        val position = entity[POSITION_MAPPER] ?: return

        propelling.current?.let {
            val elapsedTime = entity[STATE_MAPPER]?.elapsedTime ?: return
            position.value += it(interval, elapsedTime)
        }
    }
}
