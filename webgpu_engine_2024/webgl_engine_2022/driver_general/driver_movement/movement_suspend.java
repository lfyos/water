package driver_movement;

import kernel_part.part;
import kernel_render.render;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_component.component_container;
import kernel_component.component_link_list;
import kernel_common_class.debug_information;

public class movement_suspend
{
	public int virtual_mount_root_component_id,follow_mouse_component_id;
	
	private component_collector suspend_collector,virtual_mount_collector;
	private movement_match match_array[];
	private int match_number;

	public void destroy()
	{
		if(suspend_collector!=null) {
			suspend_collector.destroy();
			suspend_collector=null;
		}
		if(virtual_mount_collector!=null) {
			virtual_mount_collector.destroy();
			virtual_mount_collector=null;
		}
		if(match_array!=null) {
			for(int i=0,ni=match_array.length;i<ni;i++) 
				if(match_array[i]!=null) {
					match_array[i].destroy();
					match_array[i]=null;
				}
			match_array=null;
		}
		match_number=0;
	}
	private void init_virtual_mount_component_routine(
			scene_kernel sk,component comp,int parameter_channel_id[])
	{
		int children_number;

		if((children_number=comp.children_number())<=0){
			comp.modify_display_flag(parameter_channel_id,false,sk.component_cont);
			if(comp.driver_number()>0)
				virtual_mount_collector.register_component(comp,0);
		}else{
			for(int i=0;i<children_number;i++)
				init_virtual_mount_component_routine(sk,comp.children[i],parameter_channel_id);
			comp.modify_display_flag(parameter_channel_id,true,sk.component_cont);
		}
	}
	public void reset_virtual_mount_component(scene_kernel sk)
	{
		component virtual_mount_root_comp;
		follow_mouse_component_id=-1;
		if(virtual_mount_collector!=null)
			virtual_mount_collector.destroy();
		virtual_mount_collector=new component_collector(sk.render_cont.renders);
		if((virtual_mount_root_comp=sk.component_cont.get_component(virtual_mount_root_component_id))!=null){
			int parameter_channel_id[]=new int[virtual_mount_root_comp.multiparameter.length];
			for(int i=0,ni=parameter_channel_id.length;i<ni;i++)
				parameter_channel_id[i]=i;
			init_virtual_mount_component_routine(sk,virtual_mount_root_comp,parameter_channel_id);
		}
	}
	public movement_suspend(scene_kernel sk,int my_virtual_mount_root_component_id)
	{
		virtual_mount_root_component_id=my_virtual_mount_root_component_id;
		suspend_collector=new component_collector(sk.render_cont.renders);
		match_array=new movement_match[10];
		match_number=0;
		reset_virtual_mount_component(sk);
	}
	public int get_suspend_match_number()
	{
		return match_number;
	}
	public int get_suspend_component_number()
	{
		return suspend_collector.component_number;
	}
	public void register_match_and_component(movement_match_container match,
			int main_component_id,int follow_component_id[],component_container component_cont)
	{
		if(match==null)
			return;
		if(match.match==null)
			return;
		for(int i=0,ni=match.match.length;i<ni;i++) {
			if(match.match[i]!=null)
				switch(match.match[i].match_type) {
				case "component_part_selection":
					suspend_collector.register_component(component_cont.get_component(main_component_id));
					if(follow_component_id!=null)
						for(int j=0,nj=follow_component_id.length;j<nj;j++)
							suspend_collector.register_component(component_cont.get_component(follow_component_id[j]));
					break;
				case "component_face_match":
					if(match_array.length<=match_number) {
						movement_match bak[]=match_array;
						match_array=new movement_match[match_array.length+10];
						for(int j=0,nj=bak.length;j<nj;j++)
							match_array[j]=bak[j];
					}
					match_array[match_number++]=match.match[i];
					break;
				default:
					break;
				}
		}
	}
	private void response_suspend_jason_data(client_information ci,scene_kernel sk)
	{
		component_collector target_collector=new component_collector(sk.render_cont.renders);
		for(int i=0,ni=virtual_mount_collector.component_collector.length;i<ni;i++)
			if(virtual_mount_collector.component_collector[i]!=null)
				for(int j=0,nj=virtual_mount_collector.component_collector[i].length;j<nj;j++) {
					component_link_list cll=virtual_mount_collector.component_collector[i][j];
					for(;cll!=null;cll=cll.next_list_item)
						if(cll.comp.get_effective_display_flag(ci.display_camera_result.target.parameter_channel_id))
							target_collector.register_component(cll.comp,0);
				}
		ci.request_response.println("{");
		ci.request_response.println("	\"collector\"	:");
		
		suspend_collector.sort_component_list(sk.scene_par.component_sort_type,sk.scene_par.component_sort_min_distance);
		new movement_collector_compare(ci,sk,suspend_collector,target_collector,"		",",");

		ci.request_response.println().println().print("	\"match\"	:	[");
		String pre_str="";
		for(int i=0;i<match_number;i++) {
			movement_match m=match_array[i];
			if((m.source_body_id<0)||(m.source_face_id<0)||(m.destatination_body_id<0)||(m.destatination_face_id<0))
				continue;
			component s_comp=sk.component_cont.search_component(m.source_component_name);
			if(s_comp==null)
				continue;
			component d_comp=sk.component_cont.search_component(m.destatination_component_name);
			if(d_comp==null)
				continue;
			
			ci.request_response.println(pre_str);pre_str=",";
			ci.request_response.println("		{");
			ci.request_response.print  ("			\"source_component_id\"		:	",	s_comp.component_id).		println(",");
			ci.request_response.print  ("			\"source_body_id\"		:	",		m.source_body_id).			println(",");
			ci.request_response.print  ("			\"source_face_id\"		:	",		m.source_face_id).			println(",");
			ci.request_response.print  ("			\"destatination_component_id\"	:	",d_comp.component_id).		println(",");
			ci.request_response.print  ("			\"destatination_body_id\"		:	",m.destatination_body_id).	println(",");
			ci.request_response.print  ("			\"destatination_face_id\"		:	",m.destatination_face_id).	println();
			ci.request_response.print  ("		}");
		}
		ci.request_response.println();
		ci.request_response.println("	]");
		
		ci.request_response.println("}");
	}
	
