package sandbox.sandbox.drawfunctions

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import dogengine.Kernel
import dogengine.drawcore.drawfunctions.BatchDrawFunction
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.utils.GameCamera

class MyDrawFunct2(private val shadowFBO: FrameBuffer) : BatchDrawFunction() {
    private var shadowShader2: ShaderProgram =
            ShaderProgram(Gdx.files.internal("assets/shaders/shadow/shadow_final.vert").readString(),
                    Gdx.files.internal("assets/shaders/shadow/shadow_final.frag").readString())
    private val defaultShaderProgram = SpriteBatch.createDefaultShader()
    init {
        if (!shadowShader2.isCompiled)
            Gdx.app.log("shader error", shadowShader2.log)

        Kernel.getInjector().getInstance(InputMultiplexer::class.java).addProcessor(ShaderRefresh(shadowShader2))
    }

    override fun draw(spriteBatch: SpriteBatch, entity: Entity) {
        spriteBatch.shader = shadowShader2
        shadowFBO.colorBufferTexture.bind(1)
        shadowShader2.setUniformi("shadow_texture",1)
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
        val gameCam= Kernel.getInjector().getInstance(GameCamera::class.java)
        shadowShader2.setUniformf("u_globalPosition", CTransforms[entity].position.y/gameCam.getWorldSize().height)
        shadowShader2.setUniformf("u_world_size", Vector2(gameCam.getWorldSize().width,gameCam.getWorldSize().height))
        super.draw(spriteBatch, entity)
        spriteBatch.shader = defaultShaderProgram
    }
}