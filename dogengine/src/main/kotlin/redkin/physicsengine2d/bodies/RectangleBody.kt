package dogengine.redkin.physicsengine2d.bodies

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import dogengine.redkin.physicsengine2d.sensors.Sensor
import dogengine.redkin.physicsengine2d.variables.Types
import java.util.*

class RectangleBody(x: Float, y: Float, width: Float, height: Float, var type: Types.TYPE, var name: String) : Rectangle(x, y, width, height) {
    @JvmField
    var gravityScale = 1f
    @JvmField
    var velocity: Vector2 = Vector2(0f, 0f)
    @JvmField
    var clampY = true
    var MAX_VELOCITY_X = 200f
    @JvmField
    var MAX_VELOCITY_Y = 200f
    @JvmField
    var JUMP_VELOCITY = 350f
    @JvmField
    var sensors: ArrayList<Sensor> = ArrayList()
    var isBullet = false
    var collisionType = 0
    var collideWith: IntArray = IntArray(100)
    @JvmField
    var translateX = 0f
    @JvmField
    var translateY = 0f
    var userData: Any? = null

    fun addSensor(sensor: Sensor) {
        sensor.x = x + width / 2 - sensor.width / 2 + sensor.xRelative
        sensor.y = y + height / 2 - sensor.height / 2 + sensor.yRelative
        sensor.rect = this
        sensors.add(sensor)
    }

    companion object {
        private const val serialVersionUID = 1L
    }

}