package dogengine.shadow2d.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.Texture.TextureWrap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.GdxRuntimeException
import com.google.inject.Inject
import dogengine.ecs.components.draw.CDraw
import dogengine.ecs.components.getCTransforms
import dogengine.ecs.systems.SystemPriority
import dogengine.shadow2d.PointLight
import dogengine.shadow2d.components.CShadow
import dogengine.utils.GameCamera
import dogengine.utils.Size

class SShadow2D @Inject constructor(private val batch: SpriteBatch, private val gameCamera: GameCamera) : IteratingSystem(Family.all(CShadow::class.java).get()) {
    private val shadowObj = Array<Entity>()
    private var lvl = 1

    companion object {
        private val finaleFBO = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, false)
        private var lightSize = 256
        private var upScale = 1f //for example; try lightSize=128, upScale=1.5f
        private var levelDetailShadow: Int = 1
        val lights = Array<PointLight>()
        val shadowMap: ShadowMap = ShadowMap(TextureRegion(finaleFBO.colorBufferTexture))
        var softShadows: Boolean = true
        var additive: Boolean = true
        fun setDetailLevelShadow(level: Int) {
            var lvl = level
            if (level <= 0) lvl = 1
            if (level > 5) lvl = 5
            levelDetailShadow = lvl
            //   1        2       3          4           5          6
            // 8 - 32 , 4 - 64 ,2 - 128, 1 - 256, 0.5 - 512, 0.25 - 1024
            // 1 - 8
            // 3 - x
            // :\ ну как то так
            when (lvl) {
                1 -> {
                    upScale = 8f
                    lightSize = 32
                }
                2 -> {
                    upScale = 4f
                    lightSize = 64
                }
                3 -> {
                    upScale = 2f
                    lightSize = 128
                }
                4 -> {
                    upScale = 1f
                    lightSize = 256
                }
                5 -> {
                    upScale = 0.5f
                    lightSize = 512
                }
                6 -> {
                    upScale = 0.25f
                    lightSize = 1024
                }
                else -> {
                    upScale = 1f
                    lightSize = 256
                }
            }
        }

