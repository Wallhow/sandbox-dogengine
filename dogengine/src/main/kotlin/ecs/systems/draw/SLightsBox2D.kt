package dogengine.ecs.systems.draw

import box2dLight.DirectionalLight
import box2dLight.PointLight
import box2dLight.RayHandler
import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.World
import com.google.inject.Inject
import dogengine.ecs.components.getCTransforms
import dogengine.ecs.components.utility.visible.CHide
import dogengine.ecs.components.utility.visible.CLightBox2D
import dogengine.ecs.components.utility.visible.LightType.*
import dogengine.ecs.systems.SystemPriority
import dogengine.ecs.systems.utility.STime
import dogengine.utils.system
import kotlin.properties.Delegates

class SLightsBox2D @Inject constructor(private val camera: OrthographicCamera, private val worldLight: World) : EntitySystem(), EntityListener {
    private lateinit var array: ImmutableArray<Entity>
    private lateinit var rayHandler: RayHandler
    private val timeClock = TimeClock()

    companion object {
        var ambientColor = Color(Color.argb8888(1f, 1f, 0.9f, 0.9f))
        var blur: Int by Delegates.observable(5) { _, _, _ -> }
    }

    init {
        priority = SystemPriority.DRAW + 1
    }

    fun update(world: World) {
        RayHandler.setGammaCorrection(true);
        RayHandler.useDiffuseLight(true);

        rayHandler = RayHandler(world)
        rayHandler.setAmbientLight(ambientColor)
        rayHandler.setBlurNum(blur)
    }

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(this)
        array = engine.getEntitiesFor(Family.all(CLightBox2D::class.java).get())

        update(worldLight)
    }

    override fun entityRemoved(entity: Entity) {
        CLightBox2D[entity]?.let {
            it.light?.dispose()
            array = engine.getEntitiesFor(Family.all(CLightBox2D::class.java).exclude(CHide::class.java).get())
        }
    }

    override fun entityAdded(entity: Entity) {
        CLightBox2D[entity]?.let {
            if (it.light == null && it.type == POINT) {
                val tr = entity.getCTransforms()
                it.light = when (it.type) {
                    POINT -> {
                        PointLight(rayHandler, 128, Color(0.8f,0.5f,0.2f,1f),
                                tr.size.getRadius() * 12,//TODO
                                tr.getCenterX(), tr.getCenterY())
                    }
                    CONE -> TODO()
                    DIRECTIONAL -> {
                        val sunDirection = MathUtils.random(0f, 360f)
                        DirectionalLight(
                                rayHandler, 4 * 128, null, sunDirection)
                    }
                    CHAIN -> TODO()
                }
            }
            array = engine.getEntitiesFor(Family.all(CLightBox2D::class.java).exclude(CHide::class.java).get())
        }
    }

    override fun update(deltaTime: Float) {
        timeClock.update(deltaTime)
        rayHandler.setCombinedMatrix(camera)
        rayHandler.setAmbientLight(timeClock.ambientColor)

        array.forEach {
            CLightBox2D[it].light?.position?.set(it.getCTransforms().position)
        }
        rayHandler.update()
        rayHandler.render()
    }


    private class TimeClock {
        private val DAWNCOLOR = Color(0f, 0.1f, 0.7f, .2f)
        private val LIGHTCOLOR = Color(1f, 0.9f, 0.9f, 1f)
        private val LIGHT2COLOR = Color(0.9f, 0.9f, 0.8f, 1f)
        private val DUSKCOLOR = Color(0.35f, .3f, .67f, .1f)
        private val DARKCOLOR = Color(0f, 0f, 0f, .4f)

        private val dawnTime = 4f
        private val lightTime = 10f
        private val light2Time = 17f
        private val dustTime = 20f
        private val darkTime = 24f

        val ambientColor: Color = Color()
        private var accTime = -1f
        private var t = 0f
        private var color1 = Color()
        private var color2 = Color()
        private var next = dawnTime
        fun update(deltaTime: Float) {
            var hour = 0f
            var scl_: Float = 1f
            system<STime> {
                hour = getCurrentHour()
                scl_ = this.scl
            }
            if(t >= 1f || accTime == -1f) {
                accTime = 0f
                when {
                    hour <= dawnTime -> {
                        color1 = DARKCOLOR
                        color2 = DAWNCOLOR
                        next = dawnTime
                    }
                    hour >= dawnTime && hour < lightTime -> {
                        color1 = DAWNCOLOR
                        color2 = LIGHTCOLOR
                        next = lightTime - dawnTime
                    }
                    hour >= lightTime && hour < light2Time -> {
                        color1 = LIGHTCOLOR
                        color2 = LIGHT2COLOR
                        next = light2Time - lightTime
                    }
                    hour >= light2Time && hour < dustTime -> {
                        color1 = LIGHT2COLOR
                        color2 = DUSKCOLOR
                        next = dustTime - light2Time
                    }
                    hour >= dustTime && hour < darkTime -> {
                        color1 = DUSKCOLOR
                        color2 = DARKCOLOR
                        next = darkTime - dustTime
                    }
                    else -> {
                        color1 = Color.RED
                        color2 = Color.YELLOW
                    }
                }
            }
            if(t>=1f) t = 1f
            t = accTime / (60f*next)
            ambientColor.apply {
                r = Interpolation.fade.apply(color1.r, color2.r, t)
                g = Interpolation.fade.apply(color1.g, color2.g, t)
                b = Interpolation.fade.apply(color1.b, color2.b, t)
                a = Interpolation.fade.apply(color1.a, color2.a, t)
            }
            accTime += deltaTime * scl_
        }
    }

}
