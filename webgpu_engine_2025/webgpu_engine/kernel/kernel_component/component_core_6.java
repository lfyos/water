package kernel_component;

import kernel_common_class.change_name;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_directory;
import kernel_common_class.name_exist_tester;
import kernel_common_class.debug_information;

public class component_core_6 extends component_core_5
{
	public void destroy()
	{
		super.destroy();
	}
	private void process_component_operation(String token_string,
				file_reader fr,component_construction_parameter ccp)
	{
		for(String child_number_str;!(fr.eof());){
			if((child_number_str=fr.get_string())==null)
				continue;
			if((child_number_str=child_number_str.trim().toLowerCase()).length()<=0)
				continue;
			switch(child_number_str){
			default:
			{
				int my_child_number;
				try{
					my_child_number=Integer.decode(child_number_str);
				}catch(Exception e){
					e.printStackTrace();

					debug_information.println("Find error child_number:	",child_number_str);
					debug_information.println("File name:	 ",fr.directory_name+fr.file_name);
					debug_information.println("Error:	",e.toString());
					
					my_child_number=0;
				}
				for(int i=0;i<my_child_number;i++)
					children.add(new component(
						token_string,fr,uniparameter.part_list_flag,
						uniparameter.normalize_location_flag,ccp));
				return;
			}
			case "push_file_part_type_string":
			{
				String type_string=ccp.sk.scene_par.part_type_string;
				String file_name=file_directory.replace_special_char(fr.get_string());
				String path_name[]=new String[] {fr.directory_name+file_name};
				name_exist_tester tester=new name_exist_tester(path_name,type_string,fr.get_charset());
				ccp.push_part_type_string_tester(tester);
				break;
			}
			case "push_file_part_type_string_without_scene_parameter":
			{
				String file_name=file_directory.replace_special_char(fr.get_string());
				String path_name[]=new String[] {fr.directory_name+file_name};
				name_exist_tester tester=new name_exist_tester(path_name,null,fr.get_charset());
				ccp.push_part_type_string_tester(tester);
				break;
			}
			case "push_string_part_type_string":
			{
				String type_string;
				if((type_string=fr.get_string())==null)
					type_string=ccp.sk.scene_par.part_type_string;
				else if((type_string=type_string.trim()).length()<=0)
					type_string=ccp.sk.scene_par.part_type_string;
				else
					type_string=ccp.sk.scene_par.part_type_string+";"+type_string;
				name_exist_tester tester=new name_exist_tester(null,type_string,fr.get_charset());
				ccp.push_part_type_string_tester(tester);
				break;
			}
			case "push_string_part_type_string_without_scene_parameter":
			{
				String type_string;
				if((type_string=fr.get_string())==null)
					type_string="";
				else if((type_string=type_string.trim()).length()<=0)
					type_string="";
				name_exist_tester tester=new name_exist_tester(null,type_string,fr.get_charset());
				ccp.push_part_type_string_tester(tester);
				break;
			}
			case "pop_part_type_string":	
				ccp.pop_part_type_string_tester();
				break;
			case "push_file_part_change_name":
			{
				String part_string=ccp.sk.scene_par.change_part_string;
				String file_name=file_directory.replace_special_char(fr.get_string());
				String path_name[]=new String[] {fr.directory_name+file_name};
				change_name ch_name=new change_name(path_name,part_string,fr.get_charset());
				ccp.push_change_part_name(ch_name);
				break;
			}
			case "push_file_part_change_name_without_scene_par":
			{
				String file_name=file_directory.replace_special_char(fr.get_string());
				String path_name[]=new String[] {fr.directory_name+file_name};
				change_name ch_name=new change_name(path_name,null,fr.get_charset());
				ccp.push_change_part_name(ch_name);
				break;
			}
			case "push_string_part_change_name":
			{
				String change_string;
				if((change_string=fr.get_string())==null)
					change_string=ccp.sk.scene_par.change_part_string;
				else if((change_string=change_string.trim()).length()<=0)
					change_string=ccp.sk.scene_par.change_part_string;
				else
					change_string=ccp.sk.scene_par.change_part_string+";"+change_string;
				change_name ch_name=new change_name(null,change_string,fr.get_charset());
				ccp.push_change_part_name(ch_name);
				break;
			}
			case "push_string_part_change_name_without_scene_par":
			{
				String change_string;
				if((change_string=fr.get_string())==null)
					change_string="";
				else if((change_string=change_string.trim()).length()<=0)
					change_string="";
				
				change_name ch_name=new change_name(null,change_string,fr.get_charset());
				ccp.push_change_part_name(ch_name);
				break;
			}
			case "pop_part_change_name":
				ccp.pop_change_part_name();
				break;
			case "part_list":
				uniparameter.part_list_flag=true;
				break;
			case "not_part_list":
				uniparameter.part_list_flag=false;
				break;
			case "normalize_location":
				uniparameter.normalize_location_flag=true;
				break;
			case "not_normalize_location":
				uniparameter.normalize_location_flag=false;
				break;
			case "lod_precision_scale":
				uniparameter.component_driver_lod_precision_scale=fr.get_double();
				break;
			case "blank_token_string":
				token_string="";
				break;
			case "relative_token_string":
			{
				String append_token_string;
				if((append_token_string=fr.get_string())!=null)
					token_string+=append_token_string;
				break;
			}
			case "absolute_token_string":
			{
				String new_token_string;
				if((new_token_string=fr.get_string())!=null)
					token_string=new_token_string;
				break;
			}
			case "clear_display_flag":
			{
				int my_parameter_channel_id;
				if((my_parameter_channel_id=fr.get_int())<0)
					for(int i=0,ni=multiparameter.length;i<ni;i++)
						multiparameter[i].display_flag=false;
				else if(my_parameter_channel_id<multiparameter.length)
					multiparameter[my_parameter_channel_id].display_flag=false;
				else 
					debug_information.println("set_display parameter_channel_id error:",
								component_name+"	"+my_parameter_channel_id);
				break;
			}
			case "set_display_flag":
			{
				int my_parameter_channel_id;
				if((my_parameter_channel_id=fr.get_int())<0)
					for(int i=0,ni=multiparameter.length;i<ni;i++)
						multiparameter[i].display_flag=true;
				else if(my_parameter_channel_id<multiparameter.length)
					multiparameter[my_parameter_channel_id].display_flag=true;
				else 
					debug_information.println("set_display parameter_channel_id error:",
							component_name+"	"+my_parameter_channel_id);
				break;
			}
			case "set_display_bitmap":
			{
				int my_parameter_channel_id=fr.get_int();
				long my_display_bitmap	=fr.get_long();
				if(my_parameter_channel_id<0)
					for(int i=0,ni=multiparameter.length;i<ni;i++)
							multiparameter[i].display_bitmap=my_display_bitmap;
				else if(my_parameter_channel_id<multiparameter.length)
					multiparameter[my_parameter_channel_id].display_bitmap=my_display_bitmap;
				else 
					debug_information.println("display_bitmap parameter_channel_id error:",
							component_name+"	"+my_parameter_channel_id);
				break;
			}
			
			case "token_program":	
			case "file_program":
			case "charset_file_program":
			case "multifile_program":
			case "charset_multifile_program":
				initialization.create_initialization(fr,child_number_str);
				break;
				
			case "component_mount":
				ccp.clsc.file_add_source_item(fr.get_string(),token_string, 
						fr.directory_name+file_directory.replace_special_char(fr.get_string()),fr.get_charset());
				break;
			case "charset_component_mount":
				ccp.clsc.file_add_source_item(fr.get_string(),token_string, 
						fr.directory_name+file_directory.replace_special_char(fr.get_string()),fr.get_string());
				break;
			case "absulate_component_mount":
				ccp.clsc.file_add_source_item(fr.get_string(),token_string, 
						file_directory.replace_special_char(fr.get_string()),fr.get_charset());
				break;
			case "absulate_charset_component_mount":
				ccp.clsc.file_add_source_item(fr.get_string(),token_string, 
						file_directory.replace_special_char(fr.get_string()),fr.get_string());
				break;
			case "environment_component_mount":	
			{
				String add_component_name=fr.get_string();
				change_name ch_name=ccp.sk.scene_par.scene_environment;
				String add_file_name=ch_name.search_change_name(fr.get_string(),null);
				if(add_file_name==null) 
					debug_information.println("environment_component_mount error,(add_file_name==null)");
				else
					ccp.clsc.file_add_source_item(add_component_name,token_string,
							file_directory.replace_special_char(add_file_name),fr.get_charset());
				break;
			}
			case "environment_charset_component_mount":	
			{
				String add_component_name=fr.get_string();
				change_name ch_name=ccp.sk.scene_par.scene_environment;
				String add_file_name=ch_name.search_change_name(fr.get_string(),null);
				String component_file_charset=fr.get_string();
				if(add_file_name==null)
					debug_information.println("environment_charset_component_mount error,(add_file_name==null)");
				else
					ccp.clsc.file_add_source_item(add_component_name,token_string,
						file_directory.replace_special_char(add_file_name),component_file_charset);
				break;
			}
			
			case "mount":
				file_mount_component.charset_file_mount(
					fr.get_string(),fr.get_charset(),component_name,fr,false,token_string,
					uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;
			case "charset_mount":
				file_mount_component.charset_file_mount(
					fr.get_string(),fr.get_string(),component_name,fr,false,token_string,
					uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;
			
			case "absulate_mount":
				file_mount_component.charset_file_mount(
					fr.get_string(),fr.get_charset(),component_name,fr,true,token_string,
					uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;
			case "absulate_charset_mount":
				file_mount_component.charset_file_mount(
					fr.get_string(),fr.get_string(),component_name,fr,true,token_string,
					uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;

			case "environment_select_mount":
				if(environment_mount_file_name_and_charset.select_mount(
						fr.get_string(),fr.get_string(),fr.get_string(),fr.get_charset(),fr,ccp))
					file_mount_component.charset_file_mount(
							fr.get_string(),fr.get_string(),component_name,fr,false,token_string,
							uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;
			case "environment_select_charset_mount":
				if(environment_mount_file_name_and_charset.select_mount(
						fr.get_string(),fr.get_string(),fr.get_string(),fr.get_string(),fr,ccp))
					file_mount_component.charset_file_mount(
							fr.get_string(),fr.get_string(),component_name,fr,false,token_string,
							uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;
			case "absulate_environment_select_mount":
				if(environment_mount_file_name_and_charset.select_mount(
						fr.get_string(),fr.get_string(),fr.get_string(),fr.get_charset(),fr,ccp))
					file_mount_component.charset_file_mount(
							fr.get_string(),fr.get_string(),component_name,fr,true,token_string,
							uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;
			case "absulate_environment_select_charset_mount":
				if(environment_mount_file_name_and_charset.select_mount(
						fr.get_string(),fr.get_string(),fr.get_string(),fr.get_string(),fr,ccp))
					file_mount_component.charset_file_mount(
							fr.get_string(),fr.get_string(),component_name,fr,true,token_string,
							uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;

			case "environment_parameter_mount":
				if(environment_mount_file_name_and_charset.parameter_mount(
						fr.get_string(),fr.get_string(),fr.get_charset(),fr,ccp))
					file_mount_component.charset_file_mount(
						fr.get_string(),fr.get_string(),component_name,fr,false,token_string,
						uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;
			case "environment_parameter_charset_mount":
				if(environment_mount_file_name_and_charset.parameter_mount(
						fr.get_string(),fr.get_string(),fr.get_string(),fr,ccp))
					file_mount_component.charset_file_mount(
						fr.get_string(),fr.get_string(),component_name,fr,false,token_string,
						uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;
			case "absulate_environment_parameter_mount":
				if(environment_mount_file_name_and_charset.parameter_mount(
						fr.get_string(),fr.get_string(),fr.get_charset(),fr,ccp))
					file_mount_component.charset_file_mount(
						fr.get_string(),fr.get_string(),component_name,fr,true,token_string,
						uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;
			case "absulate_environment_parameter_charset_mount":
				if(environment_mount_file_name_and_charset.parameter_mount(
						fr.get_string(),fr.get_string(),fr.get_string(),fr,ccp))
					file_mount_component.charset_file_mount(
						fr.get_string(),fr.get_string(),component_name,fr,true,token_string,
						uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;

			case "environment_scene_sub_directory_mount":
				if(environment_mount_file_name_and_charset.scene_sub_directory_mount(
						fr.get_string(),fr.get_string(),fr.get_charset(),fr,ccp))
					file_mount_component.charset_file_mount(
						fr.get_string(),fr.get_string(),component_name,fr,false,token_string,
						uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;
			case "environment_scene_sub_directory_charset_mount":
				if(environment_mount_file_name_and_charset.scene_sub_directory_mount(
						fr.get_string(),fr.get_string(),fr.get_string(),fr,ccp))
					file_mount_component.charset_file_mount(
						fr.get_string(),fr.get_string(),component_name,fr,false,token_string,
						uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;
			case "absulate_environment_scene_sub_directory_mount":
				if(environment_mount_file_name_and_charset.scene_sub_directory_mount(
						fr.get_string(),fr.get_string(),fr.get_charset(),fr,ccp))
					file_mount_component.charset_file_mount(
						fr.get_string(),fr.get_string(),component_name,fr,true,token_string,
						uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;
			case "absulate_environment_scene_sub_directory_charset_mount":
				if(environment_mount_file_name_and_charset.scene_sub_directory_mount(
						fr.get_string(),fr.get_string(),fr.get_string(),fr,ccp))
					file_mount_component.charset_file_mount(
						fr.get_string(),fr.get_string(),component_name,fr,true,token_string,
						uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;
			case "part_driver_mount":
				file_mount_component.part_driver_mount(component_name,driver_array,fr,token_string,
					uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;
			case "external_part_driver_mount":
				file_mount_component.external_part_driver_mount(component_name,fr,token_string,
					uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;
			}
		}
	}
	public component_core_6(String token_string,file_reader fr,
			boolean part_list_flag,boolean normalize_location_flag,
			component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,ccp);
		
		process_component_operation(token_string,fr,ccp);
	}
}