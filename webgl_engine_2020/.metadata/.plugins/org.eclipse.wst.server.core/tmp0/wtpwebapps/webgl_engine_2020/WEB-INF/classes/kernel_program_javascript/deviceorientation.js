
function construct_deviceorientation(my_computer)
{
	this.computer=my_computer;
	var my_deviceorientation=this;
	
	this.gps={
			x:				0,
			y:				0,
			z:				0,

			latitude:		0,
			longitude:		0,
			altitude:		0,
				
			version:		0,
			watch_id:		0
	};
	this.deviceorientation={
			alpha			:		0,
			beta			:		0,
			gamma			:		0,
			
			version			:		1,
			up_date_version	:		0,
			
			transform_matrix:		this.computer.create_move_rotate_matrix(0,0,0,0,0,0),
			
			matrix			:		this.computer.create_move_rotate_matrix(0,0,0,0,0,0)
	};
	
	this.acceleration={
			translation	:
			{
				x:	0,
				y:	0,
				z:	0
			},
			rotation	:
			{
				x:	0,
				y:	0,
				z:	0
			}
	};
	
	this.set_transform_matrix=function(new_transform_matrix)
	{
		this.deviceorientation.transform_matrix=new_transform_matrix;
		this.deviceorientation.version++;
	}
	this.get_deviceorientation_version=function()
	{
		return this.deviceorientation.version;
	};
	this.get_deviceorientation_location=function()
	{
		if((this.deviceorientation.up_date_version)!=(this.deviceorientation.version)){
			this.deviceorientation.up_date_version=this.deviceorientation.version;
			
			var x_loca=this.computer.create_move_rotate_matrix(0,0,0,this.deviceorientation.beta,0,0);
			var y_loca=this.computer.create_move_rotate_matrix(0,0,0,0,this.deviceorientation.gamma,0);
			var z_loca=this.computer.create_move_rotate_matrix(0,0,0,0,0,this.deviceorientation.alpha);
			
			var loca=[
					0,	0,	 1,		0,
					1,	0,	 0,		0,
					0,	1,	 0,		0,
					0,	0,	 0,		1
				];
			loca=this.computer.matrix_multiplication(loca,z_loca);
			loca=this.computer.matrix_multiplication(loca,x_loca);
			loca=this.computer.matrix_multiplication(loca,y_loca);
			loca=this.computer.matrix_multiplication(this.deviceorientation.transform_matrix,loca);
			
			this.deviceorientation.matrix=loca;
		}
		return this.deviceorientation.matrix;
	}
	this.get_acceleration_translation=function()
	{	
		return this.computer.caculate_coordinate(
						this.get_deviceorientation_location(),
						this.acceleration.translation.x,
						this.acceleration.translation.y,
						this.acceleration.translation.z);
	}
	this.get_acceleration_rotation=function()
	{
		return this.computer.caculate_coordinate(
				this.get_deviceorientation_location(),
				this.acceleration.rotation.x,
				this.acceleration.rotation.y,
				this.acceleration.rotation.z);
	}
	this.gps.watch_id=navigator.geolocation.watchPosition(
		function(latitude,longitude,altitude)
		{
			var B=Math.PI*latitude/180.0;
			var	L=Math.PI*longitude/180.0;
			var	H=altitude;
			
			var sin_B=Math.sin(B);
			var cos_B=Math.cos(B);
			var sin_L=Math.sin(L);
			var cos_L=Math.cos(L);
			
			var a =6378137;
			var b =6356752.314;
			var e2=(a*a-b*b)/(a*a);
			var W =Math.sqrt(1-e2*sin_B*sin_B);
			var N =a/W;

			my_deviceorientation.gps.x			=(N+H)*cos_B*cos_L;
			my_deviceorientation.gps.y			=(N+H)*cos_B*sin_L;
			my_deviceorientation.gps.z			=(N*(1-e2)+H)*sin_B;
			my_deviceorientation.gps.latitude	=latitude;
			my_deviceorientation.gps.longitude	=longitude;
			my_deviceorientation.gps.altitude	=altitude;
			my_deviceorientation.gps.version++;
		}
	);
	
	window.addEventListener(
		"deviceorientation",
		function(event)
		{
			event.preventDefault();
			
			my_deviceorientation.deviceorientation.alpha=event.alpha;
			my_deviceorientation.deviceorientation.beta	=event.beta;
			my_deviceorientation.deviceorientation.gamma=event.gamma;

			my_deviceorientation.deviceorientation.version++;
		},
		true);
	window.addEventListener(
		"devicemotion",
		function(event)
		{
			my_deviceorientation.acceleration.translation	=event.accelerationIncludingGravity;
			my_deviceorientation.acceleration.rotation		=event.rotationRate;
		},
		true);
};
