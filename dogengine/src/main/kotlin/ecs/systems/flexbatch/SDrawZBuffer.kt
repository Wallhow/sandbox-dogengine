package dogengine.ecs.systems.flexbatch

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Array
import com.cyphercove.flexbatch.FlexBatch
import com.cyphercove.flexbatch.batchable.LitQuad3D
import com.cyphercove.flexbatch.batchable.Quad
import com.cyphercove.flexbatch.batchable.Quad2D
import com.cyphercove.flexbatch.utils.BatchablePreparation
import com.google.inject.Inject
import dogengine.Kernel
import dogengine.ecs.components.createComponent
import dogengine.ecs.components.draw.CDraw
import dogengine.ecs.components.getCTransforms
import dogengine.ecs.components.utility.CDeleteMe
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CHide
import dogengine.ecs.systems.SystemPriority
import dogengine.utils.GameCamera

class SDrawZBuffer @Inject constructor(val camera: OrthographicCamera) : IteratingSystem(Family.all(CDraw::class.java,CTransforms::class.java).get()),EntityListener {
    private lateinit var flexBatch: FlexBatch<Quad2D>
    private val drawables: Array<Pair<out Quad, SDrawToFlexBatch.DrawableType>> = Array()
    private val dot = Kernel.getInjector().getProvider(Kernel.DotTexture::class.java).get().get()
    private val gameCam = Kernel.getInjector().getInstance(GameCamera::class.java)
    init {
        TODO("not use")
        priority = SystemPriority.DRAW+10
        gameCam.getCamera().position.z = 5f
        gameCam.getCamera().near = -10f
        gameCam.getCamera().far = 500f
    }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val drawable = CDraw[entity]
        val transforms = entity.getCTransforms()

        when {
            CBump[entity] != null -> {
                val bumpQuad = BumpQuad()
                bumpQuad.shininess(0f)
                        .textureRegion(drawable.texture as TextureRegion)
                        .textureRegion(CBump[entity].normalMap)
                        .position(transforms.position.x + drawable.offsetX, transforms.position.y + drawable.offsetY)
                        .origin(transforms.getCenterX() - transforms.position.x, transforms.getCenterY() - transforms.position.y)
                        .size(transforms.size.width, transforms.size.height)
                        .scale(transforms.size.scaleX, transforms.size.scaleY)
                        .rotation(transforms.angle)
                //bumpQuad.centerOrigin().positionByOrigin(camera.position.x, camera.position.y)
                drawables.add(Pair(bumpQuad, SDrawToFlexBatch.DrawableType.Bump))
            }
            CSolidQuad[entity]!=null -> {
                val quad2D = Quad2D()
                val quad3D = LitQuad3D()
                quad3D.textureRegion(dot)
                        .position(transforms.position.x + drawable.offsetX, transforms.position.y + drawable.offsetY,1f)
                        .origin(transforms.getCenterX() - transforms.position.x, transforms.getCenterY() - transforms.position.y)
                        .size(transforms.size.width, transforms.size.height)
                        .scale(transforms.size.scaleX, transforms.size.scaleY)
                        //.rotation(transforms.angle)
                        .color(drawable.tint)
                drawables.add(Pair(quad3D, SDrawToFlexBatch.DrawableType.Default))
                entity.add(createComponent<CDeleteMe> {  })
            }
            else -> {
                val quad2D = Quad2D()
                val quad3D = LitQuad3D()
                quad3D.textureRegion(drawable.texture)
                        .position(transforms.position.x + drawable.offsetX, transforms.position.y + drawable.offsetY,1f)
                        .origin(transforms.getCenterX() - transforms.position.x, transforms.getCenterY() - transforms.position.y)
                        .size(transforms.size.width, transforms.size.height)
                        .scale(transforms.size.scaleX, transforms.size.scaleY)
                        //.rotation(transforms.angle)
                        .color(drawable.tint)
                drawables.add(Pair(quad3D, SDrawToFlexBatch.DrawableType.Default))
            }
        }
    }

    override fun update(deltaTime: Float) {
        camera.update()
        drawables.clear()
        super.update(deltaTime)

        //Gdx.gl.glClearDepthf(0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)
        Gdx.gl.glDepthFunc(GL20.GL_LESS)

        flexBatch.projectionMatrix = camera.combined
        flexBatch.begin()
        flexBatch.enableBlending()


        drawables.forEach {
            val q = it.first
            val y_ =((q.y/32).toInt())
            val h_ = (gameCam.getWorldSize().height/32f).toInt()*1f


            if (it.second == SDrawToFlexBatch.DrawableType.Bump) {
                flexBatch.draw().textureRegion((q as BumpQuad).getTextureRegion())
                        .position(q.x, q.y).size(q.size.width, q.size.height)
            } else
                flexBatch.draw(q)
            val z_index = (y_/h_)

            flexBatch.shader.setUniformf("z_index",z_index)


        }
        flexBatch.end()
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST)
    }

    override fun addedToEngine(engine: Engine) {
        flexBatch = FlexBatch(Quad2D::class.java, 10000, 32767)
        val s = ShaderProgram(BatchablePreparation.generateGenericVertexShader(1),
                BatchablePreparation.generateGenericFragmentShader(1))

        if (!s.isCompiled)
            Gdx.app.log("shader error", s.log)
        flexBatch.shader = s

        engine.addEntityListener(Family.all(CDraw::class.java, CTransforms::class.java)
                .exclude(CHide::class.java).get(), this)
        super.addedToEngine(engine)
    }

    override fun entityRemoved(entity: Entity?) {

    }

    override fun entityAdded(entity: Entity) {
        if(CSolidQuad[entity]==null) {
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
    }
}