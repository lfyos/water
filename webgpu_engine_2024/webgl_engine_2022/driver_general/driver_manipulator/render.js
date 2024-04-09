function new_render_driver(	render_id,render_name,init_data,shader_code,text_array,render)
{
	this.new_part_driver=construct_part_driver;
	this.method_render_flag=[];
	
	this.destroy=function()
	{
		this.new_part_driver=null;
		this.method_render_flag=null;
	}
}