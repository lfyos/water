async function create_webgpu(my_canvas)
{
	if(!(Array.isArray(my_canvas)))
		my_canvas=[my_canvas];
	for(var i=0,ni=my_canvas.length;i<ni;i++)
		if(typeof(my_canvas[i])=="string")
			my_canvas[i]=document.getElementById(my_canvas[i]);
		
	var webgpu=new Object();
	var devicePixelRatio = window.devicePixelRatio || 1;

	webgpu.current_canvas_id	=0;
		
	webgpu.gpu					= await navigator.gpu;
	webgpu.adapter				= await webgpu.gpu.requestAdapter();
	webgpu.device				= await webgpu.adapter.requestDevice();
	webgpu.command_encoder		= null;
	webgpu.render_pass_encoder	= null;
	webgpu.compute_pass_encoder	= null;
	
	webgpu.canvas				=new Array(my_canvas.length);
	webgpu.context				=new Array(my_canvas.length);
	for(var i=0,ni=webgpu.canvas.length;i<ni;i++){
		webgpu.canvas [i]		= my_canvas[i];
		webgpu.canvas [i].width = webgpu.canvas[i].clientWidth * devicePixelRatio;
		webgpu.canvas [i].height= webgpu.canvas[i].clientHeight* devicePixelRatio;
		webgpu.context[i]		= webgpu.canvas[i].getContext("webgpu");
		webgpu.context[i].configure({
			device	 :	webgpu.device,
			format	 :	webgpu.gpu.getPreferredCanvasFormat(),
			usage	 :	GPUTextureUsage.COPY_DST+GPUTextureUsage.RENDER_ATTACHMENT,
			alphaMode:	"premultiplied"
		});
	}

	webgpu.canvas_2d		=document.createElement("canvas");
	webgpu.canvas_2d.width	=webgpu.canvas[0].width;
	webgpu.canvas_2d.height	=webgpu.canvas[0].height;
	webgpu.context_2d		=webgpu.canvas_2d.getContext("2d");
	
	webgpu.destroy=function()
	{
		this.gpu					= null;
		this.adapter				= null;
		this.device					= null;
		this.command_encoder		= null;
		this.render_pass_encoder	= null;
		this.compute_pass_encoder	= null;
		
		if(this.canvas!=null){
			for(var i=0,ni=this.canvas.length;i<ni;i++)
				this.canvas [i]= null;
			this.canvas=null;
		}
		if(this.context!=null){
			for(var i=0,ni=this.context.length;i<ni;i++)
				this.context [i]= null;
			this.context= null;
		}
	}
	return webgpu;
}
