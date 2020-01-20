package sandbox.sandbox.go.items

interface Item {
    val type: Items
}

enum class Items(val name_res: String,val id: Int) {
    WOOD("wood_item",1),
    GRASS("grass_item", 0)
}
