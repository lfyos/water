package kernel_common_class;

import kernel_engine.client_information;

public class upload_web_page extends web_page
{
	private String content_type_string,finish_url_string,upload_url_string;
	private String file_content_string;
	
	public void create_head()
	{
		String str[]=new String[]
		{
			"<script type=\"text/javascript\">",
			"function do_upload()",
			"{",
			"	var fi=document.getElementById(\"file\"),reader=new FileReader();",
			"	if(fi.files.length<=0){",
			"		window.open(\""+finish_url_string+"\", \"_self\");",
			"		return;",
			"	};",
			"	reader.onloadend=function()",
			"	{",
			"		var xhr=new XMLHttpRequest();",
			"		xhr.onreadystatechange=function()",
			"		{",
			"			if(xhr.readyState==4)",
			"				if(xhr.status==200)",
			"	 				window.open(\""+finish_url_string+"\", \"_self\");",
			"		};",
			"		xhr.responseType=\""+content_type_string+"\";",
			"		xhr.open(\"POST\",\""+upload_url_string+"\",true);",
			"		xhr.send(reader.result);",
			"	};",
			(file_content_string==null)
					?"	reader.readAsBinaryString(fi.files[0]);"
					:"	reader.readAsText(fi.files[0],\""+file_content_string+"\");",
			"}",
			"</script>"
		};
		for(int i=0,ni=str.length;i<ni;i++)
			out.println(str[i]);
	}
	public void create_body()
	{
		final String str[]=new String[]
		{
			"<div align=\"center\"><table>",
			
			"<tr align=\"left\"><td></td></tr>",
			"<tr><td><input type=\"file\"	id=\"file\"	name=\"file\"></td></tr>",
			
			"<tr align=\"left\"><td></td></tr>",
			"<tr><td><input type=\"button\" onclick=\"do_upload();\" value=\""+title+"\"></td></tr>",
			
			"</table></div>"
		};
		for(int i=0,ni=str.length;i<ni;i++)
			out.println(str[i]);
	}

	public upload_web_page(client_information ci,change_name my_language_change_name,
			String my_content_type_string,String my_file_content_string,String my_upload_url_string,String my_finish_url_string)
	{
		super(ci,my_language_change_name,"");
		title=language_change("upload_file");
		content_type_string=my_content_type_string;
		if((file_content_string=my_file_content_string)!=null)
			if((file_content_string=file_content_string.trim()).length()<=0)
				file_content_string=null;
		
		upload_url_string=request_url_header+"&"+my_upload_url_string;
		finish_url_string=request_url_header+"&"+my_finish_url_string;
	}
}
