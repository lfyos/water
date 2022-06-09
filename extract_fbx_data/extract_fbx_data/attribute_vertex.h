#pragma once

#include<fbxsdk.h>

class attribute_vertex
{
public:
	int vertex_number;
	FbxVector4* vertex;
	attribute_vertex()
	{
		vertex_number = 3;
		vertex = new FbxVector4[3];
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
		delete[]vertex;
	}
};

class attribute_vertex_container
{
public:
	int attribute_number, triangle_number;
	attribute_vertex** attr;
	double box[8];

	void caculate(int attribute_id)
	{
		double w0 = attr[attribute_id][0].vertex[0][3];
		double x0 = attr[attribute_id][0].vertex[0][0] / w0, x1 = x0;
		double y0 = attr[attribute_id][0].vertex[0][1] / w0, y1 = y0;
		double z0 = attr[attribute_id][0].vertex[0][2] / w0, z1 = z0;
		for (int i = 0; i < triangle_number; i++)
			for (int j = 0; j < 3; j++) {
				double w = attr[attribute_id][i].vertex[j][3];
				double x = attr[attribute_id][i].vertex[j][0] / w;
				double y = attr[attribute_id][i].vertex[j][1] / w;
				double z = attr[attribute_id][i].vertex[j][2] / w;

				x0 = (x0 < x) ? x0 : x;
				y0 = (y0 < y) ? y0 : y;
				z0 = (z0 < z) ? z0 : z;

				x1 = (x1 > x) ? x1 : x;
				y1 = (y1 > y) ? y1 : y;
				z1 = (z1 > z) ? z1 : z;
			}
		box[0] = x0;
		box[1] = y0;
		box[2] = z0;
		box[3] = 1;
		box[4] = x1;
		box[5] = y1;
		box[6] = z1;
		box[7] = 1;
		return;
	}
	~attribute_vertex_container()
	{
		for (int i = 0; i < attribute_number; i++)
			delete[](attr[i]);
		delete[]attr;
	}
	attribute_vertex_container(int my_attribute_number, int my_triangle_number)
	{
		attribute_number = my_attribute_number;
		triangle_number = my_triangle_number;

		attr = new attribute_vertex * [attribute_number];
		for (int i = 0; i < attribute_number; i++)
			attr[i] = new attribute_vertex[triangle_number];

		box[0] = 0;
		box[1] = 0;
		box[2] = 0;
		box[3] = 1;

		box[4] = 1;
		box[5] = 1;
		box[6] = 1;
		box[7] = 1;
	}
};

