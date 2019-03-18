package xyz.jvmejiro.fishing_game201903_core.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2
import xyz.jvmejiro.fishing_game201903_core.builders.IllegalBuildParameterException
import java.lang.IllegalArgumentException

data class Rotation(var degree: Float = 0.0f, var axis: Vector2 = vec2()) : Component
data class Position(var value: Vector2 = vec2()) : Component
data class Size(var value: Vector2 = vec2()) : Component
data class Direction(var value: Vector2 = vec2()) : Component

data class SpawnRegion(val region: Rectangle) : Component
data class TextureComponent(
    var texture: TextureRegion?,
    var zLevel: Int = 0,
    var isVisible: Boolean = true,
    var offset: Vector2 = vec2(),
    var alpha: Float = 1f
) : Component

data class Hitbox(var size: Vector2, var offset: Vector2 = vec2(), var type: ShapeType) : Component
data class Player(var score: Int = 0) : Component
data class Fish(var mouthOffset: Vector2 = vec2(), var point: Int = 0) : Component
data class FishingRod(var hookSpawnPointOffset: Vector2 = vec2(), var hookNum: Int = 1) : Component {
    var sinkingHookNum: Int = 0
        set(value) {
            if (field < 0) throw IllegalArgumentException()
            field = value
        }
}
data class Move(
    var duration: Float,
    var from: Vector2,
    var target: Vector2,
    val interpolation: Interpolation = Interpolation.linear,
    var delay: Float = 0.0f,
    var elapsedTime: Float = 0.0f
) : Component

sealed class ShapeType {
    object Rectangle : ShapeType()
    object Circle : ShapeType()
}
