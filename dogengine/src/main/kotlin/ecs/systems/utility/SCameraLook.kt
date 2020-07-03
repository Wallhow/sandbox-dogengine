package dogengine.ecs.systems.utility

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.math.Vector2
import com.google.inject.Inject
import dogengine.Kernel
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CCameraLook
import dogengine.ecs.systems.SystemPriority
import dogengine.utils.GameCamera

/**
 * Created by wallhow on 20.12.16.
 */
class SCameraLook @Inject constructor(private val gameCamera: GameCamera):
        EntitySystem( ) {
    init {
        priority = SystemPriority.UPDATE+150
    }
    private val fixCam: Boolean = Kernel.Systems.CameraLook.fixedBounds
    private var smoothMove = false
    private val vel = Vector2(0f,0f)
    private var firstRun = true
    private val coeff = 0.98f
    private val speedMove = 200f
    private var entityLook : Entity? = null
    private val camera = gameCamera.getCamera()
    private val worldSize = gameCamera.getWorldSize()
    override fun update(deltaTime: Float) {
        if (entityLook==null) {
            val ea = engine.getEntitiesFor(Family.all(CCameraLook::class.java).get())
            if(ea.size()!=0)
                entityLook = ea.first() ?: null
        }
        entityLook?.apply {
            val pos = CTransforms[this].position.cpy().add(
                    Vector2(CTransforms[this].size.halfWidth,
                            CTransforms[this].size.halfHeight))
            if (fixCam) {
                if (pos.x < camera.viewportWidth * camera.zoom * 0.5f) {
                    pos.x = camera.viewportWidth * camera.zoom * 0.5f
                }
                if (pos.x > worldSize.width - camera.viewportWidth * camera.zoom * 0.5f) {
                    pos.x = worldSize.width - camera.viewportWidth * camera.zoom * 0.5f
                }
                if (pos.y < camera.viewportHeight * camera.zoom * 0.5f) {
                    pos.y = camera.viewportHeight * camera.zoom * 0.5f
                }
                if (pos.y > worldSize.height - camera.viewportHeight * camera.zoom * 0.5f) {
                    pos.y = worldSize.height - camera.viewportHeight * camera.zoom * 0.5f
                }
            }
            if(firstRun) {
                gameCamera.setPosition(pos.x,pos.y)
                firstRun = false
            }

            val f = 0.15f
            if(smoothMove) {
                if (pos.x>camera.position.x+camera.viewportWidth* camera.zoom*f) {
                    vel.x = speedMove*deltaTime
                }
                if (pos.x<camera.position.x-camera.viewportWidth* camera.zoom*f) {
                    vel.x = -speedMove*deltaTime
                }
                if (pos.y>camera.position.y+camera.viewportHeight* camera.zoom*f) {
                    vel.y = speedMove*deltaTime
                }
                if (pos.y<camera.position.y-camera.viewportHeight* camera.zoom*f) {
                    vel.y = -speedMove*deltaTime
                }
                if(vel.x>1 || vel.x<-1) vel.x*=coeff
                else vel.x=0f
                if(vel.y>1 || vel.y<-1) vel.y*=coeff
                else vel.y = 0f
                gameCamera.translate(vel.x,vel.y)
            }
            else {

                gameCamera.setPosition(pos.x,pos.y)

            }
            //camera.frustum.update(camera.invProjectionView)
            gameCamera.update()
        }
    }
}