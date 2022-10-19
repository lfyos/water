package format_convert;

import java.io.File;
import java.nio.charset.Charset;

import kernel_common_class.class_file_reader;
import kernel_common_class.common_reader;
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
		String class_charset=Charset.defaultCharset().name();
		common_reader f=class_file_reader.get_reader("cadex_parameter.txt",
				cadex_converter.class,class_charset,class_charset);
		class_charset=f.get_string();
		f.close();
		
		f=class_file_reader.get_reader("cadex_parameter.txt",
				cadex_converter.class,class_charset,class_charset);
		class_charset=f.get_string();
		String target_charset=f.get_string();
		if(target_charset==null)
			target_charset=Charset.defaultCharset().name();
		else if(target_charset.trim().compareTo("null")==0)
			target_charset=Charset.defaultCharset().name();
		String min_memory_size=f.get_string(),max_memory_size=f.get_string();
		String chordal_deflection_str=f.get_string(),angular_deflection_str=f.get_string();
		String max_step_number_str=f.get_string();
		f.close();
		
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

		debug_information.println("protected_cadex_converter memory_string:		",			min_memory_size+"/"+max_memory_size);
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
		String cmd[]=new String[]{
			"java","-Xms"+min_memory_size.trim()+"m","-Xmx"+max_memory_size.trim()+"m",
			"-classpath",jar_file_name,"format_convert.cadex_converter",
			source_file_name,target_directory_name,target_charset,
			class_charset,chordal_deflection_str,angular_deflection_str,max_step_number_str
		};
		for(int i=0,ni=cmd.length;i<ni;i++)
			debug_information.print(cmd[i]," ");
		debug_information.println();
		new exec_converter(source_file_name,cmd);
		
		if((new File(target_directory_name+"token.txt")).exists()){
			debug_information.println("Convert success,target directory: ",target_directory_name);
			return false;
		}
		file_writer.file_delete(target_directory_name);
		
		debug_information.println("Convert fail,Delete target directory: ",target_directory_name);
		return true;
	}
}
