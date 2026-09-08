package kernel_scene;

import java.io.File;
import java.util.ArrayList;

import kernel_component.component;
import kernel_render.render_target;
import kernel_transformation.plane;
import kernel_camera.camera_result;
import kernel_buffer.buffer_container;
import kernel_component.component_collector;
import kernel_render.render_target_container;
import kernel_render.render_target_parameter;
import kernel_network.client_request_response;
import kernel_driver.part_instance_driver_container;
import kernel_driver.render_instance_driver_container;
import kernel_driver.component_instance_driver_container;

public class client_information 
{
	public boolean								not_acknowledge_render_part_id[][];
	public int 									loaded_file_number;
	public long 								loaded_data_length;
	
	public buffer_container						render_buffer;

	public render_target_container				target_container;
	
	public component_collector 					display_component_collector;
	public ArrayList<component_collector> 		target_component_collector_list;
	
	public camera_result						display_camera_result;
	public ArrayList<camera_result>				target_camera_result_list;
	
	public plane								clip_plane;
	
	public String								channel_id;
	
	public client_parameter						parameter;
	public client_request_response 				request_response;
	
	public render_instance_driver_container		render_instance_driver_cont;
	public part_instance_driver_container		part_instance_driver_cont;
	public component_instance_driver_container	component_instance_driver_cont;
	
	public display_message						message_display;
	
	public String 								request_url_header;
	
	private ArrayList<String>					file_proxy_cont;
	private int 								file_proxy_pointer;
	
	public void destroy()
	{
		not_acknowledge_render_part_id=null;
		
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
		
		channel_id=null;
		
		if(parameter!=null) {
			parameter.destroy();
			parameter=null;
		}
		if(request_response!=null)
			request_response=null;
		
		if(render_instance_driver_cont!=null) {
			render_instance_driver_cont.destroy();
			render_instance_driver_cont=null;
		}
		if(part_instance_driver_cont!=null) {
			part_instance_driver_cont.destroy();
			part_instance_driver_cont=null;
		}
		if(component_instance_driver_cont!=null) {
			component_instance_driver_cont.destroy();
			component_instance_driver_cont=null;
		}
		if(message_display!=null)
			message_display=null;
		if(request_url_header!=null)
			request_url_header=null;
	
		if(file_proxy_cont!=null) {
			file_proxy_cont.clear();
			file_proxy_cont=null;
		}
	}
	public String[] get_all_file_proxy_url()
	{
		String url_array[]=new String[file_proxy_cont.size()];
		for(int i=0,ni=url_array.length;i<ni;i++)
			url_array[i]=file_proxy_cont.get(i);
		return url_array;
	}
	
	public String caculate_file_proxy_url(String file_name,String file_charset,system_parameter system_par)
	{
		if(file_proxy_cont.size()<=0)
			return null;
		File original_f=new File(file_name);
		if(!(original_f.exists()))
			return null;
		search_file_content_type_result content_type;
		if((content_type=system_par.search_file_content_type(file_name))==null)
			return null;
		File target_f=new File(content_type.path_name);
		if(target_f.length()<system_par.max_file_response_length)
			return null;

		String proxy_file_name=original_f.getAbsolutePath().replace(File.separatorChar,'/');
		String proxy_directory_name=system_par.temporary_file_par.
					temporary_root_directory_name.replace(File.separatorChar,'/');
		if(proxy_file_name.indexOf(proxy_directory_name)!=0)
			return null;
		proxy_file_name=proxy_file_name.substring(proxy_directory_name.length());

		String encode_str=request_response.implementor.get_request_charset();
		try {
			proxy_file_name	=java.net.URLEncoder.encode(proxy_file_name,encode_str);
			proxy_file_name	=java.net.URLEncoder.encode(proxy_file_name,encode_str);
		}catch(Exception e) {
			;
		}
		
		file_proxy_pointer=(file_proxy_pointer+1)%(file_proxy_cont.size());
		String proxy_url=file_proxy_cont.get(file_proxy_pointer)+proxy_file_name;

		if(file_charset==null)
			file_charset=system_par.network_data_charset;
		else if((file_charset=file_charset.trim()).length()<=0)
			file_charset=system_par.network_data_charset;
		
		return proxy_url					+"&proxy_info="
				+encode_str					+";"
				+file_charset				+";"
				+content_type.content_str	+";"
				+content_type.ext_str		+";"
				+(content_type.link_flag?"true;":"false;")
				+Long.toString(target_f.lastModified());
	}
	public void add_file_proxy_url(String my_file_proxy_url)
	{
		if(my_file_proxy_url!=null)
			if((my_file_proxy_url=my_file_proxy_url.trim()).length()>0)
				file_proxy_cont.add(my_file_proxy_url);
	}
	public void delete_file_proxy_url(String my_file_proxy_url)
	{
		if(my_file_proxy_url!=null)
			if((my_file_proxy_url=my_file_proxy_url.trim()).length()>0)
				for(int i=file_proxy_cont.size()-1;i>=0;i--) 
					if(file_proxy_cont.get(i).compareTo(my_file_proxy_url)==0)
						file_proxy_cont.remove(i);
	}
	public client_information(client_request_response my_request_response,scene_kernel sk)
	{
		not_acknowledge_render_part_id=new boolean[sk.render_cont.renders.size()][];
		for(int i=0,ni=not_acknowledge_render_part_id.length;i<ni;i++) {
			not_acknowledge_render_part_id[i]=new boolean[sk.render_cont.renders.get(i).parts.size()];
			for(int j=0,nj=not_acknowledge_render_part_id[i].length;j<nj;j++)
				not_acknowledge_render_part_id[i][j]=true;
		}
		
		loaded_file_number				=0;
		loaded_data_length				=0;

		render_buffer					=new buffer_container(sk);
		target_container				=new render_target_container();
		
		display_component_collector		=null;
		target_component_collector_list	=new ArrayList<component_collector>();

		render_target rt=new render_target(-1,
				render_target_parameter.create_client_information_parameter(),
				null,sk.component_cont.root_component.component_id,0,0,
				new component[]{sk.component_cont.root_component},0,0,null,null,null,null);
		camera_result cr=new camera_result(sk.camera_cont.get(rt.camera_id),rt,sk.component_cont);

		display_camera_result			=cr;

		target_camera_result_list		=new ArrayList<camera_result>();
		
		clip_plane						=null;
		
		channel_id						=Long.toString(system_channel_id++);
		
		parameter						=new client_parameter();
		
		request_response				=my_request_response;

		file_proxy_cont					=new ArrayList<String>();
		file_proxy_pointer				=0;
		
		render_instance_driver_cont		=new render_instance_driver_container(sk,request_response);
		part_instance_driver_cont		=new part_instance_driver_container(sk,request_response);	
		component_instance_driver_cont	=new component_instance_driver_container(sk,request_response);
		
		message_display		=new display_message();	
		
		request_url_header	 =request_response.implementor.get_url();
		request_url_header	+="?channel="	+channel_id;
		request_url_header	+="&container="	+request_response.container_id;
		request_url_header	+="&user_name="	+request_response.user_name;
		request_url_header	+="&pass_word="	+request_response.pass_word;
		request_url_header	+="&language="	+request_response.language_str;

		return;
	}
	private volatile static long system_channel_id=0;
	
