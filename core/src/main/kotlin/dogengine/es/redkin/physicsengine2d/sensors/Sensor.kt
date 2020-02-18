package dogengine.es.redkin.physicsengine2d.sensors

import com.badlogic.gdx.math.Rectangle
import dogengine.es.redkin.physicsengine2d.bodies.LineBody
import dogengine.es.redkin.physicsengine2d.bodies.RectangleBody
import dogengine.es.redkin.physicsengine2d.utils.Functions
import java.util.*

class Sensor(var name: String, var xRelative: Float, var yRelative: Float, width: Float, height: Float) : Rectangle() {
    var functions: Functions? = null
    var userData: Any? = null
    @JvmField
    var rectangleBodies: ArrayList<RectangleBody>
    @JvmField
    var sensors: ArrayList<Sensor>
    @JvmField
    var lineBodies: ArrayList<LineBody>
    @JvmField
    var lineBodiesToRemove: ArrayList<LineBody>
    @JvmField
    var lastX = 0f
    @JvmField
    var lastY = 0f
    @JvmField
    var isBullet = false
    var rect: RectangleBody? = null

    fun containsLine(lineBody: LineBody): Boolean {
        var istrue = false
        for (lineBody2 in lineBodies) {
            if (lineBody2.x1 == lineBody.x1 && lineBody2.x2 == lineBody.x2 && lineBody2.y1 == lineBody.y1 && lineBody2.y2 == lineBody.y2
                    || lineBody2.x1 == lineBody.x2 && lineBody2.x2 == lineBody.x1 && lineBody2.y1 == lineBody.y2 && lineBody2.y2 == lineBody.y1) {
                istrue = true
            }
        }
        return istrue
    }

    init {
        this.width = width
        this.height = height
        rectangleBodies = ArrayList()
        lineBodies = ArrayList()
        lineBodiesToRemove = ArrayList()
        sensors = ArrayList()
    }
}