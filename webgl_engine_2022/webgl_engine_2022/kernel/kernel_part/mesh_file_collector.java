package kernel_part;

import java.io.File;

import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class mesh_file_collector 
{
	public String file_type[];
	public int file_id[];
	public String file_name[];
	
	public long file_length[];
	
	public mesh_file_collector()
	{
		file_type	=new String[0];
		file_id		=new int[0];
		file_name	=new String[0];
		file_length	=new long[0];
	}
	public void create_head_data(file_writer head_fw,long system_max_file_data_length)
	{
		long my_max_file_data_length=system_max_file_data_length-head_fw.output_data_length;
		
		for(int i=0,ni=file_length.length;i<ni;i++){
			if(system_max_file_data_length>0)
				if((my_max_file_data_length-=file_length[i])<=0) {
					file_writer.file_delete(file_name[i]+".in_head_flag");
					continue;
				}
			head_fw.println(",");
			head_fw.println("{");
			head_fw.print  ("\t\"file_type\"\t:\t\"",file_type[i]);	head_fw.println("\",");
			head_fw.print  ("\t\"file_id\"\t:\t",file_id[i]);		head_fw.println(",");
			
			head_fw.print  ("\t\"file_data\"\t:\t[");
			
			file_reader fr=new file_reader(file_name[i]+".txt",head_fw.get_charset());
			for(int print_number=0;!(fr.eof());) {
				String str;
				if((str=fr.get_string())==null)
					continue;
				if((str=str.trim()).length()<=0)
					continue;
				head_fw.print  (((print_number++)==0)?"":",",str);
			}
			fr.close();
			
			head_fw.println("]");

			file_writer.file_touch(file_name[i]+".in_head_flag",true);

			head_fw.print  ("}");
		}
	}
	public void register(String my_file_type,int my_file_id,String my_file_name)
	{
		String str_bak[]=file_type;
		file_type=new String[file_type.length+1];
		for(int i=0,ni=str_bak.length;i<ni;i++)
			file_type[i]=str_bak[i];
		file_type[file_type.length-1]=my_file_type;
		
		int int_bak[]=file_id;
		file_id=new int[file_id.length+1];
		for(int i=0,ni=int_bak.length;i<ni;i++)
			file_id[i]=int_bak[i];
		file_id[file_id.length-1]=my_file_id;

		str_bak=file_name;
		file_name=new String[file_name.length+1];
		for(int i=0,ni=str_bak.length;i<ni;i++)
			file_name[i]=str_bak[i];
		file_name[file_name.length-1]=my_file_name;
		
		long long_bak[]=file_length;
		file_length=new long[file_length.length+1];
		for(int i=0,ni=long_bak.length;i<ni;i++)
			file_length[i]=long_bak[i];
		file_length[file_length.length-1]=(new File(my_file_name+".txt")).length();
		
		for(int j=file_type.length-1,i=j-1;i>=0;i--,j--){
			if(file_length[i]<=file_length[j])
				break;
			
			my_file_type=file_type[i];
			file_type[i]=file_type[j];
			file_type[j]=my_file_type;
					
			my_file_id=file_id[i];
			file_id[i]=file_id[j];
			file_id[j]=my_file_id;
					
			my_file_name=file_name[i];
			file_name[i]=file_name[j];
			file_name[j]=my_file_name;
					
			long my_file_length=file_length[i];
			file_length[i]=file_length[j];
			file_length[j]=my_file_length;
		}
	}
}
