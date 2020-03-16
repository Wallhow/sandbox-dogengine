package dogengine.redkin.physicsengine2d.utils

import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import dogengine.redkin.physicsengine2d.bodies.LineBody
import dogengine.redkin.physicsengine2d.bodies.PolylineBody
import dogengine.redkin.physicsengine2d.sensors.Sensor
import java.util.*

class Functions {
    private var tmpPolygon: Polygon? = null
    private var polygonPool: Pool<Polygon?> = object : Pool<Polygon?>() {
        override fun newObject(): Polygon {
            return Polygon()
        }
    }

    // Devuelve el punto de colision entre dos lineas
    fun collisionLine(line: LineBody, polylines: ArrayList<PolylineBody>): Int {
        var puntos: ArrayList<Vector2>
        var intersecciones = 0
        val intersection = Vector2(0f, 0f)
        for (polylineBody in polylines) {
            puntos = convertPoints(polylineBody.transformedVertices)
            var p1: Vector2
            var p2: Vector2
            for (x in 0 until puntos.size - 1) {
                p1 = puntos[x]
                p2 = puntos[x + 1]
                if (Intersector.intersectSegments(line.getP1(), line.getP2(), p1, p2, intersection)) {
                    intersecciones++
                }
            }
        }
        return intersecciones
    }

    // Devuelve los cuerpos con los que colisiona el rect�ngulo
    fun collisionRectLine(rectangle: Rectangle, polylines: ArrayList<PolylineBody>): ArrayList<LineBody> {
        var puntos: ArrayList<Vector2>
        tmpPolygon = polygonPool.obtain()
        tmpPolygon!!.vertices = rectGetVertices(rectangle)
        val lineBodies = ArrayList<LineBody>()
        for (polylineBody in polylines) {
            puntos = convertPoints(polylineBody.transformedVertices)
            var p1 = Vector2()
            var p2 = Vector2()
            //
            for (x in 0 until puntos.size - 1) {
                p1 = puntos[x]
                p2 = puntos[x + 1]
                if (Intersector.intersectSegmentPolygon(p1, p2, tmpPolygon)) {
                    lineBodies.add(LineBody(p1.x, p1.y, p2.x, p2.y, polylineBody.linetype, polylineBody.name))
                }
            }
            polygonPool.free(tmpPolygon)
        }
        return lineBodies
    }

    fun intersect2lines(p1: Vector2?, p2: Vector2?, p3: Vector2?, p4: Vector2?): Vector2? {
        val intersection = Vector2()
        return if (Intersector.intersectSegments(p1, p2, p3, p4, intersection)) {
            intersection
        } else {
            null
        }
    }

    fun setChecker(rectangle: Rectangle, line: LineBody, direccion: String) {
        when (direccion) {
            "up" -> {
                line.x1 = rectangle.x + rectangle.width / 2
                line.y1 = rectangle.y + rectangle.height / 2
                line.x2 = rectangle.x + rectangle.width / 2
                line.y2 = rectangle.y + rectangle.height + 15
            }
            "down" -> {
                line.x1 = rectangle.x + rectangle.width / 2
                line.y1 = rectangle.y + rectangle.height / 2
                line.x2 = rectangle.x + rectangle.width / 2
                line.y2 = rectangle.y - 15
            }
            "leftDown" -> {
                line.x1 = rectangle.x
                line.y1 = rectangle.y + rectangle.height / 2
                line.x2 = rectangle.x
                line.y2 = rectangle.y - 2
            }
            "leftUp" -> {
                line.x1 = rectangle.x
                line.y1 = rectangle.y + rectangle.height / 2
                line.x2 = rectangle.x
                line.y2 = rectangle.y + rectangle.height + 2
            }
            "rightDown" -> {
                line.x1 = rectangle.x + rectangle.width
                line.y1 = rectangle.y + rectangle.height / 2
                line.x2 = rectangle.x + rectangle.width
                line.y2 = rectangle.y - 2
            }
            "rightUp" -> {
                line.x1 = rectangle.x + rectangle.width
                line.y1 = rectangle.y + rectangle.height / 2
                line.x2 = rectangle.x + rectangle.width
                line.y2 = rectangle.y + rectangle.height + 2
            }
        }
    }

    fun getMax(a: Float, b: Float): Float {
        return if (a > b) {
            a
        } else {
            b
        }
    }

    fun getMin(a: Float, b: Float): Float {
        return if (a < b) {
            a
        } else {
            b
        }
    }

    fun convertPoints(vertices: FloatArray): ArrayList<Vector2> {
        val vectores = ArrayList<Vector2>()
        var i = 0
        while (i < vertices.size) {
            vectores.add(Vector2(vertices[i], vertices[i + 1]))
            i = i + 2
        }
        return vectores
    }

    fun updateBullet(bulletLine: LineBody, sensor: Sensor) {
        bulletLine.x1 = sensor.lastX + sensor.width / 2
        bulletLine.y1 = sensor.lastY + sensor.height / 2
        bulletLine.x2 = sensor.x + sensor.width / 2
        bulletLine.y2 = sensor.y + sensor.height / 2
    }

    companion object {
        // Devuelve los vértices de un cuadrado
        @JvmStatic
        fun rectGetVertices(rectangle: Rectangle): FloatArray {
            val vertices = ArrayList<Float>()
            val x1 = rectangle.x
            val y1 = rectangle.y
            val x2 = rectangle.x + rectangle.width
            val y2 = rectangle.y
            val x3 = rectangle.x + rectangle.width
            val y3 = rectangle.y + rectangle.height
            val x4 = rectangle.x
            val y4 = rectangle.y + rectangle.height
            vertices.add(x1)
            vertices.add(y1)
            vertices.add(x2)
            vertices.add(y2)
            vertices.add(x3)
            vertices.add(y3)
            vertices.add(x4)
            vertices.add(y4)
            val floatArray = FloatArray(vertices.size)
            for ((i, f) in vertices.withIndex()) {
                floatArray[i] = f
            }
            return floatArray
        }
    }

}