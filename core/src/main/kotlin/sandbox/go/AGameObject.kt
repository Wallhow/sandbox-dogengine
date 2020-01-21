package sandbox.sandbox.go

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import dogengine.Kernel
import dogengine.ecs.components.create
import dogengine.ecs.components.createBody
import dogengine.ecs.components.createSensor
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
import sandbox.sandbox.go.items.ItemID
import sandbox.sandbox.go.items.ObjectList

abstract class AGameObject (name: String) : Entity(), IGameObject {
    override val entity: Entity get() = this
    abstract override val dropID: ObjectList
    protected val assets = Kernel.getInjector().getInstance(AssetManager::class.java)
    protected val atlas = assets.get<TextureAtlas>(R.matlas0)
    protected val engine = Kernel.getInjector().getInstance(Engine::class.java)

    init {
        create<CName> { this.name = name }
    }

    protected fun createCAtlasRegion(name: String = CName[this].name) : CAtlasRegion {
        create<CAtlasRegion> {
            atlas = this@AGameObject.atlas
            nameRegion = name
        }
        return CAtlasRegion[this]
    }

    protected fun createCTransform(pos: Vector2, size: Size) {
        create<CTransforms> {
            this.position.set(pos)
            this.size = size
            updateZIndex()
        }
    }

    protected fun createCPhysicsDef(x: Float = 0f,y: Float = 0f,
                                    width : Float = CTransforms[this@AGameObject].size.width,
                                    height: Float = CTransforms[this@AGameObject].size.height,sensor: Boolean = true,
                                    type: Types.TYPE = Types.TYPE.STATIC) {
        create<CDefaultPhysics2d> {
            createBody(CTransforms[this@AGameObject],x, y,width,height).name = CName[this@AGameObject].name
            if(sensor)
                rectangleBody?.createSensor()
            rectangleBody?.userData = this@AGameObject
        }
    }

    protected fun createCUpdate(process: (delta: Float) -> Unit) {
        create<CUpdate> {
            func = process
            CTransforms[this@AGameObject].updateZIndex()
        }
    }

    protected fun deleteMe() {
        create<CDeleteMe>()
    }
}

interface IGameObject : ItemID {
    val entity: Entity
}