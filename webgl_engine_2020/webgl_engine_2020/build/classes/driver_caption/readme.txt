This driver is for display caption text in scene,It selects the display text content in the following order,
if each is not blank, select it, if NOT blank or null, select the next one£º

	(1) pickup component information
	(2) ci.message_display.get_display_message()
	(3) ek.collector_stack.get_top_collector().description

1.Render List File

	No user-defined parameter exists. Only System Render Parameter File and Part List File exist in a record.
	
	Example:
	
		parameter.list		part.list

2.Part List File
	
	No user-defined parameter exists. Only six system-defined parameters exist in a record, there are 
	
		(1).part user name
		(2).part system name
		(3).part mesh file name: no geometry is drawn, content in this file should as simple as possible. But this file must exist. 
		(4).part material file name: This file may not exist,it is useless.
		(5).part description file name: This file may not exist.
		(6).part audio file name: This file may not exist.
	  
	Example:
	
		caption_part
			caption_part
			part\part.mesh
			part\part.material
			part\part.description
			part\part.mp3

3.Component node in Component Assembly File

		Besides four system-defined parameters exist in a component node(component name,part name,component location,child number),
		there are five user-defined component parameters existing after component location. 
			
			(1).component name
			(2).part name
			(3).component location
			
				(5)String text_component_name:						the shown text.
				(6)(7)int canvas_width,canvas_height:				width and height of canvas to create text texture.
				(8)(9)double text_square_width,text_square_height:	text size in model coordinate.

					
			(4).child number

			
		Example:
	
			/* 3name		*/			caption_controller
			/* 3type		*/			caption_part
			/* 3location	*/
					
					1    0    0    0
					0    1    0    0
					0    0    1    0
					0    0    0    1  

					text_0
					4096		64
					4			0.25
					
			/* 3children number */     0	
	
4.instance driver

	
5.component driver

	
6.part driver

	
7.render.component_event_processor[component_id]

