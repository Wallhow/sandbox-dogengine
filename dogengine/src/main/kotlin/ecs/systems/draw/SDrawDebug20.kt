package dogengine.ecs.systems.draw

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.google.inject.Inject
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CHide
import dogengine.ecs.systems.SystemPriority
import dogengine.utils.TTFFont
import space.earlygrey.shapedrawer.ShapeDrawer

class SDrawDebug20 @Inject constructor(val camera: OrthographicCamera, private val spriteBatch: SpriteBatch) : IteratingSystem(Family.all(CTransforms::class.java).exclude(CHide::class.java).get()) {
    var visible: Boolean = false
    var customDebug: ((ShapeDrawer) -> Unit)? = null
    private val ttf: TTFFont = TTFFont()
    private var font: BitmapFont
    private var layout: GlyphLayout
    private var drawer: ShapeDrawer

    init {
        drawer = ShapeDrawer(spriteBatch,getRegion())

        priority = SystemPriority.DRAW+50
        font = ttf.create(36, Color.FIREBRICK).get(36)
        ttf.create(16, Color.FIREBRICK).get(8)
        layout = GlyphLayout(font, "")
        layout.setText(font, "FPS: 00")
    }

    private fun getRegion(): TextureRegion {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE)
        pixmap.drawPixel(0, 0)
        val texture = Texture(pixmap) //remember to dispose of later
        pixmap.dispose()
        return TextureRegion(texture, 0, 0, 1, 1)
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


            val x0 = camera.position.x - (camera.viewportWidth * 0.5f)
            val fps = Gdx.graphics.framesPerSecond
            val gPadding = layout.width + layout.width * 0.5f
            val entities = engine.entities.size()

            font = ttf.get(36)
            font.draw(spriteBatch, "FPS: $fps\nentities:$entities", x0 + gPadding, camera.position.y)
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


