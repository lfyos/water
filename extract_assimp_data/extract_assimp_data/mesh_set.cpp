#include "mesh_set.h"

mesh_set::mesh_set(int max_number, aiNode* node)
{
	mesh_number = node->mNumMeshes;
	system_name = string(node->mName.C_Str());
	user_name = system_name;
	mesh_id_array = new bool[max_number];
	for (int i = 0; i < max_number; i++)
		mesh_id_array[i] = false;
	for (int i = 0, ni = node->mNumMeshes; i < ni; i++)
		mesh_id_array[node->mMeshes[i]] = true;
}
bool mesh_set::compare(int max_number,mesh_set* p)
{
	if (p->mesh_number != this->mesh_number)
		return false;
	for (int i = 0; i < max_number; i++)
		if (p->mesh_id_array[i] ^ this->mesh_id_array[i])
			return false;
	return true;
}
string mesh_set_collector::register_mesh(int max_number, aiNode* node)
{
	if (node->mNumMeshes <= 0)
		return string(node->mName.C_Str())+"_no_part";

	mesh_set* p = new mesh_set(max_number, node);
	for (int i = 0; i < mesh_set_number; i++)
		if (p->compare(max_number,mesh_set_array[i])) {
			delete p;
			return string(mesh_set_array[i]->system_name);
		}
	p->system_name = string("part_") + to_string(mesh_set_number);
	mesh_set_array[mesh_set_number++] = p;
	return string(p->system_name);
}
mesh_set_collector::mesh_set_collector()
{
	for (int i = 0, ni = sizeof(mesh_set_array) / sizeof(mesh_set_array[0]); i < ni; i++)
		mesh_set_array[i] = NULL;
	mesh_set_number = 0;
}
mesh_set_collector::~mesh_set_collector()
{
	for (int i = 0; i < mesh_set_number; i++)
		delete(mesh_set_array[i]);
	mesh_set_number = NULL;
}