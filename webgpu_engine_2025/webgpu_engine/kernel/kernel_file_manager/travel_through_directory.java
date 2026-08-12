package kernel_file_manager;

import java.io.File;

import kernel_common_class.tree_string_search_container;

public class travel_through_directory 
{
	private String exclude_file_name[];
	private boolean continue_flag;
	
	public void operate_directory_start(String directory_name)
	{	
	}
	public void operate_directory_terminate(String directory_name)
	{
	}
	public void operate_file(String file_name)
	{
		
	}
	public void do_travel(String file_name,boolean sort_file_name_flag)
	{
		class file_name_sorter extends tree_string_search_container<String>
		{
			public file_name_sorter(String file_name_array[])
			{
				if(file_name_array!=null)
					for(int i=0,ni=file_name_array.length;i<ni;i++)
						add(new String[] {file_name_array[i]},file_name_array[i]);
			}
		};
		
		if(exclude_file_name!=null)
			for(int i=0,ni=exclude_file_name.length;i<ni;i++)
				if(exclude_file_name[i].compareTo(file_name)==0)
					return;

		File f;
		if(!((f=new File(file_name)).exists()))
			return;
		String path_name=f.getAbsolutePath();
		if(!(f.isDirectory())){
			operate_file(path_name);
			return;
		}
		
		String file_list[]=f.list();
		if(file_list==null){
			operate_directory_start(path_name);
			if(!continue_flag)
				return;
			operate_directory_terminate(path_name);
			if(!continue_flag)
				return;
		}else {
			if(sort_file_name_flag) {
				var tree_list=(new file_name_sorter(file_list)).tree_get_value_list();
				file_list=new String[tree_list.size()];
				tree_list.toArray(file_list);
			}
			operate_directory_start(path_name);
			if(!continue_flag)
				return;
			for(int i=0,ni=file_list.length;(i<ni)&&continue_flag;i++)
				do_travel(path_name+File.separator+file_list[i],sort_file_name_flag);
			operate_directory_terminate(path_name);
			if(!continue_flag)
				return;
		}
		return;
	}
	public void mark_terminate()
	{
		continue_flag=false;
	}
	public travel_through_directory(String my_exclude_file_name[])
	{
		exclude_file_name=my_exclude_file_name;
		continue_flag=true;
	}
	public travel_through_directory()
	{
		exclude_file_name=null;
		continue_flag=true;
	}
}
