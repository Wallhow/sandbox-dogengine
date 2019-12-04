package sandbox

import com.anupcowkur.statelin.TriggerHandler
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.*
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.google.inject.Injector
import dogengine.ashley.components.*
import dogengine.ashley.components.draw.*
import dogengine.ashley.components.tilemap.CTiledMapOrtho
import dogengine.ashley.systems.STiledMapOrtho
import dogengine.ashley.systems.controllers.CPlayerController
import dogengine.ashley.systems.controllers.ControllerListener
import dogengine.ashley.systems.draw.SDrawable
import dogengine.def.GameEntity
import dogengine.utils.Size
import dogengine.utils.vec2
import sandbox.def.CJBumpAABB
import sandbox.def.SJBumpAABB

class MainScreen(val injector: Injector) : ScreenAdapter() {
    val camera: OrthographicCamera = injector.getInstance(OrthographicCamera::class.java)
    val sb = injector.getInstance(SpriteBatch::class.java)
    val engine: Engine = injector.getInstance(Engine::class.java)

    val mapEntity = MapEntity()

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0.5f, 0.5f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        engine.update(delta)
    }

    override fun show() {
        val engine = injector.getInstance(Engine::class.java)
        val am = injector.getInstance(AssetManager::class.java)
        am.load(Gdx.files.internal("assets/atlas/matlas.atlas").path(), TextureAtlas::class.java)
        am.finishLoadingAsset<TextureAtlas>(Gdx.files.internal("assets/atlas/matlas.atlas").path())
        engine.addEntity(mapEntity)
        engine.addEntity(Pety1(am))
        camera.zoom = .7f

        (engine.getSystem(STiledMapOrtho::class.java)).customCreateObject = {
            object : GameEntity() {
                init {
                    name = it.name
                    add(CTransforms(Vector2(it.positionObj), it.sizeObj))
                    add(CTextureRegion(it.textureRegionObj))

                    //TODO Временно
                    if (it.type == "collide") {
                        println(name)
                        add(CJBumpAABB(false).apply { scaleSize.x = 0.2f; scaleSize.y = 0.59f })
                    }
                }
            }
        }

        val inputAdapter = object : InputAdapter() {
            override fun keyDown(keycode: Int): Boolean {
                if(keycode == Input.Keys.SPACE) {
                    engine.getSystem(SDrawable::class.java).drawInFBO = !engine.getSystem(SDrawable::class.java).drawInFBO
                }
                return super.keyDown(keycode)
            }
        }

        injector.getInstance(InputMultiplexer::class.java).addProcessor(inputAdapter)
    }
}

class MapEntity : GameEntity() {

    init {
        name = "map"
        add(CTiledMapOrtho("assets/map/map.tmx"))
    }

}

class Pety1(val am: AssetManager) : GameEntity(), ControllerListener {

    init {
        name = "player"
        add(CTransforms(300f, 100f, Size(64f, 48f)))
        val atlas = am.get("assets/atlas/matlas.atlas", TextureAtlas::class.java)
        add(CAtlasRegion(atlas, "knight"))
        add(CAtlasRegionAnimation().apply {
            createSequence(T.W_NON.ordinal, 0.4f) { isRepeat = true; putFrames(intArrayOf(1, 5)) }
            createSequence(T.W_DOWN.ordinal, 0.15f) { isRepeat = true; putFrames(intArrayOf(1, 2, 3, 4, 5)) }
            createSequence(T.W_UP.ordinal, 0.15f) { isRepeat = true; putFrames(intArrayOf(6, 7, 8, 9, 10)) }
            createSequence(T.W_LEFT.ordinal, 0.15f) { isRepeat = true; putFrames(intArrayOf(11, 12, 13, 14)) }
            createSequence(T.W_RIGHT.ordinal, 0.15f) { isRepeat = true; putFrames(intArrayOf(16, 17, 18, 19, 20)) }
            currentSequence(T.W_NON.ordinal)
        })
        add(createState())
        add(CName("player"))

        add(CCameraLook())
        add(CPlayerController(this))
        add(CVelocity(vec2(0f, 0f)))
        add(CJBumpAABB(true).apply { scaleSize.x = 0.3f ; scaleSize.y = 0.6f })
    }

