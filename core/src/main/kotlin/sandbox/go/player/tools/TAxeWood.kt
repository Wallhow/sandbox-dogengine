package sandbox.go.player.tools

import com.badlogic.gdx.graphics.g2d.TextureRegion
import sandbox.sandbox.go.assetAtlas
import sandbox.sandbox.go.objects.ObjectList
import sandbox.sandbox.go.findRegionOfTool
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
        workWith.add(ObjectList.WORKBENCH)
    }
}