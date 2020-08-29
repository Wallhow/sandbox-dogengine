package sandbox.sandbox.go.objects

import sandbox.sandbox.go.objects.Counter.nextId

enum class ObjectList(val resourcesName: String, val id: Int) {
    ZERO("null", nextId()),
    ROCK("rock",nextId()),
    WOOD("wood", nextId()),
    SANDSTONE("sandstone", nextId()),
    WORKBENCH("workbench_object",nextId()),
    BONFIRE1("bonfire_object",nextId()),
    TREE("tree_root",nextId())
}