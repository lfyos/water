<%@page 	 language="java"	contentType="charset=UTF-8"				pageEncoding="UTF-8"%>
<jsp:useBean id="web_server"	class="kernel_servlet.servlet_server"	scope="application"></jsp:useBean>
<%
	web_server.system_call(
		"liufuyan_data_configure",			/*	data configure environment variable		*/
		"liufuyan_proxy_configure",			/*	proxy configure environment variable	*/
		request,response);

	try{
		out.clear();
	}catch(Exception e){
		System.out.println("jsp_server out.clear() exception:	"+e.toString());
		e.printStackTrace();
	}
	out=pageContext.pushBody();
%>
