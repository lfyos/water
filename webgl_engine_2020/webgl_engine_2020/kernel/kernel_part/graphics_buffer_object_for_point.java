package kernel_part;

import kernel_transformation.point;

public class graphics_buffer_object_for_point
{
	public graphics_buffer_object_creater_container gbocc;
	
	private point line_create(int flag,
		int body_id,int face_id,int loop_id,int edge_id,face_edge fe,part my_part,
		String my_file_name,String my_file_charset,String curve_type_id,
		long my_create_buffer_object_bitmap,int max_material_id,long max_file_data_length)
	{
		int material_id=caculate_material_id.caculate(my_part.driver,
				max_material_id,my_part,"line",body_id,face_id,loop_id,edge_id,
				fe.parameter_material[0],fe.parameter_material[1],
				fe.parameter_material[2],fe.parameter_material[3]);
		graphics_buffer_object_creater gbo=gbocc.get_creater(material_id,
				my_file_name,my_file_charset,my_create_buffer_object_bitmap);
		
		point normal=fe.end_point.sub(fe.start_point);
		point pp	=fe.end_point.add(fe.start_point).scale(0.5);
		
		gbo.vertex_begin(pp.x,pp.y,pp.z);
			
		gbo.register(pp.x,		pp.y,		pp.z,		fe.parameter_extra_data);
		gbo.register(normal.x,	normal.y,	normal.z,	"1");
		gbo.register(
				fe.parameter_material[0],fe.parameter_material[1],
				fe.parameter_material[2],fe.parameter_material[3]);
		gbo.register(Integer.toString(body_id),Integer.toString(face_id),"3",Integer.toString(flag));
		gbo.register(Integer.toString(loop_id),Integer.toString(edge_id),curve_type_id,curve_type_id);
		if(gbo.test_end(max_file_data_length,false))
			gbocc.expand_creater_array(material_id);
		
		return normal;
	}
	private point circle_create(int flag,int body_id,int face_id,int loop_id,int edge_id,face_edge fe,part my_part,
			String my_file_name,String my_file_charset,String curve_type_id,
			long my_create_buffer_object_bitmap,int max_material_id,long max_file_data_length)
	{
		int material_id=caculate_material_id.caculate(my_part.driver,
				max_material_id,my_part,"circle",body_id,face_id,loop_id,edge_id,
				fe.parameter_material[0],fe.parameter_material[1],
				fe.parameter_material[2],fe.parameter_material[3]);
		graphics_buffer_object_creater gbo=gbocc.get_creater(material_id,
				my_file_name,my_file_charset,my_create_buffer_object_bitmap);
		
		gbo.vertex_begin(fe.curve_parameter[0],fe.curve_parameter[1],fe.curve_parameter[2]);
			
		gbo.register(fe.curve_parameter[0],fe.curve_parameter[1],fe.curve_parameter[2],fe.parameter_extra_data);
		gbo.register(fe.curve_parameter[3],fe.curve_parameter[4],fe.curve_parameter[5],"1");
		gbo.register(fe.parameter_material[0],fe.parameter_material[1],fe.parameter_material[2],fe.parameter_material[3]);
		gbo.register(Integer.toString(body_id),Integer.toString(face_id),"4",Integer.toString(flag));
		gbo.register(Integer.toString(loop_id),Integer.toString(edge_id),curve_type_id,curve_type_id);

		if(gbo.test_end(max_file_data_length,false))
			gbocc.expand_creater_array(material_id);
		
		return (new point(fe.curve_parameter[3],fe.curve_parameter[4],fe.curve_parameter[5])).expand(1);
	}
	private point ellipse_hyperbola_parabola_create(int flag,
			int body_id,int face_id,int loop_id,int edge_id,face_edge fe,part my_part,
			String my_file_name,String my_file_charset,String curve_type_id,
			long my_create_buffer_object_bitmap,int max_material_id,long max_file_data_length)
	{
		int material_id=caculate_material_id.caculate(my_part.driver,
				max_material_id,my_part,fe.curve_type,body_id,face_id,loop_id,edge_id,
				fe.parameter_material[0],fe.parameter_material[1],
				fe.parameter_material[2],fe.parameter_material[3]);
		point point_array[]=caculate_part_items.caculate_point_for_ellipse_hyperbola_parabola(fe.curve_parameter,fe.curve_type);
		point normal=caculate_part_items.caculate_normal_for_ellipse_hyperbola_parabola(fe.curve_parameter);
	
		for(int point_i=0,point_n=point_array.length;point_i<point_n;point_i++){
			graphics_buffer_object_creater gbo=gbocc.get_creater(material_id,
				my_file_name,my_file_charset,my_create_buffer_object_bitmap);
			
			point pp=point_array[point_i];
			
			gbo.vertex_begin(pp.x,pp.y,pp.z);

			gbo.register(pp.x,		pp.y,		pp.z,		fe.parameter_extra_data);
			gbo.register(normal.x,	normal.y,	normal.z,	"1");
			gbo.register(fe.parameter_material[0],fe.parameter_material[1],fe.parameter_material[2],fe.parameter_material[3]);
			gbo.register(Integer.toString(body_id),Integer.toString(face_id),
				Integer.toString(point_i+5),Integer.toString(flag));
			gbo.register(Integer.toString(loop_id),Integer.toString(edge_id),curve_type_id,curve_type_id);
			
			if(gbo.test_end(max_file_data_length,false))
				gbocc.expand_creater_array(material_id);
		}
		
		return normal;
	}
	
