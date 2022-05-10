#pragma once

#include<iostream>
#include<fstream>
#include<fbxsdk.h>

#include"part.h"
class assemble
{
public:
	assemble(part &part_collector,std::ofstream &f,
		FbxNode* my_assemble_node,int space_number);

private:
	void output_space(std::ofstream &f, int space_number);
};

