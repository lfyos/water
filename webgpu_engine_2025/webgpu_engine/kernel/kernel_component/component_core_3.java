package kernel_component;

import java.util.ArrayList;

import kernel_common_class.change_name;
import kernel_file_manager.file_reader;
import kernel_scene.part_type_string_sorter;
import kernel_common_class.debug_information;

public class component_core_3 extends component_core_2
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
	private void process_component_operation(String token_string,
				file_reader fr,component_construction_parameter ccp)
	{
		for(String child_number_str;!(fr.eof());) {
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
				ccp.push_part_type_string_sorter(
					new part_type_string_sorter(
							new String[] {fr.directory_name+file_reader.separator(fr.get_string())},
							ccp.sk.scene_par.part_type_string,fr.get_charset()));
					break;
			case "push_file_part_type_string_without_scene_par":
				ccp.push_part_type_string_sorter(
					new part_type_string_sorter(
							new String[] {fr.directory_name+file_reader.separator(fr.get_string())},
							null,fr.get_charset()));
				break;
			case "push_string_part_type_string":
			{
				String type_string;
				if((type_string=fr.get_string())==null)
					type_string=ccp.sk.scene_par.part_type_string;
				else if((type_string=type_string.trim()).length()<=0)
					type_string=ccp.sk.scene_par.part_type_string;
				else
					type_string=ccp.sk.scene_par.part_type_string+";"+type_string;
				ccp.push_part_type_string_sorter(
						new part_type_string_sorter(null,type_string,fr.get_charset()));
				break;
			}
			case "push_string_part_type_string_without_scene_par":
			{
				String type_string;
				if((type_string=fr.get_string())==null)
					type_string="";
				else if((type_string=type_string.trim()).length()<=0)
					type_string="";
				ccp.push_part_type_string_sorter(
						new part_type_string_sorter(null,type_string,fr.get_charset()));
				break;
			}
			case "pop_part_type_string":	
				ccp.pop_part_type_string_sorter();
				break;
			case "push_file_part_change_name":
				ccp.push_change_part_name(
					new change_name(
						new String[] {fr.directory_name+file_reader.separator(fr.get_string())},
						ccp.sk.scene_par.change_part_string,fr.get_charset()));
				break;
			case "push_file_part_change_name_without_scene_par":
				ccp.push_change_part_name(
					new change_name(
						new String[] {fr.directory_name+file_reader.separator(fr.get_string())},
						null,fr.get_charset()));
				break;
			case "push_string_part_change_name":
			{
				String change_string;
				if((change_string=fr.get_string())==null)
					change_string=ccp.sk.scene_par.change_part_string;
				else if((change_string=change_string.trim()).length()<=0)
					change_string=ccp.sk.scene_par.change_part_string;
				else
					change_string=ccp.sk.scene_par.change_part_string+";"+change_string;
				
				ccp.push_change_part_name(new change_name(null,change_string,fr.get_charset()));
				break;
			}
			case "push_string_part_change_name_without_scene_par":
			{
				String change_string;
				if((change_string=fr.get_string())==null)
					change_string="";
				else if((change_string=change_string.trim()).length()<=0)
					change_string="";
				ccp.push_change_part_name(new change_name(null,change_string,fr.get_charset()));
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
			case "charset_component_mount":
				
			case "absulate_component_mount":
			case "absulate_charset_component_mount":
				
			case "environment_component_mount":
			case "environment_charset_component_mount":
				
			case "part_driver_mount":
			case "external_part_driver_mount":
				
			case "mount":
			case "charset_mount":
				
			case "client_select_mount":
			case "client_select_charset_mount":
				
			case "client_parameter_mount":
			case "client_parameter_charset_mount":
				
			case "environment_scene_sub_directory_mount":
			case "environment_scene_sub_directory_charset_mount":
				file_mount_switch.switch_file_mount(child_number_str,component_name,
						driver_array,children,uniparameter,token_string,fr,ccp);
				break;
			}
		}
	}
	public component_core_3(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,ccp);
		
		children=new ArrayList<component>();
		process_component_operation(token_string,fr,ccp);
	}
}