package kernel_render;

import java.io.File;
import java.util.ArrayList;

import kernel_part.part;
import kernel_part.part_rude;
import kernel_part.permanent_part_id_encoder;
import kernel_scene.part_package;
import kernel_scene.scene_parameter;
import kernel_scene.system_parameter;
import kernel_part.part_loader;
import kernel_transformation.box;
import kernel_part.part_parameter;
import kernel_transformation.location;
import kernel_file_manager.file_reader;
import kernel_part.part_loader_container;
import kernel_interface.client_process_bar;
import kernel_common_class.debug_information;
import kernel_common_class.tree_string_locker_container;
import kernel_network.client_request_response;
import kernel_part.part_container_for_part_search;
import kernel_component.component_load_source_container;
import kernel_part.buffer_object_file_modify_time_and_length_container;

public class render_container
{
	public ArrayList<render> renders,sorted_renders;
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
		if(sorted_renders!=null) {
			for(int i=0,ni=sorted_renders.size();i<ni;i++) {
				if((r=sorted_renders.get(i))!=null){
					r.destroy();
					sorted_renders.set(i,null);
				}
			}
			sorted_renders=null;
		}
		if(system_part_package!=null) {
			system_part_package.destroy();
			system_part_package=null;
		}
		if(type_part_package!=null) {
			for(int i=0,ni=type_part_package.length;i<ni;i++) {
				type_part_package[i].destroy();
				type_part_package[i]=null;
			}
			type_part_package=null;
		}
		if(scene_part_package!=null) {
			scene_part_package.destroy();
			scene_part_package=null;
		}
	}
	
	public render search_render(String my_render_name)
	{
		if(sorted_renders!=null)
			for(int begin_pointer=0,end_pointer=sorted_renders.size()-1,result;begin_pointer<=end_pointer;) {
				int middle_pointer=(begin_pointer+end_pointer)/2;
				render r=sorted_renders.get(middle_pointer);
				if((result=r.render_name.compareTo(my_render_name))<0)
					begin_pointer=middle_pointer+1;
				else if(result>0)
					end_pointer=middle_pointer-1;
				else 
					return r;
			}
		return null;
	}
	
	public ArrayList<part> part_array_list(int part_type_id)
	{
		ArrayList<part> ret_val=new ArrayList<part>();
		
		if(renders==null)
			return ret_val;
		for(int i=0,ni=renders.size();i<ni;i++) {
			render r=renders.get(i);
			if(r==null)
				continue;
			if(r.parts==null)
				continue;
			for(int j=0,nj=r.parts.size();j<nj;j++){
				part p=r.parts.get(j);
				if(p==null)
					continue;
				if(part_type_id>=0)
					if(p.part_type_id!=part_type_id)
						continue;
				ret_val.add(p);
			}
		}
		return ret_val;
	}
	public void load_part(long part_type_code,int part_normal_bottom_box_top_box_flag,
		part_loader_container part_loader_cont,system_parameter system_par,scene_parameter scene_par,
		ArrayList<buffer_object_file_modify_time_and_length_container> boftal_container,
		tree_string_locker_container string_locker_container,
		client_process_bar process_bar,String process_bar_title,String fast_load_type)
	{
		if(renders==null)
			return;

		debug_information.println();
		debug_information.println("Begin loading part meshes");

		ArrayList<part_loader> already_loaded_part=new ArrayList<part_loader>();
		
		int load_number=0,all_number=0;
		for(int pass_id=0;pass_id<2;pass_id++) {
			for(int i=0,ni=renders.size();i<ni;i++) {
				render r;
				if((r=renders.get(i))==null)
					continue;
				if(r.parts==null)
					continue;
				for(int j=0,part_number=r.parts.size();j<part_number;j++) {
					part p;
					if((p=r.parts.get(j))==null)
						continue;
					if(((((long)1)<<p.part_type_id)&part_type_code)==0)
						continue;
					int my_part_flag=	(p.is_normal_part()		?1:0)+
										(p.is_bottom_box_part()	?2:0)+
										(p.is_top_box_part()	?4:0);
					if((my_part_flag&part_normal_bottom_box_top_box_flag)==0)
						continue;
					if(pass_id==0)
						all_number++;
					else{
						part_loader_cont.load(p,fast_load_type,system_par,scene_par,
								string_locker_container,already_loaded_part,boftal_container);
						load_number++;
						if(process_bar!=null)
							process_bar.set_process_bar(false,
								process_bar_title,p.user_name,load_number,all_number);
					}
				}
			}
			if(all_number<1)
				all_number=1;
		}
		
		part_loader_container.wait_for_completion(already_loaded_part,system_par,scene_par);
		
		if(process_bar!=null)
			process_bar.set_process_bar(false,process_bar_title,"",all_number,all_number);
		
		debug_information.println();
		debug_information.println("End loading part meshes:\t",all_number);
		debug_information.println();
		
		return;
	}
	
	public void create_bottom_box_part(part_container_for_part_search pcps,
			client_request_response request_response,permanent_part_id_encoder encoder[],
			system_parameter system_par,scene_parameter scene_par)
	{
		for(int i=0,j,part_number=pcps.get_number();i<part_number;i=j){
			part i_part=pcps.data_list.get(i);
			for(j=i;j<part_number;j++)
				if(i_part.system_name.compareTo(pcps.data_list.get(j).system_name)!=0)
					break;
			part insert_part=null;
			box  part_box	=null;
			
			for(;i<j;i++) {
				i_part=pcps.data_list.get(i);
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
			ren.add_part(add_part,encoder);
			pcps.append(add_part);
		}
	}
	private void load_one_shader(
			component_load_source_container component_load_source_cont,part_container_for_part_search pcps,
			String driver_name,file_reader f_render_list,String shader_file_name,
			system_parameter system_par,scene_parameter scene_par,render ren,
			permanent_part_id_encoder encoder[],client_request_response request_response)
	{
		while(!(f_render_list.eof())){
			String str;
			String part_type_string			=f_render_list.get_string();
			String assemble_part_name		=f_render_list.get_string();
			String part_parameter_file_name	=(str=f_render_list.get_string())==null?"":str;
			if((part_parameter_file_name=file_reader.separator(part_parameter_file_name)).compareTo("")==0)
				continue;
			if(file_reader.is_exist(f_render_list.directory_name+part_parameter_file_name))
				part_parameter_file_name=f_render_list.directory_name+part_parameter_file_name;
			else
				part_parameter_file_name=system_par.parameter_directory
						+"part_parameter"+File.separator+part_parameter_file_name;
			
			if(!(new File(part_parameter_file_name).exists())) {
				debug_information.println("part parameter file:	",part_parameter_file_name+"	not exist");
				continue;
			}

			part_parameter part_par=new part_parameter(part_type_string,
				assemble_part_name,part_parameter_file_name,f_render_list.get_charset());

			String get_part_list_result[];
			try{
				get_part_list_result=ren.driver.get_part_list(ren,f_render_list,part_par,
					component_load_source_cont,request_response,system_par,scene_par);
			}catch(Exception e){
				e.printStackTrace();
				
				debug_information.println("Execute get_part_list fail:		",e.toString());
				debug_information.println("Driver name:		",	driver_name);
				debug_information.println("Shader file:		",	shader_file_name);
				debug_information.println("render file:		",	f_render_list.directory_name+f_render_list.file_name);

				continue;
			}
			
			if(get_part_list_result==null) {
				debug_information.println("part list file is NULL");
				debug_information.println("Driver name:		",	driver_name);
				debug_information.println("Shader file:		",	shader_file_name);
				debug_information.println("render file:		",	f_render_list.directory_name+f_render_list.file_name);
				continue;
			}
			
			for(int i=0,ni=get_part_list_result.length-1;i<ni;i++,i++){
				if(get_part_list_result[i]==null) {
					debug_information.println("get_part_list_result[i]==null");
					debug_information.println("Driver name:		",	driver_name);
					debug_information.println("Shader file:		",	shader_file_name);
					debug_information.println("render file:		",	f_render_list.directory_name+f_render_list.file_name);
					continue;
				}
				
				File par_list_f;			
				if(!((par_list_f=new File(get_part_list_result[i])).exists())) {
					debug_information.println("part list file:	",get_part_list_result[i]+"	not exist");
					debug_information.println("Driver name:		",	driver_name);
					debug_information.println("Shader file:		",	shader_file_name);
					debug_information.println("render file:		",	f_render_list.directory_name+f_render_list.file_name);
					continue;
				}
				if(par_list_f.lastModified()<f_render_list.lastModified_time)
					par_list_f.setLastModified(f_render_list.lastModified_time);
				
				debug_information.println();
				debug_information.println("Begin load part list file:	",	get_part_list_result[i]);
				debug_information.println("part parameter file:		",		part_parameter_file_name);
	
				String part_file_system_charset=get_part_list_result[i+1];
				if(part_file_system_charset==null)
					part_file_system_charset=f_render_list.get_charset();

				int render_id=(renders==null)?0:renders.size();
				ren.add_part(pcps,ren,component_load_source_cont,
					part_par,system_par,scene_par,get_part_list_result[i],part_file_system_charset,
					"part_mesh_"+Integer.toString(render_id)+"_",encoder,request_response);

				debug_information.println("End load part list file:	",	part_file_system_charset);
				debug_information.println("part parameter file:		",	part_parameter_file_name);
				debug_information.println();
			}
			component_load_source_cont.register_component(f_render_list,
				part_par.render_load_assemble_type,system_par.default_system_mount_component_name);
		}
	}
	public void load_shader(
		component_load_source_container component_load_source_cont,
		part_container_for_part_search pcps,long last_modify_time,
		String shader_file_name,String shader_file_charset,
		int part_type_id,system_parameter system_par,scene_parameter scene_par,
		permanent_part_id_encoder encoder[],client_request_response request_response)
	{
		File f;
		if((f=new File(shader_file_name)).lastModified()<last_modify_time)
			f.setLastModified(last_modify_time);
		
		file_reader f_shader=new file_reader(shader_file_name,shader_file_charset);
		if(f_shader.error_flag()){
			debug_information.println();
			debug_information.println("shader configure file error,file name is ",shader_file_name);
			f_shader.close();
			return;
		}
		debug_information.println();
		debug_information.println("Begin shader and part initialization,file name is ",shader_file_name);

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
							f_shader,request_response,system_par,scene_par);
			if(ren.driver==null) {
				debug_information.print  ("ren.driver==null		",driver_name);
				continue;
			}
			String render_list_file_name[]=ren.driver.get_render_list(f_shader,ren,
					component_load_source_cont,request_response,system_par,scene_par);
			if(render_list_file_name==null){
				debug_information.print  ("render list file is NULL	",	driver_name);
				continue;
			}
			for(int i=0,ni=render_list_file_name.length-1;i<ni;i++,i++){
				if(render_list_file_name[i]==null){
					debug_information.print  ("render_list_file_name[0]==null	",	driver_name);
					continue;
				}
				render_list_file_name[i]=file_reader.separator(render_list_file_name[i]);
				if(!((f=new File(render_list_file_name[i])).exists())) {
					debug_information.println(render_list_file_name[0],"		not exist");
					continue;
				}
				
				if(f.lastModified()<my_shader_last_time)
					f.setLastModified(my_shader_last_time);
	
				String file_system_charset=render_list_file_name[i+1];
				if(file_system_charset==null)
					file_system_charset=f_shader.get_charset();
				
				file_reader f_render_list=new file_reader(render_list_file_name[i],file_system_charset);
				load_one_shader(component_load_source_cont,pcps,driver_name,
					f_render_list,f_shader.directory_name+f_shader.file_name,
					system_par,scene_par,ren,encoder,request_response);
				f_render_list.close();
			}
			
			if(ren.parts==null)
				ren.destroy();
			else if(ren.parts.size()<=0) 
				ren.destroy();
			else
				renders.add(renders.size(),ren);
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
				renders.add(i,new render(ren_con.renders.get(i),
						request_response,system_par,scene_par));
		system_part_package	=new part_package(ren_con.system_part_package);
		type_part_package	=new part_package[ren_con.type_part_package.length];
		for(int i=0,ni=ren_con.type_part_package.length;i<ni;i++)
			type_part_package[i]=new part_package(ren_con.type_part_package[i]);
		scene_part_package	=new part_package(ren_con.scene_part_package);
	}
}