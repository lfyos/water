package kernel_engine;

import java.io.File;
import java.util.ArrayList;

import kernel_common_class.debug_information;
import kernel_common_class.exclusive_file_mutex;
import kernel_common_class.compress_file_data;
import kernel_file_manager.file_directory;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_interface.client_process_bar;
import kernel_part.part;
import kernel_render.render_container;
import kernel_part.part_container_for_process_sequence;

public class part_package 
{
	public String	package_file_name[];
	public long		package_length[],package_last_time[];
	
	public void destroy()
	{
		package_file_name	=null;
		package_length		=null;
		package_last_time	=null;
	}
	public part_package()
	{
		package_file_name	=new String[0];
		package_length		=new long[0];
		package_last_time	=new long[0];
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
	}
	public part_package(
			client_process_bar process_bar,
			String package_process_bar_title,
			String boftal_process_bar_title,
			render_container rc,int part_type_id,
			system_parameter system_par,scene_parameter scene_par)
	{
		class part_arraylist
		{
			public ArrayList<part> list;
			public long last_time;
			public part_arraylist()
			{
				list=new ArrayList<part>();
				last_time=0;
			}
		};
		class part_package_collector extends part_container_for_process_sequence
		{
			public part_arraylist part_package[];
			
			public int package_compare(part s,part t)
			{
				int ret_val;
				if((ret_val=s.part_type_id-t.part_type_id)!=0)
					return ret_val;
				if((ret_val=s.part_par.process_sequence_id-t.part_par.process_sequence_id)!=0)
					return ret_val;
				if((ret_val=s.part_par.part_type_string.compareTo(t.part_par.part_type_string))!=0)
					return ret_val;
				return 0;
			}
			public int compare_part(part pi,part pj)
			{
				int ret_val;
				if((ret_val=package_compare(pi,pj))!=0)
					return ret_val;
				else
					return super.compare_part(pi,pj);
			}
			public int compare_key(part pi,part pj)
			{
				return compare_part(pi,pj);
			}
			public part_package_collector(ArrayList<part> my_part_list,system_parameter system_par)
			{
				super(my_part_list.toArray(new part[my_part_list.size()]));
				
				int package_number=0;
				long my_package_length=0;
				
				for(int i=0,ni=data_array.length;i<ni;i++){
					data_array[i].part_package_id=package_number;
					if(data_array[i].boftal==null)
						debug_information.println("Find null boftal:	",
								data_array[i].system_name+"	"+data_array[i].directory_name+data_array[i].mesh_file_name);
					else
						my_package_length+=data_array[i].boftal.buffer_object_head_length;
					if((i<(ni-1))&&(my_package_length<system_par.max_buffer_object_head_package_length))
						if(package_compare(data_array[i+0],data_array[i+1])==0)
							continue;
					package_number++;
					my_package_length=0;
				}

				part_package=new part_arraylist[package_number];
				for(int i=0;i<package_number;i++)
					part_package[i]=new part_arraylist();

				for(int i=0,ni=data_array.length;i<ni;i++){
					int part_package_id=data_array[i].part_package_id;
					int part_package_sequence_id=part_package[part_package_id].list.size();
					part_package[part_package_id].list.add(part_package_sequence_id,data_array[i]);
					data_array[i].part_package_sequence_id=part_package_sequence_id;
					long my_last_time=data_array[i].boftal.buffer_object_head_last_modify_time;
					if(part_package[part_package_id].last_time<my_last_time)
						part_package[part_package_id].last_time=my_last_time;
				}
			}
		};

		debug_information.println("Begin create part package");
		
		part_package_collector ppc=new part_package_collector(rc.part_array_list(part_type_id),system_par);
		
		int package_number=ppc.part_package.length;

		package_length	 =new long	 [package_number];
		package_last_time=new long	 [package_number];
		package_file_name=new String [package_number];
				
		String package_directory_name=file_directory.package_file_directory(part_type_id,system_par,scene_par);
		String package_data_file_name=package_directory_name+"package_data.txt";
		String boftal_data_file_name =package_directory_name+"boftal_data.txt";
		
		File package_data_f			=new File(package_data_file_name);
		File boftal_data_f			=new File(boftal_data_file_name);
		long package_data_last_time	=package_data_f.lastModified();
		
		if((package_data_f.exists())&&(boftal_data_f.exists())){
			boolean not_create_flag=true;
			file_reader fr=new file_reader(package_data_file_name,system_par.local_data_charset);
			for(int i=0;i<package_number;i++) {
				String my_package_file_name=package_directory_name+"package_"+i+".gzip_text";
				File f=new File(my_package_file_name);
				if(	(!(f.exists()))
					||(f.lastModified()>=package_data_last_time)
					||(ppc.part_package[i].last_time>=package_data_last_time))
				{
					not_create_flag=false;
					break;
				}
				package_length[i]	=fr.get_long();
				package_last_time[i]=fr.get_long();
				package_file_name[i]=my_package_file_name;
			}
			fr.close();
			if(not_create_flag) 
				return;
		}
		
		exclusive_file_mutex efm=exclusive_file_mutex.lock(
			package_directory_name+"package.lock",
			"wait for create scene package:	"+package_directory_name);
		
		for(int i=0;i<package_number;i++){
			if(process_bar!=null)
				process_bar.set_process_bar((i<=0),package_process_bar_title,"",i,package_number);
			
			String my_tmp_file_name		=package_directory_name+"package_"+i+".tmp";
			String my_package_file_name	=package_directory_name+"package_"+i+".gzip_text";
			File f=new File(my_package_file_name);
	
			package_length[i]   =f.length();
			package_last_time[i]=f.lastModified();
			package_file_name[i]=my_package_file_name;
			
			if(f.exists())
				if(package_last_time[i]>ppc.part_package[i].last_time)
					continue;
				
			debug_information.println("Create part package:	",my_package_file_name);
	
			file_writer fw=new file_writer(my_package_file_name,system_par.network_data_charset);
			fw.println("[");
	
			for(int j=0,nj=ppc.part_package[i].list.size();j<nj;j++) {
				part p=ppc.part_package[i].list.get(j);
				debug_information.print  ("part user_name:	",	p.user_name);
				debug_information.print  ("		part type:	",	p.part_par.part_type_string);
				if(p.is_normal_part())
					debug_information.println("		part mesh_file_name:	",p.directory_name+p.mesh_file_name);
				else 
					debug_information.println(
						p.is_bottom_box_part()?"		Bottom box part":"		Top box part",
						",permanent_part_id:"	+p.permanent_part_id);
				String my_file_name=file_directory.part_file_directory(p,system_par,scene_par)+"mesh.head.gzip_text";
				compress_file_data.do_uncompress(new File(my_tmp_file_name),
						new File(my_file_name),system_par.response_block_size,"gzip");
				fw.print_file(my_tmp_file_name).println((j<(nj-1))?",":"");
			}

			fw.println("]");
			fw.close();
						
			compress_file_data.do_compress(new File(my_package_file_name),
					new File(my_tmp_file_name),system_par.response_block_size,"gzip");
			file_writer.file_rename(my_tmp_file_name,my_package_file_name);
							
			f=new File(my_package_file_name);
			package_length[i]	=f.length();
			package_last_time[i]=f.lastModified();
		}

		if(process_bar!=null)
			process_bar.set_process_bar(false,package_process_bar_title,"",package_number,package_number);

		boolean do_create_flag;
		if((!(package_data_f.exists()))||(!(boftal_data_f.exists())))
			do_create_flag=true;
		else {
			do_create_flag=false;
			package_data_last_time=package_data_f.lastModified();
			for(int i=0;i<package_number;i++)
				if(package_last_time[i]>=package_data_last_time) {
					do_create_flag=true;
					break;
				}
		}
		if(do_create_flag) {
			new part_boftal_creator(
					part_type_id,boftal_data_file_name,system_par.local_data_charset,
					ppc.data_array,system_par,scene_par,process_bar,boftal_process_bar_title);
			
			file_writer fw=new file_writer(package_data_file_name,system_par.local_data_charset);
			for(int i=0;i<package_number;i++)
				fw.	println("/*	"+i+".package_length	*/	",package_length[i]).
					println("/*	"+i+".package_last_time	*/	",package_last_time[i]).
					println();
			fw.close();
		}

		efm.unlock();
		
		debug_information.println("End create part package");
		
		return;
	}
}