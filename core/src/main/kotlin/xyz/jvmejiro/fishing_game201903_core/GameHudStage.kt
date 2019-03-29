package xyz.jvmejiro.fishing_game201903_core

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.FitViewport

class GameHudStage(private val skin: Skin, screenWidth: Float, screenHeight: Float, batch: Batch) :
    Stage(FitViewport(screenWidth, screenHeight), batch) {

    private var totalPoint = 0
    private val table by lazy { Table(skin).top() }
    private val pointShowLabel: Label

    init {
        table.debug = true
        val pointLabel = Label("point -> ", skin)
        pointShowLabel = Label(totalPoint.toString(), skin)

        table.add<Actor>(pointLabel)
        table.add<Actor>(pointShowLabel)
        table.setFillParent(true)
        addActor(table)
    }

    fun addPoint(added: Int) {
        totalPoint += added
        pointShowLabel.setText(totalPoint)
    }
}