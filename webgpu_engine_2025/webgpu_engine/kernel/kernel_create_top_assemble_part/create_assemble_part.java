package kernel_create_top_assemble_part;

import java.util.ArrayList;

import kernel_part.part;
import kernel_scene.scene_kernel;
import kernel_part.part_parameter;
import kernel_component.component;
import kernel_file_manager.file_directory;
import kernel_common_class.debug_information;
import kernel_part.permanent_part_id_encoder;
import kernel_scene.scene_load_call_parameter;
import kernel_network.client_request_response;
import kernel_common_class.tree_string_search_container;

public class create_assemble_part 
{
	private scene_kernel sk;	
	private long last_modified_time;
	
	private client_request_response request_response;
	private permanent_part_id_encoder part_id_encoder;

	private create_part_number part_number;
	private String reference_part_name[];
	private assemble_component_heap component_heap;

	private int has_created_part_component_number;
	private tree_string_search_container<component>has_created_part_component_container;

	public ArrayList<part> top_box_part;
	
	private boolean create_part_driver(part add_part,create_part_rude cpr)
	{
		try{
			add_part.driver=cpr.select_ref_part.driver.clone(
					cpr.select_ref_part,add_part,request_response,sk.system_par,sk.scene_par);
			return false;
		}catch(Exception e){
			e.printStackTrace();

			debug_information.println("Execte part clone() fail",e.toString());
			debug_information.println("Part user name:",	add_part.user_name);
			debug_information.println("Part system name:",	add_part.system_name);
			debug_information.println("Mesh_file_name:",	
					add_part.directory_name+add_part.mesh_file_name);
			debug_information.println("Material_file_name:",
					add_part.directory_name+add_part.material_file_name);
			debug_information.println("Temp directory:",	
					file_directory.part_temporary_directory(add_part,sk.system_par,sk.scene_par));

			return true;
		}
	}
	private void create_one_top_part(component comp)
	{
		var component_tree_node=has_created_part_component_container.search_tree_node(comp.part_name);
		
		if(component_tree_node!=null){
			component_tree_node.list.add(comp);
			has_created_part_component_number+=part_number.part_number[comp.component_id];

			return;
		}
		part part_par_assemble_part=null;
		ArrayList<part> assemble_part_array=sk.part_search_cont.
				search_value_list(reference_part_name[comp.component_id]);
		if(assemble_part_array!=null)
			for(int i=0,ni=assemble_part_array.size();i<ni;i++)
				if((part_par_assemble_part=assemble_part_array.get(i))!=null){
					if(part_par_assemble_part.driver!=null)
						break;
					part_par_assemble_part=null;
				}
		create_part_rude cpr=new create_part_rude(comp,part_par_assemble_part,
					sk.scene_par.discard_top_part_component_precision2);
		
		if((cpr.topbox_part_rude==null)||(cpr.select_ref_part==null)){
			part_number.give_up_number +=part_number.part_number[comp.component_id];
			part_number.all_part_number-=part_number.part_number[comp.component_id];
			return;
		}
		has_created_part_component_container.add(comp.part_name,comp);

		part_parameter part_par=create_part_parameter.create(cpr.select_ref_part,
			comp.uniparameter.file_last_modified_time,
			sk.scene_par.create_top_part_assembly_precision2,
			sk.scene_par.create_top_part_discard_precision2);
		if(part_par.last_modified_time<last_modified_time)
			part_par.last_modified_time=last_modified_time;
		
		part add_part=new part(1,true,part_par,
				cpr.select_ref_part.directory_name,
				cpr.select_ref_part.file_charset,
				comp.part_name,comp.part_name,null,
				cpr.select_ref_part.material_file_name,null,null);
		add_part.part_mesh=cpr.topbox_part_rude;
			
		sk.render_cont.renders.get(cpr.select_ref_part.render_id).add_part(add_part,part_id_encoder);
		add_part.part_from_id			=cpr.select_ref_part.part_id;
		add_part.permanent_part_from_id	=cpr.select_ref_part.permanent_part_id;

		if(create_part_driver(add_part,cpr)) {
			sk.render_cont.renders.get(cpr.select_ref_part.render_id).delete_last_part();
			has_created_part_component_container.remove(comp.part_name);
			return;
		}
		
		top_box_part.add(add_part);
		sk.part_search_cont.add(add_part.system_name,add_part);
		has_created_part_component_number+=part_number.part_number[comp.component_id];
		
		debug_information.println("Create top part:		",add_part.system_name+"	"+comp.component_name);
	}
	public create_assemble_part(scene_kernel my_sk,String fast_load_type,
			scene_load_call_parameter load_par,client_request_response my_request_response,
			permanent_part_id_encoder my_part_id_encoder)
	{
		sk					=my_sk;
		request_response	=my_request_response;
		part_id_encoder		=my_part_id_encoder;

		last_modified_time	=sk.caculate_scene_last_modified_time();
		
		part_number			=new create_part_number(
				sk.component_cont.root_component,sk.component_cont.component_number);
		reference_part_name	=create_assemble_part_name.create(
				sk.component_cont.root_component,sk.component_cont.component_number);
		component_heap		=new assemble_component_heap(
				part_number.part_number,reference_part_name,sk.component_cont.root_component,
				(int)(part_number.all_part_number/sk.create_parameter.create_top_part_expand_ratio));

		has_created_part_component_number	=0;
		has_created_part_component_container=new tree_string_search_container<component>();
		
		top_box_part=new ArrayList<part>();

		debug_information.println("Begin creating top box");
		debug_information.println();
		
		for(component comp;;){
			int min_left_part_number=(int)(((double)part_number.all_part_number)
					/sk.create_parameter.create_top_part_left_ratio);
			if((has_created_part_component_number+min_left_part_number)>=part_number.all_part_number)
				break;
			if((comp=component_heap.extract_heap_data())==null)
				break;
			create_one_top_part(comp);
		};
		debug_information.println();
		debug_information.print  ("Creating top box");
		debug_information.print  ("\tadd_part_number:",top_box_part.size());
		debug_information.print  ("\tgive_up_number:",part_number.give_up_number);
		debug_information.print  ("\tratio:",part_number.all_part_number-has_created_part_component_number);
		debug_information.print  ("/",part_number.all_part_number);
		if(part_number.all_part_number>0){
			double ratio=10000*(double)(part_number.all_part_number-has_created_part_component_number);
			ratio=Math.round(ratio/(double)part_number.all_part_number)/100.0;
			debug_information.print  ("/",Double.toString(ratio)+"%");
		}
		debug_information.println();
	}
}