package kernel_part;

import java.io.File;
import kernel_file_manager.file_reader; 
import kernel_file_manager.file_writer; 

public class buffer_object_file_modify_time_and_length 
{
	public long buffer_object_head_last_modify_time;
	public long	buffer_object_head_length,buffer_object_total_file_length;
	
	public long	buffer_object_file_last_modify_time[][];
	
	public long	buffer_object_text_file_length[][];
	public boolean buffer_object_file_in_head_flag[][];
	
	public void write_out(file_writer fw)
	{
		fw.println("/*\tpart mesh file length information\t\t*/");
		fw.println();
		
		fw.println("/*\tbuffer_object_head_last_modify_time\t\t*/\t",
			buffer_object_head_last_modify_time);
		fw.println("/*\tbuffer_object_head_length\t\t\t\t*/\t",
			buffer_object_head_length);
		fw.println("/*\tbuffer_object_text_file_length.length\t*/\t",
			buffer_object_text_file_length.length);
		for(int i=0,ni=buffer_object_text_file_length.length;i<ni;i++){
			fw.println("/*\t\tbuffer_object_text_file_length["+i+"]\t*/\t",
				buffer_object_text_file_length[i].length);
			for(int j=0,nj=buffer_object_text_file_length[i].length;j<nj;j++){
				fw.println("/*\t\t\tbuffer_object_file_last_modify_time\t["+i+","+j+"]\t\t*/\t",
					buffer_object_file_last_modify_time[i][j]);
				fw.println("/*\t\t\tbuffer_object_text_file_length\t\t["+i+","+j+"]\t\t*/\t",
					buffer_object_text_file_length[i][j]);
				fw.println("/*\t\t\tbuffer_object_file_in_head_flag\t\t["+i+","+j+"]\t\t*/\t",
					buffer_object_file_in_head_flag[i][j]?"true":"false");
			}
		}
		fw.println();
	}
	public buffer_object_file_modify_time_and_length(file_reader fr)
	{
		buffer_object_head_last_modify_time	=fr.get_long();
		buffer_object_head_length			=fr.get_long();
		buffer_object_total_file_length		=buffer_object_head_length;
		
		buffer_object_text_file_length		=new long[fr.get_int()][];
		buffer_object_file_last_modify_time	=new long[buffer_object_text_file_length.length][];
		buffer_object_file_in_head_flag		=new boolean[buffer_object_text_file_length.length][];
		
		for(int i=0,ni=buffer_object_text_file_length.length;i<ni;i++){
			buffer_object_text_file_length[i]		=new long[fr.get_int()];
			buffer_object_file_last_modify_time[i]	=new long[buffer_object_text_file_length[i].length];
			buffer_object_file_in_head_flag[i]		=new boolean[buffer_object_text_file_length[i].length];
			for(int j=0,nj=buffer_object_text_file_length[i].length;j<nj;j++){
				buffer_object_file_last_modify_time[i][j]	=fr.get_long();
				buffer_object_text_file_length[i][j]		=fr.get_long();
				buffer_object_file_in_head_flag[i][j]		=fr.get_boolean();
				if(!(buffer_object_file_in_head_flag[i][j]))
					buffer_object_total_file_length+=buffer_object_text_file_length[i][j];
			}
		}
	}

	public buffer_object_file_modify_time_and_length(String root_file_name)
	{
		File f=new File(root_file_name+".head.txt");
		buffer_object_head_last_modify_time	=f.lastModified();
		buffer_object_head_length			=f.length();
		buffer_object_total_file_length		=buffer_object_head_length;
		
		String file_type[]=new String[]{".face",".edge",".point"};
		buffer_object_file_last_modify_time	=new long[file_type.length][];
		buffer_object_text_file_length		=new long[file_type.length][];
		buffer_object_file_in_head_flag		=new boolean[file_type.length][];
		
		for(int i=0,ni=file_type.length;i<ni;i++){
			buffer_object_file_last_modify_time[i]	=new long[100];
			buffer_object_text_file_length[i]		=new long[100];
			buffer_object_file_in_head_flag[i]		=new boolean[100];
			
			int file_number=0;
			
			for(long j=0;;j++){
				String my_file_name=root_file_name+file_type[i]+Long.toString(j)+".txt";
				if(!((f=new File(my_file_name)).exists()))
					break;
				if(!(f.isFile()))
					break;
				if(f.length()<=0)
					break;
				
				if(file_number>=buffer_object_text_file_length[i].length) {
					long bak[]=buffer_object_text_file_length[i];
					buffer_object_text_file_length[i]=new long[bak.length+100];
					for(int k=0,nk=bak.length;k<nk;k++)
						buffer_object_text_file_length[i][k]=bak[k];
	
					bak=buffer_object_file_last_modify_time[i];
					buffer_object_file_last_modify_time[i]=new long[bak.length+100];
					for(int k=0,nk=bak.length;k<nk;k++)
						buffer_object_file_last_modify_time[i][k]=bak[k];
	
					boolean flag_bak[]=buffer_object_file_in_head_flag[i];
					buffer_object_file_in_head_flag[i]=new boolean[flag_bak.length+100];
					for(int k=0,nk=flag_bak.length;k<nk;k++)
						buffer_object_file_in_head_flag[i][k]=flag_bak[k];
				}
		
				buffer_object_text_file_length[i][file_number]=f.length();
				buffer_object_file_last_modify_time[i][file_number]=f.lastModified();

				my_file_name=root_file_name+file_type[i]+Long.toString(j)+".in_head_flag";
				buffer_object_file_in_head_flag[i][file_number]=new File(my_file_name).exists();
				
				if(!(buffer_object_file_in_head_flag[i][file_number]))
					buffer_object_total_file_length+=buffer_object_text_file_length[i][file_number];
				file_number++;
			}
			
			if(buffer_object_file_last_modify_time[i].length!=file_number) {
				long bak[]=buffer_object_file_last_modify_time[i];
				buffer_object_file_last_modify_time[i]=new long[file_number];
				for(int j=0;j<file_number;j++)
					buffer_object_file_last_modify_time[i][j]=bak[j];
				
				bak=buffer_object_text_file_length[i];
				buffer_object_text_file_length[i]=new long[file_number];
				for(int j=0;j<file_number;j++)
					buffer_object_text_file_length[i][j]=bak[j];
				
				boolean bak_flag[]=buffer_object_file_in_head_flag[i];
				buffer_object_file_in_head_flag[i]=new boolean[file_number];
				for(int j=0;j<file_number;j++)
					buffer_object_file_in_head_flag[i][j]=bak_flag[j];
			}
		}
	}
}
