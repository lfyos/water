package kernel_mesh;

import kernel_transformation.box;
import kernel_file_manager.file_reader;

public class face_mesh_external_triangle implements face_mesh
{
	private int primitive_number,attribute_number;
	private double vertex_scale_value,normal_scale_value;
	
	private int current_primitive_id;
	private String material[];
	private double vx[],vy[],vz[],	nx[],ny[],nz[],	ax[][],ay[][],az[][];
	private String vxw, vyw, vzw,	nxw, nyw, nzw,	axw[], ayw[], azw[];
	
	public int get_vertex_number(auxiliary_file_handler f)
	{
		return 3*primitive_number;
	};
	public double[] get_vertex(int index_id,auxiliary_file_handler f)
	{
		switch(index_id%3) {
		default:
		case 0:
			return vx;
		case 1:
			return vy;
		case 2:
			return vz;
		}
	};
	public String get_vertex_extra_data(int index_id,auxiliary_file_handler f)
	{
		switch(index_id%3) {
		default:
		case 0:
			return vxw;
		case 1:
			return vyw;
		case 2:
			return vzw;
		}
	};
	public int get_normal_number(auxiliary_file_handler f)
	{
		return 3*primitive_number;
	};
	public double[] get_normal(int index_id,auxiliary_file_handler f)
	{
		switch(index_id%3) {
		default:
		case 0:
			return nx;
		case 1:
			return ny;
		case 2:
			return nz;
		}
	};
	public String get_normal_extra_data(int index_id,auxiliary_file_handler f)
	{
		switch(index_id%3) {
		default:
		case 0:
			return nxw;
		case 1:
			return nyw;
		case 2:
			return nzw;
		}
	};
	public int get_attribute_number(int attribute_id,auxiliary_file_handler f)
	{
		return 3*primitive_number;
	};
	public double[] get_attribute(int index_id,int attribute_id,auxiliary_file_handler f)
	{
		switch(index_id%3) {
		default:
		case 0:
			return ax[attribute_id];
		case 1:
			return ay[attribute_id];
		case 2:
			return az[attribute_id];
		}
	};
	public String get_attribute_extra_data(int index_id,int attribute_id,auxiliary_file_handler f)
	{
		switch(index_id%3) {
		default:
		case 0:
			return axw[attribute_id];
		case 1:
			return ayw[attribute_id];
		case 2:
			return azw[attribute_id];
		}
	};
	public int get_attribute_number()
	{
		return attribute_number;
	};
	public int get_primitive_number()
	{
		return primitive_number;
	};
	public int get_primitive_vertex_number(int primitive_id,auxiliary_file_handler f)
	{
		skip(primitive_id,f);
		return 3;
	};
	public String[] get_primitive_material(int primitive_id,auxiliary_file_handler f) 
	{
		return material;
	};
	public int get_primitive_vertex_index(int primitive_id,int index_id,auxiliary_file_handler f)
	{
		return 3*primitive_id+index_id;
	};
	public int get_primitive_normal_index(int primitive_id,int index_id,auxiliary_file_handler f)
	{
		return 3*primitive_id+index_id;
	};
	public int get_primitive_attribute_index(int primitive_id,int index_id,int attribute_id,auxiliary_file_handler f)
	{
		return 3*primitive_id+index_id;
	};
	public int add_attribute(double x,double y,double z,String w)
	{
		return 0;
	};
	public face_mesh clone() 
	{
		return this;
	};
	public void destroy()
	{
		material=null;
		vx=null;
		vy=null;
		vz=null;
		nx=null;
		ny=null;
		nz=null;
		ax=null;
		ay=null;
		az=null;
		vxw=null;
		vyw=null;
		vzw=null;
		nxw=null;
		nyw=null;
		nzw=null;
		axw=null;
		ayw=null;
		azw=null;

	};
	private void skip(int primitive_id,auxiliary_file_handler f)
	{
		for(;current_primitive_id<=primitive_id;current_primitive_id++) {
			material[0]=f.face_file.get_string();
			material[1]=f.face_file.get_string();
			material[2]=f.face_file.get_string();
			material[3]=f.face_file.get_string();
			
			vx[0]=f.face_file.get_double()*vertex_scale_value;
			vx[1]=f.face_file.get_double()*vertex_scale_value;
			vx[2]=f.face_file.get_double()*vertex_scale_value;
			vxw	 =f.face_file.get_string();
			
			nx[0]=f.face_file.get_double()*normal_scale_value;
			nx[1]=f.face_file.get_double()*normal_scale_value;
			nx[2]=f.face_file.get_double()*normal_scale_value;
			nxw	 =f.face_file.get_string();
			
			for(int i=0;i<attribute_number;i++) {
				ax[i][0]=f.face_file.get_double();
				ax[i][1]=f.face_file.get_double();
				ax[i][2]=f.face_file.get_double();
				axw[i]	=f.face_file.get_string();
			}

			vy[0]=f.face_file.get_double()*vertex_scale_value;
			vy[1]=f.face_file.get_double()*vertex_scale_value;
			vy[2]=f.face_file.get_double()*vertex_scale_value;
			vyw	 =f.face_file.get_string();
			
			ny[0]=f.face_file.get_double()*normal_scale_value;
			ny[1]=f.face_file.get_double()*normal_scale_value;
			ny[2]=f.face_file.get_double()*normal_scale_value;
			nyw	 =f.face_file.get_string();
			
			for(int i=0;i<attribute_number;i++) {
				ay[i][0]=f.face_file.get_double();
				ay[i][1]=f.face_file.get_double();
				ay[i][2]=f.face_file.get_double();
				ayw[i]	=f.face_file.get_string();
			}

			vz[0]=f.face_file.get_double()*vertex_scale_value;
			vz[1]=f.face_file.get_double()*vertex_scale_value;
			vz[2]=f.face_file.get_double()*vertex_scale_value;
			vzw	 =f.face_file.get_string();
			
			nz[0]=f.face_file.get_double()*normal_scale_value;
			nz[1]=f.face_file.get_double()*normal_scale_value;
			nz[2]=f.face_file.get_double()*normal_scale_value;
			nzw	 =f.face_file.get_string();
			
			for(int i=0;i<attribute_number;i++) {
				az[i][0]=f.face_file.get_double();
				az[i][1]=f.face_file.get_double();
				az[i][2]=f.face_file.get_double();
				azw[i]	=f.face_file.get_string();
			}
		}
	}
	
