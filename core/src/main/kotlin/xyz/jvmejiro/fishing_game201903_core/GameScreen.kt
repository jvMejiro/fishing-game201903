package xyz.jvmejiro.fishing_game201903_core

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxScreen
import ktx.inject.Context
import ktx.math.vec2
import xyz.jvmejiro.fishing_game201903_core.builders.FishingRodBuilder
import xyz.jvmejiro.fishing_game201903_core.builders.PlayerBuilder
import xyz.jvmejiro.fishing_game201903_core.states.*
import xyz.jvmejiro.fishing_game201903_core.systems.*

class GameScreen(private val context: Context) : KtxScreen {
    private lateinit var shapeBatch: ShapeRenderer
    private val batch: SpriteBatch by lazy { SpriteBatch() }
    private val gameStage: Stage by lazy { Stage(FitViewport(screenWidth, screenHeight), batch) }
    private val hudStage: GameHudStage by lazy { GameHudStage(context.inject(), screenWidth, screenHeight, batch) }
    private lateinit var engine: PooledEngine
    private lateinit var backgroundColor: Color
    private lateinit var eventBus: EventBus

    override fun show() {
//        Gdx.app.logLevel = Application.LOG_DEBUG

        // initialize
        shapeBatch = ShapeRenderer()
        engine = PooledEngine()
        backgroundColor = Color(91f / 256f, 110f / 256f, 225f / 256f, 1f)

        // resister systems
        eventBus = EventBus()
        engine.addSystem(ShapeRenderSystem(shapeBatch))
        engine.addSystem(RenderSystem(batch))
        engine.addSystem(PropellingSystem(1.0f / 60.0f))
        engine.addSystem(PropellingLogicSystem(eventBus, gameStage.viewport))
        engine.addSystem(StateSystem())
        engine.addSystem(MoveSystem())

        engine.addSystem(FishSpawnSystem(context, 1, gameStage.viewport, 0.1f))
        engine.addSystem(FishSystem(eventBus, gameStage.viewport))
        engine.addSystem(FishingRodSystem(eventBus, gameStage.viewport))
        engine.addSystem(HookSystem(eventBus))

        engine.addSystem(PlayerSystem(eventBus))
        engine.addSystem(PlayerControlSystem(gameStage.viewport))

        // resister entities
        val tempH = screenHeight * 0.75f
        val player = PlayerBuilder.builder(engine) {
            position = vec2(10f, tempH)
            size = vec2(20f, 30f)
        }.build()

        FishingRodBuilder.builder(player, engine) {
            position = vec2(25f, tempH + 15f)
            size = vec2(10f, 20f)
            hookNum = 10
            hookSpawnOffset = vec2(10f, -50f)
        }.build()

        // resister events
        eventBus.register(object : EventListener {
            override fun onEvent(event: EventInterface, eventData: EventData) {
                if (eventData.body is Int) hudStage.addPoint(eventData.body as Int)
            }
        }, GameStateEvent.GET_POINT)
    }

    override fun resize(width: Int, height: Int) {
        gameStage.viewport.update(width, height)
        hudStage.viewport.update(width, height)
    }

    override fun render(delta: Float) {

        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        shapeBatch.projectionMatrix = gameStage.viewport.camera.combined
        gameStage.batch.projectionMatrix = gameStage.viewport.camera.combined

        shapeBatch.begin(ShapeRenderer.ShapeType.Filled)
        shapeBatch.color = Color.YELLOW
        val fl = screenHeight * 0.25f
        shapeBatch.rect(0f, screenHeight - fl, gameStage.viewport.worldWidth, fl)
        shapeBatch.color = Color.CYAN
        shapeBatch.rect(
            0f,
            gameStage.viewport.coordinatesOfRightBottomCorner.y,
            gameStage.viewport.worldWidth,
            gameStage.viewport.worldHeight - fl
        )
        shapeBatch.end()

//        gameStage.viewport.worldWidth += .5f
//        gameStage.viewport.worldHeight += .5f

        gameStage.viewport.apply(vec2(0f, screenHeight))
        hudStage.viewport.apply(vec2(0f, screenHeight))

        eventBus.update(delta)
        engine.update(Math.min(delta, 1 / 60f))

        gameStage.draw()
        hudStage.draw()
    }

    override fun dispose() {
        super.dispose()
        shapeBatch.dispose()
        gameStage.dispose()
        hudStage.dispose()
    }
}