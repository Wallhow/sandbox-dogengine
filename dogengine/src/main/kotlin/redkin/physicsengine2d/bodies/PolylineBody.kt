package dogengine.redkin.physicsengine2d.bodies

import com.badlogic.gdx.math.Polyline
import dogengine.redkin.physicsengine2d.variables.Types.LINETYPE

class PolylineBody(vertices: FloatArray?, var linetype: LINETYPE, var name: String) : Polyline(vertices)