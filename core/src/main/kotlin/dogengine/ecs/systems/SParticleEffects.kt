package dogengine.ecs.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.google.inject.Inject
import dogengine.particles2d.EffectsManager

class SParticleEffects @Inject constructor(private val batch: SpriteBatch, val emanager: EffectsManager) : EntitySystem() {
    init {
    priority = SystemPriority.DRAW+1
    }
    override fun update(deltaTime: Float) {
        emanager.update(deltaTime)
        batch.begin()
        emanager.draw(batch)
        batch.end()
    }
}