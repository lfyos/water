package format_convert;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class stl_converter 
{
	private byte a,b,c,d,e,f;
	private boolean not_ascii_flag;
	
	public double stl_data[][][];
	public int facet_number;

	private void write_out_one_data(file_writer f,String data_header,double data[])
	{
		f.print(data_header);
		for(int i=0,ni=data.length;i<ni;i++){
			f.print("	");
			f.print(data[i]);
		}
		f.println();
	}
	private void write_out(file_writer f)
	{
		if((stl_data==null)||(facet_number<=0))
			return;
		f.println("/* version          */   2019.08.12");
		f.println("/* origin material  */   0  0  0  1");
		f.println("/* body_number      */   1");
		f.println("/* body  0  name   */  stl_body   /*   face_number   */  1");
		f.println();
		f.println("	/* face  0  name   */  stl_face");
		f.println("	/* face_type   */  unknown  /*   parameter_number   */  0 /*   parameter  */"); 
		f.println("	/* face_attribute_number   */  0");


		f.println();
		f.print  ("	/* face_vertex_number   */  ");		f.println(facet_number*3);
		for(int i=0;i<facet_number;i++){
			write_out_one_data(f,"/*		face_vertex  "+Integer.toString(3*i+0)+" is  */",stl_data[i][1]);
			write_out_one_data(f,"/*		face_vertex  "+Integer.toString(3*i+1)+" is  */",stl_data[i][2]);
			write_out_one_data(f,"/*		face_vertex  "+Integer.toString(3*i+2)+" is  */",stl_data[i][3]);
		}

		f.println();
		f.print  ("	/* face_normal_number    */  ");		f.println(facet_number);
		for(int i=0;i<facet_number;i++)
			write_out_one_data(f,"/*		face_normal  "+Integer.toString(i)+" is  */",stl_data[i][0]);
	
		f.println();
		f.print  ("	/* face_primitive_number */  ");		f.println(facet_number);
		
		for(int i=0;i<facet_number;i++){
			f.print  ("		/* face_primitive  ",i);		
			f.println("  material     */    0    0    0    0");  

			f.print  ("		/* face_primitive  ",	i);		
			f.print  ("  vertex_index */    ",		3*i+0);
			f.print  ("    ",						3*i+1);
			f.print  ("    ",						3*i+2);
			f.println("    -1");
			
			f.print  ("		/* normal_index  ",		i);		
			f.print  ("  vertex_index */    ",		i);
			f.print  ("    ",						i);
			f.print  ("    ",						i);
			f.println("    -1");
		}
		
		f.println();
		f.println();
        f.println("		/* face_loop_number   */ 0");
        f.println();
        f.println();

	}
	private String ascii_search_for_header(file_reader f,String head_string)
	{
		not_ascii_flag=false;
		while(true){
			String str;
			
			if(f.eof())
				return null;
			if(f.error_flag())
				return null;
			if((str=f.get_string())!=null){
				if(str.toLowerCase().compareTo(head_string)==0)
					return head_string;
				else
					not_ascii_flag=true;
			}
		}
	}
	private double []ascii_get_data(file_reader f,String header_string)
	{
		ascii_search_for_header(f,header_string);
		
		double ret_val[]=new double[4];
		for(int i=0;i<3;i++)
			ret_val[i]=f.get_double();
		ret_val[3]=1.0;
		
		return ret_val;
	}
	private boolean ascii_processor(String file_name,String file_system_charset)
	{	
		file_reader f=new file_reader(file_name,file_system_charset);
		
		if((ascii_search_for_header(f,"solid")==null)||not_ascii_flag){
			f.close();
			return true;
		}
		for(int j=0;;j++){
			String str=ascii_search_for_header(f,"facet");
			
			if(str==null){
				f.close();
				return (facet_number>0)?false:true;
			}
			if(j>0){
				if(not_ascii_flag){
					f.close();
					return true;
				}	
			}
			double normal[]=ascii_get_data(f,"normal");
			if(not_ascii_flag){
				f.close();
				return true;
			}
			ascii_search_for_header(f,"outer");
			if(not_ascii_flag){
				f.close();
				return true;
			}
			ascii_search_for_header(f,"loop");
			if(not_ascii_flag){
				f.close();
				return true;
			}
			double vertex1[]=ascii_get_data(f,"vertex");
			if(not_ascii_flag){
				f.close();
				return true;
			}
			double vertex2[]=ascii_get_data(f,"vertex");
			if(not_ascii_flag){
				f.close();
				return true;
			}
			double vertex3[]=ascii_get_data(f,"vertex");
			if(not_ascii_flag){
				f.close();
				return true;
			}
			ascii_search_for_header(f,"endloop");
			if(not_ascii_flag){
				f.close();
				return true;
			}
			ascii_search_for_header(f,"endfacet");
			if(not_ascii_flag){
				f.close();
				return true;
			}
			if(facet_number<=0)
				stl_data=new double[1000][][];
			else if(facet_number>=(stl_data.length)){
				double bak_stl_data[][][]=stl_data;
				stl_data=new double[facet_number+1000][][];
				for(int i=0;i<facet_number;i++)
					stl_data[i]=bak_stl_data[i];
			}
			double this_data[][]={normal,vertex1,vertex2,vertex3};
			stl_data[facet_number++]=this_data;
		}
	}
	private void binary_switch_four_byte(DataInputStream dr,DataOutputStream dw)
	{
		try{
			a=dr.readByte();		b=dr.readByte();			c=dr.readByte();			d=dr.readByte();
			dw.writeByte(d);		dw.writeByte(c);			dw.writeByte(b);			dw.writeByte(a);
		}catch(Exception e){
			;
		}
	}
	private boolean binary_change_format(String file_name,String new_file_name)
	{
		try{
			FileInputStream 		fr=new FileInputStream(file_name);
			BufferedInputStream 	br=new BufferedInputStream(fr);
			DataInputStream 		dr=new DataInputStream(br);
			
			FileOutputStream		fw=new FileOutputStream(new_file_name);
			BufferedOutputStream	bw=new BufferedOutputStream(fw);
			DataOutputStream		dw=new DataOutputStream(bw);
			
			dr.skipBytes(80);
			
			binary_switch_four_byte(dr,dw);
			
			for(int i=0,number=((d*256+c)*256+b)*256+a;i<number;i++){
				for(int j=0;j<4;j++)
					for(int k=0;k<3;k++)
						binary_switch_four_byte(dr,dw);
				e=dr.readByte();
				f=dr.readByte();
				dr.skipBytes(256*f+e);
			}
			
			dw.close();
			bw.close();
			fw.close();
			
			dr.close();
			br.close();
			fr.close();
			
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	private void binary_read_facet_data(String file_name)
	{
		try{
			FileInputStream 		fr=new FileInputStream(file_name);
			BufferedInputStream 	br=new BufferedInputStream(fr);
			DataInputStream 		dr=new DataInputStream(br);
			
			facet_number=dr.readInt();
			if(facet_number>0){
				stl_data=new double[facet_number][][];
				for(int i=0;i<facet_number;i++){
					stl_data[i]	=new double[4][];
					for(int j=0;j<4;j++){
						stl_data[i][j]=new double[4];
						stl_data[i][j][3]=1.0;
						for(int k=0;k<3;k++)
							stl_data[i][j][k]=dr.readFloat();
					}
				}
			}
			dr.close();
			br.close();
			fr.close();
		}catch(Exception e){
			;
		}
	}
	private void do_convert(String file_name,String file_system_charset)
	{
		stl_data=null;
		facet_number=0;
		if(ascii_processor(file_name,file_system_charset)){
			stl_data=null;
			facet_number=0;
			if(binary_change_format(file_name,file_name+".binary")){
				binary_read_facet_data(file_name+".binary");
				file_writer.file_delete(file_name+".binary");
			}
		}
	}
	public stl_converter(String mesh_file_name,String mesh_charset,String target_file_name,String target_charset)
	{
		do_convert(mesh_file_name,mesh_charset);
		
		file_writer fw=new file_writer(target_file_name,target_charset);
		write_out(fw);
		fw.close();
	}
}
