package kernel_part;

import java.io.File;
import java.util.Comparator;

import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_common_class.tree_search_container;

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
class mesh_file_collector_item_comparator implements Comparator<Long>
{
	public int compare(Long s,Long t)
	{
		return (s<t)?-1:(s>t)?1:0;
	}
}
public class mesh_file_collector extends tree_search_container<Long,mesh_file_collector_item>
{
	public mesh_file_collector()
	{
		super(new mesh_file_collector_item_comparator(),null);
	}
	public void create_head_data(file_writer head_fw,long system_max_file_data_length)
	{
		long my_max_file_data_length=system_max_file_data_length-head_fw.output_data_length;
		
		for(mesh_file_collector_item p:tree_get_value_list()){
			if(system_max_file_data_length>0)
				if((my_max_file_data_length-=p.file_length)<=0) {
					file_writer.file_delete(p.file_name+".in_head_flag");
					continue;
				}
			head_fw.println(",");
			head_fw.println("{");
			head_fw.print  ("\t\"file_type\"\t:\t\"",	p.file_type).	println("\",");
			head_fw.print  ("\t\"file_id\"\t:\t",		p.file_id).		println(",");
			
			head_fw.print  ("\t\"file_data\"\t:\t");
			
			file_reader fr=new file_reader(p.file_name+".txt",head_fw.get_charset());
			for(String str;!(fr.eof());) {
				if((str=fr.get_string())==null)
					continue;
				if((str=str.trim()).length()<=0)
					continue;
				head_fw.print("\t",str);
			}
			fr.close();
			
			head_fw.println();

			file_writer.file_touch(p.file_name+".in_head_flag");

			head_fw.print  ("}");
		}
		
		return;
	}
	public void register(String my_file_type,int my_file_id,String my_file_name)
	{
		long file_length=new File(my_file_name+".txt").length();
		mesh_file_collector_item mfci=new mesh_file_collector_item(
				my_file_type,my_file_id,my_file_name,file_length);
		add(file_length,mfci);
	}
}
