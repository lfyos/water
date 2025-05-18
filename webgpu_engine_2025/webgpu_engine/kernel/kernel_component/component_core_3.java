package kernel_component;

import java.io.File;
import java.util.ArrayList;

import kernel_common_class.change_name;
import kernel_file_manager.file_reader;
import kernel_part.part;
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
	private void process_component_operation(
			String token_string,file_reader fr,component_construction_parameter ccp)
	{
		for(String str;!(fr.eof());) {
			if((str=fr.get_string())==null)
				continue;
			if((str=str.trim().toLowerCase()).length()<=0)
				continue;
			switch(str){
			default:
			{
				int create_child_number;
				try{
					create_child_number=Integer.decode(str);
				}catch(Exception e){
					e.printStackTrace();
					
					fr.close();
					debug_information.println("Find error child_number:	",str);
					debug_information.println("File name:	 ",fr.directory_name+fr.file_name);
					debug_information.println("Error:	",e.toString());
					
					create_child_number=0;
				}
				for(int i=0;i<create_child_number;i++)
					children.add(new component(token_string,fr,
						uniparameter.part_list_flag,uniparameter.normalize_location_flag,ccp));
				return;
			}
			case "push_file_part_type_string":
				ccp.push_part_type_string_sorter(
					new part_type_string_sorter(
						new String[] {fr.directory_name+file_reader.separator(fr.get_string())},
						ccp.sk.scene_par.part_type_string,fr.get_charset()));
				break;
			case "push_string_part_type_string":
				if((str=fr.get_string())==null)
					str=ccp.sk.scene_par.part_type_string;
				else if((str=str.trim()).length()<=0)
					str=ccp.sk.scene_par.part_type_string;
				else
					str=ccp.sk.scene_par.part_type_string+";"+str;
				ccp.push_part_type_string_sorter(new part_type_string_sorter(null,str,fr.get_charset()));
				break;
			case "pop_part_type_string":	
				ccp.pop_part_type_string_sorter();
				break;
			case "push_file_part_change_name":
				ccp.push_change_part_name(
					new change_name(
						new String[] {fr.directory_name+file_reader.separator(fr.get_string())},
						ccp.sk.scene_par.change_part_string,fr.get_charset()));
				break;
			case "push_string_part_change_name":
				if((str=fr.get_string())==null)
					str=ccp.sk.scene_par.change_part_string;
				else if((str=str.trim()).length()<=0)
					str=ccp.sk.scene_par.change_part_string;
				else
					str=ccp.sk.scene_par.change_part_string+";"+str;
				
				ccp.push_change_part_name(new change_name(null,str,fr.get_charset()));
				break;
			case "pop_part_change_name":
				ccp.pop_change_part_name();
				break;
			case "token_program":
			case "file_program":
			case "charset_file_program":
				initialization.create_initialization(fr,str);
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
				if((str=fr.get_string())!=null)
					token_string+=str;
				break;
			case "absolute_token_string":
				if((str=fr.get_string())!=null)
					token_string=str;
				break;
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
				break;
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
				break;
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
				break;
			}
			case "component_mount":
				ccp.clsc.add_source_item(fr.get_string(),token_string, 
						fr.directory_name+file_reader.separator(fr.get_string()),fr.get_charset());
				break;
			case "charset_component_mount":
				ccp.clsc.add_source_item(fr.get_string(),token_string, 
						fr.directory_name+file_reader.separator(fr.get_string()),fr.get_string());
				break;
			case "absulate_component_mount":
				ccp.clsc.add_source_item(fr.get_string(),token_string, 
						file_reader.separator(fr.get_string()),fr.get_charset());
				break;
			case "absulate_charset_component_mount":
				ccp.clsc.add_source_item(fr.get_string(),token_string, 
						file_reader.separator(fr.get_string()),fr.get_string());
				break;
			case "environment_component_mount":	
			{
				String add_component_name=fr.get_string();
				String add_file_name=file_reader.separator(System.getenv(fr.get_string()));
				if(add_file_name.charAt(add_file_name.length()-1)!=File.separatorChar)
					add_file_name+=File.separatorChar;
				ccp.clsc.add_source_item(add_component_name,token_string,add_file_name,fr.get_charset());
				break;
			}
			case "environment_charset_component_mount":	
			{
				String add_component_name=fr.get_string();
				String add_file_name=file_reader.separator(System.getenv(fr.get_string()));
				if(add_file_name.charAt(add_file_name.length()-1)!=File.separatorChar)
					add_file_name+=File.separatorChar;
				ccp.clsc.add_source_item(add_component_name,token_string,add_file_name,fr.get_string());
				break;
			}
			case "part_driver_mount":
				file_mount_component.part_driver_mount(component_name,driver_array,fr,token_string,
					uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;
			case "external_part_driver_mount":
				file_mount_component.external_part_driver_mount(component_name,fr,token_string,
					uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;
			case "mount":
				file_mount_component.file_mount(component_name,fr,false,token_string,
					uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;
			case "charset_mount":
				file_mount_component.charset_file_mount(component_name,fr,false,token_string,
					uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;
			case "client_parameter_mount":
				if(file_mount_file_name_and_charset.client_parameter_mount(fr,ccp))
					file_mount_component.file_mount(component_name,fr,false,token_string,
						uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;
			case "client_select_mount":
				if(file_mount_file_name_and_charset.client_select_mount(fr,ccp))
					file_mount_component.file_mount(component_name,fr,false,token_string,
						uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;
			case "environment_scene_sub_directory_mount":
				if(file_mount_file_name_and_charset.environment_scene_sub_directory_mount(fr,ccp))
					file_mount_component.file_mount(component_name,fr,true,token_string,
						uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;
			case "client_parameter_charset_mount":
				if(file_mount_file_name_and_charset.client_parameter_charset_mount(fr,ccp))
					file_mount_component.charset_file_mount(component_name,fr,false,token_string,
						uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;
			case "client_select_charset_mount":
				if(file_mount_file_name_and_charset.client_select_charset_mount(fr,ccp))
					file_mount_component.charset_file_mount(component_name,fr,true,token_string,
						uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;
			case "environment_scene_sub_directory_charset_mount":
				if(file_mount_file_name_and_charset.environment_scene_sub_directory_charset_mount(fr,ccp))
					file_mount_component.charset_file_mount(component_name,fr,true,token_string,
						uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
				break;
			}	
		}
	}
	private void decrease_children_number(String token_string,file_reader fr,
			boolean part_list_flag,boolean normalize_location_flag,component_construction_parameter ccp)
	{
		int child_number,max_child_number;
		if((child_number=children.size())<=1)
			return;
		if((max_child_number=ccp.sk.scene_par.max_child_number)<=2)
			return;
		if(child_number<=max_child_number)
			return;
		
		int new_child_number;
		if((new_child_number=(int)Math.sqrt(child_number))>max_child_number)
			new_child_number=max_child_number;
		if(new_child_number<2)
			new_child_number=2;
		
		ArrayList<component> bak_children=children;
		children=new ArrayList<component>();
			
		for(int collect_number=0,i=0;i<new_child_number;i++){
			String id_str="_"+(ccp.sk.scene_par.inserted_component_and_part_id++);
			String my_component_name=ccp.sk.scene_par.inserted_component_name+id_str;
			String my_part_name=ccp.sk.scene_par.inserted_part_name+id_str;
			ArrayList<part> my_part_list=ccp.pcfps.search_part(my_part_name);
			if(my_part_list!=null)
				if(my_part_list.size()>0) {
					i--;
					continue;
				}
			
			fr.push_string_array(new String[]
			{
				my_component_name,
				my_part_name,
				"1","0","0","0",
				"0","1","0","0",
				"0","0","1","0",
				"0","0","0","1",
				"0"
			});
			component my_comp=new component(token_string,fr,part_list_flag,normalize_location_flag,ccp);
			children.add(my_comp);
			
			my_comp.children=new ArrayList<component>();
			int my_child_number=(bak_children.size()-collect_number)/(new_child_number-i);
			for(int j=0;j<my_child_number;j++)
				my_comp.children.add(bak_children.get(collect_number++));
		}
	}
	public int append_component(component_construction_parameter ccp,boolean append_child_flag)
	{
		if(ccp.clsc.get_source_item_number()<=0)
			return 0;
		int ret_val=ccp.clsc.add_component(component_name,children,
				uniparameter.part_list_flag,uniparameter.normalize_location_flag,ccp);
		if(append_child_flag)
			for(int i=0,ni=children.size();i<ni;i++)
				ret_val+=children.get(i).append_component(ccp,append_child_flag);
		return ret_val;
	}
	public component_core_3(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,ccp);
		
		children=new ArrayList<component>();
		process_component_operation(token_string,fr,ccp);
		append_component(ccp,false);
		
		decrease_children_number(token_string,fr,part_list_flag,normalize_location_flag,ccp);
		
		var my_child=this;
		for(int i=0,ni=children.size();i<ni;i++) {
			my_child=children.get(i);
			if(uniparameter.file_last_modified_time<my_child.uniparameter.file_last_modified_time)
				uniparameter.file_last_modified_time=my_child.uniparameter.file_last_modified_time;
		}
	}
}