package sandbox.sandbox.go.environment

import sandbox.go.environment.Counter.nextId

enum class ObjectList(val resourcesName: String, val id: Int) {
    ZERO("null", nextId()),
    ROCK("rock",nextId()),
    WOOD("wood", nextId()),
    SANDSTONE("sandstone", nextId()),
    WORKBENCH("workbench_object",nextId());
}