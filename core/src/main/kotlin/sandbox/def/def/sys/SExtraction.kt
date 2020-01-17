package sandbox.sandbox.def.def.sys

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import dogengine.ecs.components.create
import dogengine.ecs.components.utility.CDeleteComponent
import dogengine.ecs.components.utility.CDeleteMe
import dogengine.ecs.components.utility.CName
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.systems.SystemPriority
import sandbox.sandbox.def.def.comp.CExtraction
import sandbox.sandbox.def.def.comp.CHealth
import sandbox.sandbox.go.items.GrassItem
import sandbox.sandbox.go.items.WoodItem

class SExtraction : IteratingSystem(Family.all(CHealth::class.java,CExtraction::class.java).get()) {
    init {
        priority = SystemPriority.UPDATE
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val health = CHealth[entity]
        val ext = CExtraction[entity]
        health.health-=ext.force
        if(health.health<0) {
            if(CName[entity].name == "wood") {
                entity.create<CDeleteMe>()
                val pos = CTransforms[entity].position.cpy()
                val size = CTransforms[entity].size
                pos.add(MathUtils.random(size.halfWidth-15f,size.halfWidth+15f),MathUtils.random(-6f,6f))
                engine.addEntity(WoodItem(pos))
                pos.set(CTransforms[entity].position)
                pos.add(MathUtils.random(size.halfWidth-15f,size.halfWidth+15f),MathUtils.random(-6f,6f))
                engine.addEntity(WoodItem(pos))
                pos.set(CTransforms[entity].position)
                pos.add(MathUtils.random(size.halfWidth-15f,size.halfWidth+15f),MathUtils.random(-6f,6f))
                engine.addEntity(WoodItem(pos))

                pos.set(CTransforms[entity].position)
                pos.add(MathUtils.random(size.halfWidth-15f,size.halfWidth+15f),MathUtils.random(-6f,36f))
                engine.addEntity(GrassItem(pos))

                pos.set(CTransforms[entity].position)
                pos.add(MathUtils.random(size.halfWidth-15f,size.halfWidth+15f),MathUtils.random(-6f,36f))
                engine.addEntity(GrassItem(pos))

                pos.set(CTransforms[entity].position)
                pos.add(MathUtils.random(size.halfWidth-15f,size.halfWidth+15f),MathUtils.random(-6f,36f))
                engine.addEntity(GrassItem(pos))
            }
        }
        entity.create<CDeleteComponent> {
            componentRemove = ext
        }
    }
}