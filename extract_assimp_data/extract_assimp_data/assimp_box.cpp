#include "assimp_box.h"

void assimp_box::reset()
{
	vertex_number = 0;
	min_x = 0;
	min_y = 0;
	min_z = 0;
	max_x = 0;
	max_y = 0;
	max_z = 0;
	sum_x = 0;
	sum_y = 0;
	sum_z = 0;
	min_w = 0;
	max_w = 0;
	sum_w = 0;
}
void assimp_box::append_box(assimp_box& s)
{
	if (s.vertex_number <= 0)
		return;
	if (vertex_number <= 0) {
		vertex_number = s.vertex_number;
		min_x = s.min_x;
		min_y = s.min_y;
		min_z = s.min_z;
		max_x = s.max_x;
		max_y = s.max_y;
		max_z = s.max_z;
		sum_x = s.sum_x;
		sum_y = s.sum_y;
		sum_z = s.sum_z;
		min_w = s.min_w;
		max_w = s.max_w;
		sum_w = s.sum_w;
		return;
	}
	vertex_number += s.vertex_number;

	min_x = (min_x <= s.min_x) ? min_x : s.min_x;
	min_y = (min_y <= s.min_y) ? min_y : s.min_y;
	min_z = (min_z <= s.min_z) ? min_z : s.min_z;
	min_w = (min_w <= s.min_w) ? min_w : s.min_w;

	max_x = (max_x >= s.max_x) ? max_x : s.max_x;
	max_y = (max_y >= s.max_y) ? max_y : s.max_y;
	max_z = (max_z >= s.max_z) ? max_z : s.max_z;
	max_w = (max_w >= s.max_w) ? max_w : s.max_w;

	sum_x += s.sum_x;
	sum_y += s.sum_y;
	sum_z += s.sum_z;
	sum_w += s.sum_w;
}
void assimp_box::add_vertex(double x, double y, double z, double w)
{
	if (vertex_number <= 0) {
		min_x = x;
		min_y = y;
		min_z = z;
		min_w = w;
		max_x = x;
		max_y = y;
		max_z = z;
		max_w = w;
		sum_x = x;
		sum_y = y;
		sum_z = z;
		sum_w = w;
		vertex_number = 1;
		return;
	}

	vertex_number++;

	if (x < min_x)
		min_x = x;
	if (y < min_y)
		min_y = y;
	if (z < min_z)
		min_z = z;
	if (w < min_w)
		min_w = w;

	if (x > max_x)
		max_x = x;
	if (y > max_y)
		max_y = y;
	if (z > max_z)
		max_z = z;
	if (w > max_w)
		max_w = w;

	sum_x += x;
	sum_y += y;
	sum_z += z;
	sum_w += w;
}
assimp_box::assimp_box()
{
	reset();
}