    private fun createState(): CStateMachine {
        val csMachine = CStateMachine()
        csMachine.apply {
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
            println(T.W_NON.name)


            initMachine(sWalkNon)
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(T.W_RIGHT.name)) { setState(T.W_RIGHT.name) })
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(T.W_LEFT.name)) { setState(T.W_LEFT.name) })
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(T.W_UP.name)) { setState(T.W_UP.name) })
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(T.W_DOWN.name)) { setState(T.W_DOWN.name) })

            addTriggerHandler(TriggerHandler(sWalkLeft, getTrigger(T.W_RIGHT.name)) { setState(T.W_RIGHT.name) })
            addTriggerHandler(TriggerHandler(sWalkLeft, addTrigger(T.W_NON.name)) { setState(T.W_NON.name) })
            addTriggerHandler(TriggerHandler(sWalkLeft, getTrigger(T.W_UP.name)) { setState(T.W_UP.name) })
            addTriggerHandler(TriggerHandler(sWalkLeft, getTrigger(T.W_DOWN.name)) { setState(T.W_DOWN.name) })

            addTriggerHandler(TriggerHandler(sWalkRight, getTrigger(T.W_NON.name)) { setState(T.W_NON.name) })
            addTriggerHandler(TriggerHandler(sWalkRight, getTrigger(T.W_LEFT.name)) { setState(T.W_LEFT.name) })
            addTriggerHandler(TriggerHandler(sWalkRight, getTrigger(T.W_UP.name)) { setState(T.W_UP.name) })
            addTriggerHandler(TriggerHandler(sWalkRight, getTrigger(T.W_DOWN.name)) { setState(T.W_DOWN.name) })

            addTriggerHandler(TriggerHandler(sWalkDown, getTrigger(T.W_RIGHT.name)) { setState(T.W_RIGHT.name) })
            addTriggerHandler(TriggerHandler(sWalkDown, getTrigger(T.W_LEFT.name)) { setState(T.W_LEFT.name) })
            addTriggerHandler(TriggerHandler(sWalkDown, getTrigger(T.W_UP.name)) { setState(T.W_UP.name) })
            addTriggerHandler(TriggerHandler(sWalkDown, getTrigger(T.W_NON.name)) { setState(T.W_NON.name) })

            addTriggerHandler(TriggerHandler(sWalkUp, getTrigger(T.W_RIGHT.name)) { setState(T.W_RIGHT.name) })
            addTriggerHandler(TriggerHandler(sWalkUp, getTrigger(T.W_LEFT.name)) { setState(T.W_LEFT.name) })
            addTriggerHandler(TriggerHandler(sWalkUp, getTrigger(T.W_NON.name)) { setState(T.W_NON.name) })
            addTriggerHandler(TriggerHandler(sWalkUp, getTrigger(T.W_DOWN.name)) { setState(T.W_DOWN.name) })
        }
        return csMachine
    }

    //triggers
    enum class T(i: Int) {
        W_RIGHT(2),
        W_LEFT(1),
        W_UP(3),
        W_DOWN(4),
        W_NON(0)
    }

    override fun up() {
        CStateMachine[this].setTrigger(T.W_UP.name)
        CVelocity[this].vector.y = 60f
    }

    override fun down() {
        CStateMachine[this].setTrigger(T.W_DOWN.name)
        CVelocity[this].vector.y = -60f
    }

    override fun left() {
        CStateMachine[this].setTrigger(T.W_LEFT.name)
        CVelocity[this].vector.x = -60f
    }

    override fun right() {
        CStateMachine[this].setTrigger(T.W_RIGHT.name)
        CVelocity[this].vector.x = 60f
    }

    override fun a() {
    }

    override fun b() {
    }

    override fun c() {
    }

    override fun none(keyCode: Int) {
        CStateMachine[this].setTrigger(T.W_NON.name)
        CVelocity[this].vector.setZero()
    }
}