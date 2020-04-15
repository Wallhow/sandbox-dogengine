package sandbox.sandbox.def.def.comp

import com.badlogic.gdx.utils.Array
import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent
import sandbox.sandbox.def.def.particles.Emitter

class CParticleEmitter: PoolableComponent {
    companion object : ComponentResolver<CParticleEmitter>(CParticleEmitter::class.java)
    val emittersConf: Array<Emitter.Configuration> = Array()
    var added = false
    override fun reset() {
        added = false
        emittersConf.clear()
    }
}