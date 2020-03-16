package dogengine.ecs.components.draw

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.Pool
import dogengine.ecs.def.ComponentResolver

class CTextureRegionAnimation : Component, Pool.Poolable {
    companion object : ComponentResolver<CTextureRegionAnimation>(CTextureRegionAnimation::class.java)
    private val animationArray: Array<SequenceAnimation> = Array()
    private var currentAnimation: String = ""
    private val map : HashMap<String,Int> = HashMap()
    var dirty: Boolean = true
    var frameWidth: Int = 1
    var frameHeight: Int = 1

    override fun reset() {
        animationArray.clear()
        currentAnimation = ""
        map.clear()
        dirty = true
        frameHeight = 1
        frameWidth = 1
    }

    fun addAnimation(sequenceAnimation: SequenceAnimation) {
        animationArray.add(sequenceAnimation)
        map[sequenceAnimation.name] = animationArray.size-1
        currentAnimation = sequenceAnimation.name
    }

    fun setCurrentAnimation(name: String) {
        currentAnimation = name
        getCurrentAnimation().runAnimation
    }
    fun getAnimation(name: String) : SequenceAnimation = getAnimationForName(name)
    fun getCurrentAnimation() : SequenceAnimation = getAnimationForName(currentAnimation)


    private fun getAnimationForName(name: String) : SequenceAnimation {
        return animationArray[if (map.get(name) != null) {
            map[name] as Int
        }
        else {
            throw GdxRuntimeException("CTextureRegionAnimation : animation for name $name unknown")
        }]
    }
}

data class SequenceAnimation (val name: String, val sequence: IntArray) {
    var timeBetweenFrames: Float = 0.24f
    var delay = 0.0f
    var currentFrame : Int = 0
    var loopAnimation = true
    var runAnimation = false
    var taskEndOfAnimation = {}

    fun start() { runAnimation = true }
    fun stop() { runAnimation = false }
    fun flip() {sequence.reverse()}
}