	private void pickup_point_set_create(
			int flag,int body_id,int face_id,int loop_id,int edge_id,face_edge fe,part my_part,
			String my_file_name,String my_file_charset,String curve_type_id,
			long my_create_buffer_object_bitmap,int max_material_id,long max_file_data_length)
	{
		int material_id=caculate_material_id.caculate(my_part.driver,
				max_material_id,my_part,"pickup_point_set",body_id,face_id,loop_id,edge_id,
				fe.parameter_material[0],fe.parameter_material[1],
				fe.parameter_material[2],fe.parameter_material[3]);
		
		int point_n=0;
		if(fe.curve_parameter!=null)
			point_n=fe.curve_parameter.length/3;
		
		for(int point_i=0;point_i<point_n;point_i++){
			graphics_buffer_object_creater gbo=gbocc.get_creater(material_id,
					my_file_name,my_file_charset,my_create_buffer_object_bitmap);
			
			double x=fe.curve_parameter[3*point_i+0];
			double y=fe.curve_parameter[3*point_i+1];
			double z=fe.curve_parameter[3*point_i+2];
			
			gbo.vertex_begin(x,y,z);

			gbo.register(x,y,z,fe.parameter_extra_data);
			gbo.register(x,y,z,"1");
			gbo.register(fe.parameter_material[0],fe.parameter_material[1],fe.parameter_material[2],fe.parameter_material[3]);
			gbo.register(Integer.toString(body_id),Integer.toString(face_id),
					Integer.toString(point_i+1000),Integer.toString(flag));
			gbo.register(Integer.toString(loop_id),Integer.toString(edge_id),curve_type_id,curve_type_id);
			
			if(gbo.test_end(max_file_data_length,false))
				gbocc.expand_creater_array(material_id);
		}
	}
	private void render_point_set_create(primitive_interface p_i,
			int flag,int body_id,int face_id,int loop_id,int edge_id,face_edge fe,part my_part,
			String my_file_name,String my_file_charset,String curve_type_id,
			long my_create_buffer_object_bitmap,int max_material_id,long max_file_data_length)
	{
		for(int point_i=0;point_i<fe.total_point_primitive_number;point_i++){
			double my_location_data[]		=p_i.get_point_location_data(body_id,face_id,loop_id,edge_id,point_i);
			String my_location_extra_data	=p_i.get_point_extra_data	(body_id,face_id,loop_id,edge_id,point_i);
			String my_material[]			=p_i.get_point_material		(body_id,face_id,loop_id,edge_id,point_i);
			
			int material_id=caculate_material_id.caculate(
					my_part.driver,max_material_id,my_part,"render_point_set",body_id,face_id,loop_id,edge_id,
					my_material[0],my_material[1],my_material[2],my_material[3]);

			graphics_buffer_object_creater gbo=gbocc.get_creater(material_id,
					my_file_name,my_file_charset,my_create_buffer_object_bitmap);
			
			double x=my_location_data[0],y=my_location_data[1],z=my_location_data[2];
			
			gbo.vertex_begin(x,y,z);

			gbo.register(x,y,z,my_location_extra_data);
			gbo.register(x,y,z,"1");
			gbo.register(my_material[0],my_material[1],my_material[2],my_material[3]);
			gbo.register(Integer.toString(body_id),Integer.toString(face_id),
					Integer.toString(point_i+1000),Integer.toString(flag));
			gbo.register(Integer.toString(loop_id),Integer.toString(edge_id),curve_type_id,curve_type_id);
			
			if(gbo.test_end(max_file_data_length,false))
				gbocc.expand_creater_array(material_id);
		}
	}
	private void start_end_create(int flag,int body_id,int face_id,int loop_id,int edge_id,point start_end_normal,
			face_edge fe,part my_part,String my_file_name,String my_file_charset,String curve_type_id,
			long my_create_buffer_object_bitmap,int max_material_id,long max_file_data_length)
	{
		if(fe.start_point!=null){
			int material_id=caculate_material_id.caculate(
					my_part.driver,max_material_id,my_part,"start",body_id,face_id,loop_id,edge_id,
					fe.start_point_material[0],fe.start_point_material[1],
					fe.start_point_material[2],fe.start_point_material[3]);
			graphics_buffer_object_creater gbo=gbocc.get_creater(material_id,
					my_file_name,my_file_charset,my_create_buffer_object_bitmap);
			
			gbo.vertex_begin(fe.start_point.x,fe.start_point.y,fe.start_point.z);

			gbo.register(fe.start_point.x,fe.start_point.y,fe.start_point.z,fe.start_extra_data);
			gbo.register(start_end_normal.x,start_end_normal.y,start_end_normal.z,"1");
			gbo.register(fe.start_point_material[0],fe.start_point_material[1],
					 fe.start_point_material[2],fe.start_point_material[3]);
			gbo.register(Integer.toString(body_id),Integer.toString(face_id),"1",Integer.toString(flag));
			gbo.register(Integer.toString(loop_id),Integer.toString(edge_id),curve_type_id,curve_type_id);
			
			if(gbo.test_end(max_file_data_length,false))
				gbocc.expand_creater_array(material_id);
		}
		if(fe.end_point!=null){
			int material_id=caculate_material_id.caculate(
					my_part.driver,max_material_id,my_part,"end",body_id,face_id,loop_id,edge_id,
					fe.end_point_material[0],fe.end_point_material[1],
					fe.end_point_material[2],fe.end_point_material[3]);
			graphics_buffer_object_creater gbo=gbocc.get_creater(material_id,
					my_file_name,my_file_charset,my_create_buffer_object_bitmap);
			
			gbo.vertex_begin(fe.end_point.x,fe.end_point.y,fe.end_point.z);
			gbo.register(fe.end_point.x,fe.end_point.y,fe.end_point.z,fe.end_extra_data);
			gbo.register(start_end_normal.x,start_end_normal.y,start_end_normal.z,"1");
			gbo.register(fe.end_point_material[0],fe.end_point_material[1],
					 fe.end_point_material[2],fe.end_point_material[3]);
			gbo.register(Integer.toString(body_id),Integer.toString(face_id),"2",Integer.toString(flag));
			gbo.register(Integer.toString(loop_id),Integer.toString(edge_id),curve_type_id,curve_type_id);
			
			if(gbo.test_end(max_file_data_length,false))
				gbocc.expand_creater_array(material_id);
		}
	}
	
