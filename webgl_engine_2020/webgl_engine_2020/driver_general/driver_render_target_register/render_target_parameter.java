package driver_render_target_register;

import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

import kernel_render.target_viewport;

public class render_target_parameter
{
	public int parameter_channel_id,camera_id;
	public double center_x,center_y;
	
	public target_viewport viewport[];

	public void write_out(file_writer f)
	{
		f.println("	/*	parameter_channel_id	*/	",	parameter_channel_id);
		f.println("	/*	camera_id				*/	",	camera_id);
		f.println("	/*	center_x,center_y		*/	",	center_x+"\t"+center_y);
		
		f.println();
		f.println("	/*	view_port_number	*/	",viewport.length);
		f.println();
		
		for(int i=0,ni=viewport.length;i<ni;i++){
			f.print ("	/*	view_port	",i);	f.print ("	*/");
			f.print ("	",viewport[i].x);
			f.print ("	",viewport[i].y);
			f.print ("	",viewport[i].width);
			f.print ("	",viewport[i].height);
			f.print ("	",viewport[i].method_id);
			if(viewport[i].clear_color==null)
				f.println("	false");
			else{
				f.print("	true");
				for(int j=0,nj=viewport[i].clear_color.length;j<nj;j++)
					f.print ("	",viewport[i].clear_color[j]);
				f.println();
			}
		}
		f.println();
	}
	public render_target_parameter(file_reader f)
	{
		parameter_channel_id=f.get_int();
		camera_id			=f.get_int();

		center_x=f.get_double();
		center_y=f.get_double();

		int view_port_number=f.get_int();
		
		if(view_port_number<0)
			view_port_number=0;
		viewport=new target_viewport[view_port_number];
		
		for(int i=0;i<view_port_number;i++){
			double x		=f.get_double();
			double y		=f.get_double();
			double width	=f.get_double();
			double height	=f.get_double();
			int method_id	=f.get_int();
			
			double clear_color[]=null;
			if(f.get_boolean()){
				clear_color=new double[4];
				for(int j=0,nj=clear_color.length;j<nj;j++)
					clear_color[j]=f.get_double();
			}
			viewport[i]=new target_viewport(x,y,width,height,method_id,0,clear_color);
		}
	}
}
