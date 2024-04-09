package kernel_part;

import java.io.File;

import kernel_common_class.compress_file_data;
import kernel_file_manager.file_writer;

public class create_network_buffer_object_file 
{
	public static void create(int response_block_size,	String my_charset,String root_file_name)
		{
			String my_head_file_name		=root_file_name+".head.txt";
			String my_head_gzip_file_name	=root_file_name+".head.gzip_text";

			compress_file_data.do_compress(new File(my_head_file_name),
					new File(my_head_gzip_file_name),response_block_size,"gzip");
			file_writer.file_delete(my_head_file_name);
			String file_type[]=new String[]{".face",".edge",".point"};
			for(int i=0,ni=file_type.length;i<ni;i++){
				for(int j=0;;j++){
					String id_str=Integer.toString(j);
					String my_text_file_name=root_file_name+file_type[i]+id_str+".txt";
					String my_gzip_file_name=root_file_name+file_type[i]+id_str+".gzip_text";
					String my_flag_file_name=root_file_name+file_type[i]+id_str+".in_head_flag";
					if(!(new File(my_text_file_name).exists()))
						break;
					if(new File(my_flag_file_name).exists())
						file_writer.file_delete(my_flag_file_name);
					else{
						compress_file_data.do_compress(new File(my_text_file_name),
							new File(my_gzip_file_name),response_block_size,"gzip");
					}
					file_writer.file_delete(my_text_file_name);
				}
			}
		}
}
