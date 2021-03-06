package sandbox.dev.map

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sudoplay.joise.module.*
import com.sudoplay.joise.module.ModuleBasisFunction.BasisType
import dogengine.map2D.Cell
import dogengine.map2D.Cell2D
import dogengine.map2D.Map2D
import dogengine.map2D.Properties
import dogengine.map2D.layers.ChunkGridLayer
import dogengine.map2D.layers.GridLayer
import dogengine.map2D.layers.Layer
import dogengine.map2D.layers.LayerProperties
import dogengine.utils.Array2D
import dogengine.utils.extension.get
import dogengine.utils.extension.injector
import dogengine.utils.log
import map2D.TypeData
import map2D.Vector2Int
import sandbox.dev.gui.DebugGUI.Companion.MSG_DEBUG_GET_MAP_TEXTURE
import sandbox.dev.gui.DebugGUI.Companion.MSG_DEBUG_GIVE_MAP_TEXTURE
import java.io.File
import java.io.PrintWriter
import java.lang.Math.pow
import java.util.*
import kotlin.math.pow


class Map2DGenerator(private val tileSize: Int, private val createdCellMapListener: CreatedCellMapListener? = null) {

    val size = 64
    val width = size
    val height = size

    //val generator: NoiseGenerator = NoiseGenerator()
    var seed_ideal = 3141L
    val max_chunk = 10
    val pixmap = Pixmap(max_chunk, max_chunk, Pixmap.Format.RGBA4444)
    val prop = LayerProperties(width*max_chunk, height*max_chunk, tileSize, tileSize, 0)


    init {
        injector[MessageManager::class.java].apply {
            addListener({
                log("get texture map")

                this.dispatchMessage(MSG_DEBUG_GIVE_MAP_TEXTURE,
                        ""//Texture(pixmap)
                )
                true
            }, MSG_DEBUG_GET_MAP_TEXTURE)
        }
    }

    companion object {
        val serializer: Gson = GsonBuilder()
                .registerTypeAdapter(Map2D::class.java, MapSerializer())
                .registerTypeAdapter(Map2D::class.java, MapDeserializer())
                .setPrettyPrinting()
                .create()
        val deserializer: Gson = GsonBuilder()
                .registerTypeAdapter(Map2D::class.java, MapDeserializer())
                .create()

        fun save(map2D: Map2D) {
            val jsonMap = serializer.toJson(map2D)
            val file = File("map.2d")
            val writer = PrintWriter(file)
            log(map2D)
            writer.print("")
            writer.close()
            file.bufferedWriter().use { out ->
                out.write(jsonMap)
            }
        }
    }

    private fun getFromFS(): Map2D? {
        log("loaded from fs")
        var json = ""
        json = try {
            val file = File("map.2d")
            file.readText()
        } catch (e: Exception) {
            ""
        }

        return if (json != "") deserializer.fromJson(json, Map2D::class.java) else null
    }
    val grid = IntGrid(width*max_chunk, height*max_chunk)
    private fun generateGrid(xChunk: Int, yChunk: Int,grid: IntGrid) {
        log("create map")
        val terrainOctaves = 2 // простота мира
        val terrainRidgeOctaves = 3 // детализация
        val terrainFrequency = 1.5 //
        val terrainNoiseScale = 0.8

        var seed = seed_ideal
       //seed += xChunk + yChunk * max_chunk

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


        val pixmap1 = Pixmap(width, height, Pixmap.Format.RGBA4444)
        for (x in xChunk*width until xChunk*width+width) {
            for (y in yChunk*height until yChunk*height+height) { // Noise range
                // min max начения
                val x1 = 0f
                val x2 = 1f
                val y1 = 0
                val y2 = 1f

                val dx = x2 - x1
                val dy = y2 - y1
                //перераспределение - больше цифра - более гористая местность
                val exp = 3
                // Sample noise at smaller intervals
                val s = x.toFloat() / (width * max_chunk)
                val t = y.toFloat() / (height * max_chunk)
                // Calculate our 4D coordinates
                /*val nx: Float =     v      x1 + MathUtils.cos(s * 2 * MathUtils.PI) * dx / (2 * MathUtils.PI)
                val ny: Float = y1 + MathUtils.cos(t * 2 * MathUtils.PI) * dy / (2 * MathUtils.PI)
                val nz: Float = x1 + MathUtils.sin(s * 2 * MathUtils.PI) * dx / (2 * MathUtils.PI)
                val nw: Float = y1 + MathUtils.sin(t * 2 * MathUtils.PI) * dy / (2 * MathUtils.PI)
                val heightValue = scaleDomain[nx * terrainNoiseScale, ny * terrainNoiseScale, nz * terrainNoiseScale, nw * terrainNoiseScale].toFloat()*/
                val heightValue = scaleDomain[s.toDouble(),t.toDouble()]
                grid[x, y] = (heightValue/*.toDouble().pow(exp)*/ * 100).toInt()
                //pixmap1.drawPixel(x,y,Color(heightValue,heightValue,heightValue,1f).toIntBits())
            }

        }
        //pixmap.drawPixmap(pixmap1,xChunk,yChunk)
    }

    fun generate(): Map2D {
        val map2d = getFromFS()

        return if(map2d!= null ) {
            val pixel = Pixmap(1, 1, Pixmap.Format.RGBA8888)
            map2d.getLayers().forEach {
                for (x in 0 until it.width) {
                    for (y in 0 until it.height) {
                        //drawOnPixmapMap(it.getCell(x,y).heightType,pixel,x,y,pixmap)
                    }
                }
            }
            map2d
        } else {

            for (x in 0 until max_chunk) {
                for (y in 0 until max_chunk)
                {
                    generateGrid(x,y,grid)
                }
            }
            log(grid.col)

            getMap2D(grid)
        }
    }

