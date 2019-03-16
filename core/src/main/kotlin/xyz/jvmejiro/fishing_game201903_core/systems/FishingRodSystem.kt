package xyz.jvmejiro.fishing_game201903_core.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import ktx.ashley.add
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.math.plus
import ktx.math.vec2
import xyz.jvmejiro.fishing_game201903_core.FishingRod
import xyz.jvmejiro.fishing_game201903_core.Hook
import xyz.jvmejiro.fishing_game201903_core.Position
import xyz.jvmejiro.fishing_game201903_core.builders.HookBuilder
import xyz.jvmejiro.fishing_game201903_core.states.*

class FishingRodSystem(eventBus: EventBus) :
    StateMachineSystem(eventBus, allOf(FishingRod::class, Position::class).get()) {
    override fun describeMachine() {
        startWith(FishingRodState.IDLE)
        onState(FishingRodState.IDLE).on(PlayerEvent.THROW_HOOK) { entity, event ->
            go(FishingRodState.SHINKING_HOOK, entity)
        }
        onState(FishingRodState.SHINKING_HOOK).on(HookEvent.FINISH) { entity, event ->
            go(FishingRodState.IDLE, entity)
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
    object SHINKING_HOOK : FishingRodState() {
        override fun enter(entity: Entity, machine: StateMachineSystem, eventData: EventData) {
            val hooks = machine.engine.getEntitiesFor(allOf(Hook::class).get())
            val fishingRod = entity[FISHING_ROD_MAPPER] ?: return
            val position = entity[POSITION_MAPPER] ?: return
            if (hooks.size() < fishingRod.hookNum) {
                machine.engine.add {
                    HookBuilder.builder(entity, machine.engine) {
                        this.position = position.value + fishingRod.hookGenerateOffset
                        this.size = vec2(5f, 5f)
                        hitBoxSize = vec2(3f, 3f)
                        hitBoxOffset = vec2(1f, 1f)
                        moveDuration = 3.0f
                    }
                }
            }
        }
    }
}

enum class FishingRodEvent(private val priority: Int) : EventInterface {
    THROW_HOOK(1);

    override fun getPriority(): Int = priority
}
