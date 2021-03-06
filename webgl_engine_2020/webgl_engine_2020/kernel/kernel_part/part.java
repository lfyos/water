package kernel_part;

import java.io.File;

import kernel_driver.part_driver;
import kernel_engine.system_parameter;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_network.client_request_response;

import kernel_transformation.box;
import kernel_transformation.location;
import kernel_transformation.point;
import kernel_common_class.debug_information;
import kernel_component.component;

public class part
{
	public boolean top_box_part_flag;
	
	public int part_type_id,part_package_id,part_package_sequence_id;
	
	public int render_id,part_id,part_from_id;
	public int permanent_render_id,permanent_part_id,permanent_part_from_id;
	
	public part_driver driver;

	public part_parameter part_par;
	
	public String directory_name,mesh_file_name,material_file_name,file_charset;
	public String system_name,user_name,description_file_name,audio_file_name;
	
	public part_rude part_mesh;
	
	public buffer_object_file_modify_time_and_length boftal;
	
	public box secure_caculate_part_box(component comp,int driver_id,
			int body_id,int face_id,int loop_id,int edge_id,int point_id,
			point p0,point p1)
	{
		if(driver==null)
			debug_information.println("Find No driver part");
		else
			try{
				return driver.caculate_part_box(this,comp,driver_id,body_id,face_id,loop_id,edge_id,point_id,p0,p1);
			}catch(Exception e){
				debug_information.println("secure_caculate_part_box fail:	",e.toString());
				e.printStackTrace();
			}
		debug_information.println("Part user name:",	user_name);
		debug_information.println("Part system name:",	system_name);
		debug_information.println("Mesh_file_name:",	directory_name+mesh_file_name);
		debug_information.println("Material_file_name:",directory_name+material_file_name);
		
		return null;
	}
	public part_rude caculate_part_box_mesh()
	{
		if(part_mesh==null)
			return null;
		box b=secure_caculate_part_box(null,-1,-1,-1,-1,-1,-1,null,null);
		if(b==null)
			return null;
					
		return new part_rude(part_mesh.origin_material,
				new String[][]{part_mesh.default_material},
				new double[][]{part_mesh.default_attribute_double},
				new String[][]{part_mesh.default_attribute_string},
				1,new location[]{new location()},new box[] {b},new String[] {"1"});
	}
	public void destroy()
	{
		directory_name			=null;
		mesh_file_name			=null;
		material_file_name		=null;
		file_charset			=null;
		system_name				=null;
		user_name				=null;
		description_file_name	=null;
		audio_file_name			=null;
		
		if(driver!=null) {
			try{
				driver.destroy();
			}catch(Exception e) {
				debug_information.println("Destroy part driver fail:",e.toString());
				debug_information.println("Part user name:		",	user_name);
				debug_information.println("Part system name:	",	system_name);
				debug_information.println("Directory name:		",	directory_name);
				debug_information.println("Mesh file name:		",	mesh_file_name);
				e.printStackTrace();
			}
			driver=null;
		}
		part_par=null;
		if(part_mesh!=null) {
			part_mesh.destroy();
			part_mesh=null;
		}
		boftal=null;
	}
	private String response_buffer_object_data(primitive_interface p_i,
		int max_material_id,mesh_file_collector file_collector,file_writer fw,
		String front_str,String follow_str,String buffer_object_data_charset)
	{	
		fw.print  ("\t\t\"",front_str);
		fw.println("\"\t\t\t:");
		fw.println("\t\t{");
		
		String file_name=fw.directory_name+fw.file_name;
		file_name=(file_name==null)?"":file_name.trim();
		
		for(int i=0;i<2;i++)
			for(int j=file_name.length()-1;j>=0;j--)
				if(file_name.charAt(j)=='.'){
					file_name=file_name.substring(0,j);
					break;
				}
		graphics_buffer_object_creater_container gbocc;
		switch(front_str){
		default:
			front_str="face";
		case "face":
			gbocc=(new graphics_buffer_object_for_face(p_i,
				max_material_id,this,file_name+"."+front_str,buffer_object_data_charset,
				part_par.max_file_data_length,part_par.create_face_buffer_object_bitmap)).gbocc;
			break;
		case "edge":
			gbocc=(new graphics_buffer_object_for_edge(p_i,
				max_material_id,this,file_name+"."+front_str,buffer_object_data_charset,
				part_par.max_file_data_length,part_par.create_edge_buffer_object_bitmap)).gbocc;
			break;
		case "point":
			gbocc=(new graphics_buffer_object_for_point(p_i,
				max_material_id,this,file_name+"."+front_str,buffer_object_data_charset,
				part_par.max_file_data_length,part_par.create_point_buffer_object_bitmap)).gbocc;
			break;
		}
		
		gbocc.create_head_data(fw,file_collector,front_str,file_name+"."+front_str);
		
		fw.println("\t\t}",follow_str);
		return   "item number:\t"	+Long.toString(gbocc.total_item_number)
				+"\tfile number:\t"	+Integer.toString(gbocc.file_number);
	}
	
