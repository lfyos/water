package kernel_part;

public class primitive_access implements primitive_interface
{
	private part_rude pr;
	private primitive_interface pi;
	
	private int face_body_id,face_face_id,face_primitive_id;
	private String material[];
	private double vertex_location_data[][],		vertex_normal_data[][],			vertex_attribute_data[][][];
	private String vertex_location_extra_data[],	vertex_normal_extra_data[],		vertex_attribute_extra_data[][];
	
	private void face_advance(int advance_body_id,int advance_face_id,int advance_primitive_id)
	{
		if(vertex_location_data!=null){
			if((advance_body_id==face_body_id)&&(advance_face_id==face_face_id)&&(advance_primitive_id==face_primitive_id))
				return;
			face_primitive_id++;
		}
		for(int body_number=pr.body_number();face_body_id<body_number;face_body_id++,face_face_id=0){
			body b=pr.body_array[face_body_id];
			for(int face_number=b.face_number();face_face_id<face_number;face_face_id++,face_primitive_id=0){
				face_face ff=b.face_array[face_face_id].fa_face;
				int face_attribute_number=ff.attribute_number,face_primitive_number=ff.total_face_primitive_number;
				for(;face_primitive_id<face_primitive_number;face_primitive_id++){
					material=pi.get_primitive_material(face_body_id,face_face_id,face_primitive_id);
					int primitive_vertex_number	=pi.get_primitive_vertex_number(face_body_id,face_face_id,face_primitive_id);
					vertex_location_data		=new double[primitive_vertex_number][];
					vertex_location_extra_data	=new String[primitive_vertex_number];

					vertex_normal_data			=new double[primitive_vertex_number][];
					vertex_normal_extra_data	=new String[primitive_vertex_number];
					
					vertex_attribute_data		=new double[primitive_vertex_number][][];
					vertex_attribute_extra_data	=new String[primitive_vertex_number][];
					
					for(int primitive_vertex_id=0;primitive_vertex_id<primitive_vertex_number;primitive_vertex_id++){
						vertex_location_data			[primitive_vertex_id]
								=pi.get_primitive_vertex_location_data		(face_body_id,face_face_id,face_primitive_id,primitive_vertex_id);
						vertex_location_extra_data		[primitive_vertex_id]
								=pi.get_primitive_vertex_location_extra_data(face_body_id,face_face_id,face_primitive_id,primitive_vertex_id);
						vertex_normal_data				[primitive_vertex_id]
								=pi.get_primitive_vertex_normal_data		(face_body_id,face_face_id,face_primitive_id,primitive_vertex_id);
						vertex_normal_extra_data		[primitive_vertex_id]
								=pi.get_primitive_vertex_normal_extra_data	(face_body_id,face_face_id,face_primitive_id,primitive_vertex_id);
						
						vertex_attribute_data			[primitive_vertex_id]=new double[face_attribute_number][];
						vertex_attribute_extra_data		[primitive_vertex_id]=new String[face_attribute_number];
						
						for(int face_attribute_id=0;face_attribute_id<face_attribute_number;face_attribute_id++){
							vertex_attribute_data		[primitive_vertex_id][face_attribute_id]	
								=pi.get_primitive_vertex_attribute_data		(face_body_id,face_face_id,face_primitive_id,primitive_vertex_id,face_attribute_id);
							vertex_attribute_extra_data	[primitive_vertex_id][face_attribute_id]
								=pi.get_primitive_vertex_attribute_extra_data(face_body_id,face_face_id,face_primitive_id,primitive_vertex_id,face_attribute_id);
						}
					}
					if((advance_body_id==face_body_id)&&(advance_face_id==face_face_id)&&(advance_primitive_id==face_primitive_id))
						return;
				}
			}
		}
	}
	
	public String[]get_primitive_material(int body_id,int face_id,int primitive_id)
	{
		face_advance(body_id,face_id,primitive_id);
		return material;
	};
	public int get_primitive_vertex_number(int body_id,int face_id,int primitive_id)
	{
		face_advance(body_id,face_id,primitive_id);
		return vertex_location_data.length;
	};
	
