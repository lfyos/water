package kernel_component;

import java.io.File;

import kernel_common_class.change_name;
import kernel_common_class.debug_information;
import kernel_engine.engine_kernel;
import kernel_engine.part_type_string_sorter;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_part.part;
import kernel_part.part_container_for_part_search;

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
	public void append_child(int append_number,component my_children[])
	{
		if(append_number<=0)
			return;
		if(my_children==null)
			return;
		if(append_number>my_children.length)
			append_number=my_children.length;
		if(children==null) {
			children=new component[append_number];
			for(int i=0;i<append_number;i++)
				children[i]=my_children[i];
		}else{
			component bak[]=children;
			children=new component[bak.length+append_number];
				
			for(int i=0,ni=bak.length;i<ni;i++)
				children[i]=bak[i];
			for(int i=0,j=bak.length;i<append_number;i++,j++)
				children[j]=my_children[i];
		}
	}
	private String [][]component_driver_mount_array(file_reader fr,
			engine_kernel ek,client_request_response request_response)
	{
		String ret_val[][];
		if(driver_number()<=0){
			debug_information.println(
				"Component driver assemble_file_name_and_file_charset error(driver_number()<=0):	",
				"component_name:	"+component_name);
			return null;
		}
		if((ret_val=driver_array[0].assemble_file_name_and_file_charset(fr,ek,request_response))==null){
			debug_information.println(
				"Component driver assemble_file_name_and_file_charset error(return value is null):	",
				"component_name:	"+component_name);
			return null;
		}
		if(ret_val.length<2){
			debug_information.println(
				"Component driver assemble_file_name_and_file_charset error(ret_val.length<2):	",
				"component_name:	"+component_name);
			return null;
		}
		if((ret_val[0]==null)||(ret_val[1]==null)){
			debug_information.println(
				"Component driver assemble_file_name_and_file_charset error((ret_val[0]==null)||(ret_val[1]==null)):	",
				"component_name:	"+component_name);
			return null;
		}
		if((ret_val[0].length<=0)||(ret_val[1].length<=0)){
			debug_information.println(
				"Component driver assemble_file_name_and_file_charset error((ret_val[0].length<=0)||(ret_val[1].length<=0)):	",
				"component_name:	"+component_name);
			return null;
		}
		return ret_val;
	}
	private String[][]file_mount_array(String type_string,file_reader fr,engine_kernel ek)
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
		String my_directory_name_array[]= {
				fr.directory_name,
				ek.scene_directory_name						+"assemble_default"+File.separatorChar,
				ek.scene_par.directory_name					+"assemble_default"+File.separatorChar,
				ek.system_par.default_parameter_directory	+"assemble_default"+File.separatorChar
		};
		String charset_name_array[]=new String[] {
				fr.get_charset(),
				ek.scene_charset,
				ek.scene_par.parameter_charset,
				ek.system_par.local_data_charset
		};
		if((type_string.indexOf("charset")>=0)) {
			charset_name_array[0]=fr.get_string();
			for(int i=1,ni=charset_name_array.length;i<ni;i++)
				charset_name_array[i]=charset_name_array[0];
		}
		if(type_string.indexOf("sub")>=0)
			my_file_name=ek.scene_par.scene_sub_directory+my_file_name;
		
		if(type_string.indexOf("absolute")>0)
			return new String[][] 
					{
						new String[] {my_file_name	 },
						new String[] {charset_name_array[3]}
					};
		for(int i=0,ni=my_directory_name_array.length;i<ni;i++) {
			if(new File(my_directory_name_array[i]+my_file_name).exists()) 
				return new String[][] 
						{
							new String[] {my_directory_name_array[i]+my_file_name	 },
							new String[] {charset_name_array[i]}
						};
		}
		debug_information.println("file mount file NOT exits:	",
				"my_file_name:	"+my_file_name+"component_name:	"+component_name);
		return null;
	}
	private String [][]part_driver_mount_array(file_reader fr,
			engine_kernel ek,client_request_response request_response)
	{
		String ret_val[][];
		if(driver_number()<=0)  {
			debug_information.println(
				"Part_driver driver assemble_file_name_and_file_charset error(driver_number()<=0):	",
				"component_name:	"+component_name);
			return null;
		}
		if(driver_array[0].component_part==null) {
			debug_information.println(
				"Part_driver driver assemble_file_name_and_file_charset error(driver_array[0].component_part==null):	",
				"component_name:	"+component_name);
			return null;
		}
		if(driver_array[0].component_part.driver==null) {
			debug_information.println(
				"Part_driver driver assemble_file_name_and_file_charset error(driver_array[0].component_part.driver==null):	",
				"component_name:	"+component_name);
			return null;
		}
		if((ret_val=driver_array[0].component_part.driver.assemble_file_name_and_file_charset(
			fr,driver_array[0].component_part,ek,request_response))==null)
		{
			debug_information.println(
				"Part_driver driver assemble_file_name_and_file_charset error(ret_val==null):	",
				"component_name:	"+component_name);
			return null;
		}
		if(ret_val.length<2) {
			debug_information.println(
				"Part_driver driver assemble_file_name_and_file_charset error(ret_val.length<2):	",
				"component_name:	"+component_name);
			return null;
		}
		if((ret_val[0]!=null)&&(ret_val[1]!=null)) {
			debug_information.println(
				"Part_driver driver assemble_file_name_and_file_charset error(ret_val.length<2):	",
				"component_name:	"+component_name);
			return null;
		}
		if((ret_val[0].length<=0)&&(ret_val[1].length<=0)){
			debug_information.println(
				"Part_driver driver assemble_file_name_and_file_charset error((ret_val[0].length<=0)&&(ret_val[1].length<=0)):	",
				"component_name:	"+component_name);
			return null;
		}
		return ret_val;	
	}
	private String [][]external_part_driver_mount_array(change_name change_part_name,
			file_reader fr,part_container_for_part_search pcfps,
			engine_kernel ek,client_request_response request_response)
	{
		String ret_val[][],external_part_name;
		if((external_part_name=fr.get_string())==null) {
			debug_information.println(
				"external_part_driver driver assemble_file_name_and_file_charset error(external_part_name==null):	",
				"component_name:	"+component_name+"		external_part_name:	"+external_part_name);
			return null;
		}
		part par[];
		String search_part_name=change_part_name.search_change_name(external_part_name,external_part_name);
		if((par=pcfps.search_part(search_part_name))==null){
			search_part_name=change_part_name.search_change_name(search_part_name,search_part_name);
			par=pcfps.search_part(search_part_name);
		}
		if(par==null) {
			debug_information.println(
				"external_part_driver driver assemble_file_name_and_file_charset error(par==null):	",
				"component_name:	"+component_name+"		external_part_name:	"+external_part_name);
			return null;
		}
		if(par.length<1) {
			debug_information.println(
				"external_part_driver driver assemble_file_name_and_file_charset error(par.length<1):	",
				"component_name:	"+component_name+"		external_part_name:	"+external_part_name);
			return null;
		}
		if(par[0].driver==null) {
			debug_information.println(
				"external_part_driver driver assemble_file_name_and_file_charset error(par[0].driver==null):	",
				"component_name:	"+component_name+"		external_part_name:	"+external_part_name);
			return null;
		}
		try {
			ret_val=par[0].driver.assemble_file_name_and_file_charset(fr,par[0],ek,request_response);
		}catch(Exception e) {
			debug_information.println(
				"external_part_driver driver assemble_file_name_and_file_charset execption:	",
				"component_name:	"+component_name+"		external_part_name:	"+external_part_name);
			e.printStackTrace();
			return null;
		}
		if(ret_val==null) {
			debug_information.println(
					"external_part_driver driver assemble_file_name_and_file_charset error(ret_val==null):	",
					"component_name:	"+component_name+"		external_part_name:	"+external_part_name);
			return null;
		}
		if(ret_val.length<2) {
			debug_information.println(
					"external_part_driver driver assemble_file_name_and_file_charset error(ret_val.length<2):	",
					"component_name:	"+component_name+"		external_part_name:	"+external_part_name);
			return null;
		}
		if((ret_val[0]==null)||(ret_val[1]==null)){
			debug_information.println(
					"external_part_driver driver assemble_file_name_and_file_charset error((ret_val[0]==null)||(ret_val[1]==null)):	",
					"component_name:	"+component_name+"		external_part_name:	"+external_part_name);
			return null;
		}
		if((ret_val[0].length<=0)||(ret_val[1].length<=0)) {
			debug_information.println(
					"external_part_driver driver assemble_file_name_and_file_charset error((ret_val[0].length<=0)||(ret_val[1].length<=0)):	",
					"component_name:	"+component_name+"		external_part_name:	"+external_part_name);
			return null;
		}
		return ret_val;
	}
	private void append_children(file_reader fr,String token_string,engine_kernel ek,
			client_request_response request_response,part_container_for_part_search pcfps,
			change_name change_part_name,change_name mount_component_name,
			change_name reverse_mount_component_name,part_type_string_sorter type_string_sorter,
			boolean part_list_flag,long default_display_bitmap,int max_child_number)
	{
		children=new component[0];
		for(String str,assemble_file_name_array[][];;){
			if((str=fr.get_string())==null)
				return;
			switch(str=str.toLowerCase()){
			default:
			{
				int create_child_number;
				try {
					create_child_number=Integer.decode(str);
				}catch(Exception e){
					fr.close();
					debug_information.println("Find error child_number:	",str);
					debug_information.println("File name:	 ",fr.directory_name+fr.file_name);
					debug_information.println("Error:	",e.toString());
					e.printStackTrace();
					create_child_number=0;
				}
				if(create_child_number>0){
					component my_children[]=new component[create_child_number];
					for(int i=0;i<create_child_number;i++)
						my_children[i]=new component(token_string,ek,request_response,
							fr,pcfps,change_part_name,mount_component_name,
							reverse_mount_component_name,type_string_sorter,
							part_list_flag,default_display_bitmap,max_child_number);
					append_child(create_child_number,my_children);
				}
				return;
			}
			case "token_program":
			case "file_program":
			case "charset_file_program":
				initialization.create_initialization(fr,str);
				continue;
			case "part_list":
				part_list_flag=true;
				continue;
			case "not_part_list":
				part_list_flag=false;
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
			case "max_child_number":
				max_child_number=fr.get_int();
				continue;
			case "change_part_name":
			case "charset_change_part_name":
			case "absolute_change_part_name":
			case "absolute_charset_change_part_name":
				String file_name_array[]=new String[1];
				if((file_name_array[0]=fr.get_string())==null)
					file_name_array=new String[0];
				else if(file_name_array[0].length()<=0)
					file_name_array=new String[0];
				else if(str.indexOf("absolute")<0)
					file_name_array[0]=fr.directory_name+file_name_array[0];
				
				if(str.indexOf("charset")<0)
					change_part_name=new change_name(file_name_array,null,fr.get_charset());
				else
					change_part_name=new change_name(file_name_array,null,fr.get_string());
				continue;
			case "mount_on_component":
			{
				int search_id;
				String component_name=fr.get_string(),file_name=fr.get_string();
				if((search_id=file_name.indexOf(':'))<=0)
					continue;
				String file_charset=file_name.substring(0,search_id);
				file_name=file_name.substring(search_id+1);
				switch(file_charset) {
				case "this_charset":
					file_charset=fr.get_charset();
					break;
				}
				if((search_id=file_name.indexOf(':'))<=0)
					continue;
				String directory_type=file_name.substring(0,search_id);
				file_name=file_name.substring(search_id+1);
				switch(directory_type) {
				case "this_directory":
					directory_type="absolute_directory";
					file_name=fr.directory_name+file_reader.separator(file_name);
					break;
				}
				mount_component_name.insert(
					new String[] {component_name,file_charset+":"+directory_type+":"+file_name});
				continue;
			}
			case "mount":
			case "charset_mount":
			case "absolute_mount":
			case "charset_absolute_mount":
			case "sub_mount":
			case "sub_charset_mount":
			case "sub_absolute_mount":
			case "sub_charset_absolute_mount":
				assemble_file_name_array=file_mount_array(str,fr,ek);
				break;
			case "component_driver_mount":
				assemble_file_name_array=component_driver_mount_array(fr,ek,request_response);
				break;
			case "part_driver_mount":
				assemble_file_name_array=part_driver_mount_array(fr,ek,request_response);
				break;
			case "external_part_driver_mount":
				assemble_file_name_array=external_part_driver_mount_array(
						change_part_name,fr,pcfps,ek,request_response);
				break;
			}
			if(assemble_file_name_array==null)
				continue;
			component my_children[]=new component[assemble_file_name_array[0].length];
			int create_child_number=0;
			for(int i=0,ni=assemble_file_name_array[0].length;i<ni;i++) {
				String assemble_file_name	=assemble_file_name_array[0][i];
				String assemble_file_charset=assemble_file_name_array[1][i];
				file_reader mount_fr=new file_reader(assemble_file_name,assemble_file_charset);
				if(mount_fr.eof()){
					debug_information.println(
						"switch assemble file does not exist:	",assemble_file_name);
					debug_information.println("			",fr.directory_name+fr.file_name);
				}else {
					debug_information.println("assemble_file_name:	",assemble_file_name);
					debug_information.println("assemble_file_charset:	",assemble_file_charset);
					try{
						my_children[create_child_number]=new component(
							token_string,ek,request_response,mount_fr,pcfps,change_part_name,
							mount_component_name,reverse_mount_component_name,type_string_sorter,
							part_list_flag,default_display_bitmap,max_child_number);
						create_child_number++;
					}catch(Exception e) {
						debug_information.println("Create scene from ",assemble_file_name+" fail");
						debug_information.println("			",fr.directory_name+fr.file_name);
						e.printStackTrace();
					}
				}
				mount_fr.close();
			}
			if(create_child_number>0)
				append_child(create_child_number,my_children);
		}
	}
	public component_core_4(String token_string,
			engine_kernel ek,client_request_response request_response,
			file_reader fr,part_container_for_part_search pcfps,
			change_name change_part_name,change_name mount_component_name,
			change_name reverse_mount_component_name,part_type_string_sorter type_string_sorter,
			boolean part_list_flag,long default_display_bitmap,int max_child_number)
	{
		super(token_string,ek,request_response,fr,pcfps,
			change_part_name,mount_component_name,reverse_mount_component_name,
			type_string_sorter,part_list_flag,default_display_bitmap,max_child_number);

		append_children(fr,token_string,ek,request_response,pcfps,
			change_part_name,mount_component_name,reverse_mount_component_name,
			type_string_sorter,part_list_flag,default_display_bitmap,max_child_number);
	}
}