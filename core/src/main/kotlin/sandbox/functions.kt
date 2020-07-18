package sandbox.sandbox

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import dogengine.Kernel
import dogengine.PooledEntityCreate
import dogengine.ecs.components.createEntity
import dogengine.utils.extension.get

fun getTextureDot() = Kernel.getInjector().getProvider(Kernel.DotTexture::class.java).get().get()

fun engine() = Kernel.getInjector()[Engine::class.java]
