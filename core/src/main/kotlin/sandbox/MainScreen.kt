package sandbox

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.*
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.google.inject.Injector
import dogengine.ecs.components.components
import dogengine.ecs.components.create
import dogengine.ecs.components.createEntity
import dogengine.ecs.components.draw.CTextureRegion
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.systems.draw.SDrawDebug
import dogengine.ecs.systems.physics.SDefaultPhysics2d
import dogengine.ecs.systems.tilemap.CMap2D
import dogengine.ecs.systems.tilemap.SMap2D
import dogengine.es.redkin.physicsengine2d.world.World
import dogengine.particles2d.EffectsManager
import dogengine.utils.Size
import dogengine.utils.log
import dogengine.utils.system
import dogengine.utils.vec2
import sandbox.go.environment.drop.models.GrassDrop
import sandbox.go.environment.drop.models.RockDrop
import sandbox.go.environment.drop.models.WoodDrop
import sandbox.sandbox.def.CreatedCellMapListener
import sandbox.sandbox.def.Map2DGenerator
import sandbox.sandbox.def.SGuiDraw
import sandbox.sandbox.def.def.sys.SDrop
import sandbox.sandbox.go.environment.drop.models.SandstoneDrop
import sandbox.sandbox.go.environment.models.Rock
import sandbox.sandbox.go.environment.models.Wood
import sandbox.sandbox.go.player.Player
import sandbox.sandbox.go.player.PlayerToolsListener

class MainScreen(private val injector: Injector) : ScreenAdapter() {
    private val batch: SpriteBatch = injector.getInstance(SpriteBatch::class.java)
    val camera: OrthographicCamera = injector.getInstance(OrthographicCamera::class.java)
    val engine: Engine = injector.getInstance(Engine::class.java)
    lateinit var player: Player
    private val tilesSize = 32f
    private lateinit var ef: EffectsManager

    override fun render(delta: Float) {
        engine.update(delta)
        ef.update(delta)
        batch.begin()
        ef.draw(batch)
        batch.end()
    }

    override fun show() {
        val am = injector.getInstance(AssetManager::class.java)
        am.load(Gdx.files.internal(R.matlas0).path(), TextureAtlas::class.java)
        am.finishLoadingAsset<TextureAtlas>(Gdx.files.internal(R.matlas0).path())
        player = Player(am, Vector2(100f, 100f))
        camera.zoom = 0.8f

        ef = injector.getInstance(EffectsManager::class.java)
        am.load(Gdx.files.internal(R.dot_particles0).path(),ParticleEffect::class.java)
        am.finishLoadingAsset<ParticleEffect>(Gdx.files.internal(R.dot_particles0).path())
        ef.createEffect(1,am[Gdx.files.internal(R.dot_particles0).path()])


        engine.addEntity(createMapEntity(tilesSize.toInt()))
        engine.addEntity(player)
        engine.addEntity(Wood(Vector2(100f, 200f)))
        engine.addEntity(Wood(Vector2(250f, 210f)))
        engine.addEntity(Wood(Vector2(350f, 220f)))
        engine.addEntity(Wood(Vector2(450f, 190f)))

        engine.addEntity(Rock(Vector2(470f, 590f)))
        engine.addEntity(Rock(Vector2(350f, 690f)))

        engine.addSystem(SGuiDraw(player))
        engine.addSystem(SDrop(player))


        system<SMap2D> {
            tileSize.set(tilesSize, tilesSize)
        }
        system<SDrawDebug> {
            customDebug = {
                injector.getInstance(World::class.java).drawDebugWorld(camera,it)
            }
        }
        system<SDefaultPhysics2d> {
            this.world.addContactListener(PlayerToolsListener(player))
        }

        val inputAdapter = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                val pos = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
                when (MathUtils.random(4)) {
                  0-> {engine.addEntity(SandstoneDrop(Vector2(pos.x,pos.y),pos.y))}
                  1-> {engine.addEntity(WoodDrop(Vector2(pos.x,pos.y),pos.y))}
                  2-> {engine.addEntity(GrassDrop(Vector2(pos.x,pos.y),pos.y))}
                  3-> {engine.addEntity(RockDrop(Vector2(pos.x,pos.y),pos.y))}
                }
                return true
            }

            var idxLayer = 0
            override fun keyDown(keycode: Int): Boolean {
                if (keycode == Input.Keys.Z) {
                    system<SDrawDebug> {
                        log("debug info drawing")
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
                val gen = Map2DGenerator(toInt, CreatedCellMapListener(toInt*1f))
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
                    angle = 180f
                }
                create<CMap2D> {
                    map2D = map2d
                }
            }
        }
    }
}

