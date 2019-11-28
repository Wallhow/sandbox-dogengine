package dogengine.ashley.systems.draw

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.google.inject.Inject
import dogengine.ashley.components.CTransforms

class SDrawDebug @Inject constructor(val camera : OrthographicCamera) : IteratingSystem(Family.all(CTransforms::class.java).get()) {
    val renderer: ShapeRenderer = ShapeRenderer()
    init {
        priority = 10
    }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val tr = CTransforms[entity]

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glDisable(GL20.GL_BLEND);

        renderer.projectionMatrix = camera.combined
        renderer.begin(ShapeRenderer.ShapeType.Line)
        val v1 = Vector2(tr.position.cpy())
        val v2 = Vector2(tr.position.cpy().add(tr.size.width,0f))
        val v3 = Vector2(tr.position.cpy().add(tr.size.width,tr.size.height))
        val v4 = Vector2(tr.position.cpy().add(0f,tr.size.height))


        renderer.line(v1,v2)
        renderer.line(v2,v3)
        renderer.line(v3,v4)
        renderer.line(v4,v1)

        renderer.end()

    }
}