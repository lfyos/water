package convert_old_part_rude_2021_07_01;

import kernel_file_manager.file_reader;
import kernel_transformation.box;
import kernel_transformation.point;

public class face_edge 
{
	public void destroy()
	{
		clear();
		start_point=null;
		end_point=null;
		if(edge!=null) {
			edge.destroy();
			edge=null;
		}	
	}
	public void free_memory()
	{
		if(edge!=null){
			edge.destroy();
			edge=null;
		}
	}
	public face_edge(face_edge s)
	{
		start_extra_data				=s.start_extra_data;
		start_point_material			=new String[s.start_point_material.length];
		for(int i=0,ni=start_point_material.length;i<ni;i++)
			start_point_material[i]=s.start_point_material[i];

		end_extra_data					=s.end_extra_data;
		end_point_material				=new String[s.end_point_material.length];
		for(int i=0,ni=end_point_material.length;i<ni;i++)
			end_point_material[i]=s.end_point_material[i];
		
		parameter_material		=new String[s.parameter_material.length];;
		for(int i=0,ni=parameter_material.length;i<ni;i++)
			parameter_material[i]=s.parameter_material[i];
		
		start_point=null;
		if(s.start_point!=null)
			start_point=new point(s.start_point);
		
		end_point=null;
		if(s.end_point!=null)
			end_point=new point(s.end_point);
		
		start_effective_flag=s.start_effective_flag;
		end_effective_flag	=s.end_effective_flag;
		
		curve_type=s.curve_type;
		if(s.curve_parameter_number()<=0)
			curve_parameter=null;
		else{
			curve_parameter=new double[s.curve_parameter.length];
			for(int i=0,ni=curve_parameter.length;i<ni;i++)
				curve_parameter[i]=s.curve_parameter[i];
		}
		if((edge=s.edge)!=null)
			edge=s.edge.clone();
		if((edge_box=s.edge_box)!=null)
			edge_box=new box(s.edge_box);
	}

	public String start_extra_data,start_point_material[];
	public String end_extra_data,end_point_material[];
	public String parameter_material[];
	
	public point start_point,end_point;
	public boolean start_effective_flag,end_effective_flag;

	public String curve_type;
	public double []curve_parameter;
	public int curve_parameter_number()
	{
		if(curve_parameter==null)
			return 0;
		else 
			return curve_parameter.length;		
	}

	public edge_tessellation edge;
	public box edge_box;
	
	private void clear()
	{
		start_point_material=part_rude.default_material;
		start_extra_data="1";
		
		end_point_material=part_rude.default_material;
		end_extra_data="1";
		
		parameter_material=part_rude.default_material;
		
		start_point=new point();
		end_point=new point();
		start_effective_flag=false;
		end_effective_flag=false;

		curve_type="unknown";
		curve_parameter=null;
		
		edge=null;
		edge_box=null;
	}
	public face_edge(point my_start_point,point my_end_point)
	{
		clear();
		
		start_point=new point(my_start_point);
		end_point=new point(my_end_point);
		start_effective_flag=true;
		end_effective_flag=true;

		curve_type="unknown";
		curve_parameter=null;
		
		edge=new edge_tessellation_default(this,start_point,end_point);
	}
	
	public face_edge(file_reader fr,double scale_value)
	{
		clear();
		
		curve_type=fr.get_string();
		curve_type=(curve_type==null)?"":curve_type;
	
		int my_curve_parameter_number=fr.get_int();
		
		if(my_curve_parameter_number<=0)
			my_curve_parameter_number=0;
		else{
			curve_parameter=new double[my_curve_parameter_number];
			for(int i=0;i<my_curve_parameter_number;i++)
				curve_parameter[i]=fr.get_double();
		}
		
		parameter_material=new String[4];
		
		for(int i=0,ni=parameter_material.length;i<ni;i++)
			parameter_material[i]=fr.get_string();
		
		String str=fr.get_string();
		str=(str==null)?"":str;
		start_effective_flag=(str.compareTo("start_effective")==0)?true:false;
		if(start_effective_flag){
			start_point=(new point(fr)).scale(scale_value);
			start_extra_data=fr.get_string();
		
			start_point_material=new String[4];
			for(int i=0,ni=start_point_material.length;i<ni;i++)
				start_point_material[i]=fr.get_string();
		}else{
			start_point=new point();
			start_extra_data="1";
			start_point_material=part_rude.default_material;
		}
		
		str=fr.get_string();
		str=(str==null)?"":str;
		end_effective_flag=(str.compareTo("end_effective")==0)?true:false;
		
		if(end_effective_flag){
			end_point=(new point(fr)).scale(scale_value);
			end_extra_data=fr.get_string();
		
			end_point_material=new String[4];
			for(int i=0,ni=end_point_material.length;i<ni;i++)
				end_point_material[i]=fr.get_string();
		}else{
			end_point=new point();
			end_extra_data="1";
			end_point_material=part_rude.default_material;
		}

		str=((str=fr.get_string())==null)?"nobox":(str.toLowerCase());

		switch(str){
		case "nobox":
			edge=null;
			edge_box=null;
			break;
		case "box":
			edge=null;
			edge_box=new box(fr);
			break;
		case "external":
			edge=new edge_tesslation_external_point(fr,this);
			break;
		default:
			int my_tessellation_point_number;
			if((my_tessellation_point_number=Integer.decode(str))>0) {
				edge=new edge_tessellation_default(fr,this,
					my_tessellation_point_number,scale_value);
			}else {
				edge=null;
				edge_box=null;
			}
			break;
		}
	}
}