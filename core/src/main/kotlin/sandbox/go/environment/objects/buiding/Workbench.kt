package sandbox.go.environment.objects.buiding

import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.create
import dogengine.redkin.physicsengine2d.variables.Types
import dogengine.utils.Size
import sandbox.go.environment.ItemList
import sandbox.sandbox.go.environment.AGameObjectOnMap
import sandbox.sandbox.go.environment.ObjectList
import sandbox.sandbox.go.environment.objects.buiding.CWorkbench

class Workbench (position:Vector2) : AGameObjectOnMap(ObjectList.WORKBENCH) {
    init {
        createCAtlasRegion()
        createCTransform(position, Size(getAtlasRegion().regionWidth*0.5f ,
                getAtlasRegion().regionHeight * 0.5f))
        createCPhysicsDef(type = Types.TYPE.DYNAMIC)
        createCHealth(10f,itemType = ItemList.WORKBENCH)
        create<CWorkbench> {
            type = objectType
        }
    }
}