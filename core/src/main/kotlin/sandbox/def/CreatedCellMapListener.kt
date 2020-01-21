package sandbox.sandbox.def

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.math.MathUtils
import dogengine.Kernel
import dogengine.tilemap.map2D.Cell
import dogengine.utils.vec2
import sandbox.sandbox.go.environment.models.Rock
import sandbox.sandbox.go.environment.models.Sandstone

class CreatedCellMapListener (private val tileSize: Float) {
    fun createCell(cell: Cell, type: Map2DGenerator.HeightTypes) {
        when (type) {
            Map2DGenerator.HeightTypes.SAND -> {
                if(chance(2f)) {
                    engine.addEntity(Sandstone(vec2(cell.x*tileSize,cell.y*tileSize)))
                }
            }
            Map2DGenerator.HeightTypes.ROCK -> {
                if(chance(2f)) {
                    engine.addEntity(Rock(vec2(cell.x*tileSize,cell.y*tileSize)))
                }
            }
        }

    }


    private fun chance(percent: Float) : Boolean {
        val r = MathUtils.random(0f,100f)
        return r<=percent
    }
    val engine: Engine = Kernel.getInjector().getInstance(Engine::class.java)

}