	public String get_component_request_url_header_by_component_id(int component_id,String driver_id)
	{
		String url_header=request_url_header+"&command=component&method=event&event_component_id="+component_id;
		if(driver_id!=null)
			if((driver_id=driver_id.trim()).length()>0)
				url_header+="&event_driver_id="+driver_id;
		return url_header;
	}
	public String get_component_request_url_header_by_component_name(String component_name,String driver_id)
	{
		String url_header=request_url_header+"&command=component&method=event&event_component_name="+component_name;
		if(driver_id!=null)
			if((driver_id=driver_id.trim()).length()>0)
				url_header+="&event_driver_id="+driver_id;
		return url_header;
	}
	public String get_part_request_url_header_by_part_id(int render_id,int part_id)
	{
		String url_header=request_url_header+"&command=part&method=event";
		return url_header+"&event_render_id="+render_id+"&event_part_id="+part_id;
	}
	public String get_part_request_url_header_by_part_name(String part_name)
	{
		return request_url_header+"&command=part&method=event"+"&event_part_name="+part_name;
	}
	public String get_part_request_url_header_by_component_id(int component_id,String driver_id)
	{
		String url_header=request_url_header+"&command=part&method=event";
		url_header+="&event_component_id="+Integer.toString(component_id);
		if(driver_id!=null)
			if((driver_id=driver_id.trim()).length()>0)
				url_header+="&event_driver_id="+driver_id;
		return url_header;
	}
	public String get_part_request_url_header_by_component_name(String component_name,String driver_id)
	{
		String url_header=request_url_header+"&command=part&method=event";
		url_header+="&event_component_name="+component_name;
		if(driver_id!=null)
			if((driver_id=driver_id.trim()).length()>0)
				url_header+="&event_driver_id="+driver_id;
		return url_header;
	}
	
	public String get_render_request_url_header_by_render_id(int render_id)
	{
		return request_url_header+"&command=render&method=event&event_render_id="+render_id;
	}
	public String get_render_request_url_header_by_render_name(String render_name)
	{
		return request_url_header+"&command=render&method=event&event_render_name="+render_name;
	}
	public String get_render_request_url_header_by_part_name(String part_name,String driver_id)
	{
		String url_header=request_url_header+"&command=render&method=event&event_part_name="+part_name;
		if(driver_id!=null)
			if((driver_id=driver_id.trim()).length()>0)
				url_header+="&event_driver_id="+driver_id;
		return url_header;
	}
	public String get_render_request_url_header_by_component_id(int component_id,String driver_id)
	{
		String url_header=request_url_header+"&command=render&method=event&event_component_id="+component_id;
		if(driver_id!=null)
			if((driver_id=driver_id.trim()).length()>0)
				url_header+="&event_driver_id="+driver_id;
		return url_header;
	}
	public String get_render_request_url_header_by_component_name(String component_name,String driver_id)
	{
		String url_header=request_url_header+"&command=render&method=event&event_component_name="+component_name;
		if(driver_id!=null)
			if((driver_id=driver_id.trim()).length()>0)
				url_header+="&event_driver_id="+driver_id;
		return url_header;
	}
}
