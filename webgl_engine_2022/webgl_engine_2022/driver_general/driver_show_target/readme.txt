This driver is for drawing texture object of rendering target,it is usually used when you debug your program.

1.Render List File

	No user-defined parameter exists. Only System Render Parameter File and Part List File exist in a record.
	
	Example:
	
		parameter.list		part.list

2.Part List File
	
	No user-defined parameter exists. Only six system-defined parameters exist in a record, there are 
	
		(1).part user name
		(2).part system name
		(3).part mesh file name:
				its geometry is a square,x is from 0 to 1, y is also from 0 to 1,z is always 0.
				first attribute is also a square,x is from 0 to 1, y is also from 0 to 1,z is always 0.
		(4).part material file name: the content in part material file is of JASON format, it's like following
		
				{
					"start_point"	:		[-1.0,	-1.0,	-0.999,	1.0],
					"end_point"		:		[-0.5,	-0.5,	-0.999,	1.0]
				}
				
				this JASON file defines where to draw texture object on screen.
		
		(5).part description file name: This file may not exist.
		(6).part audio file name:		This file may not exist.
  
	Example:
	
		show_target_part_0
			show_target_part_0
			part\part.mesh
			part\part_0.material
			part\part.description
			part\part.mp3


3.Component Assembly File

		Besides four system-defined parameters exist in a component node(component name,part name,component location,child number),
		there are two user-defined component parameters existing after component location. 
			
			(1).component name
			(2).part name
			(3).component location
			
				(5)target_name:	which rendering target.
				(6)texture_id:	which texture of that target.
				
			(4).child number

		Example:
	
			/* 3name		*/	show_target_component_0
			/* 3type		*/	show_target_part_0
			/* 3location	*/			
								1    0    0    0
								0    1    0    0
								0    0    1    0
								0    0    0    1 
								
					/*	target_name		*/		component_pickup_component
					/*	texture_id		*/		0
					
			/* 3children number */		0
	
4.instance driver
	

5.component driver


6.part driver

	
7.render.component_event_processor[component_id]
	
	
	