	private void origin_create(int flag,part my_part,String my_file_name,String my_file_charset,
			long my_create_buffer_object_bitmap,int max_material_id,long max_file_data_length)
	{
		int material_id=caculate_material_id.caculate(
				my_part.driver,max_material_id,my_part,"origin",-1,-1,-1,-1,
				my_part.part_mesh.origin_material[0],my_part.part_mesh.origin_material[1],
				my_part.part_mesh.origin_material[2],my_part.part_mesh.origin_material[3]);
		
		graphics_buffer_object_creater gbo=gbocc.get_creater(material_id,
				my_file_name,my_file_charset,my_create_buffer_object_bitmap);
				
		gbo.vertex_begin();
		
		gbo.register("0","0","0",my_part.part_mesh.origin_vertex_extra_data);
		gbo.register("0","0","1","1");
		gbo.register(my_part.part_mesh.origin_material[0],	 my_part.part_mesh.origin_material[1],
					 my_part.part_mesh.origin_material[2],	 my_part.part_mesh.origin_material[3]);
		gbo.register(Integer.toString(my_part.part_mesh.body_number()),"0","0",Integer.toString(flag));
		gbo.register("0","0","0","0");
		
		if(gbo.test_end(max_file_data_length,false))
			gbocc.expand_creater_array(material_id);
	}
	
