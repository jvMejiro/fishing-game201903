package xyz.jvmejiro.fishing_game201903_core.builders

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import ktx.ashley.entity
import ktx.math.vec2
import xyz.jvmejiro.fishing_game201903_core.*
import xyz.jvmejiro.fishing_game201903_core.systems.FishState

class FishBuilder(val required: Engine) {
    var position = vec2()
    var size = vec2()
    var direction = vec2()
    var hitBoxSize = vec2()
    var hitBoxOffset = vec2()

    fun build(): Entity {
        return required.entity {
            with<Fish>()
            with<Size> { value = size }
            with<Position> { value = position }
            with<Rotation> { }
            with<Direction> { value = direction }
            with<StateComponent>()
        }.add(
            Hitbox(
                vec2(hitBoxSize.x, hitBoxSize.y),
                vec2(hitBoxOffset.x, hitBoxOffset.y),
                ShapeType.Rectangle
            )
        ).add(TextureComponent(texture = null)).add(
            Propelling(
                FishState.SWIMMING.rightSwimLogic,
                listOf(
                    Pair({ pos, size -> pos.x < -size.x }, FishState.SWIMMING.rightSwimLogic),
                    Pair({ pos: Vector2, size: Vector2 -> screenWidth < pos.x }, FishState.SWIMMING.leftSwimLogic)
                )
            )
        )
    }

    companion object {
        inline fun builder(required: Engine, block: FishBuilder.() -> Unit) = FishBuilder(required).apply(block)
    }
}
