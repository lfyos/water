package kernel_buffer;

import java.util.ArrayList;

import kernel_render.render;

public class component_render_buffer 
{
	class component_render_list
	{
		public ArrayList<component_render>list;
		public component_render_list()
		{
			list=new ArrayList<component_render>();
		}
	}
	private component_render_list render_buffer[][];
	
	public void destroy()
	{
		component_render p;
		
		if(render_buffer!=null) {
			for(int i=0,ni=render_buffer.length;i<ni;i++) {
				if(render_buffer[i]==null)
					continue;
				for(int j=0,nj=render_buffer[i].length;j<nj;j++) {
					if(render_buffer[i][j]==null)
						continue;
					for(int k=0,nk=render_buffer[i][j].list.size();k<nk;k++)
						if((p=render_buffer[i][j].list.get(k))!=null)
							p.destroy();
					render_buffer[i][j].list.clear();
					render_buffer[i][j].list=null;
					render_buffer[i][j]=null;
				}
				render_buffer[i]=null;
			}
			render_buffer=null;
		}
	}
	public component_render_buffer(ArrayList<render> ren)
	{
		render_buffer=new component_render_list[ren.size()][];
		for(int i=0,ni=render_buffer.length;i<ni;i++){
			render r=ren.get(i);
			render_buffer[i]=new component_render_list[(r==null)?0:(r.parts==null)?0:r.parts.size()];
			for(int j=0,nj=render_buffer[i].length;j<nj;j++)
				render_buffer[i][j]=new component_render_list();
		}	
	}
	public component_render get_render_buffer(
				int render_id,int part_id,int render_buffer_id,int max_part_component_number)
	{
		if(render_id<0)
			return null;
		if(render_id>=render_buffer.length)
			return null;
		if(part_id<0)
			return null;
		if(part_id>=render_buffer[render_id].length)
			return null;
		if(render_buffer_id<0)
			return null;
		ArrayList<component_render>list=render_buffer[render_id][part_id].list;
		for(int pos;(pos=list.size())<=render_buffer_id;)
			list.add(pos,new component_render(max_part_component_number));
		return list.get(render_buffer_id);
	}
}