package kernel_engine;

import java.io.File;

import kernel_common_class.compress_file_data;
import kernel_common_class.debug_information;
import kernel_component.component;
import kernel_file_manager.file_directory;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_network.client_request_response;
import kernel_part.part;
import kernel_driver.component_driver;


public class engine_initialization
{
	private component sort_component_array[];
	
	public engine_initialization(engine_kernel ek,client_request_response request_response)
	{
		if((sort_component_array=ek.component_cont.get_sort_component_array())==null)
			sort_component_array=new component[] {};
		
		debug_information.println();
		debug_information.println("Begin initialize_part_driver");
		part_driver_initialize(ek,request_response);
		debug_information.println("End initialize_part_driver");
		
		debug_information.println();
		debug_information.println("Begin initialize_component_driver");
		initialize_component_driver(ek,request_response);
		debug_information.println("End initialize_component_driver");
		
		debug_information.println();
		debug_information.println("Begin create initialization file");
		file_initialize(ek);
		debug_information.println("End create initialization file");
		
		for(int i=0,ni=sort_component_array.length;i<ni;i++)
			if(sort_component_array[i].initialization!=null) {
				sort_component_array[i].initialization.destroy();
				sort_component_array[i].initialization=null;
			};
	}
	private void part_driver_initialize(engine_kernel ek,client_request_response request_response)
	{
		int process_parts_sequence[][]=ek.process_part_sequence.process_parts_sequence;
		for(int i=0,ni=process_parts_sequence.length;i<ni;i++){
			int render_id=process_parts_sequence[i][0];
			int part_id  =process_parts_sequence[i][1];
			if(ek.render_cont.renders[render_id].parts[part_id].driver==null)
				continue;
			part my_p=ek.render_cont.renders[render_id].parts[part_id];
			try {
				my_p.driver.initialize_part_driver(my_p,ek,request_response);
			}catch(Exception e){
				debug_information.println("Part driver initialize_part_driver fail:	",e.toString());
					
				debug_information.println("Part user name:		",	my_p.user_name);
				debug_information.println("Part system name:	",	my_p.system_name);
				debug_information.println("Part mesh_file_name:",	my_p.directory_name+my_p.mesh_file_name);
				debug_information.println("Part material_file_name:",my_p.directory_name+my_p.material_file_name);
				debug_information.println("part_file_directory:",
						file_directory.part_file_directory(my_p,ek.system_par,ek.scene_par));
				e.printStackTrace();
			}
		}
	}
	private void initialize_component_driver(engine_kernel ek,client_request_response request_response)
	{
		for(int i=0,ni=sort_component_array.length;i<ni;i++)
			for(int j=0,driver_number=sort_component_array[i].driver_number();j<driver_number;j++) {
				component_driver cd=sort_component_array[i].driver_array[j];
				try {
					cd.initialize_component_driver(sort_component_array[i],j,ek,request_response);
				}catch(Exception e) {
					debug_information.println("Component driver initialize fail:	",e.toString());
					debug_information.println("Component name:",sort_component_array[i].component_name);
					debug_information.println("Component file:",
							sort_component_array[i].component_directory_name+sort_component_array[i].component_file_name);
					debug_information.println("Component driver id:",j);
					e.printStackTrace();
				}
			}
	}
	
