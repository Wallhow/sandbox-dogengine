package dogengine.ecs.systems.actions

import aurelienribon.tweenengine.Tween
import aurelienribon.tweenengine.TweenManager
import com.badlogic.ashley.core.EntitySystem
import dogengine.actions.CTextureRegionAccessor
import dogengine.actions.CTransformsAccessor
import dogengine.ecs.components.draw.CTextureRegion
import dogengine.ecs.components.utility.logic.CTransforms

//TODO в процессе разработки
class STweenEngine : EntitySystem(1) {
    val tweenManager: TweenManager
    init {
        Tween.registerAccessor(CTransforms::class.java,CTransformsAccessor())
        Tween.registerAccessor(CTextureRegion::class.java,CTextureRegionAccessor())
        tweenManager = TweenManager()

    }
    override fun update(deltaTime: Float) {
        tweenManager.update(deltaTime)
        super.update(deltaTime)

    }
}