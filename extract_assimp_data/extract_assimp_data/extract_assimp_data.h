#pragma once

#include <iostream>
#include <fstream>


#include <assimp/Importer.hpp>      // C++ importer interface
#include <assimp/scene.h>           // Output data structure
#include <assimp/postprocess.h>     // Post processing flags

using  namespace std;

class extract_assimp_data
{
public:
	const aiScene* scene;

	extract_assimp_data(const char* file_name);

	~extract_assimp_data();
};

