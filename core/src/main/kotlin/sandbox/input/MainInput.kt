package sandbox.sandbox.input

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.google.inject.Injector
import dogengine.Kernel
import dogengine.MessagesType
import dogengine.ecs.components.create
import dogengine.ecs.components.draw.CTextureRegion
import dogengine.ecs.components.utility.CName
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.systems.draw.SDrawDebug20
import dogengine.ecs.systems.tilemap.SMap2D
import dogengine.shadow2d.systems.SShadow2D
import dogengine.utils.*
import dogengine.utils.extension.get
import sandbox.dev.map.Map2DGenerator

class MainInput(injector: Injector) : InputAdapter() {
    private val camera = injector[OrthographicCamera::class.java]
    private val engine = injector[Engine::class.java]
    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return super.touchUp(screenX, screenY, pointer, button)
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val pos = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
        /*SShadow2D.lights.add(PointLight().apply {
            x = pos.x
            y = pos.y
            color = randomColor()
        })
        engine.addEntity(getLightIconEntity(Vector2(pos.x, pos.y)))*/
        return true
    }

    private fun randomColor(): Color {
        val intensity = Math.random().toFloat() * 0.5f + 0.5f
        return Color(Math.random().toFloat(), Math.random().toFloat(), Math.random().toFloat(), intensity)
    }

    private fun getLightIconEntity(position_: Vector2): Entity {
        return Entity().apply {
            create<CTransforms> {
                position = position_
                size = Size(16f, 16f)
                zIndex = Int.MAX_VALUE
            }
            create<CTextureRegion> {
                texture = TextureRegion(Texture(Gdx.files.internal("assets/light.png")))
                color = Color.WHITE.cpy()
            }
            create<CName> { this.name = "light" }

        }
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.Z) {
            system<SDrawDebug20> {
                log("debug info drawing")
                this.visible = !visible
            }
        }
        if (keycode == com.badlogic.gdx.Input.Keys.DOWN) {
            camera.zoom -= 0.3f
        }
        if (keycode == com.badlogic.gdx.Input.Keys.UP) {
            camera.zoom += 0.3f
        }
        if(keycode == Input.Keys.LEFT) {
            var d = SShadow2D.getDetailLevelShadow() + 1
            if(d>5)
                d = 1
            SShadow2D.setDetailLevelShadow(d)
        }
        if (keycode == com.badlogic.gdx.Input.Keys.NUMPAD_4) {
            Kernel.getInjector()[MessageManager::class.java].dispatchMessage(MessagesType.CAMERA_FIXED_BOUNDS,true)
        }
        if (keycode == com.badlogic.gdx.Input.Keys.NUMPAD_6) {
            Kernel.getInjector()[MessageManager::class.java].dispatchMessage(MessagesType.CAMERA_FIXED_BOUNDS,false)
        }
        if (keycode == com.badlogic.gdx.Input.Keys.NUMPAD_2) {
            Kernel.getInjector()[MessageManager::class.java].dispatchMessage(MessagesType.CAMERA_LOOK_BEFORE)
        }

        if(keycode==Input.Keys.BACKSPACE) {
            val map = SMap2D.map2D
            Map2DGenerator.save(map)
            save = true
            gdxSchedule(2f) {
                save = false
            }
        }
        return super.keyDown(keycode)
    }
    companion object {
        var save = false

    }

}