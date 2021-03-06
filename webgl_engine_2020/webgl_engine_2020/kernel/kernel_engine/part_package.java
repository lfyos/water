package kernel_engine;

import java.io.File;

import kernel_common_class.debug_information;
import kernel_common_class.compress_file_data;
import kernel_file_manager.file_directory;
import kernel_file_manager.file_writer;
import kernel_part.part;
import kernel_render.render_container;
import kernel_part.part_container_for_process_sequence;

public class part_package 
{
	public String	package_file_name[];
	public long		package_length[],package_last_time[];
	public boolean	package_flag[];
	
	public void destroy()
	{
		package_file_name	=null;
		package_length		=null;
		package_last_time	=null;
		package_flag		=null;
	}
	public part_package()
	{
		package_file_name	=new String[0];
		package_length		=new long[0];
		package_last_time	=new long[0];
		package_flag		=new boolean[0];
	}
	public part_package(part_package pp)
	{
		package_file_name=new String[pp.package_file_name.length];
		for(int i=0,ni=package_file_name.length;i<ni;i++)
			package_file_name[i]=new String(pp.package_file_name[i]);
		
		package_length=new long[pp.package_length.length];
		for(int i=0,ni=package_length.length;i<ni;i++)
			package_length[i]=pp.package_length[i];
		
		package_last_time=new long[pp.package_last_time.length];
		for(int i=0,ni=package_last_time.length;i<ni;i++)
			package_last_time[i]=pp.package_last_time[i];
		
		package_flag=new boolean[pp.package_flag.length];
		for(int i=0,ni=package_flag.length;i<ni;i++)
			package_flag[i]=pp.package_flag[i];
	}
	