	public double[]get_primitive_vertex_location_data(int body_id,int face_id,int primitive_id,int vertex_id)
	{
		face_advance(body_id,face_id,primitive_id);
		return vertex_location_data[vertex_id];
	};
	public String get_primitive_vertex_location_extra_data(int body_id,int face_id,int primitive_id,int vertex_id)
	{
		face_advance(body_id,face_id,primitive_id);
		return vertex_location_extra_data[vertex_id];
	};
	
	public double[]get_primitive_vertex_normal_data(int body_id,int face_id,int primitive_id,int vertex_id)
	{
		face_advance(body_id,face_id,primitive_id);
		return vertex_normal_data[vertex_id];
	};
	public String get_primitive_vertex_normal_extra_data(int body_id,int face_id,int primitive_id,int vertex_id)
	{
		face_advance(body_id,face_id,primitive_id);
		return vertex_normal_extra_data[vertex_id];
	}
	
	public double[]get_primitive_vertex_attribute_data(int body_id,int face_id,int primitive_id,int vertex_id,int attribute_id)
	{
		face_advance(body_id,face_id,primitive_id);
		return vertex_attribute_data[vertex_id][attribute_id];
	};
	public String get_primitive_vertex_attribute_extra_data(int body_id,int face_id,int primitive_id,int vertex_id,int attribute_id)
	{
		face_advance(body_id,face_id,primitive_id);
		return vertex_attribute_extra_data[vertex_id][attribute_id];
	};

	private int edge_body_id,edge_face_id,edge_loop_id,edge_edge_id,edge_point_id;
	private double edge_location_data[];
	private String edge_location_extra_data,edge_material[];
	
	private void edge_advance(int advance_body_id,int advance_face_id,int advance_loop_id,int advance_edge_id,int advance_point_id)
	{
		if(edge_location_data!=null) {
			if((advance_body_id==edge_body_id)&&(advance_face_id==edge_face_id))
				if((advance_loop_id==edge_loop_id)&&(advance_edge_id==edge_edge_id))
					if(advance_point_id==edge_point_id)
						return;
			edge_point_id++;
		}
		for(int body_number=pr.body_number();edge_body_id<body_number;edge_body_id++,edge_face_id=0){
			body b=pr.body_array[edge_body_id];
			for(int face_number=b.face_number();edge_face_id<face_number;edge_face_id++,edge_loop_id=0){
				face fa=b.face_array[edge_face_id];
				for(int loop_number=fa.fa_curve.face_loop_number();edge_loop_id<loop_number;edge_loop_id++,edge_edge_id=0){
					face_loop fl=fa.fa_curve.f_loop[edge_loop_id];
					for(int edge_number=fl.edge_number();edge_edge_id<edge_number;edge_edge_id++,edge_point_id=0) {
						face_edge fe=fl.edge[edge_edge_id];
						for(int edge_point_number=fe.total_edge_primitive_number;edge_point_id<edge_point_number;edge_point_id++){
							edge_location_data		=pi.get_edge_location_data(edge_body_id, edge_face_id, edge_loop_id, edge_edge_id, edge_point_id);
							edge_location_extra_data=pi.get_edge_extra_data(edge_body_id, edge_face_id, edge_loop_id, edge_edge_id, edge_point_id);
							edge_material			=pi.get_edge_material(edge_body_id, edge_face_id, edge_loop_id, edge_edge_id, edge_point_id);
							if((advance_body_id==edge_body_id)&&(advance_face_id==edge_face_id))
								if((advance_loop_id==edge_loop_id)&&(advance_edge_id==edge_edge_id))
									if(advance_point_id==edge_point_id)
										return;
						}
					}
				}
			}
		}
	}
	public double[]get_edge_location_data(int body_id,int face_id,int loop_id,int edge_id,int point_id)
	{
		edge_advance(body_id,face_id,loop_id,edge_id,point_id);
		return edge_location_data;
	};
	public String get_edge_extra_data(int body_id,int face_id,int loop_id,int edge_id,int point_id)
	{
		edge_advance(body_id,face_id,loop_id,edge_id,point_id);
		return edge_location_extra_data;
	}
	public String[] get_edge_material(int body_id,int face_id,int loop_id,int edge_id,int point_id)
	{
		edge_advance(body_id,face_id,loop_id,edge_id,point_id);
		return edge_material;
	};

	
	private int point_body_id,point_face_id,point_loop_id,point_edge_id,point_point_id;
	private double point_location_data[];
	private String point_location_extra_data,point_material[];
	
