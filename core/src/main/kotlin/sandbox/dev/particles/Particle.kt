package sandbox.dev.particles

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import sandbox.sandbox.def.particles.Material

class Particle : Pool.Poolable {
    var material: Material = Material()
    var x: Float = 0f
    var y: Float = 0f
    var speedRotation: Float = 0f
    var angle: Float =0f
    var currentAge: Float = 0f
    var maxAge: Float = 0f
    var speed: Float = 0f
    var direction: Vector2 = Vector2()
    var size: Float = 5f
    var timer : Float = 0f
    var lazyBirth = 0f

    fun setColor(color: Color) {
        material.color.set(color)
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
        material.reset()
        timer = 0f
    }
}