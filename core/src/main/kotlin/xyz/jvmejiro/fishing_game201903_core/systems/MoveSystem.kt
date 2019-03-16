package xyz.jvmejiro.fishing_game201903_core.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.mapperFor
import xyz.jvmejiro.fishing_game201903_core.Move
import xyz.jvmejiro.fishing_game201903_core.Position

class MoveSystem : IteratingSystem(allOf(Position::class, Move::class).get()) {
    companion object {
        private val MOVE_MAPPER: ComponentMapper<Move> = mapperFor()
        private val POSITION_MAPPER: ComponentMapper<Position> = mapperFor()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val move = entity[MOVE_MAPPER] ?: return
        if (move.delay > 0f) {
            move.delay -= deltaTime
            if (move.delay < 0) move(entity, Math.abs(move.delay))
        } else {
            move(entity, deltaTime)
        }
    }

    private fun move(entity: Entity, deltaTime: Float) {
        val move = entity[MOVE_MAPPER] ?: return
        val position = entity[POSITION_MAPPER] ?: return
        move.elapsedTime += deltaTime

        val percent = Math.min(move.elapsedTime / move.duration, 1f)
        val resultX = move.interpolation.apply(move.from.x, move.target.x, percent)
        val resultY = move.interpolation.apply(move.from.y, move.target.y, percent)
        position.value.x = resultX
        position.value.y = resultY

        // move finished
        if (percent >= 1f) entity.remove(Move::class.java)
    }
}
