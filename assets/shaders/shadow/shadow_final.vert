attribute vec4 a_position; //позиция вершины
attribute vec4 a_color; //цвет вершины
attribute vec2 a_texCoord0; //координаты текстуры
uniform mat4 u_projTrans;  //матрица, которая содержим данные для преобразования проекции и вида
varying vec4 v_color;  //цвет который будет передан в фрагментный шейдер
varying vec2 v_texCoords;  //координаты текстуры
varying vec2 v_texCoordsShadowmap;

uniform vec2 u_world_size;
varying vec2 v_position;

uniform float u_globalPosition;
varying float v_gPosition;
varying vec3 shadow;
uniform sampler2D shadow_texture;
void main(){
    v_color=a_color;
    v_color.a = v_color.a * (255.0/254.0);
    v_texCoords = a_texCoord0;
    //применяем преобразование вида и проекции, можно не забивать себе этим голову
    // тут происходят математические преобразование что-бы правильно учесть параметры камеры
    // gl_Position это окончательная позиция вершины

    gl_Position =  u_projTrans * a_position;

    v_texCoordsShadowmap = (gl_Position.xy * 0.5) + 0.5;

    float shadow_y = texture2D(shadow_texture, v_texCoordsShadowmap).g;
    float shadow_h = texture2D(shadow_texture, v_texCoordsShadowmap).b;


    v_position = vec2(gl_Position.x/u_world_size.x,gl_Position.y/u_world_size.y);
    v_gPosition = u_globalPosition;
    shadow = vec3(0f,0f,0f);
    if(v_gPosition >= shadow_y && v_gPosition<=(shadow_y+shadow_h)) {
        shadow.r =0.2f;
        shadow.g =0.2f;
        shadow.b =0.2f;
    }
}