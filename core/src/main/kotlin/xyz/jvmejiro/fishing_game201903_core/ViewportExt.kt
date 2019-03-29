package xyz.jvmejiro.fishing_game201903_core

import com.badlogic.gdx.graphics.glutils.HdpiUtils
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
val Viewport.coordinatesOfLeftTopCorner: Vector2
    get() {
        return unproject(vec2(screenX.toFloat(), screenY.toFloat()))
    }


/**
 * Cameraの位置を指定した基準点とし、Viewportの画面サイズの中央に更新する。基準点はCameraの左上に位置する。
 * @param basePosition Cameraの左上となる基準点
 */
fun Viewport.apply(basePosition: Vector2) {
    HdpiUtils.glViewport(screenX, screenY, screenWidth, screenHeight)
    camera.viewportWidth = worldWidth
    camera.viewportHeight = worldHeight
    camera.position.set(basePosition.x + worldWidth / 2, basePosition.y - worldHeight / 2, 0f)
    camera.update()
}