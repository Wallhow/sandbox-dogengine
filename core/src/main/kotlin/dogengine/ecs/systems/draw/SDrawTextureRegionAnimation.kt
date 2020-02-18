package dogengine.ecs.systems.draw

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import com.google.inject.Inject
import dogengine.ecs.components.create
import dogengine.ecs.components.draw.CDrawable
import dogengine.ecs.components.draw.CTextureRegion
import dogengine.ecs.components.draw.CTextureRegionAnimation
import dogengine.ecs.components.utility.visible.CHide
import dogengine.ecs.def.GameEntity
import dogengine.ecs.systems.SystemPriority

class SDrawTextureRegionAnimation @Inject constructor() :
        IteratingSystem(Family.all(CTextureRegion::class.java, CTextureRegionAnimation::class.java).exclude(CHide::class.java).get()) {
    init {
        priority = SystemPriority.DRAW-20
    }
    private data class DefData(var time:Float) {
        var textureFrames: Array<TextureRegion> = Array() // кадры анимации
    }

    private var arrayStringDef: ArrayMap<String, DefData> = ArrayMap()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val it = CTextureRegionAnimation[entity]
        if(it.dirty) {
            entity as GameEntity
            val texture = CTextureRegion[entity].texture
            CTextureRegionAnimation[entity]?.apply {
                if (frameWidth != 0 || frameHeight != 0) {
                    val defData = DefData(0f)
                    for (textures in texture!!.split(frameWidth, frameHeight)) {
                        for (frame in textures) {
                            defData.textureFrames.add(frame)
                        }
                    }
                    arrayStringDef.put(entity.name, defData)
                }
            }
            it.dirty = false
        }

        val animation = it.getCurrentAnimation()
        if (animation.sequence.size!=1 && animation.runAnimation) {
            val d = arrayStringDef[(entity as GameEntity).name]
            d.time += deltaTime

            if(d.time >= animation.timeBetweenFrames) {
                animation.currentFrame ++
                if(animation.currentFrame > animation.sequence.size-1) {
                    if(animation.loopAnimation) {
                        animation.currentFrame = 0
                    }
                    else {
                        animation.runAnimation = false
                        animation.currentFrame--
                        animation.taskEndOfAnimation.invoke()
                    }
                }
                d.time = 0f
            }
        }
        entity.create<CDrawable> {
            texture = getFrame(entity as GameEntity)
            tint = CTextureRegion[entity].color
        }

    }

    private fun getFrame(e: GameEntity) =
            if(arrayStringDef[e.name]!=null) {
                val anim = CTextureRegionAnimation[e].getCurrentAnimation()
                arrayStringDef[e.name].textureFrames.get(anim.sequence[anim.currentFrame])
            }
            else
                CTextureRegion[e].texture
}