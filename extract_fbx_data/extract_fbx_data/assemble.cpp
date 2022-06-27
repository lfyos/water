#include "assemble.h"

assemble::assemble(part& part_collector, std::ofstream& f, FbxNode* assemble_node,
	int space_number, std::string assemble_parent_name)
{
	f << std::endl;

	std::string assemble_name=part::process_name(assemble_node->GetName());
	std::string part_name= part_collector.register_node(assemble_node);
	fbxsdk::FbxAMatrix loca = assemble_node->EvaluateLocalTransform();
	int children_number = assemble_node->GetChildCount();

	if (assemble_name.size() <= 0)
		assemble_name = "no_assemble_name_"+std::to_string(component_id);
	assemble_name = assemble_parent_name + "/" + assemble_name;

	output_space(f, space_number);
	
	f << "/*	assemble name	*/	" << assemble_name;
	f << "	/*	component_id:" << component_id;
	f << "	leaf_component_id:" << leaf_component_id << "	*/";
	f << std::endl;

	output_space(f, space_number);
	f << "/*	part name		*/	" << part_name	<< std::endl;
	output_space(f, space_number);
	f << "/*	location		*/";
	
	for (int i=0; i < 4; i++)
		for (int j = 0; j < 4; j++)
			f << "	" << (loca.Get(i, j));
	f << std::endl;

	output_space(f, space_number);
	f << "/*	child number	*/	" << children_number << std::endl;

	component_id++;
	leaf_component_id += (children_number > 0) ? 0 : 1;

	for (int i = 0; i < children_number; i++)
		assemble assem(part_collector, f, assemble_node->GetChild(i), space_number + 1, assemble_name);
}

int component_id=0, leaf_component_id = 0;