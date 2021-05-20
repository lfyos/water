package kernel_render;

import kernel_component.component;

public class render_target_container 
{
	private long do_render_number_array[];
	private render_target target_array[],search_array[];

	public void destroy()
	{
		do_render_number_array=null;
		
		if(target_array!=null)
			for(int i=0,ni=target_array.length;i<ni;i++)
				if(target_array[i]!=null){
					target_array[i].destroy();
					target_array[i]=null;
				}
		target_array=null;
		
		if(search_array!=null)
			for(int i=0,ni=search_array.length;i<ni;i++)
				if(search_array[i]!=null){
					search_array[i].destroy();
					search_array[i]=null;
				}
		search_array=null;
	}
	public render_target_container()
	{
		do_render_number_array	=null;
		target_array			=null;
		search_array			=null;
	}
	static public render_target get_default_target(component comp,int initial_parameter_channel_id)
	{
		render_target rt=new render_target("default_render_target",0,
			initial_parameter_channel_id,new component[] {comp},null,null,0,0,1,null,null);
		rt.main_display_target_flag=true;
		return rt;
	}
	public int get_render_target_number()
	{
		if(target_array!=null)
			if(target_array.length>0)
				return target_array.length;
		return 1;
	}
	public long get_do_render_number(int target_id)
	{
		long ret_val=do_render_number_array[target_id];
		return (ret_val<0)?-1:ret_val;
	}
	public render_target[]get_render_target(component comp,int initial_parameter_channel_id)
	{
		if(search_array!=null){
			render_target ret_val[]=new render_target[search_array.length];
			int ret_number=0;
			for(int i=0,ni=search_array.length;i<ni;i++){
				int target_id=search_array[i].target_id;
				if(do_render_number_array[target_id]!=0)
					ret_val[ret_number++]=target_array[target_id];
			}
			if(ret_number>0){
				if(ret_number<ret_val.length){
					render_target bak[]=ret_val;
					ret_val=new render_target[ret_number];
					for(int i=0;i<ret_number;i++)
						ret_val[i]=bak[i];
				}
				for(int i=0,ni=ret_val.length;i<ni;i++){
					int my_target_id=ret_val[i].target_id;
					if(do_render_number_array[my_target_id]>0)
						do_render_number_array[my_target_id]--;
				}
				return ret_val;
			}
		}
		return new render_target[]{get_default_target(comp,initial_parameter_channel_id)};
	}
	private int search_target(String my_target_name)
	{
		if(search_array!=null)
			for(int start_pointer=0,end_pointer=search_array.length-1;start_pointer<=end_pointer;){
				int compare_result,mid_pointer=(start_pointer+end_pointer)/2;
				if((compare_result=search_array[mid_pointer].target_name.compareTo(my_target_name))==0)
					return mid_pointer;
				if(compare_result<0)
					start_pointer=mid_pointer+1;
				else
					end_pointer=mid_pointer-1;
			}
		return -1;
	}
	public render_target get_target(String my_target_name)
	{
		int search_target_id;
		return ((search_target_id=search_target(my_target_name))<0)?null:(search_array[search_target_id]);
	}
	public int register_target(render_target target,long do_render_number,render_target main_target)
	{
		main_target=(main_target==null)?target:main_target;
		int search_target_id;
		if((search_target_id=search_target(target.target_name))>=0){
			target.target_id							=search_array[search_target_id].target_id;
			search_array			[search_target_id]	=target;
			target_array			[target.target_id]	=target;
			do_render_number_array	[target.target_id]	=do_render_number;
			
			target.render_target_id	=main_target.target_id;
			
			return target.target_id;
		}

		int target_number=(target_array==null)?0:target_array.length;
		long bak_render_number_array[]=do_render_number_array;
		render_target bak_target_array[]=target_array;
		render_target bak_search_array[]=search_array;
		target_array			=new render_target[target_number+1];
		search_array			=new render_target[target_number+1];
		do_render_number_array	=new long[target_number+1];
		
		for(int i=0;i<target_number;i++){
			target_array[i]				=bak_target_array[i];
			search_array[i]				=bak_search_array[i];
			do_render_number_array[i]	=bak_render_number_array[i];
		}
		target_array			[target_number]	=target;
		search_array			[target_number]	=target;
		do_render_number_array	[target_number]	=do_render_number;
		target.target_id						=target_number;
		target.render_target_id					=main_target.target_id;
		
		for(int i=target_number-1,j=target_number;i>=0;i--,j--){
			if(search_array[i].target_name.compareTo(search_array[j].target_name)<=0)
				break;
			render_target rt=search_array[i];
			search_array[i]=search_array[j];
			search_array[j]=rt;
		}
		return target.target_id;
	}
}