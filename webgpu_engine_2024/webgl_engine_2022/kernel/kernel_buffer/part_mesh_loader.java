package kernel_buffer;

import kernel_part.part;
import kernel_render.render_container;
import kernel_scene.part_process_sequence;

public class part_mesh_loader
{
	private boolean package_loaded_flag[][];
	private int request_package_id[][];
	private int package_pointer,request_number;
	
	public void destroy()
	{
		package_loaded_flag=null;
		request_package_id =null;
	}
	public part_mesh_loader(render_container rc)
	{
		package_loaded_flag=new boolean[rc.type_part_package.length+2][];
		package_loaded_flag[0]=new boolean[rc.system_part_package.package_file_name.length];
		package_loaded_flag[1]=new boolean[rc.scene_part_package. package_file_name.length];
		for(int i=0,ni=rc.type_part_package.length;i<ni;i++)
			package_loaded_flag[i+2]=new boolean[rc.type_part_package[i].package_file_name.length];
		for(int i=0,ni=package_loaded_flag.length;i<ni;i++)
			for(int j=0,nj=package_loaded_flag[i].length;j<nj;j++)
				package_loaded_flag[i][j]=false;
		
		request_package_id=new int[5][];

		request_number=0;
		package_pointer=0;
	}
	public void test_request_package(int max_length)
	{
		if(request_package_id.length!=max_length){
			int bak[][]=request_package_id;
			request_package_id=new int[max_length][];
			request_number=(request_number>max_length)?max_length:request_number;
			for(int i=0;i<request_number;i++)
				request_package_id[i]=bak[i];
		}
	}
	public int[]get_request_package(part_process_sequence pps,boolean only_request_flag)
	{
		while(request_number>0){
			int part_type_id=request_package_id[0][0];
			int package_id	=request_package_id[0][1];
			for(int i=0,j=1;j<request_number;i++,j++)
				request_package_id[i]=request_package_id[j];
			request_package_id[--request_number]=null;
			
			if(package_loaded_flag[part_type_id][package_id])
				continue;
			package_loaded_flag[part_type_id][package_id]=true;
			return new int[] {part_type_id,package_id};
		}
		if(only_request_flag)
			return null;
		if(package_pointer>=pps.process_package_sequence.size())
			return null;
		int p[]=pps.process_package_sequence.get(package_pointer++);
		int part_type_id=p[0],package_id=p[1];
		if(package_loaded_flag[part_type_id][package_id])
			return null;
		package_loaded_flag[part_type_id][package_id]=true;
		
		return new int[] {part_type_id,package_id};
	}
	public boolean load_test(part_process_sequence pps,part p)
	{
		if((p.part_type_id<0)||(p.part_package_id<0)||(p.part_package_sequence_id<0))
			return false;
		if(p.part_type_id>=package_loaded_flag.length)
			return false;
		if(p.part_package_id>=package_loaded_flag[p.part_type_id].length)
			return false;
		if(package_loaded_flag[p.part_type_id][p.part_package_id])
			return false;
		
		for(int i=0;i<request_number;i++)
			if(request_package_id[i][0]==p.part_type_id)
				if(request_package_id[i][1]==p.part_package_id)
					return true;
		
		if(request_number<request_package_id.length)
			request_package_id[request_number++]=new int[] {p.part_type_id,p.part_package_id};
		else{
			int last_part_type_id	=request_package_id[request_package_id.length-1][0];
			int last_package_id		=request_package_id[request_package_id.length-1][1];
			int last_priority,my_priority;
			switch(last_part_type_id) {
			case 0:
				last_priority=pps.system_package_priority[last_package_id];
				break;
			case 1:
				last_priority=pps.scene_package_priority[last_package_id];
				break;
			default:
				last_priority=pps.type_package_priority[last_part_type_id-2][last_package_id];
				break;
			}
			switch(p.part_type_id) {
			case 0:
				my_priority=pps.system_package_priority[p.part_package_id];
				break;
			case 1:
				my_priority=pps.scene_package_priority[p.part_package_id];
				break;
			default:
				my_priority=pps.type_package_priority[p.part_type_id-2][p.part_package_id];
				break;
			}
			if(last_priority<=my_priority)
				return true;
			request_package_id[request_package_id.length-1]=new int[]{p.part_type_id,p.part_package_id};
		}
		for(int i=request_number-1;i>0;i--){
			int part_type_id_1	=request_package_id[i-1][0];
			int package_id_1	=request_package_id[i-1][1];
			int priority_1;
			switch(part_type_id_1){
			case 0:
				priority_1=pps.system_package_priority[package_id_1];
				break;
			case 1:
				priority_1=pps.scene_package_priority[package_id_1];
				break;
			default:
				priority_1=pps.type_package_priority[part_type_id_1-2][package_id_1];
				break;
			}

			int part_type_id_0	=request_package_id[i-0][0];
			int package_id_0	=request_package_id[i-0][1];
			int priority_0;
			switch(part_type_id_0){
			case 0:
				priority_0=pps.system_package_priority[package_id_0];
				break;
			case 1:
				priority_0=pps.scene_package_priority[package_id_0];
				break;
			default:
				priority_0=pps.type_package_priority[part_type_id_0-2][package_id_0];
				break;
			}
			if(priority_1<=priority_0)
				return true;
			
			int bak[]=request_package_id[i-1];
			request_package_id[i-1]=request_package_id[i-0];
			request_package_id[i-0]=bak;
		}
		return true;
	}
}