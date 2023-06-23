async function create_webgpu(my_canvas)
{
	var webgpu=new Object();
	const devicePixelRatio = window.devicePixelRatio || 1;
	try{
		webgpu.canvas				=(typeof(my_canvas)!="string")?my_canvas:(document.getElementById(my_canvas));
		webgpu.canvas.width 		= webgpu.canvas.clientWidth * devicePixelRatio;
		webgpu.canvas.height		= webgpu.canvas.clientHeight * devicePixelRatio;
		webgpu.context				= webgpu.canvas.getContext("webgpu");
		
		webgpu.gpu					= await navigator.gpu;
		webgpu.adapter				= await webgpu.gpu.requestAdapter();
		webgpu.device				= await webgpu.adapter.requestDevice();
		webgpu.command_encoder		= null;
		webgpu.render_pass_encoder	= null;
		webgpu.compute_pass_encoder	= null;
	
		webgpu.context.configure(
			{
				device		:	webgpu.device,
				format		:	webgpu.gpu.getPreferredCanvasFormat(),
				usage		:	GPUTextureUsage.COPY_DST+GPUTextureUsage.RENDER_ATTACHMENT,
				alphaMode	:	"premultiplied"
			});
		webgpu.canvas_2d		=document.createElement("canvas");
		webgpu.canvas_2d.width	=webgpu.canvas.width;
		webgpu.canvas_2d.height	=webgpu.canvas.height;
		webgpu.context_2d		=webgpu.canvas_2d.getContext("2d");
	
		return webgpu;

	}catch(e){
		alert("Could not create WebGPU context:"+e.toString());
		return null;
	}
}