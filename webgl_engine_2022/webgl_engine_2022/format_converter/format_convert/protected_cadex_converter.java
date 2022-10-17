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
		do_convert(args[0],args[1],args[2]);
		debug_information.println("End");
	}
	public static boolean do_convert(String source_file_name,
			String target_directory_name,String max_convert_time_length_str)
	{
		return do_convert("1024",max_convert_time_length_str,
				source_file_name,target_directory_name,Charset.defaultCharset().name(),"0","0","100");
	}
	public static boolean do_convert(String memory_string,String max_convert_time_length_str,
			String source_file_name,String target_directory_name,String target_charset,
			String chordal_deflection_str,String angular_deflection_str,String max_step_number_str)
	{
		String jar_file_name="";
		try{
			jar_file_name=new cadex_converter().getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
			jar_file_name=java.net.URLDecoder.decode(jar_file_name,"UTF-8");
			if((jar_file_name.charAt(0)=='/')||(jar_file_name.charAt(0)=='\\'))
				if(	  ((jar_file_name.charAt(1)>='A')&&(jar_file_name.charAt(1)<='Z'))
					||((jar_file_name.charAt(1)>='a')&&(jar_file_name.charAt(1)<='z')))
					if(jar_file_name.charAt(2)==':')
						jar_file_name=jar_file_name.substring(1);
	    } catch (Exception e) {
	        ;
	    }

		debug_information.println("protected_cadex_converter memory_string:		",			memory_string);
		debug_information.println("protected_cadex_converter max_convert_time_length:	",	max_convert_time_length_str);
		debug_information.println("protected_cadex_converter source_file_name:		",		source_file_name);
		debug_information.println("protected_cadex_converter target_directory_name:	",		target_directory_name);
		debug_information.println("protected_cadex_converter jar_file_name:		",			jar_file_name);
		debug_information.println("protected_cadex_converter chordal_deflection:	",		chordal_deflection_str);
		debug_information.println("protected_cadex_converter angular_deflection:	",		angular_deflection_str);
		debug_information.println("protected_cadex_converter max_step_number:	",			max_step_number_str);

		jar_file_name=file_reader.separator(jar_file_name);

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
			public exec_converter(String name,String cmd[])
			{
				super(name,cmd,null,Long.decode(max_convert_time_length_str));
			}
		};
		
		new exec_converter(source_file_name,
				new String[]
				{
					"java","-Xms256m","-Xmx"+memory_string.trim()+"m",
					"-classpath",jar_file_name,"format_convert.cadex_converter",

					source_file_name,target_directory_name,target_charset,
					"UTF-8",chordal_deflection_str,angular_deflection_str,max_step_number_str
				});
		
		if((new File(target_directory_name+"token.txt")).exists()){
			debug_information.println("Convert success,target directory: ",target_directory_name);
			return false;
		}
		file_writer.file_delete(target_directory_name);
		
		debug_information.println("Convert fail,Delete target directory: ",target_directory_name);
		return true;
	}
}
