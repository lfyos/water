package kernel_component;

import java.io.File;
import java.util.ArrayList;

import kernel_part.part;
import kernel_scene.scene_kernel;
import kernel_driver.component_driver;
import kernel_common_class.cut_string;
import kernel_common_class.change_name;
import kernel_file_manager.file_reader;
import kernel_scene.part_type_string_sorter;
import kernel_common_class.debug_information;
import kernel_network.client_request_response;
import kernel_file_manager.travel_through_directory;

public class component_core_4 extends component_core_3
{
	public ArrayList<component> children;
	
	public void destroy()
	{
		super.destroy();
		for(int i=0,ni=children.size();i<ni;i++){
			component my_child=children.get(i);
			if(my_child!=null)
				my_child.destroy();
			children.set(i,null);
		}
		children.clear();
	}
	public void append_child(ArrayList<component> my_append_children_list)
	{
		if(my_append_children_list==null)
			return;
		for(int i=0,ni=my_append_children_list.size();i<ni;i++)
			children.add(my_append_children_list.get(i));
	}
	private String[]file_mount(file_reader fr,scene_kernel sk,boolean absulate_path_flag)
	{
		String my_file_name;
		if((my_file_name=fr.get_string())==null) {
			debug_information.println(
				"file_mount_array error((my_file_name=fr.get_string())==null):	",
				"component_name:	"+component_name);
			return null;
		}
		if(my_file_name.length()<=0){
			debug_information.println(
				"file_mount_array error(my_file_name.length()<=0):	",
				"component_name:	"+component_name);
			return null;
		}
		my_file_name=file_reader.separator(my_file_name);
		
		String my_directory_name_array[],my_charset_name_array[];
		if(absulate_path_flag){
			my_directory_name_array		=new String[]{""};
			my_charset_name_array		=new String[]{fr.get_charset()};
		}else{
			my_directory_name_array		=new String[sk.scene_par.type_sub_directory.length+6];
			my_charset_name_array		=new String[sk.scene_par.type_sub_directory.length+6];

			my_directory_name_array	[0]	=fr.directory_name;
			my_directory_name_array	[1]	=sk.create_parameter.scene_directory_name	+"assemble_default"+File.separatorChar;
			my_directory_name_array	[2]	=sk.scene_par.directory_name				+"assemble_default"+File.separatorChar;
			my_directory_name_array	[3]	=sk.scene_par.extra_directory_name			+"assemble_default"+File.separatorChar;
			my_directory_name_array	[4]	=sk.scene_par.scene_shader_directory_name	+"assemble_default"+File.separatorChar;
			
			my_charset_name_array	[0]	=fr.get_charset();
			my_charset_name_array	[1]	=sk.create_parameter.scene_charset;
			my_charset_name_array	[2]	=sk.scene_par.parameter_charset;
			my_charset_name_array	[3]	=sk.scene_par.extra_parameter_charset;
			my_charset_name_array	[4]	=sk.scene_par.parameter_charset;
		
			for(int i=0,ni=sk.scene_par.type_sub_directory.length;i<ni;i++){
				my_directory_name_array	[i+5] =sk.scene_par.type_shader_directory_name;
				my_directory_name_array	[i+5]+=sk.scene_par.type_sub_directory[i];
				my_directory_name_array	[i+5]+="assemble_default"+File.separatorChar;
				my_charset_name_array	[i+5] =sk.scene_par.parameter_charset;
			}

			my_directory_name_array[my_directory_name_array.length-1]
					=sk.system_par.default_parameter_directory+"assemble_default"+File.separatorChar;
			my_charset_name_array[my_charset_name_array.length-1]=sk.system_par.local_data_charset;
		}
		
		for(int i=0,ni=my_directory_name_array.length;i<ni;i++)
			if(new File(my_directory_name_array[i]+my_file_name).exists()) 
				return	new String[]
							{
								my_directory_name_array[i]+my_file_name,
								my_charset_name_array[i]
							};

		debug_information.println("file mount file NOT exits:	",
				"my_file_name:	"+my_file_name+"		component_name:	"+component_name);

		return null;
	}
	private String[]charset_file_mount(file_reader fr,scene_kernel sk,boolean absulate_path_flag)
	{
		String my_file_name=fr.get_string(),my_file_charset=fr.get_string();
		if((my_file_name==null)||(my_file_charset==null)) {
			debug_information.println(
				"file_mount_array error,file_name==null or file_charset==null,component_name:"+component_name);
			return null;
		}
		if(my_file_name.length()<=0){
			debug_information.println(
				"file_mount_array error(my_file_name.length()<=0):	",
				"component_name:	"+component_name);
			return null;
		}
		my_file_name=file_reader.separator(my_file_name);
		
		String my_directory_name_array[];
		if(absulate_path_flag)
			my_directory_name_array		=new String[]{""};
		else {
			my_directory_name_array		=new String[sk.scene_par.type_sub_directory.length+6];
	
			my_directory_name_array[0]	=fr.directory_name;
			my_directory_name_array[1]	=sk.create_parameter.scene_directory_name	+"assemble_default"+File.separatorChar;
			my_directory_name_array[2]	=sk.scene_par.directory_name				+"assemble_default"+File.separatorChar;
			my_directory_name_array[3]	=sk.scene_par.extra_directory_name			+"assemble_default"+File.separatorChar;
			my_directory_name_array[4]	=sk.scene_par.scene_shader_directory_name	+"assemble_default"+File.separatorChar;

			for(int i=0,ni=sk.scene_par.type_sub_directory.length;i<ni;i++){
				my_directory_name_array	[i+5] =sk.scene_par.type_shader_directory_name;
				my_directory_name_array	[i+5]+=sk.scene_par.type_sub_directory[i];
				my_directory_name_array	[i+5]+="assemble_default"+File.separatorChar;
			}
		
			my_directory_name_array[my_directory_name_array.length-1]
					=sk.system_par.default_parameter_directory+"assemble_default"+File.separatorChar;
		}
		for(int i=0,ni=my_directory_name_array.length;i<ni;i++)
			if(new File(my_directory_name_array[i]+my_file_name).exists()) 
				return new String[] {my_directory_name_array[i]+my_file_name,my_file_charset};

		debug_information.println("charset_file_mount file NOT exits:	",
				"my_file_name:	"+my_file_name+"component_name:	"+component_name);
		return null;
	}
	
