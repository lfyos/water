package kernel_component;

import kernel_common_class.sorter;
import kernel_part.part;
import kernel_part.part_rude;
import kernel_render.render;
import kernel_transformation.box;
import kernel_transformation.point;

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
	public void sort_component_list(int component_sort_type,double component_sort_min_distance)
	{
		class component_link_list_sorter extends sorter <component_link_list,component_link_list>
		{
			private int component_sort_type;
			private double component_sort_min_distance;
			
			public int compare_data(component_link_list s,component_link_list t)
			{
				point ps=s.comp.absolute_location.multiply(0, 0, 0);
				point pt=t.comp.absolute_location.multiply(0, 0, 0);
				switch(component_sort_type) {
				default:
				case 0://"xyz":
					if(Math.abs(ps.x-pt.x)>component_sort_min_distance)
						return (ps.x<pt.x)?-1:1;
					if(Math.abs(ps.y-pt.y)>component_sort_min_distance)
						return (ps.y<pt.y)?-1:1;
					if(Math.abs(ps.z-pt.z)>component_sort_min_distance)
						return (ps.z<pt.z)?-1:1;
					if(Math.abs(ps.y-pt.y)>component_sort_min_distance)
						return (ps.y<pt.y)?-1:1;
					if(Math.abs(ps.z-pt.z)>component_sort_min_distance)
						return (ps.z<pt.z)?-1:1;
					if(ps.x<pt.x)
						return -1;
					if(ps.x>pt.x)
						return 1;
					if(ps.y<pt.y)
						return -1;
					if(ps.y>pt.y)
						return 1;
					if(ps.z<pt.z)
						return -1;
					if(ps.z>pt.z)
						return 1;
					return 0;
				case 1://"xzy":
					if(Math.abs(ps.x-pt.x)>component_sort_min_distance)
						return (ps.x<pt.x)?-1:1;
					if(Math.abs(ps.z-pt.z)>component_sort_min_distance)
						return (ps.z<pt.z)?-1:1;
					if(Math.abs(ps.y-pt.y)>component_sort_min_distance)
						return (ps.y<pt.y)?-1:1;
					if(ps.x<pt.x)
						return -1;
					if(ps.x>pt.x)
						return 1;
					if(ps.z<pt.z)
						return -1;
					if(ps.z>pt.z)
						return 1;
					if(ps.y<pt.y)
						return -1;
					if(ps.y>pt.y)
						return 1;
					return 0;
				case 2://"yxz":
					if(Math.abs(ps.y-pt.y)>component_sort_min_distance)
						return (ps.y<pt.y)?-1:1;
					if(Math.abs(ps.x-pt.x)>component_sort_min_distance)
						return (ps.x<pt.x)?-1:1;
					if(Math.abs(ps.z-pt.z)>component_sort_min_distance)
						return (ps.z<pt.z)?-1:1;
					if(ps.y<pt.y)
						return -1;
					if(ps.y>pt.y)
						return 1;
					if(ps.x<pt.x)
						return -1;
					if(ps.x>pt.x)
						return 1;
					if(ps.z<pt.z)
						return -1;
					if(ps.z>pt.z)
						return 1;
					return 0;
				case 3://"yzx":
					if(Math.abs(ps.y-pt.y)>component_sort_min_distance)
						return (ps.y<pt.y)?-1:1;
					if(Math.abs(ps.z-pt.z)>component_sort_min_distance)
						return (ps.z<pt.z)?-1:1;
					if(Math.abs(ps.x-pt.x)>component_sort_min_distance)
						return (ps.x<pt.x)?-1:1;
					if(ps.y<pt.y)
						return -1;
					if(ps.y>pt.y)
						return 1;
					if(ps.z<pt.z)
						return -1;
					if(ps.z>pt.z)
						return 1;
					if(ps.x<pt.x)
						return -1;
					if(ps.x>pt.x)
						return 1;
					return 0;
				case 4://"zxy":
					if(Math.abs(ps.z-pt.z)>component_sort_min_distance)
						return (ps.z<pt.z)?-1:1;
					if(Math.abs(ps.x-pt.x)>component_sort_min_distance)
						return (ps.x<pt.x)?-1:1;
					if(Math.abs(ps.y-pt.y)>component_sort_min_distance)
						return (ps.y<pt.y)?-1:1;
					if(ps.z<pt.z)
						return -1;
					if(ps.z>pt.z)
						return 1;
					if(ps.x<pt.x)
						return -1;
					if(ps.x>pt.x)
						return 1;
					if(ps.y<pt.y)
						return -1;
					if(ps.y>pt.y)
						return 1;
					return 0;
				case 5://"zyx":
					if(Math.abs(ps.z-pt.z)>component_sort_min_distance)
						return (ps.z<pt.z)?-1:1;
					if(Math.abs(ps.y-pt.y)>component_sort_min_distance)
						return (ps.y<pt.y)?-1:1;
					if(Math.abs(ps.x-pt.x)>component_sort_min_distance)
						return (ps.x<pt.x)?-1:1;
					if(ps.z<pt.z)
						return -1;
					if(ps.z>pt.z)
						return 1;
					if(ps.y<pt.y)
						return -1;
					if(ps.y>pt.y)
						return 1;
					if(ps.x<pt.x)
						return -1;
					if(ps.x>pt.x)
						return 1;
					return 0;
				}
			}
			public int compare_key(component_link_list s,component_link_list t)
			{
				return compare_data(s,t);
			}
			public component_link_list_sorter(component_link_list cll,
					int my_component_sort_type,double my_component_sort_min_distance)
			{
				component_sort_type=my_component_sort_type;
				component_sort_min_distance=my_component_sort_min_distance;
				
				int number=0;
				for(component_link_list p=cll;p!=null;p=p.next_list_item)
					number++;
				data_array=new component_link_list[number];
				number=0;
				for(component_link_list p=cll;p!=null;p=p.next_list_item)
					data_array[number++]=p;
				do_sort(new component_link_list[number]);
				for(int i=0,ni=number-1;i<ni;i++)
					data_array[i].next_list_item=data_array[i+1];
				data_array[number-1].next_list_item=null;
			}
		};
		for(int i=0,ni=component_collector.length;i<ni;i++)
			if(component_collector[i]!=null)
				for(int j=0,nj=component_collector[i].length;j<nj;j++)
					if(component_collector[i][j]!=null)
						if(component_collector[i][j].next_list_item!=null)
							component_collector[i][j]=new component_link_list_sorter(component_collector[i][j],
								component_sort_type,component_sort_min_distance).data_array[0];
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
	
	public void component_space_sort(int sort_type,double min_distance)
	{
		for(int i=0,ni=component_collector.length;i<ni;i++)
			if(component_collector[i]!=null)
				for(int j=0,nj=component_collector[i].length;j<nj;j++)
					component_collector[i][j]=component_space_sorter.sort_component(
							component_collector[i][j],sort_type,min_distance);
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
		if((driver_id>=0)&&(driver_id<comp.driver_number()))
			if(comp.driver_array[driver_id]!=null)
				if(comp.driver_array[driver_id].component_part!=null) {
					part_rude part_mesh=comp.driver_array[driver_id].component_part.part_mesh;
					if(part_mesh!=null){
						total_face_primitive_number+=part_mesh.total_face_primitive_number;
						total_edge_primitive_number+=part_mesh.total_edge_primitive_number;
						total_point_primitive_number+=part_mesh.total_point_primitive_number;
					}
				}
		component_collector[render_id][part_id]=new component_link_list(
				comp,driver_id,component_collector[render_id][part_id]);
		return 1;
	}
	public int register_component(component comp,int driver_id)
	{
		part p=comp.driver_array[driver_id].component_part;
		return register_component(comp,driver_id,p.render_id,p.part_id);
	}
	public int register_component(component comp)
	{
		int register_number=0;
		if(comp!=null){
			if(comp.driver_number()>0)
				register_number+=register_component(comp,0);
			else
				for(int i=0,ni=comp.children_number();i<ni;i++)
					register_number+=register_component(comp.children[i]);
		}
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
	private void init(render renders[])
	{
		render_component_number	=null;
		part_component_number	=null;
		component_collector		=null;
		
		if(renders!=null)
			if(renders.length>0){
				render_part_number		=new int[renders.length] ;
				render_component_number	=new int[renders.length] ;
				part_component_number	=new int[renders.length][];
				component_collector		=new component_link_list[renders.length][];
				for(int i=0,ni=renders.length;i<ni;i++){
					part_component_number[i]=null;
					component_collector	 [i]=null;
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
