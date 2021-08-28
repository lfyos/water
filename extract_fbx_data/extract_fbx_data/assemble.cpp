#include "assemble.h"


assemble::assemble(
	part* part_collector, std::ofstream* f,
	FbxNode* assemble_node, int space_number)
{
	int children_number = assemble_node->GetChildCount();

	(*f) << std::endl;
	output_space(f, space_number);
	(*f) << "/*	assemble name	*/	" << assemble_node->GetName()<< std::endl;

	output_space(f, space_number);
	(*f) << "/*	part name		*/	" << part_collector->register_node(assemble_node) << std::endl;

	output_space(f, space_number);
	(*f) << "/*	location		*/";
	FbxAMatrix loca=assemble_node->EvaluateLocalTransform();
	for(int i=0;i<4;i++)
		for(int j=0;j<4;j++)
			(*f) << "	"<<loca.Get(i,j);
	(*f) << std::endl;


	output_space(f, space_number);
	(*f) << "/*	child number	*/	" << children_number << std::endl;

	for (int i = 0; i < children_number; i++)
		delete (new assemble(part_collector,f,assemble_node->GetChild(i), space_number+1));

	return;
}

void assemble::output_space(std::ofstream *f, int space_number)
{
	for (int i = 0; i < space_number; i++)
		(*f) << "	";
}
