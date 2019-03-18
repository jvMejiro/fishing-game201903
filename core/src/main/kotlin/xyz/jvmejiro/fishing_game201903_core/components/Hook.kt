package xyz.jvmejiro.fishing_game201903_core.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import ktx.ashley.has
import ktx.ashley.mapperFor
import ktx.math.vec2

class Hook(
    val hookOffset: Vector2 = vec2(),
    val from: Vector2,
    val to: Vector2,
    parentFishingRod: Entity,
    caughtFish: Entity? = null
) : Component {
    companion object {
        private val FISH_MAPPER: ComponentMapper<Fish> = mapperFor()
        private val FISHING_ROD_MAPPER: ComponentMapper<FishingRod> = mapperFor()
    }

    var parentFishingRpd: Entity = parentFishingRod
        set(value) {
            value.let {
                if (!it.has(FISHING_ROD_MAPPER)) throw IllegalArgumentException()
                field = value
            }
        }

    var caughtFish: Entity? = null
        set(value) {
            value?.let {
                if (!it.has(FISH_MAPPER)) throw IllegalArgumentException()
                field = value
            }
        }

    override fun toString(): String {
        return "Hook(from=$from, to=$to, caughtFish=$caughtFish)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Hook

        if (hookOffset != other.hookOffset) return false
        if (from != other.from) return false
        if (to != other.to) return false
        if (caughtFish != other.caughtFish) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hookOffset.hashCode()
        result = 31 * result + from.hashCode()
        result = 31 * result + to.hashCode()
        result = 31 * result + (caughtFish?.hashCode() ?: 0)
        return result
    }
}