#version 110
varying vec4 v_color;
varying vec2 v_texCoords;

uniform mat4 u_projTrans;
uniform sampler2D u_texture;
void main() {
    gl_FragColor = v_color * texture2D(u_texture, v_texCoords);
    if (gl_FragColor.a > 0.0 && gl_FragColor.b > 0.0) {
        gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
    }
}
