package driver_find_visible;

import kernel_render.render;
import kernel_component.component;
import kernel_engine.engine_kernel;
import kernel_part.face_face;
import kernel_part.part;
import kernel_part.part_rude;
import kernel_file_manager.file_writer;
import kernel_transformation.point;

public class triangle_collector 
{
	public triangle_item triangle[][];
	public triangle_collector(render renders[])
	{
		triangle=new triangle_item[renders.length][];
		for(int render_id=0,render_number=renders.length;render_id<render_number;render_id++) {
			triangle[render_id]=null;
			if(renders[render_id]!=null)
				if(renders[render_id].parts!=null)
					if(renders[render_id].parts.length>0) {
						triangle[render_id]=new triangle_item[renders[render_id].parts.length];
						for(int part_id=0,part_number=triangle[render_id].length;part_id<part_number;part_id++)
							triangle[render_id][part_id]=null;
					}
		}
	}
	public void register(component comp,int my_body_id,int my_face_id,int my_triangle_id)
	{
		int driver_number;
		if((driver_number=comp.driver_number())<=0)
			return;
		part p;
		if((comp.fix_render_driver_id>=0)&&(comp.fix_render_driver_id<driver_number))
			p=comp.driver_array[comp.fix_render_driver_id].component_part;
		else
			p=comp.driver_array[0].component_part;
		if(p==null)
			return;
		triangle[p.render_id][p.part_id]=new triangle_item(
			my_body_id,my_face_id,my_triangle_id,comp.component_id,
			triangle[p.render_id][p.part_id]);
	}
	public void write_out(String triangle_file_name,engine_kernel ek)
	{
/*
		triangle_item t;
		
		String file_name=ek.scene_par.scene_proxy_directory_name+triangle_file_name;
		file_writer stl_fw=new file_writer(file_name+".stl",ek.system_par.local_data_charset);
		file_writer id_fw=new file_writer(file_name+".id",ek.system_par.local_data_charset);
		
		stl_fw.println("solid	",stl_fw.file_name);
		
		for(int render_id=0,render_number=ek.render_cont.renders.length;render_id<render_number;render_id++) {
			if(triangle[render_id]==null)
				continue;
			for(int part_id=0,part_number=triangle[render_id].length;part_id<part_number;part_id++) {
				if((t=triangle[render_id][part_id])==null)
					continue;
				triangle[render_id][part_id]=null;
				part p=ek.render_cont.renders[render_id].parts[part_id];
				part_rude pr=null;				
				if(p.mesh_file_name==null){
					if(!(p.top_box_part_flag))
						pr=ek.render_cont.renders[p.render_id].parts[p.part_from_id].caculate_part_box_mesh();
				}else if(p.driver!=null) {
					file_writer tmp_fw=new file_writer(file_name+".tmp",ek.system_par.local_data_charset);
					pr=p.driver.create_mesh_and_material(p,tmp_fw,ek.part_cont,ek.system_par);
					tmp_fw.close();
					file_writer.file_delete(file_name+".tmp");
				}
				if(pr!=null){
					auxiliary_file_handler auxiliary_f=new auxiliary_file_handler(p);
					
					for(triangle_item s;t!=null;s=t,t=t.next,s.next=null){
						if(t.body_id>=pr.body_number())
							continue;
						if(t.face_id>=pr.body_array[t.body_id].face_number())
							continue;
						face_face fa_face=pr.body_array[t.body_id].face_array[t.face_id].fa_face;
						int primitive_number=fa_face.mesh.get_primitive_number();
						if((t.triangle_id<0)||(t.triangle_id>=primitive_number))
							continue;
						if(fa_face.mesh.get_primitive_vertex_number(t.triangle_id,auxiliary_f)<3)
							continue;
						component my_comp;
						if((my_comp=ek.component_cont.get_component(t.component_id))==null)
							continue;
						
						int index_0=fa_face.mesh.get_primitive_normal_index(t.triangle_id,0,auxiliary_f);
						int index_1=fa_face.mesh.get_primitive_normal_index(t.triangle_id,1,auxiliary_f);
						int index_2=fa_face.mesh.get_primitive_normal_index(t.triangle_id,2,auxiliary_f);
						double p0[]=fa_face.mesh.get_normal(index_0,auxiliary_f);
						double p1[]=fa_face.mesh.get_normal(index_1,auxiliary_f);
						double p2[]=fa_face.mesh.get_normal(index_2,auxiliary_f);
						double dx=p0[0]+p1[0]+p2[0];
						double dy=p0[1]+p1[1]+p2[1];
						double dz=p0[2]+p1[2]+p2[2];
						double d=Math.sqrt(dx*dx+dy*dy+dz*dz);
						point pn=my_comp.absolute_location.multiply(dx/d,dy/d,dz/d);
						pn=pn.sub(my_comp.absolute_location.multiply(0,0,0)).expand(1.0);
						
						index_0=fa_face.mesh.get_primitive_vertex_index(t.triangle_id,0,auxiliary_f);
						index_1=fa_face.mesh.get_primitive_vertex_index(t.triangle_id,1,auxiliary_f);
						index_2=fa_face.mesh.get_primitive_vertex_index(t.triangle_id,2,auxiliary_f);
						p0=fa_face.mesh.get_vertex(index_0,auxiliary_f);
						p1=fa_face.mesh.get_vertex(index_1,auxiliary_f);
						p2=fa_face.mesh.get_vertex(index_2,auxiliary_f);
						
						point pp0=my_comp.absolute_location.multiply(p0[0],p0[1],p0[2]);
						point pp1=my_comp.absolute_location.multiply(p1[0],p1[1],p1[2]);
						point pp2=my_comp.absolute_location.multiply(p2[0],p2[1],p2[2]);

						id_fw.	print  ("	",t.body_id).
								print  ("	",t.face_id).
								print  ("	",t.triangle_id).
								println("	",my_comp.component_name);
						
						
						stl_fw.print  ("facet normal	",pn.x).print("	",pn.y).println("	",pn.z);
						stl_fw.println("	outer loop");
						stl_fw.print  ("		vertex	",pp0.x).print("	",pp0.y).println("	",pp0.z);
						stl_fw.print  ("		vertex	",pp1.x).print("	",pp1.y).println("	",pp1.z);
						stl_fw.print  ("		vertex	",pp2.x).print("	",pp2.y).println("	",pp2.z);
						stl_fw.println("	endloop");
						stl_fw.println("endfacet").println();
					}
					pr.destroy();
					auxiliary_f.destroy();
				}
			}
		}
		
		stl_fw.println("endsolid").println();
		
		stl_fw.close();
		id_fw.close();	
*/		
	}
}
