package kernel_engine;


import kernel_render.render;

import java.util.ArrayList;

import kernel_component.component;
import kernel_component.component_collector;
import kernel_part.buffer_object_file_modify_time_and_length_item;
import kernel_part.part;
import kernel_part.part_container_for_process_sequence;
import kernel_render.render_container;

public class part_process_sequence 
{
	public int  process_parts_sequence[][];
	public int  total_buffer_object_file_number;
	public long total_buffer_object_text_data_length;
	
	public int process_package_sequence[][];
	public int system_render_part_id[][][],type_render_part_id[][][],scene_render_part_id[][][];
	public int system_package_priority[],  type_package_priority[],  scene_package_priority[];
	
	public long all_buffer_object_head_package_last_modify_time;
	
	public void destroy()
	{
		process_parts_sequence		=null;

		process_package_sequence	=null;
		system_render_part_id		=null;
		type_render_part_id			=null;
		scene_render_part_id		=null;
		
		system_package_priority		=null;
		type_package_priority		=null;
		scene_package_priority		=null;
	}
	private void init_package_priority()
	{
		system_package_priority	=new int[system_render_part_id.length];
		type_package_priority	=new int[type_render_part_id.length];
		scene_package_priority	=new int[scene_render_part_id.length];
		
		for(int i=0,ni=system_package_priority.length;i<ni;i++)
			system_package_priority[i]=process_package_sequence.length;
		for(int i=0,ni=type_package_priority.length;i<ni;i++)
			type_package_priority[i]=process_package_sequence.length;
		for(int i=0,ni=scene_package_priority.length;i<ni;i++)
			scene_package_priority[i]=process_package_sequence.length;
		
		for(int i=0,ni=process_package_sequence.length;i<ni;i++) {
			int part_type_id=process_package_sequence[i][0];
			int package_id=process_package_sequence[i][1];
			switch(part_type_id) {
			default:
			case 0:
				system_package_priority[package_id]	=i;
				break;
			case 1:
				type_package_priority[package_id]	=i;
				break;
			case 2:
				scene_package_priority[package_id]	=i;
				break;
			}
		}
	}
	private void append_package(int part_type_id,int package_id)
	{
		int bak[][]=process_package_sequence;
		process_package_sequence=new int[bak.length+1][];
		for(int i=0,ni=bak.length;i<ni;i++)
			process_package_sequence[i]=bak[i];
		process_package_sequence[bak.length]=new int[] {part_type_id,package_id};
	}
	private void append_system_part(int package_id,int render_id,int part_id)
	{
		int bak[][]=system_render_part_id[package_id];
		system_render_part_id[package_id]=new int[bak.length+1][];
		for(int i=0,ni=bak.length;i<ni;i++)
			system_render_part_id[package_id][i]=bak[i];
		system_render_part_id[package_id][bak.length]=new int[]{render_id, part_id};
	}
	private void append_type_part(int package_id,int render_id,int part_id)
	{
		int bak[][]=type_render_part_id[package_id];
		type_render_part_id[package_id]=new int[bak.length+1][];
		for(int i=0,ni=bak.length;i<ni;i++)
			type_render_part_id[package_id][i]=bak[i];
		type_render_part_id[package_id][bak.length]=new int[]{render_id, part_id};
	}
	private void append_scene_part(int package_id,int render_id,int part_id)
	{
		int bak[][]=scene_render_part_id[package_id];
		scene_render_part_id[package_id]=new int[bak.length+1][];
		for(int i=0,ni=bak.length;i<ni;i++)
			scene_render_part_id[package_id][i]=bak[i];
		scene_render_part_id[package_id][bak.length]=new int[]{render_id, part_id};
	}
	private void init_package_sequence(render_container render_cont)
	{
		part_package system_part_package=render_cont.system_part_package;
		part_package type_part_package	=render_cont.type_part_package;
		part_package scene_part_package	=render_cont.scene_part_package;

		process_package_sequence=new int[0][];
		
		system_render_part_id	=new int[system_part_package.package_file_name.length][][];
		type_render_part_id		=new int[type_part_package.  package_file_name.length][][];
		scene_render_part_id	=new int[scene_part_package. package_file_name.length][][];
		
		for(int i=0,ni=system_render_part_id.length;i<ni;i++)
			system_render_part_id[i]=new int[0][];
		for(int i=0,ni=type_render_part_id.length;i<ni;i++)
			type_render_part_id[i]=new int[0][];
		for(int i=0,ni=scene_render_part_id.length;i<ni;i++)  
			scene_render_part_id[i]=new int[0][];

		boolean system_flag[]	=new boolean[system_part_package.package_file_name.length];
		boolean type_flag  []	=new boolean[type_part_package.  package_file_name.length];
		boolean scene_flag []	=new boolean[scene_part_package. package_file_name.length];
		
		for(int i=0,ni=system_flag.length;i<ni;i++)
			system_flag[i]=false;
		for(int i=0,ni=type_flag.length;i<ni;i++)
			type_flag[i]=false;
		for(int i=0,ni=scene_flag.length;i<ni;i++)
			scene_flag[i]=false;
		
		all_buffer_object_head_package_last_modify_time=0;
		for(int i=0,ni=process_parts_sequence.length;i<ni;i++) {
			part p=render_cont.renders.get(process_parts_sequence[i][0]).parts.get(process_parts_sequence[i][1]);
			
			if(all_buffer_object_head_package_last_modify_time<p.boftal.buffer_object_head_last_modify_time)
				all_buffer_object_head_package_last_modify_time=p.boftal.buffer_object_head_last_modify_time;
			
			switch(p.part_type_id){
			default:
			case 0:
				append_system_part(p.part_package_id,p.render_id,p.part_id);
				if(system_flag[p.part_package_id])
					break;
				system_flag[p.part_package_id]=true;
				append_package(p.part_type_id,p.part_package_id);
				total_buffer_object_file_number++;
				total_buffer_object_text_data_length  +=system_part_package.package_length[p.part_package_id];
				
				if(all_buffer_object_head_package_last_modify_time<system_part_package.package_last_time[p.part_package_id])
					all_buffer_object_head_package_last_modify_time=system_part_package.package_last_time[p.part_package_id];
				
				break;
			case 1:
				append_type_part(p.part_package_id,p.render_id,p.part_id);
				if(type_flag[p.part_package_id])
					break;
				type_flag[p.part_package_id]=true;
				append_package(p.part_type_id,p.part_package_id);
				total_buffer_object_file_number++;
				total_buffer_object_text_data_length  +=type_part_package.package_length[p.part_package_id];
				
				if(all_buffer_object_head_package_last_modify_time<type_part_package.package_last_time[p.part_package_id])
					all_buffer_object_head_package_last_modify_time=type_part_package.package_last_time[p.part_package_id];
				break;
			case 2:
				append_scene_part(p.part_package_id,p.render_id,p.part_id);
				if(scene_flag[p.part_package_id])
					break;
				scene_flag[p.part_package_id]=true;
				append_package(p.part_type_id,p.part_package_id);
				total_buffer_object_file_number++;
				total_buffer_object_text_data_length  +=scene_part_package.package_length[p.part_package_id];
				
				if(all_buffer_object_head_package_last_modify_time<scene_part_package.package_last_time[p.part_package_id])
					all_buffer_object_head_package_last_modify_time=scene_part_package.package_last_time[p.part_package_id];
				
				break;
			}
		}
	}
	
