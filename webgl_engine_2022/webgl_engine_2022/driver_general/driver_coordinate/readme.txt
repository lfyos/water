This driver is for display coordinate in scene.There are two types of displayed coordinate:
	(1).camera component coordinate			(2).selected comopnent coordinate 

1.Render List File

	No user-defined parameter exists. Only System Render Parameter File and Part List File exist in a record.
	
	Example:
	
		parameter.list		part.list


2.Part List File
	
	No user-defined parameter exists. Only six system-defined parameters exist in a record, there are 
	
		(1).part user name
		(2).part system name
		(3).part mesh file name: define three lines for displaying X axis, Y axis and Z axis. No face is defined. 
		(4).part material file name: The contents of the file are as follows, two parameters are defined in this file£º
		 
		 		/*		camera_length_scale			*/		10000
				/*		selection_length_scale		*/		1
				
				camera_length_scale defines size of camera component coordinate.
				selection_length_scale defines size of selected component coordinate.
				
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
		there are two user-defined component parameters existing after component location. 
			
			(1).component name
			(2).part name
			(3).component location
			
				(5)(6) boolean abandon_camera_display_flag,abandon_selected_display_flag:
						 these two parameters define whether or not  displaying two types of coordinate

			(4).child number

		Example:
	
			/* 3name		*/			coordinate
			/* 3type		*/			coordinate_part
			/* 3location	*/
								1    0    0    0    
								0    1    0    0    
								0    0    1    0    
								0    0    0    1  
					
					/*	abandon display coordinate		*/		no
					/*	abandon select coordinate		*/		yes
					
			/* 3children number */		0	



4.instance driver

	(1)public String response_event(engine_kernel ek,client_information ci)
		this method response invocation from client program in web browser through Ajax,
	
	   it has two parameter from client
		(a)."camera_display_flag":	turn on or off whether or not display camera component coordinate.
		(b)."selected_display_flag":turn off sound playing selected comopnent coordinate.


5.component driver

	
6.part driver
	

7.render.component_event_processor[component_id]

	