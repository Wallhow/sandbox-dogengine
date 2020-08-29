package sandbox.dev.world

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.math.Vector2
import dogengine.utils.extension.get
import dogengine.utils.extension.injector
import dogengine.utils.log
import map2D.TypeData
import map2D.Vector2Int
import sandbox.dev.ecs.sys.SWorldHandler
import sandbox.sandbox.go.objects.ItemList
import sandbox.sandbox.go.objects.ObjectList

class MessageListeners(worldManager: IWorldManager) {
    private val messenger : MessageManager = injector[MessageManager::class.java]
    init {
        messenger.apply {
            //Слушатель сообщений о разрушении объектов
            addListener(MessagesType.WORLD_EXTRACTION) {
                val pos = it.extraInfo as Vector2Int
                val cell = worldManager.getCell(pos.x, pos.y, LayerNames.OBJECTS)
                cell.data.put(TypeData.ObjectOn, null)
                true
            }

            //Слушатель о постройке
            addListener(MessagesType.WORLD_BUILD) {
                val map = it.extraInfo as Map<*, *>
                val pos = map[1] as Vector2Int
                val typeData = map[2] as TypeData
                val buildType = map[3] as ObjectList

                val cell = worldManager.getCell(pos.x,pos.y,"objects")
                if (worldManager.isEmpty(cell)) {
                    worldManager.getCell(pos.x,pos.y, LayerNames.OBJECTS).data.put(
                            typeData,
                            buildType)
                    worldManager.createConstruction(buildType, pos.scl(32f))
                    SWorldHandler.itemIDBuild = null
                }
                else {
                    log("not empty this cell")
                }
                true
            }

            //Бросаем в стек предмет выбрашеный на игровую карту
            addListener(MessagesType.WORLD_DROP_ITEM_ON_MAP) {
                val map = it.extraInfo as Map<*,*>
                val dropItemID = map[1] as ItemList
                val pos = map[2] as Vector2
                worldManager.createItem(dropItemID, pos)
                true
            }


        }
    }

    private inline fun MessageManager.addListener(msg: Int, crossinline init: (it: Telegram) -> Boolean) {
        addListener({ init.invoke(it) }, msg)
    }
}