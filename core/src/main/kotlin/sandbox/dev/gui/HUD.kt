package sandbox.dev.gui

import com.badlogic.gdx.scenes.scene2d.Actor

interface HUD {
    fun getRoot(): Actor
    fun update()
}