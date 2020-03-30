package sandbox.sandbox.def.def.particles

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Array
import com.google.inject.Inject
import dogengine.Kernel

class EmitterManager {
    private val arrayEmitter = Array<Emitter>()
    fun addEmitter(emitter: Emitter) {
        arrayEmitter.add(emitter)
    }

    fun draw() {
        arrayEmitter.forEach {
            it.draw()
        }
    }
    fun update(delta:Float) {
        arrayEmitter.forEach {
            it.update(delta)
        }
    }
}