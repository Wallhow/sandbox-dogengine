package ecs.systems.utility

import com.badlogic.gdx.ai.utils.Location
import com.badlogic.gdx.math.Vector2
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class LocationAdapter(private val position: Vector2) : Location<Vector2> {
    override fun getPosition(): Vector2 {
        return position
    }

    override fun getOrientation(): Float = 0f

    override fun setOrientation(orientation: Float) {}

    override fun vectorToAngle(vector: Vector2): Float {
        return atan2(-vector.x.toDouble(), vector.y.toDouble()).toFloat()
    }

    override fun angleToVector(outVector: Vector2, angle: Float): Vector2 {
        outVector.x = (-sin(angle.toDouble())).toFloat()
        outVector.y = cos(angle.toDouble()).toFloat()
        return outVector
    }

    override fun newLocation(): Location<Vector2> {
        return LocationAdapter(position.cpy())
    }
}