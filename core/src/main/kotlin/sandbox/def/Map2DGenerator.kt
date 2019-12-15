package sandbox.sandbox.def

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Logger
import com.github.czyzby.noise4j.map.Grid
import com.github.czyzby.noise4j.map.generator.noise.NoiseGenerator
import com.github.czyzby.noise4j.map.generator.util.Generators
import com.sudoplay.joise.module.*
import com.sudoplay.joise.module.ModuleBasisFunction.BasisType
import sandbox.sandbox.def.map2D.*
import java.util.*


class Map2DGenerator() {

    val width = 64
    val height = 64
    val generator: NoiseGenerator = NoiseGenerator()
    val grid = Grid(width, height)
    val tileSize = 24
    val seed = 148865L
    val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)
    val prop = LayerProperties(grid.width, grid.height, tileSize, tileSize, 0)

    init {

    }

    fun generate(): Map2D {
        val terrainOctaves = 1 // простота мира
        val terrainRidgeOctaves = 4 // детализация
        val terrainFrequency = 2.0 //
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


        //val heightData = Grid(width, height)

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
                // keep track of the max and min values found
                // if (heightValue > heightData.max) heightData.max = heightValue
                // if (heightValue < heightData.min) heightData.min = heightValue
                grid[x, y] = heightValue


            }

        }

        //noiseStage(grid, generator, 64, 0.8f);
        //noiseStage(grid, generator, 32, 0.6f);
        //noiseStage(grid, generator, 16, 0.2f);
        //noiseStage(grid, generator, 8, 0.1f);


        val map2D = getMap2D()


        return map2D
    }

    private fun getMap2D(): Map2D {
        val pixel = Pixmap(tileSize, tileSize, Pixmap.Format.RGBA8888)
        val layer = GridLayer(prop)

        for (x in 0 until grid.width) {
            for (y in 0 until grid.height) {
                val cell = grid[x, y]
                layer.setCell(configCell(cell, pixel, x, y), x, y)
            }
        }
        // ищем соседей
        for (x in 0 until grid.width) {
            for (y in 0 until grid.height) {
                findAndAddNeighbors(layer.getCell(x, y), layer)
            }
        }
        //прощитываем битовую маску и играем с выдилением крайних тайлов
        pixel.setColor(Color.BLACK)
        pixel.fill()
        val tex = Texture(pixel)
        for (x in 0 until grid.width) {
            for (y in 0 until grid.height) {
                GridLayer.updateBitmask(layer.getCell(x, y))
                if (layer.getCell(x, y).bitmask != 15) {
                    //layer.getCell(x, y).userData = tex
                }
            }
        }

        //Разбиваем на группы суша и вода
        val landGroup = fillGroup(layer, CellGroupType.LAND)
        val waterGroup = fillGroup(layer, CellGroupType.WATER)

        val map2D = Map2D(layer)
        map2D.addLayer(landGroup)
        map2D.addLayer(waterGroup)

        return map2D
    }

    private fun findAndAddNeighbors(cell: Cell, layer: GridLayer) {
        cell.topNeighbors = getTopCell(cell, layer)
        cell.bottomNeighbors = getBottomCell(cell, layer)
        cell.leftNeighbors = getLeftCell(cell, layer)
        cell.rightNeighbors = getRightCell(cell, layer)
    }

    private fun GridLayer.getCell(cell: Cell, dx: Int, dy: Int): Cell {
        val nx = 0.coerceAtLeast((width - 1).coerceAtMost(cell.x + dx))
        val ny = 0.coerceAtLeast((height - 1).coerceAtMost(cell.y + dy))
        return getCell(nx, ny)
    }

    private fun getTopCell(cell: Cell, layer: GridLayer): Cell = layer.getCell(cell, 0, -1)
    private fun getBottomCell(cell: Cell, layer: GridLayer): Cell = layer.getCell(cell, 0, 1)
    private fun getLeftCell(cell: Cell, layer: GridLayer): Cell = layer.getCell(cell, -1, 0)
    private fun getRightCell(cell: Cell, layer: GridLayer): Cell = layer.getCell(cell, 1, 0)


    private fun configCell(cell: Float, pixel: Pixmap, x: Int, y: Int): Cell2D {
        val cell2d = Cell2D(x, y, 0)
        val color = Color.WHITE.cpy().apply {
            when {
                cell <= 0.3f -> {
                    Objects.WATER.getColor(this)
                    cell2d.heightType = 1
                }
                cell > 0.3f && cell <= 0.4f -> {
                    Objects.SAND.getColor(this)
                    cell2d.heightType = 2
                    cell2d.collidable = true
                }
                cell > 0.4f && cell <= 0.45f -> {
                    Objects.GROUND.getColor(this)
                    cell2d.heightType = 3
                    cell2d.collidable = true
                }
                cell > 0.45f && cell <= 0.6f -> {
                    Objects.GRASS.getColor(this)
                    cell2d.heightType = 4
                    cell2d.collidable = true
                }
                cell > 0.6f && cell <= 0.80f -> {
                    Objects.ROCK.getColor(this)
                    cell2d.heightType = 5
                    cell2d.collidable = true
                }
                cell > 0.80f && cell <= 1f -> {
                    Objects.SNOW.getColor(this)
                    cell2d.heightType = 6
                    cell2d.collidable = true
                }
                else -> set(cell, cell, cell, 1f)
            }
        }
        color.set(color.r, color.g, color.b, 1f)
        pixel.setColor(color)
        pixel.fill()
        cell2d.userData = Texture(pixel)
        pixmap.setColor(color)
        pixmap.drawPixel(x, y)
        return cell2d
    }

    private fun noiseStage(grid: Grid, noiseGenerator: NoiseGenerator, radius: Int,
                           modifier: Float) {
        noiseGenerator.radius = radius
        noiseGenerator.modifier = modifier
        // Seed ensures randomness, can be saved if you feel the need to
        // generate the same map in the future.
        noiseGenerator.seed = Generators.rollSeed()
        noiseGenerator.generate(grid)
    }

    enum class Objects(val r: Float, val g: Float, val b: Float) {
        WATER(Color.SKY.r, Color.SKY.g, Color.SKY.b),
        SAND(Color.YELLOW.r, Color.YELLOW.g, Color.YELLOW.b),
        GROUND(Color.BROWN.r, Color.BROWN.g, Color.BROWN.b),
        GRASS(Color.GREEN.r, Color.GREEN.g - 0.2f, Color.GREEN.b),
        ROCK(Color.GRAY.r, Color.GRAY.g, Color.GRAY.b),
        SNOW(Color.LIGHT_GRAY.r + 0.1f, Color.LIGHT_GRAY.g + 0.1f, Color.LIGHT_GRAY.b + 0.2f);

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
    private fun fillGroup(layer: GridLayer, groupType: CellGroupType): Layer {
        return when (groupType) {
            CellGroupType.LAND -> {
                fillGroupLand(layer)
            }
            CellGroupType.WATER -> {
                fillGroupWater(layer)
            }
        }
    }

    private fun fillGroupLand(layer: GridLayer): Layer {
        val stack = Stack<Cell>()
        for (x in 0 until grid.width) {
            for (y in 0 until grid.height) {
                val cell = layer.getCell(x, y)

                if (cell.floodFilled)
                    continue
                //суша
                if (cell.collidable) {
                    val group = CellGroup(CellGroupType.LAND)
                    stack.push(cell)
                    while (!stack.empty()) {
                        floodFill(stack.pop(), group, stack)
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

    private fun fillGroupWater(layer: GridLayer): GridLayer {
        val stack = Stack<Cell>()
        for (x in 0 until grid.width) {
            for (y in 0 until grid.height) {
                val cell = layer.getCell(x, y)
                println("${cell.x}-${cell.y}")
                if (cell.floodFilled)
                    continue
                if (!cell.collidable) { //вода
                    val group = CellGroup(CellGroupType.WATER)
                    stack.push(cell)
                    while (!stack.empty()) {
                        floodFill(stack.pop(), group, stack)
                    }
                    if (!group.cells.isEmpty) {
                        waterCellGroups.add(group)
                    }
                }
            }
        }

        return GridLayer(prop).apply {
            waterCellGroups.forEach {
                it.cells.forEach { cell ->
                    setCell(cell, cell.x, cell.y)
                }
            }
        }

    }

    private fun floodFill(c: Cell, cells: CellGroup, stack: Stack<Cell>) {
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
        var t = c.topNeighbors;
        if (!t.floodFilled && c.collidable == t.collidable)
            stack.push(t)
        t = c.bottomNeighbors;
        if (!t.floodFilled && c.collidable == t.collidable)
            stack.push(t)
        t = c.leftNeighbors;
        if (!t.floodFilled && c.collidable == t.collidable)
            stack.push(t)
        t = c.rightNeighbors;
        if (!t.floodFilled && c.collidable == t.collidable)
            stack.push(t)

    }

}