package sandbox.def

import com.badlogic.ashley.core.Engine
import dogengine.ashley.components.CHide
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import dogengine.ashley.components.CTransforms
import dogengine.ashley.components.draw.CAtlasRegion
import dogengine.ashley.components.draw.CTextureRegion
import dogengine.def.CName
import dogengine.def.GameEntity
import dogengine.utils.Size
import sandbox.R

class Progressbar(pos: Vector2,size: Size,private val maxStep:Int) {
    private val d = size.width/maxStep
    private val progressBackground: GameEntity
    private val progressLine : GameEntity
    init {
        progressBackground = object : GameEntity() {
            init {
                CName[this].name="progressbar"
                val pm = Pixmap(5,5,Pixmap.Format.RGBA8888)

                pm.setColor(Color.LIGHT_GRAY)
                pm.fillRectangle(0,0,3,3)

                add(CTransforms().apply { position.set(pos); size.set(size) })
                add(CTextureRegion(TextureRegion( Texture(pm))).apply { color  })
            }
        }
        progressLine = object : GameEntity() {
            init {
                val pm1 = Pixmap(5,5,Pixmap.Format.RGBA8888)
                pm1.setColor(Color.ROYAL)
                pm1.drawPixel(1,1)
                pm1.fillRectangle(1,1,4,4)
                add(CTransforms().apply { position.set(pos); size.set(size) ;size.setNewWidth(1f)})
                add(CAtlasRegion(TextureAtlas(R.matlas0),"knight"))
            }
        }

    }

    fun progress() {
        CTransforms[progressLine].size.setNewWidth(CTransforms[progressLine].size.width+d)
    }

    fun addToEngine(engine: Engine) {
        engine.addEntity(progressBackground)
        engine.addEntity(progressLine)
        println("progressbar added")
    }

    fun visible(flag:Boolean) {
        if(!flag) {
            progressBackground.add(CHide())
            progressLine.add(CHide())
        } else {
            if(CHide[progressBackground]!=null) progressBackground.remove(CHide::class.java)
            if(CHide[progressLine]!=null) progressLine.remove(CHide::class.java)
        }
    }
}