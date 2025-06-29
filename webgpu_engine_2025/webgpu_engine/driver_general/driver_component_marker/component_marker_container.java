package driver_component_marker;

import java.util.ArrayList;

import kernel_scene.scene_kernel;
import kernel_component.component;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_component.component_container;

public class component_marker_container 
{
	private String file_name,file_charset;
	public boolean global_private_flag,pickup_flag;
	public ArrayList<component_marker> component_marker_list;
	
	public void destroy()
	{
		component_marker cm;
		
		file_name=null;
		file_charset=null;
		if(component_marker_list!=null){
			for(int i=0,ni=component_marker_list.size();i<ni;i++)
				if((cm=component_marker_list.get(i))!=null)
					cm.destroy();
			component_marker_list.clear();
			component_marker_list=null;
		}
	}
	private void write(scene_kernel sk)
	{
		component_marker cm;
		component operate_comp;
		
		if((file_name==null)||(file_charset==null)||(!global_private_flag))
			return;
		file_writer fw=new file_writer(file_name,file_charset);
		
		for(int i=0,ni=component_marker_list.size();i<ni;i++) {
			if((cm=component_marker_list.get(i))==null)
				continue;
			if((operate_comp=sk.component_cont.get_component(cm.marker_component_id))==null)
				continue;
			if(operate_comp.component_name==null)
				continue;
			if(operate_comp.component_name.trim().length()<=0)
				continue;
			fw.print  (			cm.marker_x).
			   print  ("	",	cm.marker_y).
			   println("	",	cm.marker_z).
			   println(operate_comp.component_name).
			   println(cm.marker_text).
			   println();
		}
		fw.println();
		fw.close();
	}
	public void clear_component_marker(long marker_id,scene_kernel sk)
	{
		component_marker cm;
		for(int i=component_marker_list.size()-1;i>=0;i--)
			if((cm=component_marker_list.get(i))==null)
				component_marker_list.remove(i);
			else if(cm.marker_id==marker_id) {
				component_marker_list.remove(i);
				cm.destroy();
			}
		write(sk);
	}
	public void delete_component_marker(int index_id,scene_kernel sk)
	{
		if(index_id>=0)
			if(index_id<component_marker_list.size()) {
				component_marker_list.remove(index_id);
				write(sk);
			}
	}
	public void clear_all_component_marker(scene_kernel sk,boolean write_flag)
	{
		component_marker cm;
		for(int i=component_marker_list.size()-1;i>=0;i--)
			if((cm=component_marker_list.get(i))!=null)
				cm.destroy();
		component_marker_list.clear();
		if(write_flag)
			write(sk);
	}
	public long  append_component_marker(scene_kernel sk,
			component my_mark_comp,String my_marker_text,
			double my_marker_x,double my_marker_y,double my_marker_z)
	{
		if((my_mark_comp==null)||(my_marker_text==null))
			return -1;
		if((my_marker_text=my_marker_text.trim()).length()<=0)
			return -1;
		component_marker cm=new component_marker(
			my_mark_comp,my_marker_text,my_marker_x,my_marker_y,my_marker_z);
		component_marker_list.add(cm);
		write(sk);
		return cm.marker_id;
	}
	public component_marker_container(boolean my_pickup_flag)
	{
		global_private_flag=false;
		pickup_flag=my_pickup_flag;
		component_marker_list=new ArrayList<component_marker>();
		file_name=null;
		file_charset=null;
		return;
	}
	public component_marker_container(
			String my_directory_comp_name,String my_file_name,component_container component_cont)
	{
		component directory_comp,mark_comp;
		global_private_flag=true;
		pickup_flag=false;
		component_marker_list=new ArrayList<component_marker>();
		
		if((directory_comp=component_cont.search_component(my_directory_comp_name))==null) {
			file_name=null;
			file_charset=null;
			return;
		}
		file_name=directory_comp.component_directory_name+my_file_name;
		file_charset=directory_comp.component_charset;
		
		for(file_reader fr=new file_reader(file_name,file_charset);;){
			double marker_x=fr.get_double(),marker_y=fr.get_double(),marker_z=fr.get_double();
			String marker_component_name=fr.get_string();
			if(fr.eof()){
				fr.close();
				break;
			}
			String marker_text=fr.get_string();
			if((marker_component_name==null)||(marker_text==null))
				continue;
			if((marker_component_name.length()<=0)||(marker_text.length()<=0))
				continue;
			if((mark_comp=component_cont.search_component(marker_component_name))==null)
				continue;
			var cm=new component_marker(mark_comp,marker_text,marker_x,marker_y,marker_z);
			component_marker_list.add(cm);
		}
	}
}
