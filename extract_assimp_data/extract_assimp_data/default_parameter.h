#pragma once

#include <string>
#include <iostream>
#include <fstream>

using namespace std;

class default_parameter
{
public:
	double color[4],ambient[4], diffuse[4], specular[4], emissive[4], shininess;
	default_parameter(string parameter_file_name);
};

