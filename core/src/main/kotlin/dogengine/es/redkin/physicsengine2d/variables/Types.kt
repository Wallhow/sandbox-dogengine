package dogengine.es.redkin.physicsengine2d.variables

class Types {
    // Variables estaticas//
    enum class TYPE {
        STATIC, DYNAMIC, SENSOR
    }

    enum class BODYTYPE {
        RECTANGLE, POLYLINE, LINE, NULL
    }

    enum class LINETYPE {
        NORMAL, JUMPTHRU, SENSOR
    }
}