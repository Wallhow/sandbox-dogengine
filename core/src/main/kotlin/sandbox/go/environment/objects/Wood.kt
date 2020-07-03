package sandbox.go.environment.objects

import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.create
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.systems.flexbatch.CBump
import dogengine.redkin.physicsengine2d.variables.Types
import dogengine.shadow2d.components.CShadow
import dogengine.utils.Size
import sandbox.sandbox.go.environment.AGameObjectOnMap
import sandbox.go.environment.items.dropOnMap
import sandbox.go.environment.ItemList
import sandbox.sandbox.go.environment.ObjectList

class Wood (position : Vector2) : AGameObjectOnMap(objectType = ObjectList.WOOD) {
    private val itemType = ItemList.WOOD
    private val itemType2 = ItemList.GRASS
    init {
        createCAtlasRegion()
        val tex = getAtlasRegion()
        createCTransform(position,Size(tex.regionWidth * 2f,tex.regionHeight * 2f))
        val t = CTransforms[this]
        createCPhysicsDef(t.size.width/2f-(t.size.width / 6.5f)/2, 0f, t.size.width / 6.5f, t.size.height / 10,sensor = false,type = Types.TYPE.DYNAMIC)
        createCHealth(8f,itemType = null) { befDead() }
        create<CBump> {
            normalMap = atlas.findRegion(ObjectList.WOOD.resourcesName+"_01_n")
        }
        create<CShadow> {

        }
    }
    private fun befDead()  {
        dropOnMap(3,5, itemType2)
        dropOnMap(3,4, itemType)
    }
}