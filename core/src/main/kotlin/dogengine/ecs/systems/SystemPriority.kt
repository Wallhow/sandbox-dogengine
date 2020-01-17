package dogengine.ecs.systems

object SystemPriority {
    const val AFTER_UPDATE = 10000
    const val DRAW = 5000
    const val UPDATE = 0
    const val PHYSICS = UPDATE-500
    const val BEFORE_UPDATE = -10000
}