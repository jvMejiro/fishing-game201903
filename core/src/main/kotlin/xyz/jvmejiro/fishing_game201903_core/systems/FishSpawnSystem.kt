package xyz.jvmejiro.fishing_game201903_core.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils.random
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.ashley.add
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.inject.Context
import ktx.math.vec2
import xyz.jvmejiro.fishing_game201903_core.builders.FishBuilder
import xyz.jvmejiro.fishing_game201903_core.components.Fish
import xyz.jvmejiro.fishing_game201903_core.components.Player
import xyz.jvmejiro.fishing_game201903_core.components.Position
import xyz.jvmejiro.fishing_game201903_core.coordinatesOfRightBottomCorner

class FishSpawnSystem(
    private val context: Context,
    private val maxFishSize: Int,
    private val gameViewport: Viewport,
    interval: Float
) :
    IntervalSystem(interval) {
    companion object {
        private val POSITION_MAPPER: ComponentMapper<Position> = mapperFor()
        private val FISH_FAMILY = allOf(Fish::class).get()
        private val PLAYER_FAMILY = allOf(Player::class).get()
    }

    override fun updateInterval() {
        val existingFishSize = engine.getEntitiesFor(FISH_FAMILY).size()

        if (existingFishSize < maxFishSize) {
            val player = engine.getEntitiesFor(PLAYER_FAMILY).first()
            val playerPos = player[POSITION_MAPPER] ?: return
            engine.add {
                val sizeW = 20f
                val sizeH = 10f
                val offset = 5f
                val posX = -sizeW
                val posY = random(gameViewport.coordinatesOfRightBottomCorner.y, playerPos.value.y - sizeH)

                FishBuilder.builder(engine) {
                    texture = context.inject<TextureAtlas>().findRegion("fish_tai")
                    position = vec2(posX, posY)
                    size = vec2(sizeW, sizeH)
                    direction = vec2(1f, 0f)
                    hitBoxSize = vec2(size.x - offset, size.y - offset)
                    hitBoxOffset = vec2(offset / 2.0f, offset / 2.0f)
                }.build()
            }
        }
    }
}