The function of this driver is to configure file proxy.
If possible, client javascript program in browser can download files from file proxy instead of from engine.
In this way,file-download performance can improve greatly. 


1.Render List File

	No user-defined parameter exists. Only System Render Parameter File and Part List File exist in a record.
	
	Example:
	
		parameter.list		part.list


2.Part List File
	
	No user-defined parameter exists. Only six system-defined parameters exist in a record, there are 
	
		(1).part user name
		(2).part system name
		(3).part mesh file name: no geometry is drawn, content in this file should as simple as possible. But this file must exist. 
		(4).part material file name:
		
				The first parameter in time_length, client javascript use this parameter to test proxy server.
				client javascript sends a test message to proxy server,
				if it responses too late(delay time > time_length), client javascript will not use the proxy.
				
				follow time_length are proxy records.
				
				if first record parameter is boolean value,it will have three parameter.
	
				the first parameter is test_flag.
				if it is true, client javascript will test whether its is effective.
				if it is false, client javascript will use it directly without test.
				
				the second parameter is encode_flag.
				if it is true, client javascript will do proxy url coding.
				if it is false, client javascript will not do proxy url coding.
				
				the third parameter is proxy file server URL.
				
				
				if first record parameter is not boolean value,it will have only parameter.
				proxy file server URL will be eninge URL, test_flag will be false, encode_flag will be true.
				
				Example:
				
					/*		time_length		*/		5000

					default_proxy_url
					
					true	true	http://localhost:8080/graphics_engine/interface.jsp?channel=buffer&file=
					true	false	http://localhost:8080/graphics_engine/temp/proxy_root_directory/

					true	true	http://localhost:8080/graphics_engine/interface.jsp?channel=buffer&file=
					true	false	http://localhost:8080/graphics_engine/temp/proxy_root_directory/
					
					true	true	http://localhost:8080/graphics_engine/interface.jsp?channel=proxy&file=
					true	false	http://localhost:8080/graphics_engine/temp/proxy_root_directory/

					true	true	http://localhost:8080/graphics_engine/interface.jsp?channel=proxy&file=
					true	false	http://localhost:8080/graphics_engine/temp/proxy_root_directory/


		(5).part description file name: This file may not exist.
		(6).part audio file name: This file may not exist.
	  
	Example:
	
		proxy_part
				proxy_part
				part\part.mesh
				part\part.material
				part\part.description
				part\part.mp3


3.Component Assembly File

		No user-defined component parameter exists. Only four system-defined parameters exist in a component node, there are 
			(1).component name
			(2).part name
			(3).component location
			(4).child number

		Example:
	
			/* 3name		*/         proxy_component
			/* 3type		*/         proxy_part
			/* 3location	*/
			         
			         1    0    0    0    
			         0    1    0    0    
			         0    0    1    0    
			         0    0    0    1
			          
			/* 3children number */		0
	
	
4.instance driver


5.component driver


6.part driver
	
	
7.render.component_event_processor[component_id]

	
	