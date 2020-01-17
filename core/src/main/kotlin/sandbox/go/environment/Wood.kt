package sandbox.sandbox.go.environment

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import dogengine.Kernel
import dogengine.ecs.components.create
import dogengine.ecs.components.createBody
import dogengine.ecs.components.draw.CAtlasRegion
import dogengine.ecs.components.utility.CName
import dogengine.ecs.components.utility.logic.CDefaultPhysics2d
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.logic.CUpdate
import dogengine.ecs.components.utility.logic.updateZIndex
import dogengine.es.redkin.physicsengine2d.variables.Types
import dogengine.utils.Size
import sandbox.R
import sandbox.sandbox.def.def.comp.CHealth

class Wood (position : Vector2, textureName : String) : Entity() {
    init {
        val assets = Kernel.getInjector().getInstance(AssetManager::class.java)
        val region = assets.get<TextureAtlas>(R.matlas0)
        create<CName> {
            name = "wood"
        }
        create<CTransforms> {
            this.position.set(position)
            size = Size(64f*2.5f,76f*2.5f)
            updateZIndex()
        }
        create<CAtlasRegion> {
            atlas = region
            nameRegion = textureName
        }
        create<CDefaultPhysics2d> {
            val t = CTransforms[this@Wood]
            createBody(t, t.size.width/2f-(t.size.width / 6.5f)/2, 0f, t.size.width / 6.5f, t.size.height / 10, Types.TYPE.STATIC, "wood").userData = this@Wood
        }
        create<CUpdate> {
            func = {
                CTransforms[this@Wood].updateZIndex()
            }
        }
        create<CHealth> {
            health = 100f
        }
    }
}