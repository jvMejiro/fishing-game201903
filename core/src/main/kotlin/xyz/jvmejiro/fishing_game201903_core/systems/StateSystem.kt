package xyz.jvmejiro.fishing_game201903_core.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family.all
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.get
import ktx.ashley.mapperFor
import xyz.jvmejiro.fishing_game201903_core.StateComponent

class StateSystem : IteratingSystem(all(StateComponent::class.java).get()) {
    companion object {
        private val STATE_MAPPER: ComponentMapper<StateComponent> = mapperFor()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity[STATE_MAPPER]?.elapse(deltaTime)
    }
}