package kernel_part;

import kernel_file_manager.file_reader;
import kernel_transformation.box;
import kernel_transformation.point;

public class face_edge
{
	public void destroy()
	{
		curve_type			=null;
		curve_parameter		=null;
		
		start_point			=null;
		end_point			=null;
		
		edge_box			=null;
		
		start_extra_data	=null;
		start_point_material=null;
		end_extra_data		=null;
		end_point_material	=null;
		
		parameter_material=null;
	}
	public int curve_parameter_number()
	{
		return (curve_parameter==null)?0:curve_parameter.length;		
	}
	
	public String curve_type;
	public double []curve_parameter;

	public point start_point;
	public String start_extra_data,	start_point_material[];
	
	public point end_point;
	public String end_extra_data,	end_point_material[];
	
	public String parameter_extra_data,parameter_material[];
	
	public box edge_box;
	public int total_edge_primitive_number,total_point_primitive_number;
	
	public face_edge(face_edge s)
	{
		curve_type				=s.curve_type;
		curve_parameter			=s.curve_parameter;
		
		start_point				=s.start_point;
		start_extra_data		=s.start_extra_data;
		start_point_material	=s.start_point_material;
		
		end_point				=s.end_point;
		end_extra_data			=s.end_extra_data;
		end_point_material		=s.end_point_material;
		
		parameter_extra_data	=s.parameter_extra_data;
		parameter_material		=s.parameter_material;
		
		edge_box				=s.edge_box;
		
		total_edge_primitive_number	=s.total_edge_primitive_number;
		total_point_primitive_number=s.total_point_primitive_number;
	}
	public face_edge(point my_start_point,point my_end_point,
			String my_extra_data,String my_material[])
	{
		curve_type="unknown";
		curve_parameter=null;
		
		start_point	=(my_start_point==null)?null:my_start_point;
		start_point_material=my_material;
		start_extra_data=my_extra_data;
		
		end_point	=(my_end_point	==null)?null:my_end_point;
		end_point_material=my_material;
		end_extra_data=my_extra_data;
		
		parameter_extra_data=my_extra_data;
		parameter_material=my_material;
		
		edge_box=(start_point==null)?null:new box(start_point);
		if(end_point!=null){
			if(edge_box==null)
				edge_box=new box(end_point);
			else
				edge_box=edge_box.add(new box(end_point));
		}
		total_edge_primitive_number	=2;
		total_point_primitive_number=2;
	}
	public face_edge(file_reader fr)
	{
		curve_type=fr.get_string();
		curve_type=(curve_type==null)?"":curve_type;
		
		int my_curve_parameter_number;
		if((my_curve_parameter_number=fr.get_int())<=0) 
			curve_parameter=null;
		else{
			curve_parameter=new double[my_curve_parameter_number];
			for(int i=0;i<my_curve_parameter_number;i++)
				curve_parameter[i]=fr.get_double();
		}
		
		String str=((str=fr.get_string())==null)?"":str;
		if((str.toLowerCase().compareTo("start_effective")==0)){
			start_point=new point(fr);
			if((start_extra_data=fr.get_string())==null)
				start_extra_data="1";
		
			start_point_material=new String[4];
			for(int i=0,ni=start_point_material.length;i<ni;i++)
				if((start_point_material[i]=fr.get_string())==null)
					start_point_material[i]="0";
		}else{
			start_point=null;
			start_extra_data=null;
			start_point_material=null;
		}
		
		str=((str=fr.get_string())==null)?"":str;
		if((str.toLowerCase().compareTo("end_effective")==0)){
			end_point=new point(fr);
			if((end_extra_data=fr.get_string())==null)
				end_extra_data="1";
			
			end_point_material=new String[4];
			for(int i=0,ni=end_point_material.length;i<ni;i++)
				if((end_point_material[i]=fr.get_string())==null)
					end_point_material[i]="0";
		}else{
			end_point=null;
			end_extra_data=null;
			end_point_material=null;
		}
		
		if((parameter_extra_data=fr.get_string())==null)
			parameter_extra_data="1";
		parameter_material=new String[4];
		for(int i=0,ni=parameter_material.length;i<ni;i++)
			if((parameter_material[i]=fr.get_string())==null)
				parameter_material[i]="1";

		fr.mark_start();
		if((str=fr.get_string())==null)
			str="nobox";
		if(str.toLowerCase().compareTo("nobox")==0) {
			fr.mark_terminate(false);
			edge_box=null;
		}else{
			fr.mark_terminate(true);
			edge_box=new box(fr);
		}
		total_edge_primitive_number	=fr.get_int();
		total_point_primitive_number=fr.get_int();
		
		return;
	}
}