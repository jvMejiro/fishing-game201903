package xyz.jvmejiro.fishing_game201903_core.builders

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import ktx.ashley.entity
import ktx.math.vec2
import xyz.jvmejiro.fishing_game201903_core.components.*

class PlayerBuilder(private val engine: Engine) {
    var position = vec2()
    var size = vec2()

    fun build(): Entity {
        return engine.entity {
            with<Player>()
            with<Rotation>()
            with<Position> { value = position }
            with<Size> { value = size }
            with<StateComponent>()
            with<Direction>()
        }.add(TextureComponent(texture = null))
    }

    companion object {
        inline fun builder(required: Engine, block: PlayerBuilder.() -> Unit) = PlayerBuilder(required).apply(block)
    }
}
