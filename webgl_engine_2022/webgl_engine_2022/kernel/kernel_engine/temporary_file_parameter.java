package kernel_engine;

import java.io.File;

import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class temporary_file_parameter 
{
	public String temporary_root_directory_name;
	public String compress_data_root_directory_name;
	public String compress_temporary_root_directory_name;
	
	public temporary_file_parameter(String temporary_file_configure_file_name,String temporary_file_configure_file_charset)
	{
		if(file_reader.is_not_exist(temporary_file_configure_file_name)){
			file_writer fw=new file_writer(temporary_file_configure_file_name,temporary_file_configure_file_charset);
			
			fw.println("temporary_root_directory");
			fw.println("compress_data_root_directory");
			fw.println("compress_temporary_root_directory");
			fw.println();
			fw.close();
		}
		file_reader f=new file_reader(temporary_file_configure_file_name,temporary_file_configure_file_charset);

		temporary_root_directory_name			=f.directory_name+file_reader.separator(f.get_string());
		
		compress_data_root_directory_name		=f.directory_name+file_reader.separator(f.get_string());
		compress_temporary_root_directory_name	=f.directory_name+file_reader.separator(f.get_string());
		
		if(temporary_root_directory_name.charAt(temporary_root_directory_name.length()-1)!=File.separatorChar)
			temporary_root_directory_name+=File.separator;
		file_writer.make_directory(temporary_root_directory_name);
		
		if(compress_data_root_directory_name.charAt(compress_data_root_directory_name.length()-1)!=File.separatorChar)
			compress_data_root_directory_name+=File.separator;
		file_writer.make_directory(compress_data_root_directory_name);
		
		if(compress_temporary_root_directory_name.charAt(compress_temporary_root_directory_name.length()-1)!=File.separatorChar)
			compress_temporary_root_directory_name+=File.separator;
		file_writer.make_directory(compress_temporary_root_directory_name);
		
		f.close();
	}
}
