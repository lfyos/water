package kernel_information;

import kernel_engine.engine_kernel;
import kernel_engine.client_information;

public class engine_camera_information extends jason_creator
{
	private engine_kernel ek;
	private client_information ci;
	
	public void print()
	{
		jason_creator creator_array[];

		print("display_camera",			new camera_information(ci.display_camera_result.cam,ci));
		print("selection_camera",		new camera_information(ci.selection_camera_result.cam,ci));
		
		if(ci.target_camera_result_array==null)
			creator_array=new jason_creator[0];
		else
			creator_array=new jason_creator[ci.target_camera_result_array.length];
		for(int i=0,ni=creator_array.length;i<ni;i++)
			creator_array[i]=new camera_information(ci.target_camera_result_array[i].cam,ci);
		print("target_camera",creator_array);
		
		if(ek.camera_cont==null)
			creator_array=new jason_creator[0];
		else
			creator_array=new jason_creator[ek.camera_cont.size()];
		for(int i=0,ni=creator_array.length;i<ni;i++)
			creator_array[i]=new camera_information(ek.camera_cont.get(i),ci);
		print("engine_camera",creator_array);
	}
	public engine_camera_information(engine_kernel my_ek,client_information my_ci)
	{
		super(my_ci.request_response);
		ek=my_ek;
		ci=my_ci;
	}
}
