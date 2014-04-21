#version 110
varying vec4 v_color;
varying vec2 v_texCoords;

uniform mat4 u_projTrans;
uniform sampler2D u_texture;
void main() {
    gl_FragColor = v_color * texture2D(u_texture, v_texCoords);
    if (gl_FragColor.a > 0.0) {
        gl_FragDepth = 0.5;
    } else {
        gl_FragDepth = 0.0;
    }
}
