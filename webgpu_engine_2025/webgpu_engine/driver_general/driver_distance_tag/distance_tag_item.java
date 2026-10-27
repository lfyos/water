package driver_distance_tag;

import kernel_scene.scene_kernel;
import kernel_component.component;
import kernel_transformation.point;
import kernel_transformation.plane;
import kernel_transformation.location;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_scene.client_information;
import kernel_common_class.const_value;
import kernel_common_class.jason_string;
import kernel_common_class.format_change;

public class distance_tag_item
{	
	public int p0_component_id,px_component_id,coordinate_component_id;
	public point p0,px,py;
	public long location_version_p0,location_version_px,location_version_tag;
	public String state,type_id,tag_title;
	
	public distance_tag_item extra_distance_tag;
	
	public void destroy()
	{
		if(extra_distance_tag!=null){
			extra_distance_tag.destroy();
			extra_distance_tag=null;
		}
		p0=null;
		px=null;
		py=null;
		state=null;
		type_id=null;
		tag_title=null;
	}
	
	public distance_tag_item(distance_tag_item dti)
	{
		state					=dti.state;
		p0_component_id			=dti.p0_component_id;
		px_component_id			=dti.px_component_id;
		coordinate_component_id	=dti.coordinate_component_id;
		type_id					=dti.type_id;
		p0						=dti.p0;
		px						=dti.px;
		py						=dti.py;
		location_version_p0		=0;
		location_version_px		=0;
		location_version_tag	=0;
		tag_title				=dti.tag_title;
		
		extra_distance_tag		=null;
	}
	private distance_tag_item(
			int my_p0_component_id,int my_px_component_id,int my_coordinate_component_id,
			double p0_x,double p0_y,double p0_z,	double px_x,double px_y,double px_z,
			double py_x,double py_y,double py_z,	String my_type_id,String my_tag_title)
	{
		state="end";
		
		p0_component_id			=my_p0_component_id;
		px_component_id			=my_px_component_id;
		coordinate_component_id	=my_coordinate_component_id;
		type_id					=(new distance_tag_parameter(my_type_id)).type_id;
		p0						=new point(p0_x,p0_y,p0_z);
		px						=new point(px_x,px_y,px_z);
		py						=new point(py_x,py_y,py_z);
		location_version_p0		=0;
		location_version_px		=0;
		location_version_tag	=0;
		
		tag_title				=(my_tag_title==null)?"":(my_tag_title.trim());
		
		extra_distance_tag=null;
	}
	public static distance_tag_item load(file_reader fr,scene_kernel sk)
	{
		String component_name_p0 		=fr.get_string();
		String component_name_px 		=fr.get_string(); 
		String component_name_coordinate=fr.get_string();
		
		if(fr.eof())
			return null;

		String type_id=fr.get_string();
		
		point p0=new point(fr);
		point px=new point(fr);
		point py=new point(fr);
		
		String tag_title;
		if((tag_title=fr.get_string())!=null)
			if(tag_title.compareTo("null")==0)
				tag_title=null;
		
		component comp_p0,comp_px,comp_coordinate;
		if((comp_p0=sk.component_cont.search_component(component_name_p0))==null)
			return null;
		if((comp_px=sk.component_cont.search_component(component_name_px))==null)
			return null;
		if((comp_coordinate=sk.component_cont.search_component(component_name_coordinate))==null)
			return null;
		
		return new distance_tag_item(
			comp_p0.component_id,	comp_px.component_id,	comp_coordinate.component_id,
			p0.x,p0.y,p0.z,			px.x,px.y,px.z,			py.x,py.y,py.z,
			type_id,				tag_title);
	}
	public distance_tag_item(point my_point,int my_component_id,int my_coordinate_component_id)
	{
		state					="begin";
		
		p0_component_id			=my_component_id;
		px_component_id			=my_component_id;
		coordinate_component_id	=my_coordinate_component_id;
		p0						=new point(my_point);
		px						=new point(my_point);
		py						=new point(my_point);
		type_id					=(new distance_tag_parameter(null)).type_id;
		location_version_p0		=0;
		location_version_px		=0;
		location_version_tag	=0;
		tag_title="";
		
		extra_distance_tag		=null;
	}	
	public boolean write_out(file_writer fw,scene_kernel sk)
	{
		if(state.compareTo("end")!=0)
			return false;
		component comp_p0,comp_px,comp_coordinate;
		if((comp_p0=sk.component_cont.get_component(p0_component_id))==null)
			return false;
		if((comp_px=sk.component_cont.get_component(px_component_id))==null)
			return false;
		if((comp_coordinate=sk.component_cont.get_component(coordinate_component_id))==null)
			return false;
		
		fw.println("/*	p0_component	*/	",comp_p0. component_name);
		fw.println("/*	px_component	*/	",comp_px. component_name);
		fw.println("/*	tag_component	*/	",comp_coordinate.component_name);
		fw.println("/*	type_id			*/	",type_id);
		fw.print  ("/*	p0				*/	",p0.x).print("	",p0.y).println("	",p0.z);
		fw.print  ("/*	px				*/	",px.x).print("	",px.y).println("	",px.z);
		fw.print  ("/*	py				*/	",py.x).print("	",py.y).println("	",py.z);
		fw.println("/*	tag_title		*/	",(tag_title.isEmpty())?"null":tag_title);
		fw.println();
		
		return true;
	}
	public boolean response_jason(int tag_id,scene_kernel sk,client_information ci,String follow_str)
	{
		component comp_p0,comp_px,comp_coordinate;
		if(state.compareTo("end")!=0)
			return false;
		if((comp_p0=sk.component_cont.get_component(p0_component_id))==null)
			return false;
		if((comp_px=sk.component_cont.get_component(px_component_id))==null)
			return false;
		if((comp_coordinate=sk.component_cont.get_component(coordinate_component_id))==null)
			return false;
		
		ci.request_response.println(follow_str);
		ci.request_response.println("	{");
		ci.request_response.print  ("		\"tag_id\":	",			tag_id).println(",");
		ci.request_response.print  ("		\"p0_component\":	",	jason_string.change_string(comp_p0. component_name)).println(",");
		ci.request_response.print  ("		\"px_component\":	",	jason_string.change_string(comp_px. component_name)).println(",");
		ci.request_response.print  ("		\"tag_component\":",	jason_string.change_string(comp_coordinate.component_name)).println(",");
		ci.request_response.print  ("		\"type_id\":	",		jason_string.change_string(type_id)).println(",");
		ci.request_response.print  ("		\"p0\":		[",			p0.x).print(",	",p0.y).print(",	",p0.z).println(",	1.0],");
		ci.request_response.print  ("		\"px\":		[",			px.x).print(",	",px.y).print(",	",px.z).println(",	1.0],");
		ci.request_response.print  ("		\"py\":		[",			py.x).print(",	",py.y).print(",	",py.z).println(",	1.0],");
		ci.request_response.println("		\"tag_string\":	",		jason_string.change_string(tag_title));
		ci.request_response.print  ("	}");
		
		return true;
	}
	public void set_distance_tag_type(
			String new_type_id,distance_tag_item my_ex_distance_tag,
			scene_kernel sk,client_information ci)
	{
		var tag_parameter=new distance_tag_parameter(new_type_id);
		type_id=tag_parameter.type_id;
		
		switch(tag_parameter.extra_distance_tag){
		case "extra_distance_tag":
			extra_distance_tag=(my_ex_distance_tag==null)
				?null:new distance_tag_item(my_ex_distance_tag);
			break;
		case "no_extra_distance_tag":
		default:
			extra_distance_tag=null;
			break;
		}
		
		component comp;
		int old_coordinate_component_id=coordinate_component_id;
		switch(tag_parameter.tag_component) {
		case "root_component":
			coordinate_component_id=sk.component_cont.root_component.component_id;
			break;
		case "search_component":
			if((comp=sk.component_cont.latest_selected_component())!=null)
				coordinate_component_id=comp.component_id;
			break;
		case "view_component":
			coordinate_component_id=ci.display_camera_result.cam.eye_component.component_id;
			break;
		}
		if(old_coordinate_component_id!=coordinate_component_id)
			location_version_tag=0;
		return;
	}
	public String get_tag_string(int display_precision,scene_kernel sk,client_information ci)
	{
		component comp;
		comp=sk.component_cont.get_component(p0_component_id);
		point global_p0=comp.absolute_location.multiply(p0);
		comp=sk.component_cont.get_component(px_component_id);
		point global_px=comp.absolute_location.multiply(px);

		point extra_global_p0,extra_global_px;
		if(extra_distance_tag==null){
			extra_global_p0=new point(0,0,0);
			extra_global_px=new point(0,0,1);
		}else{
			comp=sk.component_cont.get_component(extra_distance_tag.p0_component_id);
			extra_global_p0=comp.absolute_location.multiply(extra_distance_tag.p0);
			comp=sk.component_cont.get_component(extra_distance_tag.px_component_id);
			extra_global_px=comp.absolute_location.multiply(extra_distance_tag.px);
		}

		var tag_parameter=new distance_tag_parameter(type_id);
		String my_tag_title=tag_title+tag_parameter.title;

		double value=0;
		point dir_0,dir_1;
		
		switch(tag_parameter.type_id){
		case "absolute_distance":
			value=global_px.sub(global_p0).distance();
			break;
		case "projection_distance":
			dir_0=global_px.sub(global_p0);
			dir_1=extra_global_px.sub(extra_global_p0);
			value=Math.abs(dir_0.dot(dir_1.expand(1)));
			break;
		case "projection_angle":
			dir_0=global_px.sub(global_p0);
			dir_1=extra_global_px.sub(extra_global_p0);
			value=180.0*Math.acos(dir_0.expand(1).dot(dir_1.expand(1)))/Math.PI;
			break;
		default:
			if((comp=sk.component_cont.get_component(coordinate_component_id))==null)
				return jason_string.change_string("tag_component error:"+coordinate_component_id);
			
			point coordinate_p0=comp.absolute_location.multiply(0,0,0);
			point coordinate_p1=comp.absolute_location.multiply(tag_parameter.direction);
			plane p_l=new plane(coordinate_p0,coordinate_p1);
			location loca=p_l.project_to_plane_location();
			
			switch(tag_parameter.caculate_method){
			case "plane_direction_distance":
				value=Math.abs(p_l.test(global_px)-p_l.test(global_p0));
				break;
			case "in_plane_distance":
				value=loca.multiply(global_px).sub(loca.multiply(global_p0)).distance();
				break;
			case "global_angle":
				if((dir_0=global_px.sub(global_p0)).distance2()<const_value.min_value2)
					return jason_string.change_string(my_tag_title+" global_p0 too near to global_p1");
				if((dir_1=coordinate_p1.sub(coordinate_p0)).distance2()<const_value.min_value2)
					return jason_string.change_string(my_tag_title+" p0 too near to p1");
				value=180.0*Math.acos(dir_0.expand(1.0).dot(dir_1.expand(1.0)))/Math.PI;
				break;
			case "plane_angle":
				if((dir_0=loca.multiply(global_px).sub(loca.multiply(global_p0))).distance2()<const_value.min_value2)
					return jason_string.change_string(my_tag_title+" global_p0 too near to global_p1");
				if((dir_1=loca.multiply(extra_global_px).sub(loca.multiply(extra_global_p0))).distance2()<const_value.min_value2)
					return jason_string.change_string(my_tag_title+" p0 too near to p1");
				value=180.0*Math.acos(dir_0.expand(1).dot(dir_1.expand(1)))/Math.PI;
				break;
			}
		}
		my_tag_title+=format_change.double_to_decimal_string(value,display_precision);
		return jason_string.change_string(my_tag_title);
	}
}
