package kernel_part;

import java.io.File;

import kernel_driver.part_driver;
import kernel_transformation.box;
import kernel_component.component;
import kernel_transformation.point;
import kernel_scene.scene_parameter;
import kernel_scene.system_parameter;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_common_class.jason_string;
import kernel_file_manager.file_directory;
import kernel_common_class.debug_information;
import kernel_network.client_request_response;
import kernel_common_class.compress_file_data;

public class part
{
	private boolean top_box_part_flag;
	
	public boolean is_top_box_part()
	{
		return (mesh_file_name==null)&&top_box_part_flag;
	};
	public boolean is_bottom_box_part()
	{
		return (mesh_file_name==null)&&(!top_box_part_flag);
	};
	public boolean is_normal_part()
	{
		return (mesh_file_name!=null);
	};
	
	public int part_type_id,part_package_id,part_package_sequence_id;
	
	public int render_id,part_id,part_from_id;
	public int permanent_part_id,permanent_part_from_id;
	
	public part_driver driver;

	public part_parameter part_par;
	
	public String directory_name,mesh_file_name,material_file_name,file_charset;
	public String system_name,user_name,description_file_name,audio_file_name;
	
	public part_rude part_mesh;
	
	public buffer_object_file_modify_time_and_length boftal;
	
	public box secure_caculate_part_box(component comp,int driver_id,
			int body_id,int face_id,int primitive_id,int vertex_id,int loop_id,int edge_id,
			point p0,point p1)
	{
		if(driver==null)
			debug_information.println("Find No driver part");
		else
			try{
				return driver.caculate_part_box(
						this,comp,driver_id,body_id,face_id,
						primitive_id,vertex_id,loop_id,edge_id,p0,p1);
			}catch(Exception e){
				e.printStackTrace();
				debug_information.println("secure_caculate_part_box fail:	",e.toString());
			}
		debug_information.println("Part user name:",	user_name);
		debug_information.println("Part system name:",	system_name);
		debug_information.println("Mesh_file_name:",	directory_name+mesh_file_name);
		debug_information.println("Material_file_name:",directory_name+material_file_name);
		
		return null;
	}
	public box secure_caculate_part_box(component comp,int driver_id)
	{
		return secure_caculate_part_box(comp,driver_id,-1,-1,-1,-1,-1,-1,null,null);
	}
	public box secure_caculate_part_box()
	{
		return secure_caculate_part_box(null,-1,-1,-1,-1,-1,-1,-1,null,null);
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
				e.printStackTrace();
				
				debug_information.println("Destroy part driver fail:",e.toString());
				debug_information.println("Part user name:		",	user_name);
				debug_information.println("Part system name:	",	system_name);
				debug_information.println("Directory name:		",	directory_name);
				debug_information.println("Mesh file name:		",	mesh_file_name);
				
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
	private String response_buffer_object_data(primitive_interface p_i,int max_material_id,
		mesh_file_collector file_collector,file_writer head_fw,String front_str,String follow_str)
	{	
		int dot_pos;
		String my_charset=head_fw.get_charset();
		String my_file_name=head_fw.directory_name+head_fw.file_name;
		if((dot_pos=my_file_name.lastIndexOf(".head.txt"))>=0)
			my_file_name=my_file_name.substring(0,dot_pos);
		my_file_name+="."+front_str;

		head_fw.print  ("\t\t\"",front_str);
		head_fw.println("\"\t:");
		head_fw.println("\t\t{");

		graphics_buffer_object_creater_container gbocc;
		switch(front_str){
		default:
		case "face":
			gbocc=new graphics_buffer_object_for_face(p_i,max_material_id,this,my_file_name,my_charset).gbocc;
			break;
		case "edge":
			gbocc=new graphics_buffer_object_for_edge(p_i,max_material_id,this,my_file_name,my_charset).gbocc;
			break;
		case "point":
			gbocc=new graphics_buffer_object_for_point(p_i,max_material_id,this,my_file_name,my_charset).gbocc;
			break;
		}
		
		int file_number=gbocc.create_head_data(head_fw,file_collector,front_str,my_file_name);
		
		head_fw.println("\t\t}",follow_str);
		
		return   "item number:\t"	+Long.toString(gbocc.total_item_number)
				+"\tfile number:\t"	+Integer.toString(file_number);
	}
	public boolean load_part_mesh()
	{
		if(is_normal_part()){
			if(part_mesh!=null){
				if(part_mesh.test_loaded_flag())
					return false;
				part_mesh.destroy();
			}
			String my_file_path=directory_name+mesh_file_name;
			my_file_path=file_directory.replace_special_char(my_file_path);
			file_reader fr=new file_reader(my_file_path,file_charset);
			part_mesh=new part_rude(fr);
			fr.close();
			return true;
		}
		return false;
	}
	public boolean unload_part_mesh()
	{
		if(is_normal_part())
			if(part_mesh!=null)
				return part_mesh.free_memory();
		return false;
	}
	private String create_mesh_and_material_routine(
			String part_temporary_file_directory,system_parameter system_par,scene_parameter scene_par)
	{
		String ret_val="\n\tbuffer object directory:"+part_temporary_file_directory;

		file_writer head_fw=new file_writer(
			part_temporary_file_directory+"mesh.head.txt",system_par.network_data_charset);
		
		head_fw.println("[");
		head_fw.println();
		
		head_fw.println("{");
		
		head_fw.println("\t\"information\"\t:");
			head_fw.println("\t{");
				head_fw.println("\t\t\"user_name\"\t\t:\t",		jason_string.change_string(user_name)+",");
				head_fw.println("\t\t\"system_name\"\t\t:\t",	jason_string.change_string(system_name)+",");
				head_fw.println("\t\t\"mesh_file\"\t\t:\t",		jason_string.change_string(directory_name+mesh_file_name)+",");
				head_fw.println("\t\t\"material_file\"\t\t:\t",	jason_string.change_string(directory_name+material_file_name)+",");
				head_fw.println("\t\t\"this_file\"\t\t:\t",		jason_string.change_string(head_fw.directory_name+head_fw.file_name));
			head_fw.println("\t},");
		
		head_fw.println("\t\"material\"\t:");
			head_fw.println("\t[");
				try{
					driver.create_part_material_in_head(head_fw,this,system_par,scene_par);
				}catch(Exception e){
					e.printStackTrace();
					
					debug_information.println("create_mesh_and_material fail:",	e.toString());
					debug_information.println("Part user name:",				user_name);
					debug_information.println("Part system name:",				system_name);
					debug_information.println("Mesh_file_name:",				directory_name+mesh_file_name);
					debug_information.println("Material_file_name:",			directory_name+material_file_name);
				}
			head_fw.println("\t],");
		
		head_fw.println("\t\"property\"\t:");
			head_fw.println("\t{");
				head_fw.println("\t\t\"normal_part_flag\"\t:\t",is_normal_part()	?"true,":"false,");
				head_fw.println("\t\t\"bottom_box_flag\"\t:\t",	is_bottom_box_part()?"true,":"false,");
				head_fw.println("\t\t\"top_box_flag\"\t\t:\t",	is_top_box_part()	?"true,":"false,");
				
				head_fw.print ("\t\t\"part_box\"\t\t:\t[");
					box part_box=secure_caculate_part_box();
					for(int i=0;(i<2)&&(part_box!=null);i++){
						head_fw.print("[",part_box.p[i].x);
						head_fw.print(",",part_box.p[i].y);
						head_fw.print(",",part_box.p[i].z);
						head_fw.print((i==0)?",1.0],":",1.0]");
					}
				head_fw.println("]");
			head_fw.println("\t},");

		mesh_file_collector file_collector=new mesh_file_collector();
		
		head_fw.println("\t\"data\"\t\t:\t");
			head_fw.println("\t{");
				head_fw.print  ("\t\t\"max_buffer_object_data_length\"\t:\t",
						part_par.max_buffer_object_data_length);
				head_fw.println(",");

				if(part_mesh==null) {
					String str[]=new String[]{
						"\"face\"	:","{",	"	\"region_data\"	:	[]"	,"},",
						"\"edge\"	:","{",	"	\"region_data\"	:	[]"	,"},",
						"\"point\"	:","{",	"	\"region_data\"	:	[]"	,"}"
					};
					for(int i=0,ni=str.length;i<ni;i++)
						head_fw.println("		",str[i]);
				}else{
					primitive_interface p_i;
					
					if(is_normal_part())
						p_i=new primitive_from_file(directory_name+mesh_file_name,
										file_charset,system_par.file_read_write_buffer_size);
					else
						p_i=new primitive_from_box(part_mesh.body_array);
		
					ret_val+="\n\t\tmesh " +response_buffer_object_data(p_i,
							system_par.max_material_id,file_collector,head_fw,"face",",");
					ret_val+="\n\t\tedge " +response_buffer_object_data(p_i,
							system_par.max_material_id,file_collector,head_fw,"edge",",");
					ret_val+="\n\t\tpoint "+response_buffer_object_data(p_i,
							system_par.max_material_id,file_collector,head_fw,"point"," ");
					
					p_i.destroy();
				}
			head_fw.println("\t}");
		head_fw.print  ("}");
		
		file_collector.create_head_data(head_fw,part_par.max_file_head_length);
		file_collector.destroy();
		
		head_fw.println();
		head_fw.println();
		
		head_fw.println("]");
		
		head_fw.close();
	
		return ret_val;
	}
	private void create_part_network_compress_file(int response_block_size,String root_file_name)
	{
		String my_head_file_name		=root_file_name+".head.txt";
		String my_head_gzip_file_name	=root_file_name+".head.gzip_text";

		compress_file_data.do_compress(new File(my_head_file_name),
				new File(my_head_gzip_file_name),response_block_size,"gzip");
		file_writer.file_delete(my_head_file_name);
		String file_type[]=new String[]{".face",".edge",".point"};
		for(int i=0,ni=file_type.length;i<ni;i++)
			for(int j=0;;j++){
				String my_file_name=root_file_name+file_type[i]+j;
				String my_text_file_name=my_file_name+".txt";
				String my_gzip_file_name=my_file_name+".gzip_text";
				String my_flag_file_name=my_file_name+".in_head_flag";
				if(!(new File(my_text_file_name).exists()))
					break;
				if(new File(my_flag_file_name).exists())
					file_writer.file_delete(my_flag_file_name);
				else
					compress_file_data.do_compress(new File(my_text_file_name),
						new File(my_gzip_file_name),response_block_size,"gzip");
				file_writer.file_delete(my_text_file_name);
			}
	}
	public String load_mesh_and_create_buffer_object(
			String part_temporary_file_directory,
			system_parameter system_par,scene_parameter scene_par)
	{
		String str;
		
		str ="\n\tuser part name:\t\t\t"	+user_name;
		str+="\n\tsystem part name:\t\t"	+system_name;
		str+="\n\tpart permanent ID:\t\t"	+permanent_part_id;
		str+="\n\tdirectory:\t\t\t\t"		+directory_name;
		str+="\n\tmesh file name :\t\t";
		str+=(mesh_file_name==null)?"no mesh file name":mesh_file_name;
		str+="\n\tmaterial file name:\t\t"	+material_file_name;
		str+="\n\tdescription file name:\t"	+description_file_name;
		str+="\n\taudio_file_name:\t\t"		+audio_file_name;

		if(new File(part_temporary_file_directory).exists())
			file_writer.file_delete(part_temporary_file_directory);
		file_writer.make_directory(part_temporary_file_directory);
		
		load_part_mesh();
		
		str+=create_mesh_and_material_routine(
				part_temporary_file_directory,system_par,scene_par);
		
		String root_file_name=part_temporary_file_directory+"mesh";
		boftal=new buffer_object_file_modify_time_and_length(
					part_mesh,root_file_name,system_par.local_data_charset);
		create_part_network_compress_file(
					system_par.file_read_write_buffer_size,root_file_name);

		String audio_dest=part_temporary_file_directory+"audio.mp3";
		if(new File(audio_dest).exists())
			file_writer.file_delete(audio_dest);
		if(audio_file_name!=null) {
			String audio_source=directory_name+audio_file_name;
			if(new File(audio_source).exists())
				file_writer.file_copy(audio_source,audio_dest);	
		}
		
		if(part_mesh!=null)
			part_mesh.free_memory();

		return str;
	}
	public part(int my_part_type_id,boolean my_top_box_part_flag,
			part_parameter my_part_par,String my_directory_name,String my_file_charset,
			String my_user_name,String my_system_name,String my_mesh_file_name,
			String my_material_file_name,String my_description_file_name,String my_audio_file_name)
	{
		top_box_part_flag		=my_top_box_part_flag;
		part_type_id			=my_part_type_id;
		part_package_id			=-1;
		part_package_sequence_id=-1;

		render_id				=0;
		part_id					=0;
		part_from_id			=-1;
		
		permanent_part_id		=0;
		permanent_part_from_id	=-1;
		
		part_par				=my_part_par.clone();
			
		directory_name			=my_directory_name;
		file_charset			=my_file_charset;
		user_name				=my_user_name;
		system_name				=my_system_name;
		mesh_file_name			=(my_mesh_file_name==null)
				?null:file_directory.replace_special_char(my_mesh_file_name);
		material_file_name		=(my_material_file_name==null)
				?null:file_directory.replace_special_char(my_material_file_name);
		description_file_name	=(my_description_file_name==null)
				?null:file_directory.replace_special_char(my_description_file_name);
		audio_file_name			=(my_audio_file_name==null)
				?null:file_directory.replace_special_char(my_audio_file_name);
		
		part_mesh				=null;

		driver					=null;
		
		boftal					=new buffer_object_file_modify_time_and_length();
	}
	public part(part p,client_request_response request_response,
			system_parameter system_par,scene_parameter scene_par)
	{
		top_box_part_flag		=p.top_box_part_flag;
		part_type_id			=p.part_type_id;
		part_package_id			=p.part_package_id;
		part_package_sequence_id=p.part_package_sequence_id;
	
		render_id				=p.render_id;
		part_id					=p.part_id;
		part_from_id			=p.part_from_id;
		
		permanent_part_id		=p.permanent_part_id;
		permanent_part_from_id	=p.permanent_part_from_id;
		
		part_par				=p.part_par.clone();
		
		directory_name			=p.directory_name;
		file_charset			=p.file_charset;
		user_name				=p.user_name;
		system_name				=p.system_name;
		
		mesh_file_name			=p.mesh_file_name;
		
		description_file_name	=p.description_file_name;
		audio_file_name			=p.audio_file_name;
		material_file_name		=p.material_file_name;

		if(p.part_mesh==null)
			part_mesh=null;
		else
			part_mesh=new part_rude(p.part_mesh);
		
		boftal=p.boftal;

		try{
			driver=p.driver.clone(p,this,request_response,system_par,scene_par);
		}catch(Exception e){
			e.printStackTrace();
			
			driver=null;
			debug_information.println("Part clone fail");
			debug_information.println(e.toString());
			debug_information.println("Part user name:	",		p.user_name);
			debug_information.println("Part system name:	",	p.system_name);
			debug_information.println("Directory name:	",		p.directory_name);
			debug_information.println("Mesh file name:	",		p.mesh_file_name);
		}
	}
}
