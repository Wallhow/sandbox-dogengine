//#ifdef позволяет коду работать на слабых телефонах, и мощных пк.Если шейдер используется на телефоне(GL_ES) то
//используется низкая разрядность (точность) данных.(highp – высокая точность; mediump – средняя точность; lowp – низкая точность)
#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif
varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform sampler2D shadow_texture;
varying vec2 v_texCoordsShadowmap;
varying vec2 v_position;
varying float v_gPosition;

void setColor(float color) {
    gl_FragColor.r = color;
    gl_FragColor.g = color;
    gl_FragColor.b = color;
}

void main(){

    float shadow_y = texture2D(shadow_texture, v_texCoordsShadowmap).g;
    float shadow_h = texture2D(shadow_texture, v_texCoordsShadowmap).b;
    gl_FragColor = v_color * texture2D(u_texture, v_texCoords);// итоговый цвет пикселя

   if(shadow_y !=0f) {
        gl_FragColor.r -=0.2f;
        gl_FragColor.g -=0.2f;
        gl_FragColor.b -=0.2f;
    }





}