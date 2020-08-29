package dogengine.utils.extension

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

fun Vector2.clamp(minX: Float,maxX:Float,minY:Float,maxY:Float) : Vector2 {
    val x = MathUtils.clamp(this.x,minX,maxX)
    val y = MathUtils.clamp(this.y,minY,maxY)
    this.set(x,y)
    return this
}