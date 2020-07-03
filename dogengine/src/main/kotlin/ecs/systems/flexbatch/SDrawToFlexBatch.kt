package dogengine.ecs.systems.flexbatch

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.MathUtils.cos
import com.badlogic.gdx.math.MathUtils.sin
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.cyphercove.flexbatch.CompliantBatch
import com.cyphercove.flexbatch.FlexBatch
import com.cyphercove.flexbatch.batchable.Quad2D
import com.cyphercove.flexbatch.utils.AttributeOffsets
import com.cyphercove.flexbatch.utils.BatchablePreparation
import com.google.inject.Inject
import dogengine.Kernel
import dogengine.ecs.components.createComponent
import dogengine.ecs.components.draw.CDraw
import dogengine.ecs.components.getCTransforms
import dogengine.ecs.components.utility.CDeleteMe
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CHide
import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent
import dogengine.ecs.systems.SystemPriority
import dogengine.ecs.systems.draw.DrawComparator
import dogengine.ecs.systems.utility.STime
import dogengine.shadow2d.systems.SShadow2D
import dogengine.utils.GameCamera
import dogengine.utils.Size
import dogengine.utils.system


class SDrawToFlexBatch @Inject constructor(val camera: OrthographicCamera, val spriteBatch: SpriteBatch) : SortedIteratingSystem(Family.all(CDraw::class.java, CTransforms::class.java)
        .exclude(CHide::class.java).get(), DrawComparator.comparator) {
    private lateinit var flexBatch: CompliantBatch<Quad2D>
    private lateinit var flexBatchBamp: FlexBatch<BumpQuad>

    //private lateinit var atlas_n: TextureAtlas
    private lateinit var bumpShader: ShaderProgram
    private val drawables: Array<DrawableData> = Array()
    private val dot = Kernel.getInjector().getProvider(Kernel.DotTexture::class.java).get().get()

    private val fbo = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, false)


    private val solidBatch: FlexBatch<SolidQuad> = FlexBatch(SolidQuad::class.java, 1000, 0)

    class SolidQuad : Quad2D() {
        override fun getNumberOfTextures(): Int {
            return 0
        }
    }

    init {
        priority = SystemPriority.DRAW
    }

    override fun addedToEngine(engine: Engine) {
        flexBatch = CompliantBatch(Quad2D::class.java, 10000, false, false)
        val s = ShaderProgram(Gdx.files.internal("assets/shaders/default/def.vert").readString(),
                Gdx.files.internal("assets/shaders/default/def.frag").readString())
        if (!s.isCompiled)
            Gdx.app.log("shader error", s.log)
        flexBatch.shader = s

        solidBatch.shader = ShaderProgram(BatchablePreparation.generateGenericVertexShader(0),
                BatchablePreparation.generateGenericFragmentShader(0))

        flexBatchBamp = FlexBatch(BumpQuad::class.java, 1000, 0)
        bumpShader = ShaderProgram(Gdx.files.internal("assets/shaders/bump.vert").readString(),
                Gdx.files.internal("assets/shaders/bump.frag").readString());
        if (!bumpShader.isCompiled)
            Gdx.app.log("bump shader error", bumpShader.log);

        bumpShader.begin()
        bumpShader.setUniformf("u_ambient", Color(1f, 1f, 1f, 1f))
        bumpShader.setUniformf("u_specularStrength", 0f)
        bumpShader.setUniformf("u_attenuation", 0.002f)
        bumpShader.end()

        flexBatchBamp.shader = bumpShader
        flexBatchBamp.flush()


        engine.addEntityListener(Family.all(CDraw::class.java, CTransforms::class.java)
                .exclude(CHide::class.java).get(), this)

        gameCam.getCamera().apply {
            near = -1f
            far = 1000f
        }

        //atlas_n = TextureAtlas(Gdx.files.internal("assets/atlas/matlas_n.atlas"))
        //super.addedToEngine(engine)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val drawable = CDraw[entity]
        val transforms = entity.getCTransforms()

        when {
            CBump[entity] != null -> {
                //bumpQuad.centerOrigin().positionByOrigin(camera.position.x, camera.position.y)
                drawables.add(DrawableData(createBumpQuad(drawable, transforms, CBump[entity].normalMap),
                        DrawableType.Bump, entity))
            }
            CSolidQuad[entity] != null -> {
                val quad2D = Quad2D()
                quad2D.textureRegion(dot)
                        .position(transforms.position.x + drawable.offsetX, transforms.position.y + drawable.offsetY)
                        .origin(transforms.getCenterX() - transforms.position.x, transforms.getCenterY() - transforms.position.y)
                        .size(transforms.size.width, transforms.size.height)
                        .scale(transforms.size.scaleX, transforms.size.scaleY)
                        .rotation(transforms.angle)
                        .color(drawable.tint)
                drawables.add(DrawableData(quad2D, DrawableType.Default, entity))
                entity.add(createComponent<CDeleteMe> { })
            }
            else -> {
                drawables.add(DrawableData(createDefaultQuad(drawable, transforms), DrawableType.Default, entity))
            }
        }

    }

    private val gameCam = Kernel.getInjector().getInstance(GameCamera::class.java)
    private val sunPosition = Vector3()

    private val start = 4f
    val end = 24f
    override fun update(deltaTime: Float) {
        forceSort()
        camera.update()
        drawables.clear()
        super.update(deltaTime)


        spriteBatch.projectionMatrix = camera.combined
        val hour = getCurrentHour()
        val d = 180 / (end - start)

        sunPosition.set(camera.position.x + gameCam.getViewport().worldHeight * cos((hour * d - 45) * MathUtils.degreesToRadians),
                camera.position.y + gameCam.getViewport().worldHeight * sin((hour * d - 45) * MathUtils.degreesToRadians), 100f);




        flexBatchBamp.projectionMatrix = camera.combined
        /*fbo.begin()
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)


        flexBatchBamp.begin()
        bumpShader.setUniformf("u_camPosition", camera.position);
        bumpShader.setUniformf("u_lightPosition", bumpLightPosition);

        drawables.filter { it.second == DrawableType.Bump }.forEach {
            flexBatchBamp.draw(it.first)
        }
        flexBatchBamp.end()
        fbo.end()
        val texFBO = fbo.colorBufferTexture*/

        /*  val x = gameCam.getCamera().position.x - gameCam.getScaledViewport().halfWidth
          val y = gameCam.getCamera().position.y - gameCam.getScaledViewport().halfHeight
          val w = gameCam.getScaledViewport().width
          val h = gameCam.getScaledViewport().height
  */


        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        flexBatch.projectionMatrix = camera.combined
        flexBatch.begin()

        drawables.forEach {
            val q = it.quad2D
            if (it.type == DrawableType.Bump) {
                flexBatch.draw().textureRegion((q as BumpQuad).getTextureRegion())
                        .position(q.x, q.y).size(q.size.width, q.size.height)
            } else
                flexBatch.draw(q)


        }

        val smap = SShadow2D.shadowMap

        //flexBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
        flexBatch.enableBlending()
        flexBatch.draw().textureRegion(smap.texture).position(smap.position)
                .size(smap.size.width, smap.size.height)
                .origin(smap.size.width / 2f, smap.size.height / 2)
                .scale(1f, 1f)
                .color(1f, 1f, 1f, 0.6f)

        flexBatch.end()


    }


    override fun entityAdded(entity: Entity) {
        //log(CTransforms[entity].position.toString())
        if (CSolidQuad[entity] == null) {
            val drawable = CDraw[entity]
            val transforms = entity.getCTransforms()
            if (drawable != null && transforms != null) {
                val texture = drawable.texture!!

                if (transforms.size.width == -1f || transforms.size.height == -1f) {
                    transforms.size.setNewWidth(texture.regionWidth.toFloat())
                    transforms.size.setNewHeight(texture.regionHeight.toFloat())
                }
            }
        }
        super.entityAdded(entity)
    }

    enum class DrawableType {
        Solid,
        Default,
        Bump
    }

    private fun getCurrentHour(): Float {
        var h = 0f
        system<STime> {
            h = this.getCurrentHour()
        }
        return h
    }

    private fun createBumpQuad(draw: CDraw, transforms: CTransforms, normalMap: TextureRegion): BumpQuad {
        return BumpQuad().apply {
            shininess(0f)
                    .textureRegion(draw.texture as TextureRegion)
                    .textureRegion(normalMap)
                    .position(transforms.position.x + draw.offsetX, transforms.position.y + draw.offsetY)
                    .origin(transforms.getCenterX() - transforms.position.x, transforms.getCenterY() - transforms.position.y)
                    .size(transforms.size.width, transforms.size.height)
                    .scale(transforms.size.scaleX, transforms.size.scaleY)
                    .rotation(transforms.angle)
        }

    }

    private fun createDefaultQuad(draw: CDraw, transforms: CTransforms): Quad2D {
        return Quad2D().apply {
            textureRegion(draw.texture)
                    .position(transforms.position.x + draw.offsetX, transforms.position.y + draw.offsetY)
                    .origin(transforms.getCenterX() - transforms.position.x, transforms.getCenterY() - transforms.position.y)
                    .size(transforms.size.width, transforms.size.height)
                    .scale(transforms.size.scaleX, transforms.size.scaleY)
                    .rotation(transforms.angle)
                    .color(draw.tint)
        }
    }


    private data class DrawableData(val quad2D: Quad2D, val type: DrawableType, val entity: Entity)
}


