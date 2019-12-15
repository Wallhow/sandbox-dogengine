package sandbox

import com.anupcowkur.statelin.TriggerHandler
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.*
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.google.inject.Injector
import dogengine.ashley.components.*
import dogengine.ashley.components.draw.*
import dogengine.ashley.systems.STiledMapOrtho
import dogengine.ashley.systems.c
import dogengine.ashley.systems.controllers.CPlayerController
import dogengine.ashley.systems.controllers.ControllerListener
import dogengine.ashley.systems.draw.SDrawDebug
import dogengine.ashley.systems.draw.SDrawable
import dogengine.def.GameEntity
import dogengine.utils.Size
import dogengine.utils.TTFFont
import dogengine.utils.vec2
import sandbox.def.*
import sandbox.sandbox.def.Map2DGenerator
import sandbox.sandbox.def.map2D.CMap2D
import sandbox.sandbox.def.map2D.Map2D

class MainScreen(val injector: Injector) : ScreenAdapter() {
    val camera: OrthographicCamera = injector.getInstance(OrthographicCamera::class.java)
    val sb = injector.getInstance(SpriteBatch::class.java)
    val engine: Engine = injector.getInstance(Engine::class.java)
    var spawn = false
    val mapEntity = MapEntity()
    lateinit var player: Pety1
    var map2d : Map2D? = null

    override fun render(delta: Float) {
        engine.update(delta)
    }

    override fun show() {

        val engine = injector.getInstance(Engine::class.java)
        val am = injector.getInstance(AssetManager::class.java)
        am.load(Gdx.files.internal(R.matlas0).path(), TextureAtlas::class.java)
        am.finishLoadingAsset<TextureAtlas>(Gdx.files.internal(R.matlas0).path())
        engine.addEntity(mapEntity)

        camera.zoom = 0.8f
        player = Pety1(am,Vector2(100f,100f))
        engine.addEntity(player)
        engine.addEntity(PetyBot(am, vec2(400f,1400f)))



        //TODO важный участок отвечающий за спавн игрока, надо продумать..
        (engine.getSystem(STiledMapOrtho::class.java))?.customCreateObject = {
            if(it.name.startsWith("player-spawn")) {
                CTransforms[player].position.set(CTransforms[it].position)
                engine.addEntity(player)
            }
            it
        }

        (engine.getSystem(SJBumpAABB::class.java))?.collisionListener = { e1, e2 ->
            var obj: Entity? = null
            if(CTiledObject[e1.userData]!=null) {
                if(CTiledObject[e1.userData].properties[CTiledObject.Properties.Alert]!= null) {
                    obj = e1.userData
                }
            }
            if(CTiledObject[e2.userData]!=null) {
                if(CTiledObject[e2.userData].properties[CTiledObject.Properties.Alert]!= null) {
                    obj = e2.userData
                }
            }

            if(obj!=null) {
                engine.addEntity(object : GameEntity() {
                    init {
                        add(CTransforms().apply {
                            position = CTransforms[obj!!].position.cpy()
                        })
                        add(CLabel().apply {
                            fnt = TTFFont(R.pixel0).create(18, Color.RED).get(18)
                            labelText = CTiledObject[obj!!].properties[CTiledObject.Properties.Alert]
                        })
                        val time = 6f
                        val d = 1f/time
                        var t=0f
                        add(CUpdateAfterTime() {
                            if(t<=time) {
                                CTransforms[this].position.y += 20 * it
                                CLabel[this].fnt!!.color.a=1-d*t
                            } else {
                                add(CDeleteMe())
                            }
                            t+=it

                        })

                    }
                })
            }

        }

        val inputAdapter = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                val pos = camera.unproject(Vector3(screenX.toFloat(),screenY.toFloat(),0f))
                CTransforms[player].position.set(pos.x,pos.y)
                return true
            }

            var idxLayer = 0
            override fun keyDown(keycode: Int): Boolean {
                if(keycode == Input.Keys.SPACE) {
                    //engine.getSystem(SDrawable::class.java).drawToFBO = !engine.getSystem(SDrawable::class.java).drawToFBO
                    if(CMap2D[mapEntity].map2D!=null) {
                        idxLayer = if(idxLayer==0) 1 else 0
                        CMap2D[mapEntity].currentLayer=idxLayer
                    }
                }
                if(keycode == Input.Keys.Z) {
                    engine.getSystem(SDrawDebug::class.java).visible = !engine.getSystem(SDrawDebug::class.java).visible
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
        //add(CTiledMapOrtho(R.map20))
        /*add(CTiledMapOrtho(null).apply {
            tiledMap = TileMapGenerator().getTileMap()
        })*/
        val gen = Map2DGenerator()
        val map2d = gen.generate()
        val t = Texture(gen.pixmap)
        add(CTextureRegion(TextureRegion(t).apply { flip(true,false) }))
        add(CTransforms().c(vec2(-t.width.toFloat(),0f), Size(t.width.toFloat(),t.height.toFloat())).apply { rotation = 180f })
        add(CMap2D().apply { map2D = map2d })

    }

}

class Pety1(val am: AssetManager,pos: Vector2) : GameEntity(), ControllerListener {

