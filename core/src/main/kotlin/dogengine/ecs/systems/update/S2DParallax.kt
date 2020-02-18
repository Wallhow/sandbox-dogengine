package dogengine.ecs.systems.update

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.draw.CTextureRegion
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.def.GameEntity
import dogengine.ecs.systems.SystemPriority

class S2DParallax : IteratingSystem(Family.all(CTextureRegion::class.java).get()) {
    private var gameObject: GameEntity? = null
    private var prePosition: Vector2? = null
    private var dirty : Boolean = false
    private var paddingX = 0f
    init {
        priority = SystemPriority.UPDATE+99
    }
    override fun processEntity(entity: Entity, deltaTime: Float) {

        if(prePosition!=null) {
            entity as GameEntity
            if(entity.parallaxLayer!=0) {
                if (MathUtils.round(paddingX)>0 || MathUtils.round(paddingX)<0) {
                    CTransforms[entity].position.x += paddingX * 80f / ((entity.parallaxLayer) )*deltaTime
                }
            }

        }

    }

    fun setTrackingObject(gameEntity: GameEntity) {
               gameObject = gameEntity
        dirty = true
    }
    fun getTrackingObject() : GameEntity? = gameObject

    override fun update(deltaTime: Float) {
        if(dirty) {
            prePosition = CTransforms[gameObject!!].position.cpy()
            dirty = false
        }
        if(prePosition!=null) {
            paddingX = (prePosition!!.x.toInt() - CTransforms[gameObject!!].position.x.toInt()).toFloat()
            super.update(deltaTime)
            prePosition!!.set(CTransforms[gameObject!!].position.x,
                    CTransforms[gameObject!!].position.y)
        }
    }
}