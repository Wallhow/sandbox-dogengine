package sandbox.dev.ecs.world

import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import com.badlogic.gdx.utils.ArrayMap
import com.google.inject.Singleton

@Singleton
class Messenger {
    private val listeners: ArrayMap<Int,Signal<Message>> = ArrayMap()

    fun addListener(typeMessage: Int,listener: Listener<Message>) {
        if(listeners[typeMessage]==null)
            listeners.put(typeMessage,Signal())
        listeners[typeMessage].add(listener)
    }

    fun clear() {
        listeners.clear()
    }

    fun sendMessage(typeMessage: Int, data: Any) {
        listeners[typeMessage]?.let {signal ->
            signal.dispatch(Message(typeMessage,data))
        }
    }
}
data class Message(val typeMessage: Int, val data: Any)

object MessagesType {
    const val EXTRACTION = 1
}