package sandbox.sandbox

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.*
import com.badlogic.gdx.utils.Array
import com.cyphercove.flexbatch.CompliantBatch
import com.cyphercove.flexbatch.batchable.Quad2D
import com.google.inject.Injector
import dogengine.Kernel
import dogengine.drawcore.DrawTypes
import dogengine.drawcore.SDraw2D
import dogengine.ecs.components.components
import dogengine.ecs.components.create
import dogengine.ecs.components.createEntity
import dogengine.ecs.components.draw.CDraw
import dogengine.ecs.components.draw.CTextureRegion
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.systems.tilemap.CMap2D
import dogengine.ecs.systems.tilemap.SMap2D
import dogengine.ecs.systems.utility.STime
import dogengine.shadow2d.PointLight
import dogengine.shadow2d.components.CShadow
import dogengine.shadow2d.systems.SShadow2D
import dogengine.utils.*
import sandbox.R
import sandbox.go.environment.objects.Rock
import sandbox.go.environment.objects.Wood
import sandbox.sandbox.def.map.CreatedCellMapListener
import sandbox.sandbox.def.map.Map2DGenerator
import sandbox.sandbox.drawfunctions.MyDrawBatchFunction
import sandbox.sandbox.drawfunctions.MyDrawFunct2
import sandbox.sandbox.go.player.Player


class DefClass(private val injector: Injector) : ScreenAdapter() {
    private val batch: SpriteBatch = injector.getInstance(SpriteBatch::class.java)
    val camera: OrthographicCamera = injector.getInstance(OrthographicCamera::class.java)
    val gameCam: GameCamera = injector.getInstance(GameCamera::class.java)
    lateinit var player: Player
    private val tilesSize = 32f


    lateinit var atlass: TextureAtlas
    val sizeShadowMap = 512
    val shadowFBO = FrameBuffer(Pixmap.Format.RGBA8888, sizeShadowMap, sizeShadowMap, false)
    val shadowFBO2 = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, false)

    val rocks = Array<Entity>()
    val tiles = Array<Entity>()
    val defaultShader: ShaderProgram = SpriteBatch.createDefaultShader()
    lateinit var flexBatch: CompliantBatch<Quad2D>
    val camera1 = OrthographicCamera().apply {
        near = -100f
        far = 100f
    }
