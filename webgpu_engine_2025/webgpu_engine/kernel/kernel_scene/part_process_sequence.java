package kernel_scene;

import java.util.ArrayList;

import kernel_part.part;
import kernel_render.render_container;
import kernel_part.part_container_for_process_sequence;

public class part_process_sequence 
{
	public int  process_parts_sequence[][];
	public int  total_file_number;
	public long total_data_length;
	
	public							ArrayList<int[]>  process_package_sequence;
	
	public				ArrayList<	ArrayList<int[]>> system_package_render_part_id;
	public				ArrayList<	ArrayList<int[]>> scene_package_render_part_id;
	public	ArrayList<	ArrayList<	ArrayList<int[]>>>type_package_render_part_id;
	
	public int system_package_priority[],scene_package_priority[];
	public int type_package_priority[][];
	
	public long all_buffer_object_head_package_last_modify_time;
	
	public void destroy()
	{
		process_parts_sequence			=null;

		process_package_sequence		=null;
		
		system_package_render_part_id	=null;
		scene_package_render_part_id	=null;
		type_package_render_part_id		=null;

		system_package_priority			=null;
		scene_package_priority			=null;
		type_package_priority			=null;
	}
	private void init_process_sequence(render_container render_cont,
			double my_box_distance_difference_scale,double my_buffer_data_length_difference_scale)
	{
		ArrayList<part> my_part_list=(new part_container_for_process_sequence(
				render_cont.part_array_list(-1),my_box_distance_difference_scale,
				my_buffer_data_length_difference_scale)).tree_get_value_list();
		
		total_file_number		=0;
		total_data_length		=0;
		process_parts_sequence	=new int[my_part_list.size()][];
		
		int index_id=0;
		for(var my_part:my_part_list) {
			process_parts_sequence[index_id++]=new int[]{my_part.render_id,my_part.part_id};
			for(var my_boftal_list:my_part.bofmtal.bofmtali_list)
				for(var my_boftal:my_boftal_list)
					if(!(my_boftal.buffer_object_file_in_head_flag)) {
						total_file_number++;
						total_data_length+=my_boftal.buffer_object_text_file_length;
					}
		}
	}
	private void init_package_sequence(render_container render_cont)
	{
		process_package_sequence=new ArrayList<int[]>();
		
		int number=render_cont.system_part_package.package_file_name.length;
		boolean has_registered_system_package_flag[]=new boolean[number];
		system_package_render_part_id=new ArrayList<ArrayList<int[]>>();
		for(int i=0;i<number;i++) {
			system_package_render_part_id.add(new ArrayList<int[]>());
			has_registered_system_package_flag[i]=false;
		}
		
		number=render_cont.scene_part_package.package_file_name.length;
		boolean has_registered_scene_package_flag[]=new boolean[number];
		scene_package_render_part_id=new ArrayList<ArrayList<int[]>>();
		for(int i=0;i<number;i++) {
			scene_package_render_part_id.add(new ArrayList<int[]>());
			has_registered_scene_package_flag[i]=false;
		}
		
		number=render_cont.type_part_package.length;
		boolean has_registered_type_package_flag[][]=new boolean[number][];
		type_package_render_part_id=new ArrayList<ArrayList<ArrayList<int[]>>>();
		for(int i=0;i<number;i++) {
			ArrayList<ArrayList<int[]>> p=new ArrayList<ArrayList<int[]>>();
			type_package_render_part_id.add(p);
			has_registered_type_package_flag[i]=new boolean[render_cont.type_part_package[i].package_file_name.length];
			for(int j=0,nj=has_registered_type_package_flag[i].length;j<nj;j++) {
				p.add(new ArrayList<int[]>());
				has_registered_type_package_flag[i][j]=false;
			}
		}
		
		all_buffer_object_head_package_last_modify_time=0;
		for(int i=0,ni=process_parts_sequence.length;i<ni;i++) {
			int render_id	=process_parts_sequence[i][0];
			int part_id		=process_parts_sequence[i][1];
			part my_part	=render_cont.renders.get(render_id).parts.get(part_id);
			
			if((my_part.part_package_id<0)||(my_part.part_package_sequence_id<0))
				continue;
			
			if(all_buffer_object_head_package_last_modify_time<my_part.bofmtal.buffer_object_head_last_modify_time)
				all_buffer_object_head_package_last_modify_time=my_part.bofmtal.buffer_object_head_last_modify_time;
			
			part_package my_package;

			switch(my_part.part_type_id){
			case 0:
				system_package_render_part_id.get(my_part.part_package_id).add(
						new int[] {my_part.render_id,my_part.part_id});
				if(has_registered_system_package_flag[my_part.part_package_id])
					continue;
				has_registered_system_package_flag[my_part.part_package_id]=true;
				my_package=render_cont.system_part_package;
				break;
			case 1:
				scene_package_render_part_id.get(my_part.part_package_id).add(
						new int[] {my_part.render_id,my_part.part_id});
				if(has_registered_scene_package_flag[my_part.part_package_id])
					continue;
				has_registered_scene_package_flag[my_part.part_package_id]=true;
				my_package=render_cont.scene_part_package;
				break;
			default:
				type_package_render_part_id.get(my_part.part_type_id-2).get(my_part.part_package_id).add(
						new int[] {my_part.render_id,my_part.part_id});
				if(has_registered_type_package_flag[my_part.part_type_id-2][my_part.part_package_id])
					continue;
				has_registered_type_package_flag[my_part.part_type_id-2][my_part.part_package_id]=true;
				my_package=render_cont.type_part_package[my_part.part_type_id-2];
				break;
			}

			process_package_sequence.add(new int[]{my_part.part_type_id,my_part.part_package_id});
			
			total_file_number++;
			total_data_length+=my_package.package_length[my_part.part_package_id];
			if(all_buffer_object_head_package_last_modify_time<my_package.package_last_time[my_part.part_package_id])
				all_buffer_object_head_package_last_modify_time=my_package.package_last_time[my_part.part_package_id];
		}
	}
	private void init_package_priority()
	{
		int pps_number=process_package_sequence.size();
		
		system_package_priority	=new int[system_package_render_part_id.size()];
		for(int i=0,ni=system_package_priority.length;i<ni;i++)
			system_package_priority[i]=pps_number;
		
		scene_package_priority	=new int[scene_package_render_part_id.size()];
		for(int i=0,ni=scene_package_priority.length;i<ni;i++)
			scene_package_priority[i]=pps_number;
		
		type_package_priority	=new int[type_package_render_part_id.size()][];
		for(int i=0,ni=type_package_priority.length;i<ni;i++) {
			type_package_priority[i]=new int[type_package_render_part_id.get(i).size()];
			for(int j=0,nj=type_package_priority[i].length;j<nj;j++)
				type_package_priority[i][j]=pps_number;
		}

		for(int i=pps_number-1;i>=0;i--){
			int pps[]=process_package_sequence.get(i);
			int part_type_id=pps[0],package_id=pps[1];
			switch(part_type_id){
			case 0:
				system_package_priority[package_id]=i;
				break;
			case 1:
				scene_package_priority[package_id]=i;
				break;
			default:
				type_package_priority[part_type_id-2][package_id]=i;
				break;
			}
		}
	}
	public part_process_sequence(
			render_container render_cont,
			double my_box_distance_difference_scale,
			double my_buffer_data_length_difference_scale)
	{
		init_process_sequence(render_cont,
				my_box_distance_difference_scale,
				my_buffer_data_length_difference_scale);
		init_package_sequence(render_cont);
		init_package_priority();
	}
}