	public part_package(render_container rc,
			int part_type_id,system_parameter system_par,scene_parameter scene_par)
	{
		class part_package_collector extends part_container_for_process_sequence
		{
			public int package_compare(part s,part t)
			{
				int ret_val;
				if((s.part_par.combine_to_part_package_flag)^(t.part_par.combine_to_part_package_flag))
					return s.part_par.combine_to_part_package_flag?-1:1;
				if((ret_val=s.part_type_id-t.part_type_id)!=0)
					return ret_val;
				if((ret_val=s.part_par.part_type_string.compareTo(t.part_par.part_type_string))!=0)
					return ret_val;
				if((ret_val=s.part_par.process_sequence_id-t.part_par.process_sequence_id)!=0)
					return ret_val;
				if((s.mesh_file_name==null)^(t.mesh_file_name==null))
					return (s.mesh_file_name==null)?-1:1;
				return 0;
			}
			public int compare_part(part pi,part pj)
			{
				int ret_val;
				if((ret_val=package_compare(pi,pj))==0)
					ret_val=super.compare_part(pi,pj);
				return ret_val;
			}
			private int caculate_part_package_id()
			{
				int package_number=0;
				long my_package_length=0;
				
				for(int i=0,ni=data_array.length;i<ni;i++) {
					if(!(data_array[i].part_par.combine_to_part_package_flag))
						continue;
					data_array[i].part_package_id=package_number;
					my_package_length+=data_array[i].boftal.buffer_object_head_length;
					if(my_package_length<system_par.max_buffer_object_head_package_length)
						if(i<(ni-1))
							if(package_compare(data_array[i+0],data_array[i+1])==0)
								continue;
					package_number++;
					my_package_length=0;
				}
				if(my_package_length>0)
					package_number++;
				
				for(int i=0,ni=data_array.length;i<ni;i++)
					if(!(data_array[i].part_par.combine_to_part_package_flag))
						data_array[i].part_package_id=package_number++;
				return package_number;
			}
			public part_package_collector(render_container rc)
			{
				super(rc.part_array(true,part_type_id));

				int package_number=caculate_part_package_id();
				part part_package[][]=new part[package_number][];

				for(int i=0;i<package_number;i++)
					part_package[i]=new part[0];
				
				for(int i=0,ni=data_array.length;i<ni;i++) {
					int part_package_id=data_array[i].part_package_id;
					part bak[]=part_package[part_package_id];
					part_package[part_package_id]=new part[bak.length+1];
					for(int j=0,nj=bak.length;j<nj;j++)
						part_package[part_package_id][j]=bak[j];
					part_package[part_package_id][bak.length]=data_array[i];
					data_array[i].part_package_sequence_id=bak.length;
				}

				package_length			=new long[package_number];
				package_last_time		=new long[package_number];
				package_file_name		=new String[package_number];
				package_flag			=new boolean[package_number];
				
				String package_directory_name=file_directory.system_package_directory(part_type_id,system_par,scene_par);
				system_par.system_exclusive_name_mutex.lock(package_directory_name+"package.lock");
				try {
					file_writer list_file_writer=null;
					for(int i=0,package_id=0;i<package_number;i++){
						if(part_package[i].length==1) {
							String my_package_file_name=file_directory.part_file_directory(
									part_package[i][0],system_par,scene_par)+"mesh.head.gzip_text";
							
							File f=new File(my_package_file_name);
							package_length[i]=f.length();
							package_last_time[i]=f.lastModified();
							package_file_name[i]=my_package_file_name;
							package_flag	 [i]=true;
							continue;
						}
						
						String my_tmp_file_name		=package_directory_name+"package_"+(package_id  )+".tmp";
						String my_package_file_name	=package_directory_name+"package_"+(package_id++)+".gzip_text";
						File f=new File(my_package_file_name);
	
						package_length[i]   =f.length();
						package_last_time[i]=f.lastModified();
						package_file_name[i]=my_package_file_name;
						package_flag	 [i]=false;
						
						for(int j=0,nj=part_package[i].length;j<nj;j++) {
							if(part_package[i][j].boftal.buffer_object_head_last_modify_time<=package_last_time[i])
								continue;
	
							if(list_file_writer==null)
								list_file_writer=new file_writer(
									package_directory_name+"package_list.txt",system_par.local_data_charset);
							
							list_file_writer.println (my_package_file_name);
							debug_information.println("Create part package:	",my_package_file_name);
							
							file_writer fw=new file_writer(my_package_file_name,system_par.network_data_charset);
							fw.println("[");
	
							for(int k=0,nk=part_package[i].length;k<nk;k++) {
								debug_information.println("Part type:	",		part_package[i][k].part_par.part_type_string);
								debug_information.println("Part user_name:	",	part_package[i][k].user_name);
								if(part_package[i][k].mesh_file_name==null)
									debug_information.println("Part mesh_file_name:	NO mesh_file_name",
											 ",permanent_render_id:"+part_package[i][k].permanent_render_id
											+",permanent_part_id:"	+part_package[i][k].permanent_part_id);
								else
									debug_information.println("part mesh_file_name:	",
											part_package[i][k].directory_name+part_package[i][k].mesh_file_name);
								
								String my_file_name=file_directory.part_file_directory(
										part_package[i][k],system_par,scene_par)+"mesh.head.gzip_text";
								compress_file_data.do_uncompress(new File(my_tmp_file_name),
									new File(my_file_name),system_par.response_block_size,"gzip");
								fw.print_file(my_tmp_file_name).println((k<(nk-1))?",":"");
								long my_file_length=new File(my_tmp_file_name).length();
	
								list_file_writer.print  ("		",part_package[i][k].part_par.part_type_string);
								list_file_writer.print  ("		",part_package[i][k].user_name);
								list_file_writer.print  ("		",my_file_length);
								list_file_writer.println("		",my_file_name);
	
							}
							
							fw.println("]");
							fw.close();
							
							compress_file_data.do_compress(
								new File(my_package_file_name),new File(my_tmp_file_name),
								system_par.response_block_size,"gzip");
							file_writer.file_rename(my_tmp_file_name,my_package_file_name);
							
							f=new File(my_package_file_name);
							package_length[i]=f.length();
							package_last_time[i]=f.lastModified();
							
							list_file_writer.println();
							list_file_writer.print  ("		Total files:	",	part_package[i].length);
							list_file_writer.println(",		Total length:	",	package_length[i]);
							list_file_writer.println();
	
							break;
						}
					}
					if(list_file_writer!=null)
						list_file_writer.close();
				}catch(Exception e){
					debug_information.println(
						"part_package_collector exception:\t",
						package_directory_name+"\t"+e.toString());
					e.printStackTrace();
				}
				system_par.system_exclusive_name_mutex.unlock(package_directory_name+"package.lock");
			}
		}
		
		debug_information.println("Begin create part package");
		new part_package_collector(rc);
		debug_information.println("End create part package");
		
		return;
	}
}