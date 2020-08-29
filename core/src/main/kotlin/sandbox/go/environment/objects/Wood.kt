package sandbox.go.environment.objects

import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.create
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CCameraLook
import dogengine.redkin.physicsengine2d.variables.Types
import dogengine.utils.Size
import sandbox.go.environment.items.dropOnMap
import sandbox.sandbox.go.environment.AGameObjectOnMap
import sandbox.sandbox.go.objects.ItemList
import sandbox.sandbox.go.objects.ObjectList

class Wood(position: Vector2) : AGameObjectOnMap(objectType = ObjectList.WOOD) {
    private val itemType = ItemList.WOOD
    private val itemType2 = ItemList.GRASS

    init {
        createCAtlasRegion()
        val tex = getAtlasRegion()
        createCTransform(position, Size(tex.regionWidth * 2f, tex.regionHeight * 2f))
        val t = CTransforms[this]
        createCPhysicsDef(t.size.width / 2f - (t.size.width / 6.5f) / 2, 0f, t.size.width / 6.5f, t.size.height / 10, sensor = false, type = Types.TYPE.DYNAMIC)
        createCHealth(8f, itemType = null) { befDead() }
        name = "wood"
    }

    private fun befDead() {
        //dropOnMap(1,2, itemType2)
        dropOnMap(3, 4, itemType)
        dropOnMap(3, 3, ItemList.BRANCH)
    }
}