package sandbox.sandbox.def.particles

import com.badlogic.gdx.math.Vector2
import sandbox.sandbox.def.def.particles.Emitter

object Presets {
    val fire = Emitter.Configuration().apply {
        pCountMin = 300
        pSpeedMin = 10f
        pSpeedMax = 20f
        pDirectionMin = Vector2(-0.3f,0.5f)
        pDirectionMax = Vector2(0.3f,1f)
        pLifeTimeMin = 1f
        pLifeTimeMax = 2f
        pRotationMin = -10f
        pRotationMax = 10f
        pOffsetMinX = -10f
        pOffsetMaxX = 10f
        pOffsetMinY = -5f
        pSizeMin = 3f
        pSizeMax = 6f
        colors.run {
            add(colorFromRGB(82,9,0),
                    colorFromRGB(226,69,0),
                    colorFromRGB(254,141,0))
            add(colorFromRGB(255,255,207))
            add(colorFromRGB(10,10,10,150))
        }
        isInfinite = true
    }
    val dust = Emitter.Configuration().apply {
        pCountMin = 4
        pSpeedMin = 80f
        pSpeedMax = 100f
        pDirectionMin = Vector2(-0.5f,1f)
        pDirectionMax = Vector2(0.5f,-1f)
        pDirectionInterpolation = true
        pLifeTimeMin = 0.6f
        pLifeTimeMax = 0.7f
        pRotationMin = 0f
        pRotationMax = 0f
        pOffsetMinX = -20f
        pOffsetMaxX = 20f
        pOffsetMinY = -15f
        pOffsetMaxY = 15f
        pSizeMin = 2f
        pSizeMax = 5f
        colors.run {
            add(colorFromRGB(20,20,20))
            add(colorFromRGB(20,20,20,0))
        }

    }
}