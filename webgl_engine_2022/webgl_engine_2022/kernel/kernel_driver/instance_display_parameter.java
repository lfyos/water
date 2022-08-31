package kernel_driver;

public class instance_display_parameter
{
	public String body_title,face_title,vertex_title,loop_title,edge_title,point_title,value_title;
	public int display_value_id;
	public double value_min,value_max;
	public boolean display_absolute_value_flag;
	
	public void destroy()
	{
		if(body_title!=null)
			body_title=null;
		if(face_title!=null)
			face_title=null;
		if(vertex_title!=null)
			vertex_title=null;
		if(loop_title!=null)
			loop_title=null;
		if(edge_title!=null)
			edge_title=null;
		if(point_title!=null)
			point_title=null;
		if(value_title!=null)
			value_title=null;
	}
	public instance_display_parameter()
	{
		body_title=null;
		face_title=null;
		vertex_title=null;
		loop_title=null;
		edge_title=null;
		point_title=null;
		
		value_title=null;
		display_value_id=0;
		value_min=0.0;
		value_max=1.0;
		
		display_absolute_value_flag=false;
	}
}
