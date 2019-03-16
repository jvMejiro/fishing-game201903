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
import ktx.ashley.add
import ktx.inject.Context
import ktx.math.vec2
import xyz.jvmejiro.fishing_game201903_core.builders.FishingRodBuilder
import xyz.jvmejiro.fishing_game201903_core.builders.PlayerBuilder
import xyz.jvmejiro.fishing_game201903_core.states.EventBus
import xyz.jvmejiro.fishing_game201903_core.systems.*

class GameScreen(private val context: Context) : KtxScreen {
    private lateinit var shapeBatch: ShapeRenderer
    private lateinit var batch: SpriteBatch
    private val viewport: Viewport by lazy { FitViewport(screenWidth, screenHeight) }
    private lateinit var engine: PooledEngine
    private lateinit var backgroundColor: Color
    private lateinit var eventBus: EventBus

    override fun show() {
        // initialize
        shapeBatch = ShapeRenderer()
        engine = PooledEngine()
        batch = SpriteBatch()
        backgroundColor = Color(91f / 256f, 110f / 256f, 225f / 256f, 1f)
        val stage = Stage(viewport, batch)
        eventBus = EventBus()

        // resister systems
        engine.addSystem(ShapeRenderSystem(shapeBatch))
        engine.addSystem(PropellingSystem(1.0f / 60.0f))
        engine.addSystem(StateSystem())
        engine.addSystem(MoveSystem())

        engine.addSystem(FishSpawnSystem(10, 1.0f))
        engine.addSystem(FishSystem(eventBus))
        engine.addSystem(FishingRodSystem(eventBus))
        engine.addSystem(HookSystem(eventBus))

        engine.addSystem(PlayerSystem(eventBus))
        engine.addSystem(PlayerControlSystem(viewport))

        // resister entities
        engine.add {
            val tempH = screenHeight * 0.75f
            PlayerBuilder.builder(engine) {
                position = vec2(10f, tempH)
                size = vec2(20f, 30f)
            }.build()

            FishingRodBuilder.builder(engine) {
                position = vec2(25f, tempH + 15f)
                size = vec2(10f, 20f)
            }.build()
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun render(delta: Float) {
        viewport.apply()

        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        shapeBatch.projectionMatrix = viewport.camera.combined
        batch.projectionMatrix = viewport.camera.combined

        shapeBatch.begin(ShapeRenderer.ShapeType.Filled)
        shapeBatch.color = Color.YELLOW
        shapeBatch.rect(0f, 0f, screenWidth, screenHeight)
//        shapeBatch.color = Color.RED
//        shapeBatch.circle(screenWidth / 2, screenHeight / 2, screenWidth / 2)
        shapeBatch.end()

        eventBus.update(delta)
        engine.update(Math.min(delta, 1 / 60f))
    }
}