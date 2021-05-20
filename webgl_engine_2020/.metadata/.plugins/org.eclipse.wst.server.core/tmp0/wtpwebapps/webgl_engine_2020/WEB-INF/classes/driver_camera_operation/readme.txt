This driver is for displaying camera direction button on canvas.
when user click a camera direction button, new direction of camera is set.
the driver also response camera operation request from client.

1.Render List File

	No user-defined parameter exists. Only System Render Parameter File and Part List File exist in a record.
	
	Example:
	
		parameter.list		part.list

2.Part List File
	
	Besides six system-defined parameters existing in a record, there are also four user-defined parameters
	
		(1).part user name
		(2).part system name
		(3).part mesh file name:  
		(4).part material file name: This file may not exist,it is useless.
		(5).part description file name: This file may not exist.
		(6).part audio file name: This file may not exist.
		
		(7)(8)xy:	where this part is drawn on canvas.
		(9)scale:	how much to scale part mesh when draw this part.this parameter decides size of camera direction button on canvas.
		(10)camera_modifier_id: which modifier_container used to implement camera operation. Usually this parameter is 0.
	  
	Example:
			camera_operation_part
				camera_operation_part
				part\part.SLDPRT.mesh
				part\part.material
				part\part.description
				part\part.mp3
				
				/*		x			y			scale		camera_modifier_id		*/
						0.85		0.75		15.0		0

3.Component Assembly File

		No user-defined component parameter exists. Only four system-defined parameters exist in a component node, there are 
			
			(1).component name
			(2).part name
			(3).component location
			(4).child number

		Example:
	
			/* 3name		*/         camera_operation_component
			/* 3type		*/         camera_operation_part
			/* 3location	*/
			
					1    0    0    0
					0    1    0    0
					0    0    1    0
					0    0    0    1
					
			/* 3children number */	0
	
4.instance driver

	(1)response_event: this method response invokation from client program in web browser through Ajax,
	   it has more than a dozen functions(determined by "operation" parameter from client);
		
		(a)."set_camera_data":set location of camera component and distance parameter of camera.
		
				The "data" parameter from client is NOT null, then
				{
					 if("data" parameter from client is  null)
					 then 
					 	set location of camera component to a location defined by "data" parameter.
					 else
					 	set location of camera component to a location defined by "data" parameter multified by an Z rotation defined by "rotate" parameter from client.
				}
				if "distance" parameter from client is NOT null, then
				{
					set "distance" of camera to parameter defined by "distance" parameter.
				}
				if "modifier" parameter from client is NOT null, then
				{
					set "camera_modifier_id" data member in instance drivar to that defined by "modifier" parameter.
				}
				if "flag" parameter from client is NOT null, then
				{
					set "cacaulate_location_flag" data member of component parameter to that defined by "flag" parameter.
				}
		(b)."get_camera_data": response a JASON data to client, the data has three fields.
				(  I)"matrix":		16 double values of component location
				( II)"distance":	distance of camera parameter
				(III)"flag":		cacaulate_location_flag data member of component parameter
		(c)."body_face_direct":		

5.component driver
	
	
6.part driver
	

7.render.component_event_processor[component_id]

