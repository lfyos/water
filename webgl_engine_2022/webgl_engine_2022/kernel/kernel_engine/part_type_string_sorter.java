package kernel_engine;

import kernel_common_class.sorter;
import kernel_file_manager.file_reader;

public class part_type_string_sorter extends sorter<String,String>
{
	public int compare_data(String s,String t)
	{
		return s.compareTo(t);
	}
	public int compare_key(String s,String t)
	{
		return s.compareTo(t);
	}
	
	private int add_part_type_string(String part_type_string,int part_type_string_number)
	{
		if(part_type_string==null)
			return part_type_string_number;
		if((part_type_string=part_type_string.trim()).length()<=0)
			return part_type_string_number;
		if(part_type_string_number>=data_array.length) {
			String bak[]=data_array;
			data_array=new String[bak.length*2];
			for(int j=0,nj=bak.length;j<nj;j++)
				data_array[j]=bak[j];
		}
		data_array[part_type_string_number++]=part_type_string;
		return part_type_string_number;
	}
	public part_type_string_sorter(String file_name[],String type_string,String file_system_charset)
	{
		int index_id,part_type_string_number=0;
		data_array=new String[100];
		
		if(file_name!=null)
			for(int i=0,ni=file_name.length;i<ni;i++)
				for(file_reader f=new file_reader(file_name[i],file_system_charset);;) {
					if(f.eof()) {
						f.close();
						break;
					}
					part_type_string_number=add_part_type_string(f.get_string(),part_type_string_number);
				}
		if(type_string!=null)
			while(type_string.length()>0)
				if((index_id=type_string.indexOf(";"))==0)
					type_string=type_string.substring(1);
				else if(index_id>0) {
					part_type_string_number=add_part_type_string(
							type_string.substring(0,index_id),part_type_string_number);
					type_string=type_string.substring(index_id+1);
				}else{
					part_type_string_number=add_part_type_string(type_string,part_type_string_number);
					break;
				}
		if(part_type_string_number<=0)
			data_array=null;
		else{
			String bak[]=data_array;
			data_array=new String[part_type_string_number];
			for(int i=0;i<part_type_string_number;i++)
				data_array[i]=bak[i];
			do_sort(-1,bak);
		}
	}
}
