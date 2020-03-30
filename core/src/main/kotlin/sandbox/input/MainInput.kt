package sandbox.sandbox.input

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.google.inject.Injector
import dogengine.ecs.systems.draw.SDrawDebug
import dogengine.ecs.systems.draw.SDrawDebug20
import dogengine.utils.log
import dogengine.utils.system
import sandbox.go.environment.drop.models.GrassDrop
import sandbox.go.environment.drop.models.RockDrop
import sandbox.go.environment.drop.models.WoodDrop
import sandbox.go.environment.drop.models.SandstoneDrop
import sandbox.sandbox.def.def.particles.Emitter
import sandbox.sandbox.def.def.particles.EmitterManager

class MainInput (injector: Injector): InputAdapter() {
    private val camera = injector.getInstance(OrthographicCamera::class.java)
    private val engine = injector.getInstance(Engine::class.java)
    private val eManager = injector.getInstance(EmitterManager::class.java)
        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            val pos = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
            val conf = Emitter.Configuration()
            conf.apply {
                pCountMin = 10
                pSpeedMin = 10f
                pSpeedMax = 20f
                pDirectionMin = Vector2(-0.4f,0.5f)
                pDirectionMax = Vector2(0.4f,1f)
                pLifeTimeMin = 1f
                pLifeTimeMax = 2f
                pRotationMin = -10f
                pRotationMax = 10f
                colors.add(Color.RED,
                        Color.GOLDENROD,
                        Color.DARK_GRAY)
                colors.add(Color.BLACK.cpy().apply { a=0f })
            }
            val em = Emitter(conf)
            em.setTo(Vector2(pos.x,pos.y)).start()
            eManager.addEmitter(em)
            return true
        }

        override fun keyDown(keycode: Int): Boolean {
            if (keycode == Input.Keys.Z) {
                system<SDrawDebug20> {
                    log("debug info drawing")
                    this.visible =!visible }
            }
            return super.keyDown(keycode)
        }

}