package kernel_render;

import java.io.File;

import kernel_common_class.debug_information;
import kernel_driver.render_driver;
import kernel_engine.part_type_string_sorter;
import kernel_engine.scene_parameter;
import kernel_engine.system_parameter;
import kernel_engine.part_package;
import kernel_file_manager.file_reader;
import kernel_interface.client_process_bar;
import kernel_network.client_request_response;
import kernel_part.buffer_object_file_modify_time_and_length_container;
import kernel_part.part;
import kernel_part.part_container;
import kernel_part.part_loader;
import kernel_part.part_parameter;
import kernel_part.part_rude;
import kernel_transformation.box;
import kernel_transformation.location;
import kernel_part.part_loader_container;
import kernel_part.part_container_for_part_search;

public class render_container
{
	public void destroy()
	{
		if(renders!=null) {
			for(int i=0,ni=renders.length;i<ni;i++)
				if(renders[i]!=null){
					renders[i].destroy();
					renders[i]=null;
				}
			renders=null;
		}
		if(system_part_package!=null) {
			system_part_package.destroy();
			system_part_package=null;
		}
		if(type_part_package!=null) {
			type_part_package.destroy();
			type_part_package=null;
		}
		if(scene_part_package!=null) {
			scene_part_package.destroy();
			scene_part_package=null;
		}
	}

	public render renders[];
	public part_package system_part_package,type_part_package,scene_part_package;
	
	public part[] part_array(boolean part_mesh_flag,int part_type_id)
	{
		int effective_part_number=0;

		if(renders!=null)
			for(int i=0,ni=renders.length;i<ni;i++)
				if(renders[i]!=null)
					if(renders[i].parts!=null)
						for(int j=0,nj=renders[i].parts.length;j<nj;j++)
							if(renders[i].parts[j]!=null)
								if((renders[i].parts[j].part_mesh!=null)||part_mesh_flag) {
									if(part_type_id>=0)
										if(renders[i].parts[j].part_type_id!=part_type_id)
											continue;
									effective_part_number++;
								}
		if(effective_part_number<=0)
			return new part[] {};
		part ret_val[]=new part[effective_part_number];
		effective_part_number=0;
		for(int i=0,ni=renders.length;i<ni;i++)
			if(renders[i]!=null)
				if(renders[i].parts!=null)
					for(int j=0,nj=renders[i].parts.length;j<nj;j++)
						if(renders[i].parts[j]!=null)
							if((renders[i].parts[j].part_mesh!=null)||part_mesh_flag) {
								if(part_type_id>=0)
									if(renders[i].parts[j].part_type_id!=part_type_id)
										continue;
								ret_val[effective_part_number++]=renders[i].parts[j];
							}
		return ret_val;
	}
	public part get_copy_from_part(part p)
	{
		while(p.part_from_id>=0)
			p=renders[p.render_id].parts[p.part_from_id];
		return p;
	}
	public void load_part(int part_type,int part_flag,part_loader_container part_loader_cont,
			system_parameter system_par,scene_parameter scene_par,part_container_for_part_search pcps,
			buffer_object_file_modify_time_and_length_container boftal_container,
			String process_bar_title,client_process_bar process_bar,part_container part_cont_for_delete_file)
	{
		if(renders==null)
			return;

		debug_information.println();
		debug_information.println("Begin loading part meshes");
		debug_information.println();
		
		part p;
		int load_number=0,all_number=0;

		for(int i=0,ni=renders.length;i<ni;i++)
			if(renders[i].parts!=null)
				for(int j=0,part_number=renders[i].parts.length;j<part_number;j++)
					if((p=renders[i].parts[j])!=null){
						if(((1<<p.part_type_id)&part_type)==0)
							continue;
						int my_part_flag=0;
						my_part_flag+=p.is_normal_part()	?1:0;
						my_part_flag+=p.is_bottom_box_part()?2:0;
						my_part_flag+=p.is_top_box_part()	?4:0;
						
						if((my_part_flag&part_flag)==0)
							continue;
						all_number++;
					}
		if(process_bar!=null)
			process_bar.set_process_bar(true,process_bar_title,0,(all_number<1)?1:all_number);
		
		part_loader already_loaded_part[]=new part_loader[]{};
		for(int i=0,ni=renders.length;i<ni;i++)
			if(renders[i].parts!=null)
				for(int j=0,part_number=renders[i].parts.length;j<part_number;j++)
					if((p=renders[i].parts[j])!=null){
						if(((1<<p.part_type_id)&part_type)==0)
							continue;
						int my_part_flag=0;
						my_part_flag+=p.is_normal_part()	?1:0;
						my_part_flag+=p.is_bottom_box_part()?2:0;
						my_part_flag+=p.is_top_box_part()	?4:0;
						
						if((my_part_flag&part_flag)==0)
							continue;
						
						already_loaded_part=part_loader_cont.load(
								p,get_copy_from_part(p),-1,system_par,scene_par,
								part_cont_for_delete_file,already_loaded_part,pcps,boftal_container);
						
						if(process_bar!=null)
							process_bar.set_process_bar(false,process_bar_title,load_number++,(all_number<1)?1:all_number);
					}
		
		part_loader_container.wait_for_completion(already_loaded_part,system_par,scene_par);
		if(process_bar!=null)
			process_bar.set_process_bar(false,process_bar_title,(all_number<1)?1:all_number,(all_number<1)?1:all_number);
		
		debug_information.println();
		debug_information.println("End loading part meshes\t",load_number);
		debug_information.println();
		
		return;
	}
	
