#include "assemble.h"


assemble::assemble(part* part_collector, std::ofstream* f,FbxNode* assemble_node, int space_number)
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

	double location_matrix_data[16];
	int n = 0, map[] = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
	for (int i = 0; i < 4; i++)
		for (int j = 0; j < 4; j++)
			location_matrix_data[n++] = loca.Get(i, j);
	for(int i=0;i<n;i++)
		(*f) << "	"<< location_matrix_data[map[i]];
	(*f) << std::endl;

	output_space(f, space_number);
	(*f) << "/*	child number	*/	" << children_number << std::endl;

	for (int i = 0; i < children_number; i++)
		assemble(part_collector,f,assemble_node->GetChild(i), space_number+1);

	return;
}
void assemble::output_space(std::ofstream *f, int space_number)
{
	for (int i = 0; i < space_number; i++)
		(*f) << "	";
}
