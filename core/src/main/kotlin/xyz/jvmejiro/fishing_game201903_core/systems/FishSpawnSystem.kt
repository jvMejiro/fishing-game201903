package xyz.jvmejiro.fishing_game201903_core.systems

import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.math.MathUtils.random
import ktx.ashley.add
import ktx.ashley.entity
import ktx.math.vec2
import xyz.jvmejiro.fishing_game201903_core.*

class FishSpawnSystem(private val maxFishSize: Int, interval: Float) : IntervalSystem(interval) {

    override fun updateInterval() {
        val existingFishSize = engine.getEntitiesFor(Family.all(Fish::class.java).get()).size()

        if (existingFishSize < maxFishSize) {
            engine.add {
                val sizeW = 20f
                val sizeH = 20f
                val offset = 5f
                val posX = -sizeW
                val posY = random(0.0f, screenHeight * 0.75f - sizeH)
                entity {
                    with<Fish>()
                    with<Size> { value = vec2(sizeW, sizeH) }
                    with<Position> { value = vec2(posX, posY) }
                    with<Rotation>()
                    with<Direction> { value = vec2(1f, 0f) }
                    with<PropellingLogic> { logic = FishState.SWIMMING.rightSwimLogic }

                }.add(
                    Hitbox(
                        vec2(sizeW - offset, sizeH - offset),
                        vec2(offset / 2.0f, offset / 2.0f),
                        ShapeType.Rectangle
                    )
                ).add(TextureComponent(texture = null)).add(StateComponent())
            }
        }
    }
}