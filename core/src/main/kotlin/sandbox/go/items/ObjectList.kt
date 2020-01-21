package sandbox.sandbox.go.items

enum class ObjectList(val name_res: String, val id: Int,val nameMainObj: String = "") {
    WOOD("wood_item",1),
    GRASS("grass_item", 0),
    ROCK("rock_item",3),
    SANDSTONE("sandstone_item",4,"sandstone")
}