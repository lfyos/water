This driver is for displaying tag size.

1.Render List File

	No user-defined parameter exists. Only System Render Parameter File and Part List File exist in a record.
	
	Example:
	
		parameter.list		part.list

2.Part List File
	
	No user-defined parameter exists. Only six system-defined parameters exist in a record, there are 
	
		(1).part user name
		(2).part system name
		(3).part mesh file name:
		
				in this mesh file,no face is defined,but three line are defined for display tag edge,
				the three lines are (0,0,0)->(0,1,0),	(0,1,0)->(1,1,0),	(1,1,0)->(1,0,0). 
				its text is displayed by a text part. 
				
		(4).part material file name: This file may not exist,it is useless.
		(5).part description file name: This file may not exist.
		(6).part audio file name: This file may not exist.
	  
	Example:
	
		tag_part
			tag_part
			part\tag.mesh
			part\tag.material
			part\tag.description
			part\tag.mp3

3.Component Assembly File

		Besides four system-defined parameters exist in a component node(component name,part name,component location,child number),
		there are some user-defined component parameters existing after component location. they are 
			
			(1).component name
			(2).part name
			(3).component location
			
				(5)some user-defined component parameters,see example.

			(4).child number

		Example:
	
			/* 3name		*/         tag_controller
			/* 3type		*/         tag_part
			/* 3location	*/
			
							1    0    0    0    
							0    1    0    0    
							0    0    1    0    
							0    0    0    1 
				
					/*	canvas  		*/		18		32				/*	canvas to creating text texture */
					/*	view_text_heigh */		0.04  					/*	tag height in model coordinate	*/
					/*	line_color		*/		0		1		0
					/*	point_color		*/		1		0		0 
					/*	text number		*/		9						/* text component name for displaying tag text	*/ 
					
							text_1
							text_2
							text_3
							text_4
							text_5
							text_6
							text_7
							text_8
							text_9
							
			/* 3children number */		0
	
4.instance driver


5.component driver
	
	
6.part driver
	

7.render.component_event_processor[component_id]

