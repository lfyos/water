package kernel_buffer;

import java.util.ArrayList;

import kernel_render.render;

public class component_render_buffer 
{
	private component_render render_buffer[][][];
	
	public void destroy()
	{
		if(render_buffer!=null)
			for(int i=0,ni=render_buffer.length;i<ni;i++)
				if(render_buffer[i]!=null){
					for(int j=0,nj=render_buffer[i].length;j<nj;j++)
						if(render_buffer[i][j]!=null) {
							for(int k=0,nk=render_buffer[i][j].length;k<nk;k++)
								if(render_buffer[i][j][k]!=null){
									render_buffer[i][j][k].destroy();
									render_buffer[i][j][k]=null;
								}
							render_buffer[i][j]=null;
						}
					render_buffer[i]=null;
				}
		render_buffer=null;
	}

	public component_render_buffer(ArrayList<render> ren,int max_component_number)
	{
		render_buffer=new component_render[ren.size()][][];
		for(int i=0;i<render_buffer.length;i++){
			render r=ren.get(i);
			render_buffer[i]=new component_render[(r==null)?1:r.parts.size()][];
			for(int j=0;j<render_buffer[i].length;j++){
				render_buffer[i][j]=new component_render[2];
				for(int k=0;k<render_buffer[i][j].length;k++)
					render_buffer[i][j][k]=null;
			}
		}	
	}
	public component_render get_render_buffer(int render_id,int part_id,int render_buffer_id,int max_part_component_number)
	{
		if(render_id<0)
			return null;
		if(render_id>=render_buffer.length)
			return null;
		if(part_id<0)
			return null;
		if(part_id>=render_buffer[render_id].length)
			return null;
		
		if(render_buffer[render_id][part_id].length<=render_buffer_id){
			component_render buf_bak[]=render_buffer[render_id][part_id];
			render_buffer[render_id][part_id]=new component_render[render_buffer_id+1];
			for(int i=0;i<buf_bak.length;i++)
				render_buffer[render_id][part_id][i]=buf_bak[i];
			for(int i=buf_bak.length;i<render_buffer[render_id][part_id].length;i++)
				render_buffer[render_id][part_id][i]=null;
		}
		if(render_buffer[render_id][part_id][render_buffer_id]==null)
			render_buffer[render_id][part_id][render_buffer_id]=new component_render(max_part_component_number);
		
		return render_buffer[render_id][part_id][render_buffer_id];
	}
}