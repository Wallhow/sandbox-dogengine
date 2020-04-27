package dogengine.ecs.systems.tilemap

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ArrayMap
import com.google.inject.Inject
import dogengine.ecs.components.components
import dogengine.ecs.components.create
import dogengine.ecs.components.createEntity
import dogengine.ecs.components.draw.CTextureRegion
import dogengine.ecs.components.utility.CDeleteMe
import dogengine.ecs.components.utility.logic.CDefaultPhysics2d
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CVisibleEntityListener
import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent
import dogengine.ecs.systems.SystemPriority
import dogengine.map2D.CCell
import dogengine.map2D.Cell
import dogengine.map2D.Map2D
import dogengine.utils.GameCamera
import dogengine.utils.Size
import dogengine.utils.isElse
import dogengine.utils.isTrue

//TODO ТУТ ВСЁ ЗАХАРДКОДЕНО, ЭТО НЕ ДОЛЖНО БЫТЬ ТАК, ПЕРЕСМОТРЕТЬ ДАННЫЙ КЛАСС В БУДУЩЕМ
class SMap2D @Inject constructor(private val gameCamera: GameCamera) : IteratingSystem(Family.all(CMap2D::class.java).get()) {
    private var firstRun = true
    val tileSize = Size()
    private val tileset: ArrayMap<Int, TextureAtlas.AtlasRegion> = ArrayMap()
    var setTileset : ((ArrayMap<Int,TextureAtlas.AtlasRegion>) -> Unit)? = null
    private var isSetTileset = true
    private val camera = gameCamera.getCamera()
    init {
        priority = SystemPriority.UPDATE+100
    }

    override fun update(deltaTime: Float) {
        if(setTileset!=null && isSetTileset) {
            setTileset?.invoke(tileset)
            isSetTileset = false
        }
        super.update(deltaTime)
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
                    texture = tileset.get(it.userData as Int)
                }

                if (!it.collidable) {
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
