package xyz.jvmejiro.fishing_game201903_core.states

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.log.debug
import xyz.jvmejiro.fishing_game201903_core.components.StateComponent
import com.badlogic.gdx.utils.Array as GdxArray


typealias  Transition = StateMachine.(entity: Entity, event: EventData) -> Unit

interface EventInterface {
    fun getPriority(): Int
}

abstract class EntityState {
    /**
     * Called when an entity is going into this state.
     */
    open fun enter(entity: Entity, machine: StateMachineSystem, eventData: EventData) = Unit

    /**
     * Called by the maloop when this state.
     */
    open fun update(entity: Entity, machine: StateMachineSystem, delta: Float) = Unit

    /**
     * Called when an entity is going into another state.
     */
    open fun exit(entity: Entity, machine: StateMachineSystem, eventData: EventData) = Unit

    companion object {
        sealed class SystemState : EntityState() {
            object STATE_NOP : SystemState() {
                override fun update(entity: Entity, machine: StateMachineSystem, delta: Float) {
                    machine.emit(StateMachineSystem.Companion.SystemEvent.EVENT_NOP)
                }
            }
        }
    }
}

interface StateMachine {
    fun go(newState: EntityState, entity: Entity)
    fun go(newState: EntityState, entity: Entity, event: EventData)
    fun emit(event: EventInterface, eventData: EventData)
    fun emit(event: EventInterface)
}

abstract class StateMachineSystem(
    val eventBus: EventBus,
    family: Family,
    priority: Int = 0
) : IteratingSystem(family, priority), EventListener, StateMachine {

    private var transitions = emptyMap<EntityState, Map<EventInterface, Transition>>()
    private var defaultTransition = emptyMap<EntityState, Transition>()

    private val state: ComponentMapper<StateComponent> = mapperFor()

    private val tmpEntities = GdxArray<Entity>()

    private val events: Set<EventInterface>
        get() {
            val allEvents = transitions.values.flatMap { it.keys }.toSet()
            if (defaultTransition.isNotEmpty()) {
                return allEvents + SystemEvent.EVENT_NOP
            }
            return allEvents
        }

    companion object {
        enum class SystemEvent(private val priority: Int) : EventInterface {
            EVENT_NOP(-1),
            EVENT_TOUCHED(-2),
            EVENT_SLIDE(-3),

            EVENT_KEY(-4),
            EVENT_KEY_UP(-5);

            override fun getPriority(): Int = priority
        }
    }

    abstract fun describeMachine()

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        describeMachine()
        eventBus.register(this, *events.toTypedArray())
    }

    private inline fun usingState(entity: Entity, block: (state: StateComponent) -> Unit) {
        val state = entity[state]
        if (state == null) {
            ktx.log.error {
                "Your entity SHOULD have the StateComponent component, " +
                        "as the entity is managed by a State Machine System. " +
                        "As the current entity doesn't have a state, it will be silently ignored. " +
                        "But you may wants to fix this issue as it is not expected."
            }
        } else {
            block.invoke(state)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        usingState(entity) { state ->
            state.state.update(entity, this, deltaTime)
        }
    }

    class OnState(
        private val state: EntityState,
        private val parent: StateMachineSystem
    ) {
        fun on(vararg events: EventInterface, block: Transition): OnState {
            events.forEach { on(it, block) }
            return this
        }

        fun on(events: List<EventInterface>, block: Transition): OnState {
            events.forEach { on(it, block) }
            return this
        }

        fun on(event: EventInterface, block: Transition): OnState {
            var currentTransitions = parent.transitions[state] ?: emptyMap()
            currentTransitions = currentTransitions + (event to block)

            parent.transitions += state to currentTransitions
            return this
        }

        fun default(block: Transition) {
            parent.defaultTransition += state to block
        }
    }

    fun onState(state: EntityState): OnState = OnState(state, this)

    fun startWith(state: EntityState) = startWith { entity, data -> go(state, entity, data) }

    fun startWith(transition: Transition) {
        onState(EntityState.Companion.SystemState.STATE_NOP).default(transition)
    }

    override fun go(newState: EntityState, entity: Entity) = go(newState, entity, eventBus.createEventData())

    override fun go(newState: EntityState, entity: Entity, event: EventData) {
        val entityState = entity[state] ?: return

        debug("STATE_MACHINE") { "Exit ${entityState.state::class.java.simpleName} on event ${event.event}" }
        entityState.state.exit(entity, this, event)

        entityState.state = newState
        debug("STATE_MACHINE") { "Enter ${entityState.state::class.java.simpleName} on event ${event.event}" }
        entityState.state.enter(entity, this, event)
    }

    override fun emit(event: EventInterface) = emit(event, eventBus.createEventData())

    override fun emit(event: EventInterface, eventData: EventData) {
        tmpEntities.clear()
        entities?.let {
            entities.forEach { tmpEntities.add(it) }
        }
        tmpEntities.forEach { perform(event, it, eventData) }
    }

    private fun perform(event: EventInterface, entity: Entity, eventData: EventData) {
        eventData.event = event
        usingState(entity) { state ->
            val entityState = state.state
            val transition: Transition? = transitions[entityState]?.get(event) ?: defaultTransition[entityState]
            transition?.invoke(this, entity, eventData)
        }

    }

    override fun onEvent(event: EventInterface, eventData: EventData) {
        if (!checkProcessing()) {
            return
        }
        val target = eventData.target

        if (target == null) {
            emit(event, eventData)
        } else {
            perform(event, target, eventData)
        }
    }
}