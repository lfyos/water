#pragma once
class location
{
private:
	double a[4][4];
	void init(double b[]);
public:
	double data[16];
	location();
	void move_rotate(double mx, double my, double mz, double rx, double ry, double rz);
	void scale(double sx, double sy, double sz);
	void multiply(location& b);
};

#define PI 3.141592653589793238462643383279502884L

