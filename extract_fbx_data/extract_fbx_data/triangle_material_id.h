#pragma once
class triangle_material_id
{
public:
	int material_id[4];
	triangle_material_id()
	{
		material_id[0] = 0;
		material_id[1] = 0;
		material_id[2] = 0;
		material_id[3] = 0;
	}
};
