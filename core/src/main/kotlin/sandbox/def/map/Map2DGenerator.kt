package sandbox.sandbox.def.map

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import com.google.gson.GsonBuilder
import com.sudoplay.joise.module.*
import com.sudoplay.joise.module.ModuleBasisFunction.BasisType
import dogengine.map2D.*
import dogengine.utils.Array2D
import dogengine.utils.log
import java.io.File
import java.util.*


class Map2DGenerator(val tileSize: Int,private val createdCellMapListener: CreatedCellMapListener? = null) {

    val width = 36
    val height = 36
    //val generator: NoiseGenerator = NoiseGenerator()
    val seed = 4082017L
    val pixmap = Pixmap(width, height, Pixmap.Format.RGBA4444)
    val prop = LayerProperties(width, height, tileSize, tileSize, 0)
    val g = GsonBuilder().create()

    init {

    }

    fun getFromFS(): IntGrid? {
        return try {
            val file = File("map.2d")
            val str = file.readText()
            log("map loaded from FS")
            g.fromJson(str, IntGrid::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun generateGrid(): IntGrid {
        log("create map")
        val terrainOctaves = 2 // простота мира
        val terrainRidgeOctaves = 3 // детализация
        val terrainFrequency = 1.5 //
        val terrainNoiseScale = 0.8

        val basis = ModuleBasisFunction()
        basis.setType(BasisType.SIMPLEX)
        basis.seed = seed

        val heightFractal = ModuleFractal(ModuleFractal.FractalType.FBM,
                BasisType.GRADIENT,
                ModuleBasisFunction.InterpolationType.QUINTIC)
        heightFractal.setNumOctaves(terrainOctaves)
        heightFractal.setFrequency(terrainFrequency)
        heightFractal.seed = seed

        val ridgedHeightFractal = ModuleFractal(ModuleFractal.FractalType.RIDGEMULTI,
                BasisType.SIMPLEX,
                ModuleBasisFunction.InterpolationType.QUINTIC)
        ridgedHeightFractal.setNumOctaves(terrainRidgeOctaves)
        ridgedHeightFractal.setFrequency(terrainFrequency)
        ridgedHeightFractal.seed = seed

        val heightTranslateDomain = ModuleTranslateDomain()
        heightTranslateDomain.setSource(heightFractal)
        heightTranslateDomain.setAxisXSource(ridgedHeightFractal)


        val heightmap = ModuleAutoCorrect()
        heightmap.setSource(heightTranslateDomain)
        heightmap.calculate()

        val scaleDomain = ModuleScaleDomain()
        scaleDomain.setSource(heightmap)
        scaleDomain.setScaleX(terrainNoiseScale)
        scaleDomain.setScaleY(terrainNoiseScale)

        val grid = IntGrid(width, height)

        for (x in 0 until width) {
            for (y in 0 until height) { // Noise range
                val x1 = 0f
                val x2 = 2f
                val y1 = 0f
                val y2 = 2f
                val dx = x2 - x1
                val dy = y2 - y1
                // Sample noise at smaller intervals
                val s = x.toFloat() / width
                val t = y.toFloat() / height
                // Calculate our 4D coordinates
                val nx: Float = x1 + MathUtils.cos(s * 2 * MathUtils.PI) * dx / (2 * MathUtils.PI)
                val ny: Float = y1 + MathUtils.cos(t * 2 * MathUtils.PI) * dy / (2 * MathUtils.PI)
                val nz: Float = x1 + MathUtils.sin(s * 2 * MathUtils.PI) * dx / (2 * MathUtils.PI)
                val nw: Float = y1 + MathUtils.sin(t * 2 * MathUtils.PI) * dy / (2 * MathUtils.PI)
                val heightValue = scaleDomain[nx * terrainNoiseScale, ny * terrainNoiseScale, nz * terrainNoiseScale, nw * terrainNoiseScale].toFloat()
                grid[x, y] = (heightValue * 100).toInt()
                println(grid[x, y])
            }

        }

        File("map.2d").bufferedWriter().use { out ->
            out.write(g.toJson(grid))
        }

        return grid
    }

    fun generate(): Map2D {
        val fs = getFromFS()
        val grid = fs ?: generateGrid()
        return getMap2D(grid)
    }

    private fun getMap2D(grid: IntGrid): Map2D {
        val pixel = Pixmap(tileSize, tileSize, Pixmap.Format.RGBA8888)
        val layer = GridLayer(prop)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val cell = grid[x, y] / 100f
                layer.setCell(configHeightTypeCell(cell, pixel, x, y), x, y)
            }
        }
        // ищем соседей
        for (x in 0 until grid.col) {
            for (y in 0 until grid.row) {
                findAndAddNeighbors(layer.getCell(x, y), layer)
            }
        }
        //прощитываем битовую маску и играем с выдилением крайних тайлов
        pixel.setColor(Color.BLACK)
        pixel.fill()
        for (x in 0 until grid.col) {
            for (y in 0 until grid.col) {
                ChunkGridLayer.updateBitmask(layer.getCell(x, y), layer)
                //настройка тайла
                tileConfigure(layer.getCell(x, y))
            }
        }

        //Разбиваем на группы суша и вода
        val landGroup = fillGroup(layer, CellGroupType.LAND,grid)
        val waterGroup = fillGroup(layer, CellGroupType.WATER,grid)

        val map2D = Map2D(layer)
        //map2D.addLayer(landGroup)
        //map2D.addLayer(waterGroup)

        return map2D
    }

    private fun tileConfigure(cell: Cell) {
        when (cell.heightType) {
            1 -> {createTileWater(cell);createdCellMapListener?.createCell(cell, HeightTypes.WATER)} //Вода
            2 -> {createTileSand(cell); createdCellMapListener?.createCell(cell, HeightTypes.SAND)} //Песок
            3 -> {cell.userData = 6 ; createdCellMapListener?.createCell(cell, HeightTypes.GROUND)} //земля
            4 -> { createTileGrass(cell); createdCellMapListener?.createCell(cell, HeightTypes.GRASS)} //трава
            5 -> {cell.userData = 5 ; createdCellMapListener?.createCell(cell, HeightTypes.ROCK)} //Горы
            6 -> {cell.userData = 5 ; createdCellMapListener?.createCell(cell, HeightTypes.SNOW)} //снег
        }

    }

    private fun createTileGrass(cell: Cell) {
        when (MathUtils.random(1, 4)) {
            1 -> {
                cell.userData = 1
            }
            2 -> {
                cell.userData = 2
            }
            3 -> {
                cell.userData = 3
            }
            4 -> {
                cell.userData = 4
            }
        }
    }
    private fun createTileSand(cell: Cell) {
        when (MathUtils.random(1, 3)) {
            1 -> {
                cell.userData = 10
            }
            2 -> {
                cell.userData = 11
            }
            3 -> {
                cell.userData = 12
            }
        }

    }
    private fun createTileWater(cell: Cell) {
        when (MathUtils.random(1, 3)) {
            1 -> {
                cell.userData = 7
            }
            2 -> {
                cell.userData = 8
            }
            3 -> {
                cell.userData = 9
            }
        }
    }

    private fun findAndAddNeighbors(cell: Cell, layerChunk: Layer) {
        cell.topNeighbors = getTopCell(cell, layerChunk)
        cell.bottomNeighbors = getBottomCell(cell, layerChunk)
        cell.leftNeighbors = getLeftCell(cell, layerChunk)
        cell.rightNeighbors = getRightCell(cell, layerChunk)
    }

    private val defCell = Cell.CellXY.tmp
    private fun Layer.getCell(cell: Cell, dx: Int, dy: Int): Cell.CellXY {
        val nx = 0.coerceAtLeast((width - 1).coerceAtMost(cell.x + dx))
        val ny = 0.coerceAtLeast((height - 1).coerceAtMost(cell.y + dy))
        return if (getCell(nx, ny) != cell) {
            Cell.CellXY(nx, ny)
        } else {
            defCell
        }
    }

    private fun getTopCell(cell: Cell, layerChunk: Layer): Cell.CellXY = layerChunk.getCell(cell, 0, 1)
    private fun getBottomCell(cell: Cell, layerChunk: Layer): Cell.CellXY = layerChunk.getCell(cell, 0, -1)
    private fun getLeftCell(cell: Cell, layerChunk: Layer): Cell.CellXY = layerChunk.getCell(cell, -1, 0)
    private fun getRightCell(cell: Cell, layerChunk: Layer): Cell.CellXY = layerChunk.getCell(cell, 1, 0)


    private fun configHeightTypeCell(cell: Float, pixel: Pixmap, x: Int, y: Int): Cell2D {
        val cell2d = Cell2D(x, y, 0)
        val color = Color.WHITE.cpy().apply {
            when {
                cell <= HeightTypes.WATER.depth -> {
                    HeightTypes.WATER.getColor(this)
                    cell2d.heightType = HeightTypes.WATER.heightType
                }
                cell > HeightTypes.WATER.depth && cell <= HeightTypes.SAND.depth -> {
                    HeightTypes.SAND.getColor(this)
                    cell2d.heightType = HeightTypes.SAND.heightType
                    cell2d.collidable = true
                }
                cell > HeightTypes.SAND.depth && cell <= HeightTypes.GROUND.depth -> {
                    HeightTypes.GROUND.getColor(this)
                    cell2d.heightType = HeightTypes.GROUND.heightType
                    cell2d.collidable = true
                }
                cell > HeightTypes.GROUND.depth && cell <= HeightTypes.GRASS.depth -> {
                    HeightTypes.GRASS.getColor(this)
                    cell2d.heightType = HeightTypes.GRASS.heightType
                    cell2d.collidable = true
                }
                cell > HeightTypes.GRASS.depth && cell <= HeightTypes.ROCK.depth -> {
                    HeightTypes.ROCK.getColor(this)
                    cell2d.heightType = HeightTypes.ROCK.heightType
                    cell2d.collidable = true
                }
                cell > HeightTypes.ROCK.depth && cell <= HeightTypes.SNOW.depth -> {
                    HeightTypes.SNOW.getColor(this)
                    cell2d.heightType = HeightTypes.SNOW.heightType
                    cell2d.collidable = true
                }
                else -> set(cell, cell, cell, 1f)
            }
        }
        color.set(color.r, color.g, color.b, 1f)
        pixel.setColor(color)
        pixel.fill()
        //cell2d.userData = Texture(pixel)
        pixmap.setColor(color)
        pixmap.drawPixel(x, y)
        return cell2d
    }

    enum class HeightTypes(val r: Float, val g: Float, val b: Float,
                           val depth: Float, val heightType: Int) {
        WATER(Color.SKY.r, Color.SKY.g, Color.SKY.b, 0.3f, 1),
        SAND(Color.YELLOW.r, Color.YELLOW.g, Color.YELLOW.b, 0.45f, 2),
        GROUND(Color.BROWN.r, Color.BROWN.g, Color.BROWN.b, 0f, 3),
        GRASS(Color.GREEN.r, Color.GREEN.g - 0.2f, Color.GREEN.b, 0.6f, 4),
        ROCK(Color.GRAY.r, Color.GRAY.g, Color.GRAY.b, 0.85f, 5),
        SNOW(Color.LIGHT_GRAY.r + 0.1f, Color.LIGHT_GRAY.g + 0.1f, Color.LIGHT_GRAY.b + 0.2f, 1f, 6);

        fun getColor(color: Color) {
            color.set(r, g, b, 1f)
        }
    }

    // Всё для группировки тайлов
    enum class CellGroupType {
        WATER,
        LAND
    }

    class CellGroup(val type: CellGroupType) {
        val cells = Array<Cell>()
    }

    private val landCellGroups = Array<CellGroup>()
    private val waterCellGroups = Array<CellGroup>()
    private fun fillGroup(layerChunk: Layer, groupType: CellGroupType, grid: IntGrid): Layer {
        return when (groupType) {
            CellGroupType.LAND -> {
                fillGroupLand(layerChunk, grid)
            }
            CellGroupType.WATER -> {
                fillGroupWater(layerChunk, grid)
            }
        }
    }

    private fun fillGroupLand(layerChunk: Layer, grid: IntGrid): Layer {
        val stack = Stack<Cell>()
        for (x in 0 until grid.col) {
            for (y in 0 until grid.row) {
                val cell = layerChunk.getCell(x, y)

                if (cell.floodFilled)
                    continue
                //суша
                if (cell.collidable) {
                    val group = CellGroup(CellGroupType.LAND)
                    stack.push(cell)
                    while (!stack.empty()) {
                        floodFill(stack.pop(), group, stack, layerChunk)
                    }
                    if (!group.cells.isEmpty) {
                        landCellGroups.add(group)
                    }
                }

            }
        }
        val layerLand = GridLayer(prop)
        landCellGroups.forEach {
            it.cells.forEach { cell ->
                layerLand.setCell(cell, cell.x, cell.y)
            }
        }
        println("groups ${landCellGroups.size}")
        return layerLand
    }

    private fun fillGroupWater(layerChunk: Layer, grid: IntGrid): Layer {
        val stack = Stack<Cell>()
        for (x in 0 until grid.col) {
            for (y in 0 until grid.row) {
                val cell = layerChunk.getCell(x, y)
                if (cell.floodFilled)
                    continue
                if (!cell.collidable) { //вода
                    val group = CellGroup(CellGroupType.WATER)
                    stack.push(cell)
                    while (!stack.empty()) {
                        floodFill(stack.pop(), group, stack, layerChunk)
                    }
                    if (!group.cells.isEmpty) {
                        waterCellGroups.add(group)
                    }
                }
            }
        }

        return GridLayer(prop).apply {
            waterCellGroups.forEach {
                for(i in 0 until it.cells.size) {
                    val cell = it.cells[i]
                    println("${cell.x}-${cell.y}")
                    if(cell === Cell.defCell2D) continue
                    setCell(cell, cell.x, cell.y)
                }
            }
        }

    }

    private fun floodFill(c: Cell, cells: CellGroup, stack: Stack<Cell>, layerChunk: Layer) {
        // Валидация
        if (c.floodFilled)
            return;
        if (cells.type == CellGroupType.LAND && !c.collidable)
            return;
        if (cells.type == CellGroupType.WATER && c.collidable)
            return;

        // Добавление в CellGroup
        c.floodFilled = true;
        cells.cells.add(c);


        // заливка соседей
        var t = c.topNeighbors.getCell(layerChunk)
        if (!t.floodFilled && c.collidable == t.collidable)
            stack.push(t)
        t = c.bottomNeighbors.getCell(layerChunk);
        if (!t.floodFilled && c.collidable == t.collidable)
            stack.push(t)
        t = c.leftNeighbors.getCell(layerChunk)
        if (!t.floodFilled && c.collidable == t.collidable)
            stack.push(t)
        t = c.rightNeighbors.getCell(layerChunk)
        if (!t.floodFilled && c.collidable == t.collidable)
            stack.push(t)

    }

}

fun Cell.CellXY.getCell(layerChunk: Layer): Cell {
    return if (x != -1 && y != -1)
        layerChunk.getCell(x, y)
    else Cell.defCell2D
}

class IntGrid(width: Int, height: Int) : Array2D(width, height) {
    private val grid: IntArray = IntArray(width * height)
    operator fun get(x: Int, y: Int): Int {
        return grid[toIndex(x, y)]
    }

    operator fun set(x: Int, y: Int, value: Int): Int {
        grid[toIndex(x, y)] = value
        return grid[toIndex(x, y)]
    }
}