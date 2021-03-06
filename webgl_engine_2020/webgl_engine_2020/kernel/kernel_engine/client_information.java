package kernel_engine;

import java.io.File;

import kernel_render.render_target;
import kernel_render.render_target_container;
import kernel_transformation.plane;
import kernel_camera.camera_result;
import kernel_component.component_collector;
import kernel_driver.instance_driver_container;
import kernel_interface.user_statistics;
import kernel_network.client_request_response;
import kernel_buffer.buffer_container;


public class client_information 
{
	public buffer_container				render_buffer;

	public render_target_container		target_container;
	
	public component_collector 			display_component_collector;
	public component_collector 			selection_component_collector;
	public component_collector 			target_component_collector_array[];
	
	public camera_result				display_camera_result;
	public camera_result				selection_camera_result;
	public camera_result				target_camera_result_array[];
	
	public plane						clip_plane;
	
	public long							channel_id;
	
	public client_parameter				parameter;
	public client_statistics			statistics_client;
	public interface_statistics			statistics_interface;
	public user_statistics 				statistics_user;
	public int							engine_current_number[];		
	public client_request_response 		request_response;
	
	public instance_driver_container	instance_container;
	
	public display_message				message_display;
	
	public client_creation_parameter	creation_parameter;
	
	private boolean						file_proxy_url_encode_flag[];
	private String						file_proxy_url_array[];
	private int 						file_proxy_pointer;
	
	public void destroy()
	{
		if(creation_parameter!=null)
			creation_parameter=null;
		
		if(message_display!=null)
			message_display=null;
			
		if(instance_container!=null) {
			instance_container.destroy();
			instance_container=null;
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
		
		if(selection_component_collector!=null) {
			selection_component_collector.destroy();
			selection_component_collector=null;
		}
		
		if(target_component_collector_array!=null) {
			for(int i=0,ni=target_component_collector_array.length;i<ni;i++)
				if(target_component_collector_array[i]!=null) {
					target_component_collector_array[i].destroy();
					target_component_collector_array[i]=null;
				}
			target_component_collector_array=null;
		}

		if(display_camera_result!=null) {
			display_camera_result.destroy();
			display_camera_result=null;
		}
		
		if(selection_camera_result!=null) {
			selection_camera_result.destroy();
			selection_camera_result=null;
		}
		
		if(target_camera_result_array!=null) {
			for(int i=0,ni=target_camera_result_array.length;i<ni;i++)
				if(target_camera_result_array[i]!=null) {
					target_camera_result_array[i].destroy();
					target_camera_result_array[i]=null;
				}
			target_camera_result_array=null;
		}
		
		if(clip_plane!=null)
			clip_plane=null;
		
		if(parameter!=null) {
			parameter.destroy();
			parameter=null;
		}
	
		if(statistics_client!=null)
			statistics_client=null;
		
		if(statistics_interface!=null)
			statistics_interface=null;
		
		if(statistics_user!=null)
			statistics_user=null;

		if(engine_current_number!=null)
			engine_current_number=null;
		
		if(request_response!=null)
			request_response=null;		
	}
	public String[] get_all_file_proxy_url()
	{
		return file_proxy_url_array;
	}
	public boolean[] get_all_file_proxy_encode_flag()
	{
		return file_proxy_url_encode_flag;
	}
	public String get_request_url_header()
	{
		String user_name=request_response.get_parameter("user_name");
		user_name=(user_name==null)?"NoName":(user_name.trim());
		
		String language_str=request_response.get_parameter("language");
		language_str=(language_str==null)?"english":(language_str.trim());
		
		String url_header;
		url_header =request_response.implementor.get_url();
		url_header+="?channel="+Long.toString(channel_id);
		url_header+="&user_name="+user_name+"&language=" +language_str;
		
		return url_header;
	}
	public String get_component_request_url_header(int component_id,String driver_id)
	{
		String  url_header;
		url_header =get_request_url_header()+"&command=component&method=event";
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
		url_header =get_request_url_header()+"&command=part&method=event";
		url_header+="&event_render_id="+Integer.toString(render_id);
		url_header+="&event_part_id="+Integer.toString(part_id);
		return url_header;
	}
	public String get_file_proxy_url(String file_name,system_parameter system_par)
	{
		return get_file_proxy_url(new File(file_name),system_par);
	}
	public String get_file_proxy_url(File f,system_parameter system_par)
	{
		if((!(f.exists()))||(file_proxy_url_array.length<=0))
			return null;
		
		String proxy_file_name=f.getAbsolutePath().replace(File.separatorChar,'/');
		String proxy_directory_name=system_par.proxy_par.proxy_data_root_directory_name.replace(File.separatorChar,'/');
		int proxy_directory_name_length=proxy_directory_name.length();
		if(proxy_file_name.length()<=proxy_directory_name_length)
			return null;
		if(proxy_file_name.substring(0,proxy_directory_name_length).compareTo(proxy_directory_name)!=0)
			return null;
		proxy_file_name=proxy_file_name.substring(proxy_directory_name_length);
		
		file_proxy_pointer=(++file_proxy_pointer)%(file_proxy_url_array.length);
		String proxy_url=file_proxy_url_array[file_proxy_pointer];
		if(!(file_proxy_url_encode_flag[file_proxy_pointer]))
			return proxy_url+proxy_file_name;

		long file_length=f.length(),last_modified_time=f.lastModified();
		caculate_charset_compress_file_name cccfn=new caculate_charset_compress_file_name(f,system_par);
		if(cccfn.charset_file_name!=null)
			if((f=new File(cccfn.charset_file_name)).exists())
				file_length=f.length();
		if(cccfn.compress_file_name!=null)
			if((f=new File(cccfn.compress_file_name)).exists())
				file_length=f.length();
		
		String length_str=Long.toString(file_length);
		String date_str=Long.toString(last_modified_time);
		String original_url=request_response.implementor.get_url();
		String code_str=request_response.implementor.get_request_charset();
		try{
			proxy_file_name	=java.net.URLEncoder.encode(java.net.URLEncoder.encode(proxy_file_name,	code_str),code_str);
			length_str		=java.net.URLEncoder.encode(java.net.URLEncoder.encode(length_str,		code_str),code_str);
			date_str		=java.net.URLEncoder.encode(java.net.URLEncoder.encode(date_str,		code_str),code_str);
			original_url	=java.net.URLEncoder.encode(java.net.URLEncoder.encode(original_url,	code_str),code_str);
		}catch(Exception e) {
			;
		}
		return proxy_url+proxy_file_name+"&length="+length_str+"&date="+date_str+"&original="+original_url;
	}
	public void add_file_proxy_url(String my_file_proxy_url,boolean my_encode_flag)
	{
		if(my_file_proxy_url==null)
			return;
		if((my_file_proxy_url=my_file_proxy_url.trim()).length()<=0)
			return;
		String bak_url[]=file_proxy_url_array;
		boolean bak_flag[]=file_proxy_url_encode_flag;
		file_proxy_url_array=new String[file_proxy_url_array.length+1];
		file_proxy_url_encode_flag=new boolean[file_proxy_url_encode_flag.length+1];
		for(int i=0,ni=bak_url.length;i<ni;i++) {
			file_proxy_url_array[i]=bak_url[i];
			file_proxy_url_encode_flag[i]=bak_flag[i];
		}
		file_proxy_url_array[file_proxy_url_array.length-1]=new String(my_file_proxy_url);
		file_proxy_url_encode_flag[file_proxy_url_encode_flag.length-1]=my_encode_flag;
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
				file_proxy_url_array[new_file_proxy_number  ]=file_proxy_url_array[i];
				file_proxy_url_encode_flag [new_file_proxy_number++]=file_proxy_url_encode_flag[i];
			}
		if(file_proxy_url_array.length!=new_file_proxy_number){
			String bak_url[]=file_proxy_url_array;
			boolean bak_flag[]=file_proxy_url_encode_flag;
			file_proxy_url_array=new String[new_file_proxy_number];
			file_proxy_url_encode_flag=new boolean[new_file_proxy_number];
			for(int i=0;i<new_file_proxy_number;i++) {
				file_proxy_url_array[i]=bak_url[i];
				file_proxy_url_encode_flag[i]=bak_flag[i];
			}
		}
		return;
	}
	public void clear_target_buffer(int target_id)
	{
		render_buffer.target_buffer.clear_buffer(target_id);
	}
	
