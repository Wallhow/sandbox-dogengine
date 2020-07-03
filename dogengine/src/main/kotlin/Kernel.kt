package dogengine

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.google.inject.*
import dogengine.ecs.systems.actions.STweenEngine
import dogengine.ecs.systems.controllers.SInputController
import dogengine.ecs.systems.draw.*
import dogengine.ecs.systems.physics.*
import dogengine.ecs.systems.update.SUpdate
import dogengine.ecs.systems.update.SVelocity
import dogengine.ecs.systems.utility.SCameraLook
import dogengine.ecs.systems.utility.SDeleteMe
import dogengine.ecs.systems.update.SVisibleEntity


object Kernel {
    private lateinit var inject: Injector
    private val systems = Systems()
    lateinit var dotTexture: TextureRegion
    var viewBoundsRect : Rectangle = Rectangle()

    fun initialize(bind: (Binder.() -> Unit) = {}) {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888).apply {
            setColor(Color.WHITE)
            drawPixel(0, 0)
        }
        dotTexture =  TextureRegion(Texture(pixmap), 0, 0, 1, 1)
        pixmap.dispose()

        inject = Guice.createInjector(KernelModule(bind,systems))
        //Загружаем системы в эшли
        systems.list.forEach {
            val system = inject.getInstance(it)
            inject.getInstance(Engine::class.java).addSystem(system)
        }

        Gdx.input.inputProcessor = getInjector().getInstance(InputMultiplexer::class.java)
        PooledEntityCreate.engine = inject.getInstance(Engine::class.java)
    }

    fun getSystems(): Systems = systems
    fun getInjector() = inject

    class Systems {
        var list: ArrayList<Class<out EntitySystem>> = ArrayList()
        fun add(system: Class<out EntitySystem>) {
            list.add(system)
        }
        fun use(defSystems: DefSystems) {
            defSystems.ordinal
            defSystems.declaringClass
            DefSystems.values()[defSystems.ordinal].system.forEach {
                list.add(it)
            }

        }

        object CameraLook {
            var fixedBounds = false
        }
        object SVisibleEntity {
            var scale = 1.0f
        }

    }
    enum class DefSystems(vararg val system:Class<out EntitySystem>) {
        GameObjects(SVisibleEntity::class.java,
                SDrawTextureRegion::class.java,SDrawTextureRegionAnimation::class.java,
                SDrawAtlasRegion::class.java,SDrawAtlasRegionAnimation::class.java,
                SVelocity::class.java),
        Box2DPhysics(
                SPhysicsBox2DUpdate::class.java,
                SPhysics2D::class.java,
                SPhysicsCollision::class.java,
                SCollideEventHandler::class.java,
                SPhysics2DDeleteMe::class.java),
        CameraLook(SCameraLook::class.java),
        DrawDebug(SDrawDebug20::class.java),
        Render3DIn2DWorld(SRender3DIn2D::class.java),
        Controller(SInputController::class.java),
        Update(SUpdate::class.java),
        DeleteEntity(SDeleteMe::class.java),
        TweenEngine(STweenEngine::class.java)

    }


    private class KernelModule(val bind: Binder.() -> Unit,val systems: Systems) : Module {
        //val bloom = Bloom().apply { this.setTreshold(0.85f) }
        override fun configure(binder: Binder) {
            bind.invoke(binder)
            binder.bind(TextureRegion::class.java).toProvider(DotTexture::class.java)
            //binder.bind(Bloom::class.java).toInstance(bloom)
        }

        @Provides
        @Singleton
        fun getBatch(): SpriteBatch = SpriteBatch()

        @Provides
        @Singleton
        fun getEngine(): Engine {
            return PooledEngine()
        }

        @Provides
        @Singleton
        fun systems(): Kernel.Systems {
            return systems
        }

        @Provides
        @Singleton
        fun inputMultiplexer(): InputMultiplexer {
            return InputMultiplexer()
        }
        @Provides
        @Singleton
        fun assetManager() : AssetManager {
            return AssetManager()
        }


    }
    @Singleton
    class DotTexture : Provider<TextureRegion> {
        override fun get(): TextureRegion {
            val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE)
            pixmap.drawPixel(0, 0)
            val texture = Texture(pixmap)
            pixmap.dispose()
            return TextureRegion(texture, 0, 0, 1, 1)
        }
    }

}

object PooledEntityCreate {
    var engine : Engine? = null
}