package dogengine.utils

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Logger
import com.badlogic.gdx.utils.Timer
import dogengine.Kernel
import dogengine.PooledEntityCreate.engine

fun Any.log(obj: Any,level: Int = Logger.INFO) {
    val name = this.javaClass.canonicalName.substring(this.javaClass.canonicalName.lastIndexOf('.')+1)
    Gdx.app.logLevel = level
    Gdx.app.log(name,obj.toString())
}

inline fun <reified T : EntitySystem> system(apply: T.() -> Unit) : Boolean {
    val sys = engine?.getSystem(T::class.java)
    return if (sys != null) {
        apply.invoke(sys)
        true
    }
    else false
}


inline fun Boolean.isTrue(func: ()-> Unit) : Boolean {
    if(this)
        func.invoke()
    return this
}
inline fun Boolean.isElse(func:() -> Unit) {
    if(!this)
        func.invoke()
}

fun vec2(x: Float, y: Float) = Vector2(x,y)


inline fun gdxSchedule(delaySeconds: Float, intervalSeconds: Float, repeatCount: Int, crossinline run :() -> Unit) {
    Timer.schedule(object : Timer.Task() {
        override fun run() {
            run.invoke()
        }
    },delaySeconds,intervalSeconds,repeatCount)
}

inline fun gdxSchedule(delaySeconds: Float,intervalSeconds: Float,crossinline run :() -> Unit) {
    Timer.schedule(object : Timer.Task() {
        override fun run() {
            run.invoke()
        }
    },delaySeconds,intervalSeconds)
}

inline fun gdxSchedule(delaySeconds: Float,crossinline run :() -> Unit) {
    Timer.schedule(object : Timer.Task() {
        override fun run() {
            run.invoke()
        }
    },delaySeconds)
}
inline fun gdxPostTask(crossinline run: () -> Unit) {
    Timer.instance().postTask(object : Timer.Task() {
        override fun run() {
            run.invoke()
        }
    })
}
val sizeViewport: Size = Size()
inline val OrthographicCamera.viewBoundsRect: Rectangle
    get() {
        if(sizeViewport.width==0f)
            sizeViewport.set(this.viewportWidth * 0.5f,this.viewportHeight * 0.5f)
        return Kernel.viewBoundsRect.set(this.position.x - sizeViewport.width,
                this.position.y - sizeViewport.height,
                this.viewportWidth, this.viewportHeight)
    }
fun OrthographicCamera.scaleViewBoundsRect(scale: Float): Rectangle {
    return Rectangle().set(this.position.x - (this.viewportWidth * 0.5f)*scale,
            this.position.y - (this.viewportHeight * 0.5f)*scale,
            this.viewportWidth*scale, this.viewportHeight*scale)
}
