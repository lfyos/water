package kernel_engine;

import java.io.File;
import java.nio.charset.Charset;
import java.util.concurrent.locks.ReentrantLock;

import kernel_engine.engine_kernel;
import kernel_engine.client_information;
import kernel_engine.engine_call_result;
import kernel_client_interface.dispatch_request_main;
import kernel_common_class.debug_information;
import kernel_component.component_load_source_container;
import kernel_file_manager.file_reader;
import kernel_interface.client_process_bar;
import kernel_network.client_request_response;
import kernel_part.part_loader_container;
import kernel_render.render_container;
import kernel_security.client_session;

public class engine_kernel_link_list
{
	volatile private int link_number;
	public int increase_link_number()
	{
		link_number_lock.lock();
		int ret_val=(++link_number);
		link_number_lock.unlock();
		return ret_val;
	}
	public int decrease_link_number()
	{
		link_number_lock.lock();
		int ret_val=(--link_number);
		link_number_lock.unlock();
		return ret_val;
	}
	public int get_link_number()
	{
		link_number_lock.lock();
		int ret_val=link_number;
		link_number_lock.unlock();
		return ret_val;
	}

	public engine_kernel ek;
	public engine_kernel_link_list next_link_list;
	
	private boolean initilization_flag;
	private ReentrantLock engine_kernel_lock,link_number_lock;

	private double create_top_part_expand_ratio;
	private double create_top_part_left_ratio;
	private String scene_name;
	private String scene_title;
	private String scene_directory_name;
	private String scene_file_name;
	private String scene_charset;
	private long   scene_list_file_last_modified_time;
	private String parameter_file_name;
	private String parameter_charset;
	private String extra_parameter_file_name;
	private String extra_parameter_charset;

	private boolean fill_search_item(String test_scenename,
			String my_file_name,String my_file_charset,system_parameter system_par)
	{
		file_reader f_type=new file_reader(my_file_name,my_file_charset);
		if(f_type.error_flag()){
			f_type.close();
			debug_information.println("Open scene_file_name fail	:	",my_file_name);
			return true;
		}
		while(!(f_type.eof())){
			String my_mount_file_name;
			if((my_mount_file_name=f_type.get_string())==null)
				break;
			if(my_mount_file_name.compareTo("")==0)
				break;
			my_mount_file_name=file_reader.separator(my_mount_file_name);
			file_reader f_name=new file_reader(
					f_type.directory_name+my_mount_file_name,f_type.get_charset());
			while(!(f_name.eof())){
				String scene_search_name	=f_name.get_string();
				scene_title					=f_name.get_string();
				scene_file_name				=f_name.get_string();
				create_top_part_expand_ratio=f_name.get_long();
				create_top_part_left_ratio	=f_name.get_long();
				parameter_file_name			=f_name.get_string();
				extra_parameter_file_name	=f_name.get_string();
				
				if(extra_parameter_file_name==null)
					break;
				if(extra_parameter_file_name.compareTo("")==0)
					break;
				if(test_scenename!=null)
					if(test_scenename.compareTo(scene_search_name)!=0)
						continue;
				
				scene_file_name				=file_reader.separator(scene_file_name);
				parameter_file_name			=file_reader.separator(parameter_file_name);
				extra_parameter_file_name	=file_reader.separator(extra_parameter_file_name);
				
				if(!(file_reader.is_exist(f_name.directory_name+scene_file_name))) {
					debug_information.println("Find unexist scene_assembly_file_name:",
						scene_search_name+"\t"+scene_title+"\t"+scene_file_name);
					continue;
				}
				if(file_reader.is_exist(f_name.directory_name+parameter_file_name))
					parameter_file_name=f_name.directory_name+parameter_file_name;
				else{
					parameter_file_name=system_par.default_parameter_directory
						+"assemble_parameter"+File.separator+parameter_file_name;
					if(!(file_reader.is_exist(parameter_file_name))) {
						debug_information.println("Find unexist scene parameter_file_name:",
								scene_search_name+"\t"+scene_title+"\t"+parameter_file_name);
						continue;
					}
				}
				if(file_reader.is_exist(f_name.directory_name+extra_parameter_file_name))
					extra_parameter_file_name=f_name.directory_name+extra_parameter_file_name;
				else {
					extra_parameter_file_name=system_par.default_parameter_directory
						+"assemble_parameter"+File.separator+extra_parameter_file_name;
					if(!(file_reader.is_exist(extra_parameter_file_name))) {
						debug_information.println("Find unexist scene extra_parameter_file_name:",
								scene_search_name+"\t"+scene_title+"\t"+extra_parameter_file_name);
						continue;
					}
				}
				
				debug_information.println("scene_name	              	:	",	scene_search_name);
				debug_information.println("scene_title              	:	",	scene_title);
				debug_information.println("scene_file_name          	:	",	f_name.directory_name+scene_file_name);
				debug_information.println("parameter_file_name		:	",		parameter_file_name);
				debug_information.println("extra_parameter_file_name	:	",	extra_parameter_file_name);
				
				f_name.close();
				f_type.close();
				
				scene_name							=scene_search_name;
				scene_directory_name				=f_name.directory_name;
				scene_charset						=f_name.get_charset();
				parameter_charset					=f_name.get_charset();
				extra_parameter_charset				=f_name.get_charset();
				scene_list_file_last_modified_time	=(f_type.lastModified_time<f_name.lastModified_time)
														?f_name.lastModified_time:f_type.lastModified_time;
				return false;
			}
			f_name.close();
		}
		f_type.close();
		return true;
	}

