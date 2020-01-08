package sandbox.sandbox.def.map2D

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.viewport.Viewport
import com.google.inject.Inject
import dogengine.ecs.components.components
import dogengine.ecs.components.create
import dogengine.ecs.components.createEntity
import dogengine.ecs.components.draw.CTextureRegion
import dogengine.ecs.components.utility.CDeleteMe
import dogengine.ecs.def.ComponentResolver
import dogengine.utils.Size
import dogengine.utils.isElse
import dogengine.utils.isTrue
import sandbox.R
import sandbox.dogengine.ecs.components.utility.logic.CTransforms
import sandbox.dogengine.ecs.components.utility.visible.CVisibleEntityListener
import sandbox.dogengine.ecs.def.PoolableComponent
import sandbox.sandbox.def.redkin.physicsengine2d.CDefaultPhysics2d

class SMap2D @Inject constructor(private val viewport: Viewport, private val camera: OrthographicCamera) : IteratingSystem(Family.all(CMap2D::class.java).get()) {
    private var firstRun = true
    val tileSize = Size()
    val tilemap: ArrayMap<Int, TextureAtlas.AtlasRegion> = ArrayMap()
    init {
        priority = 2
        for (i in 1..12) {
            tilemap.put(i, TextureAtlas(R.matlas0).findRegion("tile", i))
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (CMap2D[entity].map2D != null) {
            val scaleViewBounds = Rectangle(camera.position.x,camera.position.y,0f,0f)
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
                    size.scale = 2f
                    zIndex = Int.MIN_VALUE + it.heightType + pos.y.toInt()

                }
                create<CTextureRegion> {
                    texture = tilemap.get(it.userData as Int)
                    /*offsetX = -16
                    offsetY = -16*/

                }

                if (!it.collidable) {
                    /*create<CJBumpAABB> {
                        item = Item(this@components)
                    }*/
                    create<CDefaultPhysics2d> {
                    }

                }
                create<CCell> { cell = it; cell?.isInEngine = true }
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
        viewport.setWorldSize(map.getWidthMapInPixels(), map.getHeightMapInPixels())
        viewport.camera.update()
    }
}

class CMap2D : PoolableComponent {
    var map2D: Map2D? = null
    var currentLayer = -1
    override fun reset() {
        map2D = null
        currentLayer = -1
    }

    companion object : ComponentResolver<CMap2D>(CMap2D::class.java)
}
