package dogengine.ecs.components.draw

import com.badlogic.gdx.utils.ArrayMap

class FrameSequence ()  {
    val sequences: ArrayMap<Int,FrameArray> = ArrayMap()
    private var currentTime: Float = 0f
    private var add = 1
    var currentSequence: Int = -1
    set(value) {
        add = 1
        currentTime = 0f
        field = value
    }

    fun anim(delta: Float) {
        currentTime+=delta
        val seq = sequences[currentSequence]
        if(!seq.isDone) {
            if (currentTime >= seq.duration) {
                currentTime = 0f
                seq.indexFrame += add
                if (seq.indexFrame >= seq.length || seq.indexFrame<0) {
                    if (seq.isRepeat) {
                        if(seq.isYoyo) {
                            if(seq.indexFrame>0) {
                                add = -1
                                seq.indexFrame = seq.length-1
                            }
                            else {
                                add = 1
                                seq.indexFrame = 0
                            }
                        }
                        else
                            seq.indexFrame = 0
                    } else {
                        seq.indexFrame = seq.length - 1
                        seq.isDone = true
                    }
                }
            }
        }
    }

    fun getCurrentFrame() : Int {
        return sequences[currentSequence].currentFrame
    }

    fun reset() {
        sequences.clear()
        currentTime = 0f
        currentSequence = -1
    }

    class FrameArray(val duration: Float,array: IntArray? = null) {
        private var frames = array
        var isRepeat = true
        var isDone = false
        var isYoyo = false
        var indexFrame = 0
        val length: Int
            get() = frames!!.size
        val currentFrame : Int
            get() = frames!![indexFrame]

        fun putFrames(frames_: IntArray) {
            frames = frames_
        }
    }
}