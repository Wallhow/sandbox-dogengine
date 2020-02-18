package dogengine.ecs.components.draw

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import dogengine.ecs.def.ComponentResolver

class CAtlasRegionAnimation : Component, Pool.Poolable {
    companion object : ComponentResolver<CAtlasRegionAnimation>(CAtlasRegionAnimation::class.java)
    val frameSequenceArray: FrameSequence = FrameSequence()
    override fun reset() {
        frameSequenceArray.reset()
    }
}


fun CAtlasRegionAnimation.createSequence(nameIdx: Int,duration: Float,block : FrameSequence.FrameArray.() -> Unit) : CAtlasRegionAnimation {
    frameSequenceArray.sequences.put(nameIdx, FrameSequence.FrameArray(duration).apply {
        block.invoke(this)
    })
    return this
}
fun CAtlasRegionAnimation.currentSequence(idx: Int) {
    frameSequenceArray.setCurrentSequence(idx)
}
