package kernel_engine;

import java.io.File;

import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_common_class.sorter;

public class proxy_parameter extends sorter<proxy_information,String>
{
	public int compare_data(proxy_information s,proxy_information t)
	{
		return s.original_url.compareTo(t.original_url);
	}
	public int compare_key(proxy_information s,String t)
	{
		return s.original_url.compareTo(t);
	}
	
	public String extract_data_root_directory;
	public String proxy_data_root_directory_name;
	
	public String compress_data_root_directory_name;
	public String compress_extract_data_root_directory;
	public String compress_proxy_data_root_directory_name;
	
	public proxy_parameter(String configure_file_name,String configure_file_system_charset)
	{
		if(file_reader.is_not_exist(configure_file_name)){
			for(int i=0,ni=configure_file_name.length();i<ni;i++)
				if(configure_file_name.charAt(i)==File.separatorChar)
					file_writer.make_directory(configure_file_name.substring(0,i+1));
			
			file_writer fw=new file_writer(configure_file_name,configure_file_system_charset);
			
			fw.println("extract_data_root_directory");
			fw.println("proxy_root_directory");
			
			fw.println("compress_data_root_directory");
			fw.println("compress_extract_data_root_directory");
			fw.println("compress_proxy_root_directory");
			fw.println();
			fw.close();
		}
		file_reader f=new file_reader(configure_file_name,configure_file_system_charset);
		
		extract_data_root_directory				=f.directory_name+file_reader.separator(f.get_string());
		proxy_data_root_directory_name			=f.directory_name+file_reader.separator(f.get_string());
		
		compress_data_root_directory_name		=f.directory_name+file_reader.separator(f.get_string());
		compress_extract_data_root_directory	=f.directory_name+file_reader.separator(f.get_string());
		compress_proxy_data_root_directory_name	=f.directory_name+file_reader.separator(f.get_string());
		
		if(extract_data_root_directory.charAt(extract_data_root_directory.length()-1)!=File.separatorChar)
			extract_data_root_directory+=File.separator;
		file_writer.make_directory(extract_data_root_directory);
		
		if(proxy_data_root_directory_name.charAt(proxy_data_root_directory_name.length()-1)!=File.separatorChar)
			proxy_data_root_directory_name+=File.separator;
		file_writer.make_directory(proxy_data_root_directory_name);
		
		if(compress_data_root_directory_name.charAt(compress_data_root_directory_name.length()-1)!=File.separatorChar)
			compress_data_root_directory_name+=File.separator;
		file_writer.make_directory(compress_data_root_directory_name);
		
		if(compress_extract_data_root_directory.charAt(compress_extract_data_root_directory.length()-1)!=File.separatorChar)
			compress_extract_data_root_directory+=File.separator;
		file_writer.make_directory(compress_extract_data_root_directory);
		
		if(compress_proxy_data_root_directory_name.charAt(compress_proxy_data_root_directory_name.length()-1)!=File.separatorChar)
			compress_proxy_data_root_directory_name+=File.separator;
		file_writer.make_directory(compress_proxy_data_root_directory_name);
		
		data_array=new proxy_information[0];
		while(true){
			proxy_information pi;
			String original_url=f.get_string();
			if(f.eof())
				break;
			
			if(f.get_boolean())
				pi=new proxy_information(original_url);
			else{
				String next_url=f.get_string();
				boolean encode_flag=f.get_boolean();
				String proxy_root_directory_name=f.get_string();
				String compress_proxy_root_directory_name=f.get_string();
				String proxy_charset_name=f.get_string();
				
				long my_max_download_file_length=f.get_long();
				
				pi=new proxy_information(original_url,
					(next_url.compareTo("null")==0)?null:next_url,encode_flag,
					f.directory_name+proxy_root_directory_name,
					f.directory_name+compress_proxy_root_directory_name,
					proxy_charset_name,my_max_download_file_length);
			}
			
			proxy_information bak[]=data_array;
			data_array=new proxy_information[bak.length+1];
			for(int i=0,ni=bak.length;i<ni;i++)
				data_array[i]=bak[i];
			data_array[data_array.length-1]=pi;
		}
		f.close();
		do_sort(-1,new  proxy_information[data_array.length]);
	}
}
