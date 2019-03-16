package xyz.jvmejiro.fishing_game201903_core.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.ashley.allOf
import xyz.jvmejiro.fishing_game201903_core.Player

class PlayerControlSystem(val viewport: Viewport) : IteratingSystem(allOf(Player::class).get()) {

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {

        }
    }
}