	public void reset_suspend_collector(scene_kernel sk)
	{
		suspend_collector.reset();
	}
	public void reset_suspend_match()
	{
		if(match_array!=null)
			for(int i=0,ni=match_array.length;i<ni;i++)
				if(match_array[i]!=null)
					match_array[i]=null;
		match_array=new movement_match[10];
		for(int i=0,ni=match_array.length;i<ni;i++)
			match_array[i]=null;
		match_number=0;
	}
	private void start_follow(scene_kernel sk,client_information ci)
	{
		String str;
		
		if((str=ci.request_response.get_parameter("part_name"))==null) {
			debug_information.println("NO part name in response_suspend_event of virtual_mount_driver");
			return;
		}
		String request_charset=ci.request_response.implementor.get_request_charset();
		try{
			str=java.net.URLDecoder.decode(str,request_charset);
			str=java.net.URLDecoder.decode(str,request_charset);
		}catch(Exception e) {
			debug_information.println("Can't decode part name in response_suspend_event of virtual_mount_driver\t:\t",str);
			return;
		}
		for(int i=0,ni=virtual_mount_collector.component_collector.length;i<ni;i++) {
			if(virtual_mount_collector.component_collector[i]==null)
				continue;
			render my_render;
			if((my_render=sk.render_cont.renders.get(i))==null)
				continue;
			for(int j=0,nj=virtual_mount_collector.component_collector[i].length;j<nj;j++) {
				component_link_list cll=virtual_mount_collector.component_collector[i][j];
				if(cll==null)
					continue;
				part my_p;
				if((my_p=my_render.parts.get(j))==null)
					continue;
				if(my_p.system_name.compareTo(str)!=0)
					continue;
				for(;cll!=null;cll=cll.next_list_item) {
					if(cll.comp.get_effective_display_flag(ci.display_camera_result.target.parameter_channel_id))
						continue;
					follow_mouse_component_id=cll.comp.component_id;
						
					int parameter_channel_id_array[]=new int[cll.comp.multiparameter.length];
					for(int k=0,nk=parameter_channel_id_array.length;k<nk;k++)
						parameter_channel_id_array[k]=k;
					cll.comp.modify_display_flag(parameter_channel_id_array,true,sk.component_cont);

					debug_information.println("success in response_suspend_event of start_follow\t:\t",str);
					debug_information.println("component\t:\t",cll.comp.component_name);
					debug_information.println("system_name\t:\t",my_p.system_name);
					debug_information.println("user_name\t:\t",my_p.user_name);

					return;
				}
			}
		}
		debug_information.println("Can't find part name in response_suspend_event of virtual_mount_driver\t:\t",str);
		return;
	}
	public void response_suspend_event(scene_kernel sk,client_information ci)
	{
		String str;
		switch(((str=ci.request_response.get_parameter("virtual_mount"))==null)?"":str) {
		case "start_follow":
			start_follow(sk,ci);
			break;
		case "terminate_follow":
			follow_mouse_component_id=-1;
			break;
		case "reset":
			if((str=ci.request_response.get_parameter("component_part_selection"))!=null)
				switch(str.toLowerCase().trim()) {
				case "true":
				case "yes":
					suspend_collector.reset();
					break;
				}
					
			if((str=ci.request_response.get_parameter("component_face_match"))!=null)
				switch(str.toLowerCase().trim()) {
				case "true":
				case "yes":
					reset_suspend_match();
					break;
				}
			if((str=ci.request_response.get_parameter("component_virtual_mount"))!=null)
				switch(str.toLowerCase().trim()) {
				case "true":
				case "yes":
					reset_virtual_mount_component(sk);
					break;
				}
			break;
		case "suspend_jason":
			response_suspend_jason_data(ci,sk);
			break;
		}
	}
}