	private String create_mesh_and_material_routine(String part_temporary_file_directory,
			String buffer_object_file_name,String buffer_object_file_charset,
			system_parameter system_par,part_container_for_part_search pcps)
	{
		String ret_val="";

		ret_val+="\n\tbuffer object directory:\t"+part_temporary_file_directory;
		ret_val+="\n\tbuffer object file name:\tmesh";	

		file_writer head_fw=new file_writer(buffer_object_file_name,buffer_object_file_charset);
		
		head_fw.println("[");
		head_fw.println();
		
		head_fw.println("{");
		
		head_fw.println("\t\"information\"\t:");
		
		head_fw.println("\t{");
		
		head_fw.println("\t\t\"user_name\"\t\t:\t\"",user_name.replace('\\','/').replace("\"","")+"\",");
		head_fw.println("\t\t\"system_name\"\t:\t\"",system_name.replace('\\','/').replace("\"","")+"\",");
		head_fw.println("\t\t\"mesh_file\"\t\t:\t\"",
			(directory_name+mesh_file_name).replace('\\','/').replace("\"","")+"\",");
		head_fw.println("\t\t\"material_file\"\t:\t\"",
			(directory_name+material_file_name).replace('\\','/').replace("\"","")+"\"");
		
		head_fw.println("\t},");
		
		head_fw.println("\t\"material\"\t\t:");
		head_fw.println("\t[");
		
		debug_information.println("Begin create_part_mesh_and_buffer_object_head:	",directory_name+mesh_file_name);
		
		try{
			part_rude my_part_mesh=driver.create_part_mesh_and_buffer_object_head(this,head_fw,pcps,system_par);
			if(part_mesh==null)
				part_mesh=my_part_mesh;
		}catch(Exception e){
			debug_information.println("create_mesh_and_material fail:",e.toString());
			debug_information.println("Part user name:",	user_name);
			debug_information.println("Part system name:",	system_name);
			debug_information.println("Mesh_file_name:",	directory_name+mesh_file_name);
			debug_information.println("Material_file_name:",directory_name+material_file_name);
			e.printStackTrace();
		}
		debug_information.println("End create_part_mesh_and_buffer_object_head:	",directory_name+mesh_file_name);
		
		head_fw.println("\t],");
		
		head_fw.println("\t\"property\"\t\t:\t");
		head_fw.println("\t{");
		head_fw.println("\t\t\"normal_part_flag\"\t\t\t\t\t:\t",
				(mesh_file_name!=null)?"true,":"false,");
		head_fw.println("\t\t\"top_box_flag\"\t\t\t\t\t\t:\t",
				top_box_part_flag?"true,":"false,");
		head_fw.println("\t\t\"bottom_box_flag\"\t\t\t\t\t:\t",
				(mesh_file_name!=null)?"false,":top_box_part_flag?"false,":"true,");
		head_fw.println("\t\t\"max_component_data_buffer_number\"\t:\t",
				part_par.max_component_data_buffer_number+",");
		
		head_fw.print ("\t\t\"part_box\"\t\t\t\t\t\t\t:\t[");
		box part_box=secure_caculate_part_box(null,0,-1,-1,-1,-1,-1,null,null);
		for(int i=0;(i<2)&&(part_box!=null);i++){
			head_fw.print("[",part_box.p[i].x);
			head_fw.print(",",part_box.p[i].y);
			head_fw.print(",",part_box.p[i].z);
			head_fw.print(",",(i==0)?"1.0],":"1.0]");
		}
		head_fw.println("]");
		head_fw.println("\t},");

		head_fw.println("\t\"data\"\t\t\t:\t");
		head_fw.println("\t{");
		head_fw.print  ("\t\t\"max_buffer_object_data_length\"\t:\t",part_par.max_buffer_object_data_length);
		
		mesh_file_collector file_collector=new mesh_file_collector();
		if(part_mesh==null) {
			head_fw.println();
		}else{
			primitive_interface p_i;
			
			if(this.mesh_file_name==null)
				p_i=new primitive_from_box(this.part_mesh.body_array);
			else
				p_i=new primitive_from_file(directory_name+mesh_file_name,file_charset,
						part_par.max_comment_file_length,system_par.response_block_size);
			
			head_fw.println(",");
			head_fw.println();
			
			ret_val+="\n\t\tmesh " +response_buffer_object_data(p_i,
				system_par.max_material_id,file_collector,head_fw,"face",",",head_fw.get_charset());
			ret_val+="\n\t\tedge " +response_buffer_object_data(p_i,
				system_par.max_material_id,file_collector,head_fw,"edge",",",head_fw.get_charset());
			ret_val+="\n\t\tpoint "+response_buffer_object_data(p_i,
				system_par.max_material_id,file_collector,head_fw,"point"," ",head_fw.get_charset());
			p_i.destroy(part_par.max_compress_file_length,system_par.response_block_size);
		}
		
		head_fw.println("\t}");
		
		head_fw.print  ("}");
		
		file_collector.create_head_data(head_fw,part_par.max_file_head_length);
		
		head_fw.println();
		head_fw.println();
		
		head_fw.println("]");
		
		head_fw.close();
		
		boftal=new buffer_object_file_modify_time_and_length(part_temporary_file_directory+"mesh");
		file_writer boftal_fw=new file_writer(part_temporary_file_directory+"mesh.boftal",
				buffer_object_file_charset);
		boftal.write_out(boftal_fw);
		boftal_fw.close();
		
		create_binary_buffer_object_file.create(part_par.delete_buffer_object_text_file_flag,
				system_par.response_block_size,head_fw.get_charset(),system_par.network_data_charset,
				part_temporary_file_directory+"mesh");

		if(audio_file_name!=null)
			if(file_reader.is_exist(directory_name+audio_file_name))
				file_writer.file_copy(directory_name+audio_file_name,part_temporary_file_directory+"audio.mp3");

		return ret_val;
	}
	private String create_mesh_and_material(String part_temporary_file_directory,
			String buffer_object_file_name,String buffer_object_file_charset,
			system_parameter system_par,part_container_for_part_search pcps)
	{
		String ret_val="";
		
		debug_information.println(
				"Begin:\tlock_number:\t"+system_name+"\t"+Integer.toString(part_par.max_mesh_load_thread_number)+"\t",
				system_par.system_exclusive_number_mutex.get_lock_number());
		
		system_par.system_exclusive_number_mutex.lock_number(part_par.max_mesh_load_thread_number);
		
		debug_information.println("End:\tlock_number:\t"+system_name
				+"\t"+Integer.toString(part_par.max_mesh_load_thread_number)
				+"\t"+system_par.system_exclusive_number_mutex.get_lock_number());

		try{
			ret_val=create_mesh_and_material_routine(part_temporary_file_directory,
					buffer_object_file_name,buffer_object_file_charset,system_par,pcps);
		}catch(Exception e) {
			debug_information.println("create_mesh_and_material_routine exception:",
				"\t"+system_name+"\t"+user_name+"\t"+e.toString());
			e.printStackTrace();
		}
		
		system_par.system_exclusive_number_mutex.unlock_number();
		
		debug_information.println(
			"End:\tunlock_number:\t"+system_name+"\t"+Integer.toString(part_par.max_mesh_load_thread_number)+"\t",
			system_par.system_exclusive_number_mutex.get_lock_number());
		
		return ret_val;
	}
	private void clear_mesh_file_content()
	{
		String clear_file_name_array[]=new String[]
		{
			material_file_name,
			description_file_name,
			audio_file_name,
			mesh_file_name+".face",
			mesh_file_name+".face.gzip",
			mesh_file_name+".edge",
			mesh_file_name+".edge.gzip"
		};
		for(int i=0,ni=clear_file_name_array.length;i<ni;i++) {
			if(clear_file_name_array[i]==null)
				continue;
			if(!(part_par.clear_model_file_flag[i]))
				continue;
			String clear_file_name=file_reader.separator(directory_name+clear_file_name_array[i]);
			File f=new File(clear_file_name);
			if(!(f.exists()))
				continue;
			long t=f.lastModified();
			if(!(f.delete())) {
				debug_information.println("Delete file fail in clear_file_content	:	",clear_file_name);
				continue;
			}
			try{
				if(!(f.createNewFile())) {
					debug_information.println("Create file fail in clear_file_content	:	",clear_file_name);
					continue;
				}
			}catch(Exception e) {
				debug_information.println("Create file error in clear_file_content	:	",clear_file_name);
				debug_information.println("Create file error in clear_file_content	:	",e.toString());
				e.printStackTrace();
				continue;
			}
			if(!(f.setLastModified(t)))
				debug_information.println("setLastModified fail in clear_file_content	:	",clear_file_name);
		}
	}
	public String load_mesh_and_create_buffer_object(
			part copy_from_part,long last_modified_time,
			String part_temporary_file_directory,String part_temporary_file_charset,
			system_parameter system_par,part_container_for_part_search pcps)
	{
		String str;
		
		str =  "\tuser part name:\t\t\t"		+user_name;
		str+="\n\tsystem part name:\t\t"		+system_name;
		str+="\n\tpart permanent ID:\t\t"		+Integer.toString(permanent_render_id)+","
												+Integer.toString(permanent_part_id);
		str+="\n\tdirectory:\t\t\t"				+directory_name;
		str+="\n\tmesh file name :\t\t"			+((mesh_file_name==null)?"no mesh file name":mesh_file_name);
		str+="\n\tmaterial file name:\t\t"		+material_file_name;
		str+="\n\tdescription file name:\t\t"	+description_file_name;
		str+="\n\taudio_file_name:\t\t"			+audio_file_name;

		do{
			String token_file_name			=part_temporary_file_directory+"part.token";
			String buffer_object_file_name	=part_temporary_file_directory+"mesh.head.txt";
			String cfp_mesh_file_name=copy_from_part.directory_name+copy_from_part.mesh_file_name;
			String cfp_material_file_name=copy_from_part.directory_name+copy_from_part.material_file_name;
	
			long token_file_last_time=new File(token_file_name).lastModified();
			if(token_file_last_time>last_modified_time)
				if(token_file_last_time>part_par.last_modified_time)
					if(token_file_last_time>new File(cfp_mesh_file_name).lastModified())
						if(token_file_last_time>new File(cfp_material_file_name).lastModified()) {
							if((part_mesh==null)&&(mesh_file_name!=null)){
								String my_file_path=file_reader.separator(directory_name+mesh_file_name);
								if(new File(my_file_path).exists()){
									file_reader fr=new file_reader(my_file_path,file_charset);
									part_mesh=new part_rude(fr,part_par.scale_value);
									fr.close();
								}
							}
							file_reader boftal_fr=new file_reader(
								part_temporary_file_directory+"mesh.boftal",part_temporary_file_charset);
							boftal=new buffer_object_file_modify_time_and_length(boftal_fr);
							boftal_fr.close();
							break;
						}
			file_writer.file_delete(part_temporary_file_directory);
			file_writer.make_directory(part_temporary_file_directory);
			str+=create_mesh_and_material(part_temporary_file_directory,
					buffer_object_file_name,part_temporary_file_charset,system_par,pcps);
			clear_mesh_file_content();
			file_writer.file_touch(token_file_name,true);
		}while(false);

		return str;
	}
	public part(
			int my_part_type_id,part_parameter my_part_par,String my_directory_name,String my_file_charset,
			String my_user_name,String my_system_name,String my_mesh_file_name,String my_material_file_name,
			String my_description_file_name,String my_audio_file_name)
	{
		top_box_part_flag		=false;
		part_type_id			=my_part_type_id;
		part_package_id			=-1;
		part_package_sequence_id=-1;

		render_id				=0;
		part_id					=0;
		part_from_id			=-1;
		
		permanent_render_id		=0;
		permanent_part_id		=0;
		permanent_part_from_id	=-1;
		
		part_par				=my_part_par.clone();
			
		directory_name			=my_directory_name;
		file_charset			=my_file_charset;
		user_name				=my_user_name;
		system_name				=my_system_name;
		mesh_file_name			=(my_mesh_file_name==null)?null:my_mesh_file_name;
		description_file_name	=my_description_file_name;
		audio_file_name			=my_audio_file_name;
		material_file_name		=my_material_file_name;
		part_mesh				=null;

		driver					=null;
		
		boftal					=null;
	}
	public part(part p,system_parameter system_par,client_request_response request_response)
	{
		top_box_part_flag		=p.top_box_part_flag;
		part_type_id			=p.part_type_id;
		part_package_id			=p.part_package_id;
		part_package_sequence_id=p.part_package_sequence_id;
	
		render_id				=p.render_id;
		part_id					=p.part_id;
		part_from_id			=p.part_from_id;
		
		permanent_render_id		=p.permanent_render_id;
		permanent_part_id		=p.permanent_part_id;
		permanent_part_from_id	=p.permanent_part_from_id;
		
		part_par				=p.part_par.clone();
		
		directory_name			=p.directory_name;
		file_charset			=p.file_charset;
		user_name				=p.user_name;
		system_name				=p.system_name;
		
		mesh_file_name			=(p.mesh_file_name==null)?null:p.mesh_file_name;
		
		description_file_name	=p.description_file_name;
		audio_file_name			=p.audio_file_name;
		material_file_name		=p.material_file_name;

		if(p.part_mesh==null)
			part_mesh=null;
		else
			part_mesh=new part_rude(p.part_mesh);
		
		boftal=p.boftal;

		try{
			driver=p.driver.clone(p,this,system_par,request_response);
		}catch(Exception e){
			driver=null;
			debug_information.println("Part clone fail");
			debug_information.println(e.toString());
			debug_information.println("Part user name:	",		p.user_name);
			debug_information.println("Part system name:	",	p.system_name);
			debug_information.println("Directory name:	",		p.directory_name);
			debug_information.println("Mesh file name:	",		p.mesh_file_name);
			e.printStackTrace();
		}
	}
}
