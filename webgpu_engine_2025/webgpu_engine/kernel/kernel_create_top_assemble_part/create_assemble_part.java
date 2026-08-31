package kernel_create_top_assemble_part;

import java.util.ArrayList;

import kernel_part.part;
import kernel_part.part_loader;
import kernel_part.part_parameter;
import kernel_component.component;
import kernel_scene.scene_parameter;
import kernel_scene.system_parameter;
import kernel_render.render_container;
import kernel_part.part_loader_container;
import kernel_file_manager.file_directory;
import kernel_component.component_container;
import kernel_common_class.debug_information;
import kernel_part.permanent_part_id_encoder;
import kernel_network.client_request_response;
import kernel_part.part_container_for_part_search;
import kernel_scene.scene_kernel_create_parameter;
import kernel_content_type.create_assemble_part_name;
import kernel_common_class.tree_string_locker_container;
import kernel_common_class.tree_string_search_container;
import kernel_part.buffer_object_file_modify_time_and_length_container;

public class create_assemble_part 
{
	private create_part_number part_number;
	private String can_create_assemble_part_name[];
	private assemble_component_heap component_heap;

	public ArrayList<part> top_box_part;
	
	public create_assemble_part(String fast_load_type,
			component_container component_cont,render_container render_cont,
			client_request_response request_response,permanent_part_id_encoder part_id_encoder,
			part_loader_container part_loader_cont,part_container_for_part_search pcps,
			ArrayList<buffer_object_file_modify_time_and_length_container> boftal_container,
			long last_modified_time,tree_string_locker_container string_locker_container,
			scene_kernel_create_parameter create_par,system_parameter system_par,scene_parameter scene_par)
	{
		debug_information.println("Begin creating top box");
		
		top_box_part=new ArrayList<part>();
		
		part_number=new create_part_number(
			component_cont.root_component,component_cont.component_number);
		can_create_assemble_part_name=create_assemble_part_name.create(
			component_cont.root_component,component_cont.component_number);
		component_heap=new assemble_component_heap();
		component_heap.register_component(component_cont.root_component,
			can_create_assemble_part_name,part_number.part_number);
		component_heap.split_large_assemble(create_par.create_top_part_expand_ratio,
			can_create_assemble_part_name,part_number.part_number,part_number.all_part_number);

		int create_part_number		=0;
		var already_loaded_part		=new ArrayList<part_loader>();
		var part_component_container=new tree_string_search_container<component>(null);

		for(component comp_p;;){
			int min_left_part_number=(int)(((double)part_number.all_part_number)
					/create_par.create_top_part_left_ratio);
			if((create_part_number+min_left_part_number)>=part_number.all_part_number)
				break;
			if((comp_p=component_heap.get_heap_component(part_number.part_number))==null)
				break;
			int my_create_part_number=part_number.part_number[comp_p.component_id];
			var component_tree_node=part_component_container.search_tree_node(comp_p.part_name); 
			if(component_tree_node!=null) {
				component_tree_node.list.add(comp_p);
				create_part_number+=my_create_part_number;
				continue;
			}

			part part_par_assemble_part=null;
			ArrayList<part> assemble_part_array=pcps.search_value_list(
				can_create_assemble_part_name[comp_p.component_id]);
			if(assemble_part_array!=null)
				for(int i=0,ni=assemble_part_array.size();i<ni;i++)
					if((part_par_assemble_part=assemble_part_array.get(i))!=null){
						if(part_par_assemble_part.driver!=null)
							break;
						part_par_assemble_part=null;
					}
			var cpr=new create_part_rude(comp_p,
					scene_par.discard_top_part_component_precision2,
					part_par_assemble_part);
			if((cpr.topbox_part_rude==null)||(cpr.select_ref_part==null)){
				part_number.give_up_number +=my_create_part_number;
				part_number.all_part_number-=my_create_part_number;
				continue;
			}
			part_component_container.add(comp_p.part_name,comp_p);

			part_parameter part_par=create_part_parameter.create(cpr.select_ref_part,
				comp_p.uniparameter.file_last_modified_time,
				scene_par.create_top_part_assembly_precision2,
				scene_par.create_top_part_discard_precision2);
			if(part_par.last_modified_time<last_modified_time)
				part_par.last_modified_time=last_modified_time;
			
			part add_part=new part(1,true,part_par,
					cpr.select_ref_part.directory_name,
					cpr.select_ref_part.file_charset,
					comp_p.part_name,comp_p.part_name,null,
					cpr.select_ref_part.material_file_name,null,null);
			add_part.part_mesh=cpr.topbox_part_rude;
				
			render_cont.renders.get(cpr.select_ref_part.render_id).add_part(add_part,part_id_encoder);
			add_part.part_from_id			=cpr.select_ref_part.part_id;
			add_part.permanent_part_from_id	=cpr.select_ref_part.permanent_part_id;

			try{
				add_part.driver=cpr.select_ref_part.driver.clone(
						cpr.select_ref_part,add_part,request_response,system_par,scene_par);
			}catch(Exception e) {
				e.printStackTrace();
				
				debug_information.println("Execte part clone() fail",e.toString());
				debug_information.println("Part user name:",	add_part.user_name);
				debug_information.println("Part system name:",	add_part.system_name);
				debug_information.println("Mesh_file_name:",	
						add_part.directory_name+add_part.mesh_file_name);
				debug_information.println("Material_file_name:",
						add_part.directory_name+add_part.material_file_name);
				debug_information.println("Temp directory:",	
						file_directory.part_temporary_directory(add_part,system_par,scene_par));
				
				render_cont.renders.get(cpr.select_ref_part.render_id).delete_last_part();
				part_component_container.remove(comp_p.part_name);
				continue;
			}
			part_loader_cont.load(add_part,fast_load_type,already_loaded_part,
				string_locker_container,system_par,scene_par,boftal_container);
			top_box_part.add(add_part);
			pcps.add(add_part.system_name,add_part);
			create_part_number+=my_create_part_number;

			debug_information.println();
			debug_information.println(top_box_part.size()
					+".add top part		name:"+add_part.system_name);
			debug_information.println(top_box_part.size()+".add top part	"
					+"	render_id:"				+add_part.render_id
					+"	part_id:"				+add_part.part_id
					+"	part_from_id:"			+add_part.part_from_id
					+"	permanent_part_id:"		+add_part.permanent_part_id
					+"	permanent_part_from_id:"+add_part.permanent_part_from_id);
			debug_information.println(top_box_part.size()
					+".add top part		material:"
					+add_part.directory_name+add_part.material_file_name);
		}
		
		if(top_box_part.size()>0)
			part_loader_container.wait_for_completion(already_loaded_part,system_par,scene_par);
		
		debug_information.println();
		debug_information.print  ("End creating top box");
		debug_information.print  ("\tadd_part_number:",top_box_part.size());
		debug_information.print  ("\tgive_up_number:",part_number.give_up_number);
		debug_information.print  ("\tratio:",part_number.all_part_number-create_part_number);
		debug_information.print  ("/",part_number.all_part_number);
		if(part_number.all_part_number>0){
			double ratio=10000*(double)(part_number.all_part_number-create_part_number);
			ratio=Math.round(ratio/(double)part_number.all_part_number)/100.0;
			debug_information.print  ("/",Double.toString(ratio)+"%");
		}
		debug_information.println();
		debug_information.println();
	}
}