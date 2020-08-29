package dogengine.drawcore

import dogengine.drawcore.drawfunctions.BatchDrawFunction
import dogengine.drawcore.drawfunctions.MultiRegionsDrawFunction
import dogengine.drawcore.drawfunctions.SolidDrawFunction


object DrawTypes {
    const val MULTI_REGIONS = 4
    const val SOLID = 3
    const val BATCH = 0
    const val MAP = 1
    val batchDrawFunction = BatchDrawFunction()
    val solidDrawFunction = SolidDrawFunction()
    val multiRegionsDrawFunction = MultiRegionsDrawFunction()
}