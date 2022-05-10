package kernel_component;

import java.io.File;

import kernel_common_class.change_name;
import kernel_file_manager.file_reader;

public class component_load_source_container 
{
	private component_load_source_item source_array[];
	
	public component_load_source_container()
	{
		source_array=new component_load_source_item[]{};
	}
	public component_load_source_container(component_load_source_container clsc)
	{
		source_array=new component_load_source_item[clsc.source_array.length];
		for(int i=0,ni=source_array.length;i<ni;i++)
			source_array[i]=clsc.source_array[i];
	}
	public void destroy()
	{
		for(int i=0,ni=source_array.length;i<ni;i++)
			source_array[i]=null;
		source_array=new component_load_source_item[0];
	}
	public component[] get_source_item(String component_name,boolean part_list_flag,
			boolean normalize_location_flag,change_name change_part_name,component_construction_parameter ccp)
	{
		for(int last_id=source_array.length-1,begin_pointer=0,end_pointer=last_id;begin_pointer<=end_pointer;){
			int middle_pointer=(begin_pointer+end_pointer)/2;
			int compare_result=source_array[middle_pointer].component_name.compareTo(component_name);
			if(compare_result<0)
				begin_pointer=middle_pointer+1;
			else if(compare_result>0)
				end_pointer=middle_pointer-1;
			else {
				for(begin_pointer=middle_pointer;begin_pointer>0;begin_pointer--)
					if(source_array[begin_pointer-1].component_name.compareTo(component_name)!=0)
						break;
				for(end_pointer=middle_pointer;end_pointer<last_id;end_pointer++)
					if(source_array[end_pointer+1].component_name.compareTo(component_name)!=0)
						break;
				component ret_val[]=new component[end_pointer-begin_pointer+1];
				for(int i=0,j=begin_pointer,nj=end_pointer;j<=nj;i++,j++) {
					file_reader my_fr=new file_reader(
							source_array[j].component_file_name,source_array[j].component_file_charset);
					ret_val[i]=new component(source_array[j].token_string,my_fr,
							part_list_flag,normalize_location_flag,change_part_name,ccp);
					my_fr.close();
				}
				component_load_source_item bak[]=source_array;
				source_array=new component_load_source_item[source_array.length-(end_pointer-begin_pointer+1)];
				for(int i=0,j=0;i<bak.length;i++)
					if((i<begin_pointer)||(i>end_pointer))
						source_array[j++]=bak[i];
				return ret_val;
			}
		}
		return null;
	}
	public void add_source_item(String component_name,String token_string,
					String component_file_name,String component_file_charset)
	{
		if(new File(component_file_name).exists()){
			component_load_source_item bak[]=source_array;
			source_array=new component_load_source_item[bak.length+1];
			for(int i=0,ni=bak.length;i<ni;i++)
				source_array[i]=bak[i];
			source_array[bak.length]=new component_load_source_item(
					component_name,token_string,component_file_name,component_file_charset);
			for(int i=bak.length;i>0;i--){
				component_load_source_item this_p=source_array[i  ];
				component_load_source_item pre_p =source_array[i-1];
				if(pre_p.component_name.compareTo(this_p.component_name)<=0)
					break;
				source_array[i  ]=pre_p;
				source_array[i-1]=this_p;
			}
		}
	}
	public int get_source_item_number()
	{
		return source_array.length;
	}
}
