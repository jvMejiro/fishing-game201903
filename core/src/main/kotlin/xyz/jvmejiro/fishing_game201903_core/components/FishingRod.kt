package xyz.jvmejiro.fishing_game201903_core.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import ktx.ashley.has
import ktx.ashley.mapperFor
import ktx.math.vec2

class FishingRod(var hookSpawnPointOffset: Vector2 = vec2(), var hookNum: Int = 1, parentPlayer: Entity) :
    Component {
    companion object {
        val PLAYER_MAPPER: ComponentMapper<Player> = mapperFor()
    }

    var parentPlayer: Entity = parentPlayer
        set(value) {
            value.let {
                if (!it.has(FishingRod.PLAYER_MAPPER)) throw IllegalArgumentException()
                field = value
            }
        }

    var sinkingHookNum: Int = 0
        set(value) {
            if (field < 0) throw IllegalArgumentException()
            field = value
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FishingRod

        if (hookSpawnPointOffset != other.hookSpawnPointOffset) return false
        if (hookNum != other.hookNum) return false
        if (parentPlayer != other.parentPlayer) return false
        if (sinkingHookNum != other.sinkingHookNum) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hookSpawnPointOffset.hashCode()
        result = 31 * result + hookNum
        result = 31 * result + parentPlayer.hashCode()
        result = 31 * result + sinkingHookNum
        return result
    }
}