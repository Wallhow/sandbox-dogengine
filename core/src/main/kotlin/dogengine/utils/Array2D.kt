package dogengine.utils

/** Base class for containers that wrap around a single 1D array, treating it as a 2D array.
 *
 * @author MJ
 */
abstract class Array2D
/** @param width amount of columns.
 * @param height amount of rows.
 */(
        /** @return amount of columns.
         */
        val width: Int,
        /** @return amount of rows.
         */
        val height: Int) {

    /** @param size amount of columns and rows.
     */
    constructor(size: Int) : this(size, size) {}

    /** @param x column index.
     * @param y row index.
     * @return true if the coordinates are valid and can be safely used with getter methods.
     */
    fun isIndexValid(x: Int, y: Int): Boolean {
        return (x >= 0 && x < width) && y >= 0 && y < height
    }

    /** @param x column index.
     * @param y row index.
     * @return actual array index of the cell.
     */
    fun toIndex(x: Int, y: Int): Int {
        return x + y * width
    }

    /** @param index actual array index of a cell.
     * @return column index.
     */
    fun toX(index: Int): Int {
        return index % width
    }

    /** @param index actual array index of a cell.
     * @return row index.
     */
    fun toY(index: Int): Int {
        return index / width
    }

}