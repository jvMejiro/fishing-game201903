package xyz.jvmejiro.fishing_game201903_core.states

sealed class PlayerState : StateInterface {
    final override fun default(): StateInterface {
        return IDLE
    }

    object IDLE : PlayerState()
    object ACTIVE : PlayerState()
}