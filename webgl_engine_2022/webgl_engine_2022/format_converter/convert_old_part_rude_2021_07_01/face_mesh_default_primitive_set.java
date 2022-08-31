package convert_old_part_rude_2021_07_01;

import kernel_file_manager.file_reader;

public class face_mesh_default_primitive_set
{
	public String material[][];
	public int vertex[][],normal[][],attribute[][][];

	public void destroy()
	{
		material=null;
		vertex=null;
		normal=null;
		attribute=null;
	}
	
	public void add_attribute()
	{
		int primitive_number=0;
		if(vertex!=null)
			primitive_number=vertex.length;
		
		int new_attribute[][]=new int[primitive_number][];
		for(int i=0;i<primitive_number;i++) {
			new_attribute[i]=new int[vertex[i].length];
			for(int j=0,nj=new_attribute[i].length;j<nj;j++)
				new_attribute[i][j]=0;
		}
		
		if(attribute==null)
			attribute=new int[0][][];

		int bak[][][]=attribute;
		attribute=new int[attribute.length+1][][];
		
		for(int i=0,ni=bak.length;i<ni;i++)
			attribute[i]=bak[i];
		attribute[attribute.length-1]=new_attribute;
	}
	
	private int []input_index_id(file_reader f)
	{
		int vertex_number=0,ret_val[]=new int[100];
		do{
			if(f.eof())
				break;
			int vertex_id=f.get_int();
			if(vertex_id<0)
				break;
			if(ret_val.length<=vertex_number){
				int bak[]=ret_val;
				ret_val=new int[vertex_number+100];
				for(int i=0,ni=bak.length;i<ni;i++)
					ret_val[i]=bak[i];
			}
			ret_val[vertex_number++]=vertex_id;
		}while(true);
		
		if(ret_val.length!=vertex_number) {
			int bak[]=ret_val;
			ret_val=new int[vertex_number];
			for(int i=0;i<vertex_number;i++)
				ret_val[i]=bak[i];
		}
		return ret_val;	
	}
	public face_mesh_default_primitive_set(file_reader f,int attribute_number)
	{
		int primitive_number=f.get_int();
		material	=new String[primitive_number][];
		vertex		=new int[primitive_number][];
		normal		=new int[primitive_number][];
		attribute	=new int[attribute_number][][];
		for(int i=0;i<attribute_number;i++)
			attribute[i]=new int[primitive_number][];
		
		for(int i=0;i<primitive_number;i++){
			material[i]=new String[4];
			for(int j=0,nj=material[i].length;j<nj;j++)
				material[i][j]=f.get_string();
			
			vertex[i]=input_index_id(f);
			normal[i]=input_index_id(f);
			for(int j=0;j<attribute_number;j++)
				attribute[j][i]=input_index_id(f);
		}
	}
	public face_mesh_default_primitive_set(String my_material[])
	{
		material=new String[][]
				{
						{my_material[0],my_material[1],my_material[2],my_material[3]},
						{my_material[0],my_material[1],my_material[2],my_material[3]},
						{my_material[0],my_material[1],my_material[2],my_material[3]},
						{my_material[0],my_material[1],my_material[2],my_material[3]},
						{my_material[0],my_material[1],my_material[2],my_material[3]},
						{my_material[0],my_material[1],my_material[2],my_material[3]},
						{my_material[0],my_material[1],my_material[2],my_material[3]},
						{my_material[0],my_material[1],my_material[2],my_material[3]},
						{my_material[0],my_material[1],my_material[2],my_material[3]},
						{my_material[0],my_material[1],my_material[2],my_material[3]},
						{my_material[0],my_material[1],my_material[2],my_material[3]},
						{my_material[0],my_material[1],my_material[2],my_material[3]},
				};
		vertex=new int[][]
				{
						{0,1,2},	{2,1,3},		//left face
						{6,5,4},	{5,6,7},		//right face
						{0,5,1},	{5,0,4},		//down
						{2,7,6},	{7,2,3},		//up
						{4,0,6},	{6,0,2},		//front
						{5,7,1},	{3,1,7}			//back
				};
		normal=new int[][]
				{
						{1,1,1},	{1,1,1},		//left face
						{0,0,0},	{0,0,0},		//right face
						{3,3,3},	{3,3,3},		//down
						{2,2,2},	{2,2,2},		//up
						{5,5,5},	{5,5,5},		//front
						{4,4,4},	{4,4,4}			//back
				};
		attribute=new int[][][]{
			new int[][]
				{
						{0,1,2},	{2,1,3},		//left face
						{6,5,4},	{5,6,7},		//right face
						{0,5,1},	{5,0,4},		//down
						{2,7,6},	{7,2,3},		//up
						{4,0,6},	{6,0,2},		//front
						{5,7,1},	{3,1,7}			//back
				}
		};
	}
	public face_mesh_default_primitive_set(face_mesh_default_primitive_set s)
	{
		material=new String[s.material.length][];
		for(int i=0,ni=material.length;i<ni;i++){
			material[i]=new String[s.material[i].length];
			for(int j=0,nj=material[i].length;j<nj;j++)
				material[i][j]=s.material[i][j];
		}
		
		vertex=new int[s.vertex.length][];
		for(int i=0,ni=vertex.length;i<ni;i++){
			vertex[i]=new int[s.vertex[i].length];
			for(int j=0,nj=vertex[i].length;j<nj;j++)
				vertex[i][j]=s.vertex[i][j];
		}
		normal=new int[s.normal.length][];
		for(int i=0,ni=normal.length;i<ni;i++){
			normal[i]=new int[s.normal[i].length];
			for(int j=0,nj=normal[i].length;j<nj;j++)
				normal[i][j]=s.normal[i][j];
		}
		attribute=new int[s.attribute.length][][];
		for(int i=0,ni=attribute.length;i<ni;i++){
			attribute[i]=new int[s.attribute[i].length][];
			for(int j=0,nj=s.attribute[i].length;j<nj;j++){
				attribute[i][j]=new int[s.attribute[i][j].length];
				for(int k=0,nk=s.attribute[i][j].length;k<nk;k++)
					attribute[i][j][k]=s.attribute[i][j][k];
			}
		}
	}
}
