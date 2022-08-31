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
		(4).part material file name: This file may not exist,it is useless.
		(5).part description file name: This file may not exist.
		(6).part audio file name: This file may not exist.
	  
	Example:
	
		audio_part
			audio_part
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
	
			/* 3name			*/		audio_component
			/* 3type			*/		audio_part
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
		(a)."turn_on":	turn on sound playing.
		(b)."turn_off":	turn off sound playing.
		(c)."ended":	terminated sound playing.
		(d). "audio":	response audio content.
		(e) "state":	response playing mode("true" or "false").
		(f) "play":		play an audio file. File name is identified by "file" parameter,
						file directory is component_directory_name field of the component object.

5.component driver
	(1)void mark_terminate_flag():					terminated sound playing.
	(2)boolean get_terminate_flag():				return playing mode(true or false).
	(3)void set_audio(String new_audio_file_name):	play an audio file,its path is new_audio_file_name.
	(4)String get_audio_file_name():				return audio file path.
	(5)void turn_on_off(boolean my_on_off_flag):	turn on or turn off audio playing.
	(6)public boolean get_state():					return playing mode( true or false).
	
6.part driver
	

7.render.component_event_processor[component_id]

