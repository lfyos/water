package kernel_part;

import kernel_mesh.auxiliary_file_handler;
import kernel_transformation.point;

public class graphics_buffer_object_for_edge
{
	public graphics_buffer_object_creater_container gbocc;
	public graphics_buffer_object_for_edge(
			auxiliary_file_handler f,int max_material_id,
			part my_part,String my_file_name,String my_file_charset,
			long max_file_data_length,long my_create_buffer_object_bitmap)
	{
		int flag=1;
		gbocc=new graphics_buffer_object_creater_container();
		
		for(int i=0,ni=my_part.part_mesh.body_number();i<ni;i++){
			body b=my_part.part_mesh.body_array[i];
			for(int j=0,nj=b.face_number();j<nj;j++){
				face fa=b.face_array[j];
				for(int k=0,nk=fa.fa_curve.face_loop_number();k<nk;k++){
					face_loop fl=fa.fa_curve.f_loop[k];
					for(int m=0,nm=fl.edge_number();m<nm;m++){
						face_edge fe=fl.edge[m];
						if((fe.edge==null)||(fe.curve_type.compareTo("point_set")==0))
							continue;
						int step=(fe.curve_type.compareTo("segment")==0)?2:1;
						String curve_type_id=Integer.toString(curve_type.curve_type_id(fe.curve_type));
						for(int n=0,nn=fe.edge.tessellation_point_number()-1;n<nn;n+=step){
							point tessellation_location_0	=fe.edge.get_tessellation_point		(n+0,f);
							String tessellation_extra_data_0=fe.edge.get_tessellation_extra_data(n+0,f);
							String tessellation_material_0[]=fe.edge.get_tessellation_material	(n+0,f);
								
							point tessellation_location_1	=fe.edge.get_tessellation_point		(n+1,f);
							String tessellation_extra_data_1=fe.edge.get_tessellation_extra_data(n+1,f);
							String tessellation_material_1[]=fe.edge.get_tessellation_material	(n+1,f);
							
							int material_id=caculate_material_id.caculate(
									my_part.driver,max_material_id,my_part,"edge",i,j,k,m,
									tessellation_material_0[0],tessellation_material_0[1],
									tessellation_material_0[2],tessellation_material_0[3]);
							graphics_buffer_object_creater gbo=gbocc.get_creater(material_id,
									my_file_name,my_file_charset,my_create_buffer_object_bitmap);
								
							gbo.vertex_begin(tessellation_location_0.x,
										 tessellation_location_0.y,	tessellation_location_0.z);
							
							gbo.register(tessellation_location_0.x,	tessellation_location_0.y,
									 tessellation_location_0.z,	tessellation_extra_data_0);
							gbo.register(tessellation_location_1.x-tessellation_location_0.x,
									 tessellation_location_1.y-tessellation_location_0.y,
									 tessellation_location_1.z-tessellation_location_0.z,
									 tessellation_extra_data_0);
							gbo.register(tessellation_material_0[0],tessellation_material_0[1],
									 tessellation_material_0[2],tessellation_material_0[3]);
							gbo.register(Integer.toString(i),Integer.toString(j),
									 Integer.toString(n),Integer.toString(flag));
							gbo.register(Integer.toString(k),Integer.toString(m),"0",curve_type_id);
								
							gbo.vertex_begin(tessellation_location_1.x,
										 tessellation_location_1.y,	tessellation_location_1.z);
							
							gbo.register(tessellation_location_1.x,	tessellation_location_1.y,
									 tessellation_location_1.z,	tessellation_extra_data_1);
							gbo.register(tessellation_location_1.x-tessellation_location_0.x,
									 tessellation_location_1.y-tessellation_location_0.y,
									 tessellation_location_1.z-tessellation_location_0.z,
									 tessellation_extra_data_1);
							gbo.register(tessellation_material_1[0],tessellation_material_1[1],
									 tessellation_material_1[2],tessellation_material_1[3]);
							gbo.register(Integer.toString(i),  Integer.toString(j),
									 Integer.toString(n+1),Integer.toString(flag));
							gbo.register(Integer.toString(k),Integer.toString(m),"1",curve_type_id);

							if(gbo.test_end(max_file_data_length,false))
								gbocc.expand_creater_array(material_id);
						}
					}	
				}
			}
		}
	}
}
