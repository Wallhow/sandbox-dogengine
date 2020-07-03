package dogengine

import com.badlogic.gdx.Game
import com.badlogic.gdx.utils.viewport.Viewport
import com.google.inject.Binder
import com.google.inject.Inject
import com.google.inject.Injector

abstract class DogeEngineGame : Game() {
    abstract val viewport: Viewport
    private val kernel = Kernel
    private val systems = kernel.getSystems()

    protected val injector : Injector
        get() = kernel.getInjector()

    abstract val systemConfigure : Kernel.Systems.() -> Unit
    abstract val injectConfigure: (Binder.() -> Unit)

    fun initialize(dogeEngineGame: DogeEngineGame) {
        dogeEngineGame.systemConfigure.invoke(systems)
        kernel.initialize(dogeEngineGame.injectConfigure)
    }
}