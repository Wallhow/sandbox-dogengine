package sandbox.sandbox.def.map

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.*
import dogengine.Kernel
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.map2D.Cell2D
import dogengine.map2D.Map2D
import dogengine.map2D.Properties
import dogengine.map2D.layers.GridLayer
import dogengine.map2D.layers.Layer
import dogengine.map2D.layers.LayerProperties
import dogengine.utils.extension.get
import dogengine.utils.log
import map2D.TypeData
import sandbox.go.environment.objects.Rock
import sandbox.go.environment.objects.Sandstone
import sandbox.go.environment.objects.Wood
import sandbox.sandbox.go.environment.AGameObjectOnMap
import sandbox.sandbox.go.environment.ObjectList
import sandbox.sandbox.go.environment.objects.buiding.Bonfire
import java.lang.reflect.Type

object SerializerAttributes {
    const val TEXTURE = "type"
    const val HEIGHT_TYPE = "heightType"
    const val X = "x"
    const val Y = "y"
    const val WIDTH_MAP = "width"
    const val HEIGHT_MAP = "height"
    const val WIDTH_TILE = "tileWidth"
    const val HEIGHT_TILE = "tileHeight"
    const val PROPS_MAP = "props"
    const val OBJECTS_LAYER = "objects"
    const val LAYER_NAME = "layerName"
}

class MapSerializer : JsonSerializer<Map2D> {
    override fun serialize(src: Map2D, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        //серриализуем пропсы карты
        val mapJson = jsonObject(
                SerializerAttributes.PROPS_MAP to jsonObject(
                        SerializerAttributes.WIDTH_MAP to src.getWidthMap(),
                        SerializerAttributes.HEIGHT_MAP to src.getHeightMap(),
                        SerializerAttributes.WIDTH_TILE to src.getTileWidth(),
                        SerializerAttributes.HEIGHT_TILE to src.getTileHeight()
                )
        )

        val array = JsonArray()
        src.getLayers().forEach { layer ->
            if (layer.name == SerializerAttributes.OBJECTS_LAYER) {
                val layerObjJson = createLayerObjectsJson(layer, src.getTileWidth())
                array.add(layerObjJson)
            } else {
                val layerJson = createLayerJson(layer)
                array.add(layerJson)
            }
        }
        mapJson.add("map", array)
        return mapJson
    }

    private fun createLayerObjectsJson(layer: Layer, cellWidth: Int): JsonObject {
        val layerJson = jsonObject(
                SerializerAttributes.LAYER_NAME to SerializerAttributes.OBJECTS_LAYER
        )
        val obj = JsonArray()
        val cells = (layer as GridLayer).cells.toArray()


        cells.filter { it.data[TypeData.ObjectOn] != null }.forEach {
            val o = JsonObject()
            val gameObject = it.data[TypeData.ObjectOn] as Entity
            val x = (CTransforms[gameObject].position.x / cellWidth).toInt()
            val y = (CTransforms[gameObject].position.y / cellWidth).toInt()
            o.addProperty("x", x)
            o.addProperty("y", y)
            o.addProperty("type", (gameObject as AGameObjectOnMap).objectType.name)

            if (obj.none { o -> o["x"].asInt == x && o["y"].asInt == y })
                obj.add(o)
        }
        layerJson.add("array", obj)
        return layerJson
    }

    private fun createLayerJson(layer: Layer): JsonObject {
        val layerJson = jsonObject(
                SerializerAttributes.LAYER_NAME to layer.name
        )
        val arrayCells = JsonArray()
        val cells = (layer as GridLayer).cells.toArray()
        val str = StringBuilder()
        cells.forEach { cell ->
            if (cell.x != -1 || cell.y != -1) {
                val obj = JsonObject()

                obj.addProperty(SerializerAttributes.X, cell.x)
                obj.addProperty(SerializerAttributes.Y, cell.y)
                val n = if (cell.data[TypeData.TypeCell] is String) -1 else cell.data[TypeData.TypeCell] as Int
                obj.addProperty(SerializerAttributes.TEXTURE, n)
                obj.addProperty(SerializerAttributes.HEIGHT_TYPE, cell.heightType)
                arrayCells.add(obj)

                str.append(cell.x)
                str.append(":")
                str.append(cell.y)
                str.append(":")
                str.append(cell.heightType)
                str.append(":")
                str.append(n)
                str.append("|")
            }
        }
        layerJson.addProperty("cells", str.toString())
        return layerJson
    }
}


class MapDeserializer : JsonDeserializer<Map2D> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Map2D {
        log("deserialize start")
        val props = json[SerializerAttributes.PROPS_MAP]
        val map = json["map"]
        val width = props[SerializerAttributes.WIDTH_MAP].asInt
        val height = props[SerializerAttributes.HEIGHT_MAP].asInt
        val tileWidth = props[SerializerAttributes.WIDTH_TILE].asInt
        val tileHeight = props[SerializerAttributes.HEIGHT_TILE].asInt

        val properties = Properties(width, height, tileWidth, tileHeight)

        val map2D = Map2D(properties)
        map.asJsonArray.forEach { element ->
            val layerName = element[SerializerAttributes.LAYER_NAME].asString
            val properties = LayerProperties(width, height, tileWidth, tileHeight, 0, layerName)

            if (layerName == SerializerAttributes.OBJECTS_LAYER) {
                val layerObj = createLayerObject(element, properties)
                map2D.addLayer(layerObj)

            } else {
                val layer = GridLayer(properties, layerName)
                element["cells"].asString.split("|").forEach { str ->
                    if (str != "") {
                        log(str)
                        val p = str.split(":")
                        val x = p[0].toInt()
                        val y = p[1].toInt()
                        val cell = Cell2D(x, y, p[2].toInt())
                        cell.collidable = true
                        cell.data.put(TypeData.TypeCell, p[3].toInt())
                        layer.cells.setCell(cell, x, y)
                    }
                }
                map2D.addLayer(layer)
            }
        }


        log(map2D.getLayers())
        log(map2D.getWidthMap())

        return map2D
    }

    private fun createLayerObject(element: JsonElement, p: LayerProperties): Layer {
        val layer = GridLayer(p, SerializerAttributes.OBJECTS_LAYER)
        element["array"].asJsonArray.forEach { obj ->
            val cell = Cell2D(obj[SerializerAttributes.X].asInt,
                    obj[SerializerAttributes.Y].asInt, 4)

            val type = obj["type"].asString
            val pos = Vector2(cell.x * p.tileWidth * 1f, cell.y * p.tileHeight * 1f)
            val e = when (type) {
                ObjectList.ROCK.name -> {
                    Rock(pos)
                }
                ObjectList.WOOD.name -> {
                    Wood(pos)
                }
                ObjectList.SANDSTONE.name -> {
                    Sandstone(pos)
                }
                else -> {
                    Bonfire(pos)
                }
            }
            val size = CTransforms[e].size
            for (x in 0..(size.width / p.tileWidth).toInt()) {
                if ((cell.x + x) < p.width) {
                    val cell2 = Cell2D(cell.x + x, cell.y, 4)
                    cell2.data.put(TypeData.ObjectOn, e)
                    layer.setCell(cell2, cell.x + x, cell.y)
                }
            }
            Kernel.getInjector()[Engine::class.java].addEntity(e)
        }
        return layer
    }
}