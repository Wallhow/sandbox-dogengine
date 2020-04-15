package sandbox.sandbox.def.def.sys

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.google.inject.Inject
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CHide
import dogengine.ecs.systems.SystemPriority
import sandbox.sandbox.def.def.comp.CParticleEmitter
import sandbox.sandbox.def.def.particles.Emitter
import sandbox.sandbox.def.def.particles.EmitterManager

class SParticleEmitter @Inject constructor(val sb: SpriteBatch) : IteratingSystem(Family.all(CParticleEmitter::class.java).exclude(CHide::class.java).get()) {
    private val emitterManager = EmitterManager()
    init {
        priority = SystemPriority.DRAW+50
    }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val comp = CParticleEmitter[entity]
        if(comp.added) return
        comp.emittersConf.forEach {
            val em = Emitter(it)
            em.setTo(Vector2(CTransforms[entity].getCenterX(),CTransforms[entity].getCenterY())).start()
            emitterManager.addEmitter(em)
        }
        comp.added = true
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        emitterManager.update(deltaTime)
        // remember SpriteBatch's current functions

        // remember SpriteBatch's current functions
        val srcFunc = sb.blendSrcFunc
        val dstFunc = sb.blendDstFunc
        sb.enableBlending()
        sb.begin()
        sb.setBlendFunction(GL20.GL_ONE,GL20.GL_DST_COLOR)
        emitterManager.draw()
        sb.end()
        sb.setBlendFunction(srcFunc, dstFunc)
    }
}