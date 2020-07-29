package sandbox.dev.particles

import com.badlogic.gdx.utils.Array
import sandbox.sandbox.def.def.particles.Emitter

class EmitterManager {
    val emitters = Array<Emitter>()
    fun addEmitter(emitter: Emitter) {
        emitters.add(emitter)
    }

    fun draw() {
        emitters.forEach {
            it.draw()
        }
    }
    fun update(delta:Float) {
        emitters.forEach {
            it.update(delta)
        }
    }
}