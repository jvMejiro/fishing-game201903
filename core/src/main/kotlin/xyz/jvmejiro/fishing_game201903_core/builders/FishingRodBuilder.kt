package xyz.jvmejiro.fishing_game201903_core.builders

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import ktx.ashley.entity
import ktx.math.vec2
import xyz.jvmejiro.fishing_game201903_core.components.*

class FishingRodBuilder(private val engine: Engine) {
    var position = vec2()
    var size = vec2()
    var hookGenerateOffset = vec2()
    var hookNum = 1

    fun build(): Entity {
        if (hookNum < 0) throw IllegalBuildParameterException()
        return engine.entity {
            with<FishingRod> {
                hookSpawnPointOffset = this@FishingRodBuilder.hookGenerateOffset
                hookNum = this@FishingRodBuilder.hookNum
            }
            with<Position> { value = position }
            with<Size> { value = size }
            with<Rotation> { }
            with<StateComponent>()
            with<Direction>()
        }.add(TextureComponent(texture = null))
    }

    companion object {
        inline fun builder(required: Engine, block: FishingRodBuilder.() -> Unit) =
            FishingRodBuilder(required).apply(block)
    }
}