package sandbox.sandbox.go

import com.anupcowkur.statelin.TriggerHandler
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.create
import dogengine.ecs.components.draw.*
import dogengine.ecs.def.GameEntity
import dogengine.utils.Size
import sandbox.R
import sandbox.dogengine.ashley.components.utility.CName
import sandbox.dogengine.ashley.components.utility.CStateMachine
import sandbox.dogengine.ecs.components.utility.logic.CTransforms
import sandbox.dogengine.ecs.components.utility.logic.updateZIndex
import sandbox.sandbox.def.jbump.CJBumpAABB

class Bot(val am: AssetManager, pos: Vector2) : GameEntity() {

    init {
        name = "player_bot"

        create<CTransforms> {
            position.set(pos)
            size = Size(64f, 48f)
            updateZIndex()
        }
        create<CAtlasRegion> {
            atlas = am.get(R.matlas0, TextureAtlas::class.java)
            nameRegion = "knight"
            drawLayer = CDrawable.DrawLayer.YES_EFFECT
        }
        create<CAtlasRegionAnimation> {
            createSequence(T.W_NON.ordinal, 0.4f) { isRepeat = true; putFrames(intArrayOf(1, 5)) }
            createSequence(T.W_DOWN.ordinal, 0.15f) { isRepeat = true; putFrames(intArrayOf(1, 2, 3, 4, 5)) }
            createSequence(T.W_UP.ordinal, 0.15f) { isRepeat = true; putFrames(intArrayOf(6, 7, 8, 9, 10)) }
            createSequence(T.W_LEFT.ordinal, 0.15f) { isRepeat = true; putFrames(intArrayOf(11, 12, 13, 14)) }
            createSequence(T.W_RIGHT.ordinal, 0.15f) { isRepeat = true; putFrames(intArrayOf(16, 17, 18, 19, 20)) }
            currentSequence(T.W_NON.ordinal)
        }
        create<CName> {
            name = "player_bot"
        }
        create<CJBumpAABB> {
            scaleSize.x = 0.2f; scaleSize.y = 0.2f; dynamic = true; positionOffset.x = 32 - 5f;positionOffset.y = 10f
        }

        create<CTransforms> {
            position.set(pos)
            size = Size(64f * 2, 48f * 2)
        }
        create<CStateMachine> { createState(this) }
    }

    private fun createState(component: CStateMachine) {
        component.apply {
            val sWalkRight = createState(Player.T.W_RIGHT.name) {
                CAtlasRegionAnimation[this@Bot].currentSequence(Player.T.W_RIGHT.ordinal)
            }
            val sWalkLeft = createState(Player.T.W_LEFT.name) {
                CAtlasRegionAnimation[this@Bot].currentSequence(Player.T.W_LEFT.ordinal)
            }
            val sWalkUp = createState(Player.T.W_UP.name) {
                CAtlasRegionAnimation[this@Bot].currentSequence(Player.T.W_UP.ordinal)
            }
            val sWalkDown = createState(Player.T.W_DOWN.name) {
                CAtlasRegionAnimation[this@Bot].currentSequence(Player.T.W_DOWN.ordinal)
            }
            val sWalkNon = createState(Player.T.W_NON.name) {
                CAtlasRegionAnimation[this@Bot].currentSequence(Player.T.W_NON.ordinal)
            }
            initMachine(sWalkNon)
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(Player.T.W_RIGHT.name.hashCode())) { setState(Player.T.W_RIGHT.name) })
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(Player.T.W_LEFT.name.hashCode())) { setState(Player.T.W_LEFT.name) })
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(Player.T.W_UP.name.hashCode())) { setState(Player.T.W_UP.name) })
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(Player.T.W_DOWN.name.hashCode())) { setState(Player.T.W_DOWN.name) })

            addTriggerHandler(TriggerHandler(sWalkLeft, getTrigger(Player.T.W_RIGHT.name.hashCode())) { setState(Player.T.W_RIGHT.name) })
            addTriggerHandler(TriggerHandler(sWalkLeft, addTrigger(Player.T.W_NON.name.hashCode())) { setState(Player.T.W_NON.name) })
            addTriggerHandler(TriggerHandler(sWalkLeft, getTrigger(Player.T.W_UP.name.hashCode())) { setState(Player.T.W_UP.name) })
            addTriggerHandler(TriggerHandler(sWalkLeft, getTrigger(Player.T.W_DOWN.name.hashCode())) { setState(Player.T.W_DOWN.name) })

            addTriggerHandler(TriggerHandler(sWalkRight, getTrigger(Player.T.W_NON.name.hashCode())) { setState(Player.T.W_NON.name) })
            addTriggerHandler(TriggerHandler(sWalkRight, getTrigger(Player.T.W_LEFT.name.hashCode())) { setState(Player.T.W_LEFT.name) })
            addTriggerHandler(TriggerHandler(sWalkRight, getTrigger(Player.T.W_UP.name.hashCode())) { setState(Player.T.W_UP.name) })
            addTriggerHandler(TriggerHandler(sWalkRight, getTrigger(Player.T.W_DOWN.name.hashCode())) { setState(Player.T.W_DOWN.name) })

            addTriggerHandler(TriggerHandler(sWalkDown, getTrigger(Player.T.W_RIGHT.name.hashCode())) { setState(Player.T.W_RIGHT.name) })
            addTriggerHandler(TriggerHandler(sWalkDown, getTrigger(Player.T.W_LEFT.name.hashCode())) { setState(Player.T.W_LEFT.name) })
            addTriggerHandler(TriggerHandler(sWalkDown, getTrigger(Player.T.W_UP.name.hashCode())) { setState(Player.T.W_UP.name) })
            addTriggerHandler(TriggerHandler(sWalkDown, getTrigger(Player.T.W_NON.name.hashCode())) { setState(Player.T.W_NON.name) })

            addTriggerHandler(TriggerHandler(sWalkUp, getTrigger(Player.T.W_RIGHT.name.hashCode())) { setState(Player.T.W_RIGHT.name) })
            addTriggerHandler(TriggerHandler(sWalkUp, getTrigger(Player.T.W_LEFT.name.hashCode())) { setState(Player.T.W_LEFT.name) })
            addTriggerHandler(TriggerHandler(sWalkUp, getTrigger(Player.T.W_NON.name.hashCode())) { setState(Player.T.W_NON.name) })
            addTriggerHandler(TriggerHandler(sWalkUp, getTrigger(Player.T.W_DOWN.name.hashCode())) { setState(Player.T.W_DOWN.name) })
        }
    }

    //triggers
    enum class T(i: Int) {
        W_RIGHT(2),
        W_LEFT(1),
        W_UP(3),
        W_DOWN(4),
        W_NON(0)
    }

}