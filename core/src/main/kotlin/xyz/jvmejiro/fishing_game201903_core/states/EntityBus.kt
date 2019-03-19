package xyz.jvmejiro.fishing_game201903_core.states

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.log.debug
import xyz.jvmejiro.fishing_game201903_core.states.StateMachineSystem.Companion.SystemEvent

interface EventListener {
    fun onEvent(event: EventInterface, eventData: EventData)
}

// FIXME: I should be abble to target multiple stuff at once
class EventData(var event: EventInterface? = null, var target: Entity? = null, var body: Any? = null) : Pool.Poolable {
    override fun reset() {
        event = null
        target = null
        body = null
    }
}

data class EventTimer(var timer: Float = 0f, val event: EventInterface, val data: EventData)


/**
 * Simple Event bus.
 *
 * - register listener through register method
 * - The update method should be called from the main loop
 *
 *
 */
class EventBus(val eventMapper: Map<EventInterface, String> = emptyMap()) {

    private val pool: Pool<EventData> = object : Pool<EventData>() {
        override fun newObject(): EventData = EventData()
    }

    class EventInputProcessor(val bus: EventBus) : InputAdapter() {

        private val touchDown = Vector2()
        private val touchUp = Vector2()

        override fun keyDown(keycode: Int): Boolean {
            val keyEventData = bus.createEventData()
            keyEventData.body = keycode
            bus.emit(SystemEvent.EVENT_KEY, keyEventData)
            return super.keyDown(keycode)
        }

        override fun keyUp(keycode: Int): Boolean {
            val keyEventData = bus.createEventData()
            keyEventData.body = keycode
            bus.emit(SystemEvent.EVENT_KEY_UP, keyEventData)
            return super.keyDown(keycode)
        }

        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {

            val screenTouchData = bus.createEventData()
            touchDown.set(screenX.toFloat(), screenY.toFloat())
            screenTouchData.body = touchDown
            bus.emit(SystemEvent.EVENT_TOUCHED, screenTouchData)
            return super.touchDown(screenX, screenY, pointer, button)
        }

        override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
            touchDown.set(screenX.toFloat(), screenY.toFloat())
            return super.touchDragged(screenX, screenY, pointer)
        }

        override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            val screenTouchData = bus.createEventData()
            touchUp.set(screenX.toFloat(), screenY.toFloat())
            screenTouchData.body = touchDown to touchUp
            bus.emit(SystemEvent.EVENT_SLIDE, screenTouchData)
            return super.touchUp(screenX, screenY, pointer, button)
        }
    }

    init {
        resetInputProcessor()
    }

    private var currentProcessor: InputProcessor? = null

    private fun resetInputProcessor() {
        currentProcessor = Gdx.input.inputProcessor
        val processor = EventInputProcessor(this)
        if (currentProcessor == null) {
            Gdx.input.inputProcessor = processor
        } else {
            Gdx.input.inputProcessor = InputMultiplexer(processor, currentProcessor)
        }
    }

    fun clearInputProcessor() {
        Gdx.input.inputProcessor = null
    }

    private var listeners: MutableMap<EventInterface, List<EventListener>> = mutableMapOf()

    private var emitter: MutableList<Pair<EventInterface, EventData>> = mutableListOf()
    private var emitterMirror: MutableList<Pair<EventInterface, EventData>> = mutableListOf()

    private var emitterLatter: MutableList<EventTimer> = mutableListOf()
    private var emitterLatterMirror: MutableList<EventTimer> = mutableListOf()

    fun createEventData(): EventData = pool.obtain()

    fun emit(event: EventInterface, entity: Entity, data: EventData = createEventData()) {
        data.target = entity
        emit(event, data)
    }

    fun emit(event: EventInterface, data: EventData = createEventData()) {
        data.event = event
        debug { "will emit ${eventMapper[event] ?: "??"} (id : $event)" }
        emitter.add(event to data)
    }

    fun emit(event: EventInterface, vararg entities: Entity, data: EventData = createEventData()) {
        entities.forEach {
            val cpy = createEventData()
            cpy.body = data.body
            emit(event, it, cpy)
        }
    }

    fun emitLater(delta: Float, event: EventInterface, entity: Entity, data: EventData = createEventData()) {
        data.target = entity
        emitLater(delta, event, data)
    }

    fun emitLater(delta: Float, event: EventInterface, data: EventData = createEventData()) {
        data.event = event
        val timer = EventTimer(delta, event, data)
        emitterLatter.add(timer)
    }


    fun register(eventListener: EventListener, vararg events: EventInterface) {
        events.forEach {
            val lst = listeners[it]
            if (lst == null) {
                listeners.put(it, listOf(eventListener))
            } else {
                listeners.put(it, lst + eventListener)
            }
        }
    }


    fun update(delta: Float) {

        emitterLatter.forEach { it.timer -= delta }
        val toEmitNow = emitterLatter.filter { it.timer < 0 }

        emitterLatterMirror.addAll(toEmitNow)
        emitterLatterMirror.sortBy { it.event.getPriority() }

        emitterMirror.addAll(emitter)
        emitterMirror.sortBy { it.first.getPriority() }

        debug { emitterMirror.joinToString("\n") { "emit ${eventMapper[it.first] ?: "??"} (id : ${it.first})" } }

        emitterLatterMirror.forEach { invoke(it.event, it.data) }
        emitterMirror.forEach { invoke(it.first, it.second) }

        val eventDataToReset = toEmitNow.map { it.data } + emitterMirror.map { it.second }

        emitterLatter.removeAll(toEmitNow)
        emitter.removeAll(emitterMirror)

        eventDataToReset.forEach { pool.free(it) }

        emitterMirror.clear()
        emitterLatterMirror.clear()

    }

    private fun invoke(event: EventInterface, data: EventData) {
        listeners[event]?.forEach { lst -> lst.onEvent(event, data) }
    }
}
