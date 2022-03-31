#include "default_parameter.h"

default_parameter::default_parameter(string parameter_file_name)
{
	ifstream f(parameter_file_name);

	if (f.fail()) {
		color[0] = 1.0; color[1] = 1.0; color[2] = 1.0; color[3] = 1.0;
		ambient[0] = 1.0; ambient[1] = 1.0; ambient[2] = 1.0; ambient[3] = 1.0;
		diffuse[0] = 1.0; diffuse[1] = 1.0; diffuse[2] = 1.0; diffuse[3] = 1.0;
		specular[0] = 1.0; specular[1] = 1.0; specular[2] = 1.0; specular[3] = 1.0;
		emissive[0] = 1.0; emissive[1] = 1.0; emissive[2] = 1.0; emissive[3] = 1.0;
		shininess = 8;
	}
	else {
		string title;
		f >> title >> color[0] >> color[1] >> color[2] >> color[3];
		f >> title >> ambient[0] >> ambient[1] >> ambient[2] >> ambient[3];
		f >> title >> diffuse[0] >> diffuse[1] >> diffuse[2] >> diffuse[3];
		f >> title >> specular[0] >> specular[1] >> specular[2] >> specular[3];
		f >> title >> emissive[0] >> emissive[1] >> emissive[2] >> emissive[3];
		f >> title >> shininess;
	}

	f.close();
}