class BumpQuad : Quad2D() {
    var shininess = 0f
    private var textureRegion: TextureRegion? = null
    var size: Size = Size()
    fun shininess(shininess: Float): BumpQuad {
        this.shininess = shininess
        return this
    }

    fun getTextureRegion(): TextureRegion {
        return textureRegion as TextureRegion
    }

    override fun textureRegion(region: TextureRegion): Quad2D {
        if (textureRegion == null)
            textureRegion = region
        return super.textureRegion(region)
    }

    override fun size(width: Float, height: Float): Quad2D {
        size.set(width, height)
        return super.size(width, height)
    }

    // Must have texture set first.
    fun centerOrigin(): BumpQuad {
        val region = regions[0]
        width = (region.u2 - region.u) * textures[0].width
        height = (region.v2 - region.v) * textures[0].height
        origin(width / 2f, height / 2f)
        return this
    }

    fun positionByOrigin(x: Float, y: Float): BumpQuad {
        position(x - originX, y - originY)
        return this
    }

    override fun getNumberOfTextures(): Int {
        return 2
    }

    override fun addVertexAttributes(attributes: Array<VertexAttribute>) {
        super.addVertexAttributes(attributes)
        attributes.add(VertexAttribute(VertexAttributes.Usage.Generic, 1, "a_rotation"))
        attributes.add(VertexAttribute(VertexAttributes.Usage.Generic, 1, "a_shininess"))
    }

