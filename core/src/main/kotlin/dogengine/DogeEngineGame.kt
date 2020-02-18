package dogengine

import com.badlogic.gdx.Game
import com.badlogic.gdx.utils.viewport.Viewport

abstract class DogeEngineGame : Game() {
    abstract val viewport: Viewport
    val kernel = Kernel
    val systems = kernel.getSystems()
}