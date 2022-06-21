#pragma once

#include<iostream>
#include<fstream>
#include<string>

#include<fbxsdk.h>
#include"triangle_material_id.h"
#include"attribute_vertex.h"

class mesh
{
private:
	void extract_material_id();
	void extract_vertex();
	void extract_normal();
	void extract_color();
	void extract_texture();
	void extract_tangent();
	void write_mesh_head(std::string file_name);
public:
	FbxMesh* pMesh;

	int triangle_number;
	triangle_material_id *material_id;
	double default_material[4];
	attribute_vertex_container *vertex,* normal,* color, * texture,*tangent;

	mesh(FbxMesh* my_pMesh, std::string file_name)
	{
		pMesh = my_pMesh;
		triangle_number = pMesh->GetPolygonCount();

		extract_material_id();
		extract_vertex();
		extract_normal();
		extract_color();
		extract_texture();
		extract_tangent();

		write_mesh_head(file_name);
	}
	~mesh()
	{
		delete[]material_id;
		delete vertex;
		delete normal;
		delete color;
		delete texture;
		delete tangent;
	}
};

