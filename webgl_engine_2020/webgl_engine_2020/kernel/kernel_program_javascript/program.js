function construct_program_object(my_gl,my_parameter)
{
	this.gl=my_gl;
	this.parameter=my_parameter;
	this.render_program=new Array();
	
	this.compile_program=function(program_id,permanent_render_id,
			my_decode_function,my_draw_function,my_vertex_program,my_fragment_program,
			my_geometry_program,my_tess_control_Program,my_tess_evalueprogram)
	{
		var vertex=this.gl.createShader(this.gl.VERTEX_SHADER);
		this.gl.shaderSource(vertex,my_vertex_program);
		this.gl.compileShader(vertex);
		if (!(this.gl.getShaderParameter(vertex,this.gl.COMPILE_STATUS))){
		   	if(this.parameter.debug_mode_flag){
		   		alert("vertex shader program: "+permanent_render_id.toString());
		   		alert(this.gl.getShaderInfoLog(vertex));
		   		alert(my_vertex_program);
		   	}else{
		   		console.log("vertex shader program: "+permanent_render_id.toString());
		   		console.log(this.gl.getShaderInfoLog(vertex));
		   		console.log(my_vertex_program);
		   	}
		}
		var fragment=this.gl.createShader(this.gl.FRAGMENT_SHADER);
		this.gl.shaderSource(fragment,my_fragment_program);
		this.gl.compileShader(fragment);
		if(!(this.gl.getShaderParameter(fragment,this.gl.COMPILE_STATUS))){
		   	if(this.parameter.debug_mode_flag){
				alert("fragment shader program: "+permanent_render_id.toString());
				alert(this.gl.getShaderInfoLog(fragment));
				alert(my_fragment_program);
		   	}else{
		  		console.log("fragment shader program: "+permanent_render_id.toString());
		  		console.log(this.gl.getShaderInfoLog(fragment));
		  		console.log(my_fragment_program);
		   	}
		}
		
		var my_shader_program=this.gl.createProgram();

		this.gl.attachShader(my_shader_program, vertex);
	    this.gl.attachShader(my_shader_program, fragment);     	
	    this.gl.linkProgram (my_shader_program);

	    if(this.gl.getProgramParameter(my_shader_program,this.gl.LINK_STATUS)==null){
	       	if(this.parameter.debug_mode_flag){
				alert("Could not link shaders: "+permanent_render_id.toString());
				alert(this.gl.getProgramInfoLog(my_shader_program));
	       	}else{
	       		console.log("Could not link shaders: "+permanent_render_id.toString());
	       		console.log(this.gl.getProgramInfoLog(my_shader_program));
	       	}
	    }
	    
	    var system_index_id		=this.gl.getUniformBlockIndex(my_shader_program,"system_information");
    	var target_index_id		=this.gl.getUniformBlockIndex(my_shader_program,"target_information");
    	var pass_index_id		=this.gl.getUniformBlockIndex(my_shader_program,"pass_information")
    	var component_index_id	=this.gl.getUniformBlockIndex(my_shader_program,"component_information")
    	
    	var max_uniform_buffer_bindings=this.gl.getParameter(this.gl.MAX_UNIFORM_BUFFER_BINDINGS);
    	
    	this.gl.uniformBlockBinding(my_shader_program,system_index_id,		max_uniform_buffer_bindings-1);
    	this.gl.uniformBlockBinding(my_shader_program,target_index_id,		max_uniform_buffer_bindings-2);
    	this.gl.uniformBlockBinding(my_shader_program,pass_index_id,		max_uniform_buffer_bindings-3);
    	this.gl.uniformBlockBinding(my_shader_program,component_index_id,	max_uniform_buffer_bindings-4);
	    
    	this.render_program[program_id]=
		{
			decode_function	:	my_decode_function,
			draw_function	:	my_draw_function,
			shader_program	:	my_shader_program
		};
	};
};