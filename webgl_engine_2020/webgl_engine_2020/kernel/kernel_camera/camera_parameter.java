package kernel_camera;

import kernel_common_class.const_value;

public class camera_parameter
{
	public boolean movement_flag,direction_flag,change_type_flag;
	public double scale_value;

	public long switch_time_length;
	public double distance,half_fovy_tanl,bak_half_fovy_tanl,near_ratio,far_ratio;
	public boolean projection_type_flag;
	public double low_precision_scale,high_precision_scale;
	public boolean synchronize_location_flag,light_camera_flag;
	
	static public boolean is_not_same_parameter(camera_parameter s,camera_parameter d)
	{
		if(Math.abs(s.distance-d.distance)>const_value.min_value)
			return true;
		if(Math.abs(s.half_fovy_tanl-d.half_fovy_tanl)>const_value.min_value)
			return true;
		if(Math.abs(s.bak_half_fovy_tanl-d.bak_half_fovy_tanl)>const_value.min_value)
			return true;
		if(Math.abs(s.near_ratio-d.near_ratio)>const_value.min_value)
			return true;
		if(Math.abs(s.far_ratio-d.far_ratio)>const_value.min_value)
			return true;
	
		if(s.projection_type_flag^d.projection_type_flag)
			return true;
		
		if(Math.abs(s.low_precision_scale-d.low_precision_scale)>const_value.min_value)
			return true;
		if(Math.abs(s.high_precision_scale-d.high_precision_scale)>const_value.min_value)
			return true;
		if(s.synchronize_location_flag^d.synchronize_location_flag)
			return true;
		if(s.light_camera_flag^d.light_camera_flag)
			return true;
		return false;
	}
	public camera_parameter copy_from(camera_parameter s)
	{
		movement_flag				=s.movement_flag;
		direction_flag				=s.direction_flag;
		change_type_flag			=s.change_type_flag;
		
		scale_value					=s.scale_value;
		
		switch_time_length			=s.switch_time_length;
		distance					=s.distance;
		half_fovy_tanl				=s.half_fovy_tanl;
		bak_half_fovy_tanl			=s.bak_half_fovy_tanl;
		near_ratio					=s.near_ratio;
		far_ratio					=s.far_ratio;
		
		projection_type_flag		=s.projection_type_flag;
		
		low_precision_scale			=s.low_precision_scale;
		high_precision_scale		=s.high_precision_scale;
		
		synchronize_location_flag	=s.synchronize_location_flag;
		light_camera_flag			=s.light_camera_flag;
		return this;
	}
	public camera_parameter(camera_parameter s)
	{
		copy_from(s);
	}
	public camera_parameter(
			boolean my_movement_flag,boolean my_direction_flag,boolean my_change_type_flag,
			double my_scale_value,long my_switch_time_length,double my_distance,
			double my_half_fovy_tanl,double my_bak_half_fovy_tanl,double my_near_ratio,double my_far_value_ratio,
			boolean my_projection_type_flag,
			double my_low_precision_scale,double my_high_precision_scale,
			boolean my_synchronize_location_flag,boolean my_light_camera_flag)
	{
		movement_flag				=my_movement_flag;
		direction_flag				=my_direction_flag;
		change_type_flag			=my_change_type_flag;
		scale_value					=my_scale_value;
		
		switch_time_length			=my_switch_time_length;
		distance					=my_distance;
		half_fovy_tanl				=my_half_fovy_tanl;
		bak_half_fovy_tanl			=my_bak_half_fovy_tanl;
		near_ratio					=my_near_ratio;
		far_ratio				 	=my_far_value_ratio;
		
		projection_type_flag		=my_projection_type_flag;
		
		low_precision_scale			=my_low_precision_scale;
		high_precision_scale		=my_high_precision_scale;
		
		synchronize_location_flag	=my_synchronize_location_flag;
		light_camera_flag			=my_light_camera_flag;
	}
	public camera_parameter mix(camera_parameter t,double p,double q)
	{
		camera_parameter result;
		result=new camera_parameter(
					movement_flag,		direction_flag,		change_type_flag,
					scale_value					*p	+t.scale_value			*q,
					(long)(switch_time_length	*p	+t.switch_time_length	*q),
					distance					*p	+t.distance				*q,
					half_fovy_tanl				*p	+t.half_fovy_tanl		*q,
					bak_half_fovy_tanl			*p	+t.bak_half_fovy_tanl	*q,
					near_ratio					*p	+t.near_ratio			*q,
					far_ratio					*p	+t.far_ratio			*q,
					projection_type_flag,				
					low_precision_scale			*p	+t.low_precision_scale	*q,
					high_precision_scale		*p	+t.high_precision_scale	*q,
					synchronize_location_flag,	light_camera_flag);
		return result;
	}
	public camera_parameter mix(camera_parameter t,double p)
	{
		return mix(t,1-p,p);
	}
}