	public void create_bottom_box_part(part_container_for_part_search pcps,
			client_request_response request_response,system_parameter system_par)
	{
		for(int i=0,j=0,part_number=pcps.get_number();i<part_number;i=j){
			for(j=i;j<part_number;j++)
				if(pcps.data_array[i].system_name.compareTo(pcps.data_array[j].system_name)!=0)
					break;
			part p=null;
			box b=null;
			for(;i<j;i++)
				if(pcps.data_array[i].is_normal_part()){
					if(p==null)
						if(pcps.data_array[i].part_mesh!=null)
							if(pcps.data_array[i].part_par.do_create_bottom_box_flag)
								if(pcps.data_array[i].driver!=null)
									if((b=pcps.data_array[i].secure_caculate_part_box(null,-1,-1,-1,-1,-1,-1,null,null))!=null)
										p=pcps.data_array[i];
				}else {
					p=null;
					b=null;
					break;
				}
			if((p==null)||(b==null))
				continue;
			part add_part=new part(p.part_type_id,false,p.part_par.box_part_parameter(),
						p.directory_name,p.file_charset,p.user_name,p.system_name,
						null,p.material_file_name,p.description_file_name,p.audio_file_name);
			if((add_part.part_mesh=new part_rude(1,new part[] {p},new location[]{new location()},new box[] {b}))==null)
				continue;
			renders[p.render_id].add_part(p.render_id,add_part);
			add_part.part_from_id			=p.part_id;
			add_part.permanent_part_from_id	=p.permanent_part_id;
			try {
				add_part.driver=p.driver.clone(p,add_part,system_par,request_response);
				pcps.append_one_part(add_part);
			}catch(Exception e) {
				debug_information.println("Execte part driver clone() fail");
				debug_information.println("Part user name:",	p.user_name);
				debug_information.println("Part system name:",	p.system_name);
				debug_information.println("Mesh_file_name:",	p.directory_name+p.mesh_file_name);
				debug_information.println("Material_file_name:",p.directory_name+p.material_file_name);
				debug_information.println(e.toString());
				e.printStackTrace();
				renders[p.render_id].delete_last_part();
				continue;
			}
			pcps.append_one_part(add_part);
		}
	}
	
