package sandbox.sandbox.go.player.tools

import com.badlogic.gdx.graphics.g2d.TextureRegion
import sandbox.go.player.tools.ATool
import sandbox.sandbox.go.assetAtlas
import sandbox.sandbox.go.objects.ObjectList
import sandbox.sandbox.go.findRegionOfTool

class TPickaxeWood : ATool() {
    override val type: ToolsList = ToolsList.PICKAXE_WOOD
    override val distance: Float = 64f
    override val force = Force().apply {
        value = 0.25f
        duration = 0.15f
    }
    override val name: String = type.name_res
    override val image: TextureRegion = assetAtlas().findRegionOfTool(type)
    init {
        workWith.add(ObjectList.SANDSTONE)
        workWith.add(ObjectList.ROCK)
    }


}