package sandbox.sandbox.def.def.particles

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool

class Particle : Pool.Poolable {
    var r: Float = 0.5f
    var g: Float = 0.5f
    var b: Float = 0.5f
    var x: Float = 0f
    var y: Float = 0f
    var speedRotation: Float = 0f
    var angle: Float =0f
    var currentAge: Float = 0f
    var maxAge: Float = 0f
    var speed: Float = 0f
    var direction: Vector2 = Vector2()
    var size: Float = 5f
    var alpha: Float = 1f

    fun setColor(color: Color) {
        r = color.r
        g = color.g
        b = color.b
    }
    override fun reset() {
        x = 0f
        y = 0f
        speedRotation = 0f
        currentAge = 0f
        maxAge = 0f
        speed = 0f
        direction = Vector2()
        angle = 0f
        size = 10f
        alpha = 1f
        r = 1f
        g = 1f
        b = 1f
    }
}