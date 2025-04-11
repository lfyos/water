package kernel_part;

import java.util.ArrayList;

import kernel_file_manager.file_writer;

public class graphics_buffer_object_creater_container
{
	private ArrayList<ArrayList<graphics_buffer_object_creater>> creaters;
	
	public graphics_buffer_object_creater_container()
	{
		creaters=new ArrayList<ArrayList<graphics_buffer_object_creater>>();
	}
	public graphics_buffer_object_creater get_creater(
			int material_id,String my_file_name,String my_file_charset,
			long my_create_buffer_object_bitmap)
	{
		int material_number;
		ArrayList<graphics_buffer_object_creater> p_list;
		
		while((material_number=creaters.size())<=material_id) {
			p_list=new ArrayList<graphics_buffer_object_creater>();
			p_list.add(null);
			creaters.add(material_number,p_list);
		}
		p_list=creaters.get(material_id);
		int index_id=p_list.size()-1;
		graphics_buffer_object_creater p_creater=p_list.get(index_id);
		if(p_creater==null) {
			my_file_name+="_"+Integer.toString(material_id);
			my_file_name+="_"+Integer.toString(index_id);
			p_creater=new graphics_buffer_object_creater(
					my_file_name+".txt",my_file_charset,my_create_buffer_object_bitmap);
			p_list.set(index_id,p_creater);
		}
		return p_creater;
	}
	
	public void expand_creater_array(int material_id)
	{
		int material_number;
		ArrayList<graphics_buffer_object_creater> p_list;
		
		while((material_number=creaters.size())<=material_id) {
			p_list=new ArrayList<graphics_buffer_object_creater>();
			p_list.add(null);
			creaters.add(material_number,p_list);
		}
		p_list=creaters.get(material_id);
		if(p_list.get(p_list.size()-1)!=null)
			p_list.add(p_list.size(),null);
	}
	
	public long total_item_number;
	
	public int create_head_data(file_writer head_fw,
			mesh_file_collector file_collector,String file_type,String file_name)
	{
		ArrayList<graphics_buffer_object_creater> p_list;
		graphics_buffer_object_creater p_creater;
		
		for(int i=0,ni=creaters.size();i<ni;i++) 
			if((p_creater=(p_list=creaters.get(i)).get(p_list.size()-1))!=null)
				p_creater.test_end(0,true);
		
		head_fw.println("\t\t\t\t\"region_data\"\t:\t[");
		
		String creater_file_name;
		int file_number=0;
		long item_number;
		for(int i=0,ni=creaters.size(),file_id=0;i<ni;i++)
			for(int j=0,nj=(p_list=creaters.get(i)).size();j<nj;j++) {
				if((p_creater=p_list.get(j))==null)
					continue;
				if((item_number=p_creater.get_item_number())<=0)
					continue;
				if(file_id>0)
					head_fw.println(",");
				creater_file_name=p_creater.create_head_data(head_fw,i);
				String target_file_name=file_name+Integer.toString(file_id);
				file_writer.file_rename(creater_file_name,target_file_name+".txt");
				total_item_number+=item_number;
				file_collector.register(file_type,file_id++,target_file_name);
				file_number++;
			}
		head_fw.println().println("\t\t\t\t]");
		
		return file_number;
	}
}
