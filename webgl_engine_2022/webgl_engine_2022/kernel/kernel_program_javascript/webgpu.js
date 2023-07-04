async function create_webgpu(my_canvas)
{
	var webgpu=new Object();
	var devicePixelRatio = window.devicePixelRatio || 1;

	try{
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
			webgpu.canvas [i]		=(typeof(my_canvas[i])!="string")?my_canvas[i]:(document.getElementById(my_canvas[i]));
			webgpu.canvas [i].width = webgpu.canvas[i].clientWidth * devicePixelRatio;
			webgpu.canvas [i].height= webgpu.canvas[i].clientHeight * devicePixelRatio;
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
		
		return webgpu;

	}catch(e){
		alert("Could not create WebGPU context:"+e.toString());
		return null;
	}
}