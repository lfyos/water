package kernel_render;

import kernel_common_class.debug_information;
import kernel_component.component;
import kernel_engine.scene_parameter;
import kernel_engine.system_parameter;
import kernel_file_manager.file_directory;
import kernel_network.client_request_response;
import kernel_part.part;
import kernel_part.part_container_for_part_search;
import kernel_part.part_loader;
import kernel_part.part_parameter;
import kernel_part.part_loader_container;
import kernel_render.render_container;


public class create_assemble_part 
{		
	private boolean can_create_assemble_part_flag[];
	private int part_number[],component_number,give_up_number,all_part_number;
	private component comps[];

	private int do_test(component p)
	{
		part comp_part;
		int render_id=-1;
		
		if(p.children_number()<=0){
			can_create_assemble_part_flag[p.component_id]=false;
			for(int i=0,ni=p.driver_number();i<ni;i++)
				if((comp_part=p.driver_array[i].component_part)!=null)
					if((render_id=comp_part.render_id)>=0)
						if(comp_part.driver!=null)
							if(comp_part.secure_caculate_part_box(null,-1,-1,-1,-1,-1,-1,null,null)!=null){
								can_create_assemble_part_flag[p.component_id]=true;
								return render_id;
							}
			return -1;
		}
		can_create_assemble_part_flag[p.component_id]=true;
		render_id=-1;
		for(int i=0,n=p.children.length;i<n;i++){
			int my_render_id=do_test(p.children[i]);
			
			if(!(can_create_assemble_part_flag[p.children[i].component_id]))
				can_create_assemble_part_flag[p.component_id]=false;
			if(p.children[i].get_dynamic_location_flag())
				can_create_assemble_part_flag[p.component_id]=false;	
			
			if(my_render_id<0)
				can_create_assemble_part_flag[p.component_id]=false;
			else if(render_id<0)
				render_id=my_render_id;
			else if(render_id!=my_render_id)
				can_create_assemble_part_flag[p.component_id]=false;
		}
		return can_create_assemble_part_flag[p.component_id]?render_id:-1;
	}
	private int caculate_part_number(component p)
	{
		int n=0,length;
		if((length=p.children_number())<=0){
			if(p.driver_number()>0)
				if(p.get_component_box(false)!=null){
					part_number[p.component_id]=1;
					all_part_number++;
					return 1;
				}
			part_number[p.component_id]=0;
			give_up_number++;
			return 0;
		}
		for(int i=0;i<length;i++)
			n+=caculate_part_number(p.children[i]);
		part_number[p.component_id]=n;
		return n;
	}
	private component get_heap_component()
	{
		component ret_val=comps[0];
		comps[0]=comps[--component_number];
		comps[component_number]=null;
		
		for(int child_id,comp_id=0;comp_id<component_number;comp_id=child_id){
			int left_child_id=comp_id+comp_id+1;
			if(left_child_id>=component_number)
				return ret_val;
			int right_child_id=left_child_id+1;
			child_id=left_child_id;
			if(right_child_id<component_number){
				int left_child_number	=part_number[comps[left_child_id].component_id];
				int right_child_number	=part_number[comps[right_child_id].component_id];
				if(right_child_number>left_child_number)
					child_id=right_child_id;
				else if(right_child_number==left_child_number){
					String left_child_name	=comps[left_child_id].part_name;
					String right_child_name	=comps[right_child_id].part_name;
					if(right_child_name.compareTo(left_child_name)>=0)
						child_id=right_child_id;
				}
			}
			component p=comps[child_id];
			comps[child_id]=comps[comp_id];
			comps[comp_id]=p;
		}
		return ret_val;
	}
	private void register_component(component p)
	{
		int children_number;
		
		while((children_number=p.children_number())==1)
			p=p.children[0];
		if(children_number<=0)
			return;
		if(part_number[p.component_id]<=1)
			return;
		if(can_create_assemble_part_flag[p.component_id]){
			comps[component_number]=p;
			for(int parent_id,comp_id=component_number++;comp_id>0;comp_id=parent_id){
				parent_id=(comp_id-1)/2;
				int parent_number=part_number[comps[parent_id].component_id];
				int this_number=part_number[comps[comp_id].component_id];
				if(parent_number>this_number)
					return;
				if(parent_number==this_number)
					if(comps[parent_id].part_name.compareTo(comps[comp_id].part_name)>=0)
						return;
				component pp=comps[parent_id];
				comps[parent_id]=comps[comp_id];
				comps[comp_id]=pp;
			}
			return;
		}
		for(int i=0;i<children_number;i++)
			register_component(p.children[i]);
	}
	public part top_box_part[];
	public create_assemble_part(
			client_request_response request_response,
			double expand_ratio,double left_ratio,
			double create_top_part_assembly_precision2,
			double create_top_part_discard_precision2,
			double discard_top_part_component_precision2,
			component root_component,render_container ren,
			part_loader_container part_loader_cont,
			system_parameter system_par,scene_parameter scene_par,
			part_container_for_part_search pcps,
			long last_modified_time)
	{
		int max_component_number=root_component.component_id+1;
		
		can_create_assemble_part_flag	=new boolean[max_component_number];
		part_number						=new int[max_component_number];
		
		comps							=new component[max_component_number];
		
		for(int i=0;i<max_component_number;i++){
			can_create_assemble_part_flag[i]=false;
			part_number[i]=0;
			comps[i]=null;
		}
		
		do_test(root_component);
		give_up_number=0;
		all_part_number=0;
		caculate_part_number(root_component);

		component_number=0;
		register_component(root_component);

		int min_expand_part_number	=(int)(((double)all_part_number)/expand_ratio);
		int min_left_part_number	=(int)(((double)all_part_number)/left_ratio);

		while(component_number>0){
			int max_part_number=part_number[comps[0].component_id];
			if(max_part_number<=min_expand_part_number)
				break;
			component expand_p=get_heap_component();
			for(int i=0,child_number=expand_p.children_number();i<child_number;i++)
				register_component(expand_p.children[i]);
		}

		top_box_part=new part[component_number];
		
		debug_information.println();
		debug_information.print  ("Begin creating top box");
		debug_information.print  ("\tmin_expand_part_number:",min_expand_part_number);
		debug_information.print  ("\tmin_left_part_number:",min_left_part_number);
		debug_information.print  ("\tcomponent_number:",component_number);
		debug_information.print  ("\tgive_up_number:",give_up_number);
		debug_information.print  ("\tall_part_number:",all_part_number);
		
		part_loader already_loaded_part[]=new part_loader[]{};
		int create_part_number=0,add_part_number=0;
		while(component_number>0){
			if((create_part_number+min_left_part_number)>=all_part_number)
				break;
			int my_create_part_number=0;
			my_create_part_number+=part_number[comps[0].component_id];
			component comp_p=get_heap_component();
			while(component_number>0){
				if(comps[0].part_name.compareTo(comp_p.part_name)!=0)
					break;
				my_create_part_number+=part_number[comps[0].component_id];
				get_heap_component();
			}
			create_assemble_part_box_creation capbc;
			capbc=new create_assemble_part_box_creation(
					comp_p,discard_top_part_component_precision2);
			if(capbc.topbox_part_rude==null){
				give_up_number+=my_create_part_number;
				all_part_number-=my_create_part_number;
			}else{
				part add_part=new part(2,
					new part_parameter(
							capbc.biggest_part.part_par.part_type_string,
							capbc.biggest_part.part_par.directory_name,
							capbc.biggest_part.part_par.file_name,
							
							capbc.biggest_part.part_par.last_modified_time,
							
							capbc.biggest_part.part_par.simple_mesh_file_name,
							capbc.biggest_part.part_par.buffer_object_file_name,
							capbc.biggest_part.part_par.audio_file_name,
							
							capbc.biggest_part.part_par.process_sequence_id,
							
							capbc.biggest_part.part_par.max_file_head_length,
							capbc.biggest_part.part_par.max_file_data_length,
							capbc.biggest_part.part_par.max_buffer_object_data_length,
							
							1.0,
							capbc.biggest_part.part_par.lod_precision_scale,
							
							create_top_part_assembly_precision2,
							create_top_part_discard_precision2,
							create_top_part_discard_precision2,
							
							capbc.biggest_part.part_par.create_face_buffer_object_bitmap,
							capbc.biggest_part.part_par.create_edge_buffer_object_bitmap,
							capbc.biggest_part.part_par.create_point_buffer_object_bitmap,
							
							capbc.biggest_part.part_par.max_component_data_buffer_number,
							capbc.biggest_part.part_par.max_part_load_thread_number,
							capbc.biggest_part.part_par.max_mesh_load_thread_number,
							capbc.biggest_part.part_par.max_part_list_load_thread_number,
							
							false,false,
							
							capbc.biggest_part.part_par.delete_comment_in_simple_mesh_flag,
							capbc.biggest_part.part_par.combine_to_part_package_flag,
							capbc.biggest_part.part_par.delete_buffer_object_text_file_flag,
							capbc.biggest_part.part_par.clear_model_file_flag),
					
					capbc.biggest_part.directory_name,capbc.biggest_part.file_charset,
					comp_p.part_name,comp_p.part_name,null,
					capbc.biggest_part.material_file_name,
					capbc.biggest_part.description_file_name,
					capbc.biggest_part.audio_file_name);
			
				add_part.top_box_part_flag=true;
				add_part.part_mesh=capbc.topbox_part_rude;
				ren.renders[capbc.biggest_part.render_id].add_part(capbc.biggest_part.render_id,add_part);
				
				add_part.part_from_id			=capbc.biggest_part.part_id;
				add_part.permanent_part_from_id	=capbc.biggest_part.permanent_part_id;
				
				try {
					add_part.driver=capbc.biggest_part.driver.clone(capbc.biggest_part,add_part,system_par,request_response);
				}catch(Exception e) {
					debug_information.println("Execte part clone() fail",e.toString());
					debug_information.println("Part user name:",	add_part.user_name);
					debug_information.println("Part system name:",	add_part.system_name);
					debug_information.println("Mesh_file_name:",	add_part.directory_name+add_part.mesh_file_name);
					debug_information.println("Material_file_name:",add_part.directory_name+add_part.material_file_name);
					debug_information.println("Temp directory:",	file_directory.part_file_directory(add_part,system_par,scene_par));
					e.printStackTrace();
				}
				already_loaded_part=part_loader_cont.load(add_part,ren.get_copy_from_part(add_part),
					last_modified_time,system_par,scene_par,already_loaded_part,pcps);
	
				top_box_part[add_part_number++]=add_part;
				create_part_number+=my_create_part_number;
			}
		}
		if(add_part_number<=0)
			top_box_part=null;
		else{
			part bak[]=top_box_part;
			top_box_part=new part[add_part_number];
			for(int i=0;i<add_part_number;i++)
				top_box_part[i]=bak[i];	

			part_loader_container.wait_for_completion(already_loaded_part,system_par,scene_par);
		}
		
		debug_information.println();
		debug_information.print  ("End creating top box");
		debug_information.print  ("\tadd_part_number:",add_part_number);
		debug_information.print  ("\tcomponent_number:",component_number);
		debug_information.print  ("\tgive_up_number:",give_up_number);
		debug_information.print  ("\tratio:",all_part_number-create_part_number);
		debug_information.print  ("/",all_part_number);
		if(all_part_number>0){
			double ratio=10000*(double)(all_part_number-create_part_number);
			ratio=Math.round(ratio/(double)all_part_number)/100.0;
			debug_information.print  ("/",Double.toString(ratio)+"%");
		}
		debug_information.println();
		debug_information.println();
	}
}