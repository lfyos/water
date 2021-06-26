package format_convert;

import java.io.File;
import java.nio.charset.Charset;

import kernel_common_class.debug_information;
import kernel_common_class.execute_command;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class protected_cadex_converter
{
	public static void main(String args[])
	{
		debug_information.println("Begin");
		do_convert(args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[6]);
		debug_information.println("End");
	}
	public static boolean do_convert(
			String source_file_name,String target_directory_name,String jar_file_name,
			String max_convert_time_length_str,String memory_string,String max_step_number,
			String precision,String external_flag)
	{
		debug_information.println("protected_cadex_converter source_file_name:		",		source_file_name);
		debug_information.println("protected_cadex_converter target_directory_name:	",		target_directory_name);
		debug_information.println("protected_cadex_converter jar_file_name:		",			jar_file_name);
		debug_information.println("protected_cadex_converter max_convert_time_length:	",	max_convert_time_length_str);
		debug_information.println("protected_cadex_converter memory_string:		",			memory_string);
		
		int index_id,max_convert_time_length=Integer.decode(max_convert_time_length_str);
		
		String source_directory_name;
		source_file_name=file_reader.separator(source_file_name);
		if((index_id=source_file_name.lastIndexOf(File.separator))<0)
			source_directory_name="."+File.separator;
		else {
			source_directory_name=source_file_name.substring(0,index_id+1);
			source_file_name=source_file_name.substring(index_id+1);
		}
		
		String config_directory_name;
		jar_file_name=file_reader.separator(jar_file_name);
		if((index_id=jar_file_name.lastIndexOf(File.separator))<0)
			config_directory_name="."+File.separator;
		else 
			config_directory_name=jar_file_name.substring(0,index_id+1);

		target_directory_name=file_reader.separator(target_directory_name);
		if(target_directory_name.charAt(target_directory_name.length()-1)!=File.separatorChar)
			target_directory_name+=File.separator;
		
		file_writer.make_directory(target_directory_name);
		file_writer.file_delete(target_directory_name+"token.txt");

		class exec_converter extends execute_command
		{
			public void save_result(String result)
			{
				debug_information.println(result);
			}
			public exec_converter(String name,String cmd[],String par[])
			{
				super(name,cmd,par,max_convert_time_length);
			}
		};

		new exec_converter(source_file_name,new String[]
				{
					"java","-Xms256m","-Xmx"+memory_string.trim()+"m",
					"-classpath",jar_file_name,"format_convert.cadex_converter",

					source_directory_name,source_file_name,target_directory_name,
					config_directory_name,Charset.defaultCharset().name(),
					precision,precision,max_step_number,external_flag
				},null);
		if((new File(target_directory_name+"token.txt")).exists()){
			debug_information.println("Convert success,target directory: ",target_directory_name);
			return false;
		}
		file_writer.file_delete(target_directory_name);
		
		debug_information.println("Convert fail,Delete target directory: ",target_directory_name);
		return true;
	}
}
