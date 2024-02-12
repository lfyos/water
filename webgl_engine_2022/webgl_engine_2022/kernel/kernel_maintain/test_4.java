package kernel_maintain;

import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_transformation.point;
import kernel_transformation.box;
import kernel_common_class.debug_information;

public class test_4 
{
	public static void main(String args[])
	{
		debug_information.println("Begin");
		
		box b1=new box(-1068.3251953125,385.4619445801,-1522.4249267578,1068.3251953125,2253.46875,1522.4249267578);
		box b2=new box(-332.5439453125,-6.10352E-5,-856.7086181641,425.0537109375,1532.8129882813,687.8979492188);
		box b=b1.add(b2);
		
		point center=b.center();
		double distance_x	=Math.abs(b.p[1].x-b.p[0].x);
		double distance_y	=Math.abs(b.p[1].y-b.p[0].y);
		double distance_z	=Math.abs(b.p[1].z-b.p[0].z);
		double scale		=2.0/Math.max(distance_x, Math.max(distance_y,distance_z));
		
		file_reader fr=new file_reader("F:\\temp\\data.old.txt",null);
		file_writer fw=new file_writer("F:\\temp\\data.new.txt",null);
		fw.println("[");
		
		while(true){
			point v=new point(fr.get_double(),fr.get_double(),fr.get_double(),fr.get_double()).sub(center).scale(scale);
			for(int i=0;i<12;i++)
				fr.get_double();
			if(fr.eof())
				break;
			point t=new point(fr.get_double(),fr.get_double(),fr.get_double(),fr.get_double());
			
			fw.print("\t",v.x).print(",\t",v.y).print(",\t",v.z).println(",\t1.0,");
			fw.print("\t",t.x).print(",\t",t.y).print(",\t",t.z).println(",\t1.0,");
			fw.println();
		}
		fw.println("]");
		fr.close();
		fw.close();
		debug_information.println("End");
	}
}
