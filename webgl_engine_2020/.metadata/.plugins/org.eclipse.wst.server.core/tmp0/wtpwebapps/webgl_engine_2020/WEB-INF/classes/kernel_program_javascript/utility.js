function construct_render_utility(my_render_instance)
{
	this.render_instance=my_render_instance;
	
	this.get_pinpoint=function()
	{
		var target_id			=this.render_instance.current.target_id;
		var target_viewport_id	=this.render_instance.current.target_viewport_id;
		var viewport			=this.render_instance.target_buffer[target_id][1];
		
		if(target_viewport_id>=0)
			if(target_viewport_id<(viewport.length)){
				var tv_x		=viewport[target_viewport_id][0];
				var tv_y		=viewport[target_viewport_id][1];
				var tv_width	=viewport[target_viewport_id][2];
				var tv_height	=viewport[target_viewport_id][3];

				return {
					x		:	2.0*(this.render_instance.view.x-tv_x)/tv_width-1.0,
					y		:	2.0*(this.render_instance.view.y-tv_y)/tv_height-1.0,
					aspect	:	this.render_instance.view.aspect*tv_width/tv_height,
					viewport:	target_viewport_id,
					target	:	target_id
				};
			};
		return null;
	};
	this.destroy_texture_image=function(my_texture_object)
	{
		this.render_instance.gl.deleteTexture(my_texture_object);
		if(my_texture_object.image!=null){
			my_texture_object.image.onerror=null;
			my_texture_object.image.onload=null;
			my_texture_object.image=null;
		};
	};
	this.load_texture_image=function(my_src,old_texture_object)
	{
		var cur=this.render_instance;
		var gl =this.render_instance.gl;
		var texture_object=old_texture_object;

		if((typeof(texture_object)!="object")||(texture_object==null))
			texture_object=gl.createTexture();

		gl.bindTexture	(gl.TEXTURE_2D,			texture_object);
    	gl.pixelStorei	(gl.UNPACK_FLIP_Y_WEBGL,true);
    	gl.texParameteri(gl.TEXTURE_2D,			gl.TEXTURE_BASE_LEVEL,0);
    	gl.texParameteri(gl.TEXTURE_2D,			gl.TEXTURE_MAX_LEVEL, 0);
    	gl.texImage2D(gl.TEXTURE_2D,0,gl.RGBA,1,1,0,gl.RGBA,gl.UNSIGNED_BYTE,new Uint8Array([0,0,0,255]));
    	gl.bindTexture	(gl.TEXTURE_2D,			null);

    	if((my_src==null)||(typeof(my_src)!="string")){
    		texture_object.image=null;
    		texture_object.state="done";
    		return texture_object;
    	}
		texture_object.image = new Image();
		texture_object.image.crossOrigin="Anonymous";
		texture_object.image.onerror= function()
		{
	    	cur.buffer_object.current_loading_mesh_number--;
	    	texture_object.state="error";
		};
		texture_object.image.onload = function()
		{
			cur.buffer_object.current_loading_mesh_number--;
			cur.gl.bindTexture	(cur.gl.TEXTURE_2D,texture_object);
			cur.gl.texImage2D	(cur.gl.TEXTURE_2D,0,cur.gl.RGBA,
					cur.gl.RGBA,cur.gl.UNSIGNED_BYTE,texture_object.image);
			cur.gl.bindTexture	(cur.gl.TEXTURE_2D,null);
			texture_object.state="done";
	    }; 
	    cur.append_routine_function(
	    		function(my_render)
	    		{
	    			if(cur.buffer_object.test_busy()<=0)
	    				return true;
	    			texture_object.state="loading";
	    			texture_object.image.src=my_src;
	    			cur.buffer_object.current_loading_mesh_number++;
					return false;
	    		},"wait loading texture image!"
	    );
	    texture_object.state="waiting";
		return texture_object;
	};
	this.load_server_part_image=function(
			render_id_or_part_name,part_id_or_driver_id,part_parameter,old_texture_object)
	{
		return this.load_texture_image(
				this.render_instance.create_part_request_string(
						render_id_or_part_name,part_id_or_driver_id,part_parameter),
				old_texture_object);
	};
	this.load_server_component_image=function(
			component_name_or_id,component_driver_id,component_parameter,old_texture_object)
	{
		return this.load_texture_image(
			this.render_instance.create_component_request_string(
					component_name_or_id,component_driver_id,component_parameter),
			old_texture_object);
	};
	this.destroy_texture_video=function(my_video_object)
	{
		this.render_instance.gl.deleteTexture(my_video_object);
		if(typeof(my_video_object.video.srcObject)=="object")
			if(typeof(my_video_object.video.srcObject.stop)=="function")
				my_video_object.video.srcObject.stop();
		my_video_object.video.srcObject=null;
		my_video_object.video.src=null;
		my_video_object.video.pause();
		my_video_object.video=null;
	};
	this.load_texture_video=function(my_src,old_texture_object)
	{
		var texture_object;
		var gl=this.render_instance.gl;
		if((typeof(old_texture_object)=="object")&&(old_texture_object!=null))
			texture_object=old_texture_object;
		else
			texture_object=gl.createTexture();
		gl.bindTexture	(gl.TEXTURE_2D,texture_object);
	    gl.pixelStorei  (gl.UNPACK_FLIP_Y_WEBGL, true);
	    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_BASE_LEVEL,0);
    	gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAX_LEVEL, 0);
    	gl.texImage2D(gl.TEXTURE_2D,0,gl.RGBA,1,1,0,gl.RGBA,gl.UNSIGNED_BYTE,new Uint8Array([0,0,0,255]));
    	gl.bindTexture	(gl.TEXTURE_2D,null);

    	texture_object.video			=document.createElement("video");
    	texture_object.video.autoplay	="autoplay";
    	texture_object.video.controls	="controls";
    	texture_object.video.loop		="loop";
    	texture_object.video.volume		=1.0;
    	texture_object.video.crossOrigin="Anonymous";
    	
    	texture_object.video.oncanplay=function()
		{
    		texture_object.video.play();
    		texture_object.state="playing";
		};
    	if(my_src==null)
    		texture_object.state="done";
    	else{
    		texture_object.state="loading";
    		texture_object.video.src=my_src;
    	}	
    	return texture_object;
	};
	this.load_server_part_video=function(
			render_id_or_part_name,part_id_or_driver_id,part_parameter,old_texture_object)
	{
		return this.load_texture_video(
				this.render_instance.create_part_request_string(
						render_id_or_part_name,part_id_or_driver_id,part_parameter),
			old_texture_object);
	};
	this.load_server_component_video=function(
			component_name_or_id,driver_id,component_parameter,old_texture_object)
	{
		return this.load_texture_video(
			this.render_instance.create_component_request_string(
					component_name_or_id,driver_id,component_parameter),
			old_texture_object);
	};
	this.load_camera_video=function(my_constraints,old_texture_object)
	{
		var texture_object=this.load_texture_video(null,old_texture_object);
		texture_object.state="loading";
		navigator.mediaDevices.getUserMedia(my_constraints)
			.then(function(mediaStream)
			{
				texture_object.video.srcObject=mediaStream;
				texture_object.state="playing";
			})
			.catch(function(err)
			{
				alert("load_camera_video fail : "+err.toString());
				texture_object.state="error";
			});
		return texture_object;
	};
	this.bind_camera_video=function(texture_object)
	{
		var gl=this.render_instance.gl;
		gl.bindTexture(gl.TEXTURE_2D,texture_object);
		if(texture_object.state!="playing")
			return true;
		else{
			gl.texImage2D(gl.TEXTURE_2D,0,gl.RGBA,gl.RGBA,gl.UNSIGNED_BYTE,texture_object.video);
			return false;
		}
	};
	this.upload_string=function(str_content,str_url,complete_function)
	{
		try{
			var my_ajax=new XMLHttpRequest(),cur=this.render_instance;

			my_ajax.onreadystatechange=function()
			{
				if(my_ajax.readyState!=4)
					return;
				if(my_ajax.status!=200){
					if(cur.parameter.debug_mode_flag)
						alert("this.upload_string=function(str_content,str_url,complete_function) fail"+my_ajax.status);
					else
						console.log("this.upload_string=function(str_content,str_url,complete_function) fail"+my_ajax.status);
					return;
				}
				if(typeof(complete_function)=="function")
					complete_function(cur,my_ajax.responseText);
			};
			my_ajax.open("POST",str_url,true);
			my_ajax.setRequestHeader("Content-type","text/plain");
			my_ajax.send(str_content);
			return true;
		}catch(e){
			alert("upload_string fail!\n"+e.toString());
			return false;
		};
	};
	this.upload_canvas_image=function(uploaded_canvas,png_url,complete_function)
	{
		try{
			var my_ajax=new XMLHttpRequest(),cur=this.render_instance;

			my_ajax.onreadystatechange=function()
			{
				if(my_ajax.readyState!=4)
					return;
				if(my_ajax.status!=200){
					if(cur.parameter.debug_mode_flag)
						alert("this.upload_scene_image=function(png_url,complete_function) fail"+my_ajax.status);
					else
						console.log("this.upload_scene_image=function(png_url,complete_function) fail"+my_ajax.status);
					return;
				}
				if(typeof(complete_function)=="function")
					complete_function(cur,my_ajax.responseText);
			};
			my_ajax.open("POST",png_url,true);
			my_ajax.setRequestHeader("Content-type","image/png");
			my_ajax.send(uploaded_canvas.toDataURL());
			return true;
		}catch(e){
			alert("upload_scene_image fail!\n"+e.toString());
			return false;
		};
	};
	this.upload_scene_image=function(png_url,complete_function)
	{
		this.upload_canvas_image(this.render_instance.canvas,png_url,complete_function)
	};
	this.set_clear_fullscreen=function(set_clear_flag)
	{
		if(set_clear_flag){
			if(this.render_instance.canvas.requestFullScreen)
				this.render_instance.canvas.requestFullScreen();  
			else if(this.render_instance.canvas.requestFullscreen)
				this.render_instance.canvas.requestFullscreen();  
			else if(this.render_instance.canvas.mozRequestFullScreen) 
				this.render_instance.canvas.mozRequestFullScreen();  
			else if(this.render_instance.canvas.webkitRequestFullScreen)
				this.render_instance.canvas.webkitRequestFullScreen();
		}else{
			if(this.render_instance.canvas.exitFullScreen)
				this.render_instance.canvas.exitFullScreen();  
			else if(this.render_instance.canvas.exitFullscreen)
				this.render_instance.canvas.exitFullscreen();  
			else if(this.render_instance.canvas.mozCancelFullScreen) 
				this.render_instance.canvas.mozCancelFullScreen();  
			else if(this.render_instance.canvas.webkitCancelFullScreen)
				this.render_instance.canvas.webkitCancelFullScreen();
		};
	};
	this.decode_integer_from_pixel=function(pixels_0,pixels_1,pixels_2,pixels_3)
	{
		var my_id=0;
		my_id=128*my_id+pixels_3;
		my_id=128*my_id+pixels_2;
		my_id=128*my_id+pixels_1;
		my_id=128*my_id+pixels_0;
				
		if((my_id=my_id-1)<0)
			my_id=-1;
		return my_id;
	};
	this.decode_float_from_pixel=function (pixels_0,pixels_1,pixels_2,pixels_3)
	{
		var ret_val=0.0;
		ret_val=(ret_val/128.0)+pixels_3;
		ret_val=(ret_val/128.0)+pixels_2;
		ret_val=(ret_val/128.0)+pixels_1;
		ret_val=(ret_val/128.0)+pixels_0;
		ret_val=(ret_val/128.0);
		ret_val=2.0*ret_val-1.0;
			
		return (ret_val<(-1.0))?(-1.0):(ret_val>1.0)?1.0:ret_val;
	};
};