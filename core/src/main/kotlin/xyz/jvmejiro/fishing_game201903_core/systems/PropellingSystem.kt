package xyz.jvmejiro.fishing_game201903_core.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family.all
import com.badlogic.ashley.systems.IntervalIteratingSystem
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.math.plus
import xyz.jvmejiro.fishing_game201903_core.Position
import xyz.jvmejiro.fishing_game201903_core.PropellingLogic
import xyz.jvmejiro.fishing_game201903_core.StateComponent

class PropellingSystem(interval: Float) :
    IntervalIteratingSystem(all(PropellingLogic::class.java, Position::class.java).get(), interval) {

    companion object {
        private val PROPELLING_LOGIC_MAPPER: ComponentMapper<PropellingLogic> = mapperFor()
        private val STATE_MAPPER: ComponentMapper<StateComponent> = mapperFor()
        private val POSITION_MAPPER: ComponentMapper<Position> = mapperFor()
    }

    override fun processEntity(entity: Entity) {
        entity[POSITION_MAPPER]?.run {
            val elapsed = entity[STATE_MAPPER]?.elapsedTime ?: return
            value += entity[PROPELLING_LOGIC_MAPPER]?.logic?.invoke(interval, elapsed) ?: return@run
        }
    }
}