package kernel_common_class;

import kernel_network.client_request_response;
import kernel_scene.client_information;

public class web_page
{
	private change_name language_change_name;
	private String language_str;

	public client_request_response out;
	public String request_url_header,title,info;
	
	public web_page(client_information my_ci,change_name my_language_change_name,String my_title)
	{
		language_change_name=my_language_change_name;
		
		language_str=my_ci.request_response.language_str;
	
		(out=my_ci.request_response).response_content_type="text/html";

		request_url_header=my_ci.request_url_header;
		title=my_title;
		info="";
	}
	public String language_change(String id_str)
	{
		if((language_change_name==null)||(id_str==null))
			return id_str;
		else
			return language_change_name.search_change_name(id_str+"+"+language_str,id_str).trim();
	}
	public void create_head()
	{
	}
	public void create_body_head()
	{
		out.println("<body>");
	}
	public void create_body()
	{
		if(info!=null){
			out.println("<p style=\"text-align:center;vertical-align:center\">");
			out.println(language_change(info));
			out.println("</p>");
		}
	}
	public void create_web_page()
	{
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		
		out.println("<meta http-equiv=\"Pragma\" content=\"no-cache\">");
		out.print  ("<meta charset=\"");
		out.print  (out.implementor.get_request_charset());
		out.println("\">");
		out.println("<title>");
		out.println(language_change(title));
		out.println("</title>");
		
		create_head();

		out.println("</head>");

		create_body_head();
		
		create_body();
		
		out.println("</body>");
		out.println("</html>");
	}
}
