package kernel_part;

public class graphics_buffer_object_for_edge
{
	private primitive_interface p_i;
	private int max_material_id;
	private part gbo_part;
	private String file_name,file_charset;
	private long max_file_data_length,create_buffer_object_bitmap;
	
	public graphics_buffer_object_creater_container gbocc;
	
	private double tessellation_location_0[],tessellation_location_1[];
	private String tessellation_extra_data_0,tessellation_extra_data_1;
	private String tessellation_material_0[],tessellation_material_1[];
	
	private void create_edge(face_loop fl,face_edge fe,int step,
			int body_id,int face_id,int loop_id,int edge_id,int vertex_id)
	{
		if((step==2)||(vertex_id==0)) {
			tessellation_location_0		=p_i.get_edge_location_data	(
					body_id,face_id,loop_id,edge_id,vertex_id+0);
			tessellation_extra_data_0	=p_i.get_edge_extra_data	(
					body_id,face_id,loop_id,edge_id,vertex_id+0);
			tessellation_material_0		=p_i.get_edge_material		(
					body_id,face_id,loop_id,edge_id,vertex_id+0);
		}else{
			tessellation_location_0		=tessellation_location_1;
			tessellation_extra_data_0	=tessellation_extra_data_1;
			tessellation_material_0		=tessellation_material_1;
		}
		tessellation_location_1			=p_i.get_edge_location_data	(
				body_id,face_id,loop_id,edge_id,vertex_id+1);
		tessellation_extra_data_1		=p_i.get_edge_extra_data	(
				body_id,face_id,loop_id,edge_id,vertex_id+1);
		tessellation_material_1			=p_i.get_edge_material		(
				body_id,face_id,loop_id,edge_id,vertex_id+1);
		
		int material_id=caculate_material_id.caculate(gbo_part.driver,max_material_id,
				gbo_part,"edge",body_id,face_id,loop_id,edge_id,tessellation_material_0);

		graphics_buffer_object_creater gbo=gbocc.get_creater(material_id,
				file_name,file_charset,create_buffer_object_bitmap);
			
		gbo.vertex_begin(tessellation_location_0[0],tessellation_location_0[1],tessellation_location_0[2]);
		
		gbo.register(tessellation_location_0[0], tessellation_location_0[1],
					 tessellation_location_0[2], tessellation_extra_data_0);
		gbo.register(tessellation_location_1[0]-tessellation_location_0[0],
					 tessellation_location_1[1]-tessellation_location_0[1],
					 tessellation_location_1[2]-tessellation_location_0[2],
					 tessellation_extra_data_0);
		gbo.register(tessellation_material_0[0], tessellation_material_0[1],
					 tessellation_material_0[2], tessellation_material_0[3]);
		
		gbo.register(body_id,face_id,vertex_id,"0");
		gbo.register(loop_id,edge_id,0,"0");

		gbo.vertex_begin(tessellation_location_1[0],
				tessellation_location_1[1],tessellation_location_1[2]);
		
		gbo.register(tessellation_location_1[0], tessellation_location_1[1],
					 tessellation_location_1[2], tessellation_extra_data_1);
		
		gbo.register(
					tessellation_location_1[0]-tessellation_location_0[0],
					tessellation_location_1[1]-tessellation_location_0[1],
					tessellation_location_1[2]-tessellation_location_0[2],
					tessellation_extra_data_1);
		
		gbo.register(tessellation_material_1[0], tessellation_material_1[1],
					 tessellation_material_1[2], tessellation_material_1[3]);
		
		gbo.register(body_id,face_id,vertex_id+1,"1");
		gbo.register(loop_id,edge_id,0,"0");
		
		if(gbo.test_end(max_file_data_length,false))
			gbocc.expand_creater_array(material_id);			
	}
	public graphics_buffer_object_for_edge(
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
		gbocc=new graphics_buffer_object_creater_container();
		
		for(int body_id=0,body_number=gbo_part.part_mesh.body_number();body_id<body_number;body_id++){
			body b=gbo_part.part_mesh.body_array[body_id];
			for(int face_id=0,face_number=b.face_number();face_id<face_number;face_id++){
				face fa=b.face_array[face_id];
				for(int loop_id=0,loop_number=fa.fa_curve.face_loop_number();loop_id<loop_number;loop_id++){
					face_loop fl=fa.fa_curve.f_loop[loop_id];
					for(int step,edge_id=0,edge_number=fl.edge_number();edge_id<edge_number;edge_id++) {
						face_edge fe=fl.edge[edge_id];
						
						//"line",	"circle",	"ellipse",	"hyperbola",	"parabola",
						//"pickup_point_set",	"render_point_set",			"segment",		"unknown"
						switch(fe.curve_type) {
						case "pickup_point_set":
						case "render_point_set":
							continue;
						case "segment":
							step=2;
							break;
						case "line":
						case "circle":
						case "ellipse":
						case "hyperbola":
						case "parabola":
						case "unknown":
						default:
							step=1;
							break;
						}

						tessellation_location_0		=null;
						tessellation_location_1		=null;
						tessellation_extra_data_0	=null;
						tessellation_extra_data_1	=null;
						tessellation_material_0		=null;
						tessellation_material_1		=null;

						int vertex_number=fe.total_edge_primitive_number-1;
						for(int vertex_id=0;vertex_id<vertex_number;vertex_id+=step)
							create_edge(fl,fe,step,body_id,face_id,loop_id,edge_id,vertex_id);
					}
				}
			}
		}
	}
}
