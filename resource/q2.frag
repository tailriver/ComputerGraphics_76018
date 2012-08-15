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
//	gl_FragColor = texture2D(texture0, texcoord);
	gl_FragColor = color;
}

// vim: syntax=glsl
