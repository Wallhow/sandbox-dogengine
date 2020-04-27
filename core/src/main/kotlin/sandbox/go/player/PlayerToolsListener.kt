package sandbox.sandbox.go.player

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import dogengine.Kernel
import dogengine.ecs.components.create
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.redkin.physicsengine2d.bodies.LineBody
import dogengine.redkin.physicsengine2d.bodies.RectangleBody
import dogengine.redkin.physicsengine2d.contactListener.ContactListener
import dogengine.redkin.physicsengine2d.sensors.Sensor
import dogengine.particles2d.EffectsManager
import dogengine.utils.log
import sandbox.R
import sandbox.sandbox.def.def.comp.CShack

class PlayerToolsListener (private val player: Player) : ContactListener {
    private val effects = Kernel.getInjector().getInstance(EffectsManager::class.java)
    protected val assets = Kernel.getInjector().getInstance(AssetManager::class.java)
    protected val atlas = assets.get<TextureAtlas>(R.matlas0)

    private val durationShakeObject = 0.3f

    override fun sensorSensorIn(sensorA: Sensor?, sensorB: Sensor?) {

    }

    override fun sensorSensorOut(sensorA: Sensor?, sensorB: Sensor?) {

    }

    override fun sensorBodyIn(sensor: Sensor?, rectangleBody: RectangleBody?) {
        log("sensor ${sensor?.name} collide ${rectangleBody?.name}")
        sensor?.apply {
            if(this.name == player.getCurrentTool().name) {
                log(""+rectangleBody?.userData)
                if(rectangleBody?.userData!=null) {
                    val e = rectangleBody.userData as Entity
                    val tr = CTransforms[e]
                    effects.effectToPosition(1,tr.position.x+tr.size.halfWidth,
                            tr.position.y+tr.size.halfHeight)

                    e.create<CShack> {
                        duration = durationShakeObject
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