package sandbox.sandbox.def.map2D

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.viewport.Viewport
import com.dongbat.jbump.Item
import com.google.inject.Inject
import sandbox.dogengine.ashley.components.utility.CTransforms
import dogengine.ashley.components.draw.CDrawable
import dogengine.def.ComponentResolver
import dogengine.utils.Size
import sandbox.R
import sandbox.def.CJBumpAABB
import sandbox.dogengine.ashley.components.components
import sandbox.dogengine.ashley.components.create
import sandbox.dogengine.ashley.components.createEntity

class SMap2DRenderer @Inject constructor(private val viewport: Viewport, private val camera: OrthographicCamera) : IteratingSystem(Family.all(CMap2D::class.java).get()) {
    private val rectViewBounds: Rectangle = Rectangle()
    private var firstRun = true
    private var entityBuffer: Array<Entity> = Array()
    val tileSize = Size()
    val tilemap: ArrayMap<Int, TextureAtlas.AtlasRegion> = ArrayMap()

    init {
        priority = 2
        for (i in 1..280) {
            tilemap.put(i, TextureAtlas(R.matlas0).findRegion("tile", i))
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (CMap2D[entity].map2D != null) {
            val map = CMap2D[entity].map2D!!
            if (firstRun) {
                firstRun = false
                updateViewport(map)
            }
            updateRectViewBounds(camera)
            if (CMap2D[entity].currentLayer == -1) {
                val cells = map.getLayer(0).getCellsInViewBounds(rectViewBounds)
                cells.forEach {
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
        //println(countDrawableObj)
    }

    fun reset() {
        firstRun = true
        rectViewBounds.set(0f, 0f, 0f, 0f)
    }

    private fun createDrawableEntity(it: Cell) {
        countDrawableObj++
        if ((it.userData is String)) return
        val s = Size(tileSize.width, tileSize.height)
        val pos = Vector2(it.x * s.width, it.y * s.height)
        val e = engine.createEntity {
            components {
                create<CTransforms> {
                    position = pos
                    size = s
                }
                create<CDrawable> {
                    texture = tilemap.get(it.userData as Int)
                    isDeleteAfterDraw = true
                }

                if (!it.collidable) {
                    create<CJBumpAABB> {
                        item = Item(this@components)
                    }

                }
            }
        }
        engine.addEntity(e)
    }

    private fun updateRectViewBounds(camera: Camera) {
        rectViewBounds.set(camera.position.x - camera.viewportWidth * 0.5f,
                camera.position.y - camera.viewportHeight * 0.5f,
                camera.viewportWidth, camera.viewportHeight)
    }

    private fun updateViewport(map: Map2D) {
        val widthWorld = map.getWidthMap()
        val heightWorld = map.getHeightMap()
        val widthTile = map.getTileWidth()
        val heightTile = map.getTileHeight()
        viewport.setWorldSize((widthWorld * widthTile) * 1f,
                (heightWorld * heightTile) * 1f)
        viewport.camera.update()
    }
}

class CMap2D : Component, Pool.Poolable {
    var map2D: Map2D? = null
    var currentLayer = -1
    override fun reset() {
        map2D = null
        currentLayer = -1
    }

    companion object : ComponentResolver<CMap2D>(CMap2D::class.java)
}
