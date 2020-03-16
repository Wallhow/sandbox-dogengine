package dogengine.ecs.systems.physics.listeners

import dogengine.ecs.def.GameEntity
import dogengine.ecs.systems.physics.Collide

interface AllCollisionListener {
    fun beginCollid(collide: Collide)
    fun endCollid(collide: Collide)
    fun preSolve(collide: Collide)
    fun postSolve(collide: Collide)

    open class AllCollisionListenerAdapter : AllCollisionListener {
        override fun beginCollid(collide: Collide) {
        }

        override fun endCollid(collide: Collide) {
        }

        override fun preSolve(collide: Collide) {
        }

        override fun postSolve(collide: Collide) {
        }

         //Метод сортирует сущности в соответствии с индексом(иминем) той сущности, что должна быть первой
         protected fun Collide.getABForName(idx: String) :
                Pair<GameEntity, GameEntity>{

            var a = gameEntity1
            var b = gameEntity2


            if(b.name.startsWith(idx)) {
                b = gameEntity1
                a = gameEntity2

            }
            return Pair(a,b)
        }

        //Метод возвращает истину если одна из сущностей имеет заданный индекс
        protected fun Collide.contain(idx: String) : Boolean = this.gameEntity1.name.startsWith(idx) || this.gameEntity2.name.startsWith(idx)

        //Если столкнулись сущности с именами name1 и name2 то вернет true
        protected fun Collide.isCollide(name1: String,name2: String) : Boolean = contain(name1) && contain(name2)

        protected fun Collide.isCollide(idx: String, name2: String,block: Pair<GameEntity, GameEntity>.() -> Unit) {
            if(isCollide(idx, name2)) {
                block.invoke(getABForName(idx))
            }
        }

        protected fun Collide.isContain(idx: String,block: Pair<GameEntity, GameEntity>.() -> Unit) {
            if(contain(idx)) {
                block.invoke(getABForName(idx))
            }
        }
    }
}

//Метод возвращает истину если одна из сущностей имеет заданный индекс
fun Collide.contain(idx: String) : Boolean = this.gameEntity1.name.startsWith(idx) || this.gameEntity2.name.startsWith(idx)

//Если столкнулись сущности с именами name1 и name2 то вернет true
fun Collide.isCollide(name1: String,name2: String) : Boolean = contain(name1) && contain(name2)

fun Collide.isCollide(idx: String, name2: String,block: Pair<GameEntity, GameEntity>.() -> Unit) {
    if(isCollide(idx, name2)) {
        block.invoke(getABForName(idx))
    }
}

fun Collide.isContain(idx: String,block: Pair<GameEntity, GameEntity>.() -> Unit) {
    if(contain(idx)) {
        block.invoke(getABForName(idx))
    }
}

//Метод сортирует сущности в соответствии с индексом(иминем) той сущности, что должна быть первой
fun Collide.getABForName(idx: String) :
        Pair<GameEntity, GameEntity>{

    var a = gameEntity1
    var b = gameEntity2


    if(b.name.startsWith(idx)) {
        b = gameEntity1
        a = gameEntity2

    }
    return Pair(a,b)
}