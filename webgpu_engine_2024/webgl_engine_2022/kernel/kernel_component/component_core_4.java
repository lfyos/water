package kernel_component;

import java.io.File;
import java.util.ArrayList;

import kernel_part.part;
import kernel_scene.scene_kernel;
import kernel_scene.part_type_string_sorter;
import kernel_driver.component_driver;
import kernel_common_class.cut_string;
import kernel_common_class.change_name;
import kernel_file_manager.file_reader;
import kernel_common_class.debug_information;
import kernel_network.client_request_response;
import kernel_file_manager.travel_through_directory;

public class component_core_4 extends component_core_3
{
	public component children[];
	
	public void destroy()
	{
		super.destroy();
		if(children!=null){
			for(int i=0,ni=children_number();i<ni;i++){
				if(children[i]!=null)
					children[i].destroy();
				children[i]=null;
			}
			children=null;
		}		
	}
	public int children_number()
	{
		if(children==null)
			return 0;
		else
			return children.length;
	}
	public void append_child(ArrayList<component> my_append_children_list)
	{
		if(my_append_children_list==null)
			return;
		int my_append_child_number;
		if((my_append_child_number=my_append_children_list.size())<=0)
			return;
		if(children==null)
			children=my_append_children_list.toArray(new component[my_append_child_number]);
		else{
			component bak[]=children;
			children=new component[bak.length+my_append_child_number];
			for(int i=0,ni=bak.length;i<ni;i++)
				children[i]=bak[i];
			for(int i=0,j=bak.length;i<my_append_child_number;i++,j++)
				children[j]=my_append_children_list.get(i);
		}
	}
	private String[][]file_mount(file_reader fr,scene_kernel sk,boolean absulate_path_flag)
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
				return new String[][]{new String[]{my_directory_name_array[i]+my_file_name,my_charset_name_array[i]}};

		debug_information.println("file mount file NOT exits:	",
				"my_file_name:	"+my_file_name+"		component_name:	"+component_name);

