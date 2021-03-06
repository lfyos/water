package kernel_engine_proxy;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import kernel_common_class.common_reader;
import kernel_common_class.debug_information;

public class engine_proxy
{
	private String server_url;
	
	public engine_proxy()
	{
		server_url=null;
	}
	private void reset()
	{
		if(server_url!=null) {
			server_url+="&command=termination";
			HttpURLConnection connection=null;
			try{
				connection=(HttpURLConnection)((new URL(server_url)).openConnection());
				connection.setRequestMethod("GET");
				connection.connect();
			}catch(Exception e){
				;
			}
			if(connection!=null)
				connection.disconnect();
		}
		server_url=null;
	}
	synchronized public String process_component_call(
			String component_name,String my_call_parameter[][],
			
			String my_server_url,
			String my_user_name,	String my_pass_word,	String my_language_name,
			String my_scene_name,	String my_link_name,	String charset_name,
			String my_initialization_parameter[][])
	{
		if(server_url==null)
			init(	my_server_url,
					my_user_name,	my_pass_word,	my_language_name,
					my_scene_name,	my_link_name,	charset_name,
					my_initialization_parameter);
		if(server_url==null)
			return "";
		String ret_val=call_component(component_name,my_call_parameter,charset_name);
		if(ret_val.trim().length()<=0)
			reset();
		return ret_val;
	}
	private void init(String my_server_url,
			String my_user_name,	String my_pass_word,	String my_language_name,
			String my_scene_name,	String my_link_name,	String charset_name,
			String my_initialization_parameter[][])
	{
		server_url=my_server_url;
		server_url+="?user_name="+my_user_name;
		server_url+="&pass_word="+my_pass_word;
		server_url+="&language="+my_language_name;
		server_url+="&channel=";
		
		String request_url=server_url+"creation&command=creation";
		request_url+="&scene_name="+my_scene_name+"&link_name="+my_link_name;
		
		if(my_initialization_parameter!=null) {
			for(int i=0,ni=my_initialization_parameter.length;i<ni;i++){
					String parameter_item[]=my_initialization_parameter[i];
					if(parameter_item!=null)
						if(parameter_item.length>=2) {
							String parameter_name	=parameter_item[0].trim();
							String parameter_value	=parameter_item[1].trim();
							request_url+="&"+parameter_name+"="+parameter_value;
						}
				};
		}
		HttpURLConnection connection=null;
		BufferedInputStream stream=null;
		try{
			connection=(HttpURLConnection)((new URL(request_url)).openConnection());
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept-Encoding","gzip");
			connection.connect();
			if(connection.getResponseCode()==HttpURLConnection.HTTP_OK)
				stream=new BufferedInputStream(connection.getInputStream());
			else {
				server_url=null;
				debug_information.println(
					"engine_proxy:connection.getResponseCode()!=HttpURLConnection.HTTP_OK:",
					connection.getResponseCode());
				debug_information.println(request_url);
			}
		}catch(Exception e){
			server_url=null;
			debug_information.println("engine_proxy:	Setup connection fail:",e.toString());
			debug_information.println(request_url);
			e.printStackTrace();
		}
		if(stream==null){
			server_url=null;
			debug_information.println("engine_proxy:	Create connection stream fail");
			debug_information.println(request_url);
		}else {
			common_reader cr=new common_reader(stream,charset_name);
			cr.get_string();
			server_url+=cr.get_long();
			cr.close();
			try {
				stream.close();
			}catch(Exception e){
				server_url=null;
				debug_information.println("engine_proxy:	get channle ID fail:",e.toString());
				debug_information.println(request_url);
				e.printStackTrace();
			}
		}
		if(connection!=null)
			connection.disconnect();
	}
	private String call_component(String my_component_name,String my_call_parameter[][],String charset_name)
	{
		String ret_val="",request_url=server_url;
		request_url+="&command=component&method=event&event_component_name="+my_component_name;
		
		if(my_call_parameter!=null)
			for(int i=0,ni=my_call_parameter.length;i<ni;i++){
				String parameter_item[]=my_call_parameter[i];
				if(parameter_item==null)
					continue;
				if(parameter_item.length<2)
					continue;
				String parameter_name	=parameter_item[0];
				String parameter_value	=parameter_item[1];
				if((parameter_name==null)||(parameter_value==null))
					continue;
				if((parameter_name=parameter_name.trim()).length()<=0)
					continue;
				if((parameter_value=parameter_value.trim()).length()<=0)
					continue;
				request_url+="&"+parameter_name+"="+parameter_value;
			}
		try{
			HttpURLConnection connection=(HttpURLConnection)((new URL(request_url)).openConnection());
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept-Encoding","gzip");
			connection.connect();
			if(connection.getResponseCode()!=HttpURLConnection.HTTP_OK) {
				reset();
				debug_information.println(
					"call_component in engine_proxy:connection.getResponseCode()!=HttpURLConnection.HTTP_OK:",
					connection.getResponseCode());
				debug_information.println(request_url);
			}else{
				StringBuffer buf=new StringBuffer();
				BufferedInputStream stream=new BufferedInputStream(connection.getInputStream());
				common_reader cr=new common_reader(stream,charset_name);
				for(int i=0;!(cr.eof());) {
					String str=cr.get_line();
					if(str==null)
						continue;
					if((i++)>0)
						buf.append('\n');
					buf.append(str);
				}
				cr.close();
				stream.close();
				ret_val=buf.toString();
			}
			if(connection!=null)
				connection.disconnect();
			if(ret_val.trim().length()<=0)
				reset();
			return ret_val;
		}catch(Exception e) {
			reset();
			debug_information.println("call_component in engine_proxy:Exception:",e.toString());
			debug_information.println(request_url);
			e.printStackTrace();
			return ret_val;
		}
	}
}
