#ifdef GL_ES
    #define PRECISION mediump
    precision PRECISION float;
#else
    #define PRECISION
#endif

varying vec4 v_color;
varying vec2 v_texCoord0;

uniform PRECISION sampler2D u_texture;
uniform PRECISION vec3 u_output_color;

void main() {
    vec4 color = texture2D(u_texture, v_texCoord0) * v_color;
    float greenMask = step(0.28, color.g);
    float redMask = 1.0 - step(0.2, color.r);
    float replacementMask = greenMask * redMask;
    color.rgb = mix(color.rgb, u_output_color, replacementMask);
    gl_FragColor = color;
}
