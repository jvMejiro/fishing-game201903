package xyz.jvmejiro.fishing_game201903_core.systems

import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.math.MathUtils.random
import ktx.ashley.add
import ktx.ashley.entity
import ktx.math.vec2
import xyz.jvmejiro.fishing_game201903_core.*

class FishSpawnSystem(interval: Float) : IntervalSystem(interval) {
    companion object {
        const val FISH_MAX: Int = 100
    }

    override fun updateInterval() {
        val existingFishSize = engine.getEntitiesFor(Family.all(Fish::class.java).get()).size()
        println(existingFishSize)
        if (existingFishSize < FISH_MAX) {
            engine.add {
                val sizeW = 20f
                val sizeH = 20f
                val offset = 5f
                val posX = random(0.0f, screenWidth - sizeW)
                val posY = random(0.0f, screenHeight - sizeH)
                entity {
                    with<Fish>()
                    with<Size> { value = vec2(sizeW, sizeH) }
                    with<Position> { value = vec2(posX, posY) }
                    with<Rotation>()
                }.add(
                    Hitbox(
                        vec2(sizeW - offset, sizeH - offset),
                        vec2(offset / 2.0f, offset / 2.0f),
                        ShapeType.Rectangle
                    )
                ).add(TextureComponent(texture = null))
            }
        }
    }
}