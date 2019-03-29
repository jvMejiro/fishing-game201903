package xyz.jvmejiro.fishing_game201903_core.states

sealed class GameStateEvent : EventInterface {
    object GET_POINT : GameStateEvent()

    override fun getPriority(): Int = 1
}