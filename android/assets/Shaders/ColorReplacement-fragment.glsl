#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
#else
	#define PRECISION
#endif

varying vec4 v_color;				//varying = partagé par le vertex shader et le fragment shader
varying vec2 v_texCoord0;

uniform PRECISION sampler2D u_sampler2D;		//sampler2D = prend la couleur d'un pixel d'une texture
uniform PRECISION vec3 u_output_color;

void main(){
	vec4 color = texture2D(u_sampler2D, v_texCoord0) * v_color;
	
	if(color.g > 0.28 && color.r < 0.2)
		color.rgb = u_output_color;
	
	gl_FragColor = color;  //Classic passthrough shader
}