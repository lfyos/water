#pragma once

#include<string>
#include<iostream>
#include<fbxsdk.h>

class attribute_vertex
{
public:
	FbxVector4 vertex[3];

	attribute_vertex()
	{
		vertex[0].Set(0, 0, 0, 1);
		vertex[1].Set(0, 0, 0, 1);
		vertex[2].Set(0, 0, 0, 1);
	}
	void set(int id, double px, double py, double pz, double pw = 1.0)
	{
		vertex[id].Set(px, py, pz, pw);
	}
	void set(int id, FbxVector4 data)
	{
		vertex[id].Set(data[0], data[1], data[2], data[3]);
	}
	void set(int id, FbxVector2 data)
	{
		vertex[id].Set(data[0], data[1], 0, 1);
	}
	void set(int id, FbxColor data)
	{
		vertex[id].Set(data[0], data[1], data[2], data[3]);
	}
	~attribute_vertex()
	{
	}
};

class attribute_vertex_container
{
public:
	int attribute_number, triangle_number;
	attribute_vertex** attr;

	std::string caculate_box_string()
	{
		int vertex_number = 0;
		double x0=0, y0=0, z0=0, x1=0, y1=0, z1=0;
		for(int i=0;i<triangle_number;i++)
			for (int j = 0; j < 3; j++) {
				double my_x = attr[0][i].vertex[j][0]; 
				double my_y = attr[0][i].vertex[j][1]; 
				double my_z = attr[0][i].vertex[j][2];
				double my_w = attr[0][i].vertex[j][3];
				if ((my_w * my_w) > 0.0) {
					my_x /= my_w;
					my_y /= my_w;
					my_z /= my_w;
					my_w /= my_w;
				}
				
				if ((vertex_number++) <= 0) {
					x0 = my_x;
					y0 = my_y; 
					z0 = my_z;
					x1 = my_x;
					y1 = my_y;
					z1 = my_z;
				}else {
					x0 = (x0 < my_x) ? x0 : my_x;
					y0 = (y0 < my_y) ? y0 : my_y;
					z0 = (z0 < my_z) ? z0 : my_z;
					x1 = (x1 > my_x) ? x1 : my_x;
					y1 = (y1 > my_y) ? y1 : my_y;
					z1 = (z1 > my_z) ? z1 : my_z;
				}
			}
		if(vertex_number <= 0)
			return	"null";
		else
			return	std::to_string(x0) + "	" + std::to_string(y0) + "	" + std::to_string(z0) + "	" +
					std::to_string(x1) + "	" + std::to_string(y1) + "	" + std::to_string(z1);
	}
	std::string caculate_average_string(int attribute_id)
	{
		double average_x = 0;
		double average_y = 0;
		double average_z = 0;
		double average_w = 0;

		for (int i = 0; i < triangle_number; i++)
			for (int j = 0; j < 3; j++) {
				double w = attr[attribute_id][i].vertex[j][3];
				average_x += attr[attribute_id][i].vertex[j][0] / w;
				average_y += attr[attribute_id][i].vertex[j][1] / w;
				average_z += attr[attribute_id][i].vertex[j][2] / w;
				average_w ++;
			}
		if (average_w > 0) {
			average_x /= average_w;
			average_y /= average_w;
			average_z /= average_w;
			average_w /= average_w;
		}
		average_w = 1;
		return 
			  std::to_string(average_x) + "	"
			+ std::to_string(average_y) + "	"
			+ std::to_string(average_z) + "	"
			+ std::to_string(average_w);
	}
	attribute_vertex_container(int my_attribute_number, int my_triangle_number)
	{
		attribute_number = my_attribute_number;
		triangle_number = my_triangle_number;

		attr = new attribute_vertex * [attribute_number];
		for (int i = 0; i < attribute_number; i++)
			attr[i] = new attribute_vertex[triangle_number];
		return;
	}
	~attribute_vertex_container()
	{
		for (int i = 0; i < attribute_number; i++)
			delete[](attr[i]);
		delete[]attr;
		return;
	}
};

