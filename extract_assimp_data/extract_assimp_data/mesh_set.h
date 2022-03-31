#pragma once

#include <string>
#include <iostream>
#include <fstream>
#include <assimp/mesh.h>
#include <assimp/scene.h> 

using namespace std;

class mesh_set
{
public:
	bool* mesh_id_array;
	int mesh_number;

	string system_name, user_name;
	mesh_set(int max_number, aiNode* node);
	bool compare(int max_number,mesh_set* node);
	~mesh_set()
	{
		delete[]mesh_id_array;
	};
};

class mesh_set_collector
{
public:
	mesh_set *mesh_set_array[100000];
	int mesh_set_number;
	string register_mesh(int max_number, aiNode* node);
	mesh_set_collector();
	~mesh_set_collector();
};

