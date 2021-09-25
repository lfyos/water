package kernel_render;

import kernel_common_class.debug_information;
import kernel_component.component;
import kernel_engine.scene_parameter;
import kernel_engine.system_parameter;
import kernel_file_manager.file_directory;
import kernel_network.client_request_response;
import kernel_part.buffer_object_file_modify_time_and_length_container;
import kernel_part.part;
import kernel_part.part_container_for_part_search;
import kernel_part.part_loader;
import kernel_part.part_parameter;
import kernel_part.part_loader_container;
import kernel_render.render_container;

public class create_assemble_part 
{		
	private String can_create_assemble_part_name[];
	private int part_number[],component_number,give_up_number,all_part_number;
	private component component_heap[];

	private int do_test(component p)
	{
		if(p.children_number()<=0){
			for(int i=0,ni=p.driver_number();i<ni;i++){
				part comp_part=p.driver_array[i].component_part;
				if(comp_part==null)
					continue;
				if(comp_part.driver==null)
					continue;
				if(comp_part.secure_caculate_part_box(null,-1,-1,-1,-1,-1,-1,null,null)==null)
					continue;
				can_create_assemble_part_name[p.component_id]=comp_part.part_par.assemble_part_name;
				return comp_part.render_id;
			}
			can_create_assemble_part_name[p.component_id]=null;
			return -1;
		}
		
		int do_test_result=do_test(p.children[0]);
		if(p.children[0].get_dynamic_location_flag()){
			do_test_result=-1;
			can_create_assemble_part_name[p.component_id]=null;
		}else
			can_create_assemble_part_name[p.component_id]=can_create_assemble_part_name[p.children[0].component_id];
		
		for(int child_do_test_result,i=1,n=p.children.length;i<n;i++) {
			if((child_do_test_result=do_test(p.children[i]))>=0)
				if(do_test_result==child_do_test_result)
					if(!(p.children[i].get_dynamic_location_flag()))
						if(can_create_assemble_part_name[p.component_id].compareTo(
								can_create_assemble_part_name[p.children[i].component_id])==0)
									continue;
			do_test_result=-1;
			can_create_assemble_part_name[p.component_id]=null;
		}
		return (can_create_assemble_part_name[p.component_id]==null)?-1:do_test_result;
	}
	private int caculate_part_number(component p)
	{
		int children_number;
		if((children_number=p.children_number())<=0){
			if(p.driver_number()>0)
				if(p.get_component_box(false)!=null){
					part_number[p.component_id]=1;
					all_part_number++;
					return 1;
				}
			part_number[p.component_id]=0;
			give_up_number++;
			return 0;
		}else {
			part_number[p.component_id]=0;
			for(int i=0;i<children_number;i++)
				part_number[p.component_id]+=caculate_part_number(p.children[i]);
			return part_number[p.component_id];
		}
	}
	private component get_heap_component()
	{
		component ret_val=component_heap[0];
		component_heap[0]=component_heap[--component_number];
		component_heap[component_number]=null;
		for(int comp_id=0;comp_id<component_number;){
			int child_id,left_child_id=comp_id+comp_id+1,right_child_id=comp_id+comp_id+2;
			if(left_child_id>=component_number)
				break;
			if(right_child_id>=component_number)
				child_id=left_child_id;
			else{
				int left_child_number	=part_number[component_heap[left_child_id ].component_id];
				int right_child_number	=part_number[component_heap[right_child_id].component_id];
				if(left_child_number>right_child_number)
					child_id=left_child_id;
				else if(left_child_number<right_child_number)
					child_id=right_child_id;
				else{
					String left_child_name	=component_heap[left_child_id ].part_name;
					String right_child_name	=component_heap[right_child_id].part_name;
					child_id=(left_child_name.compareTo(right_child_name)<=0)?left_child_id:right_child_id;
				}
			}
			int this_number =part_number[component_heap[comp_id ].component_id];
			int child_number=part_number[component_heap[child_id].component_id];
			
			if(this_number>child_number)
				break;
			if(this_number==child_number){
				String this_name	=component_heap[comp_id ].part_name;
				String child_name	=component_heap[child_id].part_name;
				if(this_name.compareTo(child_name)<=0)
					break;
			}
			component p=component_heap[child_id];
			component_heap[child_id]=component_heap[comp_id];
			component_heap[comp_id]=p;
			comp_id=child_id;
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
		if(p.driver_number()>0)
			return;
		
		if(can_create_assemble_part_name[p.component_id]==null) {
			for(int i=0;i<children_number;i++)
				register_component(p.children[i]);
			return;
		}
		component_heap[component_number++]=p;
		for(int comp_id=component_number-1;comp_id>0;){
			int parent_id=(comp_id-1)/2;
			int parent_number=part_number[component_heap[parent_id].component_id];
			int this_number  =part_number[component_heap[comp_id  ].component_id];
			if(parent_number>this_number)
				break;
			if(parent_number==this_number)
				if(component_heap[parent_id].part_name.compareTo(component_heap[comp_id].part_name)<=0)
					break;
			component pp=component_heap[parent_id];
			component_heap[parent_id]=component_heap[comp_id];
			component_heap[comp_id]=pp;
			comp_id=parent_id;
		}
		return;
	}
	private part_parameter create_assemble_part_parameter(part p,long my_last_modified_time,
		double create_top_part_assembly_precision2,double create_top_part_discard_precision2)
	{
		return new part_parameter(
				p.part_par.part_type_string,
				p.part_par.assemble_part_name,
				p.part_par.directory_name,
				p.part_par.file_name,
				
				my_last_modified_time,
				
				p.part_par.process_sequence_id,
				
				p.part_par.max_file_head_length,
				p.part_par.max_file_data_length,
				p.part_par.max_buffer_object_data_length,
				p.part_par.max_compress_file_length,
				
				p.part_par.lod_precision_scale,
				
				create_top_part_assembly_precision2,
				create_top_part_discard_precision2,
				create_top_part_discard_precision2,
				
				p.part_par.create_face_buffer_object_bitmap,
				p.part_par.create_edge_buffer_object_bitmap,
				p.part_par.create_point_buffer_object_bitmap,
				
				p.part_par.max_component_data_buffer_number,
				p.part_par.max_part_load_thread_number,
				
				false,
				
				p.part_par.combine_to_part_package_flag,
				p.part_par.free_part_memory_flag,
				p.part_par.engine_boftal_flag,
				p.part_par.do_load_lock_flag,
				
				p.part_par.clear_model_file_flag);
	}
	
	public part top_box_part[];
	
	public create_assemble_part(boolean fast_load_flag,
			client_request_response request_response,component root_component,
			double expand_ratio,double left_ratio,double create_top_part_assembly_precision2,
			double create_top_part_discard_precision2,double discard_top_part_component_precision2,
			render_container render_cont,part_loader_container part_loader_cont,
			system_parameter system_par,scene_parameter scene_par,part_container_for_part_search pcps,
			buffer_object_file_modify_time_and_length_container boftal_container,long last_modified_time)
	{
		int max_component_number			=root_component.component_id+1;
		
		can_create_assemble_part_name		=new String[max_component_number];
		part_number							=new int[max_component_number];
		component_heap						=new component[max_component_number];
		
		for(int i=0;i<max_component_number;i++){
			can_create_assemble_part_name[i]=null;
			part_number[i]					=0;
			component_heap[i]				=null;
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
			int max_part_number=part_number[component_heap[0].component_id];
			if(max_part_number<=min_expand_part_number)
				break;
			component expand_p=get_heap_component();
			for(int i=0,child_number=expand_p.children_number();i<child_number;i++)
				register_component(expand_p.children[i]);
		}

		debug_information.println();
		debug_information.print  ("Begin creating top box");
		debug_information.print  ("\tmin_expand_part_number:",min_expand_part_number);
		debug_information.print  ("\tmin_left_part_number:",min_left_part_number);
		debug_information.print  ("\tcomponent_number:",component_number);
		debug_information.print  ("\tgive_up_number:",give_up_number);
		debug_information.println("\tall_part_number:",all_part_number);
		
		part_loader already_loaded_part[]=new part_loader[]{};
		int create_part_number=0,add_part_number=0;
		top_box_part=new part[component_number];
		
		while(component_number>0){
			if((create_part_number+min_left_part_number)>=all_part_number)
				break;
			component comp_p=get_heap_component();
			int my_create_part_number=part_number[comp_p.component_id];
			while(component_number>0){
				if(component_heap[0].part_name.compareTo(comp_p.part_name)!=0)
					break;
				my_create_part_number+=part_number[component_heap[0].component_id];
				get_heap_component();
			}
			if(can_create_assemble_part_name[comp_p.component_id]==null)
				continue;
			
			create_part_rude cpr=new create_part_rude(comp_p,
					max_component_number,discard_top_part_component_precision2);
			if((cpr.topbox_part_rude==null)&&(cpr.max_part==null)) {
				give_up_number+=my_create_part_number;
				all_part_number-=my_create_part_number;
				continue;
			}
			part assemble_part=null,assemble_part_array[];
			if((assemble_part_array=pcps.search_part(
					can_create_assemble_part_name[comp_p.component_id]))!=null)
				for(int i=0,ni=assemble_part_array.length;i<ni;i++)
					if((assemble_part=assemble_part_array[i])!=null)
						if(assemble_part.driver!=null)
							break;
			if(assemble_part==null)
				assemble_part=cpr.max_part;
			
			part_parameter part_par=create_assemble_part_parameter(
				assemble_part,comp_p.uniparameter.file_last_modified_time,
				create_top_part_assembly_precision2,create_top_part_discard_precision2);
			
			part add_part=new part(2,true,part_par,assemble_part.directory_name,assemble_part.file_charset,
					comp_p.part_name,comp_p.part_name,null,assemble_part.material_file_name,null,null);
			add_part.part_mesh=cpr.topbox_part_rude;
				
			render_cont.renders[assemble_part.render_id].add_part(assemble_part.render_id,add_part);
				
			add_part.part_from_id			=assemble_part.part_id;
			add_part.permanent_part_from_id	=assemble_part.permanent_part_id;
				
			try{
				add_part.driver=assemble_part.driver.clone(assemble_part,add_part,system_par,request_response);
			}catch(Exception e) {
				debug_information.println("Execte part clone() fail",e.toString());
				debug_information.println("Part user name:",	add_part.user_name);
				debug_information.println("Part system name:",	add_part.system_name);
				debug_information.println("Mesh_file_name:",	add_part.directory_name+add_part.mesh_file_name);
				debug_information.println("Material_file_name:",add_part.directory_name+add_part.material_file_name);
				debug_information.println("Temp directory:",	file_directory.part_file_directory(add_part,system_par,scene_par));
				e.printStackTrace();
			}
			already_loaded_part=part_loader_cont.load(
				fast_load_flag,add_part,render_cont.get_copy_from_part(add_part),
				last_modified_time,system_par,scene_par,already_loaded_part,pcps,boftal_container);
			top_box_part[add_part_number++]=add_part;
			create_part_number+=my_create_part_number;	
			
			debug_information.println();
			debug_information.println(add_part_number+".add top part		name:"+add_part.system_name);
			debug_information.println(add_part_number+".add top part		"
					+"render_id:"		+add_part.render_id		+"	permanent_render_id:"	+add_part.permanent_render_id
					+"	part_id:"		+add_part.part_id		+"	permanent_part_id:"		+add_part.permanent_part_id
					+"	part_from_id:"	+add_part.part_from_id	+"	permanent_part_from_id:"+add_part.permanent_part_from_id);
			debug_information.println(add_part_number+".add top part		material:"		+add_part.directory_name+add_part.material_file_name);
		}
		
		if(add_part_number<=0)
			top_box_part=null;
		else{
			part bak[]=top_box_part;
			top_box_part=new part[add_part_number];
			for(int i=0;i<add_part_number;i++) {
				top_box_part[i]=bak[i];
				pcps.append_one_part(bak[i]);
			}
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