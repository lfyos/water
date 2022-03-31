#pragma once
class assimp_box
{
public:
	int vertex_number;
	double min_x, min_y, min_z, min_w;
	double max_x, max_y, max_z, max_w;
	double sum_x, sum_y, sum_z, sum_w;

	void reset();
	void append_box(assimp_box& s);
	void add_vertex(double x, double y, double z, double w);
	assimp_box();
};

