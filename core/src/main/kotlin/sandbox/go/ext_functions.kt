package sandbox.sandbox.go

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import dogengine.Kernel
import sandbox.R
import sandbox.sandbox.go.player.tools.ToolsList

fun assets() : AssetManager {
    return Kernel.getInjector().getInstance(AssetManager::class.java)
}
fun assetAtlas() : TextureAtlas {
    return assets().get<TextureAtlas>(R.matlas0)
}

fun TextureAtlas.findRegionOfTool(type: ToolsList): TextureRegion {
    return assetAtlas().findRegion(type.name_res)
}
fun assetTextureRegion(name: String,index: Int = -1) : TextureRegion {
    return if (index ==-1) assetAtlas().findRegion(name) else assetAtlas().findRegion(name,index)
}