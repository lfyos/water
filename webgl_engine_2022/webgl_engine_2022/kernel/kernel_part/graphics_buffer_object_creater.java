package kernel_part;

import java.io.File;
import kernel_file_manager.file_writer;
import kernel_transformation.box;
import kernel_transformation.point;

public class graphics_buffer_object_creater 
{
	private long create_buffer_object_id,create_buffer_object_bitmap;

	private long item_number;
	private box region_box;
	
	private file_writer text_fw;

	public graphics_buffer_object_creater(
			String my_file_name,String my_file_charset,
			long my_create_buffer_object_bitmap)
	{
		create_buffer_object_id=1;
		create_buffer_object_bitmap=my_create_buffer_object_bitmap;

		item_number=0;
		region_box=null;
		
		text_fw=new file_writer(my_file_name,my_file_charset);
	}
	private void one_register(String str)
	{
		if((create_buffer_object_id&create_buffer_object_bitmap)!=0)
			text_fw.println(str.replace(',','\t'));
		create_buffer_object_id+=create_buffer_object_id;
	}
	public void register(String x,String y,String z,String w)
	{
		one_register(x);	one_register(y);	one_register(z);	one_register(w);
	}
	public void register(double x,double y,double z,String extra_data)
	{
		register(Double.toString(x),Double.toString(y),Double.toString(z),extra_data);
	}
	public void register(double data[],String extra_data)
	{
		register(data[0],data[1],data[2],extra_data);
	}
	public void vertex_begin()
	{
		create_buffer_object_id=1;
		item_number++;
	}
	public void vertex_begin(double x,double y,double z)
	{
		vertex_begin();
		if(region_box==null)
			region_box=new box(new point(x,y,z));
		else
			region_box=region_box.add(new box(new point(x,y,z)));
	}
	public boolean test_end(long max_file_data_length,boolean close_flag)
	{
		if(!close_flag) {
			if(max_file_data_length<=0)
				return false;
			if(text_fw.output_data_length<max_file_data_length)
				return false;
		}
		text_fw.close();
		return true;
	}
	public String create_head_data(
				file_writer head_fw,String material_id,String follow_str)
	{
		if(text_fw.output_data_length<=0){
			(new File(text_fw.directory_name+text_fw.file_name)).delete();
			return null;
		}

		head_fw.println("\t\t\t\t\t\t{");
		head_fw.println("\t\t\t\t\t\t\t\"material_id\"	:	",material_id+",");
		
		head_fw.print  ("\t\t\t\t\t\t\t");
		if(region_box==null)
			head_fw.println("\"region_box\"	:	[[0,0,0,1],[0,0,0,1]],");
		else{
			head_fw.print  ("\"region_box\"	:	[[",region_box.p[0].x);
			head_fw.print  (",",					region_box.p[0].y);
			head_fw.print  (",",					region_box.p[0].z);
			head_fw.print  (",1.0],[",				region_box.p[1].x);
			head_fw.print  (",",					region_box.p[1].y);
			head_fw.print  (",",					region_box.p[1].z);
			head_fw.println(",1.0]],");
		}

		head_fw.println("\t\t\t\t\t\t\t\"item_number\"	:	",item_number);
		
		head_fw.println("\t\t\t\t\t\t}",follow_str);
		
		return text_fw.directory_name+text_fw.file_name;
	}
	
	public long get_item_number()
	{
		return item_number;
	}
}