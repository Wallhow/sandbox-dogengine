package dogengine.ecs.systems.draw

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.google.inject.Inject
import dogengine.Kernel
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CHide
import dogengine.ecs.systems.SystemPriority
import space.earlygrey.shapedrawer.ShapeDrawer

class SDrawDebug20 @Inject constructor(val camera: OrthographicCamera, private val spriteBatch: SpriteBatch) : IteratingSystem(Family.all(CTransforms::class.java).exclude(CHide::class.java).get()) {
    var visible: Boolean = false
    var customDebug: ((ShapeDrawer) -> Unit)? = null
    private var drawer: ShapeDrawer

    init {
        drawer = ShapeDrawer(spriteBatch,getRegion())

        priority = SystemPriority.DRAW+50

    }

    private fun getRegion(): TextureRegion {
        return Kernel.getInjector().getInstance(TextureRegion::class.java)
    }


    override fun update(deltaTime: Float) {
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        Gdx.gl.glDisable(GL20.GL_BLEND)
        if (visible) {
            spriteBatch.projectionMatrix = camera.combined
            spriteBatch.begin()
            super.update(deltaTime)
            customDebug?.invoke(drawer)

            spriteBatch.end()
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val tr = CTransforms[entity]
        drawer.setColor(Color.SLATE)
        val rect = Rectangle(tr.position.x,tr.position.y,tr.size.width,tr.size.height)
        drawer.rectangle(rect)
    }
}


