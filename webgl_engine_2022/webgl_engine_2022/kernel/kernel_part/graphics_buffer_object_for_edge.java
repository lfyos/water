package kernel_part;

public class graphics_buffer_object_for_edge
{
	public graphics_buffer_object_creater_container gbocc;
	public graphics_buffer_object_for_edge(
			primitive_interface p_i,int max_material_id,
			part my_part,String my_file_name,String my_file_charset,
			long max_file_data_length,long my_create_buffer_object_bitmap)
	{
		int flag=1;
		gbocc=new graphics_buffer_object_creater_container();
		
		for(int body_id=0,body_number=my_part.part_mesh.body_number();body_id<body_number;body_id++){
			body b=my_part.part_mesh.body_array[body_id];
			for(int face_id=0,face_number=b.face_number();face_id<face_number;face_id++){
				face fa=b.face_array[face_id];
				for(int loop_id=0,loop_number=fa.fa_curve.face_loop_number();loop_id<loop_number;loop_id++){
					face_loop fl=fa.fa_curve.f_loop[loop_id];
					for(int edge_id=0,edge_number=fl.edge_number();edge_id<edge_number;edge_id++){
						face_edge fe=fl.edge[edge_id];
						int step;
						switch(fe.curve_type) {
						case "pickup_point_set":
						case "render_point_set":
							continue;
						case "segment":
							step=2;
							break;
						default:
							step=1;
							break;	
						}
						int curve_type_id=curve_type.curve_type_id(fe.curve_type);
						
						double tessellation_location_0[]=null,tessellation_location_1[]=null;
						String tessellation_extra_data_0=null,tessellation_extra_data_1=null;
						String tessellation_material_0[]=null,tessellation_material_1[]=null;
						
						for(int i=0,ni=fe.total_edge_primitive_number-1;i<ni;i+=step){
							if((step==2)||(i==0)) {
								tessellation_location_0		=p_i.get_edge_location_data	(body_id,face_id,loop_id,edge_id,i+0);
								tessellation_extra_data_0	=p_i.get_edge_extra_data	(body_id,face_id,loop_id,edge_id,i+0);
								tessellation_material_0		=p_i.get_edge_material		(body_id,face_id,loop_id,edge_id,i+0);
							}else{
								tessellation_location_0		=tessellation_location_1;
								tessellation_extra_data_0	=tessellation_extra_data_1;
								tessellation_material_0		=tessellation_material_1;
							}
							tessellation_location_1			=p_i.get_edge_location_data	(body_id,face_id,loop_id,edge_id,i+1);
							tessellation_extra_data_1		=p_i.get_edge_extra_data	(body_id,face_id,loop_id,edge_id,i+1);
							tessellation_material_1			=p_i.get_edge_material		(body_id,face_id,loop_id,edge_id,i+1);
							
							int material_id=caculate_material_id.caculate(
									my_part.driver,max_material_id,my_part,"edge",body_id,face_id,loop_id,edge_id,
									tessellation_material_0[0],tessellation_material_0[1],
									tessellation_material_0[2],tessellation_material_0[3]);
							graphics_buffer_object_creater gbo=gbocc.get_creater(material_id,
									my_file_name,my_file_charset,my_create_buffer_object_bitmap);
								
							gbo.vertex_begin(tessellation_location_0[0],tessellation_location_0[1],tessellation_location_0[2]);
							
							gbo.register(tessellation_location_0[0],
										 tessellation_location_0[1],
										 tessellation_location_0[2],
										 tessellation_extra_data_0);
							gbo.register(tessellation_location_1[0]-tessellation_location_0[0],
										 tessellation_location_1[1]-tessellation_location_0[1],
										 tessellation_location_1[2]-tessellation_location_0[2],
										 tessellation_extra_data_0);
							gbo.register(tessellation_material_0[0],
										 tessellation_material_0[1],
										 tessellation_material_0[2],
										 tessellation_material_0[3]);
							
							gbo.register(fe.system_id,2*i+0,curve_type_id,Integer.toString(flag));

							gbo.vertex_begin(tessellation_location_1[0],tessellation_location_1[1],tessellation_location_1[2]);
							
							gbo.register(tessellation_location_1[0],
										 tessellation_location_1[1],
										 tessellation_location_1[2],
										 tessellation_extra_data_1);
							
							gbo.register(
										tessellation_location_1[0]-tessellation_location_0[0],
										tessellation_location_1[1]-tessellation_location_0[1],
										tessellation_location_1[2]-tessellation_location_0[2],
										tessellation_extra_data_1);
							
							gbo.register(tessellation_material_1[0],
										 tessellation_material_1[1],
										 tessellation_material_1[2],
										 tessellation_material_1[3]);
	
							gbo.register(fe.system_id,2*i+1,curve_type_id,Integer.toString(flag));
							
							if(gbo.test_end(max_file_data_length,false))
								gbocc.expand_creater_array(material_id);
						}
					}	
				}
			}
		}
	}
}
