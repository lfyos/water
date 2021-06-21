package kernel_component;

import kernel_component.component;
import kernel_component.component_array;
import kernel_component.component_link_list;
import kernel_part.part;
import kernel_part.part_rude;
import kernel_render.render;
import kernel_transformation.box;

public class component_collector 
{
	public component_link_list component_collector[][];
	public int render_number,part_number,component_number,render_component_number[],part_component_number[][];
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
		
		render_component_number=null;
		part_component_number=null;
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
	
	private boolean has_not_been_sorted_flag;
	public void component_space_sort(int sort_type,double min_distance)
	{
		if(has_not_been_sorted_flag)
			for(int i=0,ni=component_collector.length;i<ni;i++)
				if(component_collector[i]!=null)
					for(int j=0,nj=component_collector[i].length;j<nj;j++)
						component_collector[i][j]=component_space_sorter.sort_component(
								component_collector[i][j],sort_type,min_distance);
		has_not_been_sorted_flag=false;
	}
	
	public component_array get_component_array()
	{
		if((component_number<=0)||(component_collector==null))
			return null;
		component_link_list p;
		component_array comp_con=new component_array(component_number);
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
		if((render_id>=0)&&(part_id>=0)&&(render_id<(component_collector.length)))
			if(part_id<(component_collector[render_id].length)){
				component_number++;
				if((render_component_number[render_id]++)==0)
					render_number++;
				if((part_component_number[render_id][part_id]++)==0)
					part_number++;
				
				part_rude part_mesh;
				if((driver_id>=0)&&(driver_id<comp.driver_number()))
					if(comp.driver_array[driver_id]!=null)
						if(comp.driver_array[driver_id].component_part!=null)
							if((part_mesh=comp.driver_array[driver_id].component_part.part_mesh)!=null) {
								total_face_primitive_number+=part_mesh.total_face_primitive_number;
								total_edge_primitive_number+=part_mesh.total_edge_primitive_number;
								total_point_primitive_number+=part_mesh.total_point_primitive_number;
							}
					
				component_collector[render_id][part_id]=new component_link_list(
						comp,driver_id,component_collector[render_id][part_id]);
				has_not_been_sorted_flag=true;
				return 1;
			}
		return 0;
	}
	public int register_component(component comp,int driver_id)
	{
		part p=comp.driver_array[driver_id].component_part;
		return register_component(comp,driver_id,p.render_id,p.part_id);
	}
	public int register_component(component comp)
	{
		int register_number=0;
		if(comp.driver_number()>0)
			register_number+=register_component(comp,0);
		else
			for(int i=0,ni=comp.children_number();i<ni;i++)
				register_number+=register_component(comp.children[i]);
		return register_number;
	}
	public int register_component(component_array all_components)
	{
		int register_number=0;
		for(int i=0,ni=all_components.component_number;i<ni;i++)
			register_number+=register_component(all_components.comp[i]);
		return register_number;
	}
	public int register_all(component comp)
	{
		int register_number=0;
		if(comp!=null){
			for(int i=0,ni=comp.driver_number();i<ni;i++)
				register_number+=register_component(comp,i);
			for(int i=0,ni=comp.children_number();i<ni;i++)
				register_number+=register_all(comp.children[i]);
		}
		return register_number;
	}
	
	public void reset()
	{
		if(component_collector!=null)
			for(int i=0,ni=component_collector.length;i<ni;i++)
				if(component_collector[i]!=null)
					for(int j=0,nj=component_collector[i].length;j<nj;j++)
						component_collector[i][j]=null;
		render_number=0;
		part_number=0;
		component_number=0;
		total_face_primitive_number=0;
		total_edge_primitive_number=0;
		total_point_primitive_number=0;
		
		if(render_component_number!=null)
			for(int i=0,ni=render_component_number.length;i<ni;i++)
				render_component_number[i]=0;
		
		if(part_component_number!=null)
			for(int i=0,ni=part_component_number.length;i<ni;i++)
				if(part_component_number[i]!=null)
					for(int j=0,nj=part_component_number[i].length;j<nj;j++)
						part_component_number[i][j]=0;
		list_id=1;
		title="";
		description="";
		audio_file_name="";
		
		has_not_been_sorted_flag=true;
	}
	private void init(render renders[])
	{
		render_component_number=null;
		part_component_number=null;
		component_collector=null;
		
		if(renders!=null)
			if(renders.length>0){
				render_component_number	=new int[renders.length] ;
				part_component_number	=new int[renders.length][];
				component_collector		=new component_link_list[renders.length][];
				for(int i=0,ni=component_collector.length;i<ni;i++){
					component_collector[i]	=null;
					part_component_number[i]=null;
					if(renders[i]!=null)
						if(renders[i].parts!=null)
							if(renders[i].parts.length>0){
								part_component_number[i]=new int[renders[i].parts.length];
								component_collector[i]	=new component_link_list[renders[i].parts.length];
							}
				}
			}
		reset();
	}
	public  component_collector(render renders[])
	{
		init(renders);
	}
	public  component_collector(render renders[],component comp)
	{
		init(renders);
		register_component(comp);
	}
	public  component_collector(render renders[],component_array all_components)
	{
		init(renders);
		register_component(all_components);
	}
}
