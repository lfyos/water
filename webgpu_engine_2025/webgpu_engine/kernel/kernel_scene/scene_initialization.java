package kernel_scene;

import java.io.File;
import java.util.ArrayList;

import kernel_part.part;
import kernel_render.render;
import kernel_component.component;
import kernel_driver.component_driver;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_common_class.jason_string;
import kernel_common_class.tree_string_locker_container;
import kernel_common_class.common_reader;
import kernel_file_manager.file_directory;
import kernel_interface.client_process_bar;
import kernel_common_class.class_file_reader;
import kernel_common_class.debug_information;
import kernel_common_class.compress_file_data;
import kernel_network.client_request_response;
import kernel_program_reader.program_file_reader;
import kernel_component.component_initialization;
import kernel_file_manager.travel_through_directory;

public class scene_initialization
{
	class output_component_program
	{
		private int print_number;
		private file_writer fw;
		public output_component_program(file_writer my_fw)
		{
			print_number=0;
			fw=my_fw;
		}
		public void begin_output(component comp)
		{
			String str=jason_string.change_string(comp.component_name);
			
			if((print_number++)>0)
				fw.println(",");

			fw.	println("\t{").
				println("\t\tcomponent_id           :\t",comp.component_id+",").
				println("\t\tcomponent_name         :\t",str+",").
				println("\t\tinitialization_function:");
		}
		public void output_data(String program_text)
		{
			fw.println(program_text);
		}
		public void end_output()
		{
			fw.print("\t}");
		}
	}
	class output_all_component_program  extends travel_through_directory
	{
		private component comp;
		private output_component_program ocp;
		private String file_charset;
		
