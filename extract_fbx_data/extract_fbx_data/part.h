#pragma once

#include<iostream>
#include<fstream>
#include<string>

#include<fbxsdk.h>

class part
{
	std::string directory_name;
	std::ofstream* part_list_f;
	FbxClassId part_id_array[100000];
	int register_number;
public:
	part(std::string my_directory_name);
	~part();
	std::string register_node(FbxNode* assemble_node);

private:
	std::string process_name(std::string name);
	void output_part_mesh(FbxMesh* fm);
	void output_part_material(FbxMesh* fm, FbxNode* fn);
};

