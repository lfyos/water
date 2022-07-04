package kernel_component;

import kernel_common_class.change_name;
import kernel_common_class.debug_information;
import kernel_engine.engine_kernel;
import kernel_engine.part_type_string_sorter;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_transformation.box;

public class component_container 
{
	public component root_component;
	
	public int original_part_number,part_component_number,exist_part_component_number,top_assemble_component_number;
	public int render_component_id_and_driver_id[][][],part_component_id_and_driver_id[][][][];
	public long total_face_primitive_number,total_edge_primitive_number,total_point_primitive_number;
	
	public change_name change_part_name,change_component_name;
	
	private component component_pointer[],sort_component_pointer[];
	
	public void destroy()
	{
		if(root_component!=null) {
			root_component.destroy();
			root_component=null;
		}
		render_component_id_and_driver_id=null;
		part_component_id_and_driver_id=null;
		
		if(change_part_name!=null) {
			change_part_name.destroy();
			change_part_name=null;
		}
		if(change_component_name!=null) {
			change_component_name.destroy();
			change_component_name=null;
		}
		if(sort_component_pointer!=null) {
			for(int i=0,ni=sort_component_pointer.length;i<ni;i++)
				if(sort_component_pointer[i]!=null) {
					sort_component_pointer[i].destroy();
					sort_component_pointer[i]=null;
				}
			sort_component_pointer=null;
		}
		if(component_pointer!=null){
			for(int i=0,ni=component_pointer.length;i<ni;i++)
				if(component_pointer[i]!=null){
					component_pointer[i].destroy();
					component_pointer[i]=null;
				}
			component_pointer=null;
		}
	}
	public component get_component(int component_id)
	{
		return ((component_id<0)||(component_id>=component_pointer.length))?null:component_pointer[component_id];
	}
	public component[] get_component_array()
	{
		return component_pointer;
	}
	public component[] get_sort_component_array()
	{
		return sort_component_pointer;
	}
	public component search_component(String my_search_component_name)
	{
		if(my_search_component_name!=null){
			String search_component_name=change_component_name.search_change_name(
						my_search_component_name,my_search_component_name);
			for(int i=0,j=sort_component_pointer.length-1;i<=j;){
				int mid=(i+j)/2;
				int result=sort_component_pointer[mid].component_name.compareTo(search_component_name);
				if(result>0)
					j=mid-1;
				else if(result<0)
					i=mid+1;
				else
					return sort_component_pointer[mid];
			}
		}
		return null;
	}
	public component search_component()
	{
		component ret_val;
		if((ret_val=root_component)!=null)
			if(component_pointer!=null)
				for(int i=0,ni=component_pointer.length;i<ni;i++)
					if(component_pointer[i]!=null)
						if(ret_val.uniparameter.selected_time<component_pointer[i].uniparameter.selected_time)
							ret_val=component_pointer[i];
		return ret_val;
	}
	public box get_effective_box(int parameter_channel_id)
	{
		box effective_box;
		component_array effective_comp_container=new component_array(root_component.component_id+1);

		effective_comp_container.clear_compoment();
		effective_comp_container.add_selected_component(root_component,false);
		if((effective_box=effective_comp_container.get_box())!=null)	
			return effective_box;
		
		effective_comp_container.clear_compoment();
		effective_comp_container.add_visible_component(root_component,parameter_channel_id,false);
		if((effective_box=effective_comp_container.get_box())!=null)
			return effective_box;
		
		effective_comp_container.clear_compoment();
		effective_comp_container.add_visible_component(root_component,parameter_channel_id,true);
		if((effective_box=effective_comp_container.get_box())!=null)
			return effective_box;

		effective_comp_container.clear_compoment();
		effective_comp_container.add_part_list_component(root_component);
		if((effective_box=effective_comp_container.get_box())!=null)
			return effective_box;

		effective_comp_container.clear_compoment();
		effective_comp_container.add_component(root_component);
		if((effective_box=effective_comp_container.get_box())!=null)
			return effective_box;
		
		if((effective_box=root_component.get_component_box(false))!=null)
			return effective_box;
		else
			return root_component.get_component_box(true);
	}
	public void do_component_caculator(boolean display_flag)
	{
		if(root_component==null)
			return;
	
		component_caculator c_c				=new component_caculator(root_component,display_flag);
	
		render_component_id_and_driver_id	=c_c.render_component_id_and_driver_id;
		part_component_id_and_driver_id		=c_c.part_component_id_and_driver_id;
	
		component_pointer					=c_c.component_pointer;
		sort_component_pointer				=c_c.sort_component_pointer;
		
		top_assemble_component_number		=c_c.top_assemble_component_number;
		part_component_number				=c_c.part_component_number;
		exist_part_component_number			=c_c.exist_part_component_number;
		total_face_primitive_number			=c_c.total_face_primitive_number;
		total_edge_primitive_number			=c_c.total_edge_primitive_number;
		total_point_primitive_number		=c_c.total_point_primitive_number;

		if(display_flag) {
			debug_information.println();
			debug_information.print  ("component number:",				component_pointer.length);
			debug_information.print  (",\tpart_component_number:",		part_component_number);
			debug_information.print  (",\texist_part_component_number:",exist_part_component_number);
			debug_information.println(",\ttotal_primitive_number:[",		
				total_face_primitive_number+","+total_edge_primitive_number+","+total_point_primitive_number+"]");
		}
	}
	
	public component_container(file_reader scene_f,engine_kernel ek,
			component_load_source_container component_load_source_cont,
			long default_display_bitmap,client_request_response request_response,
			change_name my_change_part_name,change_name my_change_component_name,
			part_type_string_sorter my_type_string_sorter)
	{
		{
			root_component=null;
			
			original_part_number				=0;
			
			part_component_number				=0;
			exist_part_component_number			=0;
			top_assemble_component_number		=0;
			total_face_primitive_number			=0;
			total_edge_primitive_number			=0;
			total_point_primitive_number		=0;
			
			render_component_id_and_driver_id	=null;
			part_component_id_and_driver_id		=null;
			
			component_pointer					=null;
			sort_component_pointer				=null;
			
			change_part_name					=my_change_part_name;
			change_component_name				=my_change_component_name;
		}
		{
			if(scene_f.eof()){
				debug_information.println();
				debug_information.println("Load scene file fail		:	",scene_f.directory_name+scene_f.file_name);
				debug_information.println();
				return;
			}
			
			debug_information.println();
			debug_information.println("Begin loading scene");

			component_construction_parameter ccp=new component_construction_parameter(
					ek,request_response,ek.part_cont,change_part_name,
					my_type_string_sorter,component_load_source_cont,default_display_bitmap);
			try{
				root_component=new component("",scene_f,false,false,1.0,ccp);
			}catch(Exception e){
				root_component=null;
				debug_information.println("Create scene from file exception:",scene_f.directory_name+scene_f.file_name);
				e.printStackTrace();
			}
			if(root_component!=null)
				for(int i=0,ni=ek.system_par.max_process_component_load_number;(i<ni)&&(ccp.clsc.get_source_item_number()>0);i++)
					root_component.append_component(
						root_component.uniparameter.component_driver_lod_precision_scale,ccp);

			debug_information.println();
			debug_information.println("End loading assemble");
			debug_information.println();
		}
	}
}