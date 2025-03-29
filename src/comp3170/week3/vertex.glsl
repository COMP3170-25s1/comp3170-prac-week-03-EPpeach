#version 410

in vec4 a_position;	// vertex position as a homogenous vector in NDC 
in vec3 a_colour; // vertex colour RGB
uniform mat4 u_modelMatrix; // modelMatrix for transformations (slides: model -> world co-ord

out vec3 v_colour; // to fragment shader

void main() {
	v_colour = a_colour;

//	// pad the vertex to a homogeneous 3D point
//    gl_Position = a_position;

	//convert vertex to world co-ords
	vec4 pos = u_modelMatrix * a_position;
	//pad to vec4 (slides use vec3, so have to cast to vec 4 here. We've written everything in vec4, so we can just pass vec 4. We've written the other stuff with the assumption that z=0)
	gl_Position = pos;

}

