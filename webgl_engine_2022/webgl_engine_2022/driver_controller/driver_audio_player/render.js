function new_render_driver(
	render_id,render_name,init_data,text_array,shader_code,render)
{
	this.new_part_driver=construct_part_driver;
	
	this.destroy=function()
	{
		this.new_part_driver=null;
	}
}