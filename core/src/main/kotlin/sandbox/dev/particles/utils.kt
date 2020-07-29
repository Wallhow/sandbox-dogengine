package sandbox.sandbox.def.particles

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import sandbox.sandbox.def.def.particles.Emitter


fun colorFromRGB(r: Int,g:Int,b:Int, a: Int = 255) : Color {
    return Color().set(r/255f,g/255f,b/255f,a/255f)
}

fun Emitter.Configuration.randomLife(): Float {
    return MathUtils.random(pLifeTimeMin, pLifeTimeMax)
}

fun Emitter.Configuration.randomSpeed(): Float {
    return MathUtils.random(pSpeedMin, pSpeedMax)
}

fun Emitter.Configuration.randomCount(): Int {
    return MathUtils.random(pCountMin, pCountMax)
}

fun Emitter.Configuration.randomRotation(): Float {
    return MathUtils.random(pRotationMin, pRotationMax)
}

fun Emitter.Configuration.randomDirection(): Vector2 {
    return Vector2(MathUtils.random(pDirectionMin.x, pDirectionMax.x),
            MathUtils.random(pDirectionMin.y, pDirectionMax.y))
}
fun Emitter.Configuration.randomOffsetPositionX() : Float {
    return if(pOffsetMinX == 0f && pOffsetMaxX == 0f) {
        0f
    } else
        MathUtils.random(pOffsetMinX, pOffsetMaxX)
}
fun Emitter.Configuration.randomOffsetPositionY() : Float {
    return if(pOffsetMinY == 0f && pOffsetMaxY == 0f) {
        0f
    } else
        MathUtils.random(pOffsetMinY, pOffsetMaxY)
}
fun Emitter.Configuration.randomLazyBirth() : Float  {
    return if(pLazyBirthMax == 0f) pLazyBirthMin
    else
        MathUtils.random(pLazyBirthMin, pLazyBirthMax)
}