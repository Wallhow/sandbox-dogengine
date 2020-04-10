package sandbox.sandbox

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.*
import com.badlogic.gdx.math.collision.Ray
import com.badlogic.gdx.utils.Array
import com.google.inject.Injector
import dogengine.Kernel
import dogengine.utils.log
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
    val matrix = Matrix4()


    override fun render(delta: Float) {
        arrayEmitter.forEach {
            it.update(delta)
        }
        log("render!")
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.projectionMatrix = camera.combined;
        batch.transformMatrix = matrix;

        batch.color = Color.RED
        batch.begin()
        //drawer.filledRectangle(0f,0f,500f,500f)
        arrayEmitter.forEach {
            it.draw()
        }

        for(x in 0..50) {
            for(y in 0..50) {
                drawer.rectangle(x*10f,y*10f,10f,10f)
            }
        }
        batch.end()
    }

    override fun show() {
        camera.position.set(100f, 100f, 5f)
        camera.direction.set(-1f, -1f, -1f)
        camera.near = 1f
        camera.far = 400f
        matrix.setToRotation(Vector3(1f, 0f, 0f), 90f)
        camera.zoom = 1f
        //camera.translate(500f,500f)
        camera.update()
        //Добавляем главный инпут
        injector.getInstance(InputMultiplexer::class.java).addProcessor(Input(injector,arrayEmitter))
    }

    class Input(private val injector: Injector,val arrEmitter: Array<Emitter>) : InputAdapter() {
        val curr = Vector3()
        val last = Vector3(-1f, -1f, -1f)
        val delta = Vector3()
        val xzPlane: Plane = Plane(Vector3(0f, 1f, 0f), 0f)
        override fun touchDragged(x: Int, y: Int, pointer: Int): Boolean {
            var pickRay: Ray = camera.getPickRay(x.toFloat(), y.toFloat())
            Intersector.intersectRayPlane(pickRay, xzPlane, curr)
            if (!(last.x == -1f && last.y == -1f && last.z == -1f)) {
                pickRay = camera.getPickRay(last.x, last.y)
                Intersector.intersectRayPlane(pickRay, xzPlane, delta)
                delta.sub(curr)
                camera.position.add(delta.x, delta.y, delta.z)
            }
            last.set(x.toFloat(), y.toFloat(), 0f)
            return false
        }

        override fun touchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean {
            last.set(-1f, -1f, -1f)
            return false
        }

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
