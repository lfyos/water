package kernel_scene;

import java.io.File;
import java.util.ArrayList;

import kernel_part.part;
import kernel_render.render_container;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_file_manager.file_directory;
import kernel_interface.client_process_bar;
import kernel_common_class.debug_information;
import kernel_common_class.compress_file_data;
import kernel_common_class.tree_search_container;
import kernel_common_class.tree_string_locker_container;
import kernel_part.comparator_for_part_container_for_process_sequence;

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
class comparator_for_part_package_collector extends comparator_for_part_container_for_process_sequence
{
	public static int package_compare(part s,part t)
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
	public int compare(part pi,part pj)
	{
		int ret_val;
		if((ret_val=package_compare(pi,pj))!=0)
			return ret_val;
		else
			return super.compare(pi,pj);
	}
	public comparator_for_part_package_collector(
			double my_box_distance_difference_scale,
			double my_buffer_data_length_difference_scale)
	{
		super(my_box_distance_difference_scale,my_buffer_data_length_difference_scale);
	}
}
class part_package_collector extends tree_search_container<part,part>
{
	public ArrayList<part_arraylist> part_package;
	
	public part_package_collector(ArrayList<part> my_part_list,system_parameter system_par)
	{
		super(new comparator_for_part_package_collector(
				system_par.box_distance_difference_scale,
				system_par.buffer_data_length_difference_scale));
		
		if(my_part_list!=null)
			for(var my_part:my_part_list)
				add(my_part,my_part,false);
		
		int package_number=0;
		long my_package_length=0;
		var data_list=tree_get_value_list();
		part_package=new ArrayList<part_arraylist>();
		
		for(int i=0,ni=data_list.size();i<ni;i++){
			part my_part=data_list.get(i);
			my_part.part_package_id=package_number;
			if(my_part.boftal==null)
				debug_information.println("Find null boftal:	",
						my_part.system_name+"	"+my_part.directory_name+my_part.mesh_file_name);
			else
				my_package_length+=my_part.boftal.buffer_object_head_length;
			
			while(package_number>(part_package.size()-1))
				part_package.add(new part_arraylist());
			
			part_arraylist my_part_arraylist=part_package.get(my_part.part_package_id);
			
			my_part.part_package_sequence_id=my_part_arraylist.list.size();
			my_part_arraylist.list.add(my_part.part_package_sequence_id,my_part);
			
			long my_last_time=my_part.boftal.buffer_object_head_last_modify_time;
			if(my_last_time<my_part.part_par.last_modified_time)
				my_last_time=my_part.part_par.last_modified_time;
			if(my_part_arraylist.last_time<my_last_time)
				my_part_arraylist.last_time=my_last_time;
			
			if(i<(ni-1))
				if(my_package_length<system_par.max_buffer_object_head_package_length)
					if(comparator_for_part_package_collector.
						package_compare(my_part,data_list.get(i+1))==0)
							continue;
			package_number++;
			my_package_length=0;
		}
	}
};
public class part_package 
{
	public String	package_file_name[];
	public long		package_length[],package_last_time[];
	
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
	private void create_package_boftal(
			String boftal_data_file_name,ArrayList<part>my_part_array_list,
			system_parameter system_par,scene_parameter scene_par,
			client_process_bar process_bar,String process_bar_title)
	{
		int part_number=my_part_array_list.size();
		
		debug_information.println();
		debug_information.println("Begin create_package_boftal,Total part number:",part_number+",	"+boftal_data_file_name);
		debug_information.println();
		
		if((process_bar!=null)&&(process_bar_title!=null))
			process_bar.set_process_bar(true, process_bar_title,"",0,part_number);

		int cut_directory_length=system_par.temporary_file_par.temporary_root_directory_name.length();
		file_writer fw=new file_writer(boftal_data_file_name+".tmp",system_par.local_data_charset);
		fw.println(part_number);
		
		for(int i=0;i<part_number;i++) {
			part boftal_part=my_part_array_list.get(i);
			String part_temporary_file_directory=file_directory.
					part_file_directory(boftal_part,system_par,scene_par);
			String boftal_file_name=part_temporary_file_directory+"mesh.boftal";
			fw.println(part_temporary_file_directory.substring(cut_directory_length));

			file_reader fr=new file_reader(boftal_file_name,fw.get_charset_name());
			for(String str;!(fr.eof());)
				if((str=fr.get_string())!=null)
					fw.println(str);
			fr.close();
			
			fw.println();
			
			if((process_bar!=null)&&(process_bar_title!=null))
				process_bar.set_process_bar(false,
					process_bar_title,boftal_part.user_name,i,part_number);
			debug_information.println((i+1)+".create_package_boftal for\t:\t",
				 boftal_part.system_name+"\t\t\tboftal_file:\t"+boftal_file_name);
		}		
		fw.close();
		file_writer.file_rename(boftal_data_file_name+".tmp",boftal_data_file_name);
		
		if((process_bar!=null)&&(process_bar_title!=null))
			process_bar.set_process_bar(false, process_bar_title,"",part_number,part_number);
		
		debug_information.println();
		debug_information.println("End create_package_boftal,Total part number:",part_number+",	"+boftal_data_file_name);
		debug_information.println();
		
		return;
	}
	public part_package(String fast_load_type,
		client_process_bar process_bar,tree_string_locker_container string_locker_container,
		String package_process_bar_title,String boftal_process_bar_title,render_container rc,
		int part_type_id,system_parameter system_par,scene_parameter scene_par)
	{
		String package_directory_name	=file_directory.package_file_directory(part_type_id,system_par,scene_par);
		String package_data_file_name	=package_directory_name+"package_data.txt";
		String boftal_data_file_name 	=package_directory_name+"boftal_data.txt";
		String package_lock_key			=package_directory_name+"package.lock";

		part_package_collector ppc=new part_package_collector(rc.part_array_list(part_type_id),system_par);
		
		int package_number=ppc.part_package.size();
		package_length	 =new long	 [package_number];
		package_last_time=new long	 [package_number];
		package_file_name=new String [package_number];

		string_locker_container.write_lock(package_lock_key);
		
		if(new File(package_data_file_name).exists()) {
			if(fast_load_type.compareTo("fast")==0) {
				file_reader fr=new file_reader(package_data_file_name,system_par.local_data_charset);
				for(int i=0;i<package_number;i++) {
					package_length[i]	=fr.get_long();
					package_last_time[i]=fr.get_long();
					package_file_name[i]=package_directory_name+"package_"+i+".gzip_text";
				}
				string_locker_container.write_unlock(package_lock_key);
				return;
			}
			if(new File(boftal_data_file_name).exists()){
				boolean not_create_flag=true;
				file_reader fr=new file_reader(package_data_file_name,system_par.local_data_charset);
				for(int i=0;i<package_number;i++) {
					package_length[i]	=fr.get_long();
					package_last_time[i]=fr.get_long();
					package_file_name[i]=package_directory_name+"package_"+i+".gzip_text";
					
					if(new File(package_file_name[i]).lastModified()<ppc.part_package.get(i).last_time){
						not_create_flag=false;
						break;
					}
				}
				fr.close();
				if(not_create_flag) {
					string_locker_container.write_unlock(package_lock_key);
					return;
				}
			}
		}

		for(int i=0;i<package_number;i++){
			if(process_bar!=null)
				process_bar.set_process_bar((i<=0),
					package_process_bar_title,"package_"+i,i,package_number);
			
			String my_tmp_file_name		=package_directory_name+"package_"+i+".tmp";
			String my_package_file_name	=package_directory_name+"package_"+i+".gzip_text";
	
			debug_information.println();
			debug_information.println("Create part package:	",my_package_file_name);
	
			file_writer fw=new file_writer(my_package_file_name,system_par.network_data_charset);
			fw.println("[");
	
			ArrayList<part> my_part_list=ppc.part_package.get(i).list;
			for(int j=0,nj=my_part_list.size();j<nj;j++){
				part my_part=my_part_list.get(j);
				String my_directory=file_directory.part_file_directory(my_part,system_par,scene_par);
				compress_file_data.do_uncompress(
					new File(my_tmp_file_name),
					new File(my_directory+"mesh.head.gzip_text"),
					system_par.response_block_size,"gzip");
				fw.print_file(my_tmp_file_name).println((j<(nj-1))?",":"");
				
				debug_information.print  ("	part user_name:	",	my_part.user_name);
				debug_information.print  ("		part type:	",	my_part.part_par.part_type_string);
				if(my_part.is_normal_part())
					debug_information.println("		part mesh_file_name:	",
							my_part.directory_name+my_part.mesh_file_name);
				else 
					debug_information.println(
							my_part.is_bottom_box_part()?"		Bottom box part":"		Top box part",
							",permanent_part_id:"	+my_part.permanent_part_id);
			}
			fw.println("]");
			fw.close();
						
			compress_file_data.do_compress(
					new File(my_package_file_name),
					new File(my_tmp_file_name),
					system_par.response_block_size,"gzip");
			file_writer.file_rename(my_tmp_file_name,my_package_file_name);

			File f=new File(my_package_file_name);
			
			package_length[i]   =f.length();
			package_last_time[i]=f.lastModified();
			package_file_name[i]=my_package_file_name;
		}

		file_writer fw=new file_writer(package_data_file_name,system_par.local_data_charset);
		for(int i=0;i<package_number;i++)
			fw.	println("/*	"+i+".package_length	*/	",package_length[i]).
				println("/*	"+i+".package_last_time	*/	",package_last_time[i]).
				println();
		fw.close();
		
		create_package_boftal(boftal_data_file_name,ppc.tree_get_value_list(),
				system_par,scene_par,process_bar,boftal_process_bar_title);
	
		string_locker_container.write_unlock(package_lock_key);
	
		return;
	}
}