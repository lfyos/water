package kernel_part;

import java.io.File;
import java.util.ArrayList;

import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class mesh_file_collector 
{
	class mesh_file_collector_item
	{
		public String file_type;
		public int file_id;
		public String file_name;
		public long file_length;
		
		public mesh_file_collector_item(
				String my_file_type,int my_file_id,
				String my_file_name,long my_file_length)
		{
			file_type	=my_file_type;
			file_id		=my_file_id;
			file_name	=my_file_name;
			file_length	=my_file_length;
		}
	}
	
	private ArrayList<mesh_file_collector_item> list;
	
	public mesh_file_collector()
	{
		list=new ArrayList<mesh_file_collector_item>();
	}
	public void create_head_data(file_writer head_fw,long system_max_file_data_length)
	{
		long my_max_file_data_length=system_max_file_data_length-head_fw.output_data_length;
		
		for(int i=0,ni=list.size();i<ni;i++){
			mesh_file_collector_item p=list.get(i);
			if(system_max_file_data_length>0)
				if((my_max_file_data_length-=p.file_length)<=0) {
					file_writer.file_delete(p.file_name+".in_head_flag");
					continue;
				}
			head_fw.println(",");
			head_fw.println("{");
			head_fw.print  ("\t\"file_type\"\t:\t\"",p.file_type);	head_fw.println("\",");
			head_fw.print  ("\t\"file_id\"\t:\t",p.file_id);		head_fw.println(",");
			
			head_fw.print  ("\t\"file_data\"\t:\t");
			
			file_reader fr=new file_reader(p.file_name+".txt",head_fw.get_charset());
			while(!(fr.eof())) {
				String str;
				if((str=fr.get_string())==null)
					continue;
				if((str=str.trim()).length()<=0)
					continue;
				head_fw.print(str);
			}
			fr.close();
			
			head_fw.println();

			file_writer.file_touch(p.file_name+".in_head_flag",true);

			head_fw.print  ("}");
		}
	}
	public void register(String my_file_type,int my_file_id,String my_file_name)
	{
		mesh_file_collector_item p=new mesh_file_collector_item(
			my_file_type,my_file_id,my_file_name,(new File(my_file_name+".txt")).length());
		for(int i=list.size()-1;i>=0;i--) 
			if(list.get(i).file_length<=p.file_length){
				list.add(i+1,p);
				return;
			}
		list.add(0,p);
	}
}
