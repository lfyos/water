package kernel_scene;

import java.util.ArrayList;

import kernel_render.render;
import kernel_render.render_container;
import kernel_part.part;
import kernel_part.part_container_for_part_search;
import kernel_common_class.sorter;
import kernel_component.component;

public class compress_render_container
{
	private render_container render_cont;
	
	private ArrayList<part>compress_part(ArrayList<part> my_part)
	{
		if(my_part==null)
			return null;
		if(my_part.size()<=0)
			return null;
		
		int part_number=0;
		ArrayList<part> ret_val=new ArrayList<part>();
		
		for(int i=0,ni=my_part.size();i<ni;i++) {
			part pp;
			if((pp=my_part.get(i))!=null) {
				if(pp.render_id<0)
					ret_val.add(part_number++,pp);
				else
					pp.destroy();
			}
			my_part.set(i,null);
		}
		if(part_number<=0)
			return null;

		class create_sorted_part extends sorter<part,part>
		{
			public int compare_data(part s,part t)
			{
				return s.system_name.compareTo(t.system_name);
			}
			public int compare_key(part s,part t)
			{
				return s.system_name.compareTo(t.system_name);
			}
			public create_sorted_part()
			{
				data_array=ret_val.toArray(new part[ret_val.size()]);
				do_sort();
				for(int i=0,ni=data_array.length;i<ni;i++)
					ret_val.set(i,data_array[i]);
			}
		};
		new create_sorted_part();

		for(int i=0,ni=ret_val.size();i<ni;i++){
			part pp=ret_val.get(i);
			pp.part_id=i;
			pp.part_from_id=-1;
			
			if(pp.permanent_part_from_id>=0)
				for(int begin_pointer=0,end_pointer=ret_val.size()-1;begin_pointer<=end_pointer;){
					int middle_pointer=(begin_pointer+end_pointer)/2;
					part pm=ret_val.get(middle_pointer);
					if(pm.permanent_part_id>pp.permanent_part_from_id)
						end_pointer		=middle_pointer-1;
					else if(pm.permanent_part_id<pp.permanent_part_from_id)
						begin_pointer	=middle_pointer+1;
					else{
						pp.part_from_id=middle_pointer;
						break;
					}
				}
		}
		return ret_val;
	}
	private int compress_render()
	{
		render r;
		int render_number=0,total_original_part_number=0;
		if(render_cont.renders==null)
			render_cont.renders=new ArrayList<render>();
		else{
			for(int i=0,ni=render_cont.renders.size();i<ni;i++)
				if((r=render_cont.renders.get(i)).parts!=null){
					total_original_part_number+=r.parts.size();
					if((r.parts=compress_part(r.parts))!=null)
						if(r.parts.size()>0) {
							render_cont.renders.set(render_number++,r);
							continue;
						}
					r.destroy();
				}
			for(int i=render_cont.renders.size()-1;i>=render_number;i--)
				render_cont.renders.remove(i);
			
			for(int i=0;i<render_number;i++) {
				r=render_cont.renders.get(i);
				r.render_id=i;
				for(int j=0,nj=r.parts.size();j<nj;j++)
					r.parts.get(j).render_id=i;
			}
		}
		return total_original_part_number;
	}
	private void touch_component_access_part(component comp,part_container_for_part_search pcps)
	{
		part p;
		ArrayList<part>p_array;
		
		for(int i=0,ni=comp.children_number();i<ni;i++)
			touch_component_access_part(comp.children[i],pcps);
		
		for(int i=0,ni=comp.driver_number();i<ni;i++)
			if((p=comp.driver_array.get(i).component_part)!=null){
				if(p.part_par.assemble_part_name!=null)
					if((p_array=pcps.search_part(p.part_par.assemble_part_name))!=null)
						for(int j=0,nj=p_array.size();j<nj;j++)
							p_array.get(j).render_id=-1;
				p.render_id=-1;
			}
	}
	
	public int original_part_number;
	public compress_render_container(render_container my_render_cont,
			part_container_for_part_search pcps,component root_component)
	{
		render_cont=my_render_cont;
		touch_component_access_part(root_component,pcps);
		original_part_number=compress_render();
		
		render_cont.sorted_renders=null;
		if(render_cont.renders==null)
			return;
	
		class create_sorted_render extends sorter<render,render>
		{
			public int compare_data(render s,render t)
			{
				return s.render_name.compareTo(t.render_name);
			}
			public int compare_key(render s,render t)
			{
				return s.render_name.compareTo(t.render_name);
			}
			public create_sorted_render()
			{
				int render_number=render_cont.renders.size();
				data_array=new render[render_number];
				for(int i=0;i<render_number;i++)
					data_array[i]=render_cont.renders.get(i);
				
				do_sort();
				
				render_cont.sorted_renders=new ArrayList<render>();
				for(int i=0;i<render_number;i++)
					render_cont.sorted_renders.add(i,data_array[i]);
			}
		};
		new create_sorted_render();
	}
}
