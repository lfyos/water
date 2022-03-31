#pragma once

#include <string>
#include <iostream>
#include <fstream>
#include <assimp/mesh.h>
#include <assimp/scene.h>
#include "mesh_set.h"

using namespace std;

class extract_assemble_data
{
public:
	extract_assemble_data(aiNode* p, ofstream& fw, string pre_str,mesh_set_collector &msc, int max_mesh_number);
};