		return null;
	}
	private String[][]charset_file_mount(file_reader fr,scene_kernel sk,boolean absulate_path_flag)
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
				return new String[][]{new String[] {my_directory_name_array[i]+my_file_name,my_file_charset}};

		debug_information.println("charset_file_mount file NOT exits:	",
				"my_file_name:	"+my_file_name+"component_name:	"+component_name);
		return null;
	}
	private String [][]part_driver_mount(file_reader fr,scene_kernel sk,client_request_response request_response)
	{
		String ret_val[][];
		if(driver_number()<=0)  {
			debug_information.println(
				"Part_driver driver assemble_file_name_and_file_charset error(driver_number()<=0):	",
				"component_name:	"+component_name);
			return null;
		}
		component_driver c_d=driver_array.get(0);
		if(c_d.component_part==null) {
			debug_information.println(
				"Part_driver driver assemble_file_name_and_file_charset error(driver_array[0].component_part==null):	",
				"component_name:	"+component_name);
			return null;
		}
		if(c_d.component_part.driver==null) {
			debug_information.println(
				"Part_driver driver assemble_file_name_and_file_charset error(driver_array[0].component_part.driver==null):	",
				"component_name:	"+component_name);
			return null;
		}
		if((ret_val=c_d.component_part.driver.assemble_file_name_and_file_charset(
			fr,c_d.component_part,sk,request_response))==null)
		{
			debug_information.println(
				"Part_driver driver assemble_file_name_and_file_charset error(ret_val==null):	",
				"component_name:	"+component_name);
			return null;
		}
		if(ret_val.length<=0) {
			debug_information.println(
				"Part_driver driver assemble_file_name_and_file_charset error(ret_val.length<=0):	",
				"component_name:	"+component_name);
			return null;
		}
		return ret_val;
	}
	private String [][]external_part_driver_mount(file_reader fr,component_construction_parameter ccp)
	{
		String ret_val[][],external_part_name;
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
		for(children=null;!(fr.eof());){
			String str,assemble_file_name_array[][]=null;
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
					new part_type_string_sorter(new String[] {},str,fr.get_charset()));
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
				assemble_file_name_array=part_driver_mount(fr,ccp.sk,ccp.request_response);
				break;
			case "external_part_driver_mount":
				assemble_file_name_array=external_part_driver_mount(fr,ccp);
				break;

			case "mount":
				assemble_file_name_array=file_mount(fr,ccp.sk,false);
				break;
			case "client_parameter_mount":
			{
				String my_directory=fr.get_string(),my_file_name=fr.get_string();
				if((my_directory==null)||(my_file_name==null))
					debug_information.println("client_parameter_mount error((my_directory==null)||(my_file_name==null))");
				else if(((my_directory=my_directory.trim()).length()<=0)||((my_file_name=my_file_name.trim()).length()<=0))
					debug_information.println("client_parameter_mount error((my_directory.length()<=0)||(my_file_name.length()<=0))");
				else if((my_directory=ccp.sk.get_client_parameter(my_directory.trim()))==null)
					debug_information.println("client_parameter_mount error(my_directory==null)");
				else if((my_directory=my_directory.trim()).length()<0)
					debug_information.println("client_parameter_mount error(my_directory.length()<0)");
				else{
					fr.push_string_array(new String[] {str+File.separatorChar+my_file_name});
					assemble_file_name_array=file_mount(fr,ccp.sk,false);
					break;
				}
				continue;
			}
			case "client_select_mount":
			{
				String select_token			=cut_string.do_cut(fr.get_string());
				String select_file_name		=file_reader.separator(cut_string.do_cut(fr.get_string()));
				String assemble_file_name	=file_reader.separator(cut_string.do_cut(fr.get_string()));
				if((select_token.length()<=0)||(select_file_name.length()<=0)||(assemble_file_name.length()<=0))
					continue;
				if((select_token=ccp.sk.get_client_parameter(select_token))==null)
					continue;
				select_file_name=fr.directory_name+File.separatorChar+select_file_name;
				file_reader f_select=new file_reader(select_file_name,fr.get_charset());
				for(assemble_file_name_array=null;!(f_select.eof());){
					String my_select_token			=cut_string.do_cut(f_select.get_string());
					String my_select_directory_name	=cut_string.do_cut(f_select.get_string());	
					if((my_select_token.length()>0)&&(my_select_directory_name.length()>0))
						if(select_token.compareTo(my_select_token)==0){
							my_select_directory_name=fr.directory_name+my_select_directory_name;
							fr.push_string_array(new String[]
								{my_select_directory_name+File.separatorChar+assemble_file_name});
							assemble_file_name_array=file_mount(fr,ccp.sk,true);
							break;
						}
				}
				f_select.close();
				break;
			}
			case "environment_scene_sub_directory_mount":
				if((str=fr.get_string())!=null) 
					if((str=System.getenv(str))!=null) 
						if((str=file_reader.separator(str.trim())).length()>0) {
							if(str.charAt(str.length()-1)!=File.separatorChar)
								str+=File.separatorChar;
							str+=ccp.sk.scene_par.scene_sub_directory+fr.get_string();
							fr.push_string_array(new String[] {str});
							assemble_file_name_array=file_mount(fr,ccp.sk,true);
							break;
						}else
							debug_information.println("environment_scene_sub_directory_mount error",
									"((str=file_reader.separator(str.trim())).length()>0)");
					else
						debug_information.println("environment_scene_sub_directory_mount error",
								"((str=System.getenv(str))!=null) ");
				else
					debug_information.println("environment_scene_sub_directory_mount error",
							"((str=fr.get_string())!=null)");
				fr.get_string();
				continue;
			case "charset_mount":
				assemble_file_name_array=charset_file_mount(fr,ccp.sk,false);
				break;
			case "client_parameter_charset_mount":
				if((str=fr.get_string())==null) 
					debug_information.println(
							"client_parameter_charset_mount error","((str=fr.get_string())!=null)");
				else if((str=ccp.sk.get_client_parameter(str))==null)
					debug_information.println("client_parameter_charset_mount error","str!=null)");
				else{
					String my_str;
					if((my_str=cut_string.do_cut(fr.get_string())).length()<=0){
						fr.get_string();
						continue;
					}else{
						fr.push_string_array(new String[]{str+File.separatorChar+my_str});
						assemble_file_name_array=charset_file_mount(fr,ccp.sk,false);
						break;
					}	
				}
				fr.get_string();
				fr.get_string();
				continue;
			case "client_select_charset_mount":
			{
				String select_token			=cut_string.do_cut(fr.get_string());
				String select_file_name		=cut_string.do_cut(fr.get_string());
				String assemble_file_name	=cut_string.do_cut(fr.get_string());
				if((select_token.length()>0)&&(select_file_name.length()>0)&&(assemble_file_name.length()<=0))
					if((select_token=ccp.sk.get_client_parameter(select_token))!=null){
						select_file_name=fr.directory_name+File.separatorChar+select_file_name;
						boolean done_flag=false;
						file_reader f_select=new file_reader(select_file_name,fr.get_charset());
						for(assemble_file_name_array=null;!(f_select.eof());){
							String my_select_token			=cut_string.do_cut(f_select.get_string());
							String my_select_directory_name	=cut_string.do_cut(f_select.get_string());	
							if((my_select_token.length()>0)&&(my_select_directory_name.length()>0))
								if(select_token.compareTo(my_select_token)==0){
									my_select_directory_name=fr.directory_name+my_select_directory_name;
									fr.push_string_array(new String[]
										{my_select_directory_name+File.separatorChar+assemble_file_name});
									assemble_file_name_array=charset_file_mount(fr,ccp.sk,true);
									done_flag=true;
									break;
								}
						}
						f_select.close();
						if(done_flag)
							break;
					}
				fr.get_string();
				continue;
			}
			case "environment_scene_sub_directory_charset_mount":
				if((str=fr.get_string())!=null) 
					if((str=System.getenv(str))!=null) 
						if((str=file_reader.separator(str.trim())).length()>0){
							if(str.charAt(str.length()-1)!=File.separatorChar)
								str+=File.separatorChar;
							str+=ccp.sk.scene_par.scene_sub_directory+fr.get_string();
							fr.push_string_array(new String[]{str});
							assemble_file_name_array=charset_file_mount(fr,ccp.sk,true);
							break;
						}else
							debug_information.println("environment_scene_sub_directory_charset_mount error",
									"((str=file_reader.separator(str.trim())).length()>0)");
					else
						debug_information.println("environment_scene_sub_directory_charset_mount error",
								"((str=System.getenv(str))!=null) ");
				else
					debug_information.println("environment_scene_sub_directory_charset_mount error",
							"((str=fr.get_string())!=null)");
				fr.get_string();
				fr.get_string();
				continue;
			}
			if(assemble_file_name_array==null)
				continue;
			for(int i=0,ni=assemble_file_name_array.length;i<ni;i++) {
				if(assemble_file_name_array[i]==null)
					continue;
				if(assemble_file_name_array[i].length<2)
					continue;
				String assemble_file_name,assemble_file_charset;
				if((assemble_file_name=assemble_file_name_array[i][0])==null)
					continue;
				if((assemble_file_charset=assemble_file_name_array[i][1])==null)
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
						do_travel(file_reader.separator(assemble_file_name),true);
					}
				};
				
				ArrayList<String> 		file_name_list=(new assemble_file_collector()).file_name_list;
				ArrayList<component>	child_component_list=new ArrayList<component>();
				
				for(int j=0,nj=file_name_list.size();j<nj;j++) {
					String my_file_name=file_name_list.get(j);
					file_reader mount_fr=new file_reader(my_file_name,assemble_file_charset);
					if(mount_fr.eof())
						debug_information.println(
								"switch assemble file does not exist:	",	my_file_name);
					else {
						debug_information.println("assemble_file_name:	",		my_file_name);
						debug_information.println("assemble_file_charset:	",	assemble_file_charset);
						
						try{
							component this_child_comp=new component(token_string,mount_fr,
								uniparameter.part_list_flag,uniparameter.normalize_location_flag,ccp);
							child_component_list.add(this_child_comp);
						}catch(Exception e) {
							e.printStackTrace();
							debug_information.println("Create scene from ",my_file_name+" fail");
							debug_information.println("			",my_file_name);
						}
					}
					mount_fr.close();
				}
				append_child(child_component_list);
			}
		}
	}
	public void append_component(component_construction_parameter ccp)
	{
		if(ccp.clsc.get_source_item_number()>0){
			append_child(ccp.clsc.get_source_item(component_name,
				uniparameter.part_list_flag,uniparameter.normalize_location_flag,ccp));
			for(int i=0,ni=children_number();i<ni;i++)
				children[i].append_component(ccp);
		}
	}
	public component_core_4(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,ccp);
		
		process_component_operation(token_string,fr,ccp);
		append_child(ccp.clsc.get_source_item(component_name,
			uniparameter.part_list_flag,uniparameter.normalize_location_flag,ccp));
	}
}