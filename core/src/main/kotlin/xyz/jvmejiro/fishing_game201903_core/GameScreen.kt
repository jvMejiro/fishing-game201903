package xyz.jvmejiro.fishing_game201903_core

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.HdpiUtils
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.KtxScreen
import ktx.ashley.add
import ktx.inject.Context
import ktx.math.vec2
import xyz.jvmejiro.fishing_game201903_core.builders.FishingRodBuilder
import xyz.jvmejiro.fishing_game201903_core.builders.PlayerBuilder
import xyz.jvmejiro.fishing_game201903_core.components.PropellingLogicSystem
import xyz.jvmejiro.fishing_game201903_core.states.EventBus
import xyz.jvmejiro.fishing_game201903_core.systems.*

class GameScreen(private val context: Context) : KtxScreen {
    private lateinit var shapeBatch: ShapeRenderer
    private val batch: SpriteBatch by lazy { SpriteBatch() }
    private val gameStage: Stage by lazy { Stage(FitViewport(screenWidth, screenHeight), batch) }
    private val uiStage: Stage by lazy { Stage(FitViewport(screenWidth, screenHeight), batch) }
    private lateinit var engine: PooledEngine
    private lateinit var backgroundColor: Color
    private lateinit var eventBus: EventBus

    override fun show() {
//        Gdx.app.logLevel = Application.LOG_DEBUG

        // initialize
        shapeBatch = ShapeRenderer()
        engine = PooledEngine()
        backgroundColor = Color(91f / 256f, 110f / 256f, 225f / 256f, 1f)

        val table = Table(context.inject()).top()
        table.debug = true
        val pointLabel = Label("point -> ", context.inject<Skin>())
        val pointShowLabel = Label("??", context.inject<Skin>())

        table.add<Actor>(pointLabel)
        table.add<Actor>(pointShowLabel)
        table.setFillParent(true)
        uiStage.addActor(table)

        // resister systems
        eventBus = EventBus()
        engine.addSystem(ShapeRenderSystem(shapeBatch))
        engine.addSystem(PropellingSystem(1.0f / 60.0f))
        engine.addSystem(PropellingLogicSystem(eventBus))
        engine.addSystem(StateSystem())
        engine.addSystem(MoveSystem())

        engine.addSystem(FishSpawnSystem(1000, 0.1f))
        engine.addSystem(FishSystem(eventBus))
        engine.addSystem(FishingRodSystem(eventBus, gameStage))
        engine.addSystem(HookSystem(eventBus))

        engine.addSystem(PlayerSystem(eventBus))
        engine.addSystem(PlayerControlSystem(gameStage.viewport))

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
                hookNum = 5
                hookGenerateOffset = vec2(10f, 0f)
            }.build()
        }
    }

    override fun resize(width: Int, height: Int) {
        gameStage.viewport.update(width, height)
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
        shapeBatch.rect(0f, 0f, 1000f, 1000f)
        shapeBatch.end()

        gameStage.viewport.worldWidth *= 1.0005f
        gameStage.viewport.worldHeight *= 1.0005f

//        println("${gameStage.viewport.screenX}  ${gameStage.viewport.screenY}")
        gameStage.viewport.apply(vec2(0f, screenHeight))
        uiStage.viewport.apply(vec2(0f, screenHeight))

        eventBus.update(delta)
        engine.update(Math.min(delta, 1 / 60f))

        gameStage.draw()
        uiStage.draw()
    }

    override fun dispose() {
        super.dispose()
        shapeBatch.dispose()
        gameStage.dispose()
        uiStage.dispose()
    }
}

/**
 * Cameraの位置を指定した基準点とし、Viewportの画面サイズの中央に更新する。基準点はCameraの左上に位置する。
 * @param basePosition Cameraの左上となる基準点
 */
private fun Viewport.apply(basePosition: Vector2) {
    HdpiUtils.glViewport(screenX, screenY, screenWidth, screenHeight)
    camera.viewportWidth = worldWidth
    camera.viewportHeight = worldHeight
    camera.position.set(basePosition.x + worldWidth / 2, basePosition.y - worldHeight / 2, 0f)
    camera.update()
}