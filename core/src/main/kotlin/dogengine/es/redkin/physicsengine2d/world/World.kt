package dogengine.es.redkin.physicsengine2d.world

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.*
import com.badlogic.gdx.utils.Pool
import dogengine.es.redkin.physicsengine2d.bodies.LineBody
import dogengine.es.redkin.physicsengine2d.bodies.PolylineBody
import dogengine.es.redkin.physicsengine2d.bodies.RectangleBody
import dogengine.es.redkin.physicsengine2d.contactListener.ContactListener
import dogengine.es.redkin.physicsengine2d.utils.Functions
import dogengine.es.redkin.physicsengine2d.utils.Functions.Companion.rectGetVertices
import dogengine.es.redkin.physicsengine2d.variables.Types
import dogengine.es.redkin.physicsengine2d.variables.Types.LINETYPE
import java.util.*

class World(gravity: Float, val PPU: Float = 1f) {
    var drawSensors = true
    private var collideWithDynamicRectangles = false
    var showDebug = false
    var drawBulletLine = false
    private var bulletLineExist = false
    private var delta = 0f
    private var polylines: ArrayList<PolylineBody>
    private var rectangles: ArrayList<RectangleBody>
    private var gravity = 0f
    private var max = 0f
    private var min = 0f
    private var rectPool: Pool<Rectangle?>
    private var tmpRectangleBot: Rectangle? = null
    private var tmpRectangleTop: Rectangle? = null
    private var tmpRectangleLeft: Rectangle? = null
    private var tmpRectangleRight: Rectangle? = null
    private var tmpLinesLeft: ArrayList<LineBody>? = null
    private var tmpLinesRight: ArrayList<LineBody>? = null
    private var tmpLinesTop: ArrayList<LineBody>? = null
    private var tmpLinesBot: ArrayList<LineBody>? = null
    private var tmpLinesSensor: ArrayList<LineBody>? = null
    private var functions: Functions
    private var contactListener: ContactListener? = null
    private var tmpPolygon: Polygon? = null
    var polygonPool: Pool<Polygon?>
    var bulletLine: LineBody
    var actualPolig: Polygon? = null
    private var intersectorCenter: Vector2?
    private var intersectorLeft: Vector2?
    private var intersectorRight: Vector2?
    private val lineCheckerCenter: LineBody
    private val lineCheckerLeft: LineBody
    private val lineCheckerRight: LineBody
    private val rectanglesToRemove: ArrayList<RectangleBody>
    private val rectanglesToAdd: ArrayList<RectangleBody>
    private val rectanglesToTranslate: ArrayList<RectangleBody>
    fun setGravity(gravity: Float) {
        this.gravity = gravity
        for (rectangleBody in rectangles) {
            if (gravity < 0) rectangleBody.velocity.y -= rectangleBody.JUMP_VELOCITY / PPU else if (gravity > 0) {
                rectangleBody.velocity.y = rectangleBody.JUMP_VELOCITY / PPU
            }
        }
    }

    fun drawDebugWorld(cam: OrthographicCamera,shapeRenderer:ShapeRenderer) { // Draw Objects
        shapeRenderer.projectionMatrix = cam.combined
        shapeRenderer.setColor(0f, 1f, 0f, 1f)
        //shapeRenderer.begin(ShapeType.Line)
        if (showDebug) {
            shapeRenderer.line(lineCheckerCenter.getP1(), lineCheckerCenter.getP2())
            shapeRenderer.line(lineCheckerLeft.getP1(), lineCheckerLeft.getP2())
            shapeRenderer.line(lineCheckerRight.getP1(), lineCheckerRight.getP2())
        }
        for (polyline in polylines) {
            shapeRenderer.polyline(polyline.transformedVertices)
        }
        for (rectangle in rectangles) {
            shapeRenderer.rect(rectangle.x*PPU, rectangle.y*PPU, rectangle.width*PPU, rectangle.height*PPU)
        }
        //shapeRenderer.end()
        // Draw Sensors
        if (drawSensors) {
            shapeRenderer.projectionMatrix = cam.combined
            shapeRenderer.setColor(1f, 1f, 0f, 1f)
            //shapeRenderer.begin(ShapeType.Line)
            if (drawBulletLine) {
                if (bulletLineExist) shapeRenderer.line(bulletLine.x1, bulletLine.y1, bulletLine.x2, bulletLine.y2)
                bulletLineExist = false
            }
            for (rectangle in rectangles) {
                for (sensor in rectangle.sensors) {
                    shapeRenderer.rect(sensor.x, sensor.y, sensor.width, sensor.height)
                }
            }
            //shapeRenderer.end()
        }
    }

