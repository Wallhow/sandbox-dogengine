package sandbox.sandbox.go.environment

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import dogengine.Kernel
import dogengine.ecs.components.create
import dogengine.ecs.components.createBody
import dogengine.ecs.components.draw.CAtlasRegion
import dogengine.ecs.components.utility.CDeleteMe
import dogengine.ecs.components.utility.CName
import dogengine.ecs.components.utility.logic.CDefaultPhysics2d
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.logic.CUpdate
import dogengine.ecs.components.utility.logic.updateZIndex
import dogengine.es.redkin.physicsengine2d.variables.Types
import dogengine.utils.Size
import sandbox.R
import sandbox.go.items.onmap.models.GrassItemMap
import sandbox.go.items.onmap.models.WoodItemMap
import sandbox.sandbox.def.def.comp.CHealth

class Wood (position : Vector2, textureName : String) : Entity() {
    init {
        val assets = Kernel.getInjector().getInstance(AssetManager::class.java)
        val region = assets.get<TextureAtlas>(R.matlas0)
        create<CName> {
            name = "wood"
        }
        create<CAtlasRegion> {
            atlas = region
            nameRegion = textureName
        }
        val tex = CAtlasRegion[this@Wood].atlas!!.findRegion(CAtlasRegion[this@Wood].nameRegion)
        create<CTransforms> {
            this.position.set(position)
            size = Size(tex.regionWidth * 2f,tex.regionHeight * 2f)
            updateZIndex()
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
            beforeDead = {befDead()}
        }
    }
    private fun befDead()  {
        val engine = Kernel.getInjector().getInstance(Engine::class.java)
        this.create<CDeleteMe>()
        val size = CTransforms[this].size
        var count = MathUtils.random(3,4)
        for (i in 1..count) {
            val pos = CTransforms[this].position.cpy()
            pos.add(MathUtils.random(size.halfWidth-10f,size.halfWidth+10f), MathUtils.random(6f,size.halfHeight))
            engine.addEntity(WoodItemMap(pos, CTransforms[this].position.y))
        }
        count = MathUtils.random(3,5)
        for (i in 1..count) {
            val pos = CTransforms[this].position.cpy()
            pos.add(MathUtils.random(size.halfWidth-10f,size.halfWidth+10f), MathUtils.random(6f,size.halfHeight))
            engine.addEntity(GrassItemMap(pos, CTransforms[this].position.y))
        }
    }
}