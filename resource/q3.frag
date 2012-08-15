//#version 120
//
// simple.frag
//
//uniform mat4 mat;
uniform sampler2D texture0;
uniform vec3 lightdir;
varying vec3 normal;
varying vec4 color;
varying vec2 texcoord;

void main (void) {
	float diffuse = -dot( normalize(normal), normalize(lightdir) );
	float amb = 0.1;
	float r = max(diffuse, 0.0) + amb;
	gl_FragColor = vec4(r * color.xyz, color.w);
}

// vim: syntax=glsl
