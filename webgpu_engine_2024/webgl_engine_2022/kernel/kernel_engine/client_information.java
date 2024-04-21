package kernel_engine;

import java.io.File;
import java.util.ArrayList;

import kernel_render.render_target;
import kernel_render.render_target_container;
import kernel_transformation.plane;
import kernel_camera.camera_result;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_interface.user_statistics;
import kernel_network.client_request_response;
import kernel_buffer.buffer_container;
import kernel_interface.client_process_bar;
import kernel_driver.render_instance_driver_container;
import kernel_driver.part_instance_driver_container;
import kernel_driver.component_instance_driver_container;


public class client_information 
{
	public boolean							not_acknowledge_render_part_id[][];
	public buffer_container					render_buffer;

	public render_target_container			target_container;
	
	public component_collector 				display_component_collector;
	public ArrayList<component_collector> 	target_component_collector_list;
	
	public camera_result					display_camera_result;
	public ArrayList<camera_result>			target_camera_result_list;
	
	public plane							clip_plane;
	
	public String							channel_id;
	
	public client_parameter					parameter;
	public create_engine_counter			engine_counter;
	public user_statistics 					statistics_user;
	public client_request_response 			request_response;
	public client_process_bar				process_bar;
	
	
	public render_instance_driver_container		render_instance_driver_cont;
	public part_instance_driver_container		part_instance_driver_cont;
	public component_instance_driver_container	component_instance_driver_cont;
	
	public display_message					message_display;
	
	public String 							request_url_header;
	
	private String							file_proxy_url_array[];
	private int 							file_proxy_pointer;
	
