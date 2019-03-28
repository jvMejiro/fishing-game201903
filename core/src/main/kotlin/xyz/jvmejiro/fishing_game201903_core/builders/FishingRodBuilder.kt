package xyz.jvmejiro.fishing_game201903_core.builders

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import ktx.ashley.entity
import ktx.ashley.has
import ktx.ashley.mapperFor
import ktx.math.vec2
import xyz.jvmejiro.fishing_game201903_core.components.*

class FishingRodBuilder(parentPlayer: Entity, private val engine: Engine) {
    var position = vec2()
    var size = vec2()
    var hookGenerateOffset = vec2()
    var hookNum = 1
    var parentPlayer: Entity = parentPlayer
        set(value) {
            if (!value.has(PLAYER_MAPPER)) throw IllegalBuildParameterException()
            field = value
        }

    fun build(): Entity {
        if (hookNum < 0) throw IllegalBuildParameterException()
        return engine.entity {
            with<Position> { value = position }
            with<Size> { value = size }
            with<Rotation> { }
            with<StateComponent>()
            with<Direction>()
        }.add(TextureComponent(texture = null)).add(FishingRod(hookGenerateOffset, hookNum, parentPlayer))
    }

    companion object {
        val PLAYER_MAPPER: ComponentMapper<Player> = mapperFor()
        inline fun builder(player: Entity, engine: Engine, block: FishingRodBuilder.() -> Unit) =
            FishingRodBuilder(player, engine).apply(block)
    }
}