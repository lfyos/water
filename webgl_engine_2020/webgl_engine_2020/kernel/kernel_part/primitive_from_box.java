package kernel_part;

import kernel_transformation.point;
import kernel_common_class.const_value;
import kernel_transformation.plane;

public class primitive_from_box implements primitive_interface
{
	private body body_array[];
	
	public String[]get_primitive_material(int body_id,int face_id,int primitive_id)
	{
		return body_array[body_id].face_array[face_id].fa_face.default_material;
	}
	public int get_primitive_vertex_number(int body_id,int face_id,int primitive_id)
	{
		return 3;
	}
	public double[]get_primitive_vertex_location_data(int body_id,int face_id,int primitive_id,int vertex_id)
	{
		point p;
		face_loop fl=body_array[body_id].face_array[face_id].fa_curve.f_loop[primitive_id/2];
		switch(3*(primitive_id%2)+vertex_id) {
		default:
		case 0:
			p=fl.edge[0].start_point;
			break;
		case 1:
			p=fl.edge[0].end_point;
			break;
		case 2:
			p=fl.edge[1].end_point;
			break;
		case 3:
			p=fl.edge[2].start_point;
			break;
		case 4:
			p=fl.edge[2].end_point;	
			break;
		case 5:
			p=fl.edge[3].end_point;
			break;
		}
		return new double[] {p.x,p.y,p.z};
	}
	public String get_primitive_vertex_location_extra_data(int body_id,int face_id,int primitive_id,int vertex_id)
	{
		face_loop fl=body_array[body_id].face_array[face_id].fa_curve.f_loop[primitive_id/2];
		switch(3*(primitive_id%2)+vertex_id) {
		default:
		case 0:
			return fl.edge[0].start_extra_data;
		case 1:
			return fl.edge[0].end_extra_data;
		case 2:
			return fl.edge[1].end_extra_data;
		case 3:
			return fl.edge[2].start_extra_data;
		case 4:
			return fl.edge[2].end_extra_data;
		case 5:
			return fl.edge[3].end_extra_data;
		}
	}
	public double[]get_primitive_vertex_normal_data(int body_id,int face_id,int primitive_id,int vertex_id)
	{
		face_loop fl=body_array[body_id].face_array[face_id].fa_curve.f_loop[primitive_id/2];
		point p[]=new point[] {
				fl.edge[0].start_point,
				fl.edge[0].end_point,
				fl.edge[1].end_point,
				fl.edge[2].end_point,
				fl.edge[3].end_point
		};
		for(int i=1,ni=p.length-1;i<ni;i++)
			if((p[i].sub(p[i-1]).distance2()>const_value.min_value2))
				if((p[i].sub(p[i+1]).distance2()>const_value.min_value2)) {
					plane pl=new plane(p[i-1],p[i],p[i+1]);
					return new double[] {pl.A,pl.B,pl.C};
				}
		return new double[]{0,1,0};
	}
	public String get_primitive_vertex_normal_extra_data(int body_id,int face_id,int primitive_id,int vertex_id)
	{
		return get_primitive_vertex_location_extra_data(body_id,face_id,primitive_id,vertex_id);
	}
	public double[]get_primitive_vertex_attribute_data(int body_id,int face_id,int primitive_id,int vertex_id,int attribute_id)
	{
		return new double[] {0,0,0};
	}
	public String get_primitive_vertex_attribute_extra_data(int body_id,int face_id,int primitive_id,int vertex_id,int attribute_id)
	{
		return "1";
	}
	
	
	public double[]get_edge_location_data(int body_id,int face_id,int loop_id,int edge_id,int point_id)
	{
		face_edge fe=body_array[body_id].face_array[face_id].fa_curve.f_loop[loop_id].edge[edge_id];
		point p=((point_id%2)==0)?fe.start_point:fe.end_point;
		return new double[] {p.x,p.y,p.z};
	}
	public String get_edge_extra_data(int body_id,int face_id,int loop_id,int edge_id,int point_id)
	{
		face_edge fe=body_array[body_id].face_array[face_id].fa_curve.f_loop[loop_id].edge[edge_id];
		return ((point_id%2)==0)?fe.start_extra_data:fe.end_extra_data;
	}
	public String[] get_edge_material(int body_id,int face_id,int loop_id,int edge_id,int point_id)
	{
		face_edge fe=body_array[body_id].face_array[face_id].fa_curve.f_loop[loop_id].edge[edge_id];
		return ((point_id%2)==0)?fe.start_point_material:fe.end_point_material;
	}
	public double[]get_point_location_data(int body_id,int face_id,int loop_id,int edge_id,int point_id)
	{
		return new double[] {0,0,0,1};
	}
	public String get_point_extra_data(int body_id,int face_id,int loop_id,int edge_id,int point_id)
	{
		return "1";
	}
	public String[] get_point_material(int body_id,int face_id,int loop_id,int edge_id,int point_id)
	{
		return new String[] {"0","0","0","1"};
	}
	public void destroy(long my_max_compress_file_length,int my_response_block_size)
	{
		body_array=null;
	}
	public primitive_from_box(body my_body_array[])
	{
		body_array=my_body_array;
	}
}
