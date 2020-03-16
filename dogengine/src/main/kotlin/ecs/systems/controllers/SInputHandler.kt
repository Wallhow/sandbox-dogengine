package dogengine.ecs.systems.controllers

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import com.google.inject.Inject
import dogengine.ecs.systems.SystemPriority
import dogengine.utils.log

class SInputHandler @Inject constructor(val camera: OrthographicCamera,multiplexer: InputMultiplexer) : EntitySystem() {
    private val eventListeners = ArrayMap<InputEvent, Array<EventInputListener>>()
    init {
        priority = SystemPriority.BEFORE_UPDATE
        val defInput = DefInputAdapter(camera,eventListeners)
        multiplexer.addProcessor(defInput)
    }

    fun sign(event: InputEvent, eventInputListener: EventInputListener) {
        if(eventListeners[event] == null) {
            eventListeners.put(event,Array<EventInputListener>())
            log("create handler on event $event")
        }
        log("$eventInputListener signed on $event")
        eventListeners[event].add(eventInputListener)
    }

    fun unsubscribe(event: InputEvent,eventInputListener: EventInputListener) {
        if(eventListeners[event] == null) {
            eventListeners[event].removeValue(eventInputListener,true)
        }
    }

    private class DefInputAdapter(val camera: OrthographicCamera,val eventListeners: ArrayMap<InputEvent, Array<EventInputListener>>) : InputAdapter() {
        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            val pos = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
            eventListeners[InputEvent.SCREEN_TOUCH]?.forEach {
                val result = it.touchDown(screenX.toFloat(), Gdx.graphics.height-screenY.toFloat())
                if (!result)
                    return@forEach
            }
            return super.touchDown(screenX, screenY, pointer, button)
        }

        override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
            val pos = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
            eventListeners[InputEvent.SCREEN_TOUCH]?.forEach {
                val result = it.touchDragged(pos.x, pos.y, pointer)
                if (!result)
                    return@forEach
            }
            return super.touchDragged(screenX, screenY, pointer)
        }

        override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            val pos = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
            eventListeners[InputEvent.SCREEN_TOUCH]?.forEach {
                val result = it.touchUp(pos.x, pos.y, pointer)
                if (!result)
                    return@forEach
            }
            return super.touchUp(screenX, screenY, pointer, button)
        }
    }
}

enum class InputEvent {
    SCREEN_TOUCH
}

interface IEventInputListener {
    fun touchDown(x: Float, y: Float): Boolean
    fun touchDragged(x:Float, y:Float, pointer: Int): Boolean
    fun touchUp(x:Float, y:Float, pointer: Int): Boolean
}
open class EventInputListener: IEventInputListener {
    override fun touchDown(x: Float, y: Float): Boolean {
        return true
    }

    override fun touchDragged(x: Float, y: Float, pointer: Int): Boolean {
        return true
    }

    override fun touchUp(x: Float, y: Float, pointer: Int): Boolean {
        return true
    }
}