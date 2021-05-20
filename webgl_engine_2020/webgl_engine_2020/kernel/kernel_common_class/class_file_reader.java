package kernel_common_class;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import kernel_file_manager.file_reader;
import kernel_jar_charset.jar_charset;

public class class_file_reader
{
	private static common_reader get_reader(String file_name,
			String jar_file_charset,String webpage_charset,Class<?>first_class)
	{
		for(Class<?> my_class=first_class;my_class!=null;my_class=my_class.getSuperclass()){
			URL url;
			if((url=my_class.getResource(""))==null)
				continue;
			URI uri;
			try{
				uri=url.toURI();
			}catch(Exception e){
				continue;
			}
			String class_directory_name=uri.getPath();
			if(class_directory_name==null)
				continue;
			String my_file_name=class_directory_name+file_name;
			File f=new File(my_file_name=file_reader.separator(my_file_name));
			if(!(f.exists()))
				continue;
			common_reader ret_val=new file_reader(my_file_name,webpage_charset);
			return ret_val.error_flag()?null:ret_val;
		}
		for(Class<?> my_class=first_class;my_class!=null;my_class=my_class.getSuperclass()){
			InputStream stream; 
			if((stream=my_class.getResourceAsStream(file_name))==null)
				continue;
			String path=my_class.getProtectionDomain().getCodeSource().getLocation().getPath();
			try{
				path=java.net.URLDecoder.decode(path,jar_file_charset);
			}catch(Exception e){
				continue;
			}
			common_reader ret_val=new common_reader(stream,webpage_charset);
			path=file_reader.separator(path);
			ret_val.lastModified_time=(new File(path)).lastModified();
			return ret_val.error_flag()?null:ret_val;
		}
		return null;
	}
	public static common_reader get_reader(String file_name,Class<?>first_class)
	{
		jar_charset my_jar_charset=new jar_charset();
		
		common_reader charset_reader=get_reader(my_jar_charset.file_name,
			my_jar_charset.initial_get_jar_file_name_charset,
			my_jar_charset.initial_webpage_charset,jar_charset.class);
		
		if(charset_reader!=null){
			my_jar_charset.initial_get_jar_file_name_charset=charset_reader.get_string();
			my_jar_charset.initial_webpage_charset			=charset_reader.get_string();
			charset_reader.close();
		}
		
		return get_reader(file_name,
			my_jar_charset.initial_get_jar_file_name_charset,
			my_jar_charset.initial_webpage_charset,first_class);
	}
}
