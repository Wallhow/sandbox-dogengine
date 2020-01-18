package sandbox.sandbox.go.items

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import dogengine.Kernel
import dogengine.ecs.components.create
import dogengine.ecs.components.draw.CAtlasRegion
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.logic.updateZIndex
import dogengine.utils.Size
import sandbox.R
import sandbox.sandbox.def.def.comp.CDrop

class GrassItem(pos:Vector2): Entity() {
    init {
        val assets = Kernel.getInjector().getInstance(AssetManager::class.java)
        val region = assets.get<TextureAtlas>(R.matlas0)
        create<CTransforms> {
            this.position.set(pos)
            size = Size(24f,24f)
            updateZIndex()
        }
        create<CAtlasRegion> {
            atlas = region
            nameRegion = "grass_item"
        }
        create<CDrop> {
            time = 0.5f
        }
    }
}