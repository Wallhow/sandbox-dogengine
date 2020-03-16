package sandbox.dogengine.ecs.components.controllers

import com.badlogic.gdx.Input

interface EventListener {
    fun keyJustPressed(keyCode: Int)
    fun keyPressed(keyCode: Int, input: Input)
    fun keyReleased(keyCode: Int)

}