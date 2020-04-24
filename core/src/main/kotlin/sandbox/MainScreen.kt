package sandbox

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.*
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import com.google.inject.Injector
import dogengine.ecs.components.components
import dogengine.ecs.components.create
import dogengine.ecs.components.createEntity
import dogengine.ecs.components.draw.CTextureRegion
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CHide
import dogengine.ecs.systems.draw.SDrawDebug20
import dogengine.ecs.systems.physics.SDefaultPhysics2d
import dogengine.ecs.systems.tilemap.CMap2D
import dogengine.ecs.systems.tilemap.SMap2D
import dogengine.redkin.physicsengine2d.world.World
import dogengine.particles2d.EffectsManager
import dogengine.utils.Size
import dogengine.utils.system
import dogengine.utils.vec2
import sandbox.sandbox.def.map.CreatedCellMapListener
import sandbox.sandbox.def.map.Map2DGenerator
import sandbox.sandbox.def.gui.SMainGUI
import sandbox.sandbox.def.def.particles.EmitterManager
import sandbox.sandbox.def.def.sys.SDropUpdate
import sandbox.go.environment.objects.Rock
import sandbox.go.environment.objects.Wood
import sandbox.go.environment.objects.buiding.Workbench
import sandbox.sandbox.def.gui.DebugGUI
import sandbox.sandbox.def.def.comp.CNearbyObject
import sandbox.sandbox.def.def.sys.STools
import sandbox.sandbox.def.def.sys.SWorkbenchDetected
import sandbox.sandbox.go.environment.objects.buiding.Bonfire
import sandbox.sandbox.go.environment.objects.buiding.CWorkbench
import sandbox.sandbox.go.player.Player
import sandbox.sandbox.go.player.Player.DirectionSee.*
import sandbox.sandbox.go.player.PlayerToolsListener
import sandbox.sandbox.input.MainInput

class MainScreen(private val injector: Injector) : ScreenAdapter() {
    private val batch: SpriteBatch = injector.getInstance(SpriteBatch::class.java)
    val camera: OrthographicCamera = injector.getInstance(OrthographicCamera::class.java)
    val engine: Engine = injector.getInstance(Engine::class.java)
    lateinit var player: Player
    private val tilesSize = 32f
    private lateinit var ef: EffectsManager
    private val eManager = injector.getInstance(EmitterManager::class.java)

    //ДЕБАГ ТАБЛО
    private val debugGui = DebugGUI()

    override fun render(delta: Float) {
        engine.update(delta)
        eManager.update(delta)
        ef.update(delta)
        batch.begin()
        ef.draw(batch)
        eManager.draw()
        batch.end()

        debugGui.updateAndDraw(delta)
    }

    override fun show() {
        val am = injector.getInstance(AssetManager::class.java)
        am.load(Gdx.files.internal(R.matlas0).path(), TextureAtlas::class.java)
        am.finishLoadingAsset<TextureAtlas>(Gdx.files.internal(R.matlas0).path())
        player = Player(am, Vector2(100f, 100f))
        camera.zoom = 0.8f

        debugGui.setPlayer(player)

        ef = injector.getInstance(EffectsManager::class.java)
        am.load(Gdx.files.internal(R.dot_particles0).path(),ParticleEffect::class.java)
        am.finishLoadingAsset<ParticleEffect>(Gdx.files.internal(R.dot_particles0).path())
        ef.createEffect(1,am[Gdx.files.internal(R.dot_particles0).path()])


        engine.addEntity(createMapEntity(tilesSize.toInt()))
        engine.addEntity(player)
        engine.addEntity(Wood(Vector2(100f, 200f)))
        engine.addEntity(Wood(Vector2(250f, 210f)))
        engine.addEntity(Workbench(Vector2(350f, 220f)))

        engine.addEntity(Bonfire(Vector2(300f,100f)))


        engine.addEntity(Rock(Vector2(470f, 590f)))
        engine.addEntity(Rock(Vector2(350f, 690f)))

        engine.addSystem(SMainGUI(player))

        engine.addSystem(STools(player))
        engine.addSystem(SDropUpdate(player))
        engine.addSystem(SWorkbenchDetected(player))


        system<SMap2D> {
            tileSize.set(tilesSize, tilesSize)
            setTileset = {
                for (i in 1..12) {
                    it.put(i, TextureAtlas(R.matlas0).findRegion("tile", i))
                }
            }
        }
        system<SDrawDebug20> {
            customDebug = {
                injector.getInstance(World::class.java).drawDebug(camera,it)
                val c = it.packedColor
                it.setColor(Color.LIME)
                engine.getEntitiesFor(Family.all(CWorkbench::class.java).exclude(CHide::class.java).get()).forEach {w ->
                    if(CWorkbench[w].isNear) {
                        it.circle(CTransforms[w].getCenterX(),CTransforms[w].getCenterY(),CTransforms[w].size.getRadius(),3f)
                    }

                }
                it.setColor(Color.CYAN)
                engine.getEntitiesFor(Family.all(CNearbyObject::class.java).get()).forEach { w ->
                    it.circle(CTransforms[w].getCenterX(),CTransforms[w].getCenterY(),CTransforms[w].size.getRadius(),3f)
                }
                when(player.directionSee) {
                    UP -> {
                        it.line(CTransforms[player].getCenterX(),CTransforms[player].getCenterY(),
                                CTransforms[player].getCenterX(),
                                CTransforms[player].getCenterY()+player.getCurrentTool().distance)
                    }
                    DOWN -> {
                        it.line(CTransforms[player].getCenterX(),CTransforms[player].getCenterY(),
                                CTransforms[player].getCenterX(),
                                CTransforms[player].getCenterY()-player.getCurrentTool().distance)
                    }
                    LEFT -> {
                        it.line(CTransforms[player].getCenterX(),CTransforms[player].getCenterY(),
                                CTransforms[player].getCenterX()-player.getCurrentTool().distance,
                                CTransforms[player].getCenterY())
                    }
                    RIGHT -> {
                        it.line(CTransforms[player].getCenterX(),CTransforms[player].getCenterY(),
                                CTransforms[player].getCenterX()+player.getCurrentTool().distance,
                                CTransforms[player].getCenterY())
                    }
                }
                it.setColor(c)
            }
        }
        system<SDefaultPhysics2d> {
            this.world.addContactListener(PlayerToolsListener(player))
        }

        //Добавляем главный инпут
        injector.getInstance(InputMultiplexer::class.java).addProcessor(MainInput(injector))
    }

    private fun createMapEntity(toInt: Int): Entity {
        return engine.createEntity {
            components {
                val gen = Map2DGenerator(toInt, CreatedCellMapListener(toInt * 1f))
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

