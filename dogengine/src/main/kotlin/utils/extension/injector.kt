package dogengine.utils.extension

import com.badlogic.gdx.ai.msg.MessageManager
import com.google.inject.Injector
import dogengine.Kernel


operator fun <T> Injector.get(type: Class<T>) : T = this.getInstance(type)
val injector  = Kernel.getInjector()

