package kernel_information;

import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class scene_camera_information extends jason_creator
{
	private scene_kernel sk;
	private client_information ci;
	
	public void print()
	{
		jason_creator creator_array[];

		print("display_camera",new camera_information(ci.display_camera_result.cam,ci));
		
		if(ci.target_camera_result_list==null)
			creator_array=new jason_creator[0];
		else
			creator_array=new jason_creator[ci.target_camera_result_list.size()];
		for(int i=0,ni=creator_array.length;i<ni;i++)
			creator_array[i]=new camera_information(ci.target_camera_result_list.get(i).cam,ci);
		print("target_camera",creator_array);
		
		if(sk.camera_cont==null)
			creator_array=new jason_creator[0];
		else
			creator_array=new jason_creator[sk.camera_cont.size()];
		for(int i=0,ni=creator_array.length;i<ni;i++)
			creator_array[i]=new camera_information(sk.camera_cont.get(i),ci);
		print("scene_camera",creator_array);
	}
	public scene_camera_information(scene_kernel my_sk,client_information my_ci)
	{
		super(my_ci.request_response);
		sk=my_sk;
		ci=my_ci;
	}
}
