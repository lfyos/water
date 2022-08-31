This driver is for displaying button icon on canvas.
when clicked by user, a button icon can response user click.
button icon's function is to emulate button of windows system in a canvas. 

1.Render List File

	No user-defined parameter exists. Only System Render Parameter File and Part List File exist in a record.
	
	Example:
	
		parameter.list		part.list

2.Part List File
	
	No user-defined parameter exists. Only six system-defined parameters exist in a record, there are 
	
		(1).part user name
		(2).part system name
		(3).part mesh file name: its geometry is a square,x is from 0 to 1, y is also from 0 to 1,z is always 0. 
		(4).part material file name: This file may not exist,it is useless.
		(5).part description file name: This file may not exist.
		(6).part audio file name: This file may not exist.
	  
	Example:
			button_operation_part
				button_operation_part
				part\part.mesh
				part\part.material
				part\part.description
				part\part.mp3

3.Component Assembly File

		Besides four system-defined parameters exist in a component node(component name,part name,component location,child number),
		there are six user-defined component parameters existing after component location. 
			
			(1).component name
			(2).part name
			(3).component location
			
				(5)(6)(7)(8) x0,y0,dx,dy:
					these four double values define where to draw the button icon on canvas.
				
				(9)(10) hight_file_name,low_file_name:
					these two file names define texture image file name of the button icon.
					when mouse is on the button icon, image of hight_file_name is drawn, otherwise  image of low_file_name is drawn.
					
					directory of these two file is that of part directory.
					
			(4).child number

		Example:
	
			/* 3name		*/	background_component_0
			/* 3type		*/	background_part
			/* 3location	*/
						
								1    0    0    0
								0    1    0    0
								0    0    1    0
								0    0    0    1 
								
					/*	x0,y0,dx,dy						*/		0.75 0.75 0.1 0.1 
					/*	hight_file_name,low_file_name	*/		image/hight.jpg	image/low.jpg
					
			/* 3children number */		0
	
4.instance driver
	
	instance driver has a public data member
	
		public  int switch_component_id;
				
	this is message target component id. any server-side java program can set this data memory.
	 
	When a button operation component has received a message in client program,
	it will call processing functions in render.component_event_processor[switch_component_id]. 
	
5.component driver

	
6.part driver
	

7.render.component_event_processor[component_id]

