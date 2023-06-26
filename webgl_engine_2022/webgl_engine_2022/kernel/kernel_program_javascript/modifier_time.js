function construct_modifier_time_parameter(modifier_container_number)
{
	this.delay_time_length		=0;
	this.webserver_current_time	=0;
	
	this.timer_adjust_value		=new Array(modifier_container_number);
	
	for(var i=0;i<modifier_container_number;i++)
		this.timer_adjust_value[i]	=0;
		
	this.caculate_current_time=function(index_id)
	{
		return this.webserver_current_time-this.timer_adjust_value[index_id];
	};
	this.modify_parameter=function(response_data)
	{
		this.delay_time_length		 =response_data[1];
		this.webserver_current_time	+=Math.round(response_data[2]);

		for(var i=3,ni=response_data.length;i<ni;){
			var index_id					  =response_data[i++];
			this.timer_adjust_value[index_id]+=response_data[i++];
		}
	};
	
	this.destroy=function()
	{
		this.timer_adjust_value		=null;
		this.caculate_current_time	=null;
		this.modify_parameter		=null;
		this.destroy				=null;
	};
};