		public void operate_file(String file_name)
		{
			ocp.begin_output(comp);
			ocp.output_data(file_reader.get_text(file_name,file_charset));
			ocp.end_output();
		}
		public output_all_component_program(component my_comp,
				output_component_program my_ocp,String my_file_name,String my_file_charset)
		{
			comp=my_comp;
			ocp=my_ocp;
			file_charset=my_file_charset;
			do_travel(my_file_name,true);
		}
	}
	class file_last_time extends travel_through_directory
	{
		public long last_time;
		public void operate_file(String file_name)
		{
			long my_last_time=new File(file_name).lastModified();
			if(last_time<my_last_time)
				last_time=my_last_time;
		}
		public file_last_time(String file_name)
		{
			last_time=0;
			do_travel(file_name,false);
		}
	}
	private void  initialize_render_driver(scene_kernel sk,
			client_request_response request_response,client_process_bar process_bar)
	{
		int render_number=sk.render_cont.renders.size();
		process_bar.set_process_bar(true,"render_driver_initialization","", 0, render_number);
		
		for(int render_id=0;render_id<render_number;render_id++){
			render r;
			process_bar.set_process_bar(false,"render_driver_initialization","", render_id, render_number);
			if((r=sk.render_cont.renders.get(render_id))==null)
				continue;
			if(r.driver==null)
				continue;
			
			try{
				r.driver.initialize_render_driver(r,sk,request_response);
			}catch(Exception e){
				e.printStackTrace();
				
				debug_information.println("Render driver initialize_part_driver fail:	",e.toString());
				debug_information.println("Render class name:		",	r.driver.getClass().getName());
				debug_information.println("render_id:		",			render_id);
				debug_information.println("render_name:		",			r.render_name);
				
			}
		}
		process_bar.set_process_bar(false,"render_driver_initialization","", render_number, render_number);
		return;
	}
	private void initialize_part_drive(scene_kernel sk,
			client_request_response request_response,client_process_bar process_bar)
	{
		int process_parts_sequence[][]=sk.process_part_sequence.process_parts_sequence;
		int part_number=process_parts_sequence.length;
		process_bar.set_process_bar(true,"part_driver_initialization","", 0, part_number);
		
		for(int part_index_id=0;part_index_id<part_number;part_index_id++){
			int render_id=process_parts_sequence[part_index_id][0];
			int part_id  =process_parts_sequence[part_index_id][1];
			part my_part=sk.render_cont.renders.get(render_id).parts.get(part_id);
			process_bar.set_process_bar(false,"part_driver_initialization",
					my_part.user_name,part_index_id,part_number);
			
			if(my_part.driver!=null)
				try {
					my_part.driver.initialize_part_driver(my_part,sk,request_response);
				}catch(Exception e){
					e.printStackTrace();
					
					debug_information.println("Part driver initialize_part_driver fail:	",e.toString());
						
					debug_information.println("Part user name:		",	my_part.user_name);
					debug_information.println("Part system name:	",	my_part.system_name);
					debug_information.println("Part mesh_file_name:",
							my_part.directory_name+my_part.mesh_file_name);
					debug_information.println("Part material_file_name:",
							my_part.directory_name+my_part.material_file_name);
					debug_information.println("part_file_directory:",
							file_directory.part_temporary_directory(my_part,sk.system_par,sk.scene_par));
					
				}
		}
		process_bar.set_process_bar(false,"part_driver_initialization","",part_number,part_number);
	}
	private void initialize_component_driver(scene_kernel sk,
			client_request_response request_response,client_process_bar process_bar)
	{
		ArrayList<component> sort_component_array=sk.component_cont.get_sort_component_list();
		int component_number=sort_component_array.size();
		process_bar.set_process_bar(true,"component_driver_initialization","",0, component_number);
		for(int component_id=0;component_id<component_number;component_id++) {
			component my_component=sort_component_array.get(component_id);
			process_bar.set_process_bar(false,"component_driver_initialization",
					my_component.component_name,component_id, component_number);
			int driver_number=my_component.driver_array.size();
			for(int driver_id=0;driver_id<driver_number;driver_id++) {
				component_driver cd=my_component.driver_array.get(driver_id);
				try{
					cd.initialize_component_driver(my_component,driver_id,sk,request_response);
				}catch(Exception e) {
					e.printStackTrace();
					
					debug_information.println("Component driver initialize fail:	",e.toString());
					debug_information.println("Component name:",my_component.component_name);
					debug_information.println("Component file:",
							my_component.component_directory_name+my_component.component_file_name);
					debug_information.println("Component driver id:",driver_id);
				}
			}
		}
		process_bar.set_process_bar(false,"component_driver_initialization","",component_number,component_number);
	}
	private long shader_program_last_time(scene_kernel sk)
	{
		long last_time=0;
		for(render my_render:sk.render_cont.renders) {
			if(my_render==null)
				continue;
			String shader_file_name[][];
			if((shader_file_name=my_render.driver.shader_file_name_array())==null)
				continue;
			for(var shader_file_name_array:shader_file_name) { 
				if(shader_file_name_array==null)
					continue;
				for(var shader_file_name_item:shader_file_name_array){
					if(shader_file_name_item==null)
						continue;
					long my_last_time=class_file_reader.get_last_time(
							file_directory.replace_special_char(shader_file_name_item),
							my_render.driver.getClass(),sk.system_par.text_class_charset);
					if(last_time<my_last_time)
						last_time=my_last_time;
				}
			}
		}
		return last_time;
	}
	private long initialization_component_last_time(
			ArrayList<component> init_comp,ArrayList<component_initialization>init_init,
			scene_kernel sk,client_process_bar process_bar)
	{
		long last_time=0;
		
		process_bar.set_process_bar(true,"file_initialization_0","",0, init_comp.size());
		for(int i=0,ni=init_comp.size();i<ni;i++){
			component my_comp=init_comp.get(i);
			component_initialization my_init=init_init.get(i);
			
			process_bar.set_process_bar(false,"file_initialization_0",my_comp.component_name,i,ni);
			
			for(int j=0,nj=my_comp.initialization.program_and_charset.size();j<nj;j++){
				String my_program_and_charset[]=my_comp.initialization.program_and_charset.get(j);
				if(my_program_and_charset==null)
					continue;
				if(my_program_and_charset.length<2)
					continue;
				if(my_program_and_charset[1]==null)
					continue;
				File f;
				String file_name=my_program_and_charset[0];
				do{
					if((f=new File(my_comp.component_directory_name+file_name)).exists())
						break;
					if((f=new File(sk.create_parameter.scene_directory_name+file_name)).exists())
						break;
					if((f=new File(sk.scene_par.directory_name+file_name)).exists())
						break;
					if((f=new File(sk.scene_par.extra_directory_name+file_name)).exists())
						break;
					if((f=new File(sk.scene_par.scene_shader_directory_name+file_name)).exists())
						break;
					f=null;
					for(int k=0,nk=sk.scene_par.type_sub_directory.length;k<nk;k++) {
						String my_file_name=sk.scene_par.type_shader_directory_name;
						my_file_name+=sk.scene_par.type_sub_directory[k]+file_name;
						
						if((f=new File(my_file_name)).exists())
							break;
						f=null;
					}
					if(f!=null)
						break;
					if((f=new File(sk.system_par.data_root_directory_name+file_name)).exists())
						break;
					f=null;
				}while(false);
				
				if(f==null){
					debug_information.print(
							"Not exist component init function,component name:	",
							my_comp.component_name);
					debug_information.println("	file_name:	",my_program_and_charset[0]);
					my_init.program_and_charset.set(j,null);
				}else {
					file_name=f.getAbsolutePath();
					file_last_time flt=new file_last_time(file_name);
					if(last_time<flt.last_time)
						last_time=flt.last_time;
					my_init.program_and_charset.set(j,new String[]{file_name,my_program_and_charset[1]});
				}
			}
		}

		process_bar.set_process_bar(false,"file_initialization_0","",init_comp.size(),init_comp.size());
		
		return last_time;
	}
	private long initialization_last_time(
			ArrayList<component> init_comp,ArrayList<component_initialization>	init_init,
			scene_kernel sk,client_process_bar process_bar)
	{
		long last_time=0,my_last_time;
		if(last_time<(my_last_time=program_file_reader.get_system_program_last_time(sk.system_par)))
			last_time=my_last_time;
		if(last_time<(my_last_time=sk.caculate_scene_last_modified_time()))
			last_time=my_last_time;
		if(last_time<(my_last_time=shader_program_last_time(sk)))
			last_time=my_last_time;
		if(last_time<(my_last_time=initialization_component_last_time(init_comp,init_init,sk,process_bar)))
			last_time=my_last_time;
		return last_time;
	}
	private void output_component_initialization_data(
			file_writer fw,scene_kernel sk,ArrayList<component> sort_component_list,
			client_request_response request_response,client_process_bar process_bar)
	{
		fw.println("[");
		
		int component_number=sort_component_list.size();
		process_bar.set_process_bar(true,"file_initialization_1","",0, component_number);

		for(int i=0;i<component_number;i++){
			component my_component=sort_component_list.get(i);
			
			process_bar.set_process_bar(false,"file_initialization_1",
					my_component.component_name,i,component_number);
			
			fw.print("\t[",jason_string.change_string(my_component.component_name));
			fw.print(",",my_component.component_id);
			
			fw.print(",[");
			for(int j=0,nj=my_component.children.size();j<nj;j++)
				fw.print((j<=0)?"":",",my_component.children.get(j).component_id);
			fw.println("],");
			
			fw.println("\t\t[");
			int driver_number=my_component.driver_array.size();
			for(int driver_id=0;driver_id<driver_number;driver_id++) {
				var comp_driver=my_component.driver_array.get(driver_id);
				if(comp_driver!=null) {
					long output_length=fw.output_data_length;
					comp_driver.create_component_driver_initialization_data(
							fw,my_component,driver_id,sk,request_response);
					if(fw.output_data_length>output_length) {
						if(driver_id<(driver_number-1))
							fw.println(",");
						else
							fw.println();
						continue;
					}
				}
				if(driver_id<(driver_number-1))
					fw.print("\t\t\tnull,");
				else
					fw.print("\t\t\tnull");
			}
			fw.println("\t\t]");
			fw.println("\t",(i<(component_number-1))?"],":"]");
		}
		
		process_bar.set_process_bar(false,"file_initialization_1","",component_number,component_number);
		
		fw.println("],").println().println();
	}
	private void output_part_and_render_initialization_data(file_writer fw,scene_kernel sk,
			client_request_response request_response,client_process_bar process_bar)
	{
		fw.println("[");
		{
			int id[][][][]=sk.component_cont.part_component_id_and_driver_id;
			process_bar.set_process_bar(true,"file_initialization_2","",0, id.length);
			for(int render_id=0,render_number=id.length;render_id<render_number;render_id++){
				render r=sk.render_cont.renders.get(render_id);
				process_bar.set_process_bar(false,"file_initialization_2",r.render_name,render_id,render_number);
				fw.println("\t[");
				for(int part_id=0,part_number=id[render_id].length;part_id<part_number;part_id++){
					fw.println("\t\t[");
					for(int i=0,ni=id[render_id][part_id].length;i<ni;i++) {
						int component_id=id[render_id][part_id][i][0];
						int driver_id	=id[render_id][part_id][i][1];
						fw.print  ("\t\t\t[",component_id);
						fw.print  (",",driver_id);
						fw.println((i==(ni-1))?"]":"],");
					}
					fw.println("\t\t],");
					
					part my_part;
					if((my_part=r.parts.get(part_id))!=null) 
						if(my_part.driver!=null){
							long output_length=fw.output_data_length;
							my_part.driver.create_part_driver_initialization_data(fw,my_part,sk,request_response);
							if(fw.output_data_length>output_length) {
								if(part_id<(part_number-1))
									fw.println(",");
								else
									fw.println();
								continue;
							}
						}
					if(part_id<(part_number-1))
						fw.println("\t\tnull,");
					else
						fw.println("\t\tnull");
				}
				fw.println("\t],");
				
				if(r.driver!=null) {
					long output_length=fw.output_data_length;
					r.driver.create_render_driver_initialization_data(fw,r,sk,request_response);
					if(fw.output_data_length>output_length){
						if(render_id<(render_number-1))
							fw.println(",");
						else
							fw.println();
						continue;
					}
				}
				if(render_id<(render_number-1))
					fw.println("\tnull,");
				else
					fw.println("\tnull");
			}
			process_bar.set_process_bar(false,"file_initialization_2","",id.length, id.length);
		}
		
		fw.println("],").println().println();
	}
	private void output_component_initialization_program(file_writer fw,
			ArrayList<component> init_comp,ArrayList<component_initialization>init_init,
			scene_kernel sk,client_process_bar process_bar)
	{
		fw.println("[");
		process_bar.set_process_bar(true,"file_initialization_3","",0,init_comp.size());
		output_component_program ocp=new output_component_program(fw);
		for(int i=0,ni=init_comp.size();i<ni;i++){
			component 					my_comp=init_comp.get(i);
			component_initialization 	my_init=init_init.get(i);
			process_bar.set_process_bar(false,"file_initialization_3",my_comp.component_name,i,ni);
			for(int j=0,nj=my_init.program_and_charset.size();j<nj;j++) {
				String my_program_and_charset[];
				if((my_program_and_charset=my_init.program_and_charset.get(j))==null)
					continue;
				if(my_program_and_charset[0]==null)
					continue;
				if(my_program_and_charset[1]==null){
					ocp.begin_output(my_comp);
					ocp.output_data(my_program_and_charset[0]);
					ocp.end_output();
				}else
					new output_all_component_program(my_comp,ocp,
							my_program_and_charset[0],my_program_and_charset[1]);
			}
		}
		process_bar.set_process_bar(false,"file_initialization_3","",init_comp.size(),init_comp.size());

		fw.println("],").println().println();
	}
	private void output_shader_program(file_writer fw,scene_kernel sk,client_process_bar process_bar)
	{
		fw.println("[");
		
		int render_number=sk.render_cont.renders.size();
		process_bar.set_process_bar(true,"file_initialization_4","",0,render_number);			
		
		for(int render_id=0;render_id<render_number;render_id++) {
			render r=sk.render_cont.renders.get(render_id);
			process_bar.set_process_bar(false,"file_initialization_4",r.render_name,render_id,render_number);
			fw.	println("	[").print(jason_string.change_string(r.render_name));

			String shader_file_name[][]=r.driver.shader_file_name_array();
			if(shader_file_name==null)
				shader_file_name=new String[][] {};
			for(int i=0,ni=shader_file_name.length;i<ni;i++) {
				fw.println(",");
				if(i!=0)
					fw.println("		[");
				else{
					fw.println("function(render_id,render_name,");
					fw.println("	init_data,create_data,shader_code,text_array,render)");
					fw.println("{");
				}				
				for(int j=0,nj=shader_file_name[i].length;j<nj;j++){
					String class_charset=sk.system_par.text_class_charset;
					String my_file_name=file_directory.replace_special_char(shader_file_name[i][j]);
					int index_id=shader_file_name[i][j].lastIndexOf('.');
					if(index_id>=0)
						if(my_file_name.substring(index_id,index_id+3).toLowerCase().compareTo(".js")==0)
							class_charset	=sk.system_par.js_class_charset;
					String str="";	
					common_reader reader=class_file_reader.get_reader(
						my_file_name,r.driver.getClass(),class_charset);
					if(reader!=null){
						if(!(reader.error_flag()))
							str=reader.get_text();
						reader.close();
					}
					if(i==0)
						fw.println(str);
					else{
						str=jason_string.change_string(str);
						fw.print("			",str).println((j==(nj-1))?"":",");
					}					
				}				
				if(i!=0) 
					fw.print  ("		]");
				else{
					fw.println("	return new new_render_driver(render_id,render_name,");
					fw.println("		init_data,create_data,shader_code,text_array,render);");
					fw.print  ("}");
				}
			}
			fw.println().println().println();
			fw.println((render_id<(render_number-1))?"	],":"	]");				
		}				
		process_bar.set_process_bar(false,"file_initialization_4","",render_number,render_number);

		fw.println("],").println().println();
	}
	private void output_common_shader_program_and_data(file_writer fw,scene_kernel sk)
	{
		fw.println("[");
		{
			String str;
			str=program_file_reader.get_common_shader_data_structure(sk.system_par);
			fw.print  ("		",jason_string.change_string(str)).println(",");
			str=program_file_reader.get_common_shader_variable_declaration(sk.system_par);
			fw.print  ("		",jason_string.change_string(str)).println(",");
			str=program_file_reader.get_location_shader_program(sk.system_par);
			fw.print  ("		",jason_string.change_string(str)).println();
		}	
		fw.println("],").println().println();
	}
	private void output_system_parameter(file_writer fw,scene_kernel sk)
	{
		fw.println("{");
		
		fw.print  ("	scene_touch_time_length	:	",sk.system_par.scene_touch_time_length).	println(",");
		fw.print  ("	max_target_number		:	",sk.scene_par.max_target_number).			println(",");
		fw.print  ("	max_method_number		:	",sk.system_par.max_method_number);
		
		fw.println("}");
	}
	private void file_initialize(
			scene_kernel sk,tree_string_locker_container string_locker_cont,
			client_request_response request_response,client_process_bar process_bar)
	{
		ArrayList<component> 				init_comp=new ArrayList<component>();
		ArrayList<component_initialization>	init_init=new ArrayList<component_initialization>();
		ArrayList<component> sort_component_list=sk.component_cont.get_sort_component_list();
		for(int i=0,ni=sort_component_list.size();i<ni;i++) {
			component my_component=sort_component_list.get(i);
			component_initialization pi=my_component.initialization;
			if(pi==null)
				continue;
			if(pi.program_and_charset!=null)
				if(pi.program_and_charset.size()>0){
					init_comp.add(my_component);
					init_init.add(new component_initialization(my_component.initialization));
					continue;
				}
			pi.destroy();
			my_component.initialization=null;
		}

		String destination_file_name=sk.scene_par.scene_temporary_directory_name+"initialization.gzip_js";
		do{	
			string_locker_cont.read_lock(destination_file_name);
			long t=initialization_last_time(init_comp,init_init,sk,process_bar);
			if(t<=(new File(destination_file_name)).lastModified()){
				string_locker_cont.read_unlock(destination_file_name);
				break;
			}
			string_locker_cont.read_unlock(destination_file_name);
			
			string_locker_cont.write_lock(destination_file_name);
			t=initialization_last_time(init_comp,init_init,sk,process_bar);
			if(t<=(new File(destination_file_name)).lastModified()){
				string_locker_cont.write_unlock(destination_file_name);
				break;
			}
			
			file_writer fw=new file_writer(destination_file_name,sk.system_par.network_data_charset);
	
			fw.println("export var initialization_data=[").println();
			
			output_component_initialization_data(fw,sk,sort_component_list,request_response,process_bar);
			output_part_and_render_initialization_data(fw,sk,request_response,process_bar);
			output_component_initialization_program(fw,init_comp,init_init,sk,process_bar);
			output_shader_program(fw,sk,process_bar);
			output_common_shader_program_and_data(fw,sk);
			output_system_parameter(fw,sk);
	
			fw.println().println("];").println();
			
			fw.close();
			
			String my_file_name=fw.directory_name+fw.file_name;
			String tmp_file_name=my_file_name+".tmp";
			file_writer.file_rename(my_file_name,tmp_file_name);
			compress_file_data.do_compress(
					new File(tmp_file_name),new File(my_file_name),
					sk.system_par.file_read_write_buffer_size,"gzip");
			file_writer.file_delete(tmp_file_name);
			string_locker_cont.write_unlock(destination_file_name);
		}while(false);

		for(int i=0,ni=init_comp.size();i<ni;i++) {
			init_init.get(i).destroy();
			component comp=init_comp.get(i);
			if(comp.initialization!=null) {
				comp.initialization.destroy();
				comp.initialization=null;
			}
		}
	}
	public scene_initialization(
			scene_kernel sk,tree_string_locker_container string_locker_cont,
			client_request_response request_response,client_process_bar process_bar)
	{
		debug_information.println();
		debug_information.println("Begin initialize_render_driver");
		initialize_render_driver(sk,request_response,process_bar);
		debug_information.println("End initialize_render_driver");		
		
		debug_information.println();
		debug_information.println("Begin initialize_part_driver");
		initialize_part_drive(sk,request_response,process_bar);
		debug_information.println("End initialize_part_driver");
		
		debug_information.println();
		debug_information.println("Begin initialize_component_driver");
		initialize_component_driver(sk,request_response,process_bar);
		debug_information.println("End initialize_component_driver");
		
		debug_information.println();
		debug_information.println("Begin create initialization file");
		
		file_initialize(sk,string_locker_cont,request_response,process_bar);
		debug_information.println("End create initialization file");
	}
}