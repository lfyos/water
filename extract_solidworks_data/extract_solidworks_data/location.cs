using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace extract_solidworks_data
{
    class location
    {
		private double [][]a;
		public location(double []b)
		{
			a = new double[][]
			{
				new double[]{ b[ 0], b[ 4], b[ 8], b[12]},
				new double[]{ b[ 1], b[ 5], b[ 9], b[13]},
				new double[]{ b[ 2], b[ 6], b[10], b[14]},
				new double[]{ b[ 3], b[ 7], b[11], b[15]}
			};
		}
		public double[] get_data()
		{
			return new double[]
			{
				a[0][0],a[1][0],a[2][0],a[3][0],
				a[0][1],a[1][1],a[2][1],a[3][1],
				a[0][2],a[1][2],a[2][2],a[3][2],
				a[0][3],a[1][3],a[2][3],a[3][3],
			};
		}
		public location negative()
		{
			int i, j, k;
			double[][] b = new double[][]
			{
				new double[]{ a[0][0], a[0][1], a[0][2], a[0][3]},
				new double[]{ a[1][0], a[1][1], a[1][2], a[1][3]},
				new double[]{ a[2][0], a[2][1], a[2][2], a[2][3]},
				new double[]{ a[3][0], a[3][1], a[3][2], a[3][3]}
			};
			double[][] c = new double[][]
			{
				new double[]{ 1,0,0,0},
				new double[]{ 0,1,0,0},
				new double[]{ 0,0,1,0},
				new double[]{ 0,0,0,1}
			};
			double p;

			for(i=0;i<3;i++){
				for(k=i,j=i+1;j<4;j++)
					if(System.Math.Abs(b[j][i])> System.Math.Abs(b[k][i]))
						k=j;
				for(j=0;j<4;j++){
					p=b[i][j];		b[i][j]=b[k][j];		b[k][j]=p;
					p=c[i][j];		c[i][j]=c[k][j];		c[k][j]=p;
				}
				for(j=i+1;j<4;j++){
					p=b[j][i]/b[i][i];
					for(k=i+1;k<4;k++)
						b[j][k]-=p* b[i][k];
					for(k=0;k<4;k++)
						c[j][k]-=p* c[i][k];
				}
			}
			for(i=3;i>0;i--)
				for(j=0;j<i;j++)
				{
					p=b[j][i]/b[i][i];
					for(k=0;k<4;k++)
						c[j][k]-=p* c[i][k];
					for(k=i+1;k<4;k++)
						b[j][k]-=p* b[i][k];
				}

			for(i=0;i<4;i++)
				for(j=0;j<4;j++)
					c[i][j]/=b[i][i];

			return new location(new double[] 
			{ 
				c[0][0],c[1][0],c[2][0],c[3][0],
				c[0][1],c[1][1],c[2][1],c[3][1],
				c[0][2],c[1][2],c[2][2],c[3][2],
				c[0][3],c[1][3],c[2][3],c[3][3],
			});
		}	
		public location multiply(location b)
		{
			location p = new location(new double[] { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});
			for (int i = 0; i < 4; i++)
				for (int j = 0; j < 4; j++)
					for (int k = 0; k < 4; k++)
						p.a[i][j] += a[i][k] * b.a[k][j];
			return p;
		}
	}
}
