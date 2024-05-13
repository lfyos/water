function construct_part_driver(init_data,part_object,render_driver,render)
{
	this.render_material=render_driver.render_material;
	this.material_bindgroup_flag=true;
	this.material_bindgroup_array=new Array();
	
	part_object.material[0].material.push(render_driver.render_material.selected_material);

	this.create_bind_group=async function(part_object,render_driver,render)
	{
		if(render.terminate_flag)
			return;
			
		for(var p,i=0,ni=part_object.material[0].material.length;i<ni;i++){
			var my_material=part_object.material[0].material[i];
			
			var int_buffer_data=new Array();
			
			p=(typeof(p=my_material.vertex_color_type)!="number")?2:p;
			int_buffer_data.push(p);
			
			p=(typeof(p=my_material.fragment_color_type)!="number")?0:p;
			int_buffer_data.push(p);
			
			int_buffer_data.push(0,1)
			
			var float_buffer_data=new Array();
			
			p=(typeof(p=my_material.vertex_color_parameter)=="undefined")?[0,0,0,1]:p;
			float_buffer_data.push(p[0],p[1],p[2],p[3]);
			
			p=(typeof(p=my_material.fragment_color_parameter)=="undefined")?[0,0,0,1]:p;
			float_buffer_data.push(p[0],p[1],p[2],p[3]);
			
			p=(typeof(p=my_material.color)=="undefined")?[1,1,1,1]:p;
			float_buffer_data.push(p[0],p[1],p[2],p[3]);
			
			p=(typeof(p=my_material.ambient)=="undefined")?[0,0,0,1]:p;
			float_buffer_data.push(p[0],p[1],p[2],p[3]);
			
			p=(typeof(p=my_material.diffuse)=="undefined")?[0,0,0,1]:p;
			float_buffer_data.push(p[0],p[1],p[2],p[3]);
			
			p=(typeof(p=my_material.specular)=="undefined")?[0,0,0,1]:p;
			float_buffer_data.push(p[0],p[1],p[2],p[3]);
			
			p=(typeof(p=my_material.emission)=="undefined")?[0,0,0,1]:p;
			float_buffer_data.push(p[0],p[1],p[2],p[3]);
			
			p=(typeof(p=my_material.shininess)=="undefined")?[0,0,0,1]:[p,0,0,1];
			float_buffer_data.push(p[0],p[1],p[2],p[3]);
			
			p=[	1,0,0,0,		0,1,0,0,	0,0,1,0,	0,0,0,1,
				1,0,0,0,		0,1,0,0,	0,0,1,0,	0,0,0,1,
				1,0,0,0,		0,1,0,0,	0,0,1,0,	0,0,0,1,
				1,0,0,0,		0,1,0,0,	0,0,1,0,	0,0,0,1
			];
			var my_texture_array=new Array();
					
			if(Array.isArray(my_material.texture))
				for(var j=0,nj=my_material.texture.length;(j<nj)&&(j<4);j++){
					for(var k=0;k<16;k++)
						p[16*j+k]=my_material.texture[j].matrix[k];
					try{
						if(render.terminate_flag)
							break;
						var my_blob=await render.caller.call_server_part(
										part_object.render_id,part_object.part_id,
										[["file",my_material.texture[j].texture_file]],"blob");
						if(render.terminate_flag)
							break;
						var my_imageBitmap=await createImageBitmap(my_blob);
						if(render.terminate_flag)
							break;
						var my_texture=render.webgpu.device.createTexture(
								{
									size:
									{
										width	:	my_imageBitmap.width,
										height	:	my_imageBitmap.height
									},
									format		:	"rgba16float",
									usage		:	 GPUTextureUsage.TEXTURE_BINDING 
													|GPUTextureUsage.COPY_DST
													|GPUTextureUsage.RENDER_ATTACHMENT
						    	});
						render.webgpu.device.queue.copyExternalImageToTexture(
							{
								source	:	my_imageBitmap
							},
							{
								texture	:	my_texture
							},
							{
								width	:	my_imageBitmap.width,
								height	:	my_imageBitmap.height
							});
						my_texture_array.push(my_texture);
					}catch(e){
						my_texture_array.push(render_driver.tmp_texture);
						console.log(part_object.information.system_name
							+"	download texture picture fail:	"
							+my_material.texture[j].texture_file);
					}
				}
			if(render.terminate_flag){
				for(var j=0,nj=my_texture_array.length;j<nj;j++)
					my_texture_array[j].destroy();
				return;
			}

			float_buffer_data=float_buffer_data.concat(p).concat(
					render_driver.render_material.light_color_factor);
	
			var int_buffer_length	=int_buffer_data.  length*Int32Array.  BYTES_PER_ELEMENT;
			var float_buffer_length	=float_buffer_data.length*Float32Array.BYTES_PER_ELEMENT;
			var total_buffer_length	=int_buffer_length+float_buffer_length;
				
			var my_parameter_buffer=render.webgpu.device.createBuffer(
			{
				size	:	total_buffer_length,
				usage	:	GPUBufferUsage.COPY_DST|GPUBufferUsage.UNIFORM 
			});
			render.webgpu.device.queue.writeBuffer(my_parameter_buffer,
				0,				  new Int32Array(int_buffer_data));
			render.webgpu.device.queue.writeBuffer(my_parameter_buffer,
				int_buffer_length,new Float32Array(float_buffer_data));
			
			var my_bindgroup_entries=[
				{	// material_information
					binding		:	0,
					resource	:
					{
						buffer	:	my_parameter_buffer,
						size	:	total_buffer_length
					}
				},
				{	//texture 1
					binding		:	1,
					resource	:	(my_texture_array.length<=0)
										?(render_driver.tmp_texture.createView())
										:(my_texture_array[0].createView())
				},
				{	//texture 2
					binding		:	2,
					resource	:	(my_texture_array.length<=1)
										?(render_driver.tmp_texture.createView())
										:(my_texture_array[1].createView())
				},
				{	//texture 3
					binding		:	3,
					resource	:	(my_texture_array.length<=2)
										?(render_driver.tmp_texture.createView())
										:(my_texture_array[2].createView())
				},
				{	//texture 4
					binding		:	4,
					resource	:	(my_texture_array.length<=3)
										?(render_driver.tmp_texture.createView())
										:(my_texture_array[3].createView())
				},
				{
					//sampler_1
					binding		:	5,
					resource	:	render.webgpu.device.createSampler(
						(my_texture_array.length>0)
						?my_material.texture[0].parameter
						:{
							addressModeU	:	"mirror-repeat",
							addressModeV	:	"mirror-repeat",
							magFilter		:	"nearest",
							minFilter		:	"nearest",
							mipmapFilter	:	"nearest"
						})
				},
				{
					//sampler_2
					binding		:	6,
					resource	:	render.webgpu.device.createSampler(
						(my_texture_array.length>1)
						?my_material.texture[1].parameter
						:{
							addressModeU	:	"mirror-repeat",
							addressModeV	:	"mirror-repeat",
							magFilter		:	"nearest",
							minFilter		:	"nearest",
							mipmapFilter	:	"nearest"
						})
				},
				{
					//sampler_3
					binding		:	7,
					resource	:	render.webgpu.device.createSampler(
						(my_texture_array.length>2)
						?my_material.texture[2].parameter
						:{
							addressModeU	:	"mirror-repeat",
							addressModeV	:	"mirror-repeat",
							magFilter		:	"nearest",
							minFilter		:	"nearest",
							mipmapFilter	:	"nearest"
						})
				},
				{
					//sampler_4
					binding		:	8,
					resource	:	render.webgpu.device.createSampler(
						(my_texture_array.length>3)
						?my_material.texture[3].parameter
						:{
							addressModeU	:	"mirror-repeat",
							addressModeV	:	"mirror-repeat",
							magFilter		:	"nearest",
							minFilter		:	"nearest",
							mipmapFilter	:	"nearest"
						})
				}
			];
			var my_bindgroup=render.webgpu.device.createBindGroup(
				{
					layout	:	render_driver.material_bindgroup_layout,
					entries	:	my_bindgroup_entries
				});
			this.material_bindgroup_array[i]={
				texture_array	:	my_texture_array,
				buffer			:	my_parameter_buffer,
				bindgroup		:	my_bindgroup
			};
		};
		this.material_bindgroup_flag=false;
	}
	this.decode_vertex_data=function(request_type_string,buffer_object_data,part_object)
	{
		var	p=render.system_call_processor.default_vertex_data_decoder;
		var	new_buffer_object_data=p.voxel(request_type_string,buffer_object_data,part_object);
		p.modify_item_size(new_buffer_object_data,this.render_material.array_stride);
		return new_buffer_object_data;
	}
	
	this.new_component_driver=construct_component_driver;
	
	this.destroy=function()
	{
		if(this.material_bindgroup_array!=null){
			for(var i=0,ni=this.material_bindgroup_array.length;i<ni;i++){
				if(this.material_bindgroup_array[i].texture_array!=null)
					for(var j=0,nj=this.material_bindgroup_array[i].texture_array.length;j<nj;j++)
						if(this.material_bindgroup_array[i].texture_array[j]!=null){
							this.material_bindgroup_array[i].texture_array[j].destroy();
							this.material_bindgroup_array[i].texture_array[j]=null;
						}
				if(this.material_bindgroup_array[i].buffer!=null){
					this.material_bindgroup_array[i].buffer.destroy();
					this.material_bindgroup_array[i].buffer	=null;
				}
			}
		}
	}
	this.create_bind_group(part_object,render_driver,render);
	
	return;
}
