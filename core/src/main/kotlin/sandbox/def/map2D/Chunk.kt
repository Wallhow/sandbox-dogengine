package sandbox.sandbox.def.map2D


class Chunk(width: Int,height: Int) {
    var x: Int = 0
    var y: Int = 0
    var index : Int = 0
    var cells: Array<Cell?> = arrayOfNulls(width * height)
    var neighborses : Array<Chunk?> = arrayOfNulls(3*3)
}