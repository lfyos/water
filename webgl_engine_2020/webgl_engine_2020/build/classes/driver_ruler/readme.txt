This driver is for drawing ruler on screen.

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
				color are also defined in mesh file as first attribute. 
		(4).part material file name:	This file may not exist,it is useless.
		(5).part description file name: This file may not exist.
		(6).part audio file name:		This file may not exist.
  
	Example:
	
		ruler_part
			ruler_part
			part\part.mesh
			part\part.material
			part\part.description
			part\part.mp3


3.Component Assembly File

		Besides four system-defined parameters exist in a component node(component name,part name,component location,child number),
		there are four user-defined component parameters existing after component location,
		they define ruler region on screen. 
			
			(1).component name
			(2).part name
			(3).component location
				
				(5).ruler region on screen(4 double values)
				
			(4).child number

		Example:
	
			/*	2:name	*/	ruler_component
			/*	2:type	*/	ruler_part
			/*	2:location	*/
			
				1		0		0		0		
				0		1		0		0		
				0		0		1		0	
				0		0		0		1	
			
						/*	region data	*/	0.750	-0.8625		0.775	 0.425
			
			/*	2:child_number	*/	0
	
4.instance driver
	

5.component driver


6.part driver

	
7.render.component_event_processor[component_id]

