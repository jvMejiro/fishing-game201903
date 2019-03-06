package xyz.jvmejiro.fishing_game201903_core

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import xyz.jvmejiro.fishing_game201903_core.states.StateInterface

sealed class ShapeType {
    object Rectangle : ShapeType()
    object Circle : ShapeType()
}

data class Rotation(var degree: Float = 0.0f, var axis: Vector2 = Vector2()) : Component
data class Position(var x: Int = 0, var y: Int = 0) : Component
data class Size(var value: Vector2 = Vector2()) : Component
data class Direction(val value: Vector2 = Vector2()) : Component
data class TextureComponent(
    var texture: TextureRegion?,
    var zLevel: Int = 0,
    var isVisible: Boolean = true,
    var offset: Vector2 = Vector2(),
    var alpha: Float = 1f
) : Component

data class Hitbox(var hitbox: Vector2, var offset: Vector2, var type: ShapeType) : Component
data class Player(var score: Int = 0) : Component
data class Fish(var point: Int = 0) : Component
data class Move(
    var elapsedTime: Float = 0.0f,
    var duration: Float,
    var delay: Float,
    var from: Vector2,
    var target: Vector2,
    val interpolation: Interpolation = Interpolation.linear
)

class Lure : Component

class StateComponent<T : StateInterface>(state: T, elapsedTime: Float = 0.0f) : Component {
    var state: T = state
        set(value) {
            field = value
            resetTime()
        }
    var elapsedTime: Float = elapsedTime
        private set

    fun resetTime() {
        elapsedTime = 0.0f
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StateComponent<*>

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
