package kernel_maintain;

import kernel_file_manager.file_writer;
import kernel_transformation.point;
import kernel_common_class.debug_information;


public class test_6 
{
	public static void main(String args[])
	{
		point squard_point[]=new point[] {
				new point(0.0,0.0,0.0),new point(1.0,0.0,0.0),new point(1.0,1.0,0.0),
				new point(1.0,1.0,0.0),new point(0.0,1.0,0.0),new point(0.0,0.0,0.0)
		};
		
		debug_information.println("Begin");
		
		file_writer fw=new file_writer("F:\\temp\\x.txt",null);
		fw.println("[");
		
		for(int j=0,nj=10;j<nj;j++) 
			for(int i=0,ni=20;i<ni;i++)
				for(int k=0;k<6;k++){
					point texture=new point(i,j,0).add(squard_point[k]);
					texture.x/=ni;
					texture.y/=nj;
					
					fw.	print("		",Math.sin(Math.PI*(1.0-texture.y))*Math.sin(2.0*Math.PI*texture.x)).
						print(",	",Math.cos(Math.PI*(1.0-texture.y))).
						print(",	",Math.sin(Math.PI*(1.0-texture.y))*Math.cos(2.0*Math.PI*texture.x)).
						println(",	1.0,");
					
					fw.	print("		",texture.x).
						print(",	",texture.y).
						println(",	0.0,	1.0,").
						println();
				}
		
		fw.println("]");
		fw.close();
		debug_information.println("End");
	}
}
