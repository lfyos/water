package kernel_information;

import kernel_engine.engine_kernel;
import kernel_engine.client_information;

public class engine_information extends jason_creator
{
	private engine_kernel ek;
	private client_information ci;
	
	public void print()
	{
		print("kernel",					new kernel_information(ek,ci));
		
		print("browser",				new browser_information(ci));

		
		if(ci.clip_plane==null)
			print("clip_plane","[]");
		else
			print("clip_plane",new double[]{ci.clip_plane.A,ci.clip_plane.B,ci.clip_plane.C,ci.clip_plane.D});
		print("channel_id",ci.channel_id);
		
		print("client_parameter",		new client_parameter_information(ci));
		print("statistics_client",		new statistics_client_information(ek,ci));
		print("statistics_engine",		new statistics_engine_information(ci));
		print("statistics_user",		new statistics_user_information(ek,ci));
		print("proxy",					new proxy_information(ci));
	}
	public engine_information(engine_kernel my_ek,client_information my_ci)
	{
		super(my_ci.request_response);
		ek=my_ek;
		ci=my_ci;
	}
}
