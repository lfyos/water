package kernel_information;

import kernel_scene.client_information;

class proxy_information_item extends jason_creator
{
	private String url;
	private boolean flag;
	
	public void print()
	{	
		print("compare_date",flag);
		print("url",url);
	}
	public proxy_information_item(
			String my_url,boolean my_flag,
			client_information my_ci)
	{
		super(my_ci.request_response);
		url=my_url;
		flag=my_flag;
	}
}

public class proxy_information extends jason_creator
{
	private proxy_information_item item_array[];
	
	public void print()
	{
		print("proxy",item_array);
	}
	public proxy_information(client_information my_ci)
	{
		super(my_ci.request_response);
		
		var url		=my_ci.get_all_file_proxy_url();
		var flag	=my_ci.get_all_file_proxy_date_flag();
		
		item_array=new proxy_information_item[url.length];
		for(int i=0,ni=item_array.length;i<ni;i++)
			item_array[i]=new proxy_information_item(url[i],flag[i],my_ci);
	}
}
