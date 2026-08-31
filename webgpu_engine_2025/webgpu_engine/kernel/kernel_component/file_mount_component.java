package kernel_component;

import java.io.File;
import java.util.ArrayList;

import kernel_part.part;
import kernel_driver.component_driver;
import kernel_driver.part_driver;
import kernel_file_manager.file_directory;
import kernel_file_manager.file_reader;
import kernel_common_class.change_name;
import kernel_common_class.debug_information;
import kernel_file_manager.travel_through_directory;

public class file_mount_component
{
	static private void load_component_from_file_list(
			String my_assemble_file_name,String my_assemble_file_charset,
			String token_string,boolean part_list_flag,boolean normalize_location_flag,
			ArrayList<component>child_component_list,component_construction_parameter ccp)
	{
		class assemble_file_collector extends travel_through_directory
		{
			public ArrayList<String> file_name_list;
			public void operate_file(String file_name)
			{
				file_name_list.add(file_name);
			}
			public assemble_file_collector(String my_assemble_file_name)
			{
				file_name_list=new ArrayList<String>();
				do_travel(file_directory.replace_special_char(my_assemble_file_name),true);
			}
		};
		if((my_assemble_file_name==null)||(my_assemble_file_charset==null)) {
			debug_information.println(
				"load_component_array_list error,(my_assemble_file_name==null)||(my_assemble_file_charset==null)");
			return;
		}
		
		ArrayList<String> file_name_list=(new assemble_file_collector(my_assemble_file_name)).file_name_list;
		for(String my_file_name:file_name_list){
			file_reader mount_fr=new file_reader(my_file_name,my_assemble_file_charset);
			if(mount_fr.error_flag()) {
				debug_information.println(
					"load_component_array_list (mount_fr.error_flag()):	",my_file_name);
			}else {
				debug_information.println("assemble_file_name:	",		my_file_name);
				debug_information.println("assemble_file_charset:	",	my_assemble_file_charset);
				try{
					child_component_list.add(new component(token_string,
							mount_fr,part_list_flag,normalize_location_flag,ccp));
				}catch(Exception e) {
					e.printStackTrace();
					debug_information.println("Create scene fail: ",my_file_name+" fail");
				}
			}
			mount_fr.close();
		}
		return;
	}
	static public void charset_file_mount(
			String my_file_name,String my_file_charset,
			String component_name,file_reader fr,boolean absulate_path_flag,
			String token_string,boolean part_list_flag,boolean normalize_location_flag,
			ArrayList<component>child_component_list,component_construction_parameter ccp)
	{
		if((my_file_name==null)||(my_file_charset==null)) {
			debug_information.println(
				"file_mount_array error,file_name==null or file_charset==null,component_name:"+component_name);
			return;
		}
		if(my_file_name.length()<=0){
			debug_information.println(
				"file_mount_array error(my_file_name.length()<=0):	",
				"component_name:	"+component_name);
			return;
		}
		my_file_name=file_directory.replace_special_char(my_file_name);
		
		String my_directory_name_array[];
		if(absulate_path_flag)
			my_directory_name_array		=new String[]{""};
		else {
			my_directory_name_array		=new String[ccp.sk.scene_par.type_sub_directory.length+6];
	
			my_directory_name_array[0]	=fr.directory_name;
			my_directory_name_array[1]	=ccp.sk.create_parameter.scene_directory_name	+"assemble_default"+File.separatorChar;
			my_directory_name_array[2]	=ccp.sk.scene_par.directory_name				+"assemble_default"+File.separatorChar;
			my_directory_name_array[3]	=ccp.sk.scene_par.extra_directory_name			+"assemble_default"+File.separatorChar;
			my_directory_name_array[4]	=ccp.sk.scene_par.scene_shader_directory_name	+"assemble_default"+File.separatorChar;

			for(int i=0,ni=ccp.sk.scene_par.type_sub_directory.length;i<ni;i++){
				my_directory_name_array	[i+5] =ccp.sk.scene_par.type_shader_directory_name;
				my_directory_name_array	[i+5]+=ccp.sk.scene_par.type_sub_directory[i];
				my_directory_name_array	[i+5]+="assemble_default"+File.separatorChar;
			}
		
			my_directory_name_array[my_directory_name_array.length-1]
				=ccp.sk.system_par.parameter_directory+"assemble_default"+File.separatorChar;
		}
		for(int i=0,ni=my_directory_name_array.length;i<ni;i++)
			if(new File(my_directory_name_array[i]+my_file_name).exists()) {
				load_component_from_file_list(my_directory_name_array[i]+my_file_name,my_file_charset,
						token_string,part_list_flag,normalize_location_flag,child_component_list,ccp);
				return;
			}
		debug_information.println("charset_file_mount file NOT exits:	",
				"my_file_name:	"+my_file_name+"	component_name:	"+component_name);
		debug_information.println("my_directory_name_array.length:	",my_directory_name_array.length);
		for(int i=0,ni=my_directory_name_array.length;i<ni;i++) 
			debug_information.println(i+":		",my_directory_name_array[i]+my_file_name);
		return;
	}
	static public void part_driver_mount(
			String component_name,ArrayList<component_driver> driver_array,
			file_reader fr,String token_string,boolean part_list_flag,boolean normalize_location_flag,
			ArrayList<component>child_component_list,component_construction_parameter ccp)
	{
		int my_driver_id=-1;
		for(component_driver my_driver:driver_array){
			my_driver_id++;
			if(my_driver.component_part==null) {
				debug_information.println(
					"part_driver_mount error(driver_array[i].component_part==null):	",
					"component_name:	"+component_name+"		driver_id:"+my_driver_id);
				continue;
			}
			part_driver my_part_driver;
			if((my_part_driver=my_driver.component_part.driver)==null) {
				debug_information.println(
					"part_driver_mount error(driver_array[i].component_part.driver==null):",
					"	component_name:	"	+component_name+"		driver_id:"+my_driver_id);
				debug_information.println("part system_name:	",	my_driver.component_part.system_name);
				debug_information.println("part user_name:	",		my_driver.component_part.user_name);
				continue;
			}
			
			String file_name_and_charset[]=my_part_driver.assemble_file_name_and_file_charset(
					fr,my_driver.component_part,ccp.sk,ccp.request_response);
			if(file_name_and_charset==null){
				debug_information.println(
					"part_driver_mount error(file_name_and_charset==null):	",
					"	component_name:	"	+component_name+"		driver_id:"+my_driver_id);
				debug_information.println("part system_name:	",	my_driver.component_part.system_name);
				debug_information.println("part user_name:	",		my_driver.component_part.user_name);
				continue;
			}
			
			for(int i=1,ni=file_name_and_charset.length;i<ni;i+=2){
				if((file_name_and_charset[i-1]==null)||(file_name_and_charset[i]==null)){
					debug_information.println(
						"part_driver_mount error(file_name_and_charset[0 or 1]==null):	",
						"component_name:	"+component_name+"		driver_id:"+my_driver_id
						+"		index_id:"+i);
					debug_information.println("part system_name:	",	my_driver.component_part.system_name);
					debug_information.println("part user_name:	",		my_driver.component_part.user_name);
					continue;
				}
				load_component_from_file_list(file_name_and_charset[i-1],file_name_and_charset[i],
					token_string,part_list_flag,normalize_location_flag,child_component_list,ccp);
			}
			return;
		}
		debug_information.println(
			"part_driver_mount error(NO assemble_file_name exist):	",
			"component_name:	"+component_name+"		driver_number:"+driver_array.size());
		return;
	}
	
