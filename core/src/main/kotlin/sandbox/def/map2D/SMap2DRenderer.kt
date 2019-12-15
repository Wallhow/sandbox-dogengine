package sandbox.sandbox.def.map2D

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import com.google.inject.Inject
import dogengine.ashley.components.CTransforms
import dogengine.ashley.components.draw.CDrawable
import dogengine.def.ComponentResolver
import dogengine.utils.Size

class SMap2DRenderer @Inject constructor(private val viewport: Viewport,private val camera: OrthographicCamera) : IteratingSystem(Family.all(CMap2D::class.java).get()) {
    private val rectViewBounds : Rectangle = Rectangle()
    private var firstRun = true
    private val tex = Texture(Pixmap(16,16,Pixmap.Format.RGBA8888).apply { setColor(Color.GRAY);fill() })
    init {
        priority = 2
    }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (CMap2D[entity].map2D != null) {
            val map = CMap2D[entity].map2D!!
            if(firstRun) {
                firstRun = false
                updateViewport(map)
            }
            updateRectViewBounds(camera)
            if(CMap2D[entity].currentLayer==-1) {
                map.getLayer(0).getCellsInViewBounds(rectViewBounds).forEach {
                    createDrawableEntity(it)
                }
            } else {
                map.getLayer(CMap2D[entity].currentLayer).getCellsInViewBounds(rectViewBounds).forEach {
                    createDrawableEntity(it)
                }
            }

        }
    }
    var countDrawableObj = 0
    override fun update(deltaTime: Float) {
        countDrawableObj = 0
        super.update(deltaTime)
        println(countDrawableObj)
    }

    fun reset() {
        firstRun = true
        rectViewBounds.set(0f,0f,0f,0f)
    }

    private fun createDrawableEntity(it: Cell) {
        countDrawableObj++
        if((it.userData is String)) return
        val texture = (it.userData as Texture)
        val s = Size(texture.width.toFloat(), texture.height.toFloat())
        val pos = Vector2(it.x *s.width,it.y*s.height)
        val transform = engine.createComponent(CTransforms::class.java)
        transform.position.set(pos)
        transform.size = s

        val drawable = engine.createComponent(CDrawable::class.java)
        drawable.texture = TextureRegion(texture)
        drawable.isDeleteAfterDraw = true
        val e = engine.createEntity().add(transform).add(drawable)
        engine.addEntity(e)
    }

    private fun updateRectViewBounds(camera: Camera) {
        rectViewBounds.set(camera.position.x-camera.viewportWidth*0.5f,
                camera.position.y - camera.viewportHeight*0.5f,
                camera.viewportWidth,camera.viewportHeight)
    }

    private fun updateViewport(map: Map2D) {
        val widthWorld = map.getWidthMap()
        val heightWorld = map.getHeightMap()
        val widthTile = map.getTileWidth()
        val heightTile = map.getTileHeight()
        viewport.setWorldSize((widthWorld * widthTile)*1f,
                (heightWorld * heightTile)*1f)
        viewport.camera.update()
    }
}

class CMap2D : Component {
    var map2D : Map2D? = null
    var currentLayer = -1
    companion object : ComponentResolver<CMap2D>(CMap2D::class.java)
}
