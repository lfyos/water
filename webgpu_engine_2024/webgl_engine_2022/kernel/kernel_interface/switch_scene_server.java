package kernel_interface;

import java.util.ArrayList;

import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_scene.system_parameter;

public class switch_scene_server 
{
	private ArrayList<String> server_url_list;
	private int index_id;
	
	public switch_scene_server(String file_name,String file_charset)
	{
		server_url_list=new ArrayList<String>();
		file_reader fr=new file_reader(file_name,file_charset);
		for(String my_url;!(fr.eof());)
			if((my_url=fr.get_string())!=null) 
				if((my_url=my_url.trim()).length()>0)
					server_url_list.add(my_url);
		fr.close();
		index_id=server_url_list.size()-1;
	}
	public String get_switch_server_url(
		client_request_response request_response,system_parameter system_par)
	{
		int number;
		if((number=server_url_list.size())<=0)
			return null;
		index_id=(index_id+1)%number;
		return server_url_list.get(index_id);
	}
}
