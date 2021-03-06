package xyz.jvmejiro.fishing_game201903_core.builders

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import ktx.ashley.entity
import ktx.ashley.has
import ktx.ashley.mapperFor
import ktx.math.vec2
import xyz.jvmejiro.fishing_game201903_core.components.*

class HookBuilder(
    parentFishingRod: Entity,
    private val engine: Engine
) {
    var parentFishingRod: Entity = parentFishingRod
        set(value) {
            if (!value.has(FISHING_ROD_MAPPER)) throw IllegalBuildParameterException()
            field = value
        }

    var position = vec2()
    var size = vec2()
    var direction = vec2(1f, 0f)
    var hitBoxSize = vec2()
    var hitBoxOffset = vec2()
    var moveDuration = 0.0f
    var sinkDepth = 0.0f

    companion object {
        private val FISHING_ROD_MAPPER: ComponentMapper<FishingRod> = mapperFor()
        fun builder(fishingRod: Entity, engine: Engine, body: HookBuilder.() -> Unit) =
            HookBuilder(fishingRod, engine).apply(body)
    }

    fun build(): Entity {
        val to = vec2(position.x, position.y - sinkDepth)
        return engine.entity {
            with<Position> { value = position.cpy() }
            with<Size> { value = size.cpy() }
            with<Direction> { value = direction.cpy() }
            with<Rotation>()
            with<StateComponent>()
        }.add(
            Hitbox(
                vec2(hitBoxSize.x, hitBoxSize.y),
                vec2(hitBoxOffset.x, hitBoxOffset.y),
                ShapeType.Rectangle
            )
        ).add(TextureComponent(texture = null)).add(
            Hook(
                parentFishingRod = parentFishingRod,
                hookOffset = vec2(size.x / 2.0f, size.y / 2.0f), from = position.cpy(), to = to.cpy()
            )
        ).add(
            Move(
                duration = moveDuration,
                from = position.cpy(),
                target = to.cpy()
            )
        )
    }
}