package kernel_buffer;

import java.util.ArrayList;

import kernel_part.part;
import kernel_render.render_container;
import kernel_scene.part_process_sequence;

public class part_mesh_loader
{
	private boolean package_loaded_flag[][];
	private ArrayList<int[]>request_package_id;
	private int package_pointer,max_loading_number;
	
	public void destroy()
	{
		package_loaded_flag=null;
		request_package_id =null;
	}
	public part_mesh_loader(render_container rc)
	{
		package_loaded_flag		=new boolean[rc.type_part_package.length+2][];
		package_loaded_flag[0]	=new boolean[rc.system_part_package.package_file_name.length];
		package_loaded_flag[1]	=new boolean[rc.scene_part_package. package_file_name.length];
		for(int i=0,ni=rc.type_part_package.length;i<ni;i++)
			package_loaded_flag[i+2]=new boolean[rc.type_part_package[i].package_file_name.length];
		
		for(int i=0,ni=package_loaded_flag.length;i<ni;i++)
			for(int j=0,nj=package_loaded_flag[i].length;j<nj;j++)
				package_loaded_flag[i][j]=false;
		
		request_package_id=new ArrayList<int[]>();

		package_pointer=0;
		max_loading_number=1;
	}
	public int[]get_request_package(part_process_sequence pps)
	{
		while(request_package_id.size()>0){
			int p[]=request_package_id.remove(0);
			int part_type_id=p[0],package_id=p[1];
			if(!(package_loaded_flag[part_type_id][package_id])){
				package_loaded_flag[part_type_id][package_id]=true;
				return new int[] {part_type_id,package_id};
			}
		}
		for(int last_pointer=pps.process_package_sequence.size();package_pointer<last_pointer;){
			int p[]=pps.process_package_sequence.get(package_pointer++);
			int part_type_id=p[0],package_id=p[1];
			if(!(package_loaded_flag[part_type_id][package_id])){
				package_loaded_flag[part_type_id][package_id]=true;
				return new int[] {part_type_id,package_id};
			}
		}
		return null;
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
		
		for(int i=0,ni=request_package_id.size();i<ni;i++){
			int rpi[]=request_package_id.get(i);
			if((rpi[0]==p.part_type_id)&&(rpi[1]==p.part_package_id))
				return true;
		}
		
		request_package_id.add(new int[]{p.part_type_id,p.part_package_id});
		for(int i=request_package_id.size()-1,j=i-1;i>0;i--,j--){
			int rpi_i[]=request_package_id.get(i);
			int part_type_id_i=rpi_i[0],package_id_i=rpi_i[1],priority_i;
			switch(part_type_id_i){
			case 0:
				priority_i=pps.system_package_priority[package_id_i];
				break;
			case 1:
				priority_i=pps.scene_package_priority[package_id_i];
				break;
			default:
				priority_i=pps.type_package_priority[part_type_id_i-2][package_id_i];
				break;
			}
			int rpi_j[]=request_package_id.get(j);
			int part_type_id_j=rpi_j[0],package_id_j=rpi_j[1],priority_j;
			switch(part_type_id_j){
			case 0:
				priority_j=pps.system_package_priority[package_id_j];
				break;
			case 1:
				priority_j=pps.scene_package_priority[package_id_j];
				break;
			default:
				priority_j=pps.type_package_priority[part_type_id_j-2][package_id_j];
				break;
			}
			if(priority_j<=priority_i)
				break;
			request_package_id.set(i,rpi_j);
			request_package_id.set(j,rpi_i);
		}
		for(int i=request_package_id.size()-1;i>=max_loading_number;i--)
			request_package_id.remove(i);

		return true;
	}
	public void clear_request_package_id(int my_max_loading_number)
	{
		request_package_id=new ArrayList<int[]>();
		max_loading_number=my_max_loading_number;
	}
}