var d = 0f
var d1 = 0f
    override fun render(delta: Float) {
        //cc.update()
        posSun.set(0f,0f)
        system<STime> {
            posSun.set(sun)
        }

        log(Gdx.graphics.framesPerSecond)
        camera.update()
        Gdx.gl.glClearColor(0f, 0f, 0.3f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        Kernel.getInjector().getInstance(Engine::class.java).update(delta)

        shadowFBO2.bind()
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        createShadowMap2Step1()



        val shadowMap = SShadow2D.shadowMap


        batch.projectionMatrix = camera.combined

        batch.begin()
        batch.shader = defaultShader
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
        batch.draw(shadowMap.texture,
                shadowMap.position.x, shadowMap.position.y,
                shadowMap.size.width, shadowMap.size.height)
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        val x = gameCam.getCamera().position.x - gameCam.getScaledViewport().halfWidth
        val y = gameCam.getCamera().position.y - gameCam.getScaledViewport().halfHeight
        val w = gameCam.getScaledViewport().width
        val h = gameCam.getScaledViewport().height

        /*batch.draw(TextureRegion(shadowFBO2.colorBufferTexture).apply { flip(false, true) },
                x, y,
                w, h)*/




        batch.draw(atlass.findRegion("apple_item"),posSun.x,posSun.y)

        batch.end()

    }

    var t = 0f
    private fun createShadowMap2Step1() {
        rocks.forEach {
            stepOne(it) // Рисуем тень в фрейм буффер
            stepTwo(it)
        }

    }
    val posSun = Vector2()
    private fun stepTwo(it: Entity) {


        shadowFBO2.begin()
        flexBatch.shader = defaultShader
        val matrix = camera.combined.cpy()
        //matrix.rotate(Vector3(1f, 0f, 0f), -45f)
        //matrix.rotate(Vector3(0f, 0f, 1f), -45f)
        flexBatch.projectionMatrix = matrix
        flexBatch.begin()

        val padding = Vector2(-CTransforms[it].size.halfWidth,CTransforms[it].size.halfHeight-CTransforms[it].size.halfHeight/2f)

        val x =(CTransforms[it].position.x - shadowFBO.width/2f )
        val y = (CTransforms[it].position.y - shadowFBO.height/2f )


        flexBatch.draw()
                .textureRegion(
                TextureRegion(shadowFBO.colorBufferTexture).apply { flip(false, true) }
        )
                .position(x,y)

                .size(shadowFBO.width * 1f, shadowFBO.height * 1f)
                //.origin(0.5f, 0f)
                .origin(shadowFBO.width/2f, shadowFBO.height/2f)
                .scale(1f,1f)
                .rotation(posSun.angle()-270)
        flexBatch.end()
        shadowFBO2.end()
        log("angle = ${posSun.angle()-270}")
    }


    private fun stepOne(it: Entity) {
        shadowFBO.begin()
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        flexBatch.shader = shadowShader1
        flexBatch.begin()
        val x = CTransforms[it].position.x
        val y = CTransforms[it].position.y
        camera1.apply {
            setToOrtho(false, shadowFBO.width.toFloat(), shadowFBO.height.toFloat())

            //rotate(Vector3(0f, 1f, 0f), -45f)
            //rotate(Vector3(0f, 0f, 1f), -45f)
            translate(Vector2(x - shadowFBO.width / 2,
                    y - shadowFBO.height / 2))
            update()
        }
        shadowShader1.apply {
            setUniformf("u_heightObject",CTransforms[it].size.height/gameCam.getWorldSize().height)
            setUniformf("u_world_size",Vector2(gameCam.getWorldSize().width,gameCam.getWorldSize().height))
            setUniformf("u_y_global", CTransforms[it].position.y)
        }

        flexBatch.projectionMatrix = camera1.combined
        flexBatch.draw().textureRegion(TextureRegion(CDraw[it].texture).apply { flip(true, false) })
                .position(x, y)
                .size(CTransforms[it].size.width, CTransforms[it].size.height)
        flexBatch.end()

    }

    lateinit var shadowShader1: ShaderProgram


    lateinit var p: PolygonRegion
    override fun show() {
        //camera.translate(100f,100f)
        val am = injector.getInstance(AssetManager::class.java)
        am.load(Gdx.files.internal(R.matlas0).path(), TextureAtlas::class.java)
        am.finishLoadingAsset<TextureAtlas>(Gdx.files.internal(R.matlas0).path())

        camera.update()

        system<SMap2D> {
            tilesets.createTileSet(Size(tilesSize)) {
                for (i in 1..12) {
                    it.put(i, TextureAtlas(R.matlas0).findRegion("tile", i))
                }
            }
        }

        system<STime> {
            scl = 30f
        }

        system<SDraw2D> {
            drawFunctions.put(DrawTypes.MAP, MyDrawBatchFunction(shadowFBO2))
            drawFunctions.put(DrawTypes.BATCH, MyDrawFunct2(shadowFBO2))
        }

        //cc = CameraInputController(camera)
        //Добавляем главный инпут
        injector.getInstance(InputMultiplexer::class.java).addProcessor(Input(injector))
        player = Player(am, Vector2(0f, 0f))
        player.add(CShadow())

        val w = Wood(Vector2(100f, 200f))
        w.add(CShadow())
        rocks.add(Rock(vec2(150f, 150f)))
        rocks.add(Rock(vec2(200f, 200f)))
        rocks.add(Rock(vec2(350f, 150f)))
        rocks.add(Rock(vec2(400f, 550f)))
        rocks.add(Rock(vec2(450f, 300f)))
        rocks.add(Rock(vec2(500f, 350f)))
        rocks.add(Rock(vec2(550f, 400f)))
        rocks.add(w)



        flexBatch = CompliantBatch(Quad2D::class.java, 10000, false, false)
        shadowShader1 = ShaderProgram(Gdx.files.internal("assets/shaders/shadow/shadow_step1.vert").readString(),
                Gdx.files.internal("assets/shaders/shadow/shadow_step1.frag").readString())
        if (!shadowShader1.isCompiled)
            Gdx.app.log("shader error", shadowShader1.log)

        flexBatch.shader = shadowShader1

        atlass = TextureAtlas(Gdx.files.internal("assets/atlas/matlas.atlas"))


        injector.getInstance(Engine::class.java).apply {
            addEntity(createEntity {
                components {
                    create<CTransforms> {
                        position.set(100f, 100f)
                        size = Size(64f, 64f)
                    }
                    create<CTextureRegion> {
                        this.texture = TextureRegion(atlass.findRegion("rock"))
                    }
                    create<CShadow> {

                    }
                }
            })
            //addEntity(Rock(Vector2(470f, 590f)))

            addEntity(player)

            rocks.forEach(::addEntity)
            rocks.add(player)

            addEntity(createMapEntity(32))
        }


        /*system<SDrawDebug20> {
            visible = false
            customDebug = {
                injector.getInstance(World::class.java).drawDebug(camera,it)
                val c = it.packedColor
                it.setColor(Color.LIME)
                engine.getEntitiesFor(Family.all(CWorkbench::class.java).exclude(CHide::class.java).get()).forEach { w ->
                    if(CWorkbench[w].isNear) {
                        it.circle(CTransforms[w].getCenterX(),CTransforms[w].getCenterY(),CTransforms[w].size.getRadius(),3f)
                    }

                }
                it.setColor(Color.CYAN)
                engine.getEntitiesFor(Family.all(CNearbyObject::class.java).get()).forEach { w ->
                    it.circle(CTransforms[w].getCenterX(),CTransforms[w].getCenterY(),CTransforms[w].size.getRadius(),3f)
                }
                when(player.directionSee) {
                    Player.DirectionSee.UP -> {
                        it.line(CTransforms[player].getCenterX(),CTransforms[player].getCenterY(),
                                CTransforms[player].getCenterX(),
                                CTransforms[player].getCenterY()+player.getCurrentTool().distance)
                    }
                    Player.DirectionSee.DOWN -> {
                        it.line(CTransforms[player].getCenterX(),CTransforms[player].getCenterY(),
                                CTransforms[player].getCenterX(),
                                CTransforms[player].getCenterY()-player.getCurrentTool().distance)
                    }
                    Player.DirectionSee.LEFT -> {
                        it.line(CTransforms[player].getCenterX(),CTransforms[player].getCenterY(),
                                CTransforms[player].getCenterX()-player.getCurrentTool().distance,
                                CTransforms[player].getCenterY())
                    }
                    Player.DirectionSee.RIGHT -> {
                        it.line(CTransforms[player].getCenterX(),CTransforms[player].getCenterY(),
                                CTransforms[player].getCenterX()+player.getCurrentTool().distance,
                                CTransforms[player].getCenterY())
                    }
                }
                it.setColor(c)
            }
        }*/
    }

    class Input(private val injector: Injector) : InputAdapter() {

        override fun touchDragged(x: Int, y: Int, pointer: Int): Boolean {

            return true
        }

        override fun touchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean {

            return true
        }

        private val camera: OrthographicCamera = injector.getInstance(OrthographicCamera::class.java)
        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            val pos = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
            SShadow2D.lights.add(PointLight().apply {
                x = pos.x
                y = pos.y
                color = randomColor()
            })
            return true
        }

        fun randomColor(): Color {
            val intensity = Math.random().toFloat() * 0.5f + 0.5f
            return Color(Math.random().toFloat(), Math.random().toFloat(), Math.random().toFloat(), intensity)
        }

        override fun scrolled(amount: Int): Boolean {
            if (amount > 0) {
                camera.zoom += 0.1f
            } else {
                camera.zoom -= 0.1f
            }
            camera.update()
            return true
        }

        override fun keyDown(keycode: Int): Boolean {
            if (keycode == com.badlogic.gdx.Input.Keys.DOWN) {
                camera.zoom -= 0.3f
            }
            if (keycode == com.badlogic.gdx.Input.Keys.UP) {
                camera.zoom += 0.3f
            }
            return false
        }
    }

    private fun Engine.createMapEntity(toInt: Int): Entity {
        return this.createEntity {
            components {
                val gen = Map2DGenerator(toInt, CreatedCellMapListener(toInt * 1f))
                val map2d = gen.generate()
                val t = Texture(gen.pixmap)
                val scale = 4
                create<CMap2D> {
                    map2D = map2d
                }
            }
        }
    }
}
