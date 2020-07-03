package dogengine.utils

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport

class GameCamera (private val viewport: Viewport) {
    private val worldSize = Size()
    fun getViewport() : Viewport = viewport
    fun getCamera() : OrthographicCamera = viewport.camera as OrthographicCamera
    fun setWorldSize(width: Float, height: Float) {
        worldSize.set(width,height)
    }
    fun getWorldSize() : Size = worldSize
    fun setPosition(x: Float,y: Float) {
        getCamera().position.set(x,y,getCamera().position.z)
    }
    fun translate(x: Float = 0f, y: Float = 0f) {
        getCamera().translate(x,y)
    }
    fun update() {
        getCamera().update()
    }
    fun getScaledViewport() : Size {
        return Size(getCamera().viewportWidth*getCamera().zoom,
                getCamera().viewportHeight*getCamera().zoom)
    }

    fun inViewBounds(point: Vector2): Boolean {
        return Rectangle.tmp.set(getCamera().position.x-getViewport().worldWidth/2f,
                getCamera().position.y-getViewport().worldHeight/2f,
                getViewport().worldWidth,getViewport().worldHeight).contains(point)
    }
}