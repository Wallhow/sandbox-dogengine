package dogengine.ecs.systems.utility

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.ai.GdxAI
import com.badlogic.gdx.ai.msg.MessageManager
import com.google.inject.Inject
import dogengine.ecs.systems.SystemPriority

class SMessageManager : EntitySystem(SystemPriority.AFTER_UPDATE) {
    @Inject
    private lateinit var messageManager: MessageManager
    override fun update(deltaTime: Float) {
        GdxAI.getTimepiece().update(deltaTime)
        messageManager.update()
    }
}