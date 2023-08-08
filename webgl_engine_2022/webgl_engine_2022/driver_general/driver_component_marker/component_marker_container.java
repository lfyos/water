package driver_component_marker;

import kernel_component.component;
import kernel_component.component_container;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class component_marker_container 
{
	private String file_name,file_charset;
	public boolean global_private_flag,pickup_flag;
	public component_marker component_marker_array[];
	
	private void write(engine_kernel ek)
	{
		if((file_name==null)||(file_charset==null)||(!global_private_flag))
			return;
		file_writer fw=new file_writer(file_name,file_charset);
		
		for(int i=0,ni=component_marker_array.length;i<ni;i++) {
			component operate_comp=ek.component_cont.get_component(
					component_marker_array[i].marker_component_id);
			if(operate_comp==null)
				continue;
			if(operate_comp.component_name==null)
				continue;
			if(operate_comp.component_name.trim().length()<=0)
				continue;
			fw.print  (			component_marker_array[i].marker_x).
			   print  ("	",	component_marker_array[i].marker_y).
			   println("	",	component_marker_array[i].marker_z).
			   println(operate_comp.component_name).
			   println(component_marker_array[i].marker_text).
			   println();
		}
		fw.println();
		fw.close();
	}
	
	public void delete_component_marker(int marker_id,engine_kernel ek)
	{
		if((marker_id<0)||(marker_id>=component_marker_array.length))
			return;
		component_marker bak[]=component_marker_array;
		component_marker_array=new component_marker[bak.length-1];
		for(int i=0,j=0,ni=bak.length;i<ni;i++)
			if(i!=marker_id)
				component_marker_array[j++]=bak[i];
		write(ek);
		return;
	}
	public void clear_component_marker(long marker_id,engine_kernel ek)
	{
		boolean done_flag=false;
		component_marker bak[]=component_marker_array;
		component_marker_array=new component_marker[bak.length-1];
		for(int i=0,j=0,ni=bak.length;i<ni;i++)
			if(bak[i].marker_id==marker_id)
				done_flag=true;
			else
				component_marker_array[j++]=bak[i];
		if(done_flag)
			write(ek);
		else
			component_marker_array=bak;
		return;
	}
	public void clear_all_component_marker(engine_kernel ek)
	{
		component_marker_array=new component_marker[] {};
		write(ek);
		return;
	}
	public long  append_component_marker(engine_kernel ek,
			component my_mark_comp,String my_marker_text,
			double my_marker_x,double my_marker_y,double my_marker_z)
	{
		if((my_mark_comp==null)||(my_marker_text==null))
			return -1;
		if((my_marker_text=my_marker_text.trim()).length()<=0)
			return -1;
		component_marker bak[]=component_marker_array;
		component_marker_array=new component_marker[bak.length+1];
		for(int i=0,ni=bak.length;i<ni;i++)
			component_marker_array[i]=bak[i];
		component_marker_array[bak.length]=new component_marker(
				my_mark_comp,my_marker_text,my_marker_x,my_marker_y,my_marker_z);
		write(ek);
		return component_marker_array[bak.length].marker_id;
	}
	public component_marker_container(boolean my_pickup_flag)
	{
		global_private_flag=false;
		pickup_flag=my_pickup_flag;
		component_marker_array=new component_marker[] {};
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
		component_marker_array=new component_marker[] {};
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
			component_marker bak[]=component_marker_array;
			component_marker_array=new component_marker[bak.length+1];
			for(int i=0,ni=bak.length;i<ni;i++)
				component_marker_array[i]=bak[i];
			component_marker_array[bak.length]=new component_marker(
					mark_comp,marker_text,marker_x,marker_y,marker_z);
		}
	}
}
