package xyz.jvmejiro.fishing_game201903_core.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.ashley.add
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.math.plus
import ktx.math.vec2
import ktx.math.vec3
import xyz.jvmejiro.fishing_game201903_core.builders.HookBuilder
import xyz.jvmejiro.fishing_game201903_core.components.FishingRod
import xyz.jvmejiro.fishing_game201903_core.components.Hook
import xyz.jvmejiro.fishing_game201903_core.components.Position
import xyz.jvmejiro.fishing_game201903_core.coordinatesOfRightBottomCorner
import xyz.jvmejiro.fishing_game201903_core.states.*

class FishingRodSystem(eventBus: EventBus, val stage: Stage) :
    StateMachineSystem(eventBus, allOf(FishingRod::class, Position::class).get()) {
    companion object {
        private val FISHING_ROG_MAPPER: ComponentMapper<FishingRod> = mapperFor()
    }

    override fun describeMachine() {
        startWith(FishingRodState.IDLE)
        onState(FishingRodState.IDLE).on(PlayerEvent.THROW_HOOK) { entity, event ->
            go(FishingRodState.SHINKING_HOOKS, entity)
        }
        onState(FishingRodState.SHINKING_HOOKS).on(HookEvent.FINISH) { entity, event ->
            val fishingRod = entity[FISHING_ROG_MAPPER] ?: return@on
            fishingRod.sinkingHookNum--
            if (fishingRod.sinkingHookNum < 1) {
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
            val position = entity[POSITION_MAPPER] ?: return
            fishingRod.sinkingHookNum = fishingRod.hookNum
            for (idx in 0 until fishingRod.hookNum) {
                machine.engine.add {
                    HookBuilder.builder(entity, machine.engine) {
                        this.position = position.value + fishingRod.hookSpawnPointOffset + HOOK_OFFSET_DATA[idx].first
                        this.size = vec2(SIZE, SIZE)
                        hitBoxSize = vec2(4f, 4f)
                        hitBoxOffset = vec2(1f, 1f)
                        direction = HOOK_OFFSET_DATA[idx].second
                        moveDuration = 3.0f
                        val stage = (machine as FishingRodSystem).stage
                        sinkDepth = position.value.y - stage.viewport.coordinatesOfRightBottomCorner.y
                    }.build()
                }
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
    THROW_HOOK(1);

    override fun getPriority(): Int = priority
}
