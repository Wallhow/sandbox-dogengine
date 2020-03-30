package sandbox.sandbox.def.def.particles

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool

class Particle : Pool.Poolable {
    var x: Float = 0f
    var y: Float = 0f
    var rotation: Float = 0f
    var currentAge: Float = 0f
    var maxAge: Float = 0f
    var speed: Float = 0f
    var direction: Vector2 = Vector2()
    override fun reset() {
        x = 0f
        y = 0f
        rotation = 0f
        currentAge = 0f
        maxAge = 0f
        speed = 0f
        direction = Vector2()
    }
}