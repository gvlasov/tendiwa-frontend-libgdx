#version 110
#define M_PI 3.1415926535897932384626433832795
varying vec4 v_color;
varying vec2 v_texCoords;

uniform mat4 u_projTrans;
uniform sampler2D u_texture;
uniform float waveState;
float dLightness(float x, float yMax);
float f(float x);
float rand(vec2 co);
void main() {
    gl_FragColor = v_color * texture2D(u_texture, v_texCoords);
    //gl_FragColor.rgb *= 1.0+ sin(waveState+mod(gl_FragCoord.x, 32.0)* M_PI/32.0*2.0)*0.05;
    gl_FragColor.rgb *= 1.0+dLightness(sin(waveState+rand(gl_FragCoord.xy)*M_PI*2.0), 0.2);
}
/**
 * Returns relative shift in point's lightness.
 * x is in [-1..1]
 */
float dLightness(float x, float yMax) {
    float shift = yMax-abs(f(x));
    if (shift < 0.0) {
        shift = 0.0;
    }
    return shift;
}
/**
 * Any function that grows on entire domain.
 */
float f(float x) {
    return x*1.0;
}
float rand(vec2 co) {
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}