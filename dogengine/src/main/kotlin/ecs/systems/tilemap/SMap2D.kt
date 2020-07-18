package dogengine.ecs.systems.tilemap

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.google.inject.Inject
import dogengine.drawcore.CDrawable
import dogengine.ecs.components.addEntityAddedListener
import dogengine.ecs.components.components
import dogengine.ecs.components.create
import dogengine.ecs.components.draw.CTextureRegion
import dogengine.ecs.components.utility.CDeleteMe
import dogengine.ecs.components.utility.logic.CDefaultPhysics2d
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CVisibleEntityListener
import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent
import dogengine.ecs.systems.SystemPriority
import dogengine.map2D.Cell
import dogengine.map2D.Map2D
import dogengine.map2D.comp.CCell
import dogengine.utils.GameCamera
import dogengine.utils.extension.addEntity
import map2D.TypeData

class SMap2D @Inject constructor(private val gameCamera: GameCamera) :
        IteratingSystem(Family.all(CMap2D::class.java).get()) {
    val tilesets = Tilesets()
    private val camera = gameCamera.getCamera()

    companion object {
        lateinit var map2D: Map2D
    }

    init {
        priority = SystemPriority.UPDATE + 100
    }

    override fun addedToEngine(engine: Engine) {
        engine.addEntityAddedListener(Family.all(CMap2D::class.java).get()) {
            map2D = (CMap2D[it].map2D!!)
            updateViewport(map2D)
        }
        super.addedToEngine(engine)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        CMap2D[entity].map2D?.let { map ->
            val scaleViewBounds = Rectangle(camera.position.x, camera.position.y,
                    gameCamera.getViewport().worldWidth / 2, gameCamera.getViewport().worldHeight / 2)

            map.getLayers().forEach { layer ->
                layer.getCellsInViewBounds(scaleViewBounds).forEach { cell ->
                    initCreate(cell)
                }
            }
        }
    }

    private fun initCreate(it: Cell) {
        if (!it.isInEngine) {
            createDrawableEntity(it) ?: return
            it.isInEngine = true
        }
    }


    private fun createDrawableEntity(it: Cell): Entity? {
        if ((it.data[TypeData.TypeCell] is String)) return null
        val s = tilesets.tileSize.cpy()
        val pos = Vector2(it.x * s.width, it.y * s.height)
        return engine.addEntity {
            components {
                create<CTransforms> {
                    position = pos
                    size = s
                    size.scale = 2f
                    zIndex = Int.MIN_VALUE + it.heightType + pos.y.toInt()

                }
                create<CTextureRegion> {
                    texture = tilesets.getTile(it.data[TypeData.TypeCell] as Int)
                }
                create<CDrawable> {
                    this.drawType = 1
                }

                if (!it.collidable) {
                    create<CDefaultPhysics2d> {
                    }

                }
                create<CCell> { cell = it; cell?.isInEngine = true }
                create<CVisibleEntityListener> {
                    hide = { e ->
                        CCell[e].cell?.isInEngine = false
                        create<CDeleteMe> { }
                    }
                    visible = { e ->
                        CCell[e].cell?.isInEngine = true
                    }
                }
            }
        }
    }

    private fun updateViewport(map: Map2D) {
        gameCamera.setWorldSize(map.getWidthMapInPixels(), map.getHeightMapInPixels())
        camera.update()
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
