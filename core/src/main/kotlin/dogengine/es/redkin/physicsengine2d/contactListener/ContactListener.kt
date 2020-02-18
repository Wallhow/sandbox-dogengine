package dogengine.es.redkin.physicsengine2d.contactListener

import dogengine.es.redkin.physicsengine2d.bodies.LineBody
import dogengine.es.redkin.physicsengine2d.bodies.RectangleBody
import dogengine.es.redkin.physicsengine2d.sensors.Sensor

interface ContactListener {
    fun sensorSensorIn(sensorA: Sensor?, sensorB: Sensor?)
    fun sensorSensorOut(sensorA: Sensor?, sensorB: Sensor?)
    fun sensorBodyIn(sensor: Sensor?, rectangleBody: RectangleBody?)
    fun sensorBodyOut(sensor: Sensor?, rectangleBody: RectangleBody?)
    fun sensorLineIn(sensor: Sensor?, line: LineBody?)
    fun sensorLineOut(sensor: Sensor?, line: LineBody?)
}