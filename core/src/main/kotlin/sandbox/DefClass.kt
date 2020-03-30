package sandbox.sandbox

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.google.inject.Injector
import dogengine.Kernel
import sandbox.sandbox.def.def.particles.Emitter
import sandbox.sandbox.go.player.Player
import space.earlygrey.shapedrawer.ShapeDrawer

class DefClass(private val injector: Injector) : ScreenAdapter() {
    private val batch: SpriteBatch = injector.getInstance(SpriteBatch::class.java)
    val camera: OrthographicCamera = injector.getInstance(OrthographicCamera::class.java)
    lateinit var player: Player
    private val tilesSize = 32f
    val arrayEmitter = Array<Emitter>()
    val drawer = ShapeDrawer(batch,Kernel.getInjector().getProvider(Kernel.DotTexture::class.java).get().get())


    override fun render(delta: Float) {
        arrayEmitter.forEach {
            it.update(delta)
        }
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.projectionMatrix = camera.combined
        batch.color = Color.RED
        batch.begin()
        //drawer.filledRectangle(0f,0f,500f,500f)
        arrayEmitter.forEach {
            it.draw()
        }
        batch.end()
    }

    override fun show() {
        camera.zoom = 0.8f
        camera.translate(500f,500f)
        camera.update()
        //Добавляем главный инпут
        injector.getInstance(InputMultiplexer::class.java).addProcessor(Input(injector,arrayEmitter))
    }

    class Input(private val injector: Injector,val arrEmitter: Array<Emitter>) : InputAdapter() {
        private val camera: OrthographicCamera = injector.getInstance(OrthographicCamera::class.java)
        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            val pos = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
            val conf = Emitter.Configuration()
            conf.apply {
                pCountMin = 10
                pSpeedMin = 10f
                pSpeedMax = 20f
                pDirectionMin = Vector2(-0.4f,0.5f)
                pDirectionMax = Vector2(0.4f,1f)
                pLifeTimeMin = 1f
                pLifeTimeMax = 2f
                pRotationMin = -10f
                pRotationMax = 10f
                colors.add(Color.RED,
                        Color.GOLDENROD,
                        Color.DARK_GRAY)
                colors.add(Color.BLACK.cpy().apply { a=0f })
            }
            val em = Emitter(conf)
            em.setTo(Vector2(pos.x,pos.y)).start()
            arrEmitter.add(em)
            return true
        }

        override fun scrolled(amount: Int): Boolean {
            if(amount>0) {
                camera.zoom+=0.1f
            } else {
                camera.zoom-=0.1f
            }
            camera.update()
            return true
        }
    }
}
