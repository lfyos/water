package kernel_part;

import kernel_common_class.sorter;

public class part_container_for_part_search extends sorter<part,String>
{
	public void destroy()
	{
		super.destroy();
	}
	
	public int compare_key(part s,String t)
	{
		return s.system_name.compareTo(t);
	}
	public int compare_data(part pi,part pj)
	{
		int result;
		
		if((result=pi.system_name.compareTo(pj.system_name))<0)
			return -5;
		if(result>0)
			return 5;
		
		if(pi.part_par.discard_precision2<pj.part_par.discard_precision2)
			return 4;
		if(pi.part_par.discard_precision2>pj.part_par.discard_precision2)
			return -4;

		if(pi.part_par.bottom_box_discard_precision2<pj.part_par.bottom_box_discard_precision2)
			return 3;
		if(pi.part_par.bottom_box_discard_precision2>pj.part_par.bottom_box_discard_precision2)
			return -3;

		if((pi.mesh_file_name==null)&&(!(pj.mesh_file_name==null)))
			return 3;
		if((!(pi.mesh_file_name==null))&&(pj.mesh_file_name==null))
			return -3;
		
		return 0;
	}
	public part_container_for_part_search(part my_parts[])
	{
		super(part.clone_array(my_parts),-1,new part[my_parts.length]);
		
		for(int i=0,j=0,id=0,n=get_number();i<n;){
			for(id=i,j=i;j<n;j++){
				if(data_array[id].system_name.compareTo(data_array[j].system_name)!=0)
					break;
				if(data_array[id].part_par.assembly_precision2>data_array[j].part_par.assembly_precision2)
					id=j;
			}
			for(;i<j;i++)
				data_array[i].part_par.assembly_precision2=data_array[id].part_par.assembly_precision2;
		}
	}
	
	public part[] search_part(String my_part_system_name)
	{
		int search_id[]=range(my_part_system_name);
		if(search_id==null)
			return null;
		
		part ret_part[]=new part[search_id[1]-search_id[0]+1];
		for(int i=0,ni=ret_part.length,j=search_id[0];i<ni;i++,j++)
			ret_part[i]=data_array[j];
		
		part bak[]=new part[ret_part.length];
		int effective_part_number=0;
		boolean top_flag=false,bottom_flag=false;
		for(int i=0,ni=ret_part.length;i<ni;i++)
			if((bak[effective_part_number++]=ret_part[i]).mesh_file_name==null){
				if(ret_part[i].top_box_part_flag){
					if(top_flag)
						effective_part_number--;
					else
						top_flag=true;
				}else{
					if(bottom_flag)
						effective_part_number--;
					else
						bottom_flag=true;
				}
			}
		if(bak.length==effective_part_number)
			ret_part=bak;
		else {
			ret_part=new part[effective_part_number];
			for(int i=0;i<effective_part_number;i++)
				ret_part[i]=bak[i];
		}
		return ret_part;
	}
}