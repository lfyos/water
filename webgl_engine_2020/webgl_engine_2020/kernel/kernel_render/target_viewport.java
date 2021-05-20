package kernel_render;

import kernel_common_class.const_value;

public class target_viewport 
{
	public double x,y,width,height;
	public int method_id,pass_id;
	public double clear_color[];
	
	public void destroy()
	{
		clear_color=null;
	}
	private void init(	double my_x,double my_y,double my_width,double my_height,
						int my_method_id,int my_pass_id,double my_clear_color[])
	{
		x			=my_x;
		y			=my_y;
		width		=my_width;
		height		=my_height;
		method_id	=my_method_id;
		pass_id		=my_pass_id;
		
		if(my_clear_color==null)
			clear_color=null;
		else{
			clear_color=new double[my_clear_color.length];
			for(int i=0,ni=my_clear_color.length;i<ni;i++)
				clear_color[i]=my_clear_color[i];
		}
	}
	public target_viewport(	double my_x,double my_y,double my_width,double my_height,
							int my_method_id,int my_pass_id,double my_clear_color[])
	{
		init(my_x,my_y,my_width,my_height,my_method_id,my_pass_id,my_clear_color);
	}
	public target_viewport()
	{
		init(-1,-1,2,2,0,0,null);
	}
	public target_viewport(target_viewport my_viewport)
	{
		init(	my_viewport.x,my_viewport.y,my_viewport.width,my_viewport.height,
				my_viewport.method_id,my_viewport.pass_id,my_viewport.clear_color);
	}
	public static boolean is_not_same(target_viewport s,target_viewport t)
	{
		if(s.method_id!=t.method_id)
			return true;
		if(s.pass_id!=t.pass_id)
			return true;
		if(Math.abs(s.x-t.x)>const_value.min_value)
			return true;
		if(Math.abs(s.y-t.y)>const_value.min_value)
			return true;
		if(Math.abs(s.width-t.width)>const_value.min_value)
			return true;
		if(Math.abs(s.height-t.height)>const_value.min_value)
			return true;
		if((s.clear_color==null)^(t.clear_color==null))
			return true;
		if(s.clear_color==null)
			return false;
		if(s.clear_color.length!=t.clear_color.length)
			return true;
		for(int i=0,ni=s.clear_color.length;i<ni;i++)
			if(Math.abs(s.clear_color[i]-t.clear_color[i])>const_value.min_value)
				return true;
		return false;
	}
	public static boolean is_not_same(target_viewport s[],target_viewport t[])
	{
		if((s==null)&&(t==null))
			return false;
		if((s==null)&&(t!=null))
			return true;
		if((s!=null)&&(t==null))
			return true;
		if(s.length!=t.length)
			return true;
		for(int i=0,ni=s.length;i<ni;i++)
			if(is_not_same(s[i],t[i]))
				return true;
		return false;
	}
}
