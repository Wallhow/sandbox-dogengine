package sandbox.sandbox.def.map

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import dogengine.Kernel
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.map2D.Cell
import dogengine.map2D.Map2D
import dogengine.utils.vec2
import map2D.TypeData
import sandbox.dev.map.HeightTypes
import sandbox.go.environment.objects.Rock
import sandbox.go.environment.objects.Sandstone
import sandbox.go.environment.objects.Wood

class CreatedCellMapListener (private val tileSize: Float) {
    fun createCell(cell: Cell, type: HeightTypes, map: Map2D) {
        when (type) {
            HeightTypes.SAND -> {
                if(chance(2f)) {
                    val obj = Sandstone(vec2(cell.x*tileSize,cell.y*tileSize))
                    map.addInObjLayerAndEngine(obj,cell)
                }
            }
            HeightTypes.ROCK -> {
                if(chance(2f)) {
                    val obj = Rock(vec2(cell.x*tileSize,cell.y*tileSize))
                    map.addInObjLayerAndEngine(obj,cell)
                }
            }
            HeightTypes.GRASS -> {
                if(chance(0.5f)) {
                    val obj = Wood(Vector2(cell.x*tileSize,cell.y*tileSize))
                    map.addInObjLayerAndEngine(obj,cell)
                }
            }
        }

    }

    private fun Map2D.addInObjLayerAndEngine(obj: Entity,cell: Cell) {
        val size = CTransforms[obj].size
        for (x in 0..(size.width/tileSize).toInt()) {
            this.getLayer("objects").getCell(cell.x+x,cell.y).data.put(TypeData.ObjectOn,obj)
        }

        engine.addEntity(obj)
    }


    private fun chance(percent: Float) : Boolean {
        val r = MathUtils.random(0f,100f)
        return r>=100f-percent
    }
    val engine: Engine = Kernel.getInjector().getInstance(Engine::class.java)

}