        fun getDetailLevelShadow(): Int = levelDetailShadow
    }

    init {
        priority = SystemPriority.DRAW - 10
        lights.add(PointLight().apply { x = -100500f;y = -100500f; })
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        shadowObj.add(entity)
    }


    override fun update(deltaTime: Float) {
        shadowObj.clear()
        super.update(deltaTime)
        if (lvl != levelDetailShadow) {
            lvl = levelDetailShadow
            initFBO()
        }

        //clear frame
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        finaleFBO.bind()
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)


        if (additive) batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE)

        lights.forEach {
            if (it.x != -100500f || it.y != -100500f) {
                if (gameCamera.inViewBounds(Vector2(it.x, it.y))) {
                    renderLightStep1(it)
                    renderLightStep2(it)
                }
            } else {
                renderLightStep1(it)
                renderLightStep2(it)
            }
        }


        if (additive) batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        //STEP 4. render sprites in full colour
        batch.begin()
        batch.shader = null //default shader
        batch.draw(finaleFBO.colorBufferTexture, 0f, 0f)
        batch.end()

        shadowMap.texture.setRegion(finaleFBO.colorBufferTexture)
        shadowMap.texture.apply { flip(false, true) }
        shadowMap.apply {
            val x = gameCamera.getCamera().position.x - gameCamera.getScaledViewport().halfWidth
            val y = gameCamera.getCamera().position.y - gameCamera.getScaledViewport().halfHeight
            val w = gameCamera.getScaledViewport().width
            val h = gameCamera.getScaledViewport().height
            position.set(x, y)
            size.set(w, h)
        }
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    }

    private lateinit var shadowMap1D //1 dimensional shadow map
            : TextureRegion
    private lateinit var occluders //occluder map
            : TextureRegion
    private lateinit var shadowMapFBO: FrameBuffer
    private lateinit var occludersFBO: FrameBuffer
    private lateinit var shadowMapShader: ShaderProgram
    private lateinit var shadowRenderShader: ShaderProgram
    override fun addedToEngine(engine: Engine?) {
        ShaderProgram.pedantic = false

        //read vertex pass-through shader
        val vertSrc = Gdx.files.internal("assets/shaders/pass.vert").readString()
        // renders occluders to 1D shadow map
        shadowMapShader = createShader(vertSrc, Gdx.files.internal("assets/shaders/shadowMap.frag").readString())
        // samples 1D shadow map to create the blurred soft shadow
        shadowRenderShader = createShader(vertSrc, Gdx.files.internal("assets/shaders/shadowRender.frag").readString())


        initFBO()
        super.addedToEngine(engine)
    }

    private fun initFBO() {
        //build frame buffers
        occludersFBO = FrameBuffer(Pixmap.Format.RGBA8888, lightSize, lightSize, false)
        occluders = TextureRegion(occludersFBO.colorBufferTexture)
        occluders.flip(false, true)

        //our 1D shadow map, lightSize x 1 pixels, no depth
        shadowMapFBO = FrameBuffer(Pixmap.Format.RGBA8888, lightSize, 1, false)
        val shadowMapTex = shadowMapFBO.colorBufferTexture

        //use linear filtering and repeat wrap mode when sampling
        shadowMapTex.setFilter(TextureFilter.Linear, TextureFilter.Linear)
        shadowMapTex.setWrap(TextureWrap.Repeat, TextureWrap.Repeat)

        //for debugging only; in order to render the 1D shadow map FBO to screen
        shadowMap1D = TextureRegion(shadowMapTex)
        shadowMap1D.flip(false, true)
    }

    val camera = OrthographicCamera()
    private fun renderLightStep1(o: PointLight) {
        val mx: Float = o.x
        val my: Float = o.y

        //STEP 1. render light region to occluder FBO
        //bind the occluder FBO
        occludersFBO.begin()

        //clear the FBO
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        //set the orthographic camera to the size of our FBO
        camera.setToOrtho(false, occludersFBO.width.toFloat(), occludersFBO.height.toFloat())

        //перемещаем камеру так, чтобы свет был в центре
        camera.translate(mx - lightSize / 2f, my - lightSize / 2f)

        //update camera matrices
        camera.update()

        //set up our batch for the occluder pass
        batch.projectionMatrix = camera.combined
        batch.shader = null //use default shader
        batch.begin()
        // Рисуем спрайты отбрасывающие тень //
        shadowObj.forEach {
            CDraw[it]?.let { a ->
                val drawable = CDraw[it]
                val tr = it.getCTransforms()
                val width: Float = tr.size.width
                val height: Float = tr.size.height

                batch.draw(drawable.texture, tr.position.x + drawable.offsetX, tr.position.y + drawable.offsetY,
                        (tr.getCenterX() - tr.position.x), (tr.getCenterY() - tr.position.y),
                        width, height,
                        tr.size.scaleX, tr.size.scaleY,
                        tr.angle)

            }
        }


        //end the batch before unbinding the FBO
        batch.end()

        //unbind the FBO
        occludersFBO.end()

        //STEP 2. build a 1D shadow map from occlude FBO

        //bind shadow map
        shadowMapFBO.begin()

        //clear it
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        //set our shadow map shader
        batch.shader = shadowMapShader
        batch.begin()
        shadowMapShader.setUniformf("resolution", lightSize.toFloat(), lightSize.toFloat())
        shadowMapShader.setUniformf("upScale", upScale)

        //reset our projection matrix to the FBO size
        camera.setToOrtho(false, shadowMapFBO.width.toFloat(), shadowMapFBO.height.toFloat())
        batch.projectionMatrix = camera.combined

        //draw the occluders texture to our 1D shadow map FBO
        batch.draw(occluders.texture, 0f, 0f, lightSize.toFloat(), shadowMapFBO.height.toFloat())

        //flush batch
        batch.end()

        //unbind shadow map FBO
        shadowMapFBO.end()
    }

    private fun renderLightStep2(o: PointLight) {
        val mx: Float = o.x
        val my: Float = o.y
        //STEP 3. render the blurred shadows

        //reset projection matrix to screen
        //gameCamera.getCamera().setToOrtho(false)
        batch.projectionMatrix = gameCamera.getCamera().combined

        //set the shader which actually draws the light/shadow
        finaleFBO.begin()
        batch.shader = shadowRenderShader
        batch.begin()
        shadowRenderShader.setUniformf("resolution", lightSize.toFloat(), lightSize.toFloat())
        shadowRenderShader.setUniformf("softShadows", if (softShadows) 1f else 0f)
        //set color to light
        batch.color = o.color
        val finalSize = lightSize * upScale

        //draw centered on light position
        batch.draw(shadowMap1D.texture, mx - finalSize / 2f, my - finalSize / 2f, finalSize, finalSize)

        //flush the batch before swapping shaders
        batch.end()

        //reset color
        batch.color = Color.WHITE
        finaleFBO.end()
    }
}

fun createShader(vert: String?, frag: String?): ShaderProgram {
    val program = ShaderProgram(vert, frag)
    if (!program.isCompiled) throw GdxRuntimeException("could not compile shader: " + program.log)
    if (program.log.isNotEmpty()) Gdx.app.log("Shadow2d", program.log)
    return program
}

class ShadowMap(tex: TextureRegion) {
    val position = Vector2()
    val size = Size()
    var texture = tex
}