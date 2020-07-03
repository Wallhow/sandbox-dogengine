package dogengine.utils.extension

import com.google.inject.Injector


operator fun <T> Injector.get(type: Class<T>) : T = this.getInstance(type)
