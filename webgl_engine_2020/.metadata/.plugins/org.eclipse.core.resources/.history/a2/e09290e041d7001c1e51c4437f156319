package driver_distance_tag;

import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_transformation.point;
import kernel_transformation.plane;
import kernel_common_class.const_value;
import kernel_common_class.debug_information;
import kernel_component.component;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class distance_tag_array
{
	public String tag_root_menu_component_name;
	public distance_tag_item distance_tag_array[],ex_distance_tag;
	private String directory_component_name,distance_tag_file_name;
	public int display_precision;
	private double min_view_distance;
	private boolean has_done_load_flag;

	public distance_tag_array(String my_tag_root_menu_component_name,
			String my_directory_component_name,String my_distance_tag_file_name,
			int my_display_precision,double my_min_view_distance)
	{
		tag_root_menu_component_name=my_tag_root_menu_component_name;
		distance_tag_array=new distance_tag_item[] {};
		ex_distance_tag=null;
		directory_component_name=my_directory_component_name;
		distance_tag_file_name=file_reader.separator(my_distance_tag_file_name);
		display_precision=my_display_precision;
		min_view_distance=my_min_view_distance;
		has_done_load_flag=false;
	}
	public void destroy()
	{
		distance_tag_array=null;
	}
	public void save(engine_kernel ek)
	{
		component directory_comp;
		if((directory_comp=ek.component_cont.search_component(directory_component_name))==null)
			return;
		String my_distance_tag_file_name=directory_comp.component_directory_name+distance_tag_file_name;
		file_writer fw=new file_writer(my_distance_tag_file_name,directory_comp.component_charset);
		for(int i=0,ni=distance_tag_array.length;i<ni;i++) 
			if(distance_tag_array[i].write_out(fw, ek)) {
				fw.println();
				if(distance_tag_array[i].extra_distance_tag==null)
					fw.println("/*	extra_distance_tag	*/	false");
				else {
					fw.println("/*	extra_distance_tag	*/	true");
					distance_tag_array[i].extra_distance_tag.write_out(fw, ek);
				}
				fw.println();
				fw.println();
			}
		fw.close();
	}
	public void jason(engine_kernel ek,client_information ci)
	{
		ci.request_response.print  ("[");
		String follow_str="";
		for(int i=0,ni=distance_tag_array.length;i<ni;i++)
			if(distance_tag_array[i].response_jason(i, ek, ci, follow_str)) {
				follow_str=",";
				if(distance_tag_array[i].extra_distance_tag!=null)
					distance_tag_array[i].extra_distance_tag.response_jason(i, ek, ci, follow_str);
				else {
					ci.request_response.print(",");
					ci.request_response.print("	null");
				}
			}
		ci.request_response.println();
		ci.request_response.println("]");
	}
	public void load(boolean init_flag,engine_kernel ek)
	{
		if(init_flag)
			if(has_done_load_flag)
				return;
		has_done_load_flag=true;
		
		component directory_comp;
		if((directory_comp=ek.component_cont.search_component(directory_component_name))==null)
			return;
		distance_tag_array=new distance_tag_item[] {};
		String my_distance_tag_file_name=directory_comp.component_directory_name+distance_tag_file_name;
		for(file_reader fr=new file_reader(my_distance_tag_file_name,directory_comp.component_charset);;) {
			distance_tag_item new_distance_tag;
			if((new_distance_tag=distance_tag_item.load(fr,ek))==null) {
				fr.close();
				return;
			}
			if(fr.get_boolean())
				new_distance_tag.extra_distance_tag=distance_tag_item.load(fr,ek);
			
			distance_tag_item bak[]=distance_tag_array;
			distance_tag_array=new distance_tag_item[bak.length+1];
			for(int i=0,ni=bak.length;i<ni;i++)
				distance_tag_array[i]=bak[i];
			distance_tag_array[bak.length]=new_distance_tag;
		}
	}
	public boolean clear_all_distance_tag(engine_kernel ek,client_information ci)
	{
		distance_tag_array=new distance_tag_item[] {};
		return false;
	}
	public boolean clear_distance_tag(engine_kernel ek,client_information ci)
	{
		String str;
		if((str=ci.request_response.get_parameter("id"))==null)
			str=Integer.toString(distance_tag_array.length-1);
		int tag_index=Integer.parseInt(str);
		if((tag_index<0)||(tag_index>=distance_tag_array.length))
			return true;
		distance_tag_item bak[]=distance_tag_array;
		distance_tag_array=new distance_tag_item[bak.length-1];
		for(int i=0,j=0,ni=bak.length;i<ni;i++)
			if(tag_index!=i)
				distance_tag_array[j++]=bak[i];
		return false;
	}
	public void set_extra_distance_tag(engine_kernel ek,client_information ci)
	{
		String str;
		ex_distance_tag=null;
		if((str=ci.request_response.get_parameter("id"))==null)
			return;
		int tag_index=Integer.parseInt(str);
		if((tag_index<0)||(tag_index>=distance_tag_array.length))
			return;
		if(distance_tag_array[tag_index].state==2)
			ex_distance_tag=new distance_tag_item(distance_tag_array[tag_index]);
		return;
	}
	public boolean modify_distance_tag(engine_kernel ek,client_information ci)
	{
		for(int i=0,ni=distance_tag_array.length;i<ni;i++)
			switch(distance_tag_array[i].state) {
			default:
			case 0:
			case 1:
				return true;
			case 2:
				break;
			}
		String str;
		if((str=ci.request_response.get_parameter("id"))==null)
			return true;
		int tag_index=Integer.parseInt(str);
		if((tag_index<0)||(tag_index>=distance_tag_array.length))
			return true;
		distance_tag_array[tag_index].state=1;
		return false;
	}
	public boolean set_distance_tag_type(engine_kernel ek,client_information ci)
	{
		String str;
		if((str=ci.request_response.get_parameter("id"))==null)
			return true;
		int tag_index=Integer.parseInt(str);
		if((tag_index<0)||(tag_index>=distance_tag_array.length))
			return true;
		if((str=ci.request_response.get_parameter("type"))==null)
			return true;
		distance_tag_array[tag_index].set_distance_tag_type(
					Integer.parseInt(str),ex_distance_tag,ek,ci);
		return false;
	}
	public boolean title_distance_tag(engine_kernel ek,client_information ci)
	{
		String str;
		if((str=ci.request_response.get_parameter("id"))==null)
			return true;
		int tag_index=Integer.parseInt(str);
		if((tag_index<0)||(tag_index>=distance_tag_array.length))
			return true;
		if((str=ci.request_response.get_parameter("title"))==null)
			return true;
		try {
			str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
			str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
		}catch(Exception e) {
			debug_information.println("Can't decode tag_title \t:\t",str);
			return true;
		}
		
		str=(str==null)?"":str;
		str=str.replace(" ","").replace("\t","").replace("\n","").replace("\r","");
		
		distance_tag_array[tag_index].tag_title=str;
		
		return false;
	}
	public boolean touch_distance_tag(engine_kernel ek,client_information ci)
	{
		distance_tag_item p;
		component comp_p0,comp_px;
		point touch_point,global_p0,global_px,global_py,view_p0,view_px,view_py,top_point,down_point;
		
		for(int i=0,ni=distance_tag_array.length;i<ni;i++) {
			switch((p=distance_tag_array[i]).state) {
			case 0:	//input second point
				if(ci.parameter.comp==null)
					return true;
				if((touch_point=ci.selection_camera_result.caculate_local_focus_point(ci.parameter))==null)
					return true;
				comp_p0=ek.component_cont.get_component(p.p0_component_id);
				global_p0=comp_p0.absolute_location.multiply(p.p0);
				global_px=ci.parameter.comp.absolute_location.multiply(touch_point);
				if(global_px.sub(global_p0).distance2()<const_value.min_value2)
					return true;
				if(new plane(global_p0,global_px).error_flag)
					return true;
				view_p0=ci.display_camera_result.matrix.multiply(global_p0);
				view_px=ci.display_camera_result.matrix.multiply(global_px);
				view_p0.z=view_px.z;
				if(view_px.sub(view_p0).distance2()<(min_view_distance*min_view_distance))
					return true;
				
				global_py=ci.selection_camera_result.to_me_direct.cross(global_px.sub(global_p0));
				if(global_py.distance2()<=const_value.min_value2)
					return true;
				
				top_point=ci.display_camera_result.negative_matrix.multiply(view_px.x, 1,view_px.z);
				down_point=ci.display_camera_result.negative_matrix.multiply(view_px.x,-1,view_px.z);
				global_py=global_py.expand(top_point.sub(down_point).distance()/100).add(global_p0);
				
				p.px_component_id=ci.parameter.comp.component_id;
				p.px=touch_point;
				p.py=comp_p0.caculate_negative_absolute_location().multiply(global_py);
				
				return false;
			case 1://confirm third point
				comp_p0=ek.component_cont.get_component(p.p0_component_id);
				global_p0=comp_p0.absolute_location.multiply(p.p0);
				view_p0=ci.display_camera_result.matrix.multiply(global_p0);
				
				comp_px=ek.component_cont.get_component(p.px_component_id);
				global_px=comp_px.absolute_location.multiply(p.px);
				view_px=ci.display_camera_result.matrix.multiply(global_px);

				plane p_pl_up	=new plane(global_p0,global_px,global_px.add(ci.selection_camera_result.up_direct));
				plane p_pl_right=new plane(global_p0,global_px,global_px.add(ci.selection_camera_result.right_direct));
				double p_pl_up_dot=0,p_pl_right_dot=0;
				if(!(p_pl_up.error_flag))
					p_pl_up_dot=ci.selection_camera_result.to_me_direct.dot(new point(p_pl_up.A,p_pl_up.B,p_pl_up.C));
				if(!(p_pl_right.error_flag))
					p_pl_right_dot=ci.selection_camera_result.to_me_direct.dot(new point(p_pl_right.A,	p_pl_right.B,p_pl_right.C));
				plane p_pl=(Math.abs(p_pl_up_dot)>=Math.abs(p_pl_right_dot))?p_pl_up:p_pl_right;
				if(p_pl.error_flag)
					return true;
				global_py=p_pl.insection_point(
						ci.selection_camera_result.negative_matrix.multiply(new point(0,0,ci.parameter.depth+0)),
						ci.selection_camera_result.negative_matrix.multiply(new point(0,0,ci.parameter.depth+1)));
				if(global_py==null)
					return true;
				global_py=(new plane(global_p0,global_px)).project_to_plane_location().multiply(global_py);
				if(global_py.sub(global_p0).distance2()<const_value.min_value2)
					return true;
				if(new plane(global_p0,global_py).error_flag)
					return true;
				
				view_p0=ci.display_camera_result.matrix.multiply(global_p0);
				view_py=ci.display_camera_result.matrix.multiply(global_py);
				view_p0.z=view_py.z;
				if(view_py.sub(view_p0).distance2()<(min_view_distance*min_view_distance))
					return true;

				p.py=comp_p0.caculate_negative_absolute_location().multiply(global_py);
				return false;
			default:
				break;
			}
		}
		return true;
	}
	public boolean mark_distance_tag(engine_kernel ek,client_information ci)
	{
		component comp_p0;
		point mark_point,global_p0,global_px,global_py,view_p0,view_px,view_py;

		for(int i=0,ni=distance_tag_array.length;i<ni;i++) {
			distance_tag_item p=distance_tag_array[i];
			switch(p.state){
			case 0:	//input second point
				if(ci.parameter.comp==null)
					return true;
				if((mark_point=ci.selection_camera_result.caculate_local_focus_point(ci.parameter))==null)
					return true;
				
				comp_p0=ek.component_cont.get_component(p.p0_component_id);
				global_p0=comp_p0.absolute_location.multiply(p.p0);
				global_px=ci.parameter.comp.absolute_location.multiply(mark_point);
				if(global_px.sub(global_p0).distance2()<const_value.min_value2)
					return true;
				if(new plane(global_p0,global_px).error_flag)
					return true;
				
				view_p0=ci.display_camera_result.matrix.multiply(global_p0);
				view_px=ci.display_camera_result.matrix.multiply(global_px);
				view_p0.z=view_px.z;
				if(view_px.sub(view_p0).distance2()<(min_view_distance*min_view_distance))
					return true;
				
				p.px_component_id=ci.parameter.comp.component_id;
				p.px=mark_point;
				p.state=1;
				return false;
			case 1://confirm third point
				comp_p0=ek.component_cont.get_component(p.p0_component_id);
				global_p0=comp_p0.absolute_location.multiply(p.p0);
				global_py=comp_p0.absolute_location.multiply(p.py);
				
				if(global_py.sub(global_p0).distance2()<const_value.min_value2)
					return true;
				if(new plane(global_p0,global_py).error_flag)
					return true;
				view_p0=ci.display_camera_result.matrix.multiply(global_p0);
				view_py=ci.display_camera_result.matrix.multiply(global_py);
				view_p0.z=view_py.z;
				if(view_py.sub(view_p0).distance2()<(min_view_distance*min_view_distance))
					return true;
				p.state=2;
				return false;
			default:
				break;
			}
		}
		//input first point
		if(ci.parameter.comp==null)
			return true;
		if((mark_point=ci.selection_camera_result.caculate_local_focus_point(ci.parameter))!=null){
			distance_tag_item bak[]=distance_tag_array;
			distance_tag_array=new distance_tag_item[bak.length+1];
			for(int i=0,ni=bak.length;i<ni;i++)
				distance_tag_array[i]=bak[i];
			distance_tag_array[distance_tag_array.length-1]=new distance_tag_item(mark_point,
				ci.parameter.comp.component_id,ek.component_cont.root_component.component_id);
		}
		return false;
	}
	public boolean test_location_modify(engine_kernel ek,client_information ci)
	{
		boolean ret_val=false;
		for(int i=0,ni=distance_tag_array.length;i<ni;i++) {
			distance_tag_item p=distance_tag_array[i];
			switch(p.state) {
			default:
				break;
			case 1:
			case 2:
				component comp_p0 =ek.component_cont.get_component(p.p0_component_id);
				component comp_px =ek.component_cont.get_component(p.px_component_id);
				component comp_tag=ek.component_cont.get_component(p.tag_component_id);
				component comp_extra_p0=null;
				component comp_extra_px=null;
				if(p.extra_distance_tag!=null) {
					comp_extra_p0 =ek.component_cont.get_component(p.extra_distance_tag.p0_component_id);
					comp_extra_px =ek.component_cont.get_component(p.extra_distance_tag.px_component_id);
				}

				if(p.location_version_p0>=comp_p0.get_absolute_location_version())
					if(p.location_version_px>=comp_px.get_absolute_location_version())
						if(p.location_version_tag>=comp_tag.get_absolute_location_version()) {
							if(p.extra_distance_tag==null)
								break;
							if(p.extra_distance_tag.location_version_p0>=comp_extra_p0.get_absolute_location_version())
								if(p.extra_distance_tag.location_version_px>=comp_extra_px.get_absolute_location_version())
									break;
						}
				p.location_version_p0=comp_p0.get_absolute_location_version();
				p.location_version_px=comp_px.get_absolute_location_version();
				p.location_version_tag=comp_tag.get_absolute_location_version();
				if(p.extra_distance_tag!=null){
					p.extra_distance_tag.location_version_p0=comp_extra_p0.get_absolute_location_version();
					p.extra_distance_tag.location_version_px=comp_extra_px.get_absolute_location_version();
				}
				ret_val=true;
				
				break;
			}
		}
		return ret_val;
	}
}
