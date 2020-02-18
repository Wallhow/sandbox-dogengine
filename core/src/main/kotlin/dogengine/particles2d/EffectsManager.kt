package dogengine.particles2d

import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap


class EffectsManager  {
    companion object {
        var  countEmitters: Int = 0
    }

    private val pooledEffects = Array<ParticleEffectPool.PooledEffect>()
    private val arrayIdxParticle: ArrayMap<Int,ParticleEffectPool> = ArrayMap()

    // Метод в котором мы создаем эффект в памяти и присваеваем ему индекс
    fun createEffect(idx: Int,particleEffect: ParticleEffect) {
        arrayIdxParticle.put(idx, ParticleEffectPool(particleEffect,1,5))
    }

    // Запрашиваем эффект из пула или создаём новый
    private fun getEffect(idx: Int) : ParticleEffect {
        val p = arrayIdxParticle.get(idx).obtain()
        pooledEffects.add(p)
        return p
    }

    fun effectToPosition(idx: Int,position: Vector2): ParticleEffect {
        return effectToPosition(idx,position.x,position.y)
    }
    fun effectToPosition(idx:Int,x:Float,y:Float) : ParticleEffect {
        return getEffect(idx).apply { setPosition(x,y) }
    }

    fun update(delta:Float) {
        countEmitters = pooledEffects.size
        for (i in pooledEffects.size - 1 downTo 0) {
            val effect = pooledEffects.get(i)
            effect.update(delta)
            if (effect.isComplete) {
                effect.free()
                pooledEffects.removeIndex(i)
            }
        }
    }
    fun draw(batch: SpriteBatch) {
        for (i in pooledEffects.size - 1 downTo 0) {
            val effect = pooledEffects.get(i)
            effect.draw(batch)
        }
    }
}