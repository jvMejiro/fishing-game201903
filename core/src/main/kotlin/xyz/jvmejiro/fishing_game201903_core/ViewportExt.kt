package xyz.jvmejiro.fishing_game201903_core

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.math.vec2

/**
 * Viweportの右下角のワールド座標
 */
val Viewport.coordinatesOfRightBottomCorner: Vector2
    get() {
        return unproject(
            vec2(
                (screenX + screenWidth).toFloat(),
                (screenY + screenHeight).toFloat()
            )
        )
    }
