package kernel_common_class;

import kernel_scene.client_information;

public class upload_web_page extends web_page
{
	private String content_type,content_charset,upload_url;
	
	public void create_head()
	{
		String str[]=new String[]
		{
			"<script type=\"text/javascript\">",
			"function do_upload()",
			"{",
			"	var fi=document.getElementById(\"file\"),reader=new FileReader();",
			"	if(fi.files.length<=0){",
			"		alert(\"Not select file\");",
			"		return;",
			"	};",
			"	reader.onloadend=function()",
			"	{",
			"		fetch(\""+upload_url+"\",",
			"			{",
			"				method: \"POST\",",
			"				headers: new Headers(",
			"					{",
		    "    					'Content-Type': '"+content_type+"'",
		   	"					}),",
			"				body: reader.result",
			"			}).catch(",
			"				function()",
			"				{",
			"					alert(\"�ϴ�ʧ��:\");",
			"				});",
			"	};",
			
			(content_charset!=null)	?"	reader.readAsText(fi.files[0],\""+content_charset+"\");"
					 				:"	reader.readAsArrayBuffer(fi.files[0]);",
			"}",
			"</script>"
		};
		for(int i=0,ni=str.length;i<ni;i++)
			out.println(str[i]);
	}
	public void create_body()
	{
		String str[]=new String[]
		{
			"<div align=\"center\"><table>",
			
			"<tr align=\"left\"><td></td></tr>",
			"<tr><td><input type=\"file\" id=\"file\"	name=\"file\"></td></tr>",
			
			"<tr align=\"left\"><td></td></tr>",
			"<tr><td><input type=\"button\" onclick=\"do_upload();\" value=\""+language_change(title)+"\"></td></tr>",
			
			"</table></div>"
		};
		for(int i=0,ni=str.length;i<ni;i++)
			out.println(str[i]);
	}

	public upload_web_page(client_information ci,change_name my_language_change_name,
			String my_content_type,String my_content_charset,String my_upload_url)
	{
		super(ci,my_language_change_name,"upload_file");
		content_type=my_content_type;
		content_charset=my_content_charset;
		upload_url=my_upload_url;
	}
}
