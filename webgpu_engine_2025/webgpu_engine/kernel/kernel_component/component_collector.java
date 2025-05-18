package kernel_component;

import java.util.ArrayList;

import kernel_part.part;
import kernel_render.render;
import kernel_part.part_rude;
import kernel_transformation.box;
import kernel_driver.component_driver;

public class component_collector 
{
	public component_link_list component_collector[][];
	public int render_number,part_number,component_number;
	public int render_part_number[],render_component_number[];
	public int part_component_number[][];
	public long total_face_primitive_number,total_edge_primitive_number,total_point_primitive_number;
	public long list_id;
	
	public String title,description,audio_file_name;
	
	public void destroy()
	{
		if(component_collector!=null){
			for(int i=0,ni=component_collector.length;i<ni;i++)
				if(component_collector[i]!=null){
					for(int j=0,nj=component_collector[i].length;j<nj;j++)
						if(component_collector[i][j]!=null){
							component_collector[i][j].destroy();
							component_collector[i][j]=null;
						}
					component_collector[i]=null;
				}
			component_collector=null;
		}
		render_part_number=null;
		render_component_number=null;
		part_component_number=null;
	}
	public void sort_component_list(String sort_type,double sort_min_distance)
	{
		for(int i=0,ni=component_collector.length;i<ni;i++)
			if(component_collector[i]!=null)
				for(int j=0,nj=component_collector[i].length;j<nj;j++)
					if(component_collector[i][j]!=null)
						if(component_collector[i][j].next_list_item!=null)
							component_collector[i][j]=component_link_list_sorter.do_sort(
								component_collector[i][j],sort_type,sort_min_distance);
	}
	public box caculate_box(boolean mandatory_flag)
	{
		box b,ret_val=null;
		for(int i=0,ni=component_collector.length;i<ni;i++)
			if(component_collector[i]!=null)
				for(int j=0,nj=component_collector[i].length;j<nj;j++)
					for(component_link_list p=component_collector[i][j];p!=null;p=p.next_list_item)
						if((b=p.comp.get_component_box(mandatory_flag))!=null){
							if(ret_val==null)
								ret_val=b;
							else
								ret_val=ret_val.add(b);
						}
		return ret_val;
	}
	public void copy_information(component_collector collector)
	{
		list_id			=collector.list_id;
		title			=collector.title;
		description		=collector.description;
		audio_file_name	=collector.audio_file_name;
	}
	public component_array get_component_array()
	{
		if((component_number<=0)||(component_collector==null))
			return null;
		component_link_list p;
		component_array comp_con=new component_array();
		for(int i=0,ni=component_collector.length;i<ni;i++)
			if(component_collector[i]!=null)
				for(int j=0,nj=component_collector[i].length;j<nj;j++)
					for(p=component_collector[i][j];p!=null;p=p.next_list_item)
						comp_con.add_component(p.comp);
		return comp_con;
	}
	public component[] get_component()
	{
		component_array comp_array=get_component_array();
		if(comp_array==null)
			return null;
		else
			return comp_array.get_component();
	}
	public int register_component(component comp,int driver_id,int render_id,int part_id)
	{
		if((render_id<0)||(render_id>=component_collector.length))
			return 0;
		if((part_id<0)||(part_id>=component_collector[render_id].length))
			return 0;
		component_number++;
		if((render_component_number[render_id]++)==0)
			render_number++;
		if((part_component_number[render_id][part_id]++)==0) {
			part_number++;
			render_part_number[render_id]++;
		}
		if((driver_id>=0)&&(driver_id<comp.driver_array.size())) {
			component_driver c_d=comp.driver_array.get(driver_id);
			if(c_d!=null)
				if(c_d.component_part!=null){
					part_rude part_mesh=c_d.component_part.part_mesh;
					if(part_mesh!=null){
						total_face_primitive_number+=part_mesh.total_face_primitive_number;
						total_edge_primitive_number+=part_mesh.total_edge_primitive_number;
						total_point_primitive_number+=part_mesh.total_point_primitive_number;
					}
				}
		}
		component_collector[render_id][part_id]=new component_link_list(
				comp,driver_id,component_collector[render_id][part_id]);
		return 1;
	}
	public int register_component(component comp,int driver_id)
	{
		part p=comp.driver_array.get(driver_id).component_part;
		return register_component(comp,driver_id,p.render_id,p.part_id);
	}
	public int register_component(component comp)
	{
		int register_number=0;
		if(comp!=null){
			if(comp.driver_array.size()>0)
				register_number+=register_component(comp,0);
			else
				for(int i=0,ni=comp.children.size();i<ni;i++)
					register_number+=register_component(comp.children.get(i));
		}
		return register_number;
	}
	public int register_component(component_array all_components)
	{
		int register_number=0;
		for(int i=0,ni=all_components.comp_list.size();i<ni;i++)
			register_number+=register_component(all_components.comp_list.get(i));
		return register_number;
	}
	public int register_all(component comp)
	{
		int register_number=0;
		if(comp!=null){
			for(int i=0,ni=comp.driver_array.size();i<ni;i++)
				register_number+=register_component(comp,i);
			for(int i=0,ni=comp.children.size();i<ni;i++)
				register_number+=register_all(comp.children.get(i));
		}
		return register_number;
	}
	public void reset()
	{
		render_number	=0;
		part_number		=0;
		component_number=0;
		total_face_primitive_number =0;
		total_edge_primitive_number =0;
		total_point_primitive_number=0;
		
		if(render_part_number!=null)
			for(int i=0,ni=render_part_number.length;i<ni;i++)
				render_part_number[i]=0;

		if(render_component_number!=null)
			for(int i=0,ni=render_component_number.length;i<ni;i++)
				render_component_number[i]=0;
		
		if(part_component_number!=null)
			for(int i=0,ni=part_component_number.length;i<ni;i++)
				if(part_component_number[i]!=null)
					for(int j=0,nj=part_component_number[i].length;j<nj;j++)
						part_component_number[i][j]=0;
		
		if(component_collector!=null)
			for(int i=0,ni=component_collector.length;i<ni;i++)
				if(component_collector[i]!=null)
					for(int j=0,nj=component_collector[i].length;j<nj;j++)
						component_collector[i][j]=null;
		
		list_id=1;
		title="";
		description="";
		audio_file_name="";
	}
	private void init(ArrayList<render> renders)
	{
		render r;
		int render_number,part_number;
		render_component_number	=null;
		part_component_number	=null;
		component_collector		=null;

		if(renders!=null)
			if((render_number=renders.size())>0){
				render_part_number		=new int[render_number] ;
				render_component_number	=new int[render_number] ;
				part_component_number	=new int[render_number][];
				component_collector		=new component_link_list[render_number][];
				for(int i=0;i<render_number;i++){
					part_component_number[i]=null;
					component_collector	 [i]=null;
					if((r=renders.get(i))!=null)
						if(r.parts!=null) {
							if((part_number=r.parts.size())>0){
								part_component_number[i]=new int[part_number];
								component_collector[i]	=new component_link_list[part_number];
							}
						}
				}
			}
		reset();
	}
	public  component_collector(ArrayList<render> renders)
	{
		init(renders);
	}
	public  component_collector(ArrayList<render> renders,component comp)
	{
		init(renders);
		register_component(comp);
	}
	public  component_collector(ArrayList<render> renders,component_array all_components)
	{
		init(renders);
		register_component(all_components);
	}
}
