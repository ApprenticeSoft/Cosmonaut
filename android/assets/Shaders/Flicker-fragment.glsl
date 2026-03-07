#ifdef GL_ES
    #define PRECISION mediump
    precision PRECISION float;
#else
    #define PRECISION
#endif

varying vec4 v_color;
varying vec2 v_texCoord0;

uniform PRECISION sampler2D u_texture;
uniform PRECISION vec3 u_color;

void main() {
    vec4 color = texture2D(u_texture, v_texCoord0) * v_color;
    if (color.r > 0.8) {
        if (color.g < 0.1) {
            color.rgb = u_color;
        }
    }
    gl_FragColor = color;
}
