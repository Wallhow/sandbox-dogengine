package sandbox.sandbox.go.environment.objects

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import dogengine.drawcore.CDrawable
import dogengine.drawcore.DrawTypes
import dogengine.drawcore.CAtlasRegions
import dogengine.ecs.components.create
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.redkin.physicsengine2d.variables.Types
import dogengine.utils.Size
import dogengine.utils.log
import sandbox.go.environment.items.dropOnMap
import sandbox.sandbox.go.environment.AGameObjectOnMap
import sandbox.sandbox.go.objects.ItemList
import sandbox.sandbox.go.objects.ObjectList

class Tree(position: Vector2) : AGameObjectOnMap(objectType = ObjectList.TREE) {
    private val itemType = ItemList.WOOD
    private val itemType2 = ItemList.GRASS

    init {
        val tex: TextureRegion = atlas.findRegion("tree_node")
        create<CAtlasRegions> {
            this.regions.apply {
                add(atlas.findRegion(objectType.resourcesName))
                add(atlas.findRegion("tree_node"))
                add(atlas.findRegion("tree_node"))
                add(atlas.findRegion("tree_node"))
                add(atlas.findRegion("tree_node"))
                add(atlas.findRegion("tree_node_finale"))
            }
            this.directionDraw.set(0f, 1f)
        }
        create<CDrawable>() {
            this.batchable = true
            this.drawType = DrawTypes.MULTI_REGIONS
        }

        createCTransform(position, Size(tex.regionWidth * 2f, tex.regionHeight * 2f))
        val t = CTransforms[this]
        createCPhysicsDef(t.size.width / 2f - (t.size.width / 2f) / 2, 0f, t.size.width / 2f, t.size.height / 5, sensor = false, type = Types.TYPE.DYNAMIC)
        createCHealth(21f, itemType = null) { befDead() }
        name = "tree"
    }

    private fun befDead() {
        val s = CTransforms[this].size
        s.setNewHeight(s.height * CAtlasRegions[this].regions.size)
        log(s)

        dropOnMap(3, 5, itemType2,s)
        //dropOnMap(0, 0, itemType)
        dropOnMap(3, 4, ItemList.BRANCH,s)
    }
}