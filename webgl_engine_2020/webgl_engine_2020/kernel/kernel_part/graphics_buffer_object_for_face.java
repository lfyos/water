package kernel_part;

import kernel_mesh.auxiliary_file_handler;

public class graphics_buffer_object_for_face
{
	public graphics_buffer_object_creater_container gbocc;
	
	public graphics_buffer_object_for_face(
			auxiliary_file_handler auxiliary_f,int max_material_id,
			part my_part,String my_file_name,String my_file_charset,
			long max_file_data_length,long my_create_buffer_object_bitmap)
	{
		int max_attribute_number=0;
		face_face max_attribute_face_face;
		if((max_attribute_face_face=my_part.part_mesh.max_attribute_face_face())!=null)
			max_attribute_number=max_attribute_face_face.attribute_number;
		String extra_str[]=new String[max_attribute_number];
		for(int i=0;i<max_attribute_number;i++)
			extra_str[i]=max_attribute_face_face.mesh.get_attribute_extra_data(0,i,auxiliary_f);
			
		int flag=0;
		gbocc=new graphics_buffer_object_creater_container();
		for(int body_id=0,body_number=my_part.part_mesh.body_number();body_id<body_number;body_id++){
			body b=my_part.part_mesh.body_array[body_id];
			for(int face_id=0,face_number=b.face_number();face_id<face_number;face_id++){
				face_face ff=b.face_array[face_id].fa_face;
				int primitive_number=ff.mesh.get_primitive_number();
				for(int primitive_id=0,vertex_id=0;primitive_id<primitive_number;primitive_id++){
					String material_str[]=ff.mesh.get_primitive_material(primitive_id,auxiliary_f);
					int material_id=caculate_material_id.caculate(
							my_part.driver,max_material_id,my_part,"face",body_id,face_id,-1,-1,
							material_str[0],material_str[1],material_str[2],material_str[3]);
					graphics_buffer_object_creater gbo=gbocc.get_creater(material_id,
							my_file_name,my_file_charset,my_create_buffer_object_bitmap);
					int primitive_vertex_number=ff.mesh.get_primitive_vertex_number(primitive_id,auxiliary_f);
					for(int primitive_vertex_id=0;primitive_vertex_id<primitive_vertex_number;primitive_vertex_id++){
						int index_id		=ff.mesh.get_primitive_vertex_index(
												primitive_id,primitive_vertex_id,auxiliary_f);
						double my_data[]	=ff.mesh.get_vertex(index_id,auxiliary_f);
						String extra_data	=ff.mesh.get_vertex_extra_data(index_id,auxiliary_f);
						gbo.vertex_begin(my_data[0],my_data[1],my_data[2]);
						gbo.register(my_data,extra_data);

						index_id	=ff.mesh.get_primitive_normal_index(
								primitive_id,primitive_vertex_id,auxiliary_f);
						my_data		=ff.mesh.get_normal(index_id,auxiliary_f);
						extra_data	=ff.mesh.get_normal_extra_data(index_id,auxiliary_f);
						gbo.register(my_data,extra_data);
						gbo.register(material_str[0],material_str[1],material_str[2],material_str[3]);
						gbo.register(Integer.toString(body_id),Integer.toString(face_id),
								Integer.toString(vertex_id++),Integer.toString(flag));
						
						int attribute_number=ff.attribute_number;
						for(int attribute_id=0;attribute_id<attribute_number;attribute_id++){
							index_id=ff.mesh.get_primitive_attribute_index(
									primitive_id,primitive_vertex_id,attribute_id,auxiliary_f);
							my_data		=ff.mesh.get_attribute(index_id,attribute_id,auxiliary_f);
							extra_data	=ff.mesh.get_attribute_extra_data(index_id,attribute_id,auxiliary_f);
							gbo.register(my_data,extra_data);
						}
						for(;attribute_number<max_attribute_number;attribute_number++)
							gbo.register(0,0,0,extra_str[attribute_number]);
					}
					if(gbo.test_end(max_file_data_length,false))
						gbocc.expand_creater_array(material_id);
				}
			}
		}
	}
}
