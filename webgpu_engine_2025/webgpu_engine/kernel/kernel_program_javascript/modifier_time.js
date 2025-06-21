function construct_modifier_time_parameter(modifier_container_number)
{
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
		this.webserver_current_time	+=response_data.shift();
		while(response_data.length>0){
			var index_id					  =response_data.shift();
			this.timer_adjust_value[index_id]+=response_data.shift();
		}
		var ret_val=new Array(this.timer_adjust_value.length);
		for(var i=0,ni=ret_val.length;i<ni;i++)
			ret_val[i]=this.caculate_current_time(i);
		return ret_val;
	};
};
