This driver is for drawing token images.

1.Render List File

	No user-defined parameter exists. Only System Render Parameter File and Part List File exist in a record.
	
	Example:
	
		parameter.list		part.list

2.Part List File
	
	No user-defined parameter exists. Only six system-defined parameters exist in a record, there are 
	
		(1).part user name
		(2).part system name
		(3).part mesh file name:		its geometry is a square,x is from -0.5 to 0.5, y is also from -0.5 to 0.5,z is always 0.0.
		(4).part material file name:	the content in part material file is of JASON format, it's like following 
			
				{
					"view_scale"	:	0.15,
					"view_method"	:	1
				}
				
			"view_scale" 	determines size of the camera_token component.
			"view_method"	determines whether(view_method==1) or not(view_method!==1) discard black pixel when draw camera_token component.
			
		(5).part description file name:
		(6).part audio file name: 
	  
	Example:
	
		camera_token_part
			camera_token_part
			part\part.mesh
			part\part.material
			part\part.description
			part\part.mp3

3.Component Assembly File

		Besides four system-defined parameters existing in a component node(component name,part name,component location,child number),
		There is an user-defined component parameters existing after component location,
		it is the file name of texture which is drawn on the token, its directory is that of part.

		Example:
	
			/* 3name			*/		camera_token_component
			/* 3type			*/		camera_token_part
			/* 3location		*/
			
						1    0    0    0
						0    1    0    0
						0    0    1    0
						0    0    0    1
					
					/* texture_file_name	*/		texture_1.png
					
			/* 3children number */	0
	
4.instance driver
	

5.component driver
	
	
6.part driver

	
7.render.component_event_processor[component_id]

	