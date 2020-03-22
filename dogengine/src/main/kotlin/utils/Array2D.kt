package dogengine.utils

/** Base class for containers that wrap around a single 1D array, treating it as a 2D array.
 *
 * @author MJ
 */
abstract class Array2D
/** @param col amount of columns.
 * @param row amount of rows.
 */(
        /** @return amount of columns.
         */
        val col: Int,
        /** @return amount of rows.
         */
        val row: Int) {

    /** @param size amount of columns and rows.
     */
    constructor(size: Int) : this(size, size) {}

    /** @param x column index.
     * @param y row index.
     * @return true if the coordinates are valid and can be safely used with getter methods.
     */
    fun isIndexValid(x: Int, y: Int): Boolean {
        return (x in 0 until col) && y >= 0 && y < row
    }

    /** @param x column index.
     * @param y row index.
     * @return actual array index of the cell.
     */
    fun toIndex(x: Int, y: Int): Int {
        return x + y * col
    }

    /** @param index actual array index of a cell.
     * @return column index.
     */
    fun toX(index: Int): Int {
        return index % col
    }

    /** @param index actual array index of a cell.
     * @return row index.
     */
    fun toY(index: Int): Int {
        return index / col
    }

}