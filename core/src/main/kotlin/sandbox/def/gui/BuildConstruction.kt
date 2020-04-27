package sandbox.sandbox.def.gui

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import dogengine.actions.CTextureRegionAccessor
import dogengine.ecs.components.components
import dogengine.ecs.components.create
import dogengine.ecs.components.createComponent
import dogengine.ecs.components.createEntity
import dogengine.ecs.components.draw.CTextureRegion
import dogengine.ecs.components.utility.CDeleteComponent
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.logic.CUpdate
import dogengine.ecs.components.utility.visible.CHide
import dogengine.ecs.systems.controllers.EventInputListener
import dogengine.map2D.CCell
import dogengine.utils.log
import ktx.ashley.add
import ktx.ashley.entity
import sandbox.def.SWorldHandler
import sandbox.go.environment.objects.buiding.Workbench
import sandbox.sandbox.go.assetAtlas
import sandbox.sandbox.go.player.Player
import space.earlygrey.shapedrawer.ShapeDrawer
import kotlin.math.abs

class BuildConstruction (val player: Player,val engine: Engine) : EventInputListener() {
    private var drawGrid: Boolean = false
    private var build = false
    private val grid : Entity
    init {
        grid = create()

        engine.addEntity(grid)
    }
    private fun create(): Entity {
        return engine.createEntity {
            components {
                create<CTransforms> {
                    size.set(5*32f,5*32f)
                }
                create<CUpdate> {
                    var time = 0f
                    var maxT = 0.5f
                    val dd = maxT/1f
                    var startA = 1f
                    var endA = 0.5f
                    func = {
                        time+=it
                        CTextureRegion[grid].color.a = Interpolation.fade.apply(startA,endA,time)
                        if(time>=1f) {
                            time = 0f
                            val d = endA
                            endA=startA
                            startA = d
                        }
                        CTransforms[this@components].zIndex = CTransforms[player].zIndex-1
                    }
                }
                create<CTextureRegion> {
                    texture = assetAtlas().findRegion("grid_build")
                }
            }
        }
    }

    override fun touchDown(x: Float, y: Float): Boolean {
        build(x, y)
        return super.touchDown(x, y)
    }

    fun update() {
        if(SWorldHandler.itemIDBuild!=null && !build) {
            build = true
            drawGrid = true
        }
        if(drawGrid) {
            val x = CTransforms[player].getCenterX()
            val y = CTransforms[player].getCenterY() - 16f
            val xx = (x/32).toInt() - 2
            val yy = (y/32).toInt() - 2
            CTransforms[grid].position.set(xx*32f,yy*32f)
        }
    }


    private val dstToBuild = 32f
    private fun build(x: Float,y: Float) {
        val itemBuild = SWorldHandler.itemIDBuild
        itemBuild?.let {
            if (build) {
                val xx = (x/32).toInt()
                val yy = (y/32).toInt()
                val pos = Vector2(xx*32f, yy*32f)
                log(pos)
                if(abs(pos.dst(CTransforms[player].getCenterX(),CTransforms[player].getCenterY())) <= dstToBuild) {
                    engine.addEntity(Workbench(pos))
                    player.getInventory().pop()
                    build = false
                    drawGrid = false
                    CTransforms[grid].position.set(Int.MIN_VALUE * 1f, Int.MIN_VALUE * 1f)
                    SWorldHandler.itemIDBuild = null
                }
            }
        }
    }
}