package dogengine.ecs.def

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity

open class ComponentResolver<T : Component>(componentClass: Class<T>) {
    private val mapper: ComponentMapper<T> = ComponentMapper.getFor(componentClass)
    operator fun get(entity: Entity) = mapper.get(entity)
}