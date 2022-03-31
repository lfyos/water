package kernel_common_class;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import kernel_file_manager.file_reader;

public class class_file_reader
{
	public static common_reader get_reader(String file_name,
			Class<?>first_class,String class_charset,String jar_file_charset)
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
			common_reader ret_val=new file_reader(my_file_name,class_charset);
			if(ret_val.error_flag()) {
				ret_val.close();
				return null;
			}
			return ret_val;
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
			common_reader ret_val=new common_reader(stream,class_charset);
			path=file_reader.separator(path);
			ret_val.lastModified_time=(new File(path)).lastModified();
			return ret_val.error_flag()?null:ret_val;
		}
		return null;
	}
}
