package sandbox

import com.anupcowkur.statelin.TriggerHandler
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.*
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.google.inject.Injector
import dogengine.ecs.components.draw.*
import dogengine.ecs.def.GameEntity
import dogengine.ecs.systems.controllers.CPlayerController
import dogengine.ecs.systems.controllers.ControllerListener
import dogengine.ecs.systems.draw.SDrawDebug
import dogengine.utils.Size
import dogengine.utils.system
import dogengine.utils.vec2
import sandbox.def.CJBumpAABB
import sandbox.dogengine.ashley.components.components
import sandbox.dogengine.ashley.components.create
import sandbox.dogengine.ashley.components.createEntity
import sandbox.dogengine.ashley.components.utility.*
import sandbox.dogengine.ecs.components.utility.logic.CTransforms
import sandbox.dogengine.ecs.components.utility.logic.updateZIndex
import sandbox.sandbox.def.Map2DGenerator
import sandbox.sandbox.def.map2D.CMap2D
import sandbox.sandbox.def.map2D.SMap2D

class MainScreen(val injector: Injector) : ScreenAdapter() {
    val camera: OrthographicCamera = injector.getInstance(OrthographicCamera::class.java)
    val engine: Engine = injector.getInstance(Engine::class.java)
    lateinit var player: Pety1

    private val tilesSize = 36f
    //val mapEntity = MapEntity(tilesSize.toInt())


    override fun render(delta: Float) {
        engine.update(delta)
    }

    override fun show() {
        val engine = injector.getInstance(Engine::class.java)
        val am = injector.getInstance(AssetManager::class.java)
        am.load(Gdx.files.internal(R.matlas0).path(), TextureAtlas::class.java)
        am.finishLoadingAsset<TextureAtlas>(Gdx.files.internal(R.matlas0).path())
        engine.addEntity(createMapEntity(tilesSize.toInt()))

        camera.zoom = 0.8f
        player = Pety1(am, Vector2(100f, 100f))
        engine.addEntity(player)
        engine.addEntity(PetyBot(am, vec2(400f, 1400f)))


        system<SMap2D> {
            tileSize.set(tilesSize, tilesSize)
        }

        val inputAdapter = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                val pos = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
                CTransforms[player].position.set(pos.x, pos.y)
                return true
            }

            var idxLayer = 0
            override fun keyDown(keycode: Int): Boolean {
                if (keycode == Input.Keys.SPACE) {
                    //engine.getSystem(SDrawable::class.java).drawToFBO = !engine.getSystem(SDrawable::class.java).drawToFBO
                    /*if(CMap2D[mapEntity].map2D!=null) {
                        idxLayer = if(idxLayer==0) 1 else 0
                        CMap2D[mapEntity].currentLayer=idxLayer
                    }*/
                }
                if (keycode == Input.Keys.Z) {
                    system<SDrawDebug> { this.visible =!visible }
                }
                return super.keyDown(keycode)
            }
        }

        injector.getInstance(InputMultiplexer::class.java).addProcessor(inputAdapter)
    }

    private fun createMapEntity(toInt: Int): Entity {
        return engine.createEntity {
            components {
                val gen = Map2DGenerator(toInt)
                val map2d = gen.generate()
                val t = Texture(gen.pixmap)
                val scale = 4
                create<CTextureRegion> {
                    texture = TextureRegion(t)
                    texture?.flip(true, false)
                }
                create<CTransforms> {
                    position = vec2(-t.width.toFloat() * scale, 0f)
                    size = Size(t.width.toFloat() * scale, t.height.toFloat() * scale)
                    rotation = 180f
                }
                create<CMap2D> {
                    map2D = map2d
                }
            }
        }
    }


}

class Pety1(val am: AssetManager, pos: Vector2) : GameEntity(), ControllerListener {

    init {
        name = "player"


        create<CTransforms> {
            position.set(pos)
            size = Size(64f, 48f)
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
            name = "player"
        }

        create<CCameraLook> {}
        create<CPlayerController> { controllerListener = this@Pety1 }
        create<CVelocity> {
            vector.set(0f, 0f)
        }
        create<CJBumpAABB> {
            scaleSize.x = 0.2f
            scaleSize.y = 0.2f
            dynamic = true
            positionOffset.x = 32 - 5f
            positionOffset.y = 10f
        }
        create<CUpdate> {
            CTransforms[this@Pety1].updateZIndex()
        }

        create<CStateMachine> { createState(this) }

        //randomGenerate(128)

    }

    private fun randomGenerate(num: Int) {
        for (i in 0..num) {
            val pos = Vector2(MathUtils.random(0, 300) * 1f, MathUtils.random(0, 400) * 1f)
            engine.addEntity(engine.createEntity {
                components {
                    create<CTransforms> {
                        position.set(pos)
                        size = Size(64f, 48f)
                    }
                    create<CAtlasRegion> {
                        atlas = am.get("assets/atlas/matlas.atlas", TextureAtlas::class.java)
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
                        name = "player"
                    }
                    create<CJBumpAABB> {
                        scaleSize.x = 0.2f
                        scaleSize.y = 0.2f
                        dynamic = true
                        positionOffset.x = 32 - 5f
                        positionOffset.y = 10f
                    }
                }
            })
        }

    }

