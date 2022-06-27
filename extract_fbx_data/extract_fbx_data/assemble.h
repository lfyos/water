#pragma once

#include<string>
#include<iostream>
#include<fstream>
#include<fbxsdk.h>

#include"part.h"

extern int component_id,leaf_component_id;

class assemble
{
private:
	void output_space(std::ofstream& f, int space_number)
	{
		for (int i = 0; i < space_number; i++)
			f << "	";
	};
public:
	assemble(part &part_collector,std::ofstream &f,FbxNode* assemble_node,
		int space_number,std::string assemble_parent_name);
};

