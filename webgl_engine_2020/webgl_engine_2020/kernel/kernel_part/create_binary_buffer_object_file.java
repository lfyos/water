package kernel_part;

import java.io.File;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;

import java.nio.ByteBuffer;

import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_common_class.compress_file_data;
import kernel_common_class.debug_information;

public class create_binary_buffer_object_file 
{
	private static void convert(String source_file_name,String charset_name,String target_file_name)
	{
		FileOutputStream fos=null;
		BufferedOutputStream bos=null;
		DataOutputStream dos=null;

		file_reader fr=new file_reader(source_file_name,charset_name);
	
		ByteBuffer byte_buffer=ByteBuffer.allocate(4);
		
		File f=new File(target_file_name);
		f.delete();
		try{
			fos=new FileOutputStream(f);
			bos=new BufferedOutputStream(fos);
			dos=new DataOutputStream(bos);
			
			for(String str;!(fr.eof());)
				if((str=fr.get_string())!=null)
					if((str=str.trim()).length()>0){
						byte_buffer.clear();
						byte buffer_data[]=byte_buffer.putFloat(Float.valueOf(str)).array();
						dos.write(new byte[]{buffer_data[3],buffer_data[2],buffer_data[1],buffer_data[0]});
					}
		}catch(Exception e) {
			debug_information.println("transform_mesh_data_to_binary fail:	",e.toString());
			debug_information.println("file name is ",source_file_name);
			e.printStackTrace();
		}
		fr.close();
		
		if(dos!=null) {
			try{
				dos.close();
			}catch(Exception e) {
				;
			}
			dos=null;
		}
		if(bos!=null) {
			try{
				bos.close();
			}catch(Exception e) {
				;
			}
			bos=null;
		}
		if(fos!=null) {
			try{
				fos.close();
			}catch(Exception e) {
				;
			}
			fos=null;
		}
	}
	public static void create(
		boolean delete_buffer_object_text_file_flag,int response_block_size,
		String my_source_charset,String my_target_charset,String root_file_name)
	{
		String my_tmp_file_name			=root_file_name+".tmp";
		String my_head_file_name		=root_file_name+".head.txt";
		String my_head_gzip_file_name	=root_file_name+".head.gzip_text";
		
		file_writer.charset_copy(my_head_file_name,
				my_source_charset,my_tmp_file_name,my_target_charset);
		if(delete_buffer_object_text_file_flag)
			file_writer.file_delete(my_head_file_name);
		compress_file_data.do_compress(new File(my_tmp_file_name),
				new File(my_head_gzip_file_name),response_block_size,"gzip");
		String file_type[]=new String[]{".face",".edge",".point"};
		for(int i=0,ni=file_type.length;i<ni;i++){
			for(int j=0;;j++){
				String id_str=Integer.toString(j);
				String my_text_file_name=root_file_name+file_type[i]+id_str+".txt";
				String my_gzip_file_name=root_file_name+file_type[i]+id_str+".gzip_binary";
				String my_flag_file_name=root_file_name+file_type[i]+id_str+".in_head_flag";
				if(!(new File(my_text_file_name).exists()))
					break;
				if(!(new File(my_flag_file_name).exists())){
					convert(my_text_file_name,my_source_charset,my_tmp_file_name);
					compress_file_data.do_compress(new File(my_tmp_file_name),
						new File(my_gzip_file_name),response_block_size,"gzip");
				}
				if(delete_buffer_object_text_file_flag)
					file_writer.file_delete(my_text_file_name);
			}
		}
		file_writer.file_delete(my_tmp_file_name);
	}
}