    private fun createState(component: CStateMachine) {
        component.apply {
            val sWalkRight = createState(T.W_RIGHT.name) {
                CAtlasRegionAnimation[this@Pety1].currentSequence(T.W_RIGHT.ordinal)
            }
            val sWalkLeft = createState(T.W_LEFT.name) {
                CAtlasRegionAnimation[this@Pety1].currentSequence(T.W_LEFT.ordinal)
            }
            val sWalkUp = createState(T.W_UP.name) {
                CAtlasRegionAnimation[this@Pety1].currentSequence(T.W_UP.ordinal)
            }
            val sWalkDown = createState(T.W_DOWN.name) {
                CAtlasRegionAnimation[this@Pety1].currentSequence(T.W_DOWN.ordinal)
            }
            val sWalkNon = createState(T.W_NON.name) {
                CAtlasRegionAnimation[this@Pety1].currentSequence(T.W_NON.ordinal)
            }
            initMachine(sWalkNon)
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(T.W_RIGHT.name.hashCode())) { setState(T.W_RIGHT.name) })
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(T.W_LEFT.name.hashCode())) { setState(T.W_LEFT.name) })
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(T.W_UP.name.hashCode())) { setState(T.W_UP.name) })
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(T.W_DOWN.name.hashCode())) { setState(T.W_DOWN.name) })

            addTriggerHandler(TriggerHandler(sWalkLeft, getTrigger(T.W_RIGHT.name.hashCode())) { setState(T.W_RIGHT.name) })
            addTriggerHandler(TriggerHandler(sWalkLeft, addTrigger(T.W_NON.name.hashCode())) { setState(T.W_NON.name) })
            addTriggerHandler(TriggerHandler(sWalkLeft, getTrigger(T.W_UP.name.hashCode())) { setState(T.W_UP.name) })
            addTriggerHandler(TriggerHandler(sWalkLeft, getTrigger(T.W_DOWN.name.hashCode())) { setState(T.W_DOWN.name) })

            addTriggerHandler(TriggerHandler(sWalkRight, getTrigger(T.W_NON.name.hashCode())) { setState(T.W_NON.name) })
            addTriggerHandler(TriggerHandler(sWalkRight, getTrigger(T.W_LEFT.name.hashCode())) { setState(T.W_LEFT.name) })
            addTriggerHandler(TriggerHandler(sWalkRight, getTrigger(T.W_UP.name.hashCode())) { setState(T.W_UP.name) })
            addTriggerHandler(TriggerHandler(sWalkRight, getTrigger(T.W_DOWN.name.hashCode())) { setState(T.W_DOWN.name) })

            addTriggerHandler(TriggerHandler(sWalkDown, getTrigger(T.W_RIGHT.name.hashCode())) { setState(T.W_RIGHT.name) })
            addTriggerHandler(TriggerHandler(sWalkDown, getTrigger(T.W_LEFT.name.hashCode())) { setState(T.W_LEFT.name) })
            addTriggerHandler(TriggerHandler(sWalkDown, getTrigger(T.W_UP.name.hashCode())) { setState(T.W_UP.name) })
            addTriggerHandler(TriggerHandler(sWalkDown, getTrigger(T.W_NON.name.hashCode())) { setState(T.W_NON.name) })

            addTriggerHandler(TriggerHandler(sWalkUp, getTrigger(T.W_RIGHT.name.hashCode())) { setState(T.W_RIGHT.name) })
            addTriggerHandler(TriggerHandler(sWalkUp, getTrigger(T.W_LEFT.name.hashCode())) { setState(T.W_LEFT.name) })
            addTriggerHandler(TriggerHandler(sWalkUp, getTrigger(T.W_NON.name.hashCode())) { setState(T.W_NON.name) })
            addTriggerHandler(TriggerHandler(sWalkUp, getTrigger(T.W_DOWN.name.hashCode())) { setState(T.W_DOWN.name) })
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

    private val speed = 200f
    override fun up() {
        CStateMachine[this@Pety1].setTrigger(T.W_UP.name.hashCode())
        CVelocity[this@Pety1].vector.y = speed
    }

    override fun down() {
        CStateMachine[this@Pety1].setTrigger(T.W_DOWN.name.hashCode())
        CVelocity[this@Pety1].vector.y = -speed
    }

    override fun left() {
        CStateMachine[this@Pety1].setTrigger(T.W_LEFT.name.hashCode())
        CVelocity[this@Pety1].vector.x = -speed
    }

    override fun right() {
        CStateMachine[this@Pety1].setTrigger(T.W_RIGHT.name.hashCode())
        CVelocity[this@Pety1].vector.x = speed
    }

    override fun a() {
    }

    override fun b() {
    }

    override fun c() {
    }

    override fun none(keyCode: Int) {
        CStateMachine[this@Pety1].setTrigger(T.W_NON.name.hashCode())
        CVelocity[this@Pety1].vector.setZero()
    }
}

