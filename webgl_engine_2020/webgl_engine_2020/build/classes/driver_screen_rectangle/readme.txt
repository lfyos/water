This driver is for drawing line rectangle on screen,
which is defined by "data" field of render.component_event_processor[component_id].

1.Render List File

	No user-defined parameter exists. Only System Render Parameter File and Part List File exist in a record.
	
	Example:
	
		parameter.list		part.list

2.Part List File
	
	No user-defined parameter exists. Only six system-defined parameters exist in a record, there are 
	
		(1).part user name
		(2).part system name
		(3).part mesh file name:its geometry is a square,x is from 0 to 1, y is also from 0 to 1,z is always 0.
		(4).part material file name:	This file may not exist,it is useless.
		(5).part description file name: This file may not exist.
		(6).part audio file name:		This file may not exist.
  
	Example:
	
		screen_rectangle_part
			screen_rectangle_part
			part\part.mesh
			part\part.material
			part\part.description
			part\part.mp3


3.Component Assembly File

		No user-defined parameter exists. Only six system-defined parameters exist in a record, there are 
	
		(1).part user name
		(2).part system name
		(3).part mesh file name:its geometry is a line square,x is from 0 to 1, y is also from 0 to 1,z is always 0. 
		(4).part material file name: This file may not exist,it is useless.
		(5).part description file name: This file may not exist.
		(6).part audio file name: This file may not exist.

		Example:
	
			/* 3name		*/         screen_rectangle_component
			/* 3type		*/         screen_rectangle_part
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
	
	"data" field of this structure define place and size of rectangle on screen,it has four doubles(x0,y0,x1,y1).
		
	
	