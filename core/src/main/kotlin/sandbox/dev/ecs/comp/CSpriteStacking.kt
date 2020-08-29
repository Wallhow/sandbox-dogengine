package sandbox.sandbox.dev.ecs.comp

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array
import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent
import sandbox.sandbox.go.assetAtlas

class CSpriteStacking: PoolableComponent {
    companion object: ComponentResolver<CSpriteStacking>(CSpriteStacking::class.java)
    var angle: Float = 0f
    var scale: Float = 1f
    val spriteStack = Array<TextureRegion>()
    var paddingY = 1f
    override fun reset() {
        angle = 0f
        scale = 1f
        spriteStack.clear()
        paddingY = 1f
    }
}

fun CSpriteStacking.loadAsStack(nameAsset: String, sliceCount: Int, downTo: Boolean = false) {
    if (downTo)
        for (i in sliceCount downTo 1 ) {
            val texture = TextureRegion(assetAtlas().findRegion(nameAsset, i))
            this.spriteStack.add(texture)
            this.paddingY = 1f
            this.angle = 180f
        }
    else
        for (i in 1 until sliceCount) {
            val texture = TextureRegion(assetAtlas().findRegion(nameAsset, i))
            this.spriteStack.add(texture)
            this.paddingY = 1f
            this.angle = 180f
        }
}