	private void file_initialize(engine_kernel ek)
	{
		String destination_file_name;
		destination_file_name =ek.scene_par.scene_proxy_directory_name;
		destination_file_name+=ek.scene_par.proxy_initialization_file_name;
		
		long my_last_time,last_time=ek.get_file_last_modified_time();

		int collect_init_comp_number=0;
		component init_comp[]=new component[sort_component_array.length];
		for(int i=0,ni=sort_component_array.length;i<ni;i++)
			if(sort_component_array[i].initialization.initialization_program!=null)
				if(sort_component_array[i].initialization.initialization_program_charset!=null)
					if(sort_component_array[i].initialization.initialization_program.length>0)
						if(sort_component_array[i].initialization.initialization_program_charset.length>0)
							init_comp[collect_init_comp_number++]=sort_component_array[i];

		String initialization_program_file_name[][]=new String[collect_init_comp_number][];
		
		for(int i=0,ni=collect_init_comp_number;i<ni;i++){
			int init_length=init_comp[i].initialization.initialization_program.length;
			initialization_program_file_name[i]=new String[init_length];
			for(int j=0,nj=initialization_program_file_name[i].length;j<nj;j++){
				initialization_program_file_name[i][j]=null;
				if(init_comp[i].initialization.initialization_program_charset[j]!=null){
					File f;
					String file_name=init_comp[i].initialization.initialization_program[j];
					initialization_program_file_name[i][j]=init_comp[i].component_directory_name+file_name;
					if(!(f=new File(initialization_program_file_name[i][j])).exists()){
						initialization_program_file_name[i][j]=ek.scene_directory_name+file_name;
						if(!(f=new File(initialization_program_file_name[i][j])).exists()) {
							initialization_program_file_name[i][j]=ek.scene_par.directory_name+file_name;
							if(!(f=new File(initialization_program_file_name[i][j])).exists()){
								initialization_program_file_name[i][j]=ek.system_par.data_root_directory_name+file_name;
								if(!(f=new File(initialization_program_file_name[i][j])).exists()){
									initialization_program_file_name[i][j]=null;
									continue;
								}
							}
						}
					}
					if(last_time<(my_last_time=f.lastModified()))
						last_time=my_last_time;
				}
			}
		}
		
		if((new File(destination_file_name)).lastModified()>last_time)
			return;
		
		file_writer fw=new file_writer(destination_file_name,ek.system_par.network_data_charset);

		fw.println("[");
		fw.print  (sort_component_array.length);
		fw.println(",");
		
		fw.println("[");
		for(int i=0,ni=sort_component_array.length,number=0;i<ni;i++){
			if((number++)>0)
				fw.println(",");
			fw.print("\t\"",sort_component_array[i].component_name.replace("\\","\\\\").
					replace("\n","\\n\\r").replace("\"","\\\"").replace("\r","\\n\\r"));
			fw.print("\",",sort_component_array[i].component_id);
		}
		fw.println();
		fw.println("],");
		fw.println();
		
		fw.println("[");
		int id[][][][]=ek.component_cont.part_component_id_and_driver_id;
		for(int render_id=0,render_number=id.length;render_id<render_number;render_id++){
			fw.println("	[");
			for(int part_id=0,part_number=id[render_id].length;part_id<part_number;part_id++){
				fw.println("		[");
				for(int i=0,ni=id[render_id][part_id].length;i<ni;i++) {
					int component_id=id[render_id][part_id][i][0];
					int driver_id	=id[render_id][part_id][i][1];
					fw.print  ("			[",component_id);
					fw.print  (",",driver_id);
					fw.println("],");
				}
				part p=ek.render_cont.renders[render_id].parts[part_id];
				fw.println("			",Integer.toString(p.permanent_render_id)+",");
				fw.println("			",Integer.toString(p.permanent_part_id));
				fw.println((part_id==(part_number-1))?"		]":"		],");
			}
			fw.println();
			fw.println((render_id==(render_number-1))?"	]":"	],");	
		}
		fw.println("],");
		fw.println();

		fw.println("[");
		for(int i=0,number=0,ni=collect_init_comp_number;i<ni;i++){
			for(int j=0,nj=initialization_program_file_name[i].length;j<nj;j++) {
				String my_program_charset,my_program_text=init_comp[i].initialization.initialization_program[j];
				if((my_program_charset=init_comp[i].initialization.initialization_program_charset[j])!=null){
					if(initialization_program_file_name[i][j]==null)
						continue;
					my_program_text=file_reader.get_text(
							initialization_program_file_name[i][j],my_program_charset);
				}
				
				if((number++)>0)
					fw.println(",");
				fw.println("\t{");
				
				fw.print  ("\t\tcomponent_id			:	",	init_comp[i].component_id);
				fw.println(",");
					
				fw.print  ("\t\tcomponent_name			:	\"",init_comp[i].component_name.
							replace("\\","\\\\").replace("\n","\\n\\r").
							replace("\"","\\\"").replace("\r","\\n\\r"));
				fw.println("\",");

				fw.println("\t\tinitialization_function	:	");
				fw.println(my_program_text);
				
				fw.println();
				fw.print  ("\t}");
			}
		}
		fw.println();
		fw.println("],");
		fw.println();

		fw.println("[");
		for(int render_id=0,render_number=ek.render_cont.renders.length;render_id<render_number;render_id++) {
			fw.print_file(file_directory.render_file_directory(
				ek.render_cont.renders[render_id].parts[0].part_type_id,
				ek.render_cont.renders[render_id].parts[0].permanent_render_id,
				ek.system_par,ek.scene_par)+"program.txt");
			fw.println((render_id<(render_number-1))?",":"");
		}
		fw.println("]");
		fw.println();
		fw.println("]");
		
		fw.close();
		
		String tmp_file_name=fw.directory_name+fw.file_name+".tmp";
		file_writer.file_rename(fw.directory_name+fw.file_name,tmp_file_name);
		compress_file_data.do_compress(
			new File(tmp_file_name),new File(fw.directory_name+fw.file_name),
			ek.system_par.response_block_size,"gzip");
		file_writer.file_delete(tmp_file_name);

		return;
	}
}
