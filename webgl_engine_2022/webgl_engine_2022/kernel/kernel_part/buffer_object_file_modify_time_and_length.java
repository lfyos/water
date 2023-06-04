package kernel_part;

import java.io.File;
import java.util.Date;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

import kernel_file_manager.file_reader; 
import kernel_file_manager.file_writer; 

public class buffer_object_file_modify_time_and_length 
{
	public long buffer_object_head_last_modify_time;
	public long	buffer_object_head_length,buffer_object_total_file_length;
	
	public ArrayList<ArrayList<buffer_object_file_modify_time_and_length_item>> list;
	
	public part_rude simple_part_mesh;
	
	public buffer_object_file_modify_time_and_length()
	{
		buffer_object_head_last_modify_time	=0;
		buffer_object_head_length			=0;
		buffer_object_total_file_length		=0;
		
		list=new ArrayList<ArrayList<buffer_object_file_modify_time_and_length_item>>();
		simple_part_mesh=null;
	}
	public buffer_object_file_modify_time_and_length(file_reader fr)
	{
		ArrayList<buffer_object_file_modify_time_and_length_item> pp;
		buffer_object_file_modify_time_and_length_item p;
		
		buffer_object_head_last_modify_time	=fr.get_long();
		buffer_object_head_length			=fr.get_long();
		buffer_object_total_file_length		=buffer_object_head_length;
		
		list=new ArrayList<ArrayList<buffer_object_file_modify_time_and_length_item>>();
		for(int i=0,ni=fr.get_int();i<ni;i++) {
			pp=new ArrayList<buffer_object_file_modify_time_and_length_item>();
			list.add(i,pp);
			for(int j=0,nj=fr.get_int();j<nj;j++){
				p=new buffer_object_file_modify_time_and_length_item(
						fr.get_long(),fr.get_long(),fr.get_boolean());
				pp.add(pp.size(),p);
				buffer_object_total_file_length+=(p.buffer_object_file_in_head_flag)?0:(p.buffer_object_text_file_length);
			}
		}
		simple_part_mesh=new part_rude(fr);

		return;
	}
	
	public buffer_object_file_modify_time_and_length(
		part_rude pr,String root_file_name,String file_charset)
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss:SSS");
		
		simple_part_mesh=null;
		list=new ArrayList<ArrayList<buffer_object_file_modify_time_and_length_item>>();
		list.add(0,new ArrayList<buffer_object_file_modify_time_and_length_item>());		//face
		list.add(1,new ArrayList<buffer_object_file_modify_time_and_length_item>());		//edge
		list.add(2,new ArrayList<buffer_object_file_modify_time_and_length_item>());		//point
		
		File f=new File(root_file_name+".head.txt");
		buffer_object_head_last_modify_time	=f.lastModified();
		buffer_object_head_length			=f.length();
		buffer_object_total_file_length		=buffer_object_head_length;
		
		ArrayList<buffer_object_file_modify_time_and_length_item> pp;
		buffer_object_file_modify_time_and_length_item p;
		
		String file_type[]=new String[]{".face",".edge",".point"};
		for(int i=0,ni=file_type.length;i<ni;i++){
			pp=list.get(i);
			for(long j=0;;j++){
				String my_file_name=root_file_name+file_type[i]+Long.toString(j)+".txt";
				if(!((f=new File(my_file_name)).exists()))
					break;
				if(!(f.isFile()))
					break;
				if(f.length()<=0)
					break;
				p=new buffer_object_file_modify_time_and_length_item(f.lastModified(),f.length(),
						new File(root_file_name+file_type[i]+Long.toString(j)+".in_head_flag").exists());
				pp.add(pp.size(),p);
				buffer_object_total_file_length+=(p.buffer_object_file_in_head_flag)?0:(p.buffer_object_text_file_length);
			}
		}
		
		file_writer fw=new file_writer(root_file_name+".boftal",file_charset);
		
		fw.println("/*\tpart mesh file length information\t\t*/");
		fw.println();
		
		fw.print  ("/*\tbuffer_object_head_last_modify_time\t\t*/\t",buffer_object_head_last_modify_time);
		fw.print  ("\t/*\t",sdf.format(new Date(buffer_object_head_last_modify_time)));
		fw.println("\t*/");
		
		fw.println("/*\tbuffer_object_head_length\t\t\t\t*/\t",buffer_object_head_length);

		fw.println("/*\tbuffer_object_text_file_length.length\t*/\t",list.size());
		for(int i=0,ni=list.size();i<ni;i++){
			pp=list.get(i);
			fw.println("/*\t\tbuffer_object_text_file_length["+i+"]\t*/\t",pp.size());
			for(int j=0,nj=pp.size();j<nj;j++){
				p=pp.get(j);
				long t=p.buffer_object_file_last_modify_time;
				fw.print  ("/*\t\t\tbuffer_object_file_last_modify_time\t["+i+","+j+"]\t\t*/\t",t);
				fw.print  ("\t/*\t",sdf.format(new Date(t)));
				fw.println("\t*/");

				fw.println("/*\t\t\tbuffer_object_text_file_length\t\t["+i+","+j+"]\t\t*/\t",
					p.buffer_object_text_file_length);
				fw.println("/*\t\t\tbuffer_object_file_in_head_flag\t\t["+i+","+j+"]\t\t*/\t",
					p.buffer_object_file_in_head_flag?"true":"false");
			}
		}

		fw.println();
		fw.println();
		
		if(pr!=null)
			pr.write_out_to_simple_file(fw);
		else{
			fw.println("/*	version:part_mesh==null				*/	simple");
			fw.println("/*	origin material						*/	0	0	0	0");
			fw.println("/*	default material					*/	0	0	0	0");
			fw.println("/*	origin  vertex_location_extra_data	*/	1");
			fw.println("/*	default vertex_location_extra_data	*/	1");
			fw.println("/*	default vertex_normal_extra_data	*/	1");
			fw.println("/*	max_attribute_number				*/	0");
			fw.println("/*	part_box							*/	nobox");
			fw.println("/*	total_face_primitive_number			*/	0");
			fw.println("/*	total_edge_primitive_number			*/	0");
			fw.println("/*	total_point_primitive_number		*/	0");
			fw.println();
		}
		
		fw.close();
	}
}
