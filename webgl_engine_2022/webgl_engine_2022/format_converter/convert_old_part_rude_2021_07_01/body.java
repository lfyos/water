package convert_old_part_rude_2021_07_01;

import kernel_file_manager.file_reader;
import kernel_transformation.box;
import kernel_transformation.location;

public class body
{
	public void destroy()
	{
		for(int i=0,ni=face_number();i<ni;i++)
			if(face_array[i]!=null){
				face_array[i].destroy();
				face_array[i]=null;
			}
		face_array=null;
		name=null;
		body_box=null;
	}
	public body(body s)
	{
		name=new String(s.name);
		face_array=null;
		if(s.face_number()>0){
			face_array=new face[s.face_array.length];
			for(int i=0,ni=face_array.length;i<ni;i++)
				face_array[i]=new face(s.face_array[i]);
		}
		body_box=null;
		if(s.body_box!=null)
			body_box=new box(s.body_box);
	}
	public String name;	
	
	public face face_array[];
	public int face_number()
	{
		if(face_array==null)
			return 0;
		else
			return face_array.length;
	}
	public box body_box;

	private void caculate_box()
	{
		body_box=null;
		for(int i=0,ni=face_number();i<ni;i++){
			if(face_array[i].face_box!=null){
				if(body_box==null)
					body_box=new box(face_array[i].face_box);
				else
					body_box=body_box.add(face_array[i].face_box);
			}
		}
	}
		
	public body(file_reader fr,double vertex_scale_value,double normal_scale_value,
			boolean delete_redundant_data_flag,boolean combine_flag,boolean create_normal_flag)
	
	{
		name=fr.get_string();
		name=(name==null)?"":name;
		
		int my_face_number=fr.get_int();
		
		if(my_face_number<=0){
			my_face_number=0;
			face_array=null;
		}else{
			face_array=new face[my_face_number];
			for(int i=0;i<my_face_number;i++)
				face_array[i]=new face(fr,vertex_scale_value,normal_scale_value,
						delete_redundant_data_flag,combine_flag,create_normal_flag);
		}
		caculate_box();
	}
	public body(int box_number,location loca[],box b[],String material[][])
	{
		name="no_name";	
		face_array=new face[box_number];
		for(int i=0;i<box_number;i++)
			face_array[i]=new face(loca[i],b[i],material[i]);
		caculate_box();
	}
	public void free_memory()
	{
		for(int face_id=0,my_face_number=face_number();face_id<my_face_number;face_id++)
			face_array[face_id].free_memory();
	}
}
