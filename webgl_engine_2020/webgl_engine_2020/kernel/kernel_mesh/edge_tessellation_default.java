package kernel_mesh;

import kernel_part.face_edge;
import kernel_part.part_rude;
import kernel_transformation.box;
import kernel_transformation.point;
import kernel_file_manager.file_reader;

public class edge_tessellation_default implements edge_tessellation
{
	private double tessellation_location[];
	private String tessellation_extra_data[],tessellation_material[][];
	
	public void destroy()
	{
		tessellation_location=null;
		tessellation_extra_data=null;
		tessellation_material=null;
	}
	public int tessellation_point_number()
	{
		if(tessellation_extra_data==null)
			return 0;
		else 
			return tessellation_extra_data.length;
	}
	public point get_tessellation_point(int index_id,auxiliary_file_handler f)
	{
		int num;
		if((num=tessellation_point_number())<=0)
			return null;
		else
			return new point(
				tessellation_location[3*(index_id%num)+0],
				tessellation_location[3*(index_id%num)+1],
				tessellation_location[3*(index_id%num)+2]);
	}
	public String get_tessellation_extra_data(int index_id,auxiliary_file_handler f)
	{
		int num;
		if((num=tessellation_point_number())<=0)
			return "1";
		else
			return tessellation_extra_data[index_id%num];
	}
	public String[] get_tessellation_material(int index_id,auxiliary_file_handler f)
	{
		int num;
		if((num=tessellation_point_number())<=0)
			return part_rude.default_material;
		else
			return tessellation_material[index_id%num];
	}
	private void caculate_edge(face_edge edge)
	{
		int point_number;
		if((point_number=tessellation_point_number())>0){
			edge.edge_box=new box(new point(tessellation_location[0],
					tessellation_location[1],tessellation_location[2]));
			for(int i=0;i<point_number;i++)
				edge.edge_box=edge.edge_box.add(new point(
						tessellation_location[3*i+0],
						tessellation_location[3*i+1],
						tessellation_location[3*i+2]));
			if(edge.start_effective_flag)
				edge.edge_box=edge.edge_box.add(edge.start_point);
			if(edge.end_effective_flag)
				edge.edge_box=edge.edge_box.add(edge.end_point);
		}else if(edge.start_effective_flag){
			edge.edge_box=new box(edge.start_point);
				if(edge.end_effective_flag)
					edge.edge_box=edge.edge_box.add(edge.end_point);
		}else if(edge.end_effective_flag)
			edge.edge_box=new box(edge.end_point);
		else
			edge.edge_box=null;
	}
	public edge_tessellation clone()
	{
		return new edge_tessellation_default(tessellation_location,
				tessellation_extra_data,tessellation_material);
	}
	private edge_tessellation_default(			double my_tessellation_location[],
			String my_tessellation_extra_data[],String my_tessellation_material[][])
	{
		tessellation_location	=my_tessellation_location;
		tessellation_extra_data	=my_tessellation_extra_data;
		tessellation_material	=my_tessellation_material;
	}
	public edge_tessellation_default(file_reader fr,face_edge edge,
			int my_tessellation_point_number,double scale_value)
	{
		tessellation_location	=new double[3*my_tessellation_point_number];
		tessellation_extra_data	=new String[  my_tessellation_point_number];
		tessellation_material	=new String[  my_tessellation_point_number][];
		
		for(int i=0,ni=tessellation_extra_data.length;i<ni;i++){
			tessellation_location	[3*i+0]=fr.get_double()*scale_value;
			tessellation_location	[3*i+1]=fr.get_double()*scale_value;
			tessellation_location	[3*i+2]=fr.get_double()*scale_value;
			tessellation_extra_data	[  i  ]=fr.get_string();
			
			tessellation_material	[  i  ]=new String[4];

			for(int j=0,nj=tessellation_material[i].length;j<nj;j++)
				tessellation_material[i][j]=fr.get_string();
		}
		caculate_edge(edge);
	}
	
	public edge_tessellation_default(face_edge edge,point start_point,point end_point)
	{
		tessellation_location=new double[]{
				start_point.x,	start_point.y,	start_point.z,
				end_point.x,	end_point.y,	end_point.z
		};
		tessellation_extra_data	=new String[]{"1","1"};
		tessellation_material	=new String[][]
		{
			part_rude.default_material,part_rude.default_material
		};
		caculate_edge(edge);
	}
}
