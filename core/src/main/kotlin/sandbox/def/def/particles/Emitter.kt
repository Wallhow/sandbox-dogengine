package sandbox.sandbox.def.def.particles

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pool
import dogengine.utils.vec2

class Emitter(val config: EmitterConfig) : Pool<Particle>(200) {
    private val position = vec2(0f,0f)
    private val particles: Array<Particle> = Array()
    private var isStart = false
    init {

    }

    override fun newObject(): Particle {
        return Particle()
    }

    fun update(delta: Float) {
        if(isStart) {
            particles.forEach {
                it.x = it.direction.x * it.speed * delta
                it.y = it.direction.y * it.speed * delta
                it.currentAge += delta
                if (it.currentAge > it.maxAge) {
                    particles.removeValue(it, true)
                    free(it)
                }
            }
        }
    }
    fun setToStart(position:Vector2) {
        isStart = true
        this.position.set(position)
        for (i in config.pCountMin until config.pCountMax) {
            val p = obtain().apply {
                maxAge = config.rndLife()
                rotation = config.rndRotation()
                speed = config.rndSpeed()
                direction = config.rndDirection()
            }
            particles.add(p)
        }
    }

    data class EmitterConfig(var pCountMin: Int = 0,
                             var pCountMax: Int = pCountMin,
                             var pLifeTimeMin : Float,
                             var pLifeTimeMax : Float = pLifeTimeMin,
                             val pOffsetMin: Int,
                             val pOffsetMax: Int = pOffsetMin,
                             val pSpeedMin : Float,
                             val pSpeedMax : Float = pSpeedMin,
                             val pDirectionMin: Vector2 = Vector2(),
                             val pDirectionMax: Vector2 = pDirectionMin,
                             val pRotationMin : Float,
                             val pRotationMax : Float = pRotationMin,
                             val textureRegion: TextureRegion
                             ) {
        fun rndLife() : Float {
            return MathUtils.random(pLifeTimeMin,pLifeTimeMax)
        }
        fun rndSpeed() : Float {
            return MathUtils.random(pSpeedMin,pSpeedMax)
        }
        fun rndCount() : Int {
            return MathUtils.random(pCountMin,pCountMax)
        }
        fun rndRotation() : Float {
            return MathUtils.random(pRotationMin,pRotationMax)
        }
        fun rndDirection() : Vector2 {
            return Vector2(MathUtils.random(pDirectionMin.x,pDirectionMax.x),
                    MathUtils.random(pDirectionMin.y,pDirectionMax.y))
        }
    }
}