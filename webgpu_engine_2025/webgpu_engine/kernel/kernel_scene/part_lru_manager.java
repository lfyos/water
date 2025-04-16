package kernel_scene;

import java.util.ArrayList;

import kernel_common_class.debug_information;
import kernel_part.part;
import kernel_render.render;

public class part_lru_manager 
{
	class part_bidirection_link_list
	{
		public boolean in_list_flag;
		public int render_id,part_id;
		public part_bidirection_link_list front,back;
		
		public void destroy()
		{
			front		=null;
			back		=null;
		}
		public part_bidirection_link_list(part my_part)
		{
			in_list_flag=false;
			render_id	=my_part.render_id;
			part_id		=my_part.part_id;
			front		=null;
			back		=null;
		}
	};
	
	private int in_list_number,max_in_list_number;
	private part_bidirection_link_list first,last;
	private part_bidirection_link_list part_array[][];
	
	public int get_in_list_number()
	{
		return in_list_number;
	}
	public boolean touch(part touch_part,ArrayList<render> ren)
	{
		boolean ret_val;
		part_bidirection_link_list pbll=part_array[touch_part.render_id][touch_part.part_id];
		
		if(pbll.in_list_flag) {
			if(first==pbll) 
				return false;
			if(last==pbll){
				last=last.front;
				last.back=null;
			}else{
				pbll.front.back=pbll.back;
				pbll.back.front=pbll.front;
			}
			ret_val=false;
		}else {
			in_list_number++;
			ret_val=true;
		}
		pbll.in_list_flag=true;
		pbll.front=null;
		pbll.back=null;

		if(first==null) {
			first=pbll;
			last=pbll;
		}else {
			pbll.back=first;
			first.front=pbll;
			first=pbll;
		}
		if(in_list_number<=max_in_list_number)
			return ret_val;
		
		if((pbll=last)==first) {
			first=null;
			last=null;
		}else{
			last=last.front;
			last.back=null;
		}
		in_list_number--;
		pbll.in_list_flag=false;
		pbll.front=null;
		pbll.back=null;
		
		part free_part=ren.get(pbll.render_id).parts.get(pbll.part_id);
		if(free_part.part_mesh!=null){
			free_part.part_mesh.free_memory();
			debug_information.println("Unload part:",
					"	user name:"	+free_part.user_name+
					"	system name:"	+free_part.system_name+
					"	mesh file:"		+free_part.directory_name+free_part.mesh_file_name);
		}
		return ret_val;
	}
	public void destroy()
	{
		first=null;
		last=null;
		for(int i=0,ni=part_array.length;i<ni;i++) {
			for(int j=0,nj=part_array[i].length;j<nj;j++) {
				part_array[i][j].destroy();
				part_array[i][j]=null;
			}
			part_array[i]=null;
		}
		part_array=null;
	}
	public part_lru_manager(ArrayList<render> ren,int my_max_in_list_number)
	{
		in_list_number=0;
		if((max_in_list_number=my_max_in_list_number)<2)
			max_in_list_number=2;
		first=null;
		last=null;
		
		int render_number=0;
		if(ren!=null)
			render_number=ren.size();
		part_array=new part_bidirection_link_list[render_number][];
		for(int render_id=0;render_id<render_number;render_id++) {
			int part_number=0;
			render r=ren.get(render_id);
			if(r.parts!=null)
				part_number=r.parts.size();
			part_array[render_id]=new part_bidirection_link_list[part_number];
			for(int part_id=0;part_id<part_number;part_id++)
				part_array[render_id][part_id]=new part_bidirection_link_list(r.parts.get(part_id));
		}
	}
}
