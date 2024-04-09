This driver is for playing audio files.

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
		
		
		(5).part description file name: This file may not exist.
		(6).part audio file name: This file may not exist.
	  
	Example:
	
		render_target_register_part
			render_target_register_part
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
	
			/* 3name		*/         render_target_register_component
			/* 3type		*/         render_target_register_part
			/* 3location		*/
			
					1    0    0    0
					0    1    0    0
					0    0    1    0
					0    0    0    1
					
			/* 3children number */	0
	
4.instance driver
	(1)public String response_event(engine_kernel ek,client_information ci)
		this method response invocation from client program in web browser through Ajax,
		it has six function(determined by "operation" parameter);
		(a)."parameter_channel":	set parameter channel ID.
		(b)."camera":				set camera.
		(c)."set_clear_color":		set clear color.
		(d)."get_clear_color":		get clear color.

5.component driver
	
	
6.part driver
	

7.render.component_event_processor[component_id]

	