package kernel_engine;

import kernel_render.render;
import kernel_render.render_container;
import kernel_part.part;
import kernel_part.part_container_for_part_search;
import kernel_component.component;

public class compress_render_container
{
	private render_container render_cont;
	
	private static part[]compress_part(part my_part[])
	{
		if(my_part==null)
			return null;
		if(my_part.length<=0)
			return null;
		int part_number=0;
		for(int i=0,ni=my_part.length;i<ni;i++) {
			if(my_part[i]==null)
				continue;
			if(my_part[i].render_id<0)
				my_part[part_number++]=my_part[i];
			else
				my_part[i].destroy();
		}
		if(part_number<=0)
			return null;
		if(my_part.length!=part_number){
			part bak[]=my_part;
			my_part=new part[part_number];
			for(int i=0,ni=my_part.length;i<ni;i++)
				my_part[i]=bak[i];
		}
		for(int i=0,ni=my_part.length,permanent_part_from_id;i<ni;i++){
			my_part[i].part_id=i;
			my_part[i].part_from_id=-1;
			if((permanent_part_from_id=my_part[i].permanent_part_from_id)>=0)
				for(int begin_pointer=0,end_pointer=my_part.length-1;begin_pointer<=end_pointer;){
					int middle_pointer=(begin_pointer+end_pointer)/2;
					if(my_part[middle_pointer].permanent_part_id>permanent_part_from_id)
						end_pointer		=middle_pointer-1;
					else if(my_part[middle_pointer].permanent_part_id<permanent_part_from_id)
						begin_pointer	=middle_pointer+1;
					else{
						my_part[i].part_from_id=middle_pointer;
						break;
					}
				}
		}
		return my_part;
	}
	private int compress_render()
	{
		if(render_cont.renders==null)
			return 0;
		int render_number=0,total_original_part_number=0;
		for(int i=0,ni=render_cont.renders.length;i<ni;i++)
			if(render_cont.renders[i].parts!=null){
				total_original_part_number+=render_cont.renders[i].parts.length;
				if((render_cont.renders[i].parts=compress_part(render_cont.renders[i].parts))!=null)
					render_cont.renders[render_number++]=render_cont.renders[i];
			}
		if(render_number==0){
			render_cont.renders=null;
			return total_original_part_number;
		}
		if(render_cont.renders.length!=render_number){
			render bak[]=render_cont.renders;
			render_cont.renders=new render[render_number];
			for(int i=0;i<render_number;i++)
				render_cont.renders[i]=bak[i];
		}
		for(int i=0;i<render_number;i++)
			for(int j=0,nj=render_cont.renders[i].parts.length;j<nj;j++)
				render_cont.renders[i].parts[j].render_id=i;
		
		return total_original_part_number;
	}
	private void touch_component_access_part(component comp,part_container_for_part_search pcps)
	{
		part p,p_array[];
		
		for(int i=0,ni=comp.children_number();i<ni;i++)
			touch_component_access_part(comp.children[i],pcps);
		
		for(int i=0,ni=comp.driver_number();i<ni;i++)
			if((p=comp.driver_array[i].component_part)!=null){
				if(p.part_par.assemble_part_name!=null)
					if((p_array=pcps.search_part(p.part_par.assemble_part_name))!=null)
						for(int j=0,nj=p_array.length;j<nj;j++)
							p_array[j].render_id=-1;
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
	}
}
