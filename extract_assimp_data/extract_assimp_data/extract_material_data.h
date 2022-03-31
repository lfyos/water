#pragma once

#include <string>
#include <iostream>
#include <fstream>
#include <assimp/material.h>

#include"assimp_box.h"
#include"location.h"
#include"default_parameter.h"
#include"mesh_set.h"

using namespace std;

class extract_material_data
{
private:
	void do_extract_routine(int vertex_color_type,
		struct aiMaterial*m, ofstream& fw, string pre_str, default_parameter & par);
public:
	extract_material_data(
		struct aiMesh** p_mesh, unsigned int n_mesh,
		struct aiMaterial ** mMaterials,unsigned int mNumMaterials,
		string target_directory_name, default_parameter & par,
		mesh_set_collector &msc);
};