	static public void external_part_driver_mount(String component_name,file_reader fr,
			String token_string,boolean part_list_flag,boolean normalize_location_flag,
			ArrayList<component>child_component_list,component_construction_parameter ccp)
	{
		String external_part_name;
		if((external_part_name=fr.get_string())==null) {
			debug_information.println(
				"external_part_driver_mount error(external_part_name==null):	",
				"component_name:	"+component_name);
			return;
		}
		ArrayList<part> part_list;
		change_name part_name_change;
		String search_part_name=external_part_name;
		
		if((part_name_change=ccp.get_change_part_name())==null)
			part_list=ccp.sk.part_search_cont.search_value_list(search_part_name);
		else{
			search_part_name=part_name_change.search_change_name(search_part_name,search_part_name);
			if((part_list=ccp.sk.part_search_cont.search_value_list(search_part_name))==null){
				search_part_name=part_name_change.search_change_name(search_part_name,search_part_name);
				part_list=ccp.sk.part_search_cont.search_value_list(search_part_name);
			}
		}
		if(part_list==null) {
			debug_information.println(
				"external_part_driver_mount error(part_list==null):	",
				"component_name:	"+component_name+"		external_part_name:	"+external_part_name);
			return;
		}
		for(part my_part:part_list) {
			if(my_part.driver==null) {
				debug_information.println(
					"external_part_driver_mount error(my_part.driver==null):",
					"	component_name:	"		+component_name+
					"	external_part_name:	"	+external_part_name+
					"	part_name:	"			+my_part.system_name+"		"+my_part.user_name);
				continue;
			}
			String file_name_and_charset[];
			try {
				file_name_and_charset=my_part.driver.assemble_file_name_and_file_charset(
						fr,my_part,ccp.sk,ccp.request_response);
			}catch(Exception e) {
				e.printStackTrace();
				debug_information.println(
					"external_part_driver_mount execption:	"+e.toString(),
					"	component_name:	"		+component_name+
					"	external_part_name:	"	+external_part_name+
					"	part_name:	"			+my_part.system_name+"		"+my_part.user_name);
				continue;
			}
			if(file_name_and_charset==null) {
				debug_information.println(
						"external_part_driver_mount error(file_name_and_charset==null):",
						"	component_name:	"		+component_name+
						"	external_part_name:	"	+external_part_name+
						"	part_name:	"			+my_part.system_name+"		"+my_part.user_name);
				continue;
			}
			for(int i=1,ni=file_name_and_charset.length;i<ni;i+=2) {
				if((file_name_and_charset[i-1]==null)||(file_name_and_charset[i]==null)){
					debug_information.println(
						"external_part_driver driver error(file_name_and_charset[0 or 1]==null):	",
						"	component_name:	"		+component_name+
						"	external_part_name:	"	+external_part_name+
						"	part_name:	"			+my_part.system_name+"		"+my_part.user_name);
					continue;
				}
				load_component_from_file_list(file_name_and_charset[i-1],file_name_and_charset[i],
						token_string,part_list_flag,normalize_location_flag,child_component_list,ccp);
			}
			return;
		}
		debug_information.println(
				"external_part_driver_mount error(No part found):	",
				"	component_name:	"		+component_name+
				"	external_part_name:	"	+external_part_name);
		return;
	}
}
