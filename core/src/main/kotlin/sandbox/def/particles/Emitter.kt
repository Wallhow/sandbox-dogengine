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
import sandbox.sandbox.def.particles.*
import space.earlygrey.shapedrawer.ShapeDrawer

class Emitter(val config: Configuration) : Pool<Particle>(200) {
    private val position = vec2(0f, 0f)
    private val particles: Array<Particle> = Array()
    private var isRun = false
    private var isInfiniteEmission = config.isInfinite
    //TODO переделать обязательно!
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
                if(config.pDirectionInterpolation) {
                   it.direction.y = Interpolation.pow3Out.apply(config.pDirectionMin.y,config.pDirectionMax.y,it.timer/it.maxAge)
                }
                it.x += it.direction.x * it.speed * delta
                it.y += it.direction.y * it.speed * delta
                it.angle += it.speedRotation * delta
                it.currentAge = interpolationColor.apply(0f,it.maxAge,it.timer)
                interpolationColor(it)
                if (it.timer > it.maxAge) {
                    if(isInfiniteEmission) {
                       initParticle(it)
                    } else {
                        particles.removeValue(it, true)
                        free(it)
                    }
                }
                it.timer += delta
            }
            if (particles.isEmpty) {
                isRun = false
            }
        }
    }
    
    val interpolationColor = Interpolation.fade
    private fun interpolationColor(particle: Particle) {
        val timeInterval = (particle.maxAge/config.colors.size) //временной интервал на смену одого цвета
        val age = when {
            particle.currentAge < 0 -> 0f
            particle.currentAge>particle.maxAge -> particle.maxAge
            else -> particle.currentAge
        }

        var currentT = (age/timeInterval).toInt() //текущий цвет в массиве
        if(currentT>=config.colors.size) currentT = config.colors.size-1

        val color = config.colors[currentT]
        val color2 = if (currentT+1 >= config.colors.size)  color else config.colors[currentT+1]
        
        val koff = (timeInterval*currentT)
        val a = (age-koff)/timeInterval

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
        for (i in 0..config.randomCount()) {
            particles.add(initParticle(obtain()))
        }
    }
    private fun initParticle(particle: Particle) : Particle {
        return particle.apply {
            maxAge = config.randomLife()
            speed = config.randomSpeed()
            speedRotation = config.randomRotation()
            direction = config.randomDirection()
            size = MathUtils.random(config.pSizeMin,config.pSizeMax)
            x = config.randomOffsetPositionX()
            y = config.randomOffsetPositionY()
            lazyBirth = config.randomLazyBirth()
            currentAge = 0f
            timer = 0f
            angle = 0f
        }
    }

    fun draw() {
        particles.forEach {
            drawer.setColor(it.r, it.g, it.b, it.alpha)
            drawer.filledRectangle(position.x + it.x - it.size / 2, position.y + it.y - it.size / 2,
                    it.size, it.size,
                    it.angle)
        }
    }

    class Configuration {
        var pDirectionInterpolation: Boolean = false
        var isInfinite: Boolean = false
        var pCountMin: Int = 0
            set(value) {
                field = value
                pCountMax = value + 1
            }
        var pCountMax: Int = pCountMin + 1
        var pLifeTimeMin: Float = 1f
        var pLifeTimeMax: Float = pLifeTimeMin + 1
        var pSpeedMin: Float = 1f
        var pSpeedMax: Float = pSpeedMin + 1
        var pDirectionMin: Vector2 = Vector2()
        var pDirectionMax: Vector2 = pDirectionMin
        var pRotationMin: Float = 0f
        var pRotationMax: Float = pRotationMin + 1
        var textureRegion: TextureRegion = Kernel.dotTexture
        var pSizeMin: Float = 10f
        var pSizeMax: Float = pSizeMin+1
        var colors : Array<Color> = Array()
        var pOffsetMinX = 0f
        var pOffsetMaxX = 0f
        var pOffsetMinY = 0f
        var pOffsetMaxY = 0f
        var pLazyBirthMin = 0f
        var pLazyBirthMax = 0f
    }
}