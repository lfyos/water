package driver_movement;

import kernel_component.component;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_transformation.location;
import kernel_transformation.point;

public class movement_design_parameter {
	public String parameter[];
	public location design_location;
	public int design_component_id;
	
	public double time_length;
	public boolean start_show_flag,terminate_show_flag;
	
	public String node_name,sound_file_name,move_description;
	
	private double get_double(client_information ci,String str,double default_value)
	{	
		double ret_val=default_value;
		try{
			if((str=ci.request_response.get_parameter(str))!=null)
				ret_val=Double.parseDouble(str);
		}catch(Exception e){
			;
		}
		return ret_val;
	}
	
	private boolean get_boolean(client_information ci,String str,boolean default_value)
	{	
		try{
			if((str=ci.request_response.get_parameter(str))==null)
				return default_value;
			return (str.compareTo("true")==0)?true:false;
		}catch(Exception e){
			return default_value;
		}
	}
	private String get_string(client_information ci,
			String str,String default_value,String uri_encode_charset)
	{
		if((str=ci.request_response.get_parameter(str))!=null){
			try{
				str=java.net.URLDecoder.decode(str,uri_encode_charset);
				str=java.net.URLDecoder.decode(str,uri_encode_charset);
			}catch(Exception e){
				return default_value;
			}
		}
		if(str==null)
			return default_value;
		if(str.compareTo("")==0)
			return default_value;
		return str;
	}
	private static location move_rotate(
			double mx,double my,double mz,double rx,double ry,double rz,
			location comp_absolute_loca,location comp_move_loca,location rotate_comp_loca)
	{
		location comp_loca=comp_absolute_loca.multiply(comp_move_loca.negative());
		location loca=rotate_comp_loca.negative().multiply(comp_loca);
		loca=location.move_rotate(mx,my,mz,rx,ry,rz).multiply(loca);
		loca=rotate_comp_loca.multiply(loca);
		loca=new location(
				loca.multiply(new point(0,0,0)),
				loca.multiply(new point(1,0,0)),
				loca.multiply(new point(0,1,0)),
				loca.multiply(new point(0,0,1)));
		loca=loca.multiply(location.standard_negative);
		
		return comp_loca.negative().multiply(loca);
	}
	private static location move_rotate(
			double mx,double my,double mz,double rx,double ry,double rz,
			location comp_absolute_loca,location comp_move_loca,point rotate_center)
	{
		location loca=new location(rotate_center,	rotate_center.add(new point(1,0,0)),
				rotate_center.add(new point(0,1,0)),rotate_center.add(new point(0,0,1)));
		loca=loca.multiply(location.standard_negative);
		return move_rotate(mx,my,mz,rx,ry,rz,comp_absolute_loca,comp_move_loca,loca);
	}
	public movement_design_parameter(engine_kernel ek,client_information ci)
	{
		String str;
		component comp=null;
		do{
			if((str=ci.request_response.get_parameter("component_name"))==null)
				break;
			if(str.length()<=0)
				break;
			try {
				str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
				str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
			}catch(Exception e) {
				break;
			}
			comp=ek.component_cont.search_component(str);
		}while(false);
		
		if(comp==null)
			do{
				if((str=ci.request_response.get_parameter("component_id"))==null)
					break;
				if(str.length()<=0)
					break;
				int component_id;
				try {
					component_id=Integer.decode(str);
				}catch(Exception e) {
					break;
				}
				comp=ek.component_cont.get_component(component_id);
			}while(false);
		
		if(comp==null)
			comp=ek.component_cont.search_component();
		
		if((ek.component_cont.root_component.component_id==comp.component_id))
			design_component_id=-1;
		else
			design_component_id=comp.component_id;
		
		parameter=null;
		for(int i=0;;i++){
			String parameter_i=get_string(
					ci,"parameter_"+Integer.toString(i),
					null,ek.system_par.network_data_charset);
			if(parameter_i==null)
				break;
			if(parameter_i.length()>0){
				if(parameter==null){
					parameter=new String[1];
					parameter[0]=parameter_i;
				}else{
					String bak[]=parameter;
					parameter=new String[bak.length+1];
					for(int j=0;j<bak.length;j++)
						parameter[j]=bak[j];
					parameter[bak.length]=parameter_i;
				}
			}
		}
		do{
			if(design_component_id>=0)
				if((str=ci.request_response.get_parameter("coordinate"))!=null){
					if(str.compareTo("view")==0) {
						design_location=move_rotate(
							get_double(ci,"mx",0),get_double(ci,"my",0),get_double(ci,"mz",0),
							get_double(ci,"rx",0),get_double(ci,"ry",0),get_double(ci,"rz",0),
							comp.absolute_location,comp.move_location,
							ci.display_camera_result.cam.eye_component.absolute_location);
						break;
					}else if(str.compareTo("camera")==0) {
						design_location=move_rotate(
							get_double(ci,"mx",0),get_double(ci,"my",0),get_double(ci,"mz",0),
							get_double(ci,"rx",0),get_double(ci,"ry",0),get_double(ci,"rz",0),
							comp.absolute_location,comp.move_location,
							ci.display_camera_result.cam.eye_component.
								absolute_location.multiply(new point(0,0,0)));
						break;
					}
				}
			design_location=location.move_rotate(
				get_double(ci,"mx",0),get_double(ci,"my",0),get_double(ci,"mz",0),
				get_double(ci,"rx",0),get_double(ci,"ry",0),get_double(ci,"rz",0));
		}while(false);
		
		time_length=get_double(ci,"t",1000);
		
		start_show_flag		=get_boolean(ci,"start",	false);
		terminate_show_flag	=get_boolean(ci,"terminate",true);
		
		node_name		=get_string(ci,"node_name",	"no_node_name",		ek.system_par.network_data_charset);
		sound_file_name	=get_string(ci,"sound_file","no_sound_file",	ek.system_par.network_data_charset);
		move_description=get_string(ci,"move_desc",	"no_description",	ek.system_par.network_data_charset);
	}
}
