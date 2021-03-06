package sandbox.dev.gui

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import dogengine.Kernel
import dogengine.ecs.components.components
import dogengine.ecs.components.create
import dogengine.ecs.components.createEntity
import dogengine.ecs.components.draw.CTextureRegion
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.logic.CUpdate
import dogengine.ecs.systems.controllers.EventInputListener
import dogengine.utils.extension.get
import dogengine.utils.vec2
import map2D.TypeData
import map2D.Vector2Int
import sandbox.dev.ecs.sys.SWorldHandler
import sandbox.dev.world.MessagesType
import sandbox.sandbox.go.assetAtlas
import sandbox.sandbox.go.player.Player
import kotlin.math.abs

class BuildConstruction(val player: Player, val engine: Engine) : EventInputListener() {
    private var drawGrid: Boolean = false
    private var build = false
    private val grid: Entity
    private val messenger = Kernel.getInjector()[MessageManager::class.java]

    init {
        grid = gridCreate()

        engine.addEntity(grid)
    }

    private fun gridCreate(): Entity {
        return engine.createEntity {
            components {
                create<CTransforms> {
                    size.set(5 * 32f, 5 * 32f)
                    position.set(-512f, -512f)
                }
                create<CUpdate> {
                    var time = 0f
                    val maxT = 0.5f
                    val dd = maxT / 1f
                    var startA = 1f
                    var endA = 0.5f
                    func = {
                        if (drawGrid) {
                            time += it
                            CTextureRegion[grid].color.a = Interpolation.fade.apply(startA, endA, time)
                            if (time >= 1f) {
                                time = 0f
                                val d = endA
                                endA = startA
                                startA = d
                            }
                            CTransforms[this@components].zIndex = CTransforms[player].zIndex - 1
                        } else {
                            CTransforms[this@components].zIndex = Int.MAX_VALUE
                        }
                    }
                }
                create<CTextureRegion> {
                    texture = assetAtlas().findRegion("grid_build")
                }
            }
        }
    }

    override fun touchDown(x: Float, y: Float): Boolean {
        if (build) {
            build(x, y)
        }
        return super.touchDown(x, y)
    }

    fun update() {
        if (SWorldHandler.itemIDBuild == player.getInventory().whatSelected() && !build) {
            build = true
            drawGrid = true
        } else if (SWorldHandler.itemIDBuild != player.getInventory().whatSelected()) {
            build = false
            drawGrid = false
            SWorldHandler.itemIDBuild = null
            CTransforms[grid].position.set(-512f, -512f)
        }
        if (drawGrid) {
            val x = CTransforms[player].getCenterX()
            val y = CTransforms[player].getCenterY() - 16f
            val xx = (x / 32).toInt() - 2
            val yy = (y / 32).toInt() - 2
            CTransforms[grid].position.set(xx * 32f, yy * 32f)
        }
    }


    private val dstToBuild = 1.47f
    private fun build(x: Float, y: Float) {
        val itemBuild = SWorldHandler.itemIDBuild
        val xx = (x / 32).toInt()
        val yy = (y / 32).toInt()

        itemBuild?.let {
            val pos = Vector2(xx.toFloat(), yy.toFloat())
            val playerOrigin = vec2((CTransforms[player].getCenterX() / 32).toInt() * 1f, (CTransforms[player].getCenterY() / 32).toInt() * 1f)
            if (it.buildType != null) {
                if (abs(playerOrigin.dst(pos)) <= dstToBuild) {
                    //Сообщение о том, что мы собираемся воздвигать постройки
                    messenger.dispatchMessage(MessagesType.WORLD_BUILD, mapOf(
                            1 to Vector2Int(xx, yy),
                            2 to TypeData.ObjectOn,
                            3 to it.buildType
                    ))
                    player.getInventory().pop()
                    build = false
                    drawGrid = false
                    CTransforms[grid].position.set(Int.MIN_VALUE * 1f, Int.MIN_VALUE * 1f)
                }
            }
        }
    }
}