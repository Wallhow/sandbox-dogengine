package sandbox.dev.map

import com.badlogic.gdx.graphics.Color

enum class HeightTypes(val r: Float, val g: Float, val b: Float,
                       val depth: Float, val heightType: Int) {
    WATER(Color.SKY.r, Color.SKY.g, Color.SKY.b, 0.3f, 1),
    SAND(Color.YELLOW.r, Color.YELLOW.g, Color.YELLOW.b, 0.45f, 2),
    GROUND(Color.BROWN.r, Color.BROWN.g, Color.BROWN.b, 0f, 3),
    GRASS(Color.GREEN.r, Color.GREEN.g - 0.2f, Color.GREEN.b, 0.6f, 4),
    ROCK(Color.GRAY.r, Color.GRAY.g, Color.GRAY.b, 0.85f, 5),
    SNOW(Color.LIGHT_GRAY.r + 0.1f, Color.LIGHT_GRAY.g + 0.1f, Color.LIGHT_GRAY.b + 0.2f, 1f, 6);

    fun getColor(color: Color) {
        color.set(r, g, b, 1f)
    }
}