	private String []part_driver_mount(file_reader fr,scene_kernel sk,client_request_response request_response)
	{
		int my_driver_number;
		if((my_driver_number=driver_number())<=0)  {
			debug_information.println(
				"Part_driver driver assemble_file_name_and_file_charset error((my_driver_number=driver_number())<=0):	",
				"component_name:	"+component_name);
			return null;
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
			String ret_val[];
			if((ret_val=c_d.component_part.driver.assemble_file_name_and_file_charset(
				fr,c_d.component_part,sk,request_response))==null)
			{
				debug_information.println(
					"Part_driver driver assemble_file_name_and_file_charset error(ret_val==null):	",
					"component_name:	"+component_name+"		driver_id:"+my_driver_id);
				continue;
			}
			if(ret_val.length<=0) {
				debug_information.println(
					"Part_driver driver assemble_file_name_and_file_charset error(ret_val.length<=0):	",
					"component_name:	"+component_name+"		driver_id:"+my_driver_id);
				continue;
			}
			return ret_val;
		}
		debug_information.println(
			"Part_driver driver assemble_file_name_and_file_charset error(NO assemble_file_name exist):	",
			"component_name:	"+component_name+"		driver_number:"+my_driver_number);
		return null;
	}
	private String []external_part_driver_mount(file_reader fr,component_construction_parameter ccp)
	{
		String ret_val[],external_part_name;
		if((external_part_name=fr.get_string())==null) {
			debug_information.println(
				"external_part_driver driver assemble_file_name_and_file_charset error(external_part_name==null):	",
				"component_name:	"+component_name+"		external_part_name:	"+external_part_name);
			return null;
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
			return null;
		}
		if(par.size()<1) {
			debug_information.println(
				"external_part_driver driver assemble_file_name_and_file_charset error(par.length<1):	",
				"component_name:	"+component_name+"		external_part_name:	"+external_part_name);
			return null;
		}
		part p=par.get(0);
		
		if(p.driver==null) {
			debug_information.println(
				"external_part_driver driver assemble_file_name_and_file_charset error(par[0].driver==null):	",
				"component_name:	"+component_name+"		external_part_name:	"+external_part_name);
			return null;
		}
		try {
			ret_val=p.driver.assemble_file_name_and_file_charset(fr,p,ccp.sk,ccp.request_response);
		}catch(Exception e) {
			e.printStackTrace();
			
			debug_information.println(
				"external_part_driver driver assemble_file_name_and_file_charset execption:	",
				"component_name:	"+component_name+"		external_part_name:	"+external_part_name);
			
			return null;
		}
		if(ret_val==null) {
			debug_information.println(
					"external_part_driver driver assemble_file_name_and_file_charset error(ret_val==null):	",
					"component_name:	"+component_name+"		external_part_name:	"+external_part_name);
			return null;
		}
		if(ret_val.length<=0) {
			debug_information.println(
					"external_part_driver driver assemble_file_name_and_file_charset error(ret_val.length<=0):	",
					"component_name:	"+component_name+"		external_part_name:	"+external_part_name);
			return null;
		}
		return ret_val;
	}
	private void process_component_operation(
			String token_string,file_reader fr,component_construction_parameter ccp)
	{
		for(ArrayList<String[]>assemble_file_name_list=new ArrayList<String[]>();!(fr.eof());){
			String str;
			if((str=fr.get_string())==null)
				continue;
			switch(str=str.toLowerCase()){
			default:
				int create_child_number;
				try {
					create_child_number=Integer.decode(str);
				}catch(Exception e){
					e.printStackTrace();
					
					fr.close();
					debug_information.println("Find error child_number:	",str);
					debug_information.println("File name:	 ",fr.directory_name+fr.file_name);
					debug_information.println("Error:	",e.toString());
					
					create_child_number=0;
				}
				if(create_child_number>0){
					ArrayList<component> my_children_list=new ArrayList<component>();
					for(int i=0;i<create_child_number;i++)
						my_children_list.add(new component(token_string,fr,
							uniparameter.part_list_flag,uniparameter.normalize_location_flag,ccp));
					append_child(my_children_list);
				}
				return;
			case "push_file_part_type_string":
				ccp.push_part_type_string_sorter(
					new part_type_string_sorter(
						new String[] {fr.directory_name+file_reader.separator(fr.get_string())},
						ccp.sk.scene_par.part_type_string,fr.get_charset()));
				continue;
			case "push_string_part_type_string":
				if((str=fr.get_string())==null)
					str=ccp.sk.scene_par.part_type_string;
				else if((str=str.trim()).length()<=0)
					str=ccp.sk.scene_par.part_type_string;
				else
					str=ccp.sk.scene_par.part_type_string+";"+str;
				ccp.push_part_type_string_sorter(
					new part_type_string_sorter(new String[]{},str,fr.get_charset()));
				continue;
			case "pop_part_type_string":	
				ccp.pop_part_type_string_sorter();
				continue;
			case "push_file_part_change_name":
				ccp.push_change_part_name(
					new change_name(
						new String[] {fr.directory_name+file_reader.separator(fr.get_string())},
						ccp.sk.scene_par.change_part_string,fr.get_charset()));
				continue;
			case "push_string_part_change_name":
				if((str=fr.get_string())==null)
					str=ccp.sk.scene_par.change_part_string;
				else if((str=str.trim()).length()<=0)
					str=ccp.sk.scene_par.change_part_string;
				else
					str=ccp.sk.scene_par.change_part_string+";"+str;
				
				ccp.push_change_part_name(
					new change_name(new String[] {},str,fr.get_charset()));
				continue;
			case "pop_part_change_name":
				ccp.pop_change_part_name();
				continue;
			case "token_program":
			case "file_program":
			case "charset_file_program":
				initialization.create_initialization(fr,str);
				continue;
			case "part_list":
				uniparameter.part_list_flag=true;
				continue;
			case "not_part_list":
				uniparameter.part_list_flag=false;
				continue;
			case "normalize_location":
				uniparameter.normalize_location_flag=true;
				continue;
			case "not_normalize_location":
				uniparameter.normalize_location_flag=false;
				continue;
			case "lod_precision_scale":
				uniparameter.component_driver_lod_precision_scale=fr.get_double();
				continue;
			case "blank_token_string":
				token_string="";
				continue;
			case "relative_token_string":
				if((str=fr.get_string())!=null)
					token_string+=str;
				continue;
			case "absolute_token_string":
				if((str=fr.get_string())!=null)
					token_string=str;
				continue;
			case "clear_display_flag":
			{
				int parameter_channel_id=fr.get_int();
				if(parameter_channel_id<0)
					for(int i=0,ni=multiparameter.length;i<ni;i++)
						multiparameter[i].display_flag=false;
				else if(parameter_channel_id<multiparameter.length)
					multiparameter[parameter_channel_id].display_flag=false;
				else 
					debug_information.println("set_display parameter_channel_id error:",
							component_name+"	"+parameter_channel_id);
				continue;
			}
			case "set_display_flag":
			{
				int parameter_channel_id=fr.get_int();
				if(parameter_channel_id<0)
					for(int i=0,ni=multiparameter.length;i<ni;i++)
						multiparameter[i].display_flag=true;
				else if(parameter_channel_id<multiparameter.length)
					multiparameter[parameter_channel_id].display_flag=true;
				else 
					debug_information.println("set_display parameter_channel_id error:",
							component_name+"	"+parameter_channel_id);
				continue;
			}	
			case "set_display_bitmap":	
			{
				int parameter_channel_id=fr.get_int();
				long my_display_bitmap=fr.get_long();
				if(parameter_channel_id<0)
					for(int i=0,ni=multiparameter.length;i<ni;i++)
						multiparameter[i].display_bitmap=my_display_bitmap;
				else if(parameter_channel_id<multiparameter.length)
					multiparameter[parameter_channel_id].display_bitmap=my_display_bitmap;
				else 
					debug_information.println("display_bitmap parameter_channel_id error:",
							component_name+"	"+parameter_channel_id);
				continue;
			}	
			case "component_mount":
				ccp.clsc.add_source_item(fr.get_string(),token_string, 
						fr.directory_name+file_reader.separator(fr.get_string()),fr.get_charset());
				continue;
			case "charset_component_mount":
				ccp.clsc.add_source_item(fr.get_string(),token_string, 
						fr.directory_name+file_reader.separator(fr.get_string()),fr.get_string());
				continue;
			case "absulate_component_mount":
				ccp.clsc.add_source_item(fr.get_string(),token_string, 
						file_reader.separator(fr.get_string()),fr.get_charset());
				continue;
			case "absulate_charset_component_mount":
				ccp.clsc.add_source_item(fr.get_string(),token_string, 
						file_reader.separator(fr.get_string()),fr.get_string());
				continue;
			case "environment_component_mount":	
			{
				String add_component_name=fr.get_string();
				String add_file_name=file_reader.separator(System.getenv(fr.get_string()));
				if(add_file_name.charAt(add_file_name.length()-1)!=File.separatorChar)
					add_file_name+=File.separatorChar;
				ccp.clsc.add_source_item(add_component_name,token_string,add_file_name,fr.get_charset());
				continue;
			}
			case "environment_charset_component_mount":	
			{
				String add_component_name=fr.get_string();
				String add_file_name=file_reader.separator(System.getenv(fr.get_string()));
				if(add_file_name.charAt(add_file_name.length()-1)!=File.separatorChar)
					add_file_name+=File.separatorChar;
				ccp.clsc.add_source_item(add_component_name,token_string,add_file_name,fr.get_string());
				continue;
			}
			case "part_driver_mount":
				assemble_file_name_list.add(part_driver_mount(fr,ccp.sk,ccp.request_response));
				break;
			case "external_part_driver_mount":
				assemble_file_name_list.add(external_part_driver_mount(fr,ccp));
				break;
			case "mount":
				assemble_file_name_list.add(file_mount(fr,ccp.sk,false));
				break;
			case "charset_mount":
				assemble_file_name_list.add(charset_file_mount(fr,ccp.sk,false));
				break;
			case "client_parameter_mount":
			{
				String my_directory=fr.get_string();
				String my_file_name=fr.get_string();
				
				if((my_directory==null)||(my_file_name==null)) {
					debug_information.println("client_parameter_mount:	",
							"((my_directory==null)||(my_file_name==null))");
					continue;
				}
				if(((my_directory=my_directory.trim()).length()<=0)
						||((my_file_name=cut_string.do_cut(file_reader.separator(my_file_name))).length()<=0)) 
				{
					debug_information.println("client_parameter_mount:	",
							"(((my_directory=my_directory).length()<=0)||((my_file_name=my_file_name).length()<=0))");
					continue;
				}
				if((my_directory=ccp.sk.get_client_parameter(my_directory))==null) {
					debug_information.println("client_parameter_mount:	",
							"((my_directory=ccp.sk.get_client_parameter(my_directory))==null)");
					continue;
				}
				if((my_directory=cut_string.do_cut(file_reader.separator(my_directory.trim()))).length()<0) {
					debug_information.println("client_parameter_mount:	",
							"((my_directory=cut_string.do_cut(file_reader.separator(my_directory.trim()))).length()<0)");
					continue;
				}
				
				fr.push_string_array(new String[]{my_directory+File.separatorChar+my_file_name});
				assemble_file_name_list.add(file_mount(fr,ccp.sk,false));
				break;
			}
			case "client_select_mount":
			{
				String my_select_token		=fr.get_string();
				String my_select_file_name	=fr.get_string();
				String my_assemble_file_name=fr.get_string();
				
				if((my_select_token==null)||(my_select_file_name==null)||(my_assemble_file_name==null)) {
					debug_information.println("client_select_mount:",
						"((my_select_token==null)||(my_select_file_name==null)||(my_assemble_file_name==null))");
					continue;
				}
				my_select_token		 =cut_string.do_cut(my_select_token);
				my_select_file_name	 =cut_string.do_cut(file_reader.separator(my_select_file_name));
				my_assemble_file_name=cut_string.do_cut(file_reader.separator(my_assemble_file_name));

				if((my_select_token.length()<=0)||(my_select_file_name.length()<=0)||(my_assemble_file_name.length()<=0)){
					debug_information.println("client_select_mount:",
							"((my_select_token.length()<=0)||(my_select_file_name.length()<=0)||(my_assemble_file_name.length()<=0))");
					continue;
				}
				if((my_select_token=ccp.sk.get_client_parameter(my_select_token))==null) {
					debug_information.println("client_select_mount:",
							"((my_select_token=ccp.sk.get_client_parameter(my_select_token))==null)");
					continue;
				}
				
				file_reader f_select=new file_reader(fr.directory_name+my_select_file_name,fr.get_charset());
				
				debug_information.print  ("client_select_mount,select_token:	",my_select_token);
				debug_information.println("	file_name:	",f_select.directory_name+f_select.file_name);

				while(!(f_select.eof())){
					String f_select_token			=f_select.get_string();
					String f_select_directory_name	=f_select.get_string();
					if((f_select_token==null)||(f_select_directory_name==null))
						continue;
					f_select_token			=cut_string.do_cut(f_select_token);
					f_select_directory_name	=cut_string.do_cut(file_reader.separator(f_select_directory_name));
					if((f_select_token.length()<=0)||(f_select_directory_name.length()<=0))
						continue;
					if(my_select_token.compareTo(f_select_token)!=0)
						continue;
					my_assemble_file_name=f_select_directory_name+File.separatorChar+my_assemble_file_name;
					fr.push_string_array(new String[]{my_assemble_file_name});
					
					assemble_file_name_list.add(file_mount(fr,ccp.sk,false));
					
					break;
				}
				f_select.close();
				break;
			}
			case "environment_scene_sub_directory_mount":
			{
				String my_directory_name=fr.get_string(),my_file_name=fr.get_string();
				if((my_directory_name==null) ||(my_file_name==null)){
					debug_information.println("environment_scene_sub_directory_mount:",
							"((my_directory_name==null) ||(my_file_name==null))");
					continue;
				}
				if((my_directory_name=System.getenv(my_directory_name.trim()))==null) {
					debug_information.println("environment_scene_sub_directory_mount:",
							"((my_directory_name=System.getenv(my_directory_name.trim()))==null)");
					continue;
				}
				if((my_directory_name=cut_string.do_cut(file_reader.separator(my_directory_name.trim()))).length()<=0) {
					debug_information.println("environment_scene_sub_directory_mount:",
							"((my_directory_name=cut_string.do_cut(file_reader.separator(my_directory_name.trim()))).length()<=0)");
					continue;
				}
				if((my_file_name=cut_string.do_cut(file_reader.separator(my_file_name.trim()))).length()<=0) {
					debug_information.println("environment_scene_sub_directory_mount:",
							"((my_file_name=cut_string.do_cut(file_reader.separator(my_file_name.trim()))).length()<=0)");
					continue;
				}
				my_file_name=my_directory_name+File.separatorChar+ccp.sk.scene_par.scene_sub_directory+my_file_name;
				fr.push_string_array(new String[] {my_file_name});
				assemble_file_name_list.add(file_mount(fr,ccp.sk,true));
				break;
			}
			case "client_parameter_charset_mount":
			{
				String my_directory_name=fr.get_string();
				String my_file_name		=fr.get_string();
				String my_file_charset	=fr.get_string();
				
				if((my_directory_name==null)||(my_file_name==null)||(my_file_charset==null)) {
					debug_information.println("client_parameter_charset_mount error",
							"((my_directory_name==null)||(my_file_name==null)||(my_file_charset==null))");
					continue;
				}
				if((my_directory_name=ccp.sk.get_client_parameter(my_directory_name.trim()))==null) {
					debug_information.println("client_parameter_charset_mount error",
							"((my_directory_name=ccp.sk.get_client_parameter(my_directory_name.trim()))==null)");
					continue;
				}
				if((my_directory_name=cut_string.do_cut(file_reader.separator(my_directory_name)))==null) {
					debug_information.println("client_parameter_charset_mount error",
							"((my_directory_name=cut_string.do_cut(file_reader.separator(my_directory_name)))==null)");
					continue;
				}
				if((my_file_name=cut_string.do_cut(file_reader.separator(my_file_name))).length()<=0){
					debug_information.println("client_parameter_charset_mount error",
							"((my_file_name=cut_string.do_cut(file_reader.separator(my_file_name))).length()<=0)");
					continue;
				}
				fr.push_string_array(new String[]{my_directory_name+File.separatorChar+my_file_name,my_file_charset});
				assemble_file_name_list.add(charset_file_mount(fr,ccp.sk,false));
				break;
			}
			case "client_select_charset_mount":
			{
				String select_token				=fr.get_string();
				String select_file_name			=fr.get_string();
				String select_assemble_file_name=fr.get_string();
				String select_file_charset		=fr.get_string();
				
				if((select_token==null)||(select_file_name==null)
					||(select_assemble_file_name==null)||(select_file_charset==null)) 
				{
					debug_information.println("client_select_charset_mount:",
						"((select_token==null)||(select_file_name==null)||(select_assemble_file_name==null))||(select_file_charset==null)");
					continue;
				}

				select_token				=cut_string.do_cut(select_token);
				select_file_name			=cut_string.do_cut(select_file_name);
				select_file_name			=cut_string.do_cut(file_reader.separator(select_file_name));
				select_assemble_file_name	=cut_string.do_cut(select_assemble_file_name);
				select_assemble_file_name	=cut_string.do_cut(file_reader.separator(select_assemble_file_name));
				select_file_charset			=cut_string.do_cut(select_file_charset);

				if((select_token.length()<=0)||(select_file_name.length()<=0)
					||(select_assemble_file_name.length()<=0)||(select_file_charset.length()<=0)) 
				{
					debug_information.println("client_select_charset_mount:",
						"((select_token.length()<=0)||(select_file_name.length()<=0)||(select_assemble_file_name.length()<=0)||(select_file_charset.length()<=0))");
					continue;
				}
				
				if((select_token=ccp.sk.get_client_parameter(select_token))==null){
					debug_information.println("client_select_charset_mount:",
							"((select_token=ccp.sk.get_client_parameter(select_token))==null)");
					continue;
				}
				
				file_reader f_select=new file_reader(fr.directory_name+select_file_name,fr.get_charset());
				while(!(f_select.eof())){
					String my_select_token=f_select.get_string();
					String my_select_directory_name=f_select.get_string();
					if((my_select_token==null)||(my_select_directory_name==null))
						continue;
					
					my_select_token			=cut_string.do_cut(my_select_token);
					my_select_directory_name=cut_string.do_cut(file_reader.separator(my_select_directory_name));
					if((my_select_token.length()<=0)||(my_select_directory_name.length()<=0))
						continue;
					if(select_token.compareTo(my_select_token)!=0)
						continue;
					my_select_directory_name=fr.directory_name+my_select_directory_name;
					fr.push_string_array(new String[]{
							my_select_directory_name+File.separatorChar+select_assemble_file_name,
							select_file_charset});
					assemble_file_name_list.add(charset_file_mount(fr,ccp.sk,true));
					break;
				}
				f_select.close();
				break;
			}
			case "environment_scene_sub_directory_charset_mount":
			{
				String my_directory_name=fr.get_string();
				String my_file_name		=fr.get_string();
				String my_file_charset	=fr.get_string();
				
				if((my_directory_name==null)||(my_file_name==null)||(my_file_charset==null)) {
					debug_information.println("environment_scene_sub_directory_charset_mount:",
							"((my_directory_name==null)||(my_file_name==null)||(my_file_charset==null))");
					continue;
				}
				if((my_file_name=cut_string.do_cut(file_reader.separator(my_file_name))).length()<=0){
					debug_information.println("environment_scene_sub_directory_charset_mount:",
							"((my_file_name=cut_string.do_cut(file_reader.separator(my_file_name))).length()<=0)");
					continue;
				}
				if((my_directory_name=System.getenv(my_directory_name.trim()))==null) {
					debug_information.println("environment_scene_sub_directory_charset_mount:",
							"((my_directory_name=System.getenv(my_directory_name.trim()))==null)");
					continue;
				}
				if((my_directory_name=cut_string.do_cut(file_reader.separator(my_directory_name.trim()))).length()<=0){
					debug_information.println("environment_scene_sub_directory_charset_mount:",
							"((my_directory_name=cut_string.do_cut(file_reader.separator(my_directory_name.trim()))).length()<=0)");
					continue;
				}
				
				my_directory_name+=File.separatorChar+ccp.sk.scene_par.scene_sub_directory;
				fr.push_string_array(new String[]{my_directory_name+my_file_name,my_file_charset});
				assemble_file_name_list.add(charset_file_mount(fr,ccp.sk,true));
				break;
			}
			}
			ArrayList<component> child_component_list=new ArrayList<component>();
			for(int i=0,ni=assemble_file_name_list.size();i<ni;i++) {
				String my_assemble_file_name[]=assemble_file_name_list.get(i);
				if(my_assemble_file_name==null)
					continue;
				if(my_assemble_file_name.length<2)
					continue;
				if((my_assemble_file_name[0]==null)||(my_assemble_file_name[1]==null))
					continue;
				
				class assemble_file_collector extends travel_through_directory
				{
					public ArrayList<String> file_name_list;
					public void operate_file(String file_name)
					{
						file_name_list.add(file_name);
					}
					public assemble_file_collector()
					{
						file_name_list=new ArrayList<String>();
						do_travel(file_reader.separator(my_assemble_file_name[0]),true);
					}
				};
				
				ArrayList<String> file_name_list=(new assemble_file_collector()).file_name_list;

				for(int j=0,nj=file_name_list.size();j<nj;j++) {
					String my_file_name=file_name_list.get(j);
					file_reader mount_fr=new file_reader(my_file_name,my_assemble_file_name[1]);
					if(mount_fr.eof()) 
						debug_information.println(
								"switch assemble file does not exist:	",	my_file_name);
					else {
						debug_information.println("assemble_file_name:	",		my_file_name);
						debug_information.println("assemble_file_charset:	",	my_assemble_file_name[1]);
						try{
							child_component_list.add(new component(
								token_string,mount_fr,uniparameter.part_list_flag,
								uniparameter.normalize_location_flag,ccp));
						}catch(Exception e) {
							e.printStackTrace();
							debug_information.println("Create scene from ",my_file_name+" fail");
							debug_information.println("			",my_file_name);
						}
					}
					mount_fr.close();
				}
			}
			append_child(child_component_list);
			assemble_file_name_list.clear();
		}
	}
	public void append_component(component_construction_parameter ccp)
	{
		if(ccp.clsc.get_source_item_number()>0){
			append_child(ccp.clsc.get_source_item(component_name,
				uniparameter.part_list_flag,uniparameter.normalize_location_flag,ccp));
			for(int i=0,ni=children.size();i<ni;i++)
				children.get(i).append_component(ccp);
		}
	}
	public component_core_4(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,ccp);
		children=new ArrayList<component>();
		process_component_operation(token_string,fr,ccp);
		append_child(ccp.clsc.get_source_item(component_name,
			uniparameter.part_list_flag,uniparameter.normalize_location_flag,ccp));
	}
}