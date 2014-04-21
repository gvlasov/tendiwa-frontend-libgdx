#version 110
varying vec4 v_color;
varying vec2 v_texCoords;

uniform mat4 u_projTrans;
uniform sampler2D u_texture;
void main() {
    vec4 tex = texture2D(u_texture, v_texCoords);
    gl_FragColor = v_color * tex;
    if (gl_FragColor.a < 0.5) {
        // If a texel is transparent
        gl_FragDepth = 0.5;
    } else {
        // If it is opaque, then it passes depth test with value 0.0
        gl_FragDepth = 0.0;
    }
}
