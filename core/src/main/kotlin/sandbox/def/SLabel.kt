package sandbox.def

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.google.inject.Inject
import dogengine.ashley.components.CTransforms

class SLabel @Inject constructor(val batch:SpriteBatch) : IteratingSystem(Family.all(CTransforms::class.java,CLabel::class.java).get()) {
    init {
        priority=20
    }
    override fun update(deltaTime: Float) {
        batch.begin()
        super.update(deltaTime)
        batch.end()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val label = CLabel[entity]
        if(label.fnt!=null) {
            println("draw ${label.labelText} ${CTransforms[entity].position.toString()}")
        }
        batch.color= label.fnt?.color
        label.fnt?.draw(batch,label.labelText,CTransforms[entity].position.x,CTransforms[entity].position.y)
        batch.color= Color.WHITE
    }
}