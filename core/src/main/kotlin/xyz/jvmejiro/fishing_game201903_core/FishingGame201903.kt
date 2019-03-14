package xyz.jvmejiro.fishing_game201903_core

import com.badlogic.gdx.Screen
import com.badlogic.gdx.assets.AssetManager
import ktx.app.KtxGame
import ktx.inject.Context

const val screenWidth = 128f
const val screenHeight = 224f

class FishingGame201903 : KtxGame<Screen>() {

    private val context: Context = Context()            

    override fun create() {
        context.register {
            bindSingleton(AssetManager())
            bindSingleton(GameScreen(context))
        }
        addScreen(context.inject<GameScreen>())
        setScreen<GameScreen>()
    }

    override fun render() {
        super.render()
    }

    override fun dispose() {
        super.dispose()
        context.dispose()
    }
}