class PetyBot(val am: AssetManager, pos: Vector2) : GameEntity() {

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
            val sWalkRight = createState(Pety1.T.W_RIGHT.name) {
                CAtlasRegionAnimation[this@PetyBot].currentSequence(Pety1.T.W_RIGHT.ordinal)
            }
            val sWalkLeft = createState(Pety1.T.W_LEFT.name) {
                CAtlasRegionAnimation[this@PetyBot].currentSequence(Pety1.T.W_LEFT.ordinal)
            }
            val sWalkUp = createState(Pety1.T.W_UP.name) {
                CAtlasRegionAnimation[this@PetyBot].currentSequence(Pety1.T.W_UP.ordinal)
            }
            val sWalkDown = createState(Pety1.T.W_DOWN.name) {
                CAtlasRegionAnimation[this@PetyBot].currentSequence(Pety1.T.W_DOWN.ordinal)
            }
            val sWalkNon = createState(Pety1.T.W_NON.name) {
                CAtlasRegionAnimation[this@PetyBot].currentSequence(Pety1.T.W_NON.ordinal)
            }
            initMachine(sWalkNon)
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(Pety1.T.W_RIGHT.name.hashCode())) { setState(Pety1.T.W_RIGHT.name) })
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(Pety1.T.W_LEFT.name.hashCode())) { setState(Pety1.T.W_LEFT.name) })
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(Pety1.T.W_UP.name.hashCode())) { setState(Pety1.T.W_UP.name) })
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(Pety1.T.W_DOWN.name.hashCode())) { setState(Pety1.T.W_DOWN.name) })

            addTriggerHandler(TriggerHandler(sWalkLeft, getTrigger(Pety1.T.W_RIGHT.name.hashCode())) { setState(Pety1.T.W_RIGHT.name) })
            addTriggerHandler(TriggerHandler(sWalkLeft, addTrigger(Pety1.T.W_NON.name.hashCode())) { setState(Pety1.T.W_NON.name) })
            addTriggerHandler(TriggerHandler(sWalkLeft, getTrigger(Pety1.T.W_UP.name.hashCode())) { setState(Pety1.T.W_UP.name) })
            addTriggerHandler(TriggerHandler(sWalkLeft, getTrigger(Pety1.T.W_DOWN.name.hashCode())) { setState(Pety1.T.W_DOWN.name) })

            addTriggerHandler(TriggerHandler(sWalkRight, getTrigger(Pety1.T.W_NON.name.hashCode())) { setState(Pety1.T.W_NON.name) })
            addTriggerHandler(TriggerHandler(sWalkRight, getTrigger(Pety1.T.W_LEFT.name.hashCode())) { setState(Pety1.T.W_LEFT.name) })
            addTriggerHandler(TriggerHandler(sWalkRight, getTrigger(Pety1.T.W_UP.name.hashCode())) { setState(Pety1.T.W_UP.name) })
            addTriggerHandler(TriggerHandler(sWalkRight, getTrigger(Pety1.T.W_DOWN.name.hashCode())) { setState(Pety1.T.W_DOWN.name) })

            addTriggerHandler(TriggerHandler(sWalkDown, getTrigger(Pety1.T.W_RIGHT.name.hashCode())) { setState(Pety1.T.W_RIGHT.name) })
            addTriggerHandler(TriggerHandler(sWalkDown, getTrigger(Pety1.T.W_LEFT.name.hashCode())) { setState(Pety1.T.W_LEFT.name) })
            addTriggerHandler(TriggerHandler(sWalkDown, getTrigger(Pety1.T.W_UP.name.hashCode())) { setState(Pety1.T.W_UP.name) })
            addTriggerHandler(TriggerHandler(sWalkDown, getTrigger(Pety1.T.W_NON.name.hashCode())) { setState(Pety1.T.W_NON.name) })

            addTriggerHandler(TriggerHandler(sWalkUp, getTrigger(Pety1.T.W_RIGHT.name.hashCode())) { setState(Pety1.T.W_RIGHT.name) })
            addTriggerHandler(TriggerHandler(sWalkUp, getTrigger(Pety1.T.W_LEFT.name.hashCode())) { setState(Pety1.T.W_LEFT.name) })
            addTriggerHandler(TriggerHandler(sWalkUp, getTrigger(Pety1.T.W_NON.name.hashCode())) { setState(Pety1.T.W_NON.name) })
            addTriggerHandler(TriggerHandler(sWalkUp, getTrigger(Pety1.T.W_DOWN.name.hashCode())) { setState(Pety1.T.W_DOWN.name) })
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