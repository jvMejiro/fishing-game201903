package xyz.jvmejiro.fishing_game201903_core.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Rectangle
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.has
import ktx.ashley.mapperFor
import ktx.log.debug
import ktx.math.*
import xyz.jvmejiro.fishing_game201903_core.components.*
import xyz.jvmejiro.fishing_game201903_core.states.*

class HookSystem(eventBus: EventBus) :
    StateMachineSystem(
        eventBus,
        allOf(Hook::class, StateComponent::class, Position::class, Hitbox::class, Direction::class).get()
    ) {

    companion object {
        private val FISH_MAPPER: ComponentMapper<Fish> = mapperFor()
        private val POSITION_MAPPER: ComponentMapper<Position> = mapperFor()
        private val SIZE_MAPPER: ComponentMapper<Size> = mapperFor()
        private val HITBOX_MAPPER: ComponentMapper<Hitbox> = mapperFor()
        private val HOOK_MAPPER: ComponentMapper<Hook> = mapperFor()
        private val DIRECTION_MAPPER: ComponentMapper<Direction> = mapperFor()

        private val CAUGHT_TARGET_FISH_FAMILY = allOf(Fish::class, Hitbox::class, Position::class).get()
    }

    override fun describeMachine() {
        startWith(HookState.SINKING)
        onState(HookState.SINKING).on(HookEvent.ARRIVE_TARGET) { entity, event ->
            go(
                HookState.STAY,
                entity,
                event
            )
        }
        onState(HookState.STAY).on(HookEvent.START_FLOAT) { entity, event -> go(HookState.FLOATING, entity, event) }
    }

    private val tmpRectangle = Rectangle()
    override fun processEntity(entity: Entity, deltaTime: Float) {
        super.processEntity(entity, deltaTime)

        // 釣り針にかかった魚のチェック
        entity[HOOK_MAPPER]?.caughtFish ?: kotlin.run {
            caughtFishCheck(entity)
        }

        // 釣り針にかかった魚の移動
        entity[HOOK_MAPPER]?.caughtFish?.let { fishEntity ->
            moveCaughtFish(fishEntity, entity)
        }
    }

    private fun moveCaughtFish(fishEntity: Entity, hookEntity: Entity) {
        val fish = fishEntity[FISH_MAPPER] ?: return
        val fishPosition = fishEntity[POSITION_MAPPER] ?: return
        val fishCenterPos = fishPosition.value + (fishEntity[SIZE_MAPPER]?.value?.div(2.0f) ?: vec2())
        val fishMouthPos = fishPosition.value + fish.mouthOffset

        val hook = hookEntity[HOOK_MAPPER] ?: return
        val hookPosition = hookEntity[POSITION_MAPPER]?.value?.plus(hook.hookOffset) ?: return
        val hookDirection = hookEntity[DIRECTION_MAPPER]?.value ?: return

        // 釣り針の向きを考慮した際における、魚の口の位置の計算
        val mouthOffsetFromCenter = (fishMouthPos - fishCenterPos) * hookDirection
        val currentMouthOffset = fishCenterPos + mouthOffsetFromCenter - fishPosition.value

        fishPosition.value = hookPosition - currentMouthOffset  // 釣り針と魚の口が重なるように、魚の移動
    }

    private fun caughtFishCheck(entity: Entity) {
        val fishEntities = engine.getEntitiesFor(CAUGHT_TARGET_FISH_FAMILY)
        for (fishEntity in fishEntities) {
            val fishPos = fishEntity[POSITION_MAPPER] ?: continue
            val fishHitbox = fishEntity[HITBOX_MAPPER] ?: continue
            tmpRectangle.set(
                fishPos.value.x + fishHitbox.offset.x,
                fishPos.value.y + fishHitbox.offset.y,
                fishHitbox.size.x, fishHitbox.size.y
            )
            val hookPos = entity[POSITION_MAPPER] ?: continue
            val hookHitbox = entity[HITBOX_MAPPER] ?: continue

            val hitboxRect = Rectangle(
                hookPos.value.x + hookHitbox.offset.x,
                hookPos.value.y + hookHitbox.offset.y,
                hookHitbox.size.x, hookHitbox.size.y
            )
            // 衝突判定
            if (tmpRectangle.overlaps(hitboxRect)) {
                debug { "FISH_CAUGHT" }
                entity[HOOK_MAPPER]?.caughtFish = fishEntity
                eventBus.emit(FishEvent.BE_CAUGHT, fishEntity)
                break
            }
        }
    }
}

sealed class HookState : EntityState() {
    companion object {
        private val HOOK_MAPPER: ComponentMapper<Hook> = mapperFor()
        private val MOVE_MAPPER: ComponentMapper<Move> = mapperFor()
        private val STATE_MAPPER: ComponentMapper<StateComponent> = mapperFor()
    }

    object SINKING : HookState() {
        override fun update(entity: Entity, machine: StateMachineSystem, delta: Float) {
            if (!entity.has(MOVE_MAPPER)) machine.emit(HookEvent.ARRIVE_TARGET)
        }
    }

    object STAY : HookState() {
        private val STAY_DURATION = 0.3f

        override fun update(entity: Entity, machine: StateMachineSystem, delta: Float) {
            val state = entity[STATE_MAPPER] ?: return
            if (state.elapsedTime > STAY_DURATION) machine.eventBus.emit(HookEvent.START_FLOAT, entity)
        }
    }

    object FLOATING : HookState() {
        private const val FLOATING_DURATION = 5.0f

        override fun enter(entity: Entity, machine: StateMachineSystem, eventData: EventData) {
            val hook = entity[HOOK_MAPPER] ?: return
            entity.add(
                Move(
                    duration = FLOATING_DURATION, from = hook.to, target = hook.from
                )
            )
        }

        override fun update(entity: Entity, machine: StateMachineSystem, delta: Float) {
            if (!entity.has(MOVE_MAPPER)) {
                val hook = entity[HOOK_MAPPER] ?: return
                val eventData = machine.eventBus.createEventData().apply {
                    target = entity
                    body = hook.caughtFish
                }
                machine.eventBus.emit(HookEvent.FINISH, hook.parentFishingRpd, eventData)
            }
        }
    }

    object WAIT : HookState()
}

enum class HookEvent(private val priority: Int) : EventInterface {
    ARRIVE_TARGET(1),
    START_FLOAT(1),
    FINISH(1);

    override fun getPriority(): Int = priority
}
