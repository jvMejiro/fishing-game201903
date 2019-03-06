package xyz.jvmejiro.fishing_game201903_core

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.KtxScreen
import ktx.graphics.rect
import ktx.inject.Context

class GameScreen(private val context: Context) : KtxScreen {
    private lateinit var shapeBatch: ShapeRenderer
    private lateinit var batch: SpriteBatch
    private val viewport: Viewport by lazy { FitViewport(screenWidth, screenHeight) }
    private lateinit var engine: PooledEngine
    private lateinit var backgroundColor: Color

    override fun show() {
        shapeBatch = ShapeRenderer()
        engine = PooledEngine()
        batch = SpriteBatch()
        backgroundColor = Color(91f / 256f, 110f / 256f, 225f / 256f, 1f)
        val stage = Stage(viewport, batch)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun render(delta: Float) {
        viewport.apply()

        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        shapeBatch.projectionMatrix = viewport.camera.combined
        batch.projectionMatrix = viewport.camera.combined
        shapeBatch.begin(ShapeRenderer.ShapeType.Filled)
        shapeBatch.color = Color.YELLOW
        shapeBatch.rect(0f, 0f, screenWidth, screenHeight)
        shapeBatch.color = Color.RED
        shapeBatch.circle(screenWidth / 2, screenHeight / 2, screenWidth / 2)
        shapeBatch.end()
        engine.update(Math.min(delta, 1 / 60f))
    }
}