package sandbox.dogengine.ecs.systems.update

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.GdxRuntimeException
import com.google.inject.Inject
import dogengine.Kernel
import dogengine.ecs.components.create
import dogengine.ecs.components.draw.CAtlasRegion
import dogengine.ecs.components.draw.CDrawable
import dogengine.ecs.components.draw.CTextureRegion
import dogengine.ecs.components.utility.CDeleteComponent
import dogengine.utils.scaleViewBoundsRect
import dogengine.utils.vec2
import sandbox.dogengine.ecs.components.utility.logic.CTransforms
import sandbox.dogengine.ecs.components.utility.visible.CHide
import sandbox.dogengine.ecs.components.utility.visible.CVisibleEntityListener

class SVisibleEntity @Inject constructor(val camera: OrthographicCamera) : IteratingSystem(Family.all(CTransforms::class.java).one(
        CDrawable::class.java, CAtlasRegion::class.java, CTextureRegion::class.java, CVisibleEntityListener::class.java
).get()) {
    private var distance: Float
    private var dirty = true
    init {
        distance = (camera.viewportWidth + camera.viewportHeight) / 2 /*+ ((camera.viewportWidth + camera.viewportHeight) / 2) * 0.3f*/
    }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (dirty) {
            if(camera.zoom<=1) {
                distance*=camera.zoom
            }
            dirty = false
        }
        val tr = CTransforms[entity]
        val entityPos = vec2(tr.position.x+tr.size.halfWidth, tr.position.y+tr.size.halfHeight)
        //Проверяем, если растояние между GO  и центром видимости меньше максимального
        //размера области видимости, то удаляем у объекта компонент CHide
        if (getDistance(entityPos,getRect(camera)) <= distance) {
            if (CHide[entity] != null) { // если объект был скрыт
                CVisibleEntityListener[entity]?.visible?.invoke(entity)
                entity.create<CDeleteComponent> {
                    componentRemove = CHide[entity]
                }
            }
        } else {
            CVisibleEntityListener[entity]?.hide?.invoke(entity)
            entity.create<CHide> {}
        }
    }
    private fun getRect(camera: OrthographicCamera) : Rectangle {
        return camera.scaleViewBoundsRect(Kernel.Systems.SVisibleEntity.scale)
    }
    fun getDistance(vector2: Vector2,rect: Rectangle): Float = rect.getCenter(Vector2()).dst(vector2)


    private class ViewBounds(private val camera: OrthographicCamera) {
        private val sViewBoundsRect: Rectangle
            get() = camera.scaleViewBoundsRect(Kernel.Systems.SVisibleEntity.scale)
        private val sViewBoundsCenter = Vector2()
        private var viewBounds: Float = 0f
        fun init() {
            viewBounds = (camera.viewportWidth + camera.viewportHeight) / 2 + ((camera.viewportWidth + camera.viewportHeight) / 2) * 0.3f
        }

        fun getSizeViewBounds(): Float {
            if (viewBounds == 0f) throw GdxRuntimeException("view Bounds = 0, not called function init(), " +
                    "before call function getSize Views Bounds() you needs to call fun init()")
            else {
                return viewBounds
            }
        }

        fun isEntityVisible(vector2: Vector2): Boolean {
            return getDistance(vector2) <= getSizeViewBounds()
        }

        fun getRect(): Rectangle = sViewBoundsRect
        fun getDistance(vector2: Vector2): Float = sViewBoundsRect.getCenter(sViewBoundsCenter).dst(vector2)
        fun getDistance(x: Float, y: Float): Float = sViewBoundsRect.getCenter(sViewBoundsCenter).dst(x, y)
        fun overlaps(rect: Rectangle): Boolean = sViewBoundsRect.overlaps(rect)
    }
}