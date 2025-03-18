package driver_distance_tag;

import kernel_common_class.const_value;
import kernel_common_class.format_change;
import kernel_common_class.jason_string;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;
import kernel_transformation.point;
import kernel_transformation.plane;
import kernel_transformation.location;
import kernel_component.component;

public class distance_tag_item
{
	public int state,p0_component_id,px_component_id,tag_component_id,type_id;
	public point p0,px,py;
	public long location_version_p0,location_version_px,location_version_tag;
	public String tag_title;
	
	public distance_tag_item extra_distance_tag;
	
	public void destroy()
	{
		if(extra_distance_tag!=null) {
			extra_distance_tag.destroy();
			extra_distance_tag=null;
		}
		p0=null;
		px=null;
		py=null;
		tag_title=null;
	}
	
	public distance_tag_item(distance_tag_item dti)
	{
		state				=dti.state;
		p0_component_id		=dti.p0_component_id;
		px_component_id		=dti.px_component_id;
		tag_component_id	=dti.tag_component_id;
		type_id				=dti.type_id;
		p0					=dti.p0;
		px					=dti.px;
		py					=dti.py;
		location_version_p0	=0;
		location_version_px	=0;
		location_version_tag=0;
		tag_title			=dti.tag_title;
		
		extra_distance_tag=null;
	}
	private distance_tag_item(
			int my_p0_component_id,int my_px_component_id,int my_tag_component_id,
			int my_type_id,
			double p0_x,double p0_y,double p0_z,
			double px_x,double px_y,double px_z,
			double py_x,double py_y,double py_z,
			String my_tag_title)
	{
		state=2;
		p0_component_id=my_p0_component_id;
		px_component_id=my_px_component_id;
		tag_component_id=my_tag_component_id;
		type_id=my_type_id;
		p0=new point(p0_x,p0_y,p0_z);
		px=new point(px_x,px_y,px_z);
		py=new point(py_x,py_y,py_z);
		location_version_p0=0;
		location_version_px=0;
		location_version_tag=0;
		tag_title=(my_tag_title==null)?"":(my_tag_title.trim());
		
		extra_distance_tag=null;
	}
	public static distance_tag_item load(file_reader fr,scene_kernel sk)
	{
		String component_name_p0 =fr.get_string();
		String component_name_px =fr.get_string(); 
		String component_name_tag=fr.get_string();
		
		if(fr.eof())
			return null;

		int type_id=fr.get_int();
		
		point p0=new point(fr),px=new point(fr),py=new point(fr);
		String tag_title=fr.get_string();
		if(tag_title!=null)
			if(tag_title.compareTo("null")==0)
				tag_title=null;
		
		component comp_p0,comp_px,comp_tag;
		if((comp_p0=sk.component_cont.search_component(component_name_p0))==null)
			return null;
		if((comp_px=sk.component_cont.search_component(component_name_px))==null)
			return null;
		if((comp_tag=sk.component_cont.search_component(component_name_tag))==null)
			return null;
		return new distance_tag_item(
			comp_p0.component_id,comp_px.component_id,comp_tag.component_id,
			type_id,	p0.x,p0.y,p0.z,		px.x,px.y,px.z,		py.x,py.y,py.z,
			tag_title);
	}
	public distance_tag_item(point my_point,int my_component_id,int my_tag_component_id)
	{
		state=0;
		p0_component_id=my_component_id;
		px_component_id=my_component_id;
		tag_component_id=my_tag_component_id;
		p0=new point(my_point);
		px=new point(my_point);
		py=new point(my_point);
		type_id=-1;
		location_version_p0=0;
		location_version_px=0;
		location_version_tag=0;
		tag_title="";
		
		extra_distance_tag=null;
	}
	public boolean write_out(file_writer fw,scene_kernel sk)
	{
		if(state!=2)
			return false;
		component comp_p0,comp_px,comp_tag;
		if((comp_p0=sk.component_cont.get_component(p0_component_id))==null)
			return false;
		if((comp_px=sk.component_cont.get_component(px_component_id))==null)
			return false;
		if((comp_tag=sk.component_cont.get_component(tag_component_id))==null)
			return false;
		
		fw.println("/*	p0_component	*/	",comp_p0. component_name);
		fw.println("/*	px_component	*/	",comp_px. component_name);
		fw.println("/*	tag_component	*/	",comp_tag.component_name);
		fw.println("/*	type_id			*/	",type_id);
		fw.print  ("/*	p0				*/	",p0.x).print("	",p0.y).println("	",p0.z);
		fw.print  ("/*	px				*/	",px.x).print("	",px.y).println("	",px.z);
		fw.print  ("/*	py				*/	",py.x).print("	",py.y).println("	",py.z);
		fw.println("/*	tag_title		*/	",(tag_title.length()<=0)?"null":tag_title);
		fw.println();
		
		return true;
	}
	public boolean response_jason(int tag_id,scene_kernel sk,client_information ci,String follow_str)
	{
		component comp_p0,comp_px,comp_tag;
		if(state!=2)
			return false;
		if((comp_p0=sk.component_cont.get_component(p0_component_id))==null)
			return false;
		if((comp_px=sk.component_cont.get_component(px_component_id))==null)
			return false;
		if((comp_tag=sk.component_cont.get_component(tag_component_id))==null)
			return false;
		
		ci.request_response.println(follow_str);
		ci.request_response.println("	{");
		ci.request_response.print  ("		\"tag_id\":	",			tag_id).println(",");
		ci.request_response.print  ("		\"p0_component\":	",	jason_string.change_string(comp_p0. component_name)).println(",");
		ci.request_response.print  ("		\"px_component\":	",	jason_string.change_string(comp_px. component_name)).println(",");
		ci.request_response.print  ("		\"tag_component\":",	jason_string.change_string(comp_tag.component_name)).println(",");
		ci.request_response.print  ("		\"type_id\":	",		type_id).println(",");
		ci.request_response.print  ("		\"p0\":		[",			p0.x).print(",	",p0.y).print(",	",p0.z).println(",	1.0],");
		ci.request_response.print  ("		\"px\":		[",			px.x).print(",	",px.y).print(",	",px.z).println(",	1.0],");
		ci.request_response.print  ("		\"py\":		[",			py.x).print(",	",py.y).print(",	",py.z).println(",	1.0],");
		ci.request_response.println("		\"tag_string\":	",		jason_string.change_string(tag_title));
		ci.request_response.print  ("	}");
		
		return true;
	}
	
	
	//<0	global distance
	
