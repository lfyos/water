This driver is for drawing scene background(background image or sky box).

1.Render List File

	No user-defined parameter exists. Only System Render Parameter File and Part List File exist in a record.
	
	Example:
	
		parameter.list		part.list

2.Part List File
	
	No user-defined parameter exists. Only six system-defined parameters exist in a record, there are 
	
		(1).part user name
		(2).part system name
		(3).part mesh file name:		its geometry is a square,x is from -1 to 1, y is also from -1 to 1,z is always 0.999.
		(4).part material file name:	This file may not exist,it is useless.
		(5).part description file name: This file may not exist.
		(6).part audio file name:		This file may not exist.
	  
	Example:
	
		background_part
			background_part
			background\background.mesh
			background\background.material
			background\background.description
			background\background.mp3


3.Component Assembly File

		Besides four system-defined parameters exist in a component node(component name,part name,component location,child number),
		there are three user-defined component parameters existing after component location. 
			
			(1).component name
			(2).part name
			(3).component location
			
				(5)mode:
					if mode==0 engine draws the no_box.jpg as background.
					if mode!=0 engine draws a skybox(front.jpg,back.jpg,left.jpg,right.jpg,top.jpg,down.jpg) as background.
				(6)directory name: sub-directory name where jpg files exist,its root directory is that of the part. 
				(7)parameter_channel_id: only when draw scene at this parameter_channel_id, this component will be drawn.
				
			(4).child number

		Example:
	
			/* 3name		*/	background_component_0
			/* 3type		*/	background_part
			/* 3location	*/			
								1    0    0    0
								0    1    0    0
								0    0    1    0
								0    0    0    1 
					/*	mode					*/		0 
					/*	directory	name		*/		picture_4/
					/*	parameter_channel_id	*/		0
					
			/* 3children number */		0
			
4.instance driver
	(1)response_event: this method response invokation from client program in web browser through Ajax,
	   it has three function(determined by "operation" parameter);
		(a)."file":			response jpg file, file name is identified by "file" parameter.
		(b)."directory":	set sub-directory name, which is identified by "directory" parameter.
		(c)."mode":			set mode, which is identified by "mode" parameter.

5.component driver
		
6.part driver

7.render.component_event_processor[component_id]


	