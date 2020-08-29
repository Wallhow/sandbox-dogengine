package sandbox.sandbox

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegraph
import com.strongjoshua.console.CommandExecutor
import com.strongjoshua.console.LogLevel
import com.strongjoshua.console.annotation.ConsoleDoc
import dogengine.Kernel
import dogengine.MessagesType
import dogengine.ecs.components.utility.logic.CDefaultPhysics2d
import dogengine.ecs.systems.tilemap.SMap2D
import dogengine.redkin.physicsengine2d.variables.Types
import dogengine.utils.extension.get
import sandbox.dev.gui.DebugGUI.Companion.MSG_DEBUG_PLAYER_COLLIDE
import sandbox.dev.gui.DebugGUI.Companion.MSG_DEBUG_SHOW_MAP_TEXTURE
import sandbox.sandbox.go.player.Player

class ConsoleCommands : CommandExecutor() {
    private val messenger = Kernel.getInjector()[MessageManager::class.java]

    @ConsoleDoc(description = "camera look to entity with name")
    fun camLookTo(name: String) {
        val sender = Telegraph {
            if(it.extraInfo as Int ==-1) {
                console.log("entity with name $name not found", LogLevel.ERROR)
            }
            false
        }
        messenger.dispatchMessage(sender, MessagesType.CAMERA_LOOK_TO,name,true)
    }

    fun camSmoothMove(isSmooth: Boolean) {
        messenger.dispatchMessage(MessagesType.CAMERA_SMOOTH_MOVE, arrayOf(isSmooth,console))
    }

    fun map(isVisible: Boolean) {
        messenger.dispatchMessage(MSG_DEBUG_SHOW_MAP_TEXTURE,isVisible)
    }
    fun collide(flag: Boolean) {
        if (flag)
            CDefaultPhysics2d[Player.playerInstance].type = Types.TYPE.SENSOR
        else
            CDefaultPhysics2d[Player.playerInstance].type = Types.TYPE.DYNAMIC
    }
}