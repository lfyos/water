This driver is for drawing text in scene.

1.Render List File

	No user-defined parameter exists. Only System Render Parameter File and Part List File exist in a record.
	
	Example:
	
		parameter.list		part.list

2.Part List File
	
	No user-defined parameter exists. Only six system-defined parameters exist in a record, there are 
	
		(1).part user name
		(2).part system name
		(3).part mesh file name:
				its geometry is a square, x is from -0.5 to 0.5, y is from 0.0 to 0.5,z is always 0.0, w is always 1.0.
				its texture coordinate is also a square,x is from 0.0 to 1.0,y is also from 0.0 to 1.0,z is always 0.0,w is always 1.0.
		(4).part material file name:	the content in part material file is of JASON format, it is as follows:
				{
					"font_color"	:	[0.8,0.3,0.3,1.0],
					"text_font"		:	"32px Microsoft YaHei",
					"text_adjust"	:	5
				}
				
			"text_font" 	defines text font.
			"text_adjust"	defines how much to adjust text location.
			"font_color"	defines text color.
			
		(5).part description file name:
		(6).part audio file name: 
	  
	Example:
	
		text
			text
			part\text.mesh
			part\text.material
			part\text.description
			part\text.mp3

3.Component node in Component Assembly File

		No user-defined component parameter exists. Only four system-defined parameters exist in a component node, there are 
			(1).component name
			(2).part name
			(3).component location
			(4).child number
			
		Example:
	
			/* 3name		*/         text_1
			/* 3type		*/         text
			/* 3location	*/
					1    0    0    0
					0    1    0    0
					0    0    1    0
					0    0    0    1
			/* 3children number */		0
	
4.instance driver method
	
	(1)void set_text(text_item new_dt):set shown text,whose parameter is stored in new_dt variable,its data struct is text_item.
	
	text_item has following data fields:
		public String	display_information[];								/*	shown text array	*/
		public int 		canvas_width,canvas_height;							/*	canvas to create text texture	*/
		public double	text_square_width,text_square_height;				/*	text square width and height	*/
		public boolean	view_or_model_coordinate_flag,always_visible_flag;
			/*	if view_or_model_coordinate_flag is true, the text is shown  in view coordinate, otherwise in model coordinate	*/
			/*	if always_visible_flag, all text pixels are shown, if not, dark  pixels will be discard	*/

5.component driver method
	
	
6.part driver method
	
