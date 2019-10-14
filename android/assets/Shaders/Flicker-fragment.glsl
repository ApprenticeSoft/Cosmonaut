#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
#else
	#define PRECISION
#endif

varying vec4 v_color;				//varying = partagé par le vertex shader et le fragment shader
varying vec2 v_texCoord0;

uniform PRECISION sampler2D u_sampler2D;		//sampler2D = prend la couleur d'un pixel d'une texture
uniform PRECISION vec3 u_color;

void main(){
	vec4 color = texture2D(u_sampler2D, v_texCoord0) * v_color;
	
	if(color.r > 0.8){
		if(color.g < 0.1)
			color.rgb = u_color;
	}		
	//Test
	/*
	else{		
		bool allred = true;
	   	for( float ix = -0.01; ix < 0.01; ix += 0.001 )
	   	{
	    	for( float iy = -0.01; iy < 0.01; iy += 0.001 )
	       	{
	        	float newColor = texture2D(u_sampler2D, v_texCoord0.xy + vec2(ix, iy)).r;
	          	
	          	if(newColor > 0.8){
					//if(newColor.g < 0.1)
						color.rgb = vec3(0,1,0.5);
				}
				
	      	}
	   	}
   	}
	*/
	gl_FragColor = color;  //Classic passthrough shader
}