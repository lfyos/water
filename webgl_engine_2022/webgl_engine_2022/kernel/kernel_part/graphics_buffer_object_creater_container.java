package kernel_part;

import kernel_file_manager.file_writer;

public class graphics_buffer_object_creater_container
{
	private graphics_buffer_object_creater creaters[][];
	public graphics_buffer_object_creater_container()
	{
		creaters=new graphics_buffer_object_creater[0][];
	}
	public graphics_buffer_object_creater get_creater(
			int material_id,String my_file_name,String my_file_charset,
			long my_create_buffer_object_bitmap)
	{
		int material_number=creaters.length;
		if(material_id>=material_number){
			graphics_buffer_object_creater bak[][]=creaters;
			creaters=new graphics_buffer_object_creater[material_id+1][];
			for(int i=0,ni=bak.length;i<ni;i++)
				creaters[i]=bak[i];
			for(int i=bak.length,ni=creaters.length;i<ni;i++)
				creaters[i]=new graphics_buffer_object_creater[]{null};
		}
		int index_id=creaters[material_id].length-1;
		if(creaters[material_id][index_id]==null){
			my_file_name+="_"+Integer.toString(material_id);
			my_file_name+="_"+Integer.toString(index_id);
			creaters[material_id][index_id]=new graphics_buffer_object_creater(
					my_file_name+".txt",my_file_charset,my_create_buffer_object_bitmap);
		}
		return creaters[material_id][index_id];
	}
	
	public void expand_creater_array(int material_id)
	{
		graphics_buffer_object_creater bak[]=creaters[material_id];
		if(bak.length>0)
			if(bak[bak.length-1]==null)
				return;
		creaters[material_id]=new graphics_buffer_object_creater[bak.length+1];
		for(int i=0,ni=bak.length;i<ni;i++)
			creaters[material_id][i]=bak[i];
		creaters[material_id][bak.length]=null;
	}
	
	public int file_number;
	public long total_item_number;
	public void create_head_data(file_writer head_fw,
			mesh_file_collector file_collector,String file_type,String file_name)
	{
		file_number=0;
		for(int i=0,ni=creaters.length,index_id;i<ni;i++)
			if((index_id=creaters[i].length-1)>=0){
				file_number+=creaters[i].length;
				if(creaters[i][index_id]==null)
					file_number--; 
				else
					creaters[i][index_id].test_end(0,true);
			}
		
		head_fw.println("\t\t\t\t\"region_data\"\t:\t[");
		
		String creater_file_name;
		
		for(int i=0,ni=creaters.length,file_id=0;i<ni;i++)
			for(int j=0,nj=creaters[i].length;j<nj;j++)
				if(creaters[i][j]!=null)
					if((creater_file_name=creaters[i][j].create_head_data(
						head_fw,Integer.toString(i),(file_id<(file_number-1))?",":""))!=null)
					{
						String target_file_name=file_name+Integer.toString(file_id);
						file_writer.file_rename(creater_file_name,target_file_name+".txt");
						total_item_number+=creaters[i][j].get_item_number();
						file_collector.register(file_type,file_id++,target_file_name);
					}
		head_fw.println("\t\t\t\t]");
	}
}