	private box face_face_box;
	private String box_material[];
	public box get_face_box()
	{
		return face_face_box;
	}
	public String[] get_box_material()
	{
		return box_material;
	}
	public face_mesh_external_triangle(file_reader fr,
			double my_vertex_scale_value,double my_normal_scale_value)
	{
		vertex_scale_value=my_vertex_scale_value;
		normal_scale_value=my_normal_scale_value;
		
		attribute_number=fr.get_int();
		primitive_number=fr.get_int();
		face_face_box	=new box(fr);
		box_material	=new String[]
			{fr.get_string(),fr.get_string(),fr.get_string(),fr.get_string()};

		current_primitive_id=0;
		
		vx=new double[] {0,0,0};			vxw="1";
		vy=new double[] {0,0,0};			vyw="1";
		vz=new double[] {0,0,0};			vzw="1";

		nx=new double[] {0,1,0};			nxw="1";
		ny=new double[] {0,1,0};			nyw="1";
		nz=new double[] {0,1,0};			nzw="1";

		ax=new double[attribute_number][];	axw=new String[attribute_number];
		ay=new double[attribute_number][];	ayw=new String[attribute_number];
		az=new double[attribute_number][];	azw=new String[attribute_number];
		
		for(int i=0;i<attribute_number;i++) {
			ax[i]=new double[] {0,0,0};		axw[i]="1";
			ay[i]=new double[] {0,0,0};		ayw[i]="1";
			az[i]=new double[] {0,0,0};		azw[i]="1";
		}
		material=new String[] {"0","0","0","1"};
	}
}
