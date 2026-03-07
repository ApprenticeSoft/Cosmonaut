#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D u_texture;

// The inverse of the viewport dimensions along X and Y
uniform vec2 u_viewportInverse;

// Color of the outline
uniform vec3 u_color;

// Thickness of the outline
uniform float u_offset;

varying vec4 v_color;
varying vec2 v_texCoord;

#define ALPHA_VALUE_BORDER 0.5
const int SAMPLE_RADIUS = 3;

void main() {
   float alpha = 0.0;
   bool allIn = true;
   vec2 sampleStep = u_viewportInverse * (u_offset / float(SAMPLE_RADIUS));

   for (int ix = -SAMPLE_RADIUS; ix <= SAMPLE_RADIUS; ix++) {
      for (int iy = -SAMPLE_RADIUS; iy <= SAMPLE_RADIUS; iy++) {
         float sampleAlpha = texture2D(u_texture, v_texCoord + vec2(float(ix), float(iy)) * sampleStep).a;
         if (sampleAlpha > ALPHA_VALUE_BORDER) {
            alpha = max(alpha, sampleAlpha);
         } else {
            allIn = false;
         }
      }
   }

   if (allIn) {
      alpha = 0.0;
   }

   gl_FragColor = vec4(u_color, alpha);
}
