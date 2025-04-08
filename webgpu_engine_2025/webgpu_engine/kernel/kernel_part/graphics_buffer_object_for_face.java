package kernel_part;

public class graphics_buffer_object_for_face
{
	public graphics_buffer_object_creater_container gbocc;
	
	private primitive_interface p_i;
	private int max_material_id;
	private part gbo_part;
	private String file_name,file_charset;
	private long max_file_data_length,create_buffer_object_bitmap;
	
	private double default_attribute_double[];
	private String default_attribute_string[];
	private int max_attribute_number;
	
	private void create_primitive_vertex(graphics_buffer_object_creater gbo,String material_str[],
			int body_id,int face_id,int primitive_id,int primitive_vertex_id,int attribute_number)
	{
		double my_data[]	=p_i.get_primitive_vertex_location_data(body_id,face_id,primitive_id,primitive_vertex_id);
		String extra_data	=p_i.get_primitive_vertex_location_extra_data(body_id,face_id,primitive_id,primitive_vertex_id);
		gbo.vertex_begin(my_data[0],my_data[1],my_data[2]);
		gbo.register(my_data,extra_data);
		
		my_data		=p_i.get_primitive_vertex_normal_data(body_id,face_id,primitive_id,primitive_vertex_id);
		extra_data	=p_i.get_primitive_vertex_normal_extra_data(body_id,face_id,primitive_id,primitive_vertex_id);
		gbo.register(my_data,extra_data);
		
		gbo.register(material_str[0],material_str[1],material_str[2],material_str[3]);
		gbo.register(body_id,face_id,primitive_id,Integer.toString(primitive_vertex_id));
		
		for(int attribute_id=0;attribute_id<attribute_number;attribute_id++){
			my_data		=p_i.get_primitive_vertex_attribute_data(body_id,face_id,primitive_id,primitive_vertex_id,attribute_id);
			extra_data	=p_i.get_primitive_vertex_attribute_extra_data(body_id,face_id,primitive_id,primitive_vertex_id,attribute_id);
			gbo.register(my_data,extra_data);
		}
		for(int attribute_id=attribute_number;attribute_id<max_attribute_number;attribute_id++) {
			gbo.register(	default_attribute_double[3*attribute_id+0],
							default_attribute_double[3*attribute_id+1],
							default_attribute_double[3*attribute_id+2],
							default_attribute_string[  attribute_id  ]);
		}
	}
	private void create_primitive(int primitive_id,int body_id,int face_id,int attribute_number)
	{
		String material_str[]=p_i.get_primitive_material(body_id,face_id,primitive_id);
		int material_id=caculate_material_id.caculate(gbo_part.driver,
				max_material_id,gbo_part,"face",body_id,face_id,-1,-1,material_str);
		
		graphics_buffer_object_creater gbo=gbocc.get_creater(
				material_id,file_name,file_charset,create_buffer_object_bitmap);
		
		int primitive_vertex_number=p_i.get_primitive_vertex_number(body_id,face_id,primitive_id);
		for(int primitive_vertex_id=0;primitive_vertex_id<primitive_vertex_number;primitive_vertex_id++)
			create_primitive_vertex(gbo,material_str,
					body_id,face_id,primitive_id,primitive_vertex_id,attribute_number);
			
		if(gbo.test_end(max_file_data_length,false))
			gbocc.expand_creater_array(material_id);
	}
	
	public graphics_buffer_object_for_face(
			primitive_interface my_p_i,int my_max_material_id,
			part my_gbo_part,String my_file_name,String my_file_charset,
			long my_max_file_data_length,long my_create_buffer_object_bitmap)
	{
		p_i							=my_p_i;
		max_material_id				=my_max_material_id;
		gbo_part					=my_gbo_part;
		file_name					=my_file_name;
		file_charset				=my_file_charset;
		max_file_data_length		=my_max_file_data_length;
		create_buffer_object_bitmap	=my_create_buffer_object_bitmap;
		
		
		default_attribute_double=gbo_part.part_mesh.default_attribute_double;
		default_attribute_string=gbo_part.part_mesh.default_attribute_string;
		max_attribute_number	=default_attribute_string.length;

		gbocc=new graphics_buffer_object_creater_container();
		int body_number=gbo_part.part_mesh.body_number();
		for(int body_id=0;body_id<body_number;body_id++){
			body b=gbo_part.part_mesh.body_array[body_id];
			int face_number=b.face_number();
			for(int face_id=0;face_id<face_number;face_id++){
				face_face ff=b.face_array[face_id].fa_face;
				int primitive_number=ff.total_face_primitive_number;
				for(int primitive_id=0;primitive_id<primitive_number;primitive_id++)
					create_primitive(primitive_id,body_id,face_id,ff.attribute_number);
			}
		}
	}
}
