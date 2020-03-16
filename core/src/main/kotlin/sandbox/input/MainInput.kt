package sandbox.sandbox.input

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
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

class MainInput (injector: Injector): InputAdapter() {
    private val camera = injector.getInstance(OrthographicCamera::class.java)
    private val engine = injector.getInstance(Engine::class.java)
        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            val pos = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
            when (MathUtils.random(4)) {
                0-> {engine.addEntity(SandstoneDrop(Vector2(pos.x,pos.y),pos.y))}
                1-> {engine.addEntity(WoodDrop(Vector2(pos.x,pos.y),pos.y))}
                2-> {engine.addEntity(GrassDrop(Vector2(pos.x,pos.y),pos.y))}
                3-> {engine.addEntity(RockDrop(Vector2(pos.x,pos.y),pos.y))}
            }
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