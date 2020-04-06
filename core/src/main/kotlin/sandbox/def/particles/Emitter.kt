package sandbox.sandbox.def.def.particles

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pool
import dogengine.Kernel
import dogengine.utils.log
import dogengine.utils.vec2
import space.earlygrey.shapedrawer.ShapeDrawer

class Emitter(val config: Configuration) : Pool<Particle>(200) {
    private val position = vec2(0f, 0f)
    private val particles: Array<Particle> = Array()
    private var isRun = false
    private var isInfiniteEmission = true
    val drawer = ShapeDrawer(Kernel.getInjector().getInstance(SpriteBatch::class.java)
            , Kernel.getInjector().getProvider(Kernel.DotTexture::class.java).get().get())
    var interpolation = Interpolation.fade
    init {

    }

    override fun newObject(): Particle {
        return Particle()
    }

    fun update(delta: Float) {
        if (isRun) {
            particles.forEach {
                it.x += it.direction.x * it.speed * delta
                it.y += it.direction.y * it.speed * delta
                it.angle += it.speedRotation * delta
                it.currentAge += delta
                interpolationColor(it)
                if (it.currentAge > it.maxAge) {
                    if(isInfiniteEmission) {
                        it.x = 0f
                        it.y = 0f
                        it.currentAge = 0f
                        it.angle = 0f
                    } else {
                        particles.removeValue(it, true)
                        free(it)
                    }
                }
            }
            if (particles.isEmpty) {
                isRun = false
            }
        }
    }

    private fun interpolationColor(particle: Particle) {
        val d = (particle.maxAge/config.colors.size)
        var current = (particle.currentAge/d).toInt()
        if(current>=config.colors.size) current = config.colors.size-1
        val color = config.colors[current]
        val color2 = if (current+1 >= config.colors.size)  color else config.colors[current+1]
        val koff = (d*current)
        val a = (particle.currentAge-koff)/d

        particle.r = interpolation.apply(color.r,color2.r,a)
        particle.g = interpolation.apply(color.g,color2.g,a)
        particle.b = interpolation.apply(color.b,color2.b,a)
        particle.alpha = interpolation.apply(color.a,color2.a,a)
    }

    fun setTo(position: Vector2): Emitter {
        this.position.set(position)
        return this
    }

    fun start() {
        isRun = true
        initParticles()
    }

    fun stop() {
        isRun = false
        resetParticles()
    }

    private fun resetParticles() {
        particles.forEach {
            free(it)
        }
        particles.clear()
    }

    private fun initParticles() {
        for (i in 0..config.rndCount()) {
            val p = obtain().apply {
                maxAge = config.rndLife()
                speed = config.rndSpeed()
                speedRotation = config.rndRotation()
                direction = config.rndDirection()
            }
            particles.add(p)
        }
    }

    fun draw() {
        val size = 10f
        particles.forEach {
            drawer.setColor(it.r, it.g, it.b, it.alpha)
            drawer.filledRectangle(position.x + it.x - size / 2, position.y + it.y - size / 2,
                    it.size, it.size,
                    it.angle)
        }
    }

    class Configuration {
        var pCountMin: Int = 0
            set(value) {
                field = value
                pCountMax = value + 1
            }
        var pCountMax: Int = pCountMin + 1
        var pLifeTimeMin: Float = 1f
        var pLifeTimeMax: Float = pLifeTimeMin + 1
        var pOffsetMin: Int = 0
        var pOffsetMax: Int = pOffsetMin + 1
        var pSpeedMin: Float = 1f
        var pSpeedMax: Float = pSpeedMin + 1
        var pDirectionMin: Vector2 = Vector2()
        var pDirectionMax: Vector2 = pDirectionMin
        var pRotationMin: Float = 0f
        var pRotationMax: Float = pRotationMin + 1
        var textureRegion: TextureRegion = Kernel.getInjector().getProvider(Kernel.DotTexture::class.java).get().get()

        var colors : Array<Color> = Array()

        fun rndLife(): Float {
            return MathUtils.random(pLifeTimeMin, pLifeTimeMax)
        }

        fun rndSpeed(): Float {
            return MathUtils.random(pSpeedMin, pSpeedMax)
        }

        fun rndCount(): Int {
            return MathUtils.random(pCountMin, pCountMax)
        }

        fun rndRotation(): Float {
            return MathUtils.random(pRotationMin, pRotationMax)
        }

        fun rndDirection(): Vector2 {
            return Vector2(MathUtils.random(pDirectionMin.x, pDirectionMax.x),
                    MathUtils.random(pDirectionMin.y, pDirectionMax.y))
        }
    }
}