package dogengine.ecs.systems.draw

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.viewport.Viewport
import com.google.inject.Inject
import dogengine.drawcore.CDrawable
import dogengine.drawcore.DrawTypes
import dogengine.ecs.components.create
import dogengine.ecs.components.draw.CDraw
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CHide
import dogengine.ecs.systems.SystemPriority


class SDrawable @Inject constructor(private val batch: SpriteBatch, val camera: OrthographicCamera, val view: Viewport) : SortedIteratingSystem(Family.all(CDraw::class.java, CTransforms::class.java)
        .exclude(CHide::class.java).get(), DrawComparator.comparator) {
    init {
        priority = SystemPriority.DRAW
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if(CDrawable[entity]==null) {
            entity.create<CDrawable> {
                this.drawType = DrawTypes.BATCH
            }
        }
    }



   /* override fun processEntity(entity: Entity, deltaTime: Float) {
        val drawable = CDraw[entity]
        if(drawable.texture == null) return
        val tr = CTransforms[entity]
        val width : Float = tr.size.width
        val height : Float = tr.size.height
        batch.color = drawable.tint

        batch.draw(drawable.texture, tr.position.x+drawable.offsetX, tr.position.y+drawable.offsetY,
                (tr.getCenterX() - tr.position.x), (tr.getCenterY() - tr.position.y),
                width, height,
                tr.size.scaleX, tr.size.scaleY,
                tr.angle)
        batch.color = Color.WHITE

        *//*entity.create<CDeleteComponent> {
            componentRemove = drawable
        }*//*
        if (drawable.isDeleteAfterDraw) {
            entity.create<CDeleteMe> {  }
        }
    }

    override fun update(deltaTime: Float) {
        forceSort()

        Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.projectionMatrix = camera.combined

        batch.begin()
        super.update(deltaTime)
        //drawLayer(1)
        batch.end()

    }*/



    override fun entityAdded(entity: Entity) {
        val tr = CTransforms[entity]
        if(CDraw[entity].texture != null) {
            val tex = CDraw[entity].texture as TextureRegion
            if (tr.size.width == -1f || tr.size.height == -1f) {
                tr.size.setNewWidth(tex.regionWidth.toFloat())
                tr.size.setNewHeight(tex.regionHeight.toFloat())
            }
        }
        super.entityAdded(entity)
    }
}