	public engine_kernel_link_list(system_parameter system_par,
			client_session session,client_request_response request_response,
			render_container original_render,part_loader_container my_part_loader_cont,
			String my_scene_name,String link_name,engine_kernel_link_list my_next_link_list)
	{
		link_number=0;
		initilization_flag=true;
	
		if(fill_search_item(my_scene_name,session.scene_file_name,session.scene_file_charset,system_par))
			if(fill_search_item(null,session.scene_file_name,session.scene_file_charset,system_par)){
				ek=null;
				return;
			}
		ek=new engine_kernel(request_response,
				create_top_part_expand_ratio,create_top_part_left_ratio,
				scene_name,scene_title,link_name,scene_directory_name,
				scene_file_name,scene_charset,scene_list_file_last_modified_time,
				parameter_file_name,parameter_charset,
				extra_parameter_file_name,extra_parameter_charset,system_par,
				original_render,my_part_loader_cont);

		next_link_list=my_next_link_list;

		engine_kernel_lock=new ReentrantLock(); 
		link_number_lock=new ReentrantLock();
	}
	static private engine_call_result create_file_result(
			engine_kernel ek,client_information ci,String file_name,String file_charset)
	{
		File f=new File(file_name);
		if(f.exists())
			file_name=f.getAbsolutePath();
		else{
			debug_information.println("create_file_proxy_url error, file NOT exist\t",file_name);
			return null;
		}
		if(!(f.isFile())){
			debug_information.println("create_file_proxy_url error, file NOT normal file\t",file_name);
			return null;
		}
		if(!(f.canRead())){
			debug_information.println("create_file_proxy_url error, file CAN NOT read\t",file_name);
			return null;
		}
		
		String proxy_url;
		if((proxy_url=ci.get_file_proxy_url(f,ek.system_par))!=null){
			ci.request_response.implementor.redirect_url(proxy_url,ek.scene_par.scene_cors_string);
			ci.statistics_client.response_proxy_data_length+=f.length();
			return null;
		}
		caculate_charset_compress_file_name cccfn=new caculate_charset_compress_file_name(f,ek.system_par);
		if(cccfn.content_type_id<0)
			ci.statistics_client.response_no_type_file_data_length+=f.length();
		else
			ci.statistics_client.modify_response_data_length(cccfn.content_type_id,f.length());
		ci.request_response.response_content_type=cccfn.content_type_str;

		return new engine_call_result(
				cccfn.file_name,file_charset,cccfn.charset_file_name,
				cccfn.compress_file_name,null,ek.scene_par.scene_cors_string);
	}
	private engine_call_result get_engine_result_routine(
			component_load_source_container component_load_source_cont,
			client_process_bar process_bar,client_session session,
			engine_kernel_link_list_and_client_information ec,
			client_request_response my_request_response,long delay_time_length,
			interface_statistics statistics_interface,int engine_current_number [])
	{
		if(ek==null){
			debug_information.println(
					"(ek==null) in function get_engine_result() of engine_container");
			return null;
		}
		if(initilization_flag){
			initilization_flag=false;
			if(ek.component_cont==null) {
				component_load_source_cont=new component_load_source_container(component_load_source_cont);
				ek.load(component_load_source_cont,my_request_response,process_bar);
				if(ek.component_cont.root_component!=null) {
					engine_current_number[0]++;
					engine_current_number[1]+=ek.component_cont.root_component.component_id+1;
				}
				component_load_source_cont.destroy();
			}
		}
		if(ec.client_information==null){
			if(ek.component_cont.root_component==null){
				debug_information.println(
					"(ek.component_cont.root_component==null) in function get_engine_result() of engine_container");
				return null;
			}
			ec.client_information=new client_information(
					my_request_response,process_bar,ek,session.statistics_user,statistics_interface);
		}
		ec.client_information.engine_current_number=engine_current_number;
		ec.client_information.request_response=my_request_response;

		String file_name[];
		if((file_name=dispatch_request_main.get_engine_result(delay_time_length,ek,ec.client_information))!=null) {
			if(file_name.length<=0)
				file_name=null;
			else if(file_name[0]==null)
				file_name=null;
			else if(file_name.length<2)
				file_name=new String[] {file_name[0],null};
		}
		if(file_name!=null) {
			if(file_name[1]==null)
				file_name[1]=Charset.defaultCharset().name();
			return create_file_result(ek,ec.client_information,file_name[0],file_name[1]);
		}

		long output_data_length=ec.client_information.request_response.output_data_length;
		long compress_response_length=ek.scene_par.compress_response_length;
		ec.client_information.statistics_client.response_network_data_length+=output_data_length;
		
		String my_compress_file_name=null;
		if(compress_response_length>0)
			if(output_data_length>=compress_response_length)
				my_compress_file_name="do_compress_flag";
		
		return new engine_call_result(null,null,null,
				my_compress_file_name,null,ek.scene_par.scene_cors_string);
	}
	public engine_call_result get_engine_result(client_process_bar process_bar,
			component_load_source_container component_load_source_cont,
			client_session session,engine_kernel_link_list_and_client_information ec,
			client_request_response my_request_response,long delay_time_length,
			interface_statistics statistics_interface,int engine_current_number[])
	{
		engine_call_result ret_val;
		engine_kernel_lock.lock();
		try{
			ret_val=get_engine_result_routine(
				component_load_source_cont,process_bar,session,ec,my_request_response,
				delay_time_length,statistics_interface,engine_current_number);
		}catch(Exception e){
			debug_information.println(
					"get_engine_result function of engine_kernel_link_list fail!");
			debug_information.println(e.toString());
			e.printStackTrace();
			ret_val=null;
		};
		engine_kernel_lock.unlock();
		return ret_val;
	}
	public void destroy_client_information(client_information ci)
	{
		engine_kernel_lock.lock();
		try{
			ci.destroy();
		}catch(Exception e){
			debug_information.println(
					"destroy_client_information() function of engine_kernel_link_list fail!");
			debug_information.println(e.toString());
			e.printStackTrace();
		}
		engine_kernel_lock.unlock();
	}
	public void destroy()
	{
		if(engine_kernel_lock==null)
			return;
		
		engine_kernel_lock.lock();
		try{
			if(ek!=null){
				ek.destroy();
				ek=null;
			}
		}catch(Exception e){
			debug_information.println(
					"destroy() function of engine_kernel_link_list fail!");
			debug_information.println(e.toString());
			e.printStackTrace();
		}
		engine_kernel_lock.unlock();
	}

	public void forced_destroy()
	{
		for(engine_kernel_link_list p=this,q;p!=null;p=q){
			if(p.ek!=null) {
				p.ek.destroy();
				p.ek=null;
			}
			if(p.engine_kernel_lock!=null) {
				p.engine_kernel_lock.unlock();
				p.engine_kernel_lock=null;
			}
			if(p.link_number_lock!=null) {
				p.link_number_lock.unlock();
				p.link_number_lock=null;
			}
			if((q=p.next_link_list)!=null)
				p.next_link_list=null;
		}
	}
}
