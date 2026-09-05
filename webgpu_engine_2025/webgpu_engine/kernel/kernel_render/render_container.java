package kernel_render;

import java.io.File;
import java.util.ArrayList;

import kernel_part.part;
import kernel_part.part_rude;
import kernel_part.part_loader;
import kernel_scene.part_package;
import kernel_transformation.box;
import kernel_part.part_parameter;
import kernel_scene.scene_parameter;
import kernel_scene.system_parameter;
import kernel_transformation.location;
import kernel_file_manager.file_reader;
import kernel_part.part_loader_container;
import kernel_file_manager.file_directory;
import kernel_common_class.debug_information;
import kernel_part.permanent_part_id_encoder;
import kernel_scene.scene_load_call_parameter;
import kernel_network.client_request_response;
import kernel_part.part_container_for_part_search;

public class render_container
{
	public ArrayList<render> renders;
	public part_package system_part_package,type_part_package[],scene_part_package;
	
	public void destroy()
	{
		render r;
		
		if(renders!=null) {
			for(int i=0,ni=renders.size();i<ni;i++) {
				if((r=renders.get(i))!=null){
					r.destroy();
					renders.set(i,null);
				}
			}
			renders=null;
		}
		if(system_part_package!=null) 
			system_part_package=null;

		if(type_part_package!=null) {
			for(int i=0,ni=type_part_package.length;i<ni;i++) 
				type_part_package[i]=null;
			type_part_package=null;
		}
		if(scene_part_package!=null)
			scene_part_package=null;
	}
	public ArrayList<part> part_array_list(int part_type_id)
	{
		ArrayList<part> ret_val=new ArrayList<part>();
		
		if(renders!=null)
			for(render my_render:renders)
				if(my_render!=null)
					if(my_render.parts!=null)
						for(part my_part:my_render.parts)
							if(my_part!=null) {
								if(part_type_id>=0)
									if(my_part.part_type_id!=part_type_id)
										continue;
								ret_val.add(my_part);
							}
		return ret_val;
	}
	public void load_part(long part_type_code,int part_normal_bottom_box_top_box_flag,
		system_parameter system_par,scene_parameter scene_par,String process_bar_title,
		String fast_load_type,scene_load_call_parameter load_par)
	{
		if(renders==null)
			return;

		debug_information.println();
		debug_information.println("Begin loading part meshes");

		ArrayList<part_loader> already_loaded_part=new ArrayList<part_loader>();
		
		int load_number=0,all_number=0;
		for(int pass_id=0;pass_id<2;pass_id++) {
			for(var my_render:renders) {
				if(my_render==null)
					continue;
				if(my_render.parts==null)
					continue;
				for(part my_part:my_render.parts) {
					if(my_part==null)
						continue;
					if(((((long)1)<<my_part.part_type_id)&part_type_code)==0)
						continue;
					int my_part_flag=	(my_part.is_normal_part()		?1:0)+
										(my_part.is_bottom_box_part()	?2:0)+
										(my_part.is_top_box_part()		?4:0);
					if((my_part_flag&part_normal_bottom_box_top_box_flag)==0)
						continue;
					if(pass_id==0)
						all_number++;
					else{
						load_par.part_loader_cont.load(my_part,fast_load_type,already_loaded_part,
								load_par.string_locker_cont,system_par,scene_par,load_par.boftal_cont);
						if(load_par.process_bar!=null)
							load_par.process_bar.set_process_bar((load_number<=0),
								process_bar_title,my_part.user_name,load_number,all_number);
						load_number++;
					}
				}
			}
			if(all_number<1)
				all_number=1;
		}
		
		part_loader_container.wait_for_completion(already_loaded_part,system_par,scene_par);
		
		if(load_par.process_bar!=null)
			load_par.process_bar.set_process_bar(false,process_bar_title,"",all_number,all_number);
		
		debug_information.println();
		debug_information.println("End loading part meshes:\t",all_number);
		debug_information.println();
		
		return;
	}
	public void create_bottom_box_part(
			part_container_for_part_search pcps,
			client_request_response request_response,
			permanent_part_id_encoder part_id_encoder,
			system_parameter system_par,scene_parameter scene_par)
	{
		var data_list=pcps.tree_get_value_list();
		for(int i=0,j,part_number=data_list.size();i<part_number;i=j){
			part i_part=data_list.get(i);
			for(j=i;j<part_number;j++)
				if(i_part.system_name.compareTo(data_list.get(j).system_name)!=0)
					break;
			part insert_part=null;
			box  part_box	=null;
			
			for(;i<j;i++) {
				i_part=data_list.get(i);
				boolean normal_flag	=i_part.is_normal_part();
				boolean do_flag		=i_part.part_par.do_create_bottom_box_flag;
				if((!normal_flag)||(!do_flag)){
					insert_part=null;
					part_box=null;
					break;
				}
				if((insert_part!=null)&&(part_box!=null))
					continue;
				if((i_part.part_mesh==null)||(i_part.driver==null))
					continue;
				part_box=i_part.secure_caculate_part_box(null,-1,-1,-1,-1,-1,-1,-1,null,null);
				insert_part=(part_box==null)?null:i_part;
			}
			if((insert_part==null)||(part_box==null))
				continue;
			render ren;
			if((ren=renders.get(insert_part.render_id))==null)
				continue;
			
			part add_part=new part(insert_part.part_type_id,false,
					insert_part.part_par.box_part_parameter(),insert_part.directory_name,
					insert_part.file_charset,insert_part.user_name,insert_part.system_name,null,
					insert_part.material_file_name,insert_part.description_file_name,
					insert_part.audio_file_name);
			add_part.part_mesh=new part_rude(insert_part.part_mesh,1,
					new part[] {insert_part},new location[]{new location()},new box[] {part_box});

			add_part.part_from_id			=insert_part.part_id;
			add_part.permanent_part_from_id	=insert_part.permanent_part_id;
			try {
				add_part.driver=insert_part.driver.clone(
					insert_part,add_part,request_response,system_par,scene_par);
			}catch(Exception e) {
				
				e.printStackTrace();
				
				debug_information.println("Execte part driver clone() fail");
				debug_information.println("Part user name:",	insert_part.user_name);
				debug_information.println("Part system name:",	insert_part.system_name);
				debug_information.println("Mesh_file_name:",	
						insert_part.directory_name+insert_part.mesh_file_name);
				debug_information.println("Material_file_name:",
						insert_part.directory_name+insert_part.material_file_name);
				debug_information.println(e.toString());
				
				continue;
			}
			ren.add_part(add_part,part_id_encoder);
			pcps.add(add_part.system_name,add_part);
		}
	}
	private void load_one_shader(file_reader f_render_list,
			String driver_name,render ren,load_shader_parameter load_par)
	{
		while(!(f_render_list.eof())){
			String str;
			String part_type_string			=f_render_list.get_string();
			String assemble_part_name		=f_render_list.get_string();
			String part_parameter_file_name	=(str=f_render_list.get_string())==null?"":str;
			part_parameter_file_name=file_directory.replace_special_char(part_parameter_file_name);
			if(part_parameter_file_name.length()<=0)
				continue;
			if(file_reader.is_exist(f_render_list.directory_name+part_parameter_file_name))
				part_parameter_file_name=f_render_list.directory_name+part_parameter_file_name;
			else
				part_parameter_file_name=load_par.system_par.parameter_directory
						+"part_parameter"+File.separatorChar+part_parameter_file_name;
			
			if(!(new File(part_parameter_file_name).exists())) {
				debug_information.println("part parameter file:	",part_parameter_file_name+"	not exist");
				continue;
			}

			part_parameter part_par=new part_parameter(part_type_string,
				assemble_part_name,part_parameter_file_name,f_render_list.get_charset());

			String get_part_list_result[];
			try{
				get_part_list_result=ren.driver.get_part_list(ren,f_render_list,part_par,
						load_par.component_load_source_cont,load_par.request_response,
						load_par.system_par,load_par.scene_par);
			}catch(Exception e){
				e.printStackTrace();
				
				debug_information.println("Execute get_part_list fail:		",e.toString());
				debug_information.println("Driver name:		",	driver_name);
				debug_information.println("render file:		",	f_render_list.directory_name+f_render_list.file_name);

				continue;
			}
			
			if(get_part_list_result==null) {
				debug_information.println("part list file is NULL");
				debug_information.println("Driver name:		",	driver_name);
				debug_information.println("render file:		",	f_render_list.directory_name+f_render_list.file_name);
				continue;
			}
			
			for(int i=0,ni=get_part_list_result.length-1;i<ni;i++,i++){
				if(get_part_list_result[i]==null) {
					debug_information.println("get_part_list_result[i]==null");
					debug_information.println("Driver name:		",	driver_name);
					debug_information.println("render file:		",	f_render_list.directory_name+f_render_list.file_name);
					continue;
				}
				
				File par_list_f;			
				if(!((par_list_f=new File(get_part_list_result[i])).exists())) {
					debug_information.println("part list file:	",get_part_list_result[i]+"	not exist");
					debug_information.println("Driver name:		",	driver_name);
					debug_information.println("render file:		",	f_render_list.directory_name+f_render_list.file_name);
					continue;
				}
				if(par_list_f.lastModified()<f_render_list.lastModified_time)
					par_list_f.setLastModified(f_render_list.lastModified_time);
				
				debug_information.println();
				debug_information.println("Begin load part list file:	",	get_part_list_result[i]);
				debug_information.println("part parameter file:		",		part_parameter_file_name);
	
				int render_id=(renders==null)?0:renders.size();
				
				ren.add_part(get_part_list_result[i],get_part_list_result[i+1],
						"part_mesh_"+Integer.toString(render_id)+"_",ren,part_par,load_par);

				debug_information.println("End load part list file:	",	get_part_list_result[i]);
				debug_information.println("part parameter file:		",	part_parameter_file_name);
				debug_information.println();
			}
			load_par.component_load_source_cont.register_component(
				f_render_list,part_par.render_load_assemble_type,
				load_par.system_par.default_system_mount_component_name);
		}
	}
	public void load_shader(String shader_path_name,String shader_file_charset,
			long last_modify_time,int part_type_id,load_shader_parameter load_par)
	{
		file_reader f_shader=new file_reader(shader_path_name,shader_file_charset);
		if(f_shader.error_flag()){
			debug_information.println();
			debug_information.println("shader configure file error,file name is ",shader_path_name);
			f_shader.close();
			return;
		}	
		if(f_shader.lastModified_time<last_modify_time) {
			new File(shader_path_name).setLastModified(last_modify_time);
			f_shader.lastModified_time=last_modify_time;
		}

		debug_information.println();
		debug_information.println("Begin shader and part initialization,file name is ",shader_path_name);

		for(long my_shader_last_time=f_shader.lastModified_time;!(f_shader.eof());){
			String render_name,driver_name;
			if((render_name=f_shader.get_string())==null)
				continue;
			if((render_name=render_name.trim()).length()<=0)
				continue;
			if((driver_name=f_shader.get_string())==null)
				continue;
			if((driver_name=driver_name.trim()).length()<=0)
				continue;
			
			debug_information.println("render name:	",	render_name);
			debug_information.println("Driver name:	",	driver_name);
			
			int render_id=(renders==null)?0:(renders.size());
			render ren=new render(render_id,part_type_id,render_name,driver_name,
							f_shader,load_par.request_response,load_par.system_par,load_par.scene_par);
			if(ren.driver==null) {
				debug_information.print  ("ren.driver==null		",driver_name);
				continue;
			}
			String render_list_file_name[]=ren.driver.get_render_list(
					f_shader,ren,load_par.component_load_source_cont,
					load_par.request_response,load_par.system_par,load_par.scene_par);
			if(render_list_file_name==null){
				debug_information.print  ("render list file is NULL	",	driver_name);
				continue;
			}
			for(int i=0,ni=render_list_file_name.length-1;i<ni;i+=2){
				if(render_list_file_name[i]==null){
					debug_information.print  ("render_list_file_name[i]==null	",	driver_name);
					continue;
				}
				render_list_file_name[i]=file_directory.replace_special_char(render_list_file_name[i]);
				file_reader f_render_list=new file_reader(render_list_file_name[i],render_list_file_name[i+1]);
				if(f_render_list.error_flag()) {
					f_render_list.close();
					debug_information.println(render_list_file_name[i],"		not exist");
					continue;
				}
				if(f_render_list.lastModified_time<my_shader_last_time) {
					new File(render_list_file_name[i]).setLastModified(my_shader_last_time);
					f_render_list.lastModified_time=my_shader_last_time;
				}
				load_one_shader(f_render_list,driver_name,ren,load_par);
						
				f_render_list.close();
			}

			if(ren.parts!=null)
				if(ren.parts.size()>0){
					renders.add(renders.size(),ren);
					continue;
				}
			ren.destroy();
		}
		debug_information.println("End shader and part initialization");
		debug_information.println();
			
		f_shader.close();
	}
	public render_container()
	{
		renders					=new ArrayList<render>();
		system_part_package		=new part_package();
		type_part_package		=new part_package[] {};
		scene_part_package		=new part_package();
	}
	public render_container(render_container ren_con,
			client_request_response request_response,
			system_parameter system_par,scene_parameter scene_par)
	{
		renders=new ArrayList<render>();
		if(ren_con.renders!=null)
			for(int i=0,ni=ren_con.renders.size();i<ni;i++)
				renders.add(i,new render(ren_con.renders.get(i),request_response,system_par,scene_par));
		system_part_package	=new part_package(ren_con.system_part_package);
		type_part_package	=new part_package[ren_con.type_part_package.length];
		for(int i=0,ni=ren_con.type_part_package.length;i<ni;i++)
			type_part_package[i]=new part_package(ren_con.type_part_package[i]);
		scene_part_package	=new part_package(ren_con.scene_part_package);
	}
}