	private void point_advance(int advance_body_id,int advance_face_id,int advance_loop_id,int advance_edge_id,int advance_point_id)
	{
		if(point_location_data!=null) {
			if((advance_body_id==point_body_id)&&(advance_face_id==point_face_id))
				if((advance_loop_id==point_loop_id)&&(advance_edge_id==point_edge_id))
					if(advance_point_id==point_point_id)
						return;
			point_point_id++;
		}
		for(int body_number=pr.body_number();point_body_id<body_number;point_body_id++,point_face_id=0){
			body b=pr.body_array[point_body_id];
			for(int face_number=b.face_number();point_face_id<face_number;point_face_id++,point_loop_id=0){
				face fa=b.face_array[point_face_id];
				for(int loop_number=fa.fa_curve.face_loop_number();point_loop_id<loop_number;point_loop_id++,point_edge_id=0){
					face_loop fl=fa.fa_curve.f_loop[point_loop_id];
					for(int edge_number=fl.edge_number();point_edge_id<edge_number;point_edge_id++,point_point_id=0) {
						face_edge fe=fl.edge[point_edge_id];
						for(int point_point_number=fe.total_point_primitive_number;point_point_id<point_point_number;point_point_id++){
							point_location_data			=pi.get_point_location_data	(point_body_id, point_face_id, point_loop_id, point_edge_id, point_point_id);
							point_location_extra_data	=pi.get_point_extra_data	(point_body_id, point_face_id, point_loop_id, point_edge_id, point_point_id);
							point_material				=pi.get_point_material		(point_body_id, point_face_id, point_loop_id, point_edge_id, point_point_id);
							
							if((advance_body_id==point_body_id)&&(advance_face_id==point_face_id))
								if((advance_loop_id==point_loop_id)&&(advance_point_id==point_edge_id))
									if(advance_point_id==point_point_id)
										return;
						}
					}
				}
			}
		}
	}
	public double[]get_point_location_data(int body_id,int face_id,int loop_id,int edge_id,int point_id)
	{
		point_advance(body_id,face_id,loop_id,edge_id,point_id);
		return point_location_data;
	};
	public String get_point_extra_data(int body_id,int face_id,int loop_id,int edge_id,int point_id)
	{
		point_advance(body_id,face_id,loop_id,edge_id,point_id);
		return point_location_extra_data;
	}
	public String[] get_point_material(int body_id,int face_id,int loop_id,int edge_id,int point_id)
	{
		point_advance(body_id,face_id,loop_id,edge_id,point_id);
		return point_material;
	};

	public void destroy(long my_max_compress_file_length,int my_response_block_size)
	{
		clear();
		
		if(pi!=null) {
			pi.destroy(my_max_compress_file_length, my_response_block_size);
			pi=null;
		}
		
		pr=null;
	}
	private void clear()
	{
		face_body_id=0;
		face_face_id=0;
		face_primitive_id=0;
		material=null;
		vertex_location_data=null;
		vertex_normal_data=null;
		vertex_attribute_data=null;
		vertex_location_extra_data=null;
		vertex_normal_extra_data=null;
		vertex_attribute_extra_data=null;
		
		edge_body_id=0;
		edge_face_id=0;
		edge_loop_id=0;
		edge_edge_id=0;
		edge_point_id=0;
		
		edge_location_data=null;
		edge_location_extra_data=null;
		edge_material=null;
		
		
		point_body_id=0;
		point_face_id=0;
		point_loop_id=0;
		point_edge_id=0;
		point_point_id=0;
		
		point_location_data=null;
		point_location_extra_data=null;
		point_material=null;	
	}
	
	public primitive_access(part_rude my_pr,primitive_interface my_pi)
	{
		pr=my_pr;
		pi=my_pi;
		
		clear();
	}
}
