<%@page 	 language="java"		contentType="charset=UTF-8"				pageEncoding="UTF-8"%>
<jsp:useBean id="webgpu_servlet"	class="engine_interface.webgpu_engine"	scope="application"></jsp:useBean>
<%
	webgpu_servlet.process_system_call(request,response,"lfy_data_dir","lfy_temp_dir");

	try{
		out.clear();
	}catch(Exception e) {
		System.out.println("jsp_server out.clear() exception:	"+e.toString());
		e.printStackTrace();
	}
	
	out= pageContext.pushBody();
%>
