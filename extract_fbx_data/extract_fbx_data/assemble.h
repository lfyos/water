#pragma once

#include<string>
#include<iostream>
#include<fstream>
#include<fbxsdk.h>

#include"part.h"
class assemble
{
private:
	void output_space(std::ofstream& f, int space_number)
	{
		for (int i = 0; i < space_number; i++)
			f << "	";
	};
public:
	assemble(part &part_collector,std::ofstream &f,FbxNode* my_assemble_node,int space_number);
};

