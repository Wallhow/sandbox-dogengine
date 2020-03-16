package sandbox.dogengine.ecs.systems.update

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.google.inject.Inject
import dogengine.Kernel
import dogengine.ecs.components.create
import dogengine.ecs.components.draw.CAtlasRegion
import dogengine.ecs.components.draw.CDrawable
import dogengine.ecs.components.draw.CTextureRegion
import dogengine.ecs.components.utility.CDeleteComponent
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CHide
import dogengine.ecs.components.utility.visible.CVisibleEntityListener
import dogengine.ecs.systems.SystemPriority
import dogengine.utils.scaleViewBoundsRect
import dogengine.utils.vec2

class SVisibleEntity @Inject constructor(val camera: OrthographicCamera) : IteratingSystem(Family.all(CTransforms::class.java).one(
        CDrawable::class.java, CAtlasRegion::class.java, CTextureRegion::class.java, CVisibleEntityListener::class.java
).get()) {
    private var distance: Float = (camera.viewportWidth + camera.viewportHeight) / 2
    private var dirty = true
    init {
        priority = SystemPriority.UPDATE+98
        /*+ ((camera.viewportWidth + camera.viewportHeight) / 2) * 0.3f*/
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
    private fun getDistance(vector2: Vector2, rect: Rectangle): Float = rect.getCenter(Vector2()).dst(vector2)
}