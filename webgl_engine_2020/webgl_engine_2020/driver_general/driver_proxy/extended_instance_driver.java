package driver_proxy;

import java.io.File;
import java.net.URLEncoder;

import kernel_camera.camera_result;
import kernel_common_class.jason_string;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_part.part;
import kernel_file_manager.file_directory;
import kernel_file_manager.file_reader;

public class extended_instance_driver extends instance_driver
{
	private int time_length;
	private String proxy_url[];
	private boolean proxy_encode_flag[];
	
	public void destroy()
	{
		super.destroy();
		
		proxy_url			=null;
		proxy_encode_flag	=null;
	}
	public extended_instance_driver(component my_comp,int my_driver_id)
	{
		super(my_comp,my_driver_id);
		
		proxy_url			=null;
		proxy_encode_flag	=null;
	}
	public void response_init_data(engine_kernel ek,client_information ci)
	{
		proxy_url=new String[0];
		proxy_encode_flag=new boolean[0];
		part p=comp.driver_array[driver_id].component_part;
		file_reader f=new file_reader(p.directory_name+p.material_file_name,p.file_charset);
		time_length=f.get_int();
		while(!(f.eof())){
			boolean my_test_flag,my_encode_flag;
			String my_url;
			f.mark_start();
			if((my_url=f.get_string())==null){
				f.mark_terminate(false);
				continue;
			}
			switch(my_url.toLowerCase()) {
			case "yes":
			case "no":
			case "true":
			case "false":
				f.mark_terminate(true);
				my_test_flag=f.get_boolean();
				my_encode_flag=f.get_boolean();
				if(f.eof())
					continue;
				if((my_url=f.get_string())==null)
					continue;
				if((my_url=my_url.trim()).length()<=0)
					continue;
				break;
			default:
				f.mark_terminate(false);
				my_url=ci.request_response.implementor.get_url()+"?channel=buffer&file=";
				my_test_flag=false;
				my_encode_flag=true;
				break;
			}
			if(!my_test_flag)
				ci.add_file_proxy_url(my_url,my_encode_flag);
			else{
				String proxy_url_bak[]=proxy_url;
				boolean proxy_encode_flag_bak[]=proxy_encode_flag;
				proxy_url			=new String [proxy_url_bak.length+1];
				proxy_encode_flag	=new boolean[proxy_encode_flag_bak.length+1];
				for(int i=0,ni=proxy_url_bak.length;i<ni;i++) {
					proxy_url[i]			=proxy_url_bak[i];
					proxy_encode_flag[i]	=proxy_encode_flag_bak[i];
				}
				proxy_url[proxy_url.length-1]					=my_url;
				proxy_encode_flag[proxy_encode_flag.length-1]	=my_encode_flag;
			}
		}
		f.close();
	}
	public boolean check(int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr,component_collector collector)
	{
		return cr.target.main_display_target_flag?false:true;
	}
	public void create_render_parameter(
			int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(data_buffer_id);
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		part p=comp.driver_array[driver_id].component_part;
		
		String version_file_name=file_directory.part_file_directory(p,ek.system_par,ek.scene_par)+"version.txt";
		String stamp_string=Long.toString((new File(version_file_name)).lastModified());
		String version_string=file_reader.get_text(version_file_name,p.file_charset);
		int dir_length=ek.system_par.proxy_par.proxy_data_root_directory_name.length();
		version_file_name=jason_string.change_string(version_file_name.substring(dir_length));
		
		String original_url=ci.request_response.implementor.get_url();
		try {
			original_url=URLEncoder.encode(original_url,ci.request_response.implementor.get_request_charset());
		}catch(Exception e)
		{
			;
		}
		ci.request_response.println("{");
		ci.request_response.print  ("\"component_id\":",		comp.component_id);	ci.request_response.println(",");
		ci.request_response.print  ("\"driver_id\":",			driver_id);			ci.request_response.println(",");
		ci.request_response.print  ("\"version_string\":\"",	version_string);	ci.request_response.println("\",");
		ci.request_response.print  ("\"time_length\":",			time_length);		ci.request_response.println(",");
		ci.request_response.print  ("\"file_name\":",			version_file_name);	ci.request_response.println(",");
		ci.request_response.print  ("\"stamp_string\":\"",		stamp_string);		ci.request_response.println("\",");
		ci.request_response.print  ("\"original_url\":\"",		original_url);		ci.request_response.println("\",");
		ci.request_response.println("\"proxy_list\":[");
		
		for(int i=0,ni=proxy_url.length;i<ni;i++){
			ci.request_response.println((i<=0)?"":",");
			ci.request_response.print  ("				[\"",proxy_url[i]);
			ci.request_response.print  ("\"",			proxy_encode_flag[i]?",true]":",false]");
		}
		ci.request_response.println();
		ci.request_response.println("			]");

		ci.request_response.println("}");
	}
	private String get_proxy_url(client_information ci,String uri_encode_charset)
	{
		String proxy_url;

		if((proxy_url=ci.request_response.get_parameter("proxy_url"))!=null)
			if((proxy_url=proxy_url.trim()).length()>0)
				try{
					proxy_url=java.net.URLDecoder.decode(proxy_url,uri_encode_charset);
					proxy_url=java.net.URLDecoder.decode(proxy_url,uri_encode_charset);
					return proxy_url;
				}catch(Exception e){
					;
				}
		return null;
	}
	private boolean get_proxy_encode_flag(client_information ci)
	{
		String proxy_encode;
		if((proxy_encode=ci.request_response.get_parameter("proxy_encode"))!=null)
			if((proxy_encode=proxy_encode.trim()).length()>0)
				return (proxy_encode.compareTo("true")==0)?true:false;
		return false;
	}
	public String[] response_event(int parameter_channel_id,engine_kernel ek,client_information ci)
	{
		String str;
		switch(((str=ci.request_response.get_parameter("operation"))==null)?"":str){
		case "append":
			ci.add_file_proxy_url(get_proxy_url(ci,ek.system_par.network_data_charset),get_proxy_encode_flag(ci));
			break;
		case "delete":
			ci.delete_file_proxy_url(get_proxy_url(ci,ek.system_par.network_data_charset));
			break;
		}
		return null;
	}
}
