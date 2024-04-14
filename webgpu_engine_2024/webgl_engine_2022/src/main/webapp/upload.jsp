<%@page import="java.io.File"%>
<%@page import="java.io.InputStream"%>
<%@page import="kernel_network.network"%>
<%@page import="kernel_file_manager.file_writer"%>
<%@page import="format_convert.protected_cadex_converter"%>
<%@page import="format_convert.web_converter"%>
<%@page import="kernel_common_class.debug_information"%>
<%@page language="java" contentType="text/html; charset=UTF-8"    pageEncoding="UTF-8"%>

<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="UTF-8">
<meta http-equiv="Pragma" content="no-cache">


<title>
上传列表
</title>

<script type="text/javascript">
<%
	String format_string=request.getParameter("format");
	format_string=(format_string==null)?"NoExternal":(format_string.toLowerCase());
	out.println("var format_string=\""+format_string+"\";");
%>

function file_name_change()
{
	var fi=document.getElementById("file");
	if(fi.files.length<=0){
		alert("请选择文件");
		return;
	}
	
	var file_name=encodeURIComponent(encodeURIComponent(fi.files[0].name));

	var reader=new FileReader();
	reader.onloadend=function()
	{
		fetch("./upload.jsp?format="+format_string+"&file="+file_name,
			{
				method: "POST", 
				body: reader.result
			}).catch(
					function()
					{
						alert("上传失败!");
					});
	};
	reader.readAsArrayBuffer(fi.files[0]);
}

</script>

</head>

<body>

<div align="center" >

<br/>	选择文件上传：	<input type="file"	id="file"		name="file"			onchange="file_name_change();">

<br/>
<p>
已经上传文件如下所示，打开如下网页即可显示,每个中括号中的名称是观测方向，分别是xyz、yzx、zxy，前一个超链接是独立方式观测，后一个超链接是合作方式观测。
</p>
<%
	response.setCharacterEncoding("UTF-8");

	String directory_name=System.getenv("liufuyan_scene_directory_configure")+"sigma/";

	File f;
	if((f=new File(directory_name)).exists())
		if(f.isDirectory()){
			String file_list[]=f.list();
			if(file_list!=null)
				for(int i=0,ni=file_list.length;i<ni;i++){
					f=new File(directory_name+file_list[i]);
					if(!(f.isDirectory())){
						if(!(f.isFile()))
							continue;
						String ext_str=".zip";
						int index_id=file_list[i].lastIndexOf(ext_str);
						if(index_id<0)
							continue;
						if(file_list[i].substring(index_id).compareTo(ext_str)!=0)
							continue;
						file_list[i]=file_list[i].substring(0,index_id);
					}
					try{
						out.println(file_list[i]);
									
						out.print(  "&nbsp;&nbsp;[<a href=\"draw.jsp?coordinate=location.xyz.txt");
						out.println("&scene_sub_directory=sigma/"+file_list[i]+"\"  target=\"_self\">xyz</a>");
						out.print(  "&nbsp;,&nbsp;<a href=\"draw.jsp?coordinate=location.xyz.txt");
						out.println("&scene_sub_directory=sigma/"+file_list[i]+"&link_name="+file_list[i]+"\"  target=\"_self\">xyz</a>]");
									
						out.print(  "&nbsp;&nbsp;[<a href=\"draw.jsp?coordinate=location.yzx.txt");
						out.println("&scene_sub_directory=sigma/"+file_list[i]+"\"  target=\"_self\">yzx</a>");
						out.print(  "&nbsp;,&nbsp;<a href=\"draw.jsp?coordinate=location.yzx.txt");
						out.println("&scene_sub_directory=sigma/"+file_list[i]+"&link_name="+file_list[i]+"\"  target=\"_self\">yzx</a>]");
									
						out.print(  "&nbsp;&nbsp;[<a href=\"draw.jsp?coordinate=location.zxy.txt");
						out.println("&scene_sub_directory=sigma/"+file_list[i]+"\"  target=\"_self\">zxy</a>");
						out.print(  "&nbsp;,&nbsp;<a href=\"draw.jsp?coordinate=location.zxy.txt");
						out.println("&scene_sub_directory=sigma/"+file_list[i]+"&link_name="+file_list[i]+"\"  target=\"_self\">zxy</a>]");
									
						out.println("<br/>");
					}catch(Exception e){
						;
					}
				}
		}

	do{
		String file_name=request.getParameter("file");
		if(file_name==null)
			break;
		if(file_name.trim().isEmpty())
			break;
		try{
			file_name=java.net.URLDecoder.decode(file_name,"UTF-8");
			file_name=java.net.URLDecoder.decode(file_name,"UTF-8");
		}catch(Exception e){
			;
		}
		
		String source_file_name				=directory_name+"_source_"+file_name;

		file_writer.file_delete(source_file_name);
		file_writer fw=new file_writer(source_file_name,null);
		if(fw.error_flag) {
			fw.close();
			break;
		}
		InputStream is=new network(request,response).get_content_stream();
		if(is==null){
			fw.close();
			file_writer.file_delete(source_file_name);
			break;
		}
		try{
			byte buffer[]=new byte[8192];
			for(int my_length;(my_length=is.read(buffer))>=0;)
				fw.write(buffer,0,my_length);
			fw.close();
		}catch(Exception e){
			e.printStackTrace();
			
			fw.close();
			file_writer.file_delete(source_file_name);
			
			break;
		}
		protected_cadex_converter.do_convert(source_file_name,directory_name+file_name,"1800000");
		
		file_writer.file_delete(source_file_name);

		String url=request.getRequestURL().toString();
		url=url.substring(0,url.lastIndexOf('/'))+"/interface.jsp";
		
		web_converter.do_convert(url,"NoName","NoPassword","cad",
			new String[][]{
			new String[] {"type_sub_directory",	""},				//显示内容type
			new String[] {"scene_sub_directory",file_name},			//显示内容scene
 			new String[] {"coordinate",			"location.xyz.txt"},//坐标系选择
 			new String[] {"change_part",		""},				//part换名
 			new String[] {"change_component",	""},				//component换名
 			new String[] {"part_type",			""},				//part类型
 			new String[] {"max_loading_number",	"10"}				//同时下载数量
		});
		
	}while(false);
%>

</div>

</body>
</html>