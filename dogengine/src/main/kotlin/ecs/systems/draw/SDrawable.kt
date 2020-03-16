package dogengine.ecs.systems.draw

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.Viewport
import com.google.inject.Inject
import dogengine.ecs.components.create
import dogengine.ecs.components.draw.CDrawable
import dogengine.ecs.components.utility.CDeleteComponent
import dogengine.ecs.components.utility.CDeleteMe
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CHide
import dogengine.ecs.systems.SystemPriority

class SDrawable @Inject constructor(private val batch: SpriteBatch, val camera: OrthographicCamera, val view: Viewport) : SortedIteratingSystem(Family.all(CDrawable::class.java, CTransforms::class.java)
        .exclude(CHide::class.java).get(), DrawComparator.comparator) {
    private val layers = Array<Array<Entity>>()
    var clearColor = Color(0.2f, 0.2f, 0.2f, 1f)
    var drawToFBO = false

    init {
        priority = SystemPriority.DRAW
        layers.add(Array())
        layers.add(Array())
        //TODO нет ни чего более постоянного чем временное =)
        //vfxManager.addEffect(MotionBlurEffect(Pixmap.Format.RGBA8888, MotionBlurFilter.BlurFunction.MAX, 0.5f))
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val drawable = CDrawable[entity]
        val tr = CTransforms[entity]
        val off = drawable.offsetX*-1
        val tex = drawable.texture!!
        if(tr.size.width==-1f) {tr.size.setNewWidth(tex.regionWidth.toFloat())}
        if(tr.size.height==-1f) {tr.size.setNewHeight(tex.regionHeight.toFloat())}
        val width : Float = tr.size.width
        val height : Float = tr.size.height
        batch.color = drawable.tint
        batch.draw(drawable.texture, tr.position.x+drawable.offsetX, tr.position.y+drawable.offsetY,
                (tr.getCenterX() - tr.position.x), (tr.getCenterY() - tr.position.y),
                width, height,
                tr.size.scaleX, tr.size.scaleY,
                tr.angle)
        batch.color = Color.WHITE

        /*entity.create<CDeleteComponent> {
            componentRemove = drawable
        }*/
        if (drawable.isDeleteAfterDraw) {
            entity.create<CDeleteMe> {  }
        }
    }

    override fun update(deltaTime: Float) {
        forceSort()

        Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.projectionMatrix = camera.combined

            batch.begin()
            super.update(deltaTime)
            //drawLayer(1)
            batch.end()

    }

    private fun drawLayer(index: Int) {
        layers[index].forEach { entity ->
            val drawable = CDrawable[entity]
            val tr = CTransforms[entity]
            batch.color = drawable.tint
            batch.draw(drawable.texture, tr.position.x, tr.position.y,
                    (tr.getCenterX() - tr.position.x), (tr.getCenterY() - tr.position.y),
                    tr.size.width, tr.size.height,
                    tr.size.scaleX, tr.size.scaleY,
                    tr.angle)
            batch.color = Color.WHITE

            entity.add(engine.createComponent(CDeleteComponent::class.java).apply { componentRemove = drawable })
            if (drawable.isDeleteAfterDraw) {
                entity.add(CDeleteMe())
            }

        }
    }


    //TODO Удалить метод
    private fun getDefShader(): ShaderProgram {
        val vertexShader = ("attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                + "uniform mat4 u_projTrans;\n" //
                + "uniform vec2 player;\n" //
                + "varying vec4 v_color;\n" //
                + "varying vec2 v_texCoords;\n" //
                + "\n" //
                + "void main()\n" //
                + "{\n" //
                + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                + "   v_color.a = v_color.a * (255.0/254.0);\n" //
                + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                + "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "}\n")
        val fragmentShader = Gdx.files.internal("shaders/test.glsl").readString()
        val shader = ShaderProgram(vertexShader, fragmentShader)
        ShaderProgram.pedantic = false
        if (!shader.isCompiled) throw IllegalArgumentException("Error compiling shader: " + shader.log)
        return shader
    }
}

