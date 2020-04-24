package sandbox.sandbox.def.gui

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.systems.controllers.EventInputListener
import dogengine.map2D.CCell
import dogengine.utils.log
import sandbox.def.SWorldHandler
import sandbox.go.environment.objects.buiding.Workbench
import sandbox.sandbox.go.player.Player
import space.earlygrey.shapedrawer.ShapeDrawer
import kotlin.math.abs

class BuildConstruction (val player: Player,val engine: Engine) : EventInputListener() {
    var build = false
    override fun touchDown(x: Float, y: Float): Boolean {
        val itemBuild = SWorldHandler.itemIDBuild
        itemBuild?.let {
            if (build) {
                val xx = (x/32).toInt()
                val yy = (y/32).toInt()
                val pos = Vector2(xx*32f, yy*32f)
                log(pos)
                engine.addEntity(Workbench(pos))
                player.getInventory().pop()
                build =false
                SWorldHandler.itemIDBuild = null
            }
        }
        return super.touchDown(x, y)
    }

    fun update() {
        if(SWorldHandler.itemIDBuild!=null && !build) {
            build = true
        }
    }
    fun draw(shapeDrawer: ShapeDrawer) {
        if(build) {
            engine.getEntitiesFor(Family.all(CCell::class.java).get()).filter {
                val pp = CTransforms[player].position.cpy().add(CTransforms[player].getCenterX(), CTransforms[player].getCenterY())
                val ep = CTransforms[it].position.cpy().add(CTransforms[it].getCenterX(), CTransforms[it].getCenterY())
                abs(pp.dst(ep)) <= 32f * 4f
            }.forEach {
                shapeDrawer.rectangle(CTransforms[it].getRect(), Color.CYAN.apply { a=0.5f })
            }
        }
    }

}