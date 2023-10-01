function construct_process_bar(my_webgpu,my_user_process_bar_function,my_process_bar_url)
{
	this.webgpu							=my_webgpu;
	this.process_bar_function			=my_user_process_bar_function;
	this.process_bar_url 				=my_process_bar_url;
	this.process_bar_data 				=null;
	
	this.process_bar_caption			="";
	this.process_bar_current			=0;
	this.process_bar_current_last		=0;
	this.process_bar_max				=1;
	this.process_bar_time_length		=0;
	this.process_bar_engine_time_length	=0;
	this.time_unit						="";
	this.set_time						=new Date().getTime();

	if(typeof(this.process_bar_function)!="function"){
		this.process_bar_function	=function(
				webgpu_canvas_id,				//绘制结束后，绘制结果拷贝到哪个canvas 
				process_bar_canvas,				//绘制进度条的画布canvas
				process_bar_ctx,				//绘制进度条的2D上下文
				process_bar_caption,			//进度条当前进度标题，该标题和语言有关，目前系统中仅仅配置了中文和英文相关标题
				process_bar_value,				//进度条当前进度，取值范围0.00~1.00
				process_bar_time_length,		//进度条当前进度经过的时间，单位是毫秒
				process_bar_engine_time_length,	//进度条所有进度经过的时间，单位是毫秒
				time_unit)						//和语言有关时间单位标题，比如中文是秒，英文是second
		{
			var p_separator=Math.round(process_bar_canvas.width*process_bar_value);
			process_bar_ctx.fillStyle="rgb(127,127,127)";
			process_bar_ctx.fillRect(0,			0,	p_separator,				process_bar_canvas.height);
			process_bar_ctx.fillStyle="rgb(255,255,255)";
			process_bar_ctx.fillRect(p_separator,	0,	process_bar_canvas.width,	process_bar_canvas.height);
			
			if((process_bar_value=(Math.round(1000.0*process_bar_value)/10.0).toString()).indexOf(".")<0)
				process_bar_value+=".0";
			var display_value=process_bar_caption+":"+process_bar_value+"%,";
			display_value+=(Math.round(process_bar_time_length/1000.0)).toString()+time_unit+",";
			display_value+=(Math.round(process_bar_engine_time_length/1000.0)).toString()+time_unit;
			
			process_bar_ctx.font			="bold 48px Arial";
			process_bar_ctx.textBaseline	="middle";
			process_bar_ctx.fillStyle		="rgb(192,192,192)";
			process_bar_ctx.textAlign		="center";		
			process_bar_ctx.fillText(display_value,process_bar_canvas.width/2.0,process_bar_canvas.height/2.0);
			return;
		}
	}
	this.destroy=function()
	{
		this.webgpu							=null;
		this.process_bar_function			=null;
		this.process_bar_url		 		=null;
		this.process_bar_data		 		=null;
	
		this.process_bar_caption			=null;
		this.process_bar_current			=0;
		this.process_bar_current_last		=0;
		this.process_bar_max				=1;
		this.process_bar_time_length		=0;
		this.process_bar_engine_time_length	=0;
		this.time_unit						=null;
		this.set_time						=0;
		
		this.start							=null;
		this.draw_process_bar				=null;
		this.request_process_bar_data		=null;
	};	
	this.draw_process_bar=async function()
	{
		while(this.process_bar_data!=null){
			var p=(new Date().getTime()-this.set_time)/this.process_bar_data.show_process_bar_interval;
			if(p<0.0)
				p=0.0;
			else if(p>1.0){
				this.process_bar_current_last=this.process_bar_current;
				p=1.0;
			}
			
			for(var i=0,ni=this.webgpu.canvas.length;i<ni;i++){
				this.webgpu.canvas_2d.width		=this.webgpu.canvas[i].width;
				this.webgpu.canvas_2d.height	=this.webgpu.canvas[i].height;
			
				try{
					this.process_bar_function(i,
						this.webgpu.canvas_2d,this.webgpu.context_2d,this.process_bar_caption,
						(this.process_bar_current_last*(1.0-p)+this.process_bar_current*p)/this.process_bar_max,
						this.process_bar_time_length,this.process_bar_engine_time_length,this.time_unit);
				}catch(e){
					console.log("Process bar drawing function error:"+e.toString);
					continue;
				}
				this.webgpu.device.queue.copyExternalImageToTexture(
					{
						source	:this.webgpu.canvas_2d
					},
					{
						texture	:this.webgpu.context[i].getCurrentTexture()
					},
					{
						width	:	this.webgpu.canvas[i].width,
						height	:	this.webgpu.canvas[i].height
					}
				);
			}
			await new Promise(resolve=>window.requestAnimationFrame(resolve));
		}
	};
	
	this.request_process_bar_data=async function()
	{
		var process_bar=this.process_bar_url+"&command=data";
		process_bar+="&container="	+this.process_bar_data.container_id;
		process_bar+="&process_bar="+this.process_bar_data.process_bar_id;
			
		while(this.process_bar_data!=null){
			var start_time=new Date().getTime();
			var data_promise=await fetch(process_bar);
			if(!(data_promise.ok)){
				this.destroy();
				alert("render_show_process_bar fail:"+data_promise.status);
				return;
			}
			var response_data;
			try{
				response_data = await data_promise.json();
			}catch(e){
				this.destroy();
				alert("parse render_show_process_bar fail:"+e.toString());
				return;
			}

			this.process_bar_current_last		=this.process_bar_current;
		
			this.process_bar_caption			=response_data.caption;
			this.process_bar_current			=response_data.current;
			this.process_bar_max				=response_data.max;
			this.process_bar_time_length		=response_data.time_length;
			this.process_bar_engine_time_length	=response_data.engine_time_length;
			this.time_unit						=response_data.time_unit;
		
			if(this.process_bar_current<0)
				this.process_bar_current=0;
			else if(this.process_bar_current>this.process_bar_max)
				this.process_bar_current=this.process_bar_max;
			if(this.process_bar_current_last>this.process_bar_current)
				this.process_bar_current_last=this.process_bar_current;;
			this.set_time						=new Date().getTime();
			
			var time_length;
			if((time_length=this.process_bar_data.show_process_bar_interval-(this.set_time-start_time))>0)
				await new Promise(resolve=>setTimeout(resolve,time_length));
		};
	};
	
	this.start=async function()
	{
		var process_bar_promise=await fetch(this.process_bar_url+"&command=request");
		if(!(process_bar_promise.ok)){
			alert("render_main create process bar error,status is "+process_bar_promise.status);
			alert(this.process_bar_url+"&command=request");
			return null;
		}
		try{
			this.process_bar_data = await process_bar_promise.json();
		}catch(e){
			alert("parse process_bar_object error:"+e.toString());
			alert(this.process_bar_url+"&command=request");
			return null;
		}
		this.request_process_bar_data();
		this.draw_process_bar();
	
		return this.process_bar_data;
	};
}