	//0:global X direction		1:global Y direction	 	2:global Z direction
	//3:local  X direction		4:local  Y direction		5:local  Z direction
	//6:view   X direction		7:view   Y direction		8:view   Z direction
	
	//9:global YZ plane			10:global ZX plane		 	11:global XY plane
	//12:local YZ plane			13:local  ZX plane			14:local  XY plane
	//15:view  YZ plane			16:view   ZX plane			17:view   XY plane
	
	//18:global X angle			19:global Y angle		 	20:global Z angle	
	//21:local  X angle			22:local  Y angle			23:local  Z angle	
	//24:view   X angle			25:view   Y angle			26:view   Z angle	
	
	//27:global YZ angle		28:global ZX angle		 	29:global XY angle
	//30:local  YZ angle		31:local  ZX angle			32:local  XY angle
	//33:view   YZ angle		34:view   ZX angle			35:view   XY angle
	
	public void set_distance_tag_type(int new_type_id,
			distance_tag_item my_ex_distance_tag,scene_kernel sk,client_information ci)
	{
		type_id=(new_type_id<-3)?-1:(new_type_id>35)?-1:new_type_id;
		
		extra_distance_tag=null;
		if((type_id>=27)||(type_id==-2)||(type_id==-3)){
			if(my_ex_distance_tag==null)
				type_id=-1;
			else
				extra_distance_tag=new distance_tag_item(my_ex_distance_tag);
		}
		int old_tag_component_id=tag_component_id;
		tag_component_id=sk.component_cont.root_component.component_id;

		switch((type_id%9)/3){
		case 1:
			component comp;
			if((comp=sk.component_cont.search_component())!=null)
				tag_component_id=comp.component_id;
			break;
		case 2:
			if(ci.display_camera_result!=null)
				if(ci.display_camera_result.cam.eye_component!=null)
					tag_component_id=ci.display_camera_result.cam.eye_component.component_id;
			break;
		}
		if(old_tag_component_id!=tag_component_id)
			location_version_tag=0;
		return;
	}
	public String get_tag_str(int display_precision,scene_kernel sk,client_information ci)
	{
		component comp;
		comp=sk.component_cont.get_component(p0_component_id);
		point global_p0=comp.absolute_location.multiply(p0),extra_global_p0;
		comp=sk.component_cont.get_component(px_component_id);
		point global_px=comp.absolute_location.multiply(px),extra_global_px;
		
		point dir_0,dir_1;
		double value=0;
		
		if(extra_distance_tag==null) {
			extra_global_p0=new point(0,0,0);
			extra_global_px=new point(0,0,1);
		}else {
			comp=sk.component_cont.get_component(extra_distance_tag.p0_component_id);
			extra_global_p0=comp.absolute_location.multiply(extra_distance_tag.p0);
			comp=sk.component_cont.get_component(extra_distance_tag.px_component_id);
			extra_global_px=comp.absolute_location.multiply(extra_distance_tag.px);
		}
		if(type_id<0)
			switch(type_id){
			case -1:
				return jason_string.change_string(
						tag_title+format_change.double_to_decimal_string(
							global_px.sub(global_p0).distance(),display_precision).trim());
			case -2:
				dir_0=global_px.sub(global_p0);
				dir_1=extra_global_px.sub(extra_global_p0);
				value=Math.abs(dir_0.dot(dir_1.expand(1)));
				return jason_string.change_string(tag_title+"Line:"+
					format_change.double_to_decimal_string(value,display_precision).trim());
			case -3:
				dir_0=global_px.sub(global_p0);
				dir_1=extra_global_px.sub(extra_global_p0);
				value=180.0*Math.acos(dir_0.expand(1).dot(dir_1.expand(1)))/Math.PI;
				return jason_string.change_string(tag_title+"Ang:"+
					format_change.double_to_decimal_string(value,display_precision).trim());
			default:
				return jason_string.change_string("error:type_id="+type_id);
			}
		if((comp=sk.component_cont.get_component(tag_component_id))==null)
			return jason_string.change_string("tag_component error:"+tag_component_id);

		String title[]=new String[]
		{
			"X:",	"Y:",	"Z:",		"LX:",	"LY:",	"LZ:",		"VX:",	"VY:",	"VZ:",
			"YZ:",	"ZX:",	"XY:",		"LYZ:",	"LZX:",	"LXY:",		"VYZ:",	"VZX:",	"VXY:",
			"AX:",	"AY:",	"AZ:",		"ALX:",	"ALY:",	"ALZ:",		"AVX:",	"AVY:",	"AVZ:",
			"AYZ:",	"AZX:",	"XY:",		"ALYZ:","ALZX:","ALXY:",	"AVYZ:","AVZX:","AVXY:"
		};
		double data[][]=new double[][] {new double[] {1,0,0},	new double[] {0,1,0},	new double[] {0,0,1}};
		point p0=comp.absolute_location.multiply(0,0,0);
		point p1=comp.absolute_location.multiply(data[type_id%3][0],data[type_id%3][1],data[type_id%3][2]);
		plane p_l=new plane(p0,p1);
		location loca=p_l.project_to_plane_location();
		
		switch(type_id/9) {
		case 0:
			value=Math.abs(p_l.test(global_px)-p_l.test(global_p0));
			break;
		case 1:
			value=loca.multiply(global_px).sub(loca.multiply(global_p0)).distance();
			break;
		case 2:
			if((dir_0=global_px.sub(global_p0)).distance2()<const_value.min_value2)
				return jason_string.change_string(tag_title+title[type_id]+" global_p0 too near to global_p1");
			if((dir_1=p1.sub(p0)).distance2()<const_value.min_value2)
				return jason_string.change_string(tag_title+title[type_id]+" p0 too near to p1");
			value=180.0*Math.acos(dir_0.expand(1.0).dot(dir_1.expand(1.0)))/Math.PI;
			break;
		case 3:
			if((dir_0=loca.multiply(global_px).sub(loca.multiply(global_p0))).distance2()<const_value.min_value2)
				return jason_string.change_string(tag_title+title[type_id]+" global_p0 too near to global_p1");
			if((dir_1=loca.multiply(extra_global_px).sub(loca.multiply(extra_global_p0))).distance2()<const_value.min_value2)
				return jason_string.change_string(tag_title+title[type_id]+" p0 too near to p1");
			value=180.0*Math.acos(dir_0.expand(1).dot(dir_1.expand(1)))/Math.PI;
			break;
		}
		String tag_str=format_change.double_to_decimal_string(value,display_precision);	
		return jason_string.change_string((tag_title+title[type_id]+tag_str).trim());
	}
}
