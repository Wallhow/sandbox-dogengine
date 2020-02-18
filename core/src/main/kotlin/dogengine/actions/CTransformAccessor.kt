package dogengine.actions

import aurelienribon.tweenengine.Tween
import aurelienribon.tweenengine.TweenAccessor
import dogengine.ecs.components.utility.logic.CTransforms

class CTransformsAccessor : TweenAccessor<CTransforms> {
    override fun setValues(target: CTransforms, tweenType: Int, newValues: FloatArray) {
        when (tweenType) {
            Type.SCALE -> {
                target.size.scaleX = newValues[0]
                target.size.scaleY = newValues[0]
            }
            Type.POSITION -> {
                target.position.set(newValues[0],newValues[1])
            }
            Type.ROTATION -> {
                target.angle = newValues[0]
            }
        }
    }

    override fun getValues(target: CTransforms, tweenType: Int, returnValues: FloatArray): Int {
        when (tweenType) {
            Type.SCALE -> {
                returnValues[0] = target.size.scale
                return 1
            }
            Type.POSITION -> {
                returnValues[0] = target.position.x
                returnValues[1] = target.position.y
                return 2
            }
            Type.ROTATION -> {
                returnValues[0] = target.angle
                return 1
            }
        }
        return -1
    }

    object Type {
        const val SCALE = 0
        const val POSITION = 1
        const val ROTATION = 2
    }
}

fun CTransforms.scaleTo(to: Float, duration: Float) : Tween {
    return Tween.to(this,CTransformsAccessor.Type.SCALE,duration).target(to)
}
fun CTransforms.rotateTo(to: Float, duration: Float) : Tween {
    return Tween.to(this,CTransformsAccessor.Type.ROTATION,duration).target(to)
}
