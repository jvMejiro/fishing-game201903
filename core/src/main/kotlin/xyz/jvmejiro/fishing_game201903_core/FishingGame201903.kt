package xyz.jvmejiro.fishing_game201903_core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ktx.app.KtxGame
import ktx.inject.Context
import java.io.File

const val screenWidth = 300f
const val screenHeight = 500f

class FishingGame201903 : KtxGame<Screen>() {

    private val context: Context = Context()

    override fun create() {
        context.register {
            bindSingleton(AssetManager())
            bindSingleton(GameScreen(context))
            bindSingleton(Skin(Gdx.files.internal("data${File.separator}uiskin.json")))
            bindSingleton(TextureAtlas(Gdx.files.internal("atlases${File.separator}sprites.atlas")))
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