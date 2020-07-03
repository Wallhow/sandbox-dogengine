package dogengine.ecs.systems.utility

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import dogengine.ecs.systems.SystemPriority
import dogengine.utils.log

class STime : EntitySystem(SystemPriority.BEFORE_UPDATE) {
    private var accTimeMain = 10f*60f
    var sec = 0f
    var minute = 0f
    var hour = 0f
    var day = 0f
    var scl = 3f
    var sun : Vector2 = Vector2()
    override fun update(deltaTime: Float) {
        accTimeMain += deltaTime*scl

        minute = accTimeMain%60
        hour = accTimeMain/60 - day*24
        if(hour>=24) {
            day++
        }
        val c = this.getCurrentHour()
        val r =- (180-(( 360f/(24f)) * c))
        val s = 100f
        sun.set(MathUtils.sin(MathUtils.degreesToRadians*r)*s,
                MathUtils.cos(MathUtils.degreesToRadians*r)*s)
    }

    fun getCurrentTotalTime() : Float = accTimeMain
    fun getCurrentHour(): Float = hour
    fun getCurrentDay() : Float = day
    fun getCurrentMinute(): Float  = minute

}