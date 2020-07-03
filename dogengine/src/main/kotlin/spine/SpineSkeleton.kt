package dogengine.spine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.esotericsoftware.spine.AnimationState
import com.esotericsoftware.spine.AnimationStateData
import com.esotericsoftware.spine.Skeleton
import com.esotericsoftware.spine.SkeletonJson

class SpineSkeleton(val atlas: TextureAtlas, skeletonJsonPath: String, val scale: Float = 1f) {
    private val skeleton: Skeleton
    private val state: AnimationState

    init {
        val json = SkeletonJson(atlas)
        json.scale = scale
        json.readSkeletonData(Gdx.files.internal(skeletonJsonPath)).apply {
            skeleton = Skeleton(this)
            // Skeleton holds skeleton state (bone positions, slot attachments, etc).
            val stateData = AnimationStateData(this)
            state = AnimationState(stateData) // Holds the animation state for a skeleton (current animation, time, etc).

        }
        state.timeScale = 0.7f // Slow all animations down to 50% speed.
    }

    fun setAnimation(animationName: String, loop: Boolean = true) {
        state.setAnimation(0, animationName, loop)
    }

    fun setPosition(position: Vector2) {
        skeleton.setPosition(position.x, position.y)
    }

    var rotation: Float
        get() = skeleton.rootBone.rotation
        set(value) {
            skeleton.rootBone.rotation = value
        }

}