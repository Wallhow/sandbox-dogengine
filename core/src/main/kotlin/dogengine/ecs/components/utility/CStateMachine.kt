package sandbox.dogengine.ashley.components.utility

import com.anupcowkur.statelin.Machine
import com.anupcowkur.statelin.State
import com.anupcowkur.statelin.Trigger
import com.anupcowkur.statelin.TriggerHandler
import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.Pool
import dogengine.ecs.def.ComponentResolver

class CStateMachine : Component, Pool.Poolable {
    companion object : ComponentResolver<CStateMachine>(CStateMachine::class.java)
    private var machine: Machine? = null
    private val triggers: ArrayMap<Int,Trigger> = ArrayMap()
    private val states: ArrayMap<String,State> = ArrayMap()
    private var lastTriggerName = "".hashCode()

    override fun reset() {
        triggers.clear()
        states.clear()
        lastTriggerName = "".hashCode()
        machine = null
    }

    fun initMachine(startState: State) {
        machine = Machine(startState)
    }

    fun addTrigger(name: Int) : Trigger {
        val trigger = Trigger(name.toString())
        triggers.put(name,trigger)
        return trigger
    }
    fun getTrigger(name: Int) = triggers[name]
    fun setTrigger(triggerName: Int) {
        if (lastTriggerName!=triggerName) {
            lastTriggerName = triggerName
            machine?.trigger(getTrigger(triggerName))
        }
    }

    fun createState(name: String, onEnter: (() -> Unit)?, onExit: (() -> Unit)?) : State {
        val state = State(name, onEnter,onExit)
        states.put(name,state)
        return state
    }
    fun createState(name:String,onEnter: (() -> Unit)) : State {
        return createState(name,onEnter,null)
    }
    fun getState(name: String) = states[name]
    fun setState(name: String) {
        machine?.state = getState(name)
    }
    fun getCurrentState() = machine?.state

    fun addTriggerHandler(triggerHandler: TriggerHandler) {
        machine?.addTriggerHandler(triggerHandler)
    }
    fun addTriggerHandler(state: State,
                          trigger: Trigger,
                          handler: () -> Unit) {
        addTriggerHandler(TriggerHandler(state,trigger,handler))
    }

}