package map2D

import com.badlogic.gdx.math.Vector2
import dogengine.utils.vec2

data class Vector2Int(var x:Int, var y:Int) {
    fun scl(scl: Float): Vector2 {
        return vec2(x*scl,y*scl)
    }

    companion object {
        val tmp = Vector2Int(-1, -1)
    }
}