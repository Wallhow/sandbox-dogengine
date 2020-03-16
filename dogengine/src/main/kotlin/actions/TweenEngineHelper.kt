package dogengine.actions

import aurelienribon.tweenengine.Tween
import aurelienribon.tweenengine.TweenManager
import com.badlogic.ashley.core.Engine
import dogengine.Kernel
import dogengine.ecs.components.draw.CTextureRegion
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.def.GameEntity
import dogengine.ecs.systems.actions.STweenEngine
import dogengine.utils.gdxSchedule


fun GameEntity.scaleTo(to: Float, time:Float) {

}

fun Tween.start(engine: Engine) {
    val tweenManager = engine.getSystem(STweenEngine::class.java).tweenManager
    start(tweenManager)
}

var tweenManager: TweenManager? = null
fun Tween.start(delay: Float) {
    if(tweenManager==null) {
        tweenManager = Kernel.getInjector().getInstance(Engine::class.java).getSystem(STweenEngine::class.java).tweenManager
    }
    if(delay==0f) {
        start(tweenManager)
    } else
        gdxSchedule(delay) {start(tweenManager)}
}
fun CTransforms.stopScale() {
    val tweenManager = Kernel.getInjector().getInstance(Engine::class.java).getSystem(STweenEngine::class.java).tweenManager

    tweenManager.killTarget(this,CTransformsAccessor.Type.SCALE)
}

fun CTextureRegion.stopAlpha() {
    val tweenManager = Kernel.getInjector().getInstance(Engine::class.java).getSystem(STweenEngine::class.java).tweenManager

    tweenManager.killTarget(this,CTextureRegionAccessor.Type.ALPHA)
}