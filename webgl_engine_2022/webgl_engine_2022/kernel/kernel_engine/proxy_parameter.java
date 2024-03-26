package kernel_engine;

import java.io.File;

import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class proxy_parameter
{	
	public String proxy_data_root_directory_name;
	
	public String compress_data_root_directory_name;
	public String compress_proxy_data_root_directory_name;
	
	public proxy_parameter(String proxy_configure_file_name,String proxy_configure_file_charset)
	{
		if(file_reader.is_not_exist(proxy_configure_file_name)){
			file_writer fw=new file_writer(proxy_configure_file_name,proxy_configure_file_charset);
			
			fw.println("proxy_root_directory");
			
			fw.println("compress_data_root_directory");
			fw.println("compress_proxy_root_directory");
			fw.println();
			fw.close();
		}
		file_reader f=new file_reader(proxy_configure_file_name,proxy_configure_file_charset);

		proxy_data_root_directory_name			=f.directory_name+file_reader.separator(f.get_string());
		
		compress_data_root_directory_name		=f.directory_name+file_reader.separator(f.get_string());
		compress_proxy_data_root_directory_name	=f.directory_name+file_reader.separator(f.get_string());
		
		if(proxy_data_root_directory_name.charAt(proxy_data_root_directory_name.length()-1)!=File.separatorChar)
			proxy_data_root_directory_name+=File.separator;
		file_writer.make_directory(proxy_data_root_directory_name);
		
		if(compress_data_root_directory_name.charAt(compress_data_root_directory_name.length()-1)!=File.separatorChar)
			compress_data_root_directory_name+=File.separator;
		file_writer.make_directory(compress_data_root_directory_name);
		
		if(compress_proxy_data_root_directory_name.charAt(compress_proxy_data_root_directory_name.length()-1)!=File.separatorChar)
			compress_proxy_data_root_directory_name+=File.separator;
		file_writer.make_directory(compress_proxy_data_root_directory_name);
		
		f.close();
	}
}
