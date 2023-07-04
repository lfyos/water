package kernel_part;

import kernel_common_class.system_id;
import kernel_common_class.system_id_manager;
import kernel_file_manager.file_reader;
import kernel_transformation.box;
import kernel_transformation.location;
import kernel_transformation.point;

public class face_curve extends system_id
{
	public face_loop f_loop[];
	public box curve_box;
	public int total_edge_primitive_number,total_point_primitive_number;
	
	public void destroy()
	{
		for(int i=0,ni=face_loop_number();i<ni;i++)
			if(f_loop[i]!=null){
				f_loop[i].destroy();
				f_loop[i]=null;
			}
		f_loop=null;
		curve_box=null;
	}
	public int face_loop_number()
	{
		return (f_loop==null)?0:f_loop.length;
	}
	private void caculate_box_and_primitive_number()
	{
		curve_box=null;
		total_edge_primitive_number=0;
		total_point_primitive_number=0;
		for(int i=0,ni=face_loop_number();i<ni;i++)
			if(f_loop[i].loop_box!=null){
				if(curve_box==null)
					curve_box=new box(f_loop[i].loop_box);
				else
					curve_box=curve_box.add(f_loop[i].loop_box);
				total_edge_primitive_number+=f_loop[i].total_edge_primitive_number;
				total_point_primitive_number+=f_loop[i].total_point_primitive_number;
			}
	}
	public face_curve(location loca,box b,String my_extra_data,String my_material[],
			system_id_manager id_manager,int id_array[])
	{
		super(id_manager,id_array);
														//		 Y
		point p[]=new point[]								//		 |
		{													//       2***********************6
			new point(b.p[0].x,b.p[0].y,b.p[0].z),			//     * |                     * *
			new point(b.p[0].x,b.p[0].y,b.p[1].z),			//	 *   |                    *  *
			new point(b.p[0].x,b.p[1].y,b.p[0].z),			//	3************************7   *
			new point(b.p[0].x,b.p[1].y,b.p[1].z),			//	*    |                   *   *
			new point(b.p[1].x,b.p[0].y,b.p[0].z),			//	*    0-------------------*---4-------X
			new point(b.p[1].x,b.p[0].y,b.p[1].z),			//	*   /                    *  *
			new point(b.p[1].x,b.p[1].y,b.p[0].z),			//	* /                      * *  
			new point(b.p[1].x,b.p[1].y,b.p[1].z),			//	1************************5
		};												// Z

		for(int i=0,ni=p.length;i<ni;i++)
			p[i]=loca.multiply(p[i]);
		
		f_loop=new face_loop[] 
		{
			new face_loop(p[0],p[1],p[3],p[2],my_extra_data,my_material,id_manager,new int[] {5,id_array[1],id_array[2],0}),		//left face
			new face_loop(p[5],p[4],p[6],p[7],my_extra_data,my_material,id_manager,new int[] {5,id_array[1],id_array[2],1}),		//right face
			new face_loop(p[1],p[0],p[4],p[5],my_extra_data,my_material,id_manager,new int[] {5,id_array[1],id_array[2],2}),		//down
			new face_loop(p[2],p[3],p[7],p[6],my_extra_data,my_material,id_manager,new int[] {5,id_array[1],id_array[2],3}),		//up
			new face_loop(p[0],p[2],p[6],p[4],my_extra_data,my_material,id_manager,new int[] {5,id_array[1],id_array[2],4}),		//front
			new face_loop(p[1],p[5],p[7],p[3],my_extra_data,my_material,id_manager,new int[] {5,id_array[1],id_array[2],5})		//back
		};
		
		caculate_box_and_primitive_number();
	}
	
	public face_curve(file_reader fr,system_id_manager id_manager,int id_array[])
	{
		super(id_manager,id_array);
	
		int my_face_loop_number;
		if((my_face_loop_number=fr.get_int())<=0)
			f_loop=null;
		else{
			f_loop=new face_loop[my_face_loop_number];
			for(int i=0;i<my_face_loop_number;i++)
				f_loop[i]=new face_loop(fr,id_manager,new int[] {5,id_array[1],id_array[2],i});
		}
		caculate_box_and_primitive_number();
	}
	public face_curve(face_curve s,system_id_manager id_manager,int id_array[])
	{
		super(id_manager,id_array);
	
		if(s.face_loop_number()<=0)
			f_loop=null;
		else{
			f_loop=new face_loop[s.f_loop.length];
			for(int i=0,ni=f_loop.length;i<ni;i++)
				f_loop[i]=new face_loop(s.f_loop[i],id_manager,new int[] {5,id_array[1],id_array[2],i});
		}
		curve_box=s.curve_box;
		total_edge_primitive_number	=s.total_edge_primitive_number;
		total_point_primitive_number=s.total_point_primitive_number;
	}
};