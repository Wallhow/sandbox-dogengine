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
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.google.inject.Inject
import dogengine.ashley.components.CTransforms
import dogengine.utils.TTFFont
import sandbox.def.CJBumpAABB

class SDrawDebug @Inject constructor(val camera : OrthographicCamera,val spriteBatch: SpriteBatch) : IteratingSystem(Family.all(CTransforms::class.java).get()) {
    private val renderer: ShapeRenderer = ShapeRenderer()
    private val ttf : TTFFont = TTFFont()
    private lateinit var font : BitmapFont
    init {
        priority = 10

    }

    override fun addedToEngine(engine: Engine?) {
        font = FreeTypeFontGenerator(Gdx.files.internal("assets/pixel.ttf")).generateFont(FreeTypeFontGenerator.FreeTypeFontParameter().apply { size = 24 })
//        font = TTFFont("assets/pixel.ttf").create(26, Color.DARK_GRAY).get(26)
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

        renderer.color = Color.GREEN
        renderer.line(v1,v2)
        renderer.line(v2,v3)
        renderer.line(v3,v4)
        renderer.line(v4,v1)

        if(CJBumpAABB[entity]!= null) {
            val ss = CJBumpAABB[entity].scaleSize
            val nW = (tr.size.width - tr.size.width*ss.x)*0.5f
            val nH = (tr.size.height - tr.size.height*ss.y)*0.5f
            v1.set(tr.position.x+nW,tr.position.y+nH)
            v2.set(tr.position.x+tr.size.width - nW,v1.y)
            v3.set(v2.x,tr.position.y+tr.size.height - nH)
            v4.set(v1.x,v3.y)

            renderer.color = Color.RED
            renderer.line(v1,v2)
            renderer.line(v2,v3)
            renderer.line(v3,v4)
            renderer.line(v4,v1)
        }
        renderer.end()
        val x0 = camera.position.x - camera.viewportWidth*0.5f
        val y0 = camera.position.y - camera.viewportHeight*0.5f

        spriteBatch.begin()
        font.draw(spriteBatch,"FPS : ${Gdx.graphics.framesPerSecond}",x0,y0)
        spriteBatch.end()
    }
}