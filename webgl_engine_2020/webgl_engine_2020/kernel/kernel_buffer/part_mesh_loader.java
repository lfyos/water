package kernel_buffer;

import kernel_part.part;
import kernel_render.render_container;
import kernel_engine.part_process_sequence;

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
		package_loaded_flag=new boolean[][] {
			new boolean[rc.system_part_package.package_file_name.length],
			new boolean[rc.type_part_package.  package_file_name.length],
			new boolean[rc.scene_part_package. package_file_name.length]
		};
		for(int i=0,ni=package_loaded_flag.length;i<ni;i++)
			for(int j=0,nj=package_loaded_flag[i].length;j<nj;j++)
				package_loaded_flag[i][j]=false;
		
		request_package_id=new int[5][];
		
		request_number=0;
		package_pointer=0;
	}
	public void test_request_package(int new_length,int max_length)
	{
		if(new_length>max_length)
			new_length=max_length;
		if(new_length<1)
			new_length=1;
		if(request_package_id.length!=new_length)
			request_package_id=new int[new_length][];
		request_number=0;
	}
	public int[] get_request_package(part_process_sequence pps)
	{
		while(request_number>0){
			int part_type_id=request_package_id[0][0];
			int package_id	=request_package_id[0][1];
			for(int i=0,j=1;j<request_number;i++,j++)
				request_package_id[i]=request_package_id[j];
			request_package_id[--request_number]=null;
			
			if(!(package_loaded_flag[part_type_id][package_id])){
				package_loaded_flag[part_type_id][package_id]=true;
				return new int[] {part_type_id,package_id};
			}
		}
		for(int package_number=pps.process_package_sequence.length;package_pointer<package_number;){
			int part_type_id=pps.process_package_sequence[package_pointer  ][0];
			int package_id	=pps.process_package_sequence[package_pointer++][1];
			
			if(!(package_loaded_flag[part_type_id][package_id])){
				package_loaded_flag[part_type_id][package_id]=true;
				return new int[] {part_type_id,package_id};
			}
		}
		return null;
	}
	public boolean load_test(part_process_sequence pps,part p)
	{
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
			default:
			case 0:
				last_priority=pps.system_package_priority[last_package_id];
				break;
			case 1:
				last_priority=pps.type_package_priority[last_package_id];
				break;
			case 2:
				last_priority=pps.scene_package_priority[last_package_id];
				break;
			}
			switch(p.part_type_id) {
			default:
			case 0:
				my_priority=pps.system_package_priority[p.part_package_id];
				break;
			case 1:
				my_priority=pps.type_package_priority[p.part_package_id];
				break;
			case 2:
				my_priority=pps.scene_package_priority[p.part_package_id];
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
			default:
			case 0:
				priority_1=pps.system_package_priority[package_id_1];
				break;
			case 1:
				priority_1=pps.type_package_priority[package_id_1];
				break;
			case 2:
				priority_1=pps.scene_package_priority[package_id_1];
				break;
			}

			int part_type_id_0	=request_package_id[i-0][0];
			int package_id_0	=request_package_id[i-0][1];
			int priority_0;
			switch(part_type_id_0){
			default:
			case 0:
				priority_0=pps.system_package_priority[package_id_0];
				break;
			case 1:
				priority_0=pps.type_package_priority[package_id_0];
				break;
			case 2:
				priority_0=pps.scene_package_priority[package_id_0];
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