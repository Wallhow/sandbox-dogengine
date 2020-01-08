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
import com.google.inject.Inject
import dogengine.ecs.components.toVector2
import dogengine.utils.TTFFont
import ktx.graphics.rect
import sandbox.dogengine.ecs.components.utility.logic.CTransforms
import sandbox.dogengine.ecs.components.utility.visible.CHide
import sandbox.sandbox.def.jbump.CJBumpAABB

class SDrawDebug @Inject constructor(val camera: OrthographicCamera, val spriteBatch: SpriteBatch) : IteratingSystem(Family.all(CTransforms::class.java).exclude(CHide::class.java).get()) {
    var visible: Boolean = false
    private val renderer: ShapeRenderer = ShapeRenderer()
    private var drawDefaultPhysic = true
    var customDebug: ((ShapeRenderer) -> Unit)? = null
    private val ttf: TTFFont = TTFFont()
    private lateinit var font: BitmapFont
    private lateinit var layout: GlyphLayout

    init {
        priority = Int.MAX_VALUE
        font = ttf.create(36, Color.FIREBRICK).get(36)
        ttf.create(16, Color.FIREBRICK).get(8)
        layout = GlyphLayout(font, "")
        layout.setText(font, "FPS: 00")
        //font = FreeTypeFontGenerator(Gdx.files.internal("assets/pixel.ttf")).generateFont(FreeTypeFontGenerator.FreeTypeFontParameter().apply { size = 24 })
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
            val y0 = camera.position.y - (camera.viewportHeight * 0.5f + camera.viewportHeight) * camera.zoom
            val fps = Gdx.graphics.framesPerSecond

            val g_padding = layout.width + layout.width * 0.5f
            val v_padding = layout.height + layout.height * 0.3f
            val entities = engine.entities.size()
            spriteBatch.projectionMatrix = camera.combined
            spriteBatch.begin()
            font = ttf.get(36)
            font.draw(spriteBatch, "FPS: $fps\nentities:$entities", x0 + g_padding, camera.position.y)
            /*font = ttf.get(8)
        font.draw(spriteBatch,"${CTransforms[entity].position}",v1.x,v1.y)*/
            spriteBatch.end()
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val tr = CTransforms[entity]
        renderer.color = Color.SLATE
        renderer.rect(tr.position,tr.size.toVector2())
        if (CJBumpAABB[entity] != null) {
            val ss = CJBumpAABB[entity].scaleSize
            val of = CJBumpAABB[entity].positionOffset
            val nW = (tr.size.width * ss.x)
            val nH = (tr.size.height * ss.y)
            val nX = tr.position.x + of.x
            val nY = tr.position.y + of.y
            renderer.color = Color.RED
            renderer.rect(nX,nY,nW,nH)
        }
    }
}