	private void init_process_sequence(render_container render_cont,component root_component)
	{
		component_collector collector=new component_collector(render_cont.renders);
		collector.register_all(root_component);
		
		render r;
		part sort_parts[]=new part[collector.part_number];
		int effective_part_number=0;
		
		if((render_cont.renders!=null)&&(collector.render_component_number!=null)){
			for(int render_id=0,render_number=render_cont.renders.size();render_id<render_number;render_id++){
				if((r=render_cont.renders.get(render_id))==null)
					continue;
				if(r.parts==null)
					continue;
				int part_number=r.parts.size();
				for(int part_id=0;part_id<part_number;part_id++){
					part p;
					if((p=r.parts.get(part_id))==null)
						continue;
					if(collector.part_component_number[render_id]==null)
						continue;
					if(collector.part_component_number[render_id][part_id]<=0)
						continue;
					sort_parts[effective_part_number++]=p;
				}
			}
		}
		if(sort_parts.length!=effective_part_number){
			part bak_sort_parts[]=sort_parts;
			sort_parts=new part[effective_part_number];
			for(int i=0;i<effective_part_number;i++)
				sort_parts[i]=bak_sort_parts[i];
		}
		sort_parts=(new part_container_for_process_sequence(sort_parts)).data_array;
		process_parts_sequence=new int[sort_parts.length][];
		for(int i=0,ni=sort_parts.length;i<ni;i++)
			process_parts_sequence[i]=new int[]{sort_parts[i].render_id,sort_parts[i].part_id};

		total_buffer_object_file_number=0;
		total_buffer_object_text_data_length=0;
		
		ArrayList<buffer_object_file_modify_time_and_length_item> item_list;
		buffer_object_file_modify_time_and_length_item item;
		for(int i=0,ni=sort_parts.length;i<ni;i++)
			for(int j=0,nj=sort_parts[i].boftal.list.size();j<nj;j++) 
				for(int k=0,nk=(item_list=sort_parts[i].boftal.list.get(j)).size();k<nk;k++)
					if(!((item=item_list.get(k)).buffer_object_file_in_head_flag)){
						total_buffer_object_file_number++;
						total_buffer_object_text_data_length	+=item.buffer_object_text_file_length;
					}
	}
	public part_process_sequence(render_container render_cont,component root_component)
	{
		init_process_sequence(render_cont,root_component);
		init_package_sequence(render_cont);
		init_package_priority();
	}
}
