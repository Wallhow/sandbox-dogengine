package dogengine.ecs.systems.update

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.google.inject.Inject
import dogengine.ecs.components.create
import dogengine.ecs.components.draw.CAtlasRegion
import dogengine.ecs.components.draw.CDraw
import dogengine.ecs.components.draw.CTextureRegion
import dogengine.ecs.components.utility.CDeleteComponent
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CHide
import dogengine.ecs.components.utility.visible.CVisibleEntityListener
import dogengine.ecs.systems.SystemPriority
import dogengine.utils.vec2
import kotlin.math.abs

class SVisibleEntity @Inject constructor(val camera: OrthographicCamera) : IteratingSystem(Family.all(CTransforms::class.java).one(
        CDraw::class.java, CAtlasRegion::class.java, CTextureRegion::class.java, CVisibleEntityListener::class.java
).get()) {
    private var distance: Float = (camera.viewportWidth) * 1.2f
    private var dirty = true
    init {
        priority = SystemPriority.UPDATE+98
    }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (dirty) {
            if(camera.zoom<=1)
                distance*=camera.zoom
            dirty = false
        }
        val tr = CTransforms[entity]
        val entityPos = vec2(tr.getCenterX(), tr.getCenterY())
        //Проверяем, если растояние между GO  и центром видимости меньше максимального
        //размера области видимости, то удаляем у объекта компонент CHide
        if (getDistance(entityPos) <= distance) {
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

    private fun getDistance(vector2: Vector2): Float {
        return abs(vector2.dst(camera.position.x,camera.position.y))
    } //rect.getCenter(Vector2()).dst(vector2)
}