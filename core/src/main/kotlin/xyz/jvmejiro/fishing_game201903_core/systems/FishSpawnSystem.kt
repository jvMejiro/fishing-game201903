package xyz.jvmejiro.fishing_game201903_core.systems

import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.math.MathUtils.random
import ktx.ashley.add
import ktx.ashley.allOf
import ktx.math.vec2
import xyz.jvmejiro.fishing_game201903_core.Fish
import xyz.jvmejiro.fishing_game201903_core.builders.FishBuilder
import xyz.jvmejiro.fishing_game201903_core.screenHeight

class FishSpawnSystem(private val maxFishSize: Int, interval: Float) : IntervalSystem(interval) {

    override fun updateInterval() {
        val existingFishSize = engine.getEntitiesFor(allOf(Fish::class).get()).size()

        if (existingFishSize < maxFishSize) {
            engine.add {
                val sizeW = 20f
                val sizeH = 20f
                val offset = 5f
                val posX = -sizeW
                val posY = random(0.0f, screenHeight * 0.75f - sizeH)

                FishBuilder.builder(engine) {
                    position = vec2(posX, posY)
                    size = vec2(sizeW, sizeH)
                    hitBoxSize = vec2(size.x - offset, size.y - offset)
                    hitBoxOffset = vec2(offset / 2.0f, offset / 2.0f)
                }.build()
            }
        }
    }
}