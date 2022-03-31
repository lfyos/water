#pragma once

#include <string>
#include <iostream>
#include <fstream>
#include <assimp/mesh.h>

#include"assimp_box.h"
#include"location.h"
#include"mesh_set.h"

using namespace std;

class extract_mesh_data
{
private:
	int triangle_number,line_vertex_number,point_vertex_number,max_texture_number,max_color_number;
	assimp_box triangle_box, edge_box, point_box, texture_box[500], color_box[500];
	void create_part_mesh_head(ofstream& f_head);
	void create_part_mesh_triangles(struct aiMesh* p_mesh, int material_id,ofstream& f_triangles);
	void create_part_mesh_lines_points(struct aiMesh* p_mesh, ofstream& f_lines, ofstream& f_points);
public:
	static string cut_special_char(string str);
	extract_mesh_data(struct aiMesh** p_mesh, unsigned int n_mesh, string target_directory_name, mesh_set_collector &msc);
};

