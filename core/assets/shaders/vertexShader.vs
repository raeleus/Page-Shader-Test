#ifdef GL_ES
    precision mediump float;
#endif

attribute vec4 a_position;
attribute vec4 a_texCoord0;

uniform mat4 u_projTrans;
uniform float u_time;

varying float v_time;
varying vec2 v_texCoords;

void main() {
    v_time = u_time;
    v_texCoords = a_texCoord0;
    gl_Position = u_projTrans * a_position;
}