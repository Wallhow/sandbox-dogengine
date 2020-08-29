package dogengine.ecs.components.utility.logic

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent
import dogengine.utils.Size
import dogengine.utils.vec2

class CTransforms : PoolableComponent {
    companion object : ComponentResolver<CTransforms>(CTransforms::class.java)

    var zIndex : Int = Int.MIN_VALUE
    var position: Vector2 = Vector2()
    var size: Size = Size()
    var angle = 0f
    private val rect: Rectangle = Rectangle()

    fun getCenterY() : Float = position.y+size.halfHeight
    fun getCenterX() : Float = position.x+size.halfWidth
    fun getCenterVector() : Vector2 = vec2(getCenterX(),getCenterY())

    fun getRect() : Rectangle = rect.set(position.x,position.y,size.width,size.height)

    override fun reset() {
        position.setZero()
        size.setZero()

        zIndex = Int.MIN_VALUE
    }
}
fun CTransforms.updateZIndex() {
    zIndex = MathUtils.round(-position.y)
}