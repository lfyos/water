package kernel_file_manager;

import java.io.File;
import kernel_common_class.sorter;

public class travel_through_directory 
{
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
		class file_name_sorter extends sorter<String,String>
		{
			public file_name_sorter(String file_name_array[])
			{
				super(file_name_array,-1,new String[file_name_array.length]);
			}
			public int compare_data(String s,String t)
			{
				return s.compareTo(t);
			}
			public int compare_key(String s,String t)
			{
				return s.compareTo(t);
			}
		};
		
		File f;
		if((f=new File(file_name)).exists()){
			String path_name=f.getAbsolutePath();
			if(f.isDirectory()){
				operate_directory_start(path_name);
				String file_list[]=f.list();
				if(file_list!=null){
					if(sort_file_name_flag)
						file_list=(new file_name_sorter(file_list)).data_array;
					for(int i=0,ni=file_list.length;i<ni;i++)
						do_travel(path_name+File.separator+file_list[i],sort_file_name_flag);
				}
				operate_directory_terminate(path_name);
			}else
				operate_file(path_name);
		}
	}
	public travel_through_directory()
	{
	}
}
