package xyz.jvmejiro.fishing_game201903_core.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import xyz.jvmejiro.fishing_game201903_core.IllegalSignException
import xyz.jvmejiro.fishing_game201903_core.states.EntityState
import kotlin.math.sign

class StateComponent : Component, Pool.Poolable {
    var state: EntityState =
        EntityState.Companion.SystemState.STATE_NOP
        set(value) {
            field = value
            resetTime()
        }

    var elapsedTime: Float = 0.0f
        private set

    fun resetTime(initTime: Float = 0.0f) {
        elapsedTime = initTime
    }

    fun elapse(deltaTime: Float) {
        if (deltaTime.sign < 0) throw IllegalSignException()
        elapsedTime += deltaTime
    }

    override fun reset() {
        state = EntityState.Companion.SystemState.STATE_NOP
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StateComponent

        if (state != other.state) return false
        if (elapsedTime != other.elapsedTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = state.hashCode()
        result = 31 * result + elapsedTime.hashCode()
        return result
    }

    override fun toString(): String {
        return "StateComponent(state=$state, elapsedTime=$elapsedTime)"
    }
}