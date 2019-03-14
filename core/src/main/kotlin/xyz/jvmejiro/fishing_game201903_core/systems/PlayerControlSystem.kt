package xyz.jvmejiro.fishing_game201903_core.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family.all
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.utils.viewport.Viewport
import xyz.jvmejiro.fishing_game201903_core.Player

class PlayerControlSystem(val viewport: Viewport) : IteratingSystem(all(Player::class.java).get()) {

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            println("aaa")
        }
    }
}