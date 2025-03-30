package kernel_information;

import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class scene_information extends jason_creator
{
	private scene_kernel sk;
	private client_information ci;
	
	public void print()
	{
		print("scene",					new kernel_information(sk,ci));
		
		print("browser",				new browser_information(ci));

		
		if(ci.clip_plane==null)
			print("clip_plane","[]");
		else
			print("clip_plane",new double[]{ci.clip_plane.A,ci.clip_plane.B,ci.clip_plane.C,ci.clip_plane.D});
		print("channel_id",ci.channel_id);
		
		print("client_parameter",		new client_parameter_information(ci));
		print("statistics_user",		new statistics_user_information(sk,ci));
		print("proxy",					new proxy_information(ci));
	}
	
	public scene_information(scene_kernel my_sk,client_information my_ci)
	{
		super(my_ci.request_response);
		
		sk=my_sk;
		ci=my_ci;
	}
}
