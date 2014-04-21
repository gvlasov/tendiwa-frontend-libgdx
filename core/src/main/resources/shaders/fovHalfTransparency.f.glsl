uniform mat4 u_projTrans;
uniform sampler2D u_texture;
varying vec2 v_texCoords;
varying vec4 v_color;
void main() {
    gl_FragColor = v_color * texture2D(u_texture, v_texCoords);
    if (gl_FragColor.a > 0.9) {
        gl_FragColor.a = 0.6;
    }
}