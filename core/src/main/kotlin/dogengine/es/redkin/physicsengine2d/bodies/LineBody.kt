package dogengine.es.redkin.physicsengine2d.bodies

import com.badlogic.gdx.math.Vector2
import dogengine.es.redkin.physicsengine2d.variables.Types.LINETYPE

class LineBody {
    @JvmField
    var x1 = 0f
    @JvmField
    var x2 = 0f
    @JvmField
    var y1 = 0f
    @JvmField
    var y2 = 0f
    @JvmField
    var p1: Vector2
    @JvmField
    var p2: Vector2
    var name: String? = null
    @JvmField
    var linetype: LINETYPE? = null

    constructor(x1: Float, y1: Float, x2: Float, y2: Float, linetype: LINETYPE?, name: String?) {
        this.linetype = linetype
        this.name = name
        this.x1 = x1
        this.y1 = y1
        this.x2 = x2
        this.y2 = y2
        p1 = Vector2(x1, y1)
        p2 = Vector2(x2, y2)
    }

    constructor() {
        p1 = Vector2()
        p2 = Vector2()
    }

    fun getP1(): Vector2 {
        p1.x = x1
        p1.y = y1
        return p1
    }

    fun getP2(): Vector2 {
        p2.x = x2
        p2.y = y2
        return p2
    }
}