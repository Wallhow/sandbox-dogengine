package dogengine.drawcore

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.viewport.Viewport
import com.google.inject.Inject
import dogengine.ecs.components.create
import dogengine.ecs.components.utility.CDeleteMe
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CHide
import dogengine.ecs.systems.SystemPriority
import dogengine.ecs.systems.draw.DrawComparator
import dogengine.map2D.comp.CCell
import dogengine.shadow2d.systems.SShadow2D
import dogengine.utils.GameCamera

class SDraw2D @Inject constructor(private val batch: SpriteBatch, private val gameCamera: GameCamera, val view: Viewport) : SortedIteratingSystem(Family.all(CDrawable::class.java, CTransforms::class.java)
        .exclude(CHide::class.java).get(), DrawComparator.comparator) {
    val drawFunctions = ArrayMap<Int, IDrawFunc>()
    private val sortedEntities: Array<Entity> = Array()
    private val drawBatched: Array<Array<Entity>> = Array()
    val camera = gameCamera.getCamera()

    init {
        priority = SystemPriority.DRAW + 1
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {

    }

    override fun update(deltaTime: Float) {
        forceSort()
        super.update(deltaTime)
        drawBatched.clear()

        val arr = Array<Entity>()
        val arrMap2D = Array<Entity>()
        entities.forEach { e ->
            if (CCell[e] != null) {
                arrMap2D.add(e)
            } else {
                if (CDrawable[e].batchable) {
                    arr.add(e)
                } else {
                    drawBatched.add(Array(arr))
                    arr.clear()
                    arr.add(e)
                }

            }
        }
        drawBatched.add(arr)

        drawMap2D(arrMap2D)

        drawBatched.forEach { arr_ ->
            batch.projectionMatrix = camera.combined
            batch.begin()
            arr_.forEach { e ->
                drawFunctions[CDrawable[e].drawType]?.draw(batch, e)
                if(CDrawable[e].entityDeleteAfterDraw)
                    e.create<CDeleteMe>()
            }
            batch.end()
        }

        val smap = SShadow2D.shadowMap

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
        batch.begin()
        batch.enableBlending()
        batch.color.set(1f, 1f, 1f, 0.6f)
        batch.draw(smap.texture,smap.position.x,smap.position.y
                ,smap.size.width, smap.size.height)
        batch.end()
        batch.color= Color.WHITE

    }

    private fun drawMap2D(arrMap2D: Array<Entity>) {
        batch.projectionMatrix = camera.combined
        batch.begin()
        arrMap2D.forEach { e ->
            drawFunctions[CDrawable[e].drawType]?.draw(batch, e)

        }
        batch.end()
    }
}