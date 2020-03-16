package dogengine.utils

class Size(private var width_: Float = 0f,private var height_: Float = 0f) {
    val width: Float
    get() = width_
    val height: Float
    get() = height_
    val halfWidth : Float
        get() = width*0.5f
    val halfHeight : Float
        get() = height*0.5f
    var originX = halfWidth
    var originY = halfHeight
    var scaleX = 1f
    var scaleY = 1f

    var scale: Float = 1f
    set(value) {
        scaleX = value
        scaleY = value
        field = value
    }

    fun set(newWidth:Float,newHeight: Float) {
        width_=newWidth
        height_=newHeight
    }
    fun setNewWidth(newWidth: Float) {
        width_=newWidth
    }
    fun setNewHeight(newHeight: Float) {
        height_=newHeight
    }

    fun setZero() {
        set(0f,0f)
        scale = 1f
    }

    fun getRadius(): Float {
        return if(width==0f || height==0f) 0f else {
            (halfWidth+halfHeight)/2
        }
    }

    override fun toString(): String {
        return "(width: $width,height:$height)"
    }

    fun set(newSize: Size) {
        set(newSize.width,newSize.height)
    }
}