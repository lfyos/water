#include "location.h"
#include<cmath>

void location::init(double b[])
{
	double c[4][4] = {
		{b[0], b[4], b[8], b[12]},
		{b[1], b[5], b[9], b[13]},
		{b[2], b[6], b[10], b[14]},
		{b[3], b[7], b[11], b[15]}
	};
	for (int i = 0; i < 4; i++)
		for (int j = 0; j < 4; j++) {
			a[i][j] = c[i][j];
		}
	for (int i = 0; i < 16; i++)
		data[i] = b[i];
}
location::location()
{
	double b[16]{ 1,0,0,0,	0,1,0,0,	0,0,1,0,	0,0,0,1 };
	init(b);
}
void location::move_rotate(double mx, double my, double mz, double rx, double ry, double rz)
{
	if ((rx == 0) && (ry == 0) && (rz == 0)) {
		double b[] =
		{
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				mx, my, mz, 1
		};
		init(b);
		return;
	}

	double cos_alf = cos(rx * PI / 180), sin_alf = sin(rx * PI / 180);
	double cos_belta = cos(ry * PI / 180), sin_belta = sin(ry * PI / 180);
	double cos_gamma = cos(rz * PI / 180), sin_gamma = sin(rz * PI / 180);
	double b[] =
	{
			cos_gamma * cos_belta,									//	0:	a[0][0]
			sin_gamma * cos_belta,									//	1:	a[1][0]
			-sin_belta,												//	2:	a[2][0]
			0,														//	3:	a[3][0]

			-sin_gamma * cos_alf + cos_gamma * sin_belta * sin_alf,	//	4:	a[0][1]
			cos_gamma * cos_alf + sin_gamma * sin_belta * sin_alf,	//	5:	a[1][1]
			cos_belta * sin_alf,										//	6:	a[2][1]
			0,														//	7:	a[3][1]

			sin_gamma * sin_alf + cos_gamma * sin_belta * cos_alf,	//	8:	a[0][2]
			-cos_gamma * sin_alf + sin_gamma * sin_belta * cos_alf,	//	9:	a[1][2]
			cos_belta * cos_alf,										//	10:	a[2][2]
			0,														//	11:	a[3][2]

			mx,														//	12:	a[0][3]
			my,														//	13:	a[1][3]
			mz,														//	14:	a[2][3]
			1														//	15:	a[3][3]
	};
	init(b);
	return;
}
void location::scale(double sx, double sy, double sz)
{
	double b[] =
	{
		sx, 0, 0, 0,
		0, sy, 0, 0,
		0, 0, sz, 0,
		0, 0, 0, 1
	};
	init(b);
	return;
}
void location::multiply(location& b)
{
	double x[16];
	for (int i = 0; i < 4; i++)
		for (int j = 0; j < 4; j++) {
			x[i + 4 * j] = 0;
			for (int k = 0; k < 4; k++)
				x[i + 4 * j] += a[i][k] * (b.a[k][j]);
		}
	init(x);
}
