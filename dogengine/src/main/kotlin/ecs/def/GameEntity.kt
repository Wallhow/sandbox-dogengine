package dogengine.ecs.def

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import dogengine.Kernel
import dogengine.ecs.components.utility.CName
import dogengine.utils.extension.get
import dogengine.utils.extension.injector

abstract class GameEntity(val parallaxLayer: Int = 0,cName: CName = CName()) : Entity(){
    init {
        add(cName)
    }
    var name: String
        set(value) {
            CName[this].name = "$value:${newIdx()}"
        }
        get() {return CName[this].name}


    protected val engine: Engine = injector[Engine::class.java]
    companion object {
        private var idx = 0
        fun newIdx() : Int {
            idx++
            return idx
        }
    }
}
