package kernel_component;

import java.io.File;
import java.util.ArrayList;

import kernel_part.part;
import kernel_driver.component_driver;
import kernel_file_manager.file_reader;
import kernel_common_class.change_name;
import kernel_common_class.debug_information;
import kernel_file_manager.travel_through_directory;

public class file_mount_component
{
	static private void load_component_array_list(
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
				do_travel(file_reader.separator(my_assemble_file_name),true);
			}
		};
		if((my_assemble_file_name==null)||(my_assemble_file_charset==null))
			return;
		
		ArrayList<String> file_name_list=new assemble_file_collector(my_assemble_file_name).file_name_list;

		for(int j=0,nj=file_name_list.size();j<nj;j++) {
			String my_file_name=file_name_list.get(j);
			file_reader mount_fr=new file_reader(my_file_name,my_assemble_file_charset);
			if(mount_fr.error_flag()) 
				debug_information.println(
					"load_component_array_list assemble file does not exist:	",my_file_name);
			else {
				debug_information.println("assemble_file_name:	",		my_file_name);
				debug_information.println("assemble_file_charset:	",	my_assemble_file_charset);
				try{
					child_component_list.add(new component(token_string,
							mount_fr,part_list_flag,normalize_location_flag,ccp));
				}catch(Exception e) {
					e.printStackTrace();
					debug_information.println("Create scene from ",my_file_name+" fail");
					debug_information.println("			",my_file_name);
				}
			}
			mount_fr.close();
		}
	}
	static public void file_mount(String component_name,file_reader fr,boolean absulate_path_flag,
			String token_string,boolean part_list_flag,boolean normalize_location_flag,
			ArrayList<component>child_component_list,component_construction_parameter ccp)
	{
		String my_file_name;
		if((my_file_name=fr.get_string())==null) {
			debug_information.println(
				"file_mount_array error((my_file_name=fr.get_string())==null):	",
				"component_name:	"+component_name);
			return;
		}
		if(my_file_name.length()<=0){
			debug_information.println(
				"file_mount_array error(my_file_name.length()<=0):	",
				"component_name:	"+component_name);
			return;
		}
		my_file_name=file_reader.separator(my_file_name);
		
		String my_directory_name_array[],my_charset_name_array[];
		if(absulate_path_flag){
			my_directory_name_array		=new String[]{""};
			my_charset_name_array		=new String[]{fr.get_charset()};
		}else{
			my_directory_name_array		=new String[ccp.sk.scene_par.type_sub_directory.length+6];
			my_charset_name_array		=new String[ccp.sk.scene_par.type_sub_directory.length+6];

			my_directory_name_array	[0]	=fr.directory_name;
			my_directory_name_array	[1]	=ccp.sk.create_parameter.scene_directory_name	+"assemble_default"+File.separatorChar;
			my_directory_name_array	[2]	=ccp.sk.scene_par.directory_name				+"assemble_default"+File.separatorChar;
			my_directory_name_array	[3]	=ccp.sk.scene_par.extra_directory_name			+"assemble_default"+File.separatorChar;
			my_directory_name_array	[4]	=ccp.sk.scene_par.scene_shader_directory_name	+"assemble_default"+File.separatorChar;
			
			my_charset_name_array	[0]	=fr.get_charset();
			my_charset_name_array	[1]	=ccp.sk.create_parameter.scene_charset;
			my_charset_name_array	[2]	=ccp.sk.scene_par.parameter_charset;
			my_charset_name_array	[3]	=ccp.sk.scene_par.extra_parameter_charset;
			my_charset_name_array	[4]	=ccp.sk.scene_par.parameter_charset;
		
			for(int i=0,ni=ccp.sk.scene_par.type_sub_directory.length;i<ni;i++){
				my_directory_name_array	[i+5] =ccp.sk.scene_par.type_shader_directory_name;
				my_directory_name_array	[i+5]+=ccp.sk.scene_par.type_sub_directory[i];
				my_directory_name_array	[i+5]+="assemble_default"+File.separatorChar;
				my_charset_name_array	[i+5] =ccp.sk.scene_par.parameter_charset;
			}

			my_directory_name_array[my_directory_name_array.length-1]
				=ccp.sk.system_par.default_parameter_directory+"assemble_default"+File.separatorChar;
			my_charset_name_array[my_charset_name_array.length-1]=ccp.sk.system_par.local_data_charset;
		}
		for(int i=0,ni=my_directory_name_array.length;i<ni;i++)
			if(new File(my_directory_name_array[i]+my_file_name).exists()) {
				load_component_array_list(my_directory_name_array[i]+my_file_name,my_charset_name_array[i],
							token_string,part_list_flag,normalize_location_flag,child_component_list,ccp);
				return;
			}
		debug_information.println("file mount file NOT exits:	",
				"my_file_name:	"+my_file_name+"		component_name:	"+component_name);
		return;
	}
	static public void charset_file_mount(
			String component_name,file_reader fr,boolean absulate_path_flag,
			String token_string,boolean part_list_flag,boolean normalize_location_flag,
			ArrayList<component>child_component_list,component_construction_parameter ccp)
	{
		String my_file_name=fr.get_string(),my_file_charset=fr.get_string();
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
		my_file_name=file_reader.separator(my_file_name);
		
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
				=ccp.sk.system_par.default_parameter_directory+"assemble_default"+File.separatorChar;
		}
		for(int i=0,ni=my_directory_name_array.length;i<ni;i++)
			if(new File(my_directory_name_array[i]+my_file_name).exists()) {
				load_component_array_list(my_directory_name_array[i]+my_file_name,my_file_charset,
						token_string,part_list_flag,normalize_location_flag,child_component_list,ccp);
				return;
			}
		debug_information.println("charset_file_mount file NOT exits:	",
				"my_file_name:	"+my_file_name+"component_name:	"+component_name);
		return;
	}
	static public void part_driver_mount(
			String component_name,ArrayList<component_driver> driver_array,
			file_reader fr,String token_string,boolean part_list_flag,boolean normalize_location_flag,
			ArrayList<component>child_component_list,component_construction_parameter ccp)
	{
		int my_driver_number;
		if((my_driver_number=driver_array.size())<=0)  {
			debug_information.println(
				"Part_driver driver assemble_file_name_and_file_charset error((my_driver_number=driver_number())<=0):	",
				"component_name:	"+component_name);
			return;
		}
		for(int my_driver_id=0;my_driver_id<my_driver_number;my_driver_id++) {
			component_driver c_d=driver_array.get(my_driver_id);
			if(c_d.component_part==null) {
				debug_information.println(
					"Part_driver driver assemble_file_name_and_file_charset error(driver_array[i].component_part==null):	",
					"component_name:	"+component_name+"		driver_id:"+my_driver_id);
				continue;
			}
			if(c_d.component_part.driver==null) {
				debug_information.println(
					"Part_driver driver assemble_file_name_and_file_charset error(driver_array[i].component_part.driver==null):	",
					"component_name:	"+component_name+"		driver_id:"+my_driver_id);
				continue;
			}
			String file_name_and_charset[];
			if((file_name_and_charset=c_d.component_part.driver.assemble_file_name_and_file_charset(
				fr,c_d.component_part,ccp.sk,ccp.request_response))==null)
			{
				debug_information.println(
					"Part_driver driver assemble_file_name_and_file_charset error(file_name_and_charset==null):	",
					"component_name:	"+component_name+"		driver_id:"+my_driver_id);
				continue;
			}
			if(file_name_and_charset.length<=1){
				debug_information.println(
					"Part_driver driver assemble_file_name_and_file_charset error(file_name_and_charset.length<=1):	",
					"component_name:	"+component_name+"		driver_id:"+my_driver_id);
				continue;
			}
			if((file_name_and_charset[0]==null)||(file_name_and_charset[1]==null)){
				debug_information.println(
					"Part_driver driver assemble_file_name_and_file_charset error(file_name_and_charset[0,1]=null):	",
					"component_name:	"+component_name+"		driver_id:"+my_driver_id);
				continue;
			}
			load_component_array_list(file_name_and_charset[0],file_name_and_charset[1],
					token_string,part_list_flag,normalize_location_flag,child_component_list,ccp);
			return;
		}
		debug_information.println(
			"Part_driver driver assemble_file_name_and_file_charset error(NO assemble_file_name exist):	",
			"component_name:	"+component_name+"		driver_number:"+my_driver_number);
		return;
	}
	
	static public void external_part_driver_mount(String component_name,file_reader fr,
			String token_string,boolean part_list_flag,boolean normalize_location_flag,
			ArrayList<component>child_component_list,component_construction_parameter ccp)
	{
		String file_name_and_charset[],external_part_name;
		if((external_part_name=fr.get_string())==null) {
			debug_information.println(
				"external_part_driver driver assemble_file_name_and_file_charset error(external_part_name==null):	",
				"component_name:	"+component_name+"		external_part_name:	"+external_part_name);
			return;
		}
		ArrayList<part> par;
		String search_part_name=external_part_name;
		change_name change_part_name;
		if((change_part_name=ccp.get_change_part_name())==null)
			par=ccp.pcfps.search_part(search_part_name);
		else{
			search_part_name=change_part_name.search_change_name(search_part_name,search_part_name);
			if((par=ccp.pcfps.search_part(search_part_name))==null){
				search_part_name=change_part_name.search_change_name(search_part_name,search_part_name);
				par=ccp.pcfps.search_part(search_part_name);
			}
		}
		if(par==null) {
			debug_information.println(
				"external_part_driver driver assemble_file_name_and_file_charset error(par==null):	",
				"component_name:	"+component_name+"		external_part_name:	"+external_part_name);
			return;
		}
		if(par.size()<1) {
			debug_information.println(
				"external_part_driver driver assemble_file_name_and_file_charset error(par.length<1):	",
				"component_name:	"+component_name+"		external_part_name:	"+external_part_name);
			return;
		}
		part p=par.get(0);
		
		if(p.driver==null) {
			debug_information.println(
				"external_part_driver driver assemble_file_name_and_file_charset error(par[0].driver==null):	",
				"component_name:	"+component_name+"		external_part_name:	"+external_part_name);
			return;
		}
		try {
			file_name_and_charset=p.driver.assemble_file_name_and_file_charset(fr,p,ccp.sk,ccp.request_response);
		}catch(Exception e) {
			e.printStackTrace();
			
			debug_information.println(
				"external_part_driver driver assemble_file_name_and_file_charset execption:	",
				"component_name:	"+component_name+"		external_part_name:	"+external_part_name);
			
			return;
		}
		if(file_name_and_charset==null) {
			debug_information.println(
					"external_part_driver driver assemble_file_name_and_file_charset error(ret_val==null):	",
					"component_name:	"+component_name+"		external_part_name:	"+external_part_name);
			return;
		}
		if(file_name_and_charset.length<=1) {
			debug_information.println(
					"external_part_driver driver assemble_file_name_and_file_charset error(ret_val.length<=1):	",
					"component_name:	"+component_name+"		external_part_name:	"+external_part_name);
			return;
		}
		load_component_array_list(file_name_and_charset[0],file_name_and_charset[1],
				token_string,part_list_flag,normalize_location_flag,child_component_list,ccp);
		return;
	}
}
