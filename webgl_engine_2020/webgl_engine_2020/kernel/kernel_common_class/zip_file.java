package kernel_common_class;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class zip_file 
{
	public static boolean unzip(String source_file_name,String target_directory_name)
	{
		ZipFile f=null;
		InputStream fis=null;
		file_writer fw=null;
		byte[] buf = new byte[8192];
		String target_file_name="";
		
		try {
			f=new ZipFile(source_file_name);
			Enumeration<? extends ZipEntry> entries = f.entries(); 
			while (entries.hasMoreElements()) { 
				ZipEntry entry = entries.nextElement(); 
				target_file_name=file_reader.separator(entry.getName());
				debug_information.println(target_file_name);
				target_file_name=target_directory_name+target_file_name;

				if(entry.isDirectory())
					new File(target_file_name).mkdir();
				else{
					fw=new file_writer(target_file_name,null);
					fis=f.getInputStream(entry);
					for(int length;(length=fis.read(buf,0,buf.length))>=0;)
						fw.write(buf,0,length);
					fis.close(); 
					fw.close();
				}
				new File(target_file_name).setLastModified(entry.getLastModifiedTime().toMillis());
			}
			f.close();
			return true;
		}catch(Exception e){
			debug_information.println("source_file_name:	",source_file_name);
			debug_information.println("target_file_name:	",target_file_name);
			debug_information.println("zip_file execption:	",e.toString());
			e.printStackTrace();
		}
		
		if(fw!=null)
			fw.close();
		
		if(fis!=null) {
			try {
				fis.close();
		    }catch(Exception e){
		    	;
			}
		}
		if(f!=null) {
			try {
				f.close();
		    }catch(Exception e){
		    	;
			}
		}
	    return false;
	}
	
	public static String try_unzip_file(String source_file_name)
	{
		int index_id;

		source_file_name=file_reader.separator(source_file_name);
		if((index_id=source_file_name.lastIndexOf('.'))<0)
			return source_file_name;
		if(source_file_name.substring(index_id).toLowerCase().compareTo(".zip")!=0)
			return source_file_name;
		
		String file_name_only=source_file_name.substring(0,index_id);
		if((index_id=file_name_only.lastIndexOf(File.separatorChar))>=0)
			file_name_only=file_name_only.substring(index_id+1);
		
		String target_directory_name=source_file_name+".unzip"+File.separator;
		File f=new File(target_directory_name);
		
		f.mkdir();
		
		if(unzip(source_file_name,target_directory_name))
			return target_directory_name+file_name_only;
		
		file_writer.file_delete(target_directory_name);
		
		return null;
	}
	public static String unzip_directory(
			String directory_name,exclusive_name_mutex system_exclusive_name_mutex)
	{
		directory_name=file_reader.separator(directory_name);
		for(int i=directory_name.length()-1;i>=0;i--)
			if(directory_name.charAt(i)!=File.separatorChar){
				directory_name=directory_name.substring(0,i+1);
				break;
			}
		String lock_file_name=directory_name+".lock";
		String zip_file_name=directory_name+".zip";
		File f=new File(zip_file_name);
		if(f.exists()){
			if(system_exclusive_name_mutex!=null)
				system_exclusive_name_mutex.lock(lock_file_name);
			try{
				if(f.exists()){
					directory_name+=File.separator;
					file_writer.file_delete(directory_name);
					if(zip_file.unzip(zip_file_name,directory_name))
						new File(zip_file_name).delete();
				}
			}catch(Exception e) {
				debug_information.println("directory_name:	",directory_name);
				debug_information.println("zip_file_name:	",zip_file_name);
				debug_information.println("unzip_directory execption:	",e.toString());
				e.printStackTrace();
			}
			if(system_exclusive_name_mutex!=null)
				system_exclusive_name_mutex.unlock(lock_file_name);
		}
		return lock_file_name;
	}
}
