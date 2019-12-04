#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif
varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform vec2 player;
uniform vec2 uvView;
void main()
{
  vec4 texColor = texture2D(u_texture, v_texCoords);
  vec2 posPlayerNormal = vec2(player.x/uvView.x,player.y/uvView.y);
  float dist = distance(v_texCoords,posPlayerNormal);
  float minColor = 0.2;
  if(dist<0.03) {
    gl_FragColor = vec4(texColor.r*0.2+texColor.r*dist,
                        texColor.g*0.2+texColor.g*dist,
                        texColor.b*0.2+texColor.b*dist,1);
  } else {
    gl_FragColor = vec4(texColor.r,texColor.g,texColor.b,1);
  }

}