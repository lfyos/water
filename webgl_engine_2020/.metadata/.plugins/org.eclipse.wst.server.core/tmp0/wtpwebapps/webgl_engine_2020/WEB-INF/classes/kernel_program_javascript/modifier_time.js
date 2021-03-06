function construct_modifier_time_parameter(modifier_container_number)
{
	this.delay_time_length		=0;
	this.webserver_current_time	=0;
	this.modifier_time_parameter=new Array(modifier_container_number);
	
	for(var i=0;i<modifier_container_number;i++)
		this.modifier_time_parameter[i]=
		{
			timer_version		:	0,
			timer_adjust_value	:	0,
			speed				:	1,
			update_flag			:	true
		};

	this.modify_parameter=function(response_data)
	{
		this.delay_time_length		=response_data[0];
		this.webserver_current_time+=Math.round(response_data[1]);

		for(var p=response_data[2],i=0,ni=p.length;i<ni;i++){
			var index_id=p[i][0];
			var old_par=this.modifier_time_parameter[index_id];
			var new_par=new Object();
			
			new_par.timer_version		=old_par.timer_version		+Math.round(p[i][1]);
			new_par.timer_adjust_value	=old_par.timer_adjust_value	+Math.round(p[i][2]);
			new_par.speed				=(p[i].length<=3)?old_par.speed:p[i][3];
			new_par.update_flag			=true;
			
			this.modifier_time_parameter[index_id]=new_par;
		}
		return this.modifier_time_parameter;
	};
	this.caculate_current_time=function(index_id)
	{
		var p=this.modifier_time_parameter[index_id];
		return p.speed*(this.webserver_current_time-p.timer_adjust_value);
	};
};
