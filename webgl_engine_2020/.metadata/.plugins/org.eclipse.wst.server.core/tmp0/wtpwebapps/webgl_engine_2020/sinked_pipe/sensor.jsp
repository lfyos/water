<%@page 	 language="java"	contentType="charset=UTF-8"					pageEncoding="UTF-8"%>
<jsp:useBean id="server_proxy"	class="kernel_engine_proxy.engine_proxy"	scope="session"></jsp:useBean>
<%
	String operation_string;
	if((operation_string=request.getParameter("operation"))!=null)
		if((operation_string=operation_string.trim()).length()>0){
			String data_string;
			if((data_string=request.getParameter("sensor_data"))==null)
				data_string="0,0,0,1,0,0,0,1,0";

			String url=request.getRequestURL().toString();
			url=url.substring(0,url.lastIndexOf('/'))+"/../interface.jsp";
			
			String ret_val=server_proxy.process_component_call("radar_component",
				new String[][]
				{
					new String[]{"operation",	operation_string},
					new String[]{"sensor_data",	data_string}
				},
				url,"NoName", "NoPassword", "chinese","cad", "sinked_pipe", "utf-8",
				new String[][] {
					new String[] {"sub_directory",		"sinked_pipe"},
					new String[] {"coordinate",			"location.xyz.txt"},
					new String[] {"change_part",		"display_part:radar_part"},
					new String[] {"change_component",	"radar_component:display_component"}
				});
			out.println(ret_val);
		}
%>
