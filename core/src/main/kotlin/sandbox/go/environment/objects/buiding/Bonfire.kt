package sandbox.sandbox.go.environment.objects.buiding

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.create
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.logic.CUpdate
import dogengine.ecs.components.utility.visible.CLightBox2D
import dogengine.ecs.components.utility.visible.LightType
import dogengine.redkin.physicsengine2d.variables.Types
import dogengine.utils.Size
import sandbox.dev.ecs.comp.CParticleEmitter
import sandbox.sandbox.def.particles.Presets
import sandbox.sandbox.go.environment.AGameObjectOnMap
import sandbox.sandbox.go.objects.ObjectList

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
        create<CLightBox2D> {
            type = LightType.POINT
        }
        create<CUpdate> {
            var acc = 0f
            func = {
                acc +=it
                CLightBox2D[this@Bonfire].light?.setPosition(CTransforms[this@Bonfire].getCenterX(), CTransforms[this@Bonfire].getCenterY())
                if(acc>= 0.15f) {
                    acc = 0f
                    CLightBox2D[this@Bonfire].light?.let {lt ->
                        lt.setPosition(lt.position
                                        .add(MathUtils.random(-5f,5f),MathUtils.random(-3f,3f)))

                    }
                }
            }
        }
    }
}