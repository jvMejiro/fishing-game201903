package xyz.jvmejiro.fishing_game201903_core

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2
import xyz.jvmejiro.fishing_game201903_core.states.EntityState
import kotlin.math.sign

data class Rotation(var degree: Float = 0.0f, var axis: Vector2 = Vector2()) : Component
data class Position(var value: Vector2 = Vector2()) : Component
data class Size(var value: Vector2 = Vector2()) : Component
data class Direction(var value: Vector2 = Vector2()) : Component
data class PropellingLogic(var logic: (delta: Float, total: Float) -> Vector2 = { _, _ -> vec2() }) : Component
data class SpawnRegion(val region: Rectangle) : Component
data class TextureComponent(
    var texture: TextureRegion?,
    var zLevel: Int = 0,
    var isVisible: Boolean = true,
    var offset: Vector2 = Vector2(),
    var alpha: Float = 1f
) : Component

data class Hitbox(var size: Vector2, var offset: Vector2 = Vector2(), var type: ShapeType) : Component
data class Player(var score: Int = 0) : Component
data class Fish(var point: Int = 0) : Component
class Lure : Component
data class Move(
    var elapsedTime: Float = 0.0f,
    var duration: Float,
    var delay: Float,
    var from: Vector2,
    var target: Vector2,
    val interpolation: Interpolation = Interpolation.linear
)


class StateComponent : Component {
    var state: EntityState = EntityState.Companion.SystemState.STATE_NOP
        set(value) {
            field = value
            resetTime()
        }
    var elapsedTime: Float = 0.0f
        private set

    fun resetTime() {
        elapsedTime = 0.0f
    }

    fun elapse(deltaTime: Float) {
        if (deltaTime.sign < 0) throw IllegalSignException()
        elapsedTime += deltaTime
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

sealed class ShapeType {
    object Rectangle : ShapeType()
    object Circle : ShapeType()
}