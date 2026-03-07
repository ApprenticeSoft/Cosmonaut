#ifdef GL_ES
    #define PRECISION mediump
    precision PRECISION float;
#else
    #define PRECISION
#endif

varying vec4 v_color;
varying vec2 v_texCoord0;

uniform vec2 u_resolution;
uniform float u_PosX;
uniform float u_PosY;
uniform sampler2D u_texture;
uniform float outerRadius;
uniform float innerRadius;
uniform float intensity;

void main() {
    vec4 color = texture2D(u_texture, v_texCoord0) * v_color;

    vec2 relativePosition = gl_FragCoord.xy / u_resolution - vec2(u_PosX, u_PosY);
    relativePosition.x *= u_resolution.x / u_resolution.y;
    float len = length(relativePosition);
    float vignette = smoothstep(outerRadius, innerRadius, len);
    color.rgb = mix(color.rgb, color.rgb * vignette, intensity);

    gl_FragColor = color;
}
