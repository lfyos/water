package kernel_mesh;

import kernel_file_manager.file_reader;
import kernel_transformation.box;
import kernel_transformation.location;
import kernel_transformation.point;

public class face_mesh_default implements face_mesh
{
	private face_mesh_default_point_set 	vertices,normals;
	private face_mesh_default_point_set 	attributes[];
	private face_mesh_default_primitive_set primitives;
	
	public int get_vertex_number(auxiliary_file_handler f)
	{
		return vertices.point_number();
	}
	public double[] get_vertex(int index_id,auxiliary_file_handler f)
	{
		return vertices.get_vertex_location(index_id);
	}
	public String get_vertex_extra_data(int index_id,auxiliary_file_handler f)
	{
		return vertices.get_vertex_extra_data(index_id);
	}
	public int get_normal_number(auxiliary_file_handler f)
	{
		return normals.point_number();
	}
	public double[] get_normal(int index_id,auxiliary_file_handler f)
	{
		return normals.get_vertex_location(index_id);
	}
	public String get_normal_extra_data(int index_id,auxiliary_file_handler f)
	{
		return normals.get_vertex_extra_data(index_id);
	}
	public int get_attribute_number(int attribute_id,auxiliary_file_handler f)
	{
		if(attributes!=null)
			if(attribute_id>=0)
				if(attribute_id<attributes.length)
					return attributes[attribute_id].point_number();
		return 0;
	}
	public double[] get_attribute(int index_id,int attribute_id,auxiliary_file_handler f)
	{
		if(attributes!=null)
			if(attribute_id>=0)
				if(attribute_id<attributes.length)
					return attributes[attribute_id].get_vertex_location(index_id);
		return new double[] {0,0,0};
	}
	public String get_attribute_extra_data(int index_id,int attribute_id,auxiliary_file_handler f)
	{
		if(attributes!=null)
			if(attribute_id>=0)
				if(attribute_id<attributes.length)
					return attributes[attribute_id].get_vertex_extra_data(index_id);
		return "1.0";
	}
	public int get_attribute_number()
	{
		if(attributes!=null)
			return attributes.length;
		else
			return 0;
	};
	public int get_primitive_number()
	{
		return (primitives.vertex==null)?0:(primitives.vertex.length);
	}
	public int get_primitive_vertex_number(int primitive_id,auxiliary_file_handler f)
	{
		if(primitives.vertex!=null)
			if(primitive_id>=0)
				if(primitive_id<primitives.vertex.length)
					return primitives.vertex[primitive_id].length;
		return 0;
	}
	public String[] get_primitive_material(int primitive_id,auxiliary_file_handler f)
	{
		return primitives.material[primitive_id];
	}
	public int get_primitive_vertex_index(int primitive_id,int index_id,auxiliary_file_handler f)
	{
		return primitives.vertex[primitive_id][index_id];
	}
	public int get_primitive_normal_index(int primitive_id,int index_id,auxiliary_file_handler f)
	{
		return primitives.normal[primitive_id][index_id];
	}
	public int get_primitive_attribute_index(int primitive_id,int index_id,int attribute_id,auxiliary_file_handler f)
	{
		if(attributes!=null)
			if(attribute_id>=0)
				if(attribute_id<attributes.length)
					return primitives.attribute[attribute_id][primitive_id][index_id];
		return 0;
	}
	public void destroy()
	{
		if(vertices!=null){
			vertices.destroy();
			vertices=null;
		}
		if(normals!=null){
			normals.destroy();
			normals=null;
		}
		if(attributes!=null){
			for(int i=0,ni=attributes.length;i<ni;i++)
				if(attributes[i]!=null){
					attributes[i].destroy();
					attributes[i]=null;
				}
			attributes=null;
		}
		if(primitives!=null){
			primitives.destroy();
			primitives=null;
		}
	}
	public face_mesh clone()
	{
		face_mesh_default ret_val=new face_mesh_default();
		
		if(vertices!=null)
			ret_val.vertices=new face_mesh_default_point_set(vertices);
		if(normals!=null)
			ret_val.normals=new face_mesh_default_point_set(normals);
		if(attributes!=null)
			if(attributes.length>0){
				ret_val.attributes=new face_mesh_default_point_set[attributes.length];
				for(int i=0,ni=attributes.length;i<ni;i++)
					ret_val.attributes[i]=new face_mesh_default_point_set(attributes[i]);
			}
		if(primitives!=null)
			ret_val.primitives=new face_mesh_default_primitive_set(primitives);
		
		return ret_val;
	}
	public int add_attribute(double x,double y,double z,String w)
	{
		if(attributes==null){
			attributes=new face_mesh_default_point_set[] {new face_mesh_default_point_set(x,y,z,w)};
		}else {
			face_mesh_default_point_set bak[]=attributes;
			attributes=new face_mesh_default_point_set[bak.length+1];
			for(int i=0,ni=bak.length;i<ni;i++)
				attributes[i]=bak[i];
			attributes[attributes.length-1]=new face_mesh_default_point_set(x,y,z,w);
		}
		primitives.add_attribute();
		return attributes.length;
	}
	private void delete_redundant_data(boolean combine_flag)
	{
		face_mesh_default_point_set bak_vertices	=vertices;
		face_mesh_default_point_set bak_normals		=normals;
		face_mesh_default_point_set bak_attributes[]=attributes;
		
		vertices=new face_mesh_default_point_set();
		normals=new face_mesh_default_point_set();
		attributes=new face_mesh_default_point_set[bak_attributes.length];
		for(int i=0,ni=attributes.length;i<ni;i++)
			attributes[i]=new face_mesh_default_point_set();
		
		for(int i=0,ni=primitives.vertex.length;i<ni;i++)
			for(int j=0,nj=primitives.vertex[i].length;j<nj;j++){
				double touch_location_data[]=bak_vertices.get_vertex_location  (primitives.vertex[i][j]);
				String touch_extra_data		=bak_vertices.get_vertex_extra_data(primitives.vertex[i][j]);
				primitives.vertex[i][j]=vertices.touch_point(
						touch_location_data[0],touch_location_data[1],touch_location_data[2],
						touch_extra_data,combine_flag);
		}
		for(int i=0,ni=primitives.normal.length;i<ni;i++)
			for(int j=0,nj=primitives.normal[i].length;j<nj;j++){
			double touch_location_data[]=bak_normals.get_vertex_location  (primitives.normal[i][j]);
			String touch_extra_data		=bak_normals.get_vertex_extra_data(primitives.normal[i][j]);
			primitives.normal[i][j]=normals.touch_point(
					touch_location_data[0],touch_location_data[1],touch_location_data[2],
					touch_extra_data,combine_flag);
		}
		for(int i=0,ni=primitives.attribute.length;i<ni;i++)
			for(int j=0,nj=primitives.attribute[i].length;j<nj;j++)
				for(int k=0,nk=primitives.attribute[i][j].length;k<nk;k++){
				double touch_location_data[]=bak_attributes[i].get_vertex_location  (primitives.attribute[i][j][k]);
				String touch_extra_data		=bak_attributes[i].get_vertex_extra_data(primitives.attribute[i][j][k]);
				primitives.attribute[i][j][k]=attributes[i].touch_point(
						touch_location_data[0],touch_location_data[1],touch_location_data[2],
						touch_extra_data,combine_flag);
			}
	}
	public box get_face_box()
	{
		int id=0,total_primitive_number;
		if((total_primitive_number=primitives.vertex.length)>0){
			double min_x=0,min_y=0,min_z=0,max_x=0,max_y=0,max_z=0;
			for(int i=0,ni=total_primitive_number;i<ni;i++)
				for(int j=0,nj=primitives.vertex[i].length;j<nj;j++){
					double p[]=vertices.get_vertex_location(primitives.vertex[i][j]);
					min_x=((id==0)||(p[0]<min_x))?p[0]:min_x;
					min_y=((id==0)||(p[1]<min_y))?p[1]:min_y;
					min_z=((id==0)||(p[2]<min_z))?p[2]:min_z;
					max_x=((id==0)||(p[0]>max_x))?p[0]:max_x;
					max_y=((id==0)||(p[1]>max_y))?p[1]:max_y;
					max_z=((id==0)||(p[2]>max_z))?p[2]:max_z;
					id++;
				}
			if(id>0)
				return new box(new point(min_x,min_y,min_z),new point(max_x,max_y,max_z));
		}
		return null;
	};
	public String[] get_box_material()
	{
		if(primitives.vertex.length>0)
			return primitives.material[0];
		else
			return new String[]{"0","0","0","0"};
	};
	public face_mesh_default(file_reader f,int attribute_number,
		double vertex_scale_value,double normal_scale_value,
		boolean delete_redundant_data_flag,boolean combine_flag,boolean create_normal_flag)
	{
		(vertices=new face_mesh_default_point_set(f)).scale(vertex_scale_value);
		(normals=new face_mesh_default_point_set(f)).scale(normal_scale_value);
		
		if(attribute_number<=0){
			attribute_number=0;
			attributes=null;
		}else{
			attributes=new face_mesh_default_point_set[attribute_number];
			for(int i=0;i<attribute_number;i++)
				attributes[i]=new face_mesh_default_point_set(f);
		}		
		primitives=new face_mesh_default_primitive_set(f,attribute_number);
		
		if(delete_redundant_data_flag) 
			delete_redundant_data(combine_flag);
			
		if(create_normal_flag) {
			normals=vertices.creator_normal(primitives.vertex);
			normals.scale(normal_scale_value);
			primitives.normal=new int[primitives.vertex.length][];
			for(int i=0,ni=primitives.normal.length;i<ni;i++){
				primitives.normal[i]=new int[primitives.vertex[i].length];
				for(int j=0,nj=primitives.normal[i].length;j<nj;j++)
					primitives.normal[i][j]=primitives.vertex[i][j];
			}
		}
	}
	
	public face_mesh_default(location loca,box b,String material[])
	{
		vertices=new face_mesh_default_point_set(loca,b,true);
		normals=new face_mesh_default_point_set(loca,b,false);
		attributes=new face_mesh_default_point_set[]
			{
				new face_mesh_default_point_set(
						new location(),
						new box(
								new point(0,0,0),
								new point(1,1,1)),
						true)
			};
		primitives=new face_mesh_default_primitive_set(material);
	}
	private face_mesh_default()
	{
		vertices=null;
		normals=null;
		attributes=null;
		primitives=null;
	}
}