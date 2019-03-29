package xyz.jvmejiro.fishing_game201903_core.builders

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array
import ktx.ashley.create
import ktx.ashley.entity
import ktx.math.vec2
import xyz.jvmejiro.fishing_game201903_core.PropellingUtil
import xyz.jvmejiro.fishing_game201903_core.components.*
import xyz.jvmejiro.fishing_game201903_core.screenWidth
import xyz.jvmejiro.fishing_game201903_core.systems.FishState

class FishBuilder(val required: Engine) {
    var position = vec2()
    var size = vec2()
    var direction = vec2()
    var hitBoxSize = vec2()
    var hitBoxOffset = vec2()
    var texture: TextureRegion? = null

    fun build(): Entity {
        val pds = Array<PropellingData>()
        pds.add(
            PropellingData(PropellingUtil.Timing.SCREEN_OUT_LEFT, FishState.SWIMMING.rightSwimLogic, 1f),
            PropellingData(PropellingUtil.Timing.SCREEN_OUT_RIGHT, FishState.SWIMMING.leftSwimLogic, 1f)
        )

        return required.entity {
            with<Fish> {
                mouthOffset = vec2(size.x, size.y / 2.0f)
                point = 100
            }
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
        ).add(TextureComponent(texture = texture)).add(
            PropellingComponent(FishState.SWIMMING.rightSwimLogic)
        ).add(
            PropellingLogicComponent(pds)
        )
    }

    companion object {
        inline fun builder(required: Engine, block: FishBuilder.() -> Unit) = FishBuilder(required).apply(block)
    }
}