    init {
        name = "player"
        add(CTransforms().apply {
            position.set(pos)
            size = Size(64f, 48f)
        })
        val atlas = am.get("assets/atlas/matlas.atlas", TextureAtlas::class.java)
        add(CAtlasRegion(atlas, "knight").apply { drawLayer =CDrawable.DrawLayer.YES_EFFECT })
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
        add(CJBumpAABB().apply { scaleSize.x = 0.2f ; scaleSize.y = 0.2f ; dynamic= true ; positionOffset.x=32-5f;positionOffset.y = 10f})
        add(CUpdateAfterTime {
            CTransforms[this].updateZIndex()
        })
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
    private val speed = 200f
    override fun up() {
        CStateMachine[this].setTrigger(T.W_UP.name)
        CVelocity[this].vector.y = speed
    }

    override fun down() {
        CStateMachine[this].setTrigger(T.W_DOWN.name)
        CVelocity[this].vector.y = -speed
    }

    override fun left() {
        CStateMachine[this].setTrigger(T.W_LEFT.name)
        CVelocity[this].vector.x = -speed
    }

    override fun right() {
        CStateMachine[this].setTrigger(T.W_RIGHT.name)
        CVelocity[this].vector.x = speed
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

class PetyBot(val am: AssetManager,pos: Vector2) : GameEntity() {

    init {
        name = "player_bot"
        add(CTransforms().apply {
            position.set(pos)
            size = Size(64f*2, 48f*2)
        })
        val atlas = am.get("assets/atlas/matlas.atlas", TextureAtlas::class.java)
        add(CAtlasRegion(atlas, "knight").apply { drawLayer =CDrawable.DrawLayer.YES_EFFECT })
        add(CAtlasRegionAnimation().apply {
            createSequence(T.W_NON.ordinal, 0.4f) { isRepeat = true; putFrames(intArrayOf(1, 5)) }
            createSequence(T.W_DOWN.ordinal, 0.15f) { isRepeat = true; putFrames(intArrayOf(1, 2, 3, 4, 5)) }
            createSequence(T.W_UP.ordinal, 0.15f) { isRepeat = true; putFrames(intArrayOf(6, 7, 8, 9, 10)) }
            createSequence(T.W_LEFT.ordinal, 0.15f) { isRepeat = true; putFrames(intArrayOf(11, 12, 13, 14)) }
            createSequence(T.W_RIGHT.ordinal, 0.15f) { isRepeat = true; putFrames(intArrayOf(16, 17, 18, 19, 20)) }
            currentSequence(T.W_NON.ordinal)
        })
        add(createState())
        add(CName("player_bot"))

        add(CJBumpAABB().apply { scaleSize.x = 0.2f ; scaleSize.y = 0.2f ; dynamic= true ; positionOffset.x=32-5f;positionOffset.y = 10f})
        add(CUpdateAfterTime {
            CTransforms[this].updateZIndex()
        })
    }

    private fun createState(): CStateMachine {
        val csMachine = CStateMachine()
        csMachine.apply {
            val sWalkRight = createState(T.W_RIGHT.name) {
                CAtlasRegionAnimation[this@PetyBot].currentSequence(T.W_RIGHT.ordinal)
            }
            val sWalkLeft = createState(T.W_LEFT.name) {
                CAtlasRegionAnimation[this@PetyBot].currentSequence(T.W_LEFT.ordinal)
            }
            val sWalkUp = createState(T.W_UP.name) {
                CAtlasRegionAnimation[this@PetyBot].currentSequence(T.W_UP.ordinal)
            }
            val sWalkDown = createState(T.W_DOWN.name) {
                CAtlasRegionAnimation[this@PetyBot].currentSequence(T.W_DOWN.ordinal)
            }
            val sWalkNon = createState(T.W_NON.name) {
                CAtlasRegionAnimation[this@PetyBot].currentSequence(T.W_NON.ordinal)
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

}