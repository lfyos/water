#include "assemble.h"

assemble::assemble(part& part_collector, std::ofstream& f,FbxNode* assemble_node, int space_number)
{
	f << std::endl;

	std::string assemble_name=assemble_node->GetName();
	std::string part_name= part_collector.register_node(assemble_node);
	FbxAMatrix loca = assemble_node->EvaluateLocalTransform();
	int children_number = assemble_node->GetChildCount();

	output_space(f, space_number);	f << "/*	assemble name	*/	" << assemble_name << std::endl;
	output_space(f, space_number);	f << "/*	part name		*/	" << part_name	<< std::endl;
	output_space(f, space_number);	f << "/*	location		*/";
	
	for (int i=0; i < 4; i++)
		for (int j = 0; j < 4; j++)
			f << "	" << (loca.Get(i, j));
	f << std::endl;

	output_space(f, space_number);	f << "/*	child number	*/	" << children_number << std::endl;

	for (int i = 0; i < children_number; i++)
		assemble(part_collector,f,assemble_node->GetChild(i),space_number+1);
}