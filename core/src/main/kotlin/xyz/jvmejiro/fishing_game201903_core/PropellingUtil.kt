package xyz.jvmejiro.fishing_game201903_core

import com.badlogic.ashley.core.ComponentMapper
import ktx.ashley.get
import ktx.ashley.mapperFor
import xyz.jvmejiro.fishing_game201903_core.components.Position
import xyz.jvmejiro.fishing_game201903_core.components.PropellingTiming
import xyz.jvmejiro.fishing_game201903_core.components.Size
import kotlin.reflect.KClass

object PropellingUtil {
    private val POSITION_MAPPER: ComponentMapper<Position> = mapperFor()
    private val SIZE_MAPPER: ComponentMapper<Size> = mapperFor()

    object Timing {
        val SCREEN_OUT_LEFT: PropellingTiming = { entity, _ ->
            val pos = entity[POSITION_MAPPER] ?: throw IllegalEntityException(Position::class)
            val size = entity[SIZE_MAPPER] ?: throw IllegalEntityException(Size::class)
            pos.value.x < -size.value.x
        }
        val SCREEN_OUT_RIGHT: PropellingTiming = { entity, gameViewport ->
            val pos = entity[POSITION_MAPPER] ?: throw IllegalEntityException(Position::class)
            gameViewport.coordinatesOfRightBottomCorner.x < pos.value.x
        }
    }

    class IllegalEntityException(vararg notFoundComponent: KClass<*>) : RuntimeException()
}