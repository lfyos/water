function construct_collector_loader_object(my_scene)
{
	this.scene=my_scene;
	this.collector_data=new Array();
	
	for(var i=0;i<8;i++)		
		this.collector_data[i]={
			collector_stack_version		:	0,
			collector_stack_data		:	null,
			is_loading_collector_flag	:	false
		};
	this.collector_url=function(simple_list_flag,single_collector_flag,location_flag)
	{
		return this.scene.url_with_channel+"&command=collector"
									+"&simple="		+(simple_list_flag		?"true":"false")
									+"&single="		+(single_collector_flag	?"true":"false")
									+"&location="	+(location_flag			?"true":"false");
	};
	this.load_collector=async function(simple_list_flag,single_collector_flag,location_flag)
	{
		var my_scene=this.scene;
		var epcd=this.collector_data[(simple_list_flag?0:1)+(single_collector_flag?0:2)+(location_flag?0:4)];
		
		while((my_scene.collector_stack_version!=epcd.collector_stack_version)&&(!(epcd.is_loading_collector_flag))){
			epcd.is_loading_collector_flag=true;
			
			var old_collector_stack_version=epcd.collector_stack_version;
			epcd.collector_stack_version=my_scene.collector_stack_version;
			
			var load_collector_url=this.collector_url(simple_list_flag,single_collector_flag,location_flag);
			
			var load_collector_promise=await fetch(load_collector_url,my_scene.fetch_parameter.load_collector);
			if((!(load_collector_promise.ok))||my_scene.terminate_flag){
				epcd.is_loading_collector_flag=false;
				epcd.collector_stack_version=old_collector_stack_version;
				
				if(!(my_scene.terminate_flag)){
					alert("request load_collector error,status is "+load_collector_promise.status);
					alert(load_collector_url);
				}

				return {
					done_flag		:	false,
					collector_data	:	epcd.collector_stack_data
				};
			}
			var success_flag,load_collector_response_data=null;
			try{
				load_collector_response_data=await load_collector_promise.json();
				success_flag=my_scene.terminate_flag?false:true;
			}catch(e){
				success_flag=false;
				alert("parse load_collector error,status is "+e.toString());
				alert(load_collector_url);
			}
			if(!success_flag){	
				epcd.is_loading_collector_flag=false;
				epcd.collector_stack_version=old_collector_stack_version;

				return {
					done_flag		:	false,
					collector_data	:	epcd.collector_stack_data
				};
			}
			epcd.is_loading_collector_flag=false;
			epcd.collector_stack_data=load_collector_response_data;
		};
		return {
			done_flag		:	epcd.is_loading_collector_flag?false:true,
			collector_data	:	epcd.collector_stack_data
		};
	}
}