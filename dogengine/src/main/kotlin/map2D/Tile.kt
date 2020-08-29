package dogengine.map2D

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array

abstract class Tile <T : TextureRegion> {
    var variants = 1 // кол-во вариантов тайла
    var textures : Array<T> = Array()
}