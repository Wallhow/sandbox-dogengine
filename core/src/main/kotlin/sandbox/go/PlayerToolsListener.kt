package sandbox.sandbox.go

import com.badlogic.ashley.core.Entity
import dogengine.Kernel
import dogengine.ecs.components.create
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.es.redkin.physicsengine2d.bodies.LineBody
import dogengine.es.redkin.physicsengine2d.bodies.RectangleBody
import dogengine.es.redkin.physicsengine2d.contactListener.ContactListener
import dogengine.es.redkin.physicsengine2d.sensors.Sensor
import dogengine.particles2d.EffectsManager
import dogengine.utils.log
import sandbox.sandbox.def.def.comp.CExtraction
import sandbox.sandbox.def.def.comp.CShack

class PlayerToolsListener (private val player: Player) : ContactListener {
    private val effects = Kernel.getInjector().getInstance(EffectsManager::class.java)
    override fun sensorSensorIn(sensorA: Sensor?, sensorB: Sensor?) {

    }

    override fun sensorSensorOut(sensorA: Sensor?, sensorB: Sensor?) {

    }

    override fun sensorBodyIn(sensor: Sensor?, rectangleBody: RectangleBody?) {
        log("sensor ${player.getCurrentTool().name} collide ${rectangleBody?.name}")
        sensor?.apply {
            if(this.name == player.getCurrentTool().name) {
                if(rectangleBody?.userData!=null) {
                    val e = rectangleBody.userData as Entity
                    val tr = CTransforms[e]
                    effects.effectToPosition(1,tr.position.x+tr.size.halfWidth,
                            tr.position.y+tr.size.halfHeight)

                    e.create<CShack> {
                        duration = 0.5f
                    }
                    e.create<CExtraction> {
                        force = player.getCurrentTool().power
                    }
                }
            }
        }
    }

    override fun sensorBodyOut(sensor: Sensor?, rectangleBody: RectangleBody?) {

    }

    override fun sensorLineIn(sensor: Sensor?, line: LineBody?) {

    }

    override fun sensorLineOut(sensor: Sensor?, line: LineBody?) {

    }
}