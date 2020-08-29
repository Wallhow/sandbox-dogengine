package sandbox.dev.ecs.sys

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.google.inject.Inject
import dogengine.Kernel
import dogengine.ecs.components.addEntityAddedListener
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CHide
import dogengine.ecs.systems.SystemPriority
import dogengine.utils.GameCamera
import sandbox.dev.ecs.comp.CParticleEmitter
import sandbox.sandbox.def.def.particles.Emitter
import sandbox.dev.particles.EmitterManager
import space.earlygrey.shapedrawer.ShapeDrawer

class SParticleEmitter @Inject constructor(val sb: SpriteBatch) : IteratingSystem(Family.all(CParticleEmitter::class.java).exclude(CHide::class.java).get()) {
    private val eManager = EmitterManager()
    private val drawer = ShapeDrawer(Kernel.getInjector().getInstance(SpriteBatch::class.java)
            , Kernel.getInjector().getProvider(Kernel.DotTexture::class.java).get().get())
    private val gameCamera: GameCamera = Kernel.getInjector().getInstance(GameCamera::class.java)
    init {
        priority = SystemPriority.DRAW+50
    }

    override fun addedToEngine(engine: Engine) {
        engine.addEntityAddedListener(family) {
            val comp = CParticleEmitter[it]
            comp.emittersConf.forEach { conf ->
                val em = Emitter(conf,drawer)
                em.setTo(Vector2(CTransforms[it].getCenterX(),CTransforms[it].getCenterY())).start()
                eManager.addEmitter(em)
            }
        }
        super.addedToEngine(engine)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {

    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        eManager.emitters.forEach { emitter->
            if(gameCamera.inViewBounds(emitter.position)) {
                emitter.update(deltaTime)
            }
        }

        // remember SpriteBatch's current functions
        // remember SpriteBatch's current functions
        val srcFunc = sb.blendSrcFunc
        val dstFunc = sb.blendDstFunc
        sb.enableBlending()
        sb.begin()
        sb.setBlendFunction(GL20.GL_ONE,GL20.GL_DST_COLOR)
        eManager.draw()
        sb.end()
        sb.setBlendFunction(srcFunc, dstFunc)
    }
}