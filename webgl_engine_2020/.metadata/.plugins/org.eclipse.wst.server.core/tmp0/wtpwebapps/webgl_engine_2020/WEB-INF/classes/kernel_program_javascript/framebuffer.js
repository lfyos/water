function construct_framebuffer(gl,width,height,render_target_number) 
{
	this.width					=width;
	this.height					=height;
	this.render_target_number	=render_target_number;
	  
	this.frame=gl.createFramebuffer();
	gl.bindFramebuffer(gl.FRAMEBUFFER, this.frame);
    
	this.depth_renderbuffer=gl.createRenderbuffer();
	gl.bindRenderbuffer(gl.RENDERBUFFER,this.depth_renderbuffer);
	gl.renderbufferStorage(gl.RENDERBUFFER,gl.DEPTH_COMPONENT32F,width,height);
	gl.framebufferRenderbuffer(gl.FRAMEBUFFER,gl.DEPTH_ATTACHMENT,gl.RENDERBUFFER,this.depth_renderbuffer);
	 
	this.texture=new Array();
	var draw_buffer_array=new Array();
	for(var i=0;i<render_target_number;i++){
		this.texture[i]=gl.createTexture();
		gl.bindTexture(gl.TEXTURE_2D,this.texture[i]);
		gl.texParameteri(gl.TEXTURE_2D,gl.TEXTURE_BASE_LEVEL,0);
		gl.texParameteri(gl.TEXTURE_2D,gl.TEXTURE_MAX_LEVEL, 0);
		gl.texImage2D(gl.TEXTURE_2D,0,gl.RGBA,width,height,0,gl.RGBA,gl.UNSIGNED_BYTE,null);
		gl.framebufferTexture2D(gl.FRAMEBUFFER,gl.COLOR_ATTACHMENT0+i,gl.TEXTURE_2D,this.texture[i],0);
		draw_buffer_array[draw_buffer_array.length]=gl.COLOR_ATTACHMENT0+i;
	}
	gl.drawBuffers(draw_buffer_array);

	gl.bindFramebuffer(gl.FRAMEBUFFER,null);
	gl.bindRenderbuffer(gl.RENDERBUFFER,null);
	gl.bindTexture(gl.TEXTURE_2D,null);
	
    this.delete_framebuffer=function(gl)
    {
    	gl.deleteFramebuffer	(this.frame);
    	gl.deleteRenderbuffer	(this.depth_renderbuffer);
    	for(var i=0,ni=this.texture.length;i<ni;i++)
    		gl.deleteTexture	(this.texture[i]);
    };
};
