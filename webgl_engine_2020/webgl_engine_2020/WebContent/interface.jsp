<%@page 	 language="java"	contentType="charset=UTF-8"			pageEncoding="UTF-8"%>
<jsp:useBean id="web_server"	class="kernel_service.jsp_server"	scope="application"></jsp:useBean>
<%
	out=web_server.system_call(
		"liufuyan_data_configure",			/*	data configure environment variable		*/
		"liufuyan_proxy_configure",			/*	proxy configure environment variable	*/
		request,response,application,pageContext,out);
%>
