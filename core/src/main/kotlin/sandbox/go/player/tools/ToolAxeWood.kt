package sandbox.go.player.tools

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import dogengine.ecs.components.*
import dogengine.ecs.components.utility.CDeleteMe
import dogengine.ecs.components.utility.logic.CDefaultPhysics2d
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.logic.CUpdate
import dogengine.redkin.physicsengine2d.variables.Types
import dogengine.utils.Size
import sandbox.R
import sandbox.sandbox.go.player.Player

class ToolAxeWood(private val player: Player, am: AssetManager) : ATool() {
    override val attackSpeed: Float = 0.75f
    override val power = 2f
    override val name: String ="axe_item"
    override val image: TextureRegion = am.get(R.matlas0, TextureAtlas::class.java).findRegion(name)

    override fun hit() {
        if (!isActive) {
            super.hit()
            engine.addEntity(createAxe())
        }
    }

    private fun createAxe(): Entity {
        return engine.createEntity {
            components {
                val pp = CTransforms[player]
                val size2 = 24f
                create<CTransforms> {
                    size = Size(size2, size2)
                    position.set(pp.position.x, pp.position.y)
                    zIndex = Int.MAX_VALUE
                }
                create<CUpdate> {
                    var time = 0f
                    func = {
                        time += it
                        if (time >= (1 - attackSpeed)) {
                            create<CDeleteMe>()
                        }
                        updatePositionTool(player, this@components, CDefaultPhysics2d[this@components].rectangleBody!!)
                    }
                }
                create<CDefaultPhysics2d> {
                    this.type = Types.TYPE.SENSOR
                    this.name = this@ToolAxeWood.name
                    val t = CTransforms[this@components]
                    var nWidth = 0f
                    var nHeight = 0f
                    if (player.directionSee == Player.DirectionSee.DOWN ||
                            player.directionSee == Player.DirectionSee.UP) {
                        nWidth = t.size.width
                        nHeight = t.size.height + t.size.halfHeight
                    } else if (player.directionSee == Player.DirectionSee.RIGHT ||
                            player.directionSee == Player.DirectionSee.LEFT) {
                        nWidth = t.size.width + t.size.halfWidth
                        nHeight = t.size.height
                    }

                    createBody(CTransforms[player], 0f, 0f, nWidth, nHeight,
                            Types.TYPE.DYNAMIC, name)
                            .apply {
                                updatePositionTool(player, this@components, this)
                                createSensor()
                            }
                }
            }
        }
    }
}