package sandbox.sandbox.go.environment.objects.buiding

import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.create
import dogengine.redkin.physicsengine2d.variables.Types
import dogengine.utils.Size
import sandbox.sandbox.def.def.comp.CParticleEmitter
import sandbox.sandbox.def.def.particles.Emitter
import sandbox.sandbox.def.particles.Presets
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
            emittersConf.add(Presets.fire)
        }
        create<CWorkbench> {
            type = objectType
        }
    }
}