    fun updateWorld(delta: Float) {
        for (r in rectanglesToAdd) {
            if (!rectangles.contains(r)) {
                rectangles.add(r)
            }
        }
        rectanglesToAdd.clear()
        for (rectangle in rectangles) {
            if (rectangle.type === Types.TYPE.DYNAMIC) {
                this.delta = MathUtils.clamp(delta, 0.01f, 0.02f)
                rectangle.velocity.add(0f, gravity * rectangle.gravityScale)
                // clamp the velocity to the maximum
                if (rectangle.clampY) {
                    rectangle.velocity.y = MathUtils.clamp(rectangle.velocity.y, -rectangle.MAX_VELOCITY_Y / PPU,
                            rectangle.MAX_VELOCITY_Y / PPU)
                }
                // Bullet Variables
                for (sensor in rectangle.sensors) {
                    if (sensor.isBullet) {
                        sensor.setPosition(rectangle.x + rectangle.width / 2 - sensor.width / 2 + sensor.xRelative,
                                rectangle.y + rectangle.height / 2 - sensor.height / 2 + sensor.yRelative)
                        sensor.lastX = sensor.x
                        sensor.lastY = sensor.y
                    }
                }
                rectangle.velocity.scl(this.delta)
                // Comprobar Izquierda---------------------
                tmpRectangleLeft = rectPool.obtain()
                tmpRectangleLeft!!.set(rectangle)
                tmpRectangleLeft!!.x += rectangle.velocity.x
                if (rectangle.velocity.x <= 0) { // Comprobar si colisionamos con pared izquierda
                    tmpLinesLeft = functions.collisionRectLine(tmpRectangleLeft!!, polylines)
                    functions.setChecker(rectangle, lineCheckerLeft, "left")
                    for (lineBody in tmpLinesLeft!!) {
                        if (tmpLinesLeft != null && gravity == 0f) {
                            rectangle.velocity.x = 0f
                            break
                        }
                        if (tmpLinesLeft != null && lineBody.x1 == lineBody.x2) {
                            rectangle.velocity.x = 0f
                            rectangle.x = lineBody.x1 + 0.01f / PPU
                        }
                    }
                    if (functions.collisionLine(lineCheckerLeft, polylines) >= 2) {
                        rectangle.velocity.x = 0f
                    }
                }
                // Comprobar Derecha--------------------------
                tmpRectangleRight = rectPool.obtain()
                tmpRectangleRight!!.set(rectangle)
                tmpRectangleRight!!.x += rectangle.velocity.x
                if (rectangle.velocity.x >= 0) { // Comprobar si colisionamos con pared derecha
                    tmpLinesRight = functions.collisionRectLine(tmpRectangleRight!!, polylines)
                    functions.setChecker(rectangle, lineCheckerRight, "right")
                    for (lineBody in tmpLinesRight!!) {
                        if (tmpLinesRight != null && gravity == 0f) {
                            rectangle.velocity.x = 0f
                            break
                        }
                        if (tmpLinesRight != null && lineBody.x1 == lineBody.x2) {
                            rectangle.velocity.x = 0f
                            rectangle.x = lineBody.x1 - rectangle.width - 0.01f / PPU
                        }
                    }
                    if (functions.collisionLine(lineCheckerLeft, polylines) >= 2) {
                        rectangle.velocity.x = 0f
                    }
                }
                // Comprobar Arriba ----------------------------
                tmpRectangleTop = rectPool.obtain()
                tmpRectangleTop!!.set(rectangle)
                tmpRectangleTop!!.y += rectangle.velocity.y
                tmpRectangleTop!!.x += rectangle.velocity.x
                if (rectangle.velocity.y > 0 && gravity >= 0) {
                    functions.setChecker(tmpRectangleTop!!, lineCheckerCenter, "up")
                    functions.setChecker(tmpRectangleTop!!, lineCheckerRight, "rightUp")
                    functions.setChecker(tmpRectangleTop!!, lineCheckerLeft, "leftUp")
                    // Comprobar si colisionamos con pared arriba
                    tmpLinesTop = functions.collisionRectLine(tmpRectangleTop!!, polylines)
                    for (lineBody in tmpLinesTop!!) {
                        if (tmpLinesTop != null && gravity >= 0 && lineBody.linetype !== LINETYPE.SENSOR) {
                            intersectorCenter = functions.intersect2lines(lineCheckerCenter.getP1(),
                                    lineCheckerCenter.getP2(), lineBody.getP1(), lineBody.getP2())
                            intersectorRight = functions.intersect2lines(lineCheckerRight.getP1(),
                                    lineCheckerRight.getP2(), lineBody.getP1(), lineBody.getP2())
                            intersectorLeft = functions.intersect2lines(lineCheckerLeft.getP1(),
                                    lineCheckerLeft.getP2(), lineBody.getP1(), lineBody.getP2())
                            if (tmpLinesTop != null && gravity == 0f) {
                                rectangle.velocity.y = 0f
                                break
                            }
                            if (lineBody.y1 != lineBody.y2 && lineBody.x1 == lineBody.x2) {
                                rectangle.velocity.y = 0f
                                min = functions.getMin(lineBody.y1, lineBody.y2)
                                rectangle.y = min - tmpRectangleTop!!.height - 0.01f / PPU
                            }
                            if (intersectorCenter != null && lineBody.y1 != lineBody.y2) {
                                if (intersectorCenter!!.y - (tmpRectangleTop!!.y + tmpRectangleTop!!.height) <= 1) {
                                    rectangle.velocity.y = 0f
                                    if (lineBody.y1 == lineBody.y2) {
                                        rectangle.y = intersectorCenter!!.y - rectangle.height - 0.01f / PPU
                                    } else if (lineBody.y1 != lineBody.y2 && gravity > 0) {
                                        if (intersectorCenter!!.y > tmpRectangleTop!!.y - tmpRectangleTop!!.height / 2f) {
                                            rectangle.y = intersectorCenter!!.y - tmpRectangleTop!!.height - 0.01f / PPU
                                        }
                                    }
                                }
                                break
                            } else if (intersectorRight != null && lineBody.y1 == lineBody.y2) {
                                if (intersectorCenter != null && intersectorCenter!!.y == intersectorRight!!.y
                                        || lineBody.y1 == lineBody.y2 && tmpLinesTop!!.size < 2) {
                                    rectangle.velocity.y = 0f
                                    if (lineBody.y1 == lineBody.y2) {
                                        rectangle.y = intersectorRight!!.y - rectangle.height - 0.01f / PPU
                                    } else if (lineBody.y1 != lineBody.y2 && gravity > 0) {
                                        if (intersectorRight!!.y > tmpRectangleTop!!.y - tmpRectangleTop!!.height / 2) {
                                            rectangle.y = intersectorRight!!.y - tmpRectangleTop!!.height - 0.01f / PPU
                                        }
                                    }
                                }
                            } else if (intersectorLeft != null && lineBody.y1 == lineBody.y2) {
                                if (intersectorCenter != null && intersectorCenter!!.y == intersectorLeft!!.y
                                        || lineBody.y1 == lineBody.y2 && tmpLinesTop!!.size < 2) {
                                    rectangle.velocity.y = 0f
                                    if (lineBody.y1 == lineBody.y2) {
                                        rectangle.y = intersectorLeft!!.y - rectangle.height - 0.01f / PPU
                                    } else if (lineBody.y1 != lineBody.y2 && gravity > 0) {
                                        if (intersectorLeft!!.y > tmpRectangleTop!!.y - tmpRectangleTop!!.height / 2) {
                                            rectangle.y = intersectorLeft!!.y - tmpRectangleTop!!.height - 0.01f / PPU
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (rectangle.velocity.y >= 0 && gravity <= 0) {
                    tmpLinesTop = functions.collisionRectLine(tmpRectangleTop!!, polylines)
                    for (lineBody in tmpLinesTop!!) {
                        functions.setChecker(tmpRectangleTop!!, lineCheckerCenter, "up")
                        functions.setChecker(tmpRectangleTop!!, lineCheckerRight, "rightUp")
                        functions.setChecker(tmpRectangleTop!!, lineCheckerLeft, "leftUp")
                        if (tmpLinesBot != null && lineBody.linetype !== LINETYPE.JUMPTHRU && lineBody.linetype !== LINETYPE.SENSOR && gravity <= 0) {
                            intersectorRight = functions.intersect2lines(lineCheckerRight.getP1(),
                                    lineCheckerRight.getP2(), lineBody.getP1(), lineBody.getP2())
                            intersectorLeft = functions.intersect2lines(lineCheckerLeft.getP1(),
                                    lineCheckerLeft.getP2(), lineBody.getP1(), lineBody.getP2())
                            if (intersectorRight != null || intersectorLeft != null) {
                                rectangle.velocity.y = 0f
                                rectangle.y = rectangle.y - 2 / PPU
                                rectangle.velocity.x = 0f
                                if (intersectorRight != null && lineBody.y1 != lineBody.y2) {
                                    rectangle.x = rectangle.x - 2 / PPU
                                } else if (intersectorLeft != null && lineBody.y1 != lineBody.y2) {
                                    rectangle.x = rectangle.x + 2 / PPU
                                }
                            }
                        }
                    }
                }
                // Comprobar Abajo-------------------------
                tmpRectangleBot = rectPool.obtain()
                tmpRectangleBot!!.set(rectangle)
                tmpRectangleBot!!.y += rectangle.velocity.y
                tmpRectangleBot!!.x += rectangle.velocity.x
                if (rectangle.velocity.y < 0 && gravity <= 0) { // Comprobar si colisionamos con pared abajo
                    tmpLinesBot = functions.collisionRectLine(tmpRectangleBot!!, polylines)
                    for (lineBody in tmpLinesBot!!) {
                        if (tmpLinesBot != null && gravity <= 0 && lineBody.linetype !== LINETYPE.SENSOR) {
                            functions.setChecker(tmpRectangleBot!!, lineCheckerCenter, "down")
                            functions.setChecker(tmpRectangleBot!!, lineCheckerRight, "rightDown")
                            functions.setChecker(tmpRectangleBot!!, lineCheckerLeft, "leftDown")
                            intersectorCenter = functions.intersect2lines(lineCheckerCenter.getP1(),
                                    lineCheckerCenter.getP2(), lineBody.getP1(), lineBody.getP2())
                            intersectorRight = functions.intersect2lines(lineCheckerRight.getP1(),
                                    lineCheckerRight.getP2(), lineBody.getP1(), lineBody.getP2())
                            intersectorLeft = functions.intersect2lines(lineCheckerLeft.getP1(),
                                    lineCheckerLeft.getP2(), lineBody.getP1(), lineBody.getP2())
                            if (tmpLinesBot != null && gravity == 0f) {
                                rectangle.velocity.y = 0f
                                break
                            }
                            if (lineBody.y1 != lineBody.y2 && lineBody.x1 == lineBody.x2) {
                                rectangle.velocity.y = 0f
                                max = functions.getMax(lineBody.y1, lineBody.y2)
                                rectangle.y = max + 0.01f / PPU
                            }
                            if (intersectorCenter != null && lineBody.y1 != lineBody.y2) {
                                if (tmpRectangleBot!!.y - intersectorCenter!!.y <= 1) {
                                    rectangle.velocity.y = 0f
                                    if (lineBody.y1 == lineBody.y2) {
                                        rectangle.y = intersectorCenter!!.y + 0.01f / PPU
                                    } else if (lineBody.y1 != lineBody.y2) {
                                        rectangle.y = intersectorCenter!!.y + 0.01f / PPU
                                    }
                                }
                                break
                            } else if (intersectorRight != null && lineBody.y1 == lineBody.y2) {
                                if (intersectorCenter != null && intersectorCenter!!.y == intersectorRight!!.y
                                        || lineBody.y1 == lineBody.y2 && tmpLinesBot!!.size < 2) {
                                    rectangle.velocity.y = 0f
                                    if (lineBody.y1 == lineBody.y2) {
                                        rectangle.y = intersectorRight!!.y + 0.01f / PPU
                                    } else if (lineBody.y1 != lineBody.y2) {
                                        if (intersectorRight!!.y < tmpRectangleBot!!.y + tmpRectangleBot!!.height / 2) {
                                            rectangle.y = intersectorRight!!.y + 0.01f / PPU
                                        }
                                    }
                                }
                            } else if (intersectorLeft != null && lineBody.y1 == lineBody.y2) {
                                if (intersectorCenter != null && intersectorCenter!!.y == intersectorLeft!!.y
                                        || lineBody.y1 == lineBody.y2 && tmpLinesBot!!.size < 2) {
                                    rectangle.velocity.y = 0f
                                    if (lineBody.y1 == lineBody.y2) {
                                        rectangle.y = intersectorLeft!!.y + 0.01f / PPU
                                    } else if (lineBody.y1 != lineBody.y2) {
                                        if (intersectorLeft!!.y < tmpRectangleBot!!.y + tmpRectangleBot!!.height / 2) {
                                            rectangle.y = intersectorLeft!!.y + 0.01f / PPU
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (rectangle.velocity.y < 0 && gravity >= 0) {
                    functions.setChecker(tmpRectangleBot!!, lineCheckerRight, "rightDown")
                    functions.setChecker(tmpRectangleBot!!, lineCheckerLeft, "leftDown")
                    tmpLinesBot = functions.collisionRectLine(tmpRectangleBot!!, polylines)
                    for (lineBody in tmpLinesBot!!) {
                        if (tmpLinesBot != null && lineBody.linetype !== LINETYPE.JUMPTHRU && lineBody.linetype !== LINETYPE.SENSOR && gravity > 0) {
                            intersectorRight = functions.intersect2lines(lineCheckerRight.getP1(),
                                    lineCheckerRight.getP2(), lineBody.getP1(), lineBody.getP2())
                            intersectorLeft = functions.intersect2lines(lineCheckerLeft.getP1(),
                                    lineCheckerLeft.getP2(), lineBody.getP1(), lineBody.getP2())
                            if (intersectorRight != null || intersectorLeft != null) {
                                rectangle.velocity.y = 0f
                                rectangle.y = rectangle.y + 2 / PPU
                                rectangle.velocity.x = 0f
                                if (intersectorRight != null && lineBody.y1 != lineBody.y2) {
                                    rectangle.x = rectangle.x - 2 / PPU
                                } else if (intersectorLeft != null && lineBody.y1 != lineBody.y2) {
                                    rectangle.x = rectangle.x + 2 / PPU
                                }
                            }
                        }
                    }
                }
                // -----------------------------------------------
// Colisi�n con otros cuerpos
                for (rectangleBody in rectangles) {
                    if (rectangleBody.type != Types.TYPE.SENSOR && (rectangleBody.type == Types.TYPE.STATIC || collideWithDynamicRectangles)) {
                        if (rectangleBody != rectangle) {
                            if (rectangle.velocity.x > 0) {
                                if (rectangleBody.overlaps(tmpRectangleRight)) {
                                    rectangle.velocity.x = 0f
                                    rectangle.x = rectangleBody.x - rectangle.getWidth() - 0.01f / PPU
                                }
                            }
                            if (rectangle.velocity.x < 0) {
                                if (rectangleBody.overlaps(tmpRectangleLeft)) {
                                    rectangle.velocity.x = 0f
                                    rectangle.x = rectangleBody.x + rectangleBody.getWidth() + 0.01f / PPU
                                }
                            }
                            if (rectangle.velocity.y < 0) {
                                if (rectangleBody.overlaps(tmpRectangleBot)
                                        && rectangle.y > rectangleBody.y + rectangleBody.height) {
                                    rectangle.velocity.y = 0f
                                    rectangle.y = rectangleBody.y + rectangleBody.getHeight() + 0.01f / PPU
                                }
                            }
                            if (rectangle.velocity.y > 0) {
                                if (rectangleBody.overlaps(tmpRectangleTop)
                                        && rectangle.y + rectangle.height < rectangleBody.y) {
                                    rectangle.velocity.y = 0f
                                    rectangle.y = rectangleBody.y - rectangle.getHeight() - 0.01f / PPU
                                }
                            }
                        }
                    }
                }
                rectPool.free(tmpRectangleBot)
                rectPool.free(tmpRectangleTop)
                rectPool.free(tmpRectangleRight)
                rectPool.free(tmpRectangleLeft)
                rectangle.y += rectangle.velocity.y
                rectangle.x += rectangle.velocity.x
                // Actualizamos posicion de sensores del cuadrado
                for (sensor in rectangle.sensors) {
                    sensor.setPosition(rectangle.x + rectangle.width / 2 - sensor.width / 2 + sensor.xRelative,
                            rectangle.y + rectangle.height / 2 - sensor.height / 2 + sensor.yRelative)
                }
                if (contactListener != null) { // COMPROBAR COLISON DE SENSORES con cuerpos, otros sensores, y lineas
                    for (rectangleBody in rectangles) {
                        if (rectangleBody != rectangle) {
                            for (sensor in rectangle.sensors) { // Colison con cuerpos IN
                                if (sensor.overlaps(rectangleBody) && !sensor.rectangleBodies.contains(rectangleBody)) {
                                    sensor.rectangleBodies.add(rectangleBody)
                                    contactListener!!.sensorBodyIn(sensor, rectangleBody)
                                }
                                // Colision con cuerpos OUT
                                if (sensor.rectangleBodies.contains(rectangleBody) && !sensor.overlaps(rectangleBody)) {
                                    sensor.rectangleBodies.remove(rectangleBody)
                                    contactListener!!.sensorBodyOut(sensor, rectangleBody)
                                }
                                // COLISION DE SENSORES
                                for (sensorRectangle in rectangleBody.sensors) {
                                    if (sensorRectangle != sensor) { // Colision con sensores IN
                                        if (!sensor.isBullet) {
                                            if (sensor.overlaps(sensorRectangle)
                                                    && !sensor.sensors.contains(sensorRectangle)) {
                                                sensor.sensors.add(sensorRectangle)
                                                contactListener!!.sensorSensorIn(sensor, sensorRectangle)
                                            }
                                        } else if (sensor.isBullet && !sensor.sensors.contains(sensorRectangle)) {
                                            functions.updateBullet(bulletLine, sensor)
                                            bulletLineExist = true
                                            actualPolig = polygonPool.obtain()
                                            actualPolig!!.vertices = rectGetVertices(sensorRectangle)
                                            if (Intersector.intersectSegmentPolygon(bulletLine.getP1(),
                                                            bulletLine.getP2(), actualPolig)) {
                                                sensor.sensors.add(sensorRectangle)
                                                contactListener!!.sensorSensorIn(sensor, sensorRectangle)
                                            } else if (sensor.overlaps(sensorRectangle) && !sensor.sensors.contains(sensorRectangle)) {
                                                sensor.sensors.add(sensorRectangle)
                                                contactListener!!.sensorSensorIn(sensor, sensorRectangle)
                                            }
                                            polygonPool.free(actualPolig)
                                        }
                                        // Colision con sensores OUT
                                        if (sensor.sensors.contains(sensorRectangle)
                                                && !sensor.overlaps(sensorRectangle)) {
                                            sensor.sensors.remove(sensorRectangle)
                                            contactListener!!.sensorSensorOut(sensor, sensorRectangle)
                                        }
                                    }
                                }
                            }
                        }
                        // Collision lineas
                        for (sensor in rectangle.sensors) {
                            tmpLinesSensor = functions.collisionRectLine(sensor, polylines)
                            // Colision lineas IN
                            for (lineBody in tmpLinesSensor!!) {
                                if (!sensor.containsLine(lineBody)) {
                                    sensor.lineBodies.add(lineBody)
                                    contactListener!!.sensorLineIn(sensor, lineBody)
                                }
                            }
                            // Colision lineas OUT
                            tmpPolygon = polygonPool.obtain()
                            tmpPolygon!!.vertices = rectGetVertices(sensor)
                            for (lineBody in sensor.lineBodies) {
                                if (!Intersector.intersectSegmentPolygon(lineBody.getP1(), lineBody.getP2(),
                                                tmpPolygon)) {
                                    sensor.lineBodiesToRemove.add(lineBody)
                                }
                            }
                            for (lineBody in sensor.lineBodiesToRemove) {
                                if (sensor.lineBodies.contains(lineBody)) {
                                    sensor.lineBodies.remove(lineBody)
                                    contactListener!!.sensorLineOut(sensor, lineBody)
                                }
                            }
                        }
                    }
                }
                rectangle.velocity.scl(1 / this.delta)
            }
        }
        for (rectangleBody in rectanglesToRemove) {
            if (rectangles.contains(rectangleBody)) {
                rectangleBody.sensors.clear()
                rectangles.remove(rectangleBody)
            }
        }
        rectanglesToRemove.clear()
        for (rectangleBody in rectanglesToTranslate) {
            rectangleBody.setPosition(rectangleBody.translateX, rectangleBody.translateY)
        }
        rectanglesToTranslate.clear()
    }

    fun getGravity(): Float {
        return gravity
    }

    fun addRectangleBody(rectangleBody: RectangleBody) {
        rectanglesToAdd.add(rectangleBody)
    }

    fun translateBody(body: RectangleBody, x: Float, y: Float) {
        body.translateX = x
        body.translateY = y
        rectanglesToTranslate.add(body)
    }

    fun addPolylineBody(polylineBody: PolylineBody) {
        polylines.add(polylineBody)
    }

    fun removeRectangleBody(rectangleBody: RectangleBody) {
        rectanglesToRemove.add(rectangleBody)
    }

    fun removePolylineBody(polylineBody: PolylineBody?) {
        polylines.remove(polylineBody)
    }

    fun addArrayRectangleBodies(rectangleBodies: ArrayList<RectangleBody>) {
        rectanglesToAdd.addAll(rectangleBodies)
    }

    fun addArrayPolylineBodies(polylineBodies: ArrayList<PolylineBody>) {
        polylines.addAll(polylineBodies)
    }

    fun addContactListener(contactListener: ContactListener?) {
        this.contactListener = contactListener
    }

    init {
        this.gravity = gravity / PPU // Alto del AspectRatio
        bulletLine = LineBody()
        //shapeRenderer = ShapeRenderer()
        polylines = ArrayList()
        rectangles = ArrayList()
        rectanglesToRemove = ArrayList()
        rectanglesToAdd = ArrayList()
        rectanglesToTranslate = ArrayList()
        rectPool = object : Pool<Rectangle?>() {
            override fun newObject(): Rectangle {
                return Rectangle()
            }
        }
        polygonPool = object : Pool<Polygon?>() {
            override fun newObject(): Polygon {
                return Polygon()
            }
        }
        functions = Functions()
        intersectorCenter = Vector2()
        intersectorRight = Vector2()
        intersectorLeft = Vector2()
        lineCheckerCenter = LineBody()
        lineCheckerLeft = LineBody()
        lineCheckerRight = LineBody()
    }
}