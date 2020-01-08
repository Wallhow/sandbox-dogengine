package sandbox

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.*
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.google.inject.Injector
import dogengine.ecs.components.components
import dogengine.ecs.components.create
import dogengine.ecs.components.createEntity
import dogengine.ecs.components.draw.CTextureRegion
import dogengine.ecs.systems.draw.SDrawDebug
import dogengine.es.redkin.physicsengine2d.world.World
import dogengine.utils.Size
import dogengine.utils.system
import dogengine.utils.vec2
import sandbox.dogengine.ecs.components.utility.logic.CTransforms
import sandbox.sandbox.def.Map2DGenerator
import sandbox.sandbox.def.map2D.CMap2D
import sandbox.sandbox.def.map2D.SMap2D
import sandbox.sandbox.go.Bot
import sandbox.sandbox.go.Player

class MainScreen(val injector: Injector) : ScreenAdapter() {
    val camera: OrthographicCamera = injector.getInstance(OrthographicCamera::class.java)
    val engine: Engine = injector.getInstance(Engine::class.java)
    lateinit var player: Player

    private val tilesSize = 32f
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
        player = Player(am, Vector2(100f, 100f))
        engine.addEntity(player)
        engine.addEntity(Bot(am, vec2(400f, 1400f)))


        system<SMap2D> {
            tileSize.set(tilesSize, tilesSize)
        }
        system<SDrawDebug> {
            customDebug = {
                injector.getInstance(World::class.java).drawDebugWorld(camera,it)
            }
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
                    system<SDrawDebug> {
                        println("debug info drawing")
                        this.visible =!visible }
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