	private void load_one_shader(part_container_for_part_search pcps,
			String driver_name,String load_sub_directory_name,
			String render_list_file_name,String file_system_charset,String shader_file_name,
			int part_type_id,part_type_string_sorter my_part_type_string_sorter,
			system_parameter system_par,scene_parameter scene_par,client_request_response request_response)
	{
		Object obj;
		try{
			obj=Class.forName(driver_name).getConstructor().newInstance();
		}catch(Exception e){
			debug_information.println("Create render driver fail:		",e.toString());
			debug_information.println("Driver name is ",driver_name);
			e.printStackTrace();
			return;
		}
		if(!(obj instanceof render_driver)){
			debug_information.println("render driver class name error:		",driver_name);
			return;
	    }
		render_driver my_render_driver=(render_driver)obj;
    
		debug_information.print  ("Driver name:	",			driver_name);
		debug_information.print  ("		render list file:	",	render_list_file_name);
		debug_information.println(file_reader.is_exist(render_list_file_name)?"":"		not exist");
		
		file_reader f_render_list=new file_reader(render_list_file_name,file_system_charset);
		render ren=new render(my_render_driver);
		
		while(!(f_render_list.eof())){
			String str;
			String part_type_string			=f_render_list.get_string();
			String assemble_part_name		=f_render_list.get_string();
			String part_parameter_file_name	=(str=f_render_list.get_string())==null?"":str;
			String par_list_file_name		=(str=f_render_list.get_string())==null?"":str;
			if((par_list_file_name=file_reader.separator(par_list_file_name)).compareTo("")==0)
				continue;
			part_parameter_file_name=file_reader.separator(part_parameter_file_name);
			if(file_reader.is_exist(f_render_list.directory_name+part_parameter_file_name))
				part_parameter_file_name=f_render_list.directory_name+part_parameter_file_name;
			else
				part_parameter_file_name=system_par.default_parameter_directory
						+"part_parameter"+File.separator+part_parameter_file_name;
			
			part_parameter part_par=new part_parameter(part_type_string,
				assemble_part_name,part_parameter_file_name,f_render_list.get_charset());

			boolean giveup_part_load_flag=false;
			if(my_part_type_string_sorter==null)
				str="part_type_string_sorter==null";
			else if(my_part_type_string_sorter.search(part_type_string)<0) {
				giveup_part_load_flag=true;
				str="part_type_string_sorter search fail";
			}else
				str="";

			debug_information.println("		part list file:	",par_list_file_name+"			"+str);

			String get_part_list_result[]=null;
			try{
				get_part_list_result=my_render_driver.get_part_list(giveup_part_load_flag,
					f_render_list,load_sub_directory_name,par_list_file_name,part_par,system_par,request_response);
			}catch(Exception e){
				debug_information.println("Execute get_part_list fail:		",e.toString());
				debug_information.println("Driver name:		",	driver_name);
				debug_information.println("Shader file:		",	shader_file_name);
				debug_information.println("List file:		",	f_render_list.directory_name+f_render_list.file_name);
				giveup_part_load_flag=true;
				
				e.printStackTrace();
			}
			
			par_list_file_name=null;
			String part_list_file_charset=null;
			
			if(get_part_list_result!=null) 
				if(get_part_list_result.length>0){
					par_list_file_name=get_part_list_result[0];
					if(get_part_list_result.length>1)
						part_list_file_charset=get_part_list_result[1];
					else
						part_list_file_charset=f_render_list.get_charset();
				}

			if(giveup_part_load_flag||(par_list_file_name==null))
				continue;

			boolean par_list_file_flag=file_reader.is_exist(par_list_file_name);
			boolean part_parameter_file_flag=file_reader.is_exist(part_parameter_file_name);
			
			debug_information.println();
			debug_information.print  ("begin load part list file:	",	par_list_file_name);
			debug_information.println(par_list_file_flag?"":"	not exist");
			debug_information.print  ("part parameter file:		",	part_parameter_file_name);
			debug_information.println(part_parameter_file_flag?"":"	not exist");
			
			if(part_parameter_file_flag&&par_list_file_flag){
				File part_f=new File(par_list_file_name);
				if(part_f.lastModified()<f_render_list.lastModified_time)
						part_f.setLastModified(f_render_list.lastModified_time);
				
				int render_id=(renders==null)?0:renders.length;
				ren.add_part(pcps,my_render_driver,part_type_id,render_id,
					part_par,system_par,par_list_file_name,part_list_file_charset,
					"part_mesh_"+Integer.toString(render_id)+"_",request_response);
			}
			debug_information.print  ("end load part list file:	",	par_list_file_name);
			debug_information.println(par_list_file_flag?"":"	not exist");
		}

		f_render_list.close();
		
		try {
			my_render_driver.destroy();
		}catch(Exception e){
			debug_information.println("Destroy render driver fail:		",e.toString());
			debug_information.println("Driver name is ",driver_name);
			e.printStackTrace();
		}
		
		my_render_driver=null;
		
		if(ren.parts==null)
			return;
		if(ren.parts.length<=0)
			return;
		if(renders==null)
			renders=new render[1];
		else{
			render []tmp_renders=renders;
			renders=new render[tmp_renders.length+1];
			for(int i=0;i<tmp_renders.length;i++)
				renders[i]=tmp_renders[i];
		}
		renders[renders.length-1]=ren;
		return;
	}
	public void load_shader(part_container_for_part_search pcps,
		long last_modify_time,String shader_file_name,
		String shader_file_charset,String load_sub_directory_name,int part_type_id,
		part_type_string_sorter my_part_type_string_sorter,system_parameter system_par,
		scene_parameter scene_par,client_request_response request_response)
	{
		{
			File f=new File(shader_file_name);
			if(f.lastModified()<last_modify_time)
				f.setLastModified(last_modify_time);
		}
		
		file_reader f_shader=new file_reader(shader_file_name,shader_file_charset);
		if(f_shader.error_flag()) {
			debug_information.println();
			debug_information.println("shader configure file error,file name is ",shader_file_name);
			f_shader.close();
			return;
		}
		debug_information.println();
		debug_information.println("Begin shader and part initialization,file name is ",shader_file_name);

		for(String str;!(f_shader.eof());){
			String driver_name=(str=f_shader.get_string())==null?"":str.trim();			
			String render_list_file_name=(str=f_shader.get_string())==null?"":file_reader.separator(str.trim());
			if(render_list_file_name.compareTo("")==0)
				continue;
			{
				File render_f=new File(f_shader.directory_name+render_list_file_name);
				if(render_f.exists())
					if(f_shader.lastModified_time>render_f.lastModified())
						render_f.setLastModified(f_shader.lastModified_time);
			}
			load_one_shader(pcps,driver_name,load_sub_directory_name,
				f_shader.directory_name+render_list_file_name,f_shader.get_charset(),
				f_shader.directory_name+f_shader.file_name,part_type_id,
				my_part_type_string_sorter,system_par,scene_par,request_response);
		}
		debug_information.println();
		debug_information.println("End shader and part initialization");
			
		f_shader.close();
	}
	public render_container()
	{
		renders					=new render[0];
		system_part_package		=new part_package();
		type_part_package		=new part_package();
		scene_part_package		=new part_package();
	}
	public render_container(render_container ren_con,
			system_parameter system_par,client_request_response request_response)
	{
		renders=new render[0];
		if(ren_con.renders!=null)
			if(ren_con.renders.length>0){
				renders=new render[ren_con.renders.length];
				for(int i=0,n=ren_con.renders.length;i<n;i++)
					renders[i]=new render(ren_con.renders[i],system_par,request_response);
			}
		system_part_package	=new part_package(ren_con.system_part_package);
		type_part_package	=new part_package(ren_con.type_part_package);
		scene_part_package	=new part_package(ren_con.scene_part_package);
	}
}