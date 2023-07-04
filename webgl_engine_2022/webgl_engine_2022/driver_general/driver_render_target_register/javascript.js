function my_create_part_driver(part_object,render_driver,render)
{
	this.begin_render_target=function(render_data,render)
	{
		render.webgpu.render_pass_encoder = render.webgpu.command_encoder.beginRenderPass(
		{
			colorAttachments: 
			[
				{
					view: render.webgpu.context[render.webgpu.current_canvas_id].getCurrentTexture().createView(),
					clearValue: { r: 0.0, g: 0.0, b: 0.0, a: 1.0 },
					loadOp: 'clear',
					storeOp: 'store'
				}
			]
		});
		var ret_val=
		[
			{
				method_id:	0
			},
			{
				method_id:	1
			},
			{
				method_id:	2
			},
			{
				method_id:	3
			},
			{
				method_id:	4
			}
		];
		return  ret_val;
	};
	
	this.destroy=function()
	{
	
	};
}

function main(	render_id,		render_name,
				init_data,		text_array,
				shader_code,	render)
{
	this.create_part_driver=my_create_part_driver;
	
	this.destroy=function()
	{
	
	};
}
