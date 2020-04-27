package sandbox.sandbox.def.def.sys

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.create
import dogengine.ecs.components.utility.CDeleteComponent
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CHide
import dogengine.ecs.systems.SystemPriority
import sandbox.sandbox.def.def.comp.*
import sandbox.sandbox.go.player.Player
import sandbox.sandbox.go.player.Player.DirectionSee.*
import kotlin.math.abs

class STools(private val player: Player) : EntitySystem() {

    init {
        priority = SystemPriority.UPDATE - 1
    }

    override fun addedToEngine(engine: Engine) {
        engine.addSystem(SelectionNearbyObjects(player))
        engine.addSystem(KickNearObject(player))
        super.addedToEngine(engine)
    }

    override fun update(deltaTime: Float) {

    }

    class SelectionNearbyObjects(private val player: Player) : IteratingSystem(Family.all(CHealth::class.java, CTransforms::class.java)
            .exclude(CHide::class.java).get()) {
        private val playerPos = Vector2()
        override fun processEntity(entity: Entity, deltaTime: Float) {
            playerPos.set(CTransforms[player].getCenterX(), CTransforms[player].getCenterY())
            if (CNearbyObject[entity] == null) {
                val x = CTransforms[entity].getCenterX()
                val y = CTransforms[entity].getCenterY()

                if (abs(playerPos.dst(x, y)) <= player.getCurrentTool().distance) {
                    entity.create<CNearbyObject>()
                }
            } else {
                val x = CTransforms[entity].getCenterX()
                val y = CTransforms[entity].getCenterY()

                if (abs(playerPos.dst(x, y)) > player.getCurrentTool().distance) {
                    entity.create<CDeleteComponent> { componentRemove = CNearbyObject[entity] }
                }
            }
        }
    }

    class KickNearObject(private val player: Player) : EntitySystem() {
        override fun update(deltaTime: Float) {
            val entities = engine.getEntitiesFor(Family.all(CNearbyObject::class.java,
                    CHealth::class.java).exclude(CHide::class.java).get())
            de(entities)

        }

        fun de(entities: ImmutableArray<Entity>) {
            if (player.getCurrentTool().isActive && player.getCurrentTool().isFinish) {
                player.getCurrentTool().isFinish = false
                val lineA = getA(player)
                val direction = Vector2()
                when (player.directionSee) {
                    RIGHT -> direction.set(1f,0f)
                    UP -> direction.set(0f,1f)
                    DOWN -> direction.set(0f,-1f)
                    LEFT -> direction.set(-1f,0f)
                }
                val dist = player.getCurrentTool().distance
                entities.sorted().forEach {
                    val lineB = getA(player).cpy().add(dist*direction.x, dist*direction.y)
                    val rectObject = getRect(it)
                    if (Intersector.intersectSegmentRectangle(lineA, lineB, rectObject)) {
                        it.addShakeAndExtract()
                        return
                    }
                }
            }
        }

        private fun ImmutableArray<Entity>.sorted(): List<Entity> {
            return this.sortedBy { CTransforms[it].getCenterX() }.sortedBy { CTransforms[it].getCenterY() }.filter {
                CObjectOnMap[it] != null &&
                        player.getCurrentTool().isWork(CObjectOnMap[it].typeObject)
            }

        }

        private fun getRect(it: Entity): Rectangle {
            return Rectangle(CTransforms[it].position.x, CTransforms[it].position.y,
                    CTransforms[it].size.width, CTransforms[it].size.height)
        }

        private fun getA(player: Player): Vector2 {
            return Vector2(CTransforms[player].getCenterX(), CTransforms[player].getCenterY())
        }

        private fun Entity.addShakeAndExtract() {
            this.create<CShack> {
                duration = 0.15f
            }
            this.create<CExtraction> {
                force = player.getCurrentTool().force.value
            }
        }
    }
}