    override fun apply(vertices: FloatArray, vertexStartingIndex: Int, offsets: AttributeOffsets, vertexSize: Int): Int {
        super.apply(vertices, vertexStartingIndex, offsets, vertexSize)
        val rotation = rotation * MathUtils.degRad
        var rotationIndex = vertexStartingIndex + offsets.generic0
        vertices[rotationIndex] = rotation
        rotationIndex += vertexSize
        vertices[rotationIndex] = rotation
        rotationIndex += vertexSize
        vertices[rotationIndex] = rotation
        rotationIndex += vertexSize
        vertices[rotationIndex] = rotation
        val shininess = shininess
        var shininessIndex = vertexStartingIndex + offsets.generic1
        vertices[shininessIndex] = shininess
        shininessIndex += vertexSize
        vertices[shininessIndex] = shininess
        shininessIndex += vertexSize
        vertices[shininessIndex] = shininess
        shininessIndex += vertexSize
        vertices[shininessIndex] = shininess
        return 4
    }
}

class CBump : PoolableComponent {
    var normalMap: TextureRegion = TextureRegion()
    override fun reset() {

    }

    companion object : ComponentResolver<CBump>(CBump::class.java)
}

class CSolidQuad : PoolableComponent {
    var tint: Color = Color.WHITE.cpy()
    override fun reset() {
        tint.set(Color.WHITE)
    }

    companion object : ComponentResolver<CSolidQuad>(CSolidQuad::class.java)
}