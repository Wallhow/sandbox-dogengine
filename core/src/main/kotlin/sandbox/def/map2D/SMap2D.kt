package sandbox.sandbox.def.map2D

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.viewport.Viewport
import com.dongbat.jbump.Item
import com.google.inject.Inject
import dogengine.ecs.components.draw.CTextureRegion
import dogengine.ecs.components.utility.CDeleteMe
import dogengine.ecs.def.ComponentResolver
import dogengine.utils.Size
import dogengine.utils.isElse
import dogengine.utils.isTrue
import dogengine.utils.viewBoundsRect
import sandbox.R
import sandbox.def.CJBumpAABB
import sandbox.dogengine.ashley.components.components
import sandbox.dogengine.ashley.components.create
import sandbox.dogengine.ashley.components.createEntity
import sandbox.dogengine.ecs.components.utility.logic.CTransforms
import sandbox.dogengine.ecs.components.utility.logic.updateZIndex
import sandbox.sandbox.def.CVisibleEntityListener

class SMap2D @Inject constructor(private val viewport: Viewport, private val camera: OrthographicCamera) : IteratingSystem(Family.all(CMap2D::class.java).get()) {
    private var firstRun = true
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
            val scaleViewBounds = camera.viewBoundsRect
            val map = CMap2D[entity].map2D!!
            if (firstRun) {
                firstRun = false; updateViewport(map)
            }
            var indexLayer: Int = 0
            (CMap2D[entity].currentLayer == -1).isTrue { indexLayer = 0 }.isElse { indexLayer = CMap2D[entity].currentLayer}
            map.getLayer(indexLayer).getCellsInViewBounds(scaleViewBounds).forEach {
                initCreate(it)
            }
        }
    }

    private fun initCreate(it: Cell) {
        if (!it.isInEngine) {
            val e = createDrawableEntity(it) ?: return
            engine.addEntity(e)
            it.isInEngine = true
        }
    }

    fun reset() {
        firstRun = true
    }

    private fun createDrawableEntity(it: Cell): Entity? {
        if ((it.userData is String)) return null
        val s = Size(tileSize.width, tileSize.height)
        val pos = Vector2(it.x * s.width, it.y * s.height)
        return engine.createEntity {
            components {
                create<CTransforms> {
                    position = pos
                    size = s
                    updateZIndex()
                }
                create<CTextureRegion> {
                    texture = tilemap.get(it.userData as Int)
                }

                if (!it.collidable) {
                    create<CJBumpAABB> {
                        item = Item(this@components)
                    }

                }
                create<CCell> { cell = it }
                create<CVisibleEntityListener> {
                    hide = { e ->
                        CCell[e].cell?.isInEngine = false
                        create<CDeleteMe> {  }
                    }
                    visible = { e ->
                        CCell[e].cell?.isInEngine = true
                    }
                }
            }
        }
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
