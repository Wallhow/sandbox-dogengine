package sandbox.sandbox

import dogengine.Kernel

fun getTextureDot() = Kernel.getInjector().getProvider(Kernel.DotTexture::class.java).get().get()