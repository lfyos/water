package kernel_component;

import java.util.ArrayList;

import kernel_scene.scene_kernel;
import kernel_transformation.box;
import kernel_scene.scene_parameter;
import kernel_file_manager.file_reader;
import kernel_interface.client_process_bar;
import kernel_common_class.debug_information;
import kernel_network.client_request_response;
import kernel_common_class.tree_string_search_container;
import kernel_common_class.tree_search_container_tree_node;

public class component_container 
{
	public component root_component,scene_component;
	public scene_parameter	scene_par;
	
	public int component_number;
	
	public int original_part_number,part_component_number,exist_part_component_number,top_assemble_component_number;
	public int render_component_id_and_driver_id[][][],part_component_id_and_driver_id[][][][];
	public long total_face_primitive_number,total_edge_primitive_number,total_point_primitive_number;
	
	private component component_pointer[];
	private tree_string_search_container<component> search_component_cont;
	
	public void destroy()
	{
		if(root_component!=null) {
			root_component.destroy();
			root_component=null;
		}
		if(scene_component!=null) {
			scene_component.destroy();
			scene_component=null;
		}
		render_component_id_and_driver_id=null;
		part_component_id_and_driver_id=null;
		
		if(search_component_cont!=null) {
			search_component_cont.destroy();
			search_component_cont=null;
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
	public ArrayList<component> get_sort_component_list()
	{
		return search_component_cont.tree_get_value_list();
	}
	public component search_component(String my_search_component_name)
	{
		tree_search_container_tree_node<String,component> my_tree_node;
		my_tree_node=search_component_cont.search(my_search_component_name);
		if(my_tree_node!=null)
			if(my_tree_node.list.size()>0)
				return my_tree_node.list.get(0);
		return null;
	}
	public component latest_selected_component()
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
		component_array effective_comp_container=new component_array();

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
	public void do_component_caculator(boolean display_flag,
			client_process_bar process_bar,String process_bar_title)
	{
		if(root_component==null)
			return;
	
		component_caculator c_c				=new component_caculator(
				root_component,display_flag,process_bar,process_bar_title);
	
		render_component_id_and_driver_id	=c_c.render_component_id_and_driver_id;
		part_component_id_and_driver_id		=c_c.part_component_id_and_driver_id;
	
		component_pointer					=c_c.component_pointer;
		search_component_cont				=c_c.search_component_cont;
		
		component_number					=c_c.component_number;
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
			debug_information.println(",\ttotal_primitive_number:",
				"[face:"	+total_face_primitive_number	+
				",edge:"	+total_edge_primitive_number	+
				",point:"	+total_point_primitive_number	+"]");
		}
	}
	
	public component_container(file_reader scene_f,scene_kernel sk,
			component_load_source_container scene_component_load_source_cont,
			client_request_response request_response)
	{
		{
			root_component=null;
			scene_component=null;
			
			scene_par							=sk.scene_par;
			
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
			search_component_cont				=null;
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
					sk,request_response,scene_component_load_source_cont);
			
			try{
				root_component=new component("",scene_f,false,false,ccp);
			}catch(Exception e){
				e.printStackTrace();
				
				root_component=null;
				debug_information.println("Create scene from file exception:",scene_f.directory_name+scene_f.file_name);
			}
			
			if(root_component!=null)
				for(int i=0,ni=sk.system_par.max_process_component_load_number;i<ni;i++)
					if(root_component.append_component(scene_f,ccp)<=0)
						break;
			
			debug_information.println();
			debug_information.println("End loading assemble");
			debug_information.println();
		}
	}
}