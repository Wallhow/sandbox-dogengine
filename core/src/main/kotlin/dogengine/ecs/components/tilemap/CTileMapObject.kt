package dogengine.ecs.components.tilemap

import com.badlogic.ashley.core.Component
import dogengine.ecs.def.ComponentResolver

/**
 * Created by wallhow on 23.12.16.
 * Компонент который указывает на то, что сущность его имеющая является объектом карты
 */
class CTileMapObject : Component {
    companion object : ComponentResolver<CTileMapObject>(CTileMapObject::class.java)
}