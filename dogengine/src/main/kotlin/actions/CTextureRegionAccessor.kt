package dogengine.actions

import aurelienribon.tweenengine.Tween
import aurelienribon.tweenengine.TweenAccessor
import dogengine.ecs.components.draw.CTextureRegion

class CTextureRegionAccessor : TweenAccessor<CTextureRegion> {
    override fun setValues(target: CTextureRegion, tweenType: Int, newValues: FloatArray) {
        when (tweenType) {
            Type.ALPHA -> {
                target.color.a = newValues[0]
            }
            Type.COLOR -> {
                target.color.r = newValues[0]
                target.color.g = newValues[1]
                target.color.b = newValues[2]
            }
        }
    }
    override fun getValues(target: CTextureRegion, tweenType: Int, returnValues: FloatArray): Int {
        when (tweenType) {
            Type.ALPHA -> {
                returnValues[0] = target.color.a
                return 1
            }
            Type.COLOR -> {
                returnValues[0] = target.color.r
                returnValues[1] = target.color.g
                returnValues[2] = target.color.b
                return 3
            }
        }
        return -1
    }

    object Type {
        const val ALPHA = 0
        const val COLOR = 1
    }
}

fun CTextureRegion.colorTo(r:Float, g:Float, b:Float, duration:Float) : Tween {
    return Tween.to(this,CTextureRegionAccessor.Type.COLOR,duration).target(r,g,b)
}
fun CTextureRegion.alphaTo(alpha:Float, duration: Float) : Tween {
    return Tween.to(this, CTextureRegionAccessor.Type.ALPHA,duration).target(alpha)
}