	public graphics_buffer_object_for_point(primitive_interface p_i,
			int max_material_id,part my_part,String my_file_name,String my_file_charset,
			long max_file_data_length,long my_create_buffer_object_bitmap)
	{
		int flag=2;
		gbocc=new graphics_buffer_object_creater_container();
		origin_create(flag,my_part,my_file_name,my_file_charset,
			my_create_buffer_object_bitmap,max_material_id,max_file_data_length);
		for(int body_id=0,body_number=my_part.part_mesh.body_number();body_id<body_number;body_id++)
			for(int face_id=0,face_number=my_part.part_mesh.body_array[body_id].face_number();face_id<face_number;face_id++) {
				int loop_number=my_part.part_mesh.body_array[body_id].face_array[face_id].fa_curve.face_loop_number();
				for(int loop_id=0;loop_id<loop_number;loop_id++) {
					int edge_number=my_part.part_mesh.body_array[body_id].face_array[face_id].fa_curve.f_loop[loop_id].edge_number();
					for(int edge_id=0;edge_id<edge_number;edge_id++) {
						face_edge fe=my_part.part_mesh.body_array[body_id].face_array[face_id].fa_curve.f_loop[loop_id].edge[edge_id];
						point start_end_normal=new point(0,0,1);
						String curve_type_id=Integer.toString(curve_type.curve_type_id(fe.curve_type));
						switch(fe.curve_type){
						case "line":
							start_end_normal=line_create(flag,
								body_id,face_id,loop_id,edge_id,fe,my_part,my_file_name,my_file_charset,
								curve_type_id,my_create_buffer_object_bitmap,max_material_id,max_file_data_length);
							break;
						case "circle":
							start_end_normal=circle_create(flag,
								body_id,face_id,loop_id,edge_id,fe,my_part,my_file_name,my_file_charset,
								curve_type_id,my_create_buffer_object_bitmap,max_material_id,max_file_data_length);
							break;
						case "ellipse":
						case "hyperbola":
						case "parabola":
							start_end_normal=ellipse_hyperbola_parabola_create(
								flag,body_id,face_id,loop_id,edge_id,fe,my_part,my_file_name,my_file_charset,
								curve_type_id,my_create_buffer_object_bitmap,max_material_id,max_file_data_length);
							break;
						case "pickup_point_set":
							pickup_point_set_create(flag,
								body_id,face_id,loop_id,edge_id,fe,my_part,my_file_name,my_file_charset,
								curve_type_id,my_create_buffer_object_bitmap,max_material_id,max_file_data_length);
							break;
						case "render_point_set":
							render_point_set_create(p_i,flag,
								body_id,face_id,loop_id,edge_id,fe,my_part,my_file_name,my_file_charset,
								curve_type_id,my_create_buffer_object_bitmap,max_material_id,max_file_data_length);
							break;
						case "segment":
							break;
						default:
							break;
						}
						start_end_create(flag,
							body_id,face_id,loop_id,edge_id,start_end_normal,fe,my_part,my_file_name,my_file_charset,
							curve_type_id,my_create_buffer_object_bitmap,max_material_id,max_file_data_length);
					}
				}
			}
	}
}
