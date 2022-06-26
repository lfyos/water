#pragma once

#include<iostream>
#include<fstream>
#include<string>

#include<fbxsdk.h>

#include "mesh.h"

class part
{
private:
	std::string directory_name;
	std::ofstream* part_list_f;
	FbxMesh**	part_id_array;
	int register_number;

	bool compare_mesh(FbxMesh* s, FbxMesh* d)
	{
		return (s==d)?true:false;
	}
public:
	static std::string process_name(std::string name)
	{
		std::string ret_val = std::string(name);
		for (int i = 0, ni = (int)(ret_val.length()); i < ni; i++)
			switch (ret_val[i]) {
			case ' ':
			case '\t':
			case '\n':
			case '\r':
				ret_val[i] = '_';
				break;
			}
		return ret_val;
	}
	void output_part_material(FbxMesh* fm, FbxNode* fn);

	std::string register_node(FbxNode* assemble_node)
	{
		if (assemble_node->GetNodeAttribute())
			switch (assemble_node->GetNodeAttribute()->GetAttributeType()) {
			case FbxNodeAttribute::EType::eMesh:
				FbxMesh* fm = assemble_node->GetMesh();

				std::string part_system_name = "fbx_part_" + std::to_string(register_number);
				std::string part_user_name = process_name(fm->GetName());
				if (part_user_name.size() <= 0)
					part_user_name = part_system_name;
	
				for (int i = 0; i < register_number; i++)
					if (compare_mesh(part_id_array[i],fm))
						return "fbx_part_" + std::to_string(i);

				(*part_list_f) << part_user_name << std::endl;
				(*part_list_f) << part_system_name << std::endl;
				(*part_list_f) << "part_" << register_number << ".mesh" << std::endl;
				(*part_list_f) << "part_" << register_number << ".material" << std::endl;
				(*part_list_f) << "part_" << register_number << ".description" << std::endl;
				(*part_list_f) << "part_" << register_number << ".mp3" << std::endl;
				(*part_list_f) << std::endl;

				std::string mesh_directory = directory_name + "part_" + std::to_string(register_number);

				std::cout << "Begin extract mesh data from part " << part_user_name << " (NO. " << register_number << ")" ;

				mesh part_mesh(fm, mesh_directory + ".mesh",mesh_directory + ".mesh.face" );

				std::cout << "		End extract mesh data from part " << part_user_name << " (NO. " << register_number << ")" << std::endl;

				part_id_array[register_number++] = fm;

				return part_system_name;
			}
		return std::string("NOT_exist_fbx_part_name");
	}

	part(std::string my_directory_name)
	{
		directory_name = my_directory_name;
		part_list_f = new std::ofstream(directory_name + "part.list");
		register_number = 0;
		part_id_array = new FbxMesh * [100000];
	}
	~part()
	{
		part_list_f->close();
		delete[]part_id_array;
	}
};

