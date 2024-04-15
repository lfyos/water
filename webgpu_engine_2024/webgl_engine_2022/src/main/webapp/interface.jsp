<%@page 	 language="java"		contentType="charset=UTF-8"				pageEncoding="UTF-8"%>
<jsp:useBean id="webgpu_servlet"	class="engine_interface.webgpu_servlet"	scope="application"></jsp:useBean>
<%
	webgpu_servlet.process_call(request,response);
	try{
		out.clear();
	}catch(Exception e) {
		System.out.println("jsp_server out.clear() exception:	"+e.toString());
		e.printStackTrace();
	}
	out= pageContext.pushBody();
%>
