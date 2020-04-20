package sandbox.go.player.tools

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import dogengine.ecs.components.*
import dogengine.ecs.components.utility.CDeleteMe
import dogengine.ecs.components.utility.logic.CDefaultPhysics2d
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.logic.CUpdate
import dogengine.redkin.physicsengine2d.variables.Types
import dogengine.utils.Size
import sandbox.R
import sandbox.sandbox.go.assetAtlas
import sandbox.sandbox.go.environment.ObjectList
import sandbox.sandbox.go.findRegionOfTool
import sandbox.sandbox.go.player.Player
import sandbox.sandbox.go.player.tools.ToolsList

class TAxeWood : ATool() {
    override val force: Force = Force().apply {
        value = 1f
        duration = 0.5f
    }
    override val type: ToolsList = ToolsList.AXE_WOOD
    override val name: String = type.name_res
    override val distance: Float = 36f
    override val image: TextureRegion = assetAtlas().findRegionOfTool(type)
    init {
        workWith.add(ObjectList.WOOD)
    }
}