	public client_information(client_request_response my_request_response,
			engine_kernel ek,user_statistics my_statistics_user,interface_statistics my_statistics_interface)
	{
		ek.system_par.system_exclusive_name_mutex.lock(
				ek.scene_par.scene_proxy_directory_name+"engine.lock");

		render_buffer					=new buffer_container(ek);
		target_container				=new render_target_container();
		
		display_component_collector		=null;
		selection_component_collector	=null;
		target_component_collector_array=new component_collector[]{};

		render_target t				=render_target_container.get_default_target(
				ek.component_cont.root_component,ek.scene_par.initial_parameter_channel_id);
		display_camera_result		=new camera_result(ek.camera_cont.camera_array[t.camera_id],t,ek.component_cont);
		selection_camera_result		=display_camera_result;
		target_camera_result_array	=new camera_result[]{};	
		
		clip_plane			=null;
		
		channel_id			=system_channel_id++;
		parameter			=new client_parameter(ek.scene_par.do_discard_lod_flag,ek.scene_par.do_selection_lod_flag);
		
		statistics_client	=new client_statistics();
		statistics_interface=my_statistics_interface;
		statistics_user		=my_statistics_user;
		engine_current_number=new int[] {0,0};
		
		request_response	=my_request_response;
		
		file_proxy_url_encode_flag=new boolean[0];
		file_proxy_url_array=new String[0];
		file_proxy_pointer	=0;
		
		instance_container	=new instance_driver_container(ek,request_response);
		message_display		=new display_message();				
		creation_parameter	=new client_creation_parameter(ek,request_response);
		
		ek.system_par.system_exclusive_name_mutex.unlock(
				ek.scene_par.scene_proxy_directory_name+"engine.lock");
		
		return;
	}
	static private long	system_channel_id=0;
}
