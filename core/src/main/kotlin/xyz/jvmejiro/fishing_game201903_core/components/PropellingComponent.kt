package xyz.jvmejiro.fishing_game201903_core.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array

typealias PropellingTiming = (position: Vector2, size: Vector2) -> Boolean
typealias PropellingLogic = (delta: Float, total: Float) -> Vector2

data class PropellingData(val timing: PropellingTiming, val logic: PropellingLogic, val delay: Float)

data class PropellingComponent(var current: PropellingLogic? = null) : Component
data class PropellingLogicComponent(
    var logic: Array<PropellingData> = Array()
) : Component {
    var lastPropellingData: PropellingData? = null
}
