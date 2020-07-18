package sandbox

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.google.inject.Injector
import dogengine.drawcore.DrawTypes
import dogengine.drawcore.SDraw2D
import dogengine.ecs.components.components
import dogengine.ecs.components.create
import dogengine.ecs.components.createEntity
import dogengine.ecs.components.draw.CTextureRegion
import dogengine.ecs.components.utility.logic.CDefaultPhysics2d
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CHide
import dogengine.ecs.components.utility.visible.CLightBox2D
import dogengine.ecs.components.utility.visible.LightType
import dogengine.ecs.systems.draw.SDrawDebug20
import dogengine.ecs.systems.physics.SDefaultPhysics2d
import dogengine.ecs.systems.tilemap.CMap2D
import dogengine.ecs.systems.tilemap.SMap2D
import dogengine.redkin.physicsengine2d.world.World
import dogengine.shadow2d.components.CShadow
import dogengine.utils.Size
import dogengine.utils.system
import dogengine.utils.vec2
import sandbox.def.particles.EmitterManager
import sandbox.sandbox.def.def.comp.CNearbyObject
import sandbox.def.ecs.sys.SDropUpdate
import sandbox.def.ecs.sys.STools
import sandbox.sandbox.def.def.sys.SWorkbenchDetected
import sandbox.sandbox.def.gui.DebugGUI
import sandbox.sandbox.def.gui.SMainGUI
import sandbox.sandbox.def.map.CreatedCellMapListener
import sandbox.sandbox.def.map.Map2DGenerator
import sandbox.sandbox.go.environment.objects.buiding.CWorkbench
import sandbox.sandbox.go.mobs.Pig
import sandbox.sandbox.go.player.Player
import sandbox.sandbox.go.player.Player.DirectionSee.*
import sandbox.sandbox.go.player.PlayerToolsListener
import sandbox.sandbox.input.MainInput
import space.earlygrey.shapedrawer.ShapeDrawer


class MainScreen(private val injector: Injector) : ScreenAdapter() {
    private val batch: SpriteBatch = injector.getInstance(SpriteBatch::class.java)
    val camera: OrthographicCamera = injector.getInstance(OrthographicCamera::class.java)
    val engine: Engine = injector.getInstance(Engine::class.java)
    lateinit var player: Player
    private val tilesSize = 32f
    private val eManager = injector.getInstance(EmitterManager::class.java)

    //ДЕБАГ ТАБЛО
    private val debugGui = DebugGUI()

    override fun render(delta: Float) {
        engine.update(delta)
        eManager.update(delta)

        batch.begin()

        eManager.draw()
        batch.end()

        debugGui.updateAndDraw(delta)
    }

    override fun show() {
        val am = injector.getInstance(AssetManager::class.java)
        am.load(Gdx.files.internal(R.matlas0).path(), TextureAtlas::class.java)
        am.finishLoadingAsset<TextureAtlas>(Gdx.files.internal(R.matlas0).path())
        player = Player(am, Vector2(100f, 100f))
        player.add(CShadow())
        camera.zoom = 0.8f

        debugGui.setPlayer(player)

        engine.addEntity(createMapEntity(tilesSize.toInt()))
        engine.addEntity(player)
        engine.addEntity(Pig(am, Vector2(250f, 1500f)))

        engine.addEntity(engine.createEntity {
            components {
                create<CLightBox2D> {
                    type = LightType.DIRECTIONAL
                }
            }
        })


        engine.addSystem(SMainGUI(player))
        engine.addSystem(STools(player))
        engine.addSystem(SDropUpdate(player))
        engine.addSystem(SWorkbenchDetected(player))


        system<SMap2D> {
            tilesets.createTileSet(Size(tilesSize,tilesSize)) {
                for (i in 1..12) {
                    it.put(i, TextureAtlas(R.matlas0).findRegion("tile", i))
                }
            }
        }
        system<SDrawDebug20> {
            customDebug = createDrawFunc()
        }
        system<SDefaultPhysics2d> {
            this.world.addContactListener(PlayerToolsListener(player))
        }
        system<SDraw2D> {
            this.drawFunctions.put(DrawTypes.BATCH,DrawTypes.batchDrawFunction)
            this.drawFunctions.put(DrawTypes.MAP,DrawTypes.batchDrawFunction)
            this.drawFunctions.put(DrawTypes.SOLID,DrawTypes.solidDrawFunction)
        }

        //Добавляем главный инпут
        injector.getInstance(InputMultiplexer::class.java).addProcessor(MainInput(injector))
    }

    private fun createDrawFunc() : ((ShapeDrawer) -> Unit) {
        return  {
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
                if(CDefaultPhysics2d[w]!=null) {
                    val phys = CDefaultPhysics2d[w]
                    val pos = Vector2()
                    phys.rectangleBody?.getCenter(pos)
                    val x =pos.x
                    val y = pos.y
                    it.circle(x,y,CTransforms[w].size.getRadius(),3f)
                }

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

