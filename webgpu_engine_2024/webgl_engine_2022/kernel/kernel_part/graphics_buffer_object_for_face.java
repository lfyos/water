package kernel_part;

public class graphics_buffer_object_for_face
{
	public graphics_buffer_object_creater_container gbocc;
	
	public graphics_buffer_object_for_face(
			primitive_interface p_i,int max_material_id,
			part my_part,String my_file_name,String my_file_charset,
			long max_file_data_length,long my_create_buffer_object_bitmap)
	{
		double default_attribute_double[]=my_part.part_mesh.default_attribute_double;
		String default_attribute_string[]=my_part.part_mesh.default_attribute_string;
		int max_attribute_number=default_attribute_string.length;

		gbocc=new graphics_buffer_object_creater_container();
		for(int body_id=0,body_number=my_part.part_mesh.body_number();body_id<body_number;body_id++){
			body b=my_part.part_mesh.body_array[body_id];
			for(int face_id=0,face_number=b.face_number();face_id<face_number;face_id++){
				face_face ff=b.face_array[face_id].fa_face;
				int primitive_number=ff.total_face_primitive_number,attribute_number=ff.attribute_number;
				for(int primitive_id=0;primitive_id<primitive_number;primitive_id++){
					String material_str[]=p_i.get_primitive_material(body_id,face_id,primitive_id);
					int primitive_vertex_number=p_i.get_primitive_vertex_number(body_id,face_id,primitive_id);
					int material_id=caculate_material_id.caculate(
							my_part.driver,max_material_id,my_part,"face",body_id,face_id,-1,-1,
							material_str[0],material_str[1],material_str[2],material_str[3]);
					graphics_buffer_object_creater gbo=gbocc.get_creater(material_id,
							my_file_name,my_file_charset,my_create_buffer_object_bitmap);
					for(int primitive_vertex_id=0;primitive_vertex_id<primitive_vertex_number;primitive_vertex_id++){
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
					if(gbo.test_end(max_file_data_length,false))
						gbocc.expand_creater_array(material_id);
				}
			}
		}
	}
}
