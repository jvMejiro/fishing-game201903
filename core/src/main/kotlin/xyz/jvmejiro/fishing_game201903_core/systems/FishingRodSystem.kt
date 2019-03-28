package xyz.jvmejiro.fishing_game201903_core.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.math.plus
import ktx.math.vec2
import xyz.jvmejiro.fishing_game201903_core.builders.HookBuilder
import xyz.jvmejiro.fishing_game201903_core.components.Fish
import xyz.jvmejiro.fishing_game201903_core.components.FishingRod
import xyz.jvmejiro.fishing_game201903_core.components.Hook
import xyz.jvmejiro.fishing_game201903_core.components.Position
import xyz.jvmejiro.fishing_game201903_core.coordinatesOfRightBottomCorner
import xyz.jvmejiro.fishing_game201903_core.states.*

class FishingRodSystem(eventBus: EventBus, val gameViewport: Viewport) :
    StateMachineSystem(eventBus, allOf(FishingRod::class, Position::class).get()) {
    companion object {
        private val FISHING_ROD_MAPPER: ComponentMapper<FishingRod> = mapperFor()
        private val FISH_MAPPER: ComponentMapper<Fish> = mapperFor()
    }

    private val caughtFishesList = mutableListOf<Entity>()
    override fun describeMachine() {
        startWith(FishingRodState.IDLE)
        onState(FishingRodState.IDLE).on(PlayerEvent.THROW_HOOK) { entity, event ->
            go(FishingRodState.SHINKING_HOOKS, entity)
        }
        onState(FishingRodState.SHINKING_HOOKS).on(HookEvent.FINISH) { entity, event ->
            val fishingRod = entity[FISHING_ROD_MAPPER] ?: return@on

            fishingRod.sinkingHookNum--
            val caughtFish = event.body
            if (caughtFish is Entity) caughtFishesList.add(caughtFish)

            if (fishingRod.sinkingHookNum < 1) {
                eventBus.emit(
                    FishingRodEvent.COLLECT_ALL_HOOKS,
                    fishingRod.parentPlayer,
                    EventData().apply { body = caughtFishesList.toList() })
                caughtFishesList.clear()
                go(FishingRodState.IDLE, entity)
            }
        }
    }
}

sealed class FishingRodState : EntityState() {
    companion object {
        val FISHING_ROD_MAPPER: ComponentMapper<FishingRod> = mapperFor()
        val POSITION_MAPPER: ComponentMapper<Position> = mapperFor()
    }

    object WAIT : FishingRodState()
    object IDLE : FishingRodState()
    object THROW_ANIMATION : FishingRodState()
    object SHINKING_HOOKS : FishingRodState() {
        private val SIZE = 10f
        private val HOOK_OFFSET_DATA = arrayOf(
            vec2(0f, 0f) to vec2(-1f, 0f),
            vec2(-SIZE, SIZE) to vec2(1f, 0f),
            vec2(0f, SIZE * 2) to vec2(-1f, 0f),
            vec2(-SIZE, SIZE * 3) to vec2(1f, 0f),
            vec2(0f, SIZE * 4) to vec2(-1f, 0f)
        )

        override fun enter(entity: Entity, machine: StateMachineSystem, eventData: EventData) {
            val fishingRod = entity[FISHING_ROD_MAPPER] ?: return
            val rodPosition = entity[POSITION_MAPPER] ?: return
            fishingRod.sinkingHookNum = MathUtils.clamp(fishingRod.hookNum, 0, HOOK_OFFSET_DATA.size)
            for (idx in 0 until fishingRod.sinkingHookNum) {
                HookBuilder.builder(entity, machine.engine) {
                    val spawnPos = rodPosition.value + fishingRod.hookSpawnPointOffset
                    this.position = spawnPos + HOOK_OFFSET_DATA[idx].first
                    this.size = vec2(SIZE, SIZE)
                    hitBoxSize = vec2(4f, 4f)
                    hitBoxOffset = vec2(1f, 1f)
                    direction = HOOK_OFFSET_DATA[idx].second
                    moveDuration = 3.0f
                    val viewport = (machine as FishingRodSystem).gameViewport
                    sinkDepth = spawnPos.y - viewport.coordinatesOfRightBottomCorner.y
                }.build()
            }
        }

        override fun exit(entity: Entity, machine: StateMachineSystem, eventData: EventData) {
            // HOOKの削除
            val removableEntities = machine.engine.getEntitiesFor(allOf(Hook::class).get()).map { it }
            removableEntities.forEach { machine.engine.removeEntity(it) }
        }
    }
}

enum class FishingRodEvent(private val priority: Int) : EventInterface {
    THROW_HOOK(1), COLLECT_ALL_HOOKS(1);

    override fun getPriority(): Int = priority
}