	public void destroy()
	{
		if(request_url_header!=null)
			request_url_header=null;
	
		if(message_display!=null)
			message_display=null;
			
		if(component_instance_driver_cont!=null) {
			component_instance_driver_cont.destroy();
			component_instance_driver_cont=null;
		}
		if(part_instance_driver_cont!=null) {
			part_instance_driver_cont.destroy();
			part_instance_driver_cont=null;
		}
		if(render_instance_driver_cont!=null) {
			render_instance_driver_cont.destroy();
			render_instance_driver_cont=null;
		}
		if(render_buffer!=null) {
			render_buffer.destroy();
			render_buffer=null;
		}
		if(target_container!=null) {
			target_container.destroy();
			target_container=null;
		}
		
		if(display_component_collector!=null) {
			display_component_collector.destroy();
			display_component_collector=null;
		}
		
		if(target_component_collector_list!=null) {
			component_collector cc;
			for(int i=0,ni=target_component_collector_list.size();i<ni;i++)
				if((cc=target_component_collector_list.get(i))!=null)
					cc.destroy();
			target_component_collector_list.clear();
			target_component_collector_list=null;
		}

		if(display_camera_result!=null) {
			display_camera_result.destroy();
			display_camera_result=null;
		}
		if(target_camera_result_list!=null) {
			camera_result cr;
			for(int i=0,ni=target_camera_result_list.size();i<ni;i++)
				if((cr=target_camera_result_list.get(i))!=null)
					cr.destroy();
			target_camera_result_list.clear();
			target_camera_result_list=null;
		}
		
		if(clip_plane!=null)
			clip_plane=null;
		
		if(parameter!=null) {
			parameter.destroy();
			parameter=null;
		}
		
		if(engine_counter!=null)
			engine_counter=null;
		
		if(statistics_user!=null)
			statistics_user=null;

		if(request_response!=null)
			request_response=null;		
	}
	public String[] get_all_file_proxy_url()
	{
		return file_proxy_url_array;
	}
	public String get_component_request_url_header(int component_id,String driver_id)
	{
		String  url_header;
		url_header =request_url_header+"&command=component&method=event";
		url_header+="&event_component_id="+Integer.toString(component_id);
		if(driver_id!=null)
			if((driver_id=driver_id.trim()).length()>0)
				url_header+="&event_driver_id="+driver_id;
		return url_header;
	}
	public String get_component_request_url_header(int component_id,int driver_id)
	{
		return get_component_request_url_header(component_id,Integer.toString(driver_id));
	}
	public String get_part_request_url_header(int render_id,int part_id)
	{
		String  url_header;
		url_header =request_url_header+"&command=part&method=event";
		url_header+="&event_render_id="+Integer.toString(render_id);
		url_header+="&event_part_id="+Integer.toString(part_id);
		return url_header;
	}
	public String get_file_proxy_url(String file_name,system_parameter system_par)
	{
		File f=new File(file_name);
		
		if(file_proxy_url_array.length<=0)
			return null;
		if(!(f.exists()))
			return null;
		if(f.length()<system_par.max_file_response_length)
			return null;
		
		String proxy_file_name=f.getAbsolutePath().replace(File.separatorChar,'/');
		String proxy_directory_name=system_par.temporary_file_par.temporary_root_directory_name.replace(File.separatorChar,'/');
		int proxy_directory_name_length=proxy_directory_name.length();
		if(proxy_file_name.length()<=proxy_directory_name_length)
			return null;
		if(proxy_file_name.substring(0,proxy_directory_name_length).compareTo(proxy_directory_name)!=0)
			return null;
		proxy_file_name=proxy_file_name.substring(proxy_directory_name_length);
		
		file_proxy_pointer=(++file_proxy_pointer)%(file_proxy_url_array.length);
		String proxy_url=file_proxy_url_array[file_proxy_pointer];

		long last_modified_time=f.lastModified();
		String code_str=request_response.implementor.get_request_charset();
		try{
			proxy_file_name	=java.net.URLEncoder.encode(java.net.URLEncoder.encode(proxy_file_name,	code_str),code_str);
		}catch(Exception e) {
			;
		}
		return proxy_url+proxy_file_name+"&date="+last_modified_time;
	}
	public void add_file_proxy_url(String my_file_proxy_url)
	{
		if(my_file_proxy_url==null)
			return;
		if((my_file_proxy_url=my_file_proxy_url.trim()).length()<=0)
			return;
		String bak_url[]=file_proxy_url_array;
		file_proxy_url_array=new String[file_proxy_url_array.length+1];
		for(int i=0,ni=bak_url.length;i<ni;i++)
			file_proxy_url_array[i]=bak_url[i];
		file_proxy_url_array[file_proxy_url_array.length-1]=new String(my_file_proxy_url);
		return;
	}
	public void delete_file_proxy_url(String my_file_proxy_url)
	{
		if(my_file_proxy_url==null)
			return;
		if((my_file_proxy_url=my_file_proxy_url.trim()).length()<=0)
			return;
		int new_file_proxy_number=0;
		for(int i=0,ni=file_proxy_url_array.length;i<ni;i++)
			if(my_file_proxy_url.compareTo(file_proxy_url_array[i])!=0){
				file_proxy_url_array[new_file_proxy_number++]=file_proxy_url_array[i];
			}
		if(file_proxy_url_array.length!=new_file_proxy_number){
			String bak_url[]=file_proxy_url_array;
			file_proxy_url_array=new String[new_file_proxy_number];
			for(int i=0;i<new_file_proxy_number;i++)
				file_proxy_url_array[i]=bak_url[i];
		}
		return;
	}
	public client_information(client_request_response my_request_response,client_process_bar my_process_bar,
			engine_kernel ek,user_statistics my_statistics_user,create_engine_counter my_engine_counter)
	{
		not_acknowledge_render_part_id=new boolean[ek.render_cont.renders.size()][];
		for(int i=0,ni=not_acknowledge_render_part_id.length;i<ni;i++) {
			not_acknowledge_render_part_id[i]=new boolean[ek.render_cont.renders.get(i).parts.size()];
			for(int j=0,nj=not_acknowledge_render_part_id[i].length;j<nj;j++)
				not_acknowledge_render_part_id[i][j]=true;
		}
		
		String str;
		int new_max_loading_number,max_client_loading_number=ek.system_par.normal_loading_number;
		if((str=my_request_response.get_parameter("max_loading_number"))!=null)	
			if((new_max_loading_number=Integer.decode(str))>0)
				if(new_max_loading_number<=ek.system_par.max_loading_number)
					max_client_loading_number=new_max_loading_number;
		
		render_buffer					=new buffer_container(ek);
		target_container				=new render_target_container();
		
		display_component_collector		=null;
		target_component_collector_list	=new ArrayList<component_collector>();

		render_target rt=new render_target(
				false,ek.component_cont.root_component.component_id,0,0,
				new component[]{ek.component_cont.root_component},null,
				0,0,null,null,null,null,true,true);
	
		display_camera_result			=new camera_result(ek.camera_cont.get(rt.camera_id),rt,ek.component_cont);
		target_camera_result_list		=new ArrayList<camera_result>();
		
		clip_plane						=null;
		
		channel_id						=Long.toString(system_channel_id++);
		
		parameter						=new client_parameter(max_client_loading_number);
		
		engine_counter					=my_engine_counter;
		statistics_user					=my_statistics_user;
		
		request_response				=my_request_response;
		
		process_bar						=my_process_bar;

		file_proxy_url_array			=new String[0];
		file_proxy_pointer				=0;
		
		render_instance_driver_cont		=new render_instance_driver_container(ek,request_response);
		part_instance_driver_cont		=new part_instance_driver_container(ek,request_response);	
		component_instance_driver_cont	=new component_instance_driver_container(ek,request_response);
		
		message_display		=new display_message();	
		
		request_url_header=request_response.implementor.get_url();
		request_url_header+="?channel="		+channel_id;
		request_url_header+="&container="	+request_response.container_id;
		request_url_header+="&user_name="	+request_response.user_name;
		request_url_header+="&pass_word="	+request_response.pass_word;
		request_url_header+="&language="	+request_response.language_str;
		
		return;
	}
	
	private static long system_channel_id=0;
}
