function construct_program_object(my_gl,my_parameter)
{
	this.gl=my_gl;
	this.parameter=my_parameter;
	this.render_program=new Array();
	this.sorted_render_program_id=new Array();
	
	this.destroy=function()
	{
		var program_number=this.render_program.length;
		for(var program_id=0;program_id<program_number;program_id++){
			if(typeof(this.render_program[program_id])!="object")
				continue;
			if(this.render_program[program_id]==null)
				continue;
			if(typeof(this.render_program[program_id].shader_program.destroy)=="function"){
				this.render_program[program_id].shader_program.destroy(this.gl,
						this.render_program[program_id].shader_program,
						this.render_program[program_id],program_id);
				this.render_program[program_id].shader_program.destroy=null;
			}
			if(typeof(this.render_program[program_id].destroy)=="function"){
				this.render_program[program_id].destroy(this.gl,
						this.render_program[program_id].shader_program,
						this.render_program[program_id],program_id);
				this.render_program[program_id].destroy=null;
			}
			
			this.gl.deleteProgram(this.render_program[program_id].shader_program);
			
			this.render_program[program_id].decode_function	=null;
			this.render_program[program_id].attribute_map	=null;
			this.render_program[program_id].draw_function	=null;
			this.render_program[program_id].shader_program	=null;
			this.render_program[program_id].shader_program	=null;
			this.render_program[program_id].render_data		=null;
		}
		this.gl							=null;
		this.parameter					=null;
		this.render_program				=null;
		this.sorted_render_program_id	=null;
		
		this.compile_program			=null;
		this.destroy					=null;
	}
	
	this.create_sorted_render_program_id=function()
	{
		this.sorted_render_program_id=new Array(this.render_program.length);
		for(var i=0,ni=this.sorted_render_program_id.length;i<ni;i++)
			this.sorted_render_program_id[i]=i;
		for(var i=0,ni=this.sorted_render_program_id.length-1;i<ni;i++){
			var index_id_0		=this.sorted_render_program_id[i+0];
			var index_id_1		=this.sorted_render_program_id[i+1];
			var render_name_0	=this.render_program[index_id_0].render_name;
			var render_name_1	=this.render_program[index_id_1].render_name;
			if(render_name_0>render_name_1){
				this.sorted_render_program_id[i+0]=index_id_1;
				this.sorted_render_program_id[i+1]=index_id_0;
				if(i>0)
					i-=2;
			}
		}
	}
	
	this.get_program_object_by_name=function(my_render_name)
	{
		var start_pointer=0;
		var end_pointer=this.sorted_render_program_id.length-1;
		while(start_pointer<=end_pointer){
			var middle_pointer=Math.floor(start_pointer+end_pointer);
			var index_id=this.sorted_render_program_id[middle_pointer];
			if(this.render_program[index_id].render_name<my_render_name)
				start_pointer=middle_pointer+1;
			else if(this.render_program[index_id].render_name>my_render_name)
				end_pointer=middle_pointer-1;
			else
				return this.render_program[index_id];
		}
		return null;
	}
	
	this.compile_program=function(program_id,my_render_name,my_permanent_render_id,
			my_decode_function,my_attribute_map,my_draw_function,my_vertex_program,my_fragment_program,
			my_geometry_program,my_tess_control_Program,my_tess_evalueprogram,my_shader_data)
	{
		var my_vertex=this.gl.createShader(this.gl.VERTEX_SHADER);
		this.gl.shaderSource(my_vertex,my_vertex_program);
		this.gl.compileShader(my_vertex);
		if (!(this.gl.getShaderParameter(my_vertex,this.gl.COMPILE_STATUS))){
		   alert("vertex shader program: "+my_permanent_render_id.toString());
		   alert(this.gl.getShaderInfoLog(my_vertex));
		   alert(my_vertex_program);
		}

		var my_fragment=this.gl.createShader(this.gl.FRAGMENT_SHADER);
		this.gl.shaderSource(my_fragment,my_fragment_program);
		this.gl.compileShader(my_fragment);
		if(!(this.gl.getShaderParameter(my_fragment,this.gl.COMPILE_STATUS))){
		   	alert("fragment shader program: "+my_permanent_render_id.toString());
		  	alert(this.gl.getShaderInfoLog(my_fragment));
		  	alert(my_fragment_program);
		}
		
		var my_shader_program=this.gl.createProgram();

		this.gl.attachShader(my_shader_program, my_vertex);
	    this.gl.attachShader(my_shader_program, my_fragment);     	
	    this.gl.linkProgram (my_shader_program);
	    
		this.gl.deleteShader(my_vertex);
		this.gl.deleteShader(my_fragment);

	    if(this.gl.getProgramParameter(my_shader_program,this.gl.LINK_STATUS)==null){
	       	alert("Could not link shaders: "+my_permanent_render_id.toString());
	       	alert(this.gl.getProgramInfoLog(my_shader_program));
	    }
	    
	    var system_index_id		=this.gl.getUniformBlockIndex(my_shader_program,"system_information");
    	var target_index_id		=this.gl.getUniformBlockIndex(my_shader_program,"target_information");
    	var pass_index_id		=this.gl.getUniformBlockIndex(my_shader_program,"pass_information");
    	var component_index_id	=this.gl.getUniformBlockIndex(my_shader_program,"component_information");
    	
    	var max_uniform_buffer_bindings=this.gl.getParameter(this.gl.MAX_UNIFORM_BUFFER_BINDINGS);
    	
    	this.gl.uniformBlockBinding(my_shader_program,system_index_id,		max_uniform_buffer_bindings-1);
    	this.gl.uniformBlockBinding(my_shader_program,target_index_id,		max_uniform_buffer_bindings-2);
    	this.gl.uniformBlockBinding(my_shader_program,pass_index_id,		max_uniform_buffer_bindings-3);
    	this.gl.uniformBlockBinding(my_shader_program,component_index_id,	max_uniform_buffer_bindings-4);
	    
    	this.render_program[program_id]=
		{
			decode_function		:	my_decode_function,
			attribute_map		:	my_attribute_map,
			draw_function		:	my_draw_function,
			shader_program		:	my_shader_program,
			shader_data			:	my_shader_data,
			render_name			:	my_render_name,
			permanent_render_id	:	my_permanent_render_id
		};
	};
};