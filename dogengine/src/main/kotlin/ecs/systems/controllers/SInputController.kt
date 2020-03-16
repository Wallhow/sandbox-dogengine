package dogengine.ecs.systems.controllers

import com.badlogic.ashley.core.*
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.utils.Array
import com.google.inject.Inject
import dogengine.ecs.systems.SystemPriority
import sandbox.dogengine.ecs.components.controllers.CControllable
import sandbox.dogengine.ecs.components.controllers.EventListener
import java.util.*
import kotlin.collections.HashMap

class SInputController @Inject constructor(multiplexer: InputMultiplexer) : EntitySystem() {
    private val input = InputProcessorDef()

    init {
        priority = SystemPriority.BEFORE_UPDATE
        multiplexer.addProcessor(input)
    }

    override fun addedToEngine(engine: Engine) {
        engine.addEntityListener(Family.all(CControllable::class.java).get(), EntityControllableListener(input.getInputRecorder()))
    }

    override fun update(deltaTime: Float) {
        input.getInputRecorder().update()
    }

    private class InputRecorder {
        private val actionStack: Stack<Pair<Int, Boolean>> = Stack()
        private val actionListeners: Array<EventListener> = Array()
        private val longPressed: HashMap<Int, Boolean> = HashMap()
        fun update() {
            if (longPressed.isNotEmpty()) {
                actionListeners.forEach {
                    longPressed.forEach { (key, isPress) ->
                        if (Gdx.input.isKeyPressed(key) && isPress) {
                            it.keyPressed(key, Gdx.input)
                        }
                    }
                }
            }
            if (actionStack.empty()) return
            actionListeners.forEach {
                val action = actionStack.pop()
                when (action.second) {
                    true -> it.keyJustPressed(action.first)
                    false -> it.keyReleased(action.first)
                }
            }
        }

        fun addActionReleased(keycode: Int) {
            actionStack.push(Pair(keycode, false))
            longPressed[keycode] = false
        }

        fun addActionPressed(keycode: Int) {
            actionStack.push(Pair(keycode, true))
            longPressed[keycode] = true
        }

        fun subscribeListener(c: EventListener) {
            actionListeners.add(c)
        }

        fun unsubscribeListener(eventListener: EventListener) {
            actionListeners.removeValue(eventListener, true)
        }
    }

    private class EntityControllableListener(private val recorder: InputRecorder) : EntityListener {
        override fun entityRemoved(entity: Entity) {
            CControllable[entity]?.let {
                if (it.eventListener != null) {
                    recorder.unsubscribeListener(it.eventListener!!)
                }
            }
        }

        override fun entityAdded(entity: Entity) {
            CControllable[entity]?.let {
                if (it.eventListener != null) {
                    recorder.subscribeListener(it.eventListener!!)
                }
            }
        }
    }

    private class InputProcessorDef : InputAdapter() {
        private val inputRecorder: InputRecorder = InputRecorder()
        override fun keyDown(keycode: Int): Boolean {
            inputRecorder.addActionPressed(keycode)
            return false
        }

        override fun keyUp(keycode: Int): Boolean {
            inputRecorder.addActionReleased(keycode)
            return false
        }

        fun getInputRecorder() : InputRecorder = inputRecorder
    }
}