    private fun getMap2D(grid: IntGrid): Map2D {
        val pixel = Pixmap(tileSize, tileSize, Pixmap.Format.RGBA8888)
        val layer = GridLayer(prop)
        val map2D = Map2D(Properties(layer.width, layer.height, layer.cellWidth, layer.cellHeight))
        val objectGroup = GridLayer(prop, "objects")
        map2D.addLayer(objectGroup)

        for (x in 0 until layer.width) {
            for (y in 0 until layer.height) {
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
                tileConfigure(layer.getCell(x, y), map2D)
            }
        }

        //Разбиваем на группы суша и вода
        val landGroup = fillGroup(layer, CellGroupType.LAND, grid)
        val waterGroup = fillGroup(layer, CellGroupType.WATER, grid)



        map2D.addLayer(landGroup)
        map2D.addLayer(waterGroup)

        File("map.2d").bufferedWriter().use { out ->
            out.write(serializer.toJson(map2D))
        }



        return map2D
    }

    private fun tileConfigure(cell: Cell, map2D: Map2D) {
        when (cell.heightType) {
            1 -> {
                createTileWater(cell);createdCellMapListener?.createCell(cell, HeightTypes.WATER, map2D)
            } //Вода
            2 -> {
                createTileSand(cell); createdCellMapListener?.createCell(cell, HeightTypes.SAND, map2D)
            } //Песок
            3 -> {
                cell.setUserData(TypeData.TypeCell, 6); createdCellMapListener?.createCell(cell, HeightTypes.GROUND, map2D)
            } //земля
            4 -> {
                createTileGrass(cell); createdCellMapListener?.createCell(cell, HeightTypes.GRASS, map2D)
            } //трава
            5 -> {
                cell.setUserData(TypeData.TypeCell, 5); createdCellMapListener?.createCell(cell, HeightTypes.ROCK, map2D)
            } //Горы
            6 -> {
                cell.setUserData(TypeData.TypeCell, 5); createdCellMapListener?.createCell(cell, HeightTypes.SNOW, map2D)
            } //снег
        }

    }

    private fun createTileGrass(cell: Cell) {
        cell.setUserData(TypeData.TypeCell, MathUtils.random(1, 4))
    }

    private fun createTileSand(cell: Cell) {
        cell.setUserData(TypeData.TypeCell, 9 + MathUtils.random(1, 3))
    }

    private fun createTileWater(cell: Cell) {
        cell.setUserData(TypeData.TypeCell, 6 + MathUtils.random(1, 3))
    }

    private fun findAndAddNeighbors(cell: Cell, layerChunk: Layer) {
        cell.topNeighbors = getTopCell(cell, layerChunk)
        cell.bottomNeighbors = getBottomCell(cell, layerChunk)
        cell.leftNeighbors = getLeftCell(cell, layerChunk)
        cell.rightNeighbors = getRightCell(cell, layerChunk)
    }

    private val defCell = Vector2Int.tmp
    private fun Layer.getCell(cell: Cell, dx: Int, dy: Int): Vector2Int {
        val nx = 0.coerceAtLeast((width - 1).coerceAtMost(cell.x + dx))
        val ny = 0.coerceAtLeast((height - 1).coerceAtMost(cell.y + dy))
        return if (getCell(nx, ny) != cell) {
            Vector2Int(nx, ny)
        } else {
            defCell
        }
    }

    private fun Cell.setUserData(typeDataType: TypeData, any: Any) {
        data.put(typeDataType, any)
    }

    private fun getTopCell(cell: Cell, layerChunk: Layer): Vector2Int = layerChunk.getCell(cell, 0, 1)
    private fun getBottomCell(cell: Cell, layerChunk: Layer): Vector2Int = layerChunk.getCell(cell, 0, -1)
    private fun getLeftCell(cell: Cell, layerChunk: Layer): Vector2Int = layerChunk.getCell(cell, -1, 0)
    private fun getRightCell(cell: Cell, layerChunk: Layer): Vector2Int = layerChunk.getCell(cell, 1, 0)


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
        //pixmap.setColor(color)
        //pixmap.drawPixel(x, y)
        return cell2d
    }

    private fun drawOnPixmapMap(height: Int, pixel: Pixmap, x: Int, y: Int,pixmapOut: Pixmap){
        if(height==-1) return
        val color = Color.WHITE.cpy().apply {
            HeightTypes.values().first { it.heightType == height }.getColor(this)
        }
        color.set(color.r, color.g, color.b, 1f)
        pixel.setColor(color)
        pixel.fill()
        pixmapOut.setColor(color)
        pixmapOut.drawPixel(x, y)
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
        prop.name = "ground"
        val layerLand = GridLayer(prop)
        landCellGroups.forEach {
            it.cells.forEach { cell ->
                layerLand.setCell(cell, cell.x, cell.y)
            }
        }
        log("groups ${landCellGroups.size}")
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

        return GridLayer(prop, "water").apply {
            waterCellGroups.forEach {
                for (i in 0 until it.cells.size) {
                    val cell = it.cells[i]
                    if (cell === Cell.defCell2D || cell.x ==-1) continue
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

fun Vector2Int.getCell(layerChunk: Layer): Cell {
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