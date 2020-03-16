package dogengine.ecs.systems.draw

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.google.inject.Inject
import dogengine.ecs.components.toVector2
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CHide
import dogengine.ecs.systems.SystemPriority
import dogengine.utils.TTFFont

class SDrawDebug @Inject constructor(val camera: OrthographicCamera, private val spriteBatch: SpriteBatch) : IteratingSystem(Family.all(CTransforms::class.java).exclude(CHide::class.java).get()) {
    var visible: Boolean = false
    private val renderer: ShapeRenderer = ShapeRenderer()
    var customDebug: ((ShapeRenderer) -> Unit)? = null
    private val ttf: TTFFont = TTFFont()
    private var font: BitmapFont
    private var layout: GlyphLayout

    init {
        priority = SystemPriority.DRAW+50
        font = ttf.create(36, Color.FIREBRICK).get(36)
        ttf.create(16, Color.FIREBRICK).get(8)
        layout = GlyphLayout(font, "")
        layout.setText(font, "FPS: 00")
    }


    override fun update(deltaTime: Float) {
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        Gdx.gl.glDisable(GL20.GL_BLEND)
        if (visible) {
            renderer.projectionMatrix = camera.combined
            renderer.begin(ShapeRenderer.ShapeType.Line)
            super.update(deltaTime)
            customDebug?.invoke(renderer)
            renderer.end()

            val x0 = camera.position.x - (camera.viewportWidth * 0.5f)
            val fps = Gdx.graphics.framesPerSecond
            val gPadding = layout.width + layout.width * 0.5f
            val entities = engine.entities.size()

            spriteBatch.projectionMatrix = camera.combined
            spriteBatch.begin()
            font = ttf.get(36)
            font.draw(spriteBatch, "FPS: $fps\nentities:$entities", x0 + gPadding, camera.position.y)
            spriteBatch.end()
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val tr = CTransforms[entity]
        renderer.color = Color.SLATE
        renderer.rect(tr.position, tr.size.toVector2())
    }

    //from ktx :)
    private fun ShapeRenderer.rect(position: Vector2, size: Vector2) {
        rect(position.x, position.y, size.x, size.y)
    }
}


