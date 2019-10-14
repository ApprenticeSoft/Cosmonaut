#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
#else
	#define PRECISION
#endif

uniform sampler2D u_texture;
uniform sampler2D u_mask;
uniform float     u_time;

varying vec4 v_color;
varying vec2 v_texCoord0;

void main()
{
    vec2 mask_coord = v_texCoord0;
    vec2 new_coord = v_texCoord0;
    
	// scroll the mask
	mask_coord.y += (u_time / 8.0);
	// clamp the texture to make it repeat
	// indefinitely
	mask_coord = fract(mask_coord);
	
	// apply the displacement
	// to the mask texture
	vec4 mask = texture2D(u_mask, mask_coord);
	
	//Scroll the texture
	new_coord.y += (u_time / 20.0);
	new_coord = fract(new_coord);
	
	
	//if(v_texCoord0.y < 0.5){
	// calculate the stretching factor
    float factor = ((.5 - new_coord.x) * (v_texCoord0.y));
    // apply the factor and stretch the image
    new_coord.x -= 5.0*factor;
    //}
	
	/*
	//Déplacement latéral et vertical
		//Défilement de la texture de déplacement
	vec2 new_map_coord2 = v_texCoord0;
	new_map_coord2.y = (u_time  / 5.0);
	//new_map_coord2.x = (u_time  / 7.0);
	new_map_coord2 = fract(new_map_coord2);
	vec4 height_map_color2 = texture2D(u_mask, new_map_coord2);
		//Déplacement de la texture principale
	new_coord.x += ((0.3 - height_map_color2.a) * .3) * (1 - v_texCoord0.y);
	*/
	//Création de la texture finale
    vec4 texColor = texture2D(u_texture, new_coord);
    texColor.a *= mask.a;
    
    //Dégradé
    if(v_texCoord0.y < 0.15)
   		texColor.a *= v_texCoord0.y/0.15;
   	else if(v_texCoord0.y > 0.90)
   		texColor.a /= .5+v_texCoord0.y;
   		
   	if(texColor.r > 0.45)
   		texColor.rgba = vec4(0.0,0.0,0.0,0.0);
    
    gl_FragColor = v_color * texColor;
}