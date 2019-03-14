package xyz.jvmejiro.fishing_game201903_core.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family.all
import com.badlogic.ashley.systems.IntervalIteratingSystem
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.math.plus
import xyz.jvmejiro.fishing_game201903_core.Position
import xyz.jvmejiro.fishing_game201903_core.Propelling
import xyz.jvmejiro.fishing_game201903_core.Size
import xyz.jvmejiro.fishing_game201903_core.StateComponent

class PropellingSystem(interval: Float) :
    IntervalIteratingSystem(
        all(
            Propelling::class.java,
            Position::class.java,
            StateComponent::class.java,
            Size::class.java
        ).get(), interval
    ) {

    companion object {
        private val PROPELLING_MAPPER: ComponentMapper<Propelling> = mapperFor()
        private val STATE_MAPPER: ComponentMapper<StateComponent> = mapperFor()
        private val POSITION_MAPPER: ComponentMapper<Position> = mapperFor()
        private val SIZE_MAPPER: ComponentMapper<Size> = mapperFor()
    }

    override fun processEntity(entity: Entity) {
        entity[POSITION_MAPPER]?.also { pos ->
            val elapsed = entity[STATE_MAPPER]?.elapsedTime ?: return
            val size = entity[SIZE_MAPPER]?.value ?: return
            entity[PROPELLING_MAPPER]?.apply {
                try {
                    // 移動ロジックの更新
                    logic.first { it.first(pos.value, size) }.let {
                        current = it.second
                    }
                } catch (e: NoSuchElementException) {
                }

                // 座標更新
                pos.value += current(interval, elapsed)
            }
        }
    }
}