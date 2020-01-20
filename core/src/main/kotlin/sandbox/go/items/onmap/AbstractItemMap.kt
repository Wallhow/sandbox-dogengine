package sandbox.go.items.onmap

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import dogengine.Kernel
import dogengine.ecs.components.create
import dogengine.ecs.components.createBody
import dogengine.ecs.components.createSensor
import dogengine.ecs.components.draw.CAtlasRegion
import dogengine.ecs.components.utility.logic.CDefaultPhysics2d
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.logic.updateZIndex
import dogengine.utils.Size
import sandbox.R
import sandbox.sandbox.def.def.comp.CDrop

abstract class AbstractItemMap(override val invID: Int, override var horizontalLine: Float = 0f) : ItemMap,Entity() {
    override val entity: Entity get() = this
    protected val atlas: TextureAtlas = Kernel.getInjector().getInstance(AssetManager::class.java).get<TextureAtlas>(R.matlas0)
    protected fun createCDrop(timeFly: Float, hLine: Float = horizontalLine) {
        create<CDrop> {
            time = timeFly
            y = hLine
        }
    }

    protected fun createCAtlasRegion(name: String) {
        create<CAtlasRegion> {
            atlas = this@AbstractItemMap.atlas
            nameRegion = name
        }
    }

    protected fun createCTransform(pos: Vector2, size: Size) {
        create<CTransforms> {
            this.position.set(pos)
            this.size = size
            updateZIndex()
        }
    }

    protected fun createCPhysicsDef() {
        create<CDefaultPhysics2d> {
            createBody(CTransforms[this@AbstractItemMap],0f,0f).name = invID.toString()
            rectangleBody?.createSensor()
        }
    }

}