package sandbox.sandbox.go.environment.objects.buiding

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.create
import dogengine.ecs.components.createComponent
import dogengine.redkin.physicsengine2d.variables.Types
import dogengine.utils.Size
import sandbox.sandbox.def.def.comp.CParticleEmitter
import sandbox.sandbox.def.def.particles.Emitter
import sandbox.sandbox.def.particles.colorFromRGB
import sandbox.sandbox.go.environment.AGameObjectOnMap
import sandbox.sandbox.go.environment.ObjectList

class Bonfire (position: Vector2) : AGameObjectOnMap(ObjectList.BONFIRE1) {
    init {
        createCAtlasRegion()
        createCTransform(position, Size(getAtlasRegion().regionWidth*0.5f ,
                getAtlasRegion().regionHeight * 0.5f))
        createCPhysicsDef(type = Types.TYPE.DYNAMIC)
        createCHealth(40f,itemType = null)
        create<CParticleEmitter> {
            val conf = Emitter.Configuration()
            conf.apply {
                pCountMin = 500
                pSpeedMin = 10f
                pSpeedMax = 20f
                pDirectionMin = Vector2(-0.3f,0.5f)
                pDirectionMax = Vector2(0.3f,1f)
                pLifeTimeMin = 1f
                pLifeTimeMax = 2f
                pRotationMin = -10f
                pRotationMax = 10f
                pOffsetMinX = -10f
                pOffsetMaxX = 10f
                pOffsetMinY = -5f
                pSizeMin = 3f
                pSizeMax = 6f
                colors.run {
                    add(colorFromRGB(82,9,0),
                            colorFromRGB(226,69,0),
                            colorFromRGB(254,141,0))
                    add(colorFromRGB(255,255,207))
                    add(colorFromRGB(10,10,10,150))
                }
            }
            emittersConf.add(conf)
        }
        create<CWorkbench> {
            type = objectType
        }
    }
}