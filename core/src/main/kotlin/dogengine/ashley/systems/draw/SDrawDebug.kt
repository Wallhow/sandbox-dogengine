package dogengine.ashley.systems.draw

import com.badlogic.ashley.core.Engine
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
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.google.inject.Inject
import dogengine.ashley.components.CTransforms
import dogengine.utils.TTFFont
import sandbox.def.CJBumpAABB

class SDrawDebug @Inject constructor(val camera : OrthographicCamera,val spriteBatch: SpriteBatch) : IteratingSystem(Family.all(CTransforms::class.java).get()) {
    var visible: Boolean = false
    private val renderer: ShapeRenderer = ShapeRenderer()
    private val ttf : TTFFont = TTFFont()
    private lateinit var font : BitmapFont
    private lateinit var layout: GlyphLayout
    init {
        priority = Int.MAX_VALUE-1
        font = ttf.create(36, Color.FIREBRICK).get(36)
        ttf.create(16, Color.FIREBRICK).get(8)
        layout = GlyphLayout(font,"")
        layout.setText(font,"FPS: 00")

        //font = FreeTypeFontGenerator(Gdx.files.internal("assets/pixel.ttf")).generateFont(FreeTypeFontGenerator.FreeTypeFontParameter().apply { size = 24 })
    }



    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (visible) {
            val tr = CTransforms[entity]

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            Gdx.gl.glDisable(GL20.GL_BLEND);

            renderer.projectionMatrix = camera.combined
            renderer.begin(ShapeRenderer.ShapeType.Line)
            val v1 = Vector2(tr.position.cpy())
            val v2 = Vector2(tr.position.cpy().add(tr.size.width, 0f))
            val v3 = Vector2(tr.position.cpy().add(tr.size.width, tr.size.height))
            val v4 = Vector2(tr.position.cpy().add(0f, tr.size.height))

            renderer.color = Color.GREEN
            renderer.line(v1, v2)
            renderer.line(v2, v3)
            renderer.line(v3, v4)
            renderer.line(v4, v1)

            if (CJBumpAABB[entity] != null) {
                val ss = CJBumpAABB[entity].scaleSize
                val of = CJBumpAABB[entity].positionOffset
                val nW = (tr.size.width * ss.x)
                val nH = (tr.size.height * ss.y)
                val nX = tr.position.x +of.x
                val nY = tr.position.y+of.y
                v1.set(nX, nY + nH)
                v2.set(nX + nW, v1.y)
                v3.set(v2.x , nY)
                v4.set(v1.x, v3.y)

                renderer.color = Color.RED
                renderer.line(v1, v2)
                renderer.line(v2, v3)
                renderer.line(v3, v4)
                renderer.line(v4, v1)
            }
            renderer.end()
            val x0 = camera.position.x - (camera.viewportWidth * 0.5f)
            val y0 = camera.position.y - (camera.viewportHeight * 0.5f + camera.viewportHeight)*camera.zoom
            val fps = Gdx.graphics.framesPerSecond

            val g_padding = layout.width + layout.width * 0.5f
            val v_padding = layout.height + layout.height * 0.3f

            spriteBatch.projectionMatrix = camera.combined
            spriteBatch.begin()
            font = ttf.get(36)
            font.draw(spriteBatch, "FPS: ${fps}", x0+g_padding, camera.position.y)
            /*font = ttf.get(8)
            font.draw(spriteBatch,"${CTransforms[entity].position}",v1.x,v1.y)*/
            spriteBatch.end()
        }
    }
}