package kernel_common_class;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

public class execute_command extends Thread
{
	private String name;
	private long max_exec_time_length;
	private volatile Process process;
	private volatile boolean finished_flag;
	
	public void run()
	{ 
		for(long end_time=new Date().getTime()+max_exec_time_length;(end_time-new Date().getTime()>0);)
		{
			if(finished_flag) {
				save_result("execute_command monitor exit	:	"+name);
				return;
			}
			try{
				Thread.sleep(1000);
			}catch(Exception e) {
				Thread.yield();
			}
		}
		save_result("execute_command monitor kill process	:	"+max_exec_time_length+"	:	"+name);
		process.destroyForcibly();
	}
	public void save_result(String result)
	{	
	}
	public execute_command(String my_name,String cmd[],String par[],long my_max_exec_time_length)
	{
		finished_flag=false;
		name=my_name;
		max_exec_time_length=my_max_exec_time_length;
		
		try{
			process=Runtime.getRuntime().exec(cmd);
		}catch(Exception e) {
			save_result("execute_command start exception:"+name+"	:	"+e.toString());
			if(cmd!=null)
				for(int i=0,ni=cmd.length;i<ni;i++)
					save_result(cmd[i]);
			
			e.printStackTrace();
			return;
		}

		if(par!=null) {
			OutputStream os=process.getOutputStream();
			OutputStreamWriter osw=new OutputStreamWriter(os);
			try {
				for(int i=0,ni=par.length;i<ni;i++) {
					osw.write(par[i]);
					osw.write("\n");
				}
			}catch(Exception e) {
				save_result("execute_command send parameter exception:"+name+"	:	"+e.toString());
				e.printStackTrace();
			}
			try {
				osw.close();
			}catch(Exception e) {
				save_result("execute_command send parameter close writer exception:"+name+"	:	"+e.toString());
				e.printStackTrace();
			}
			try {
				os.close();
			}catch(Exception e) {
				save_result("execute_command send parameter close stream exception:"+name+"	:	"+e.toString());
				e.printStackTrace();
			}
		}

		start();
		
		InputStream is=process.getInputStream();
		InputStreamReader isr=new InputStreamReader(is);
		BufferedReader reader = new BufferedReader(isr);

		try {
			for(String str;(str=reader.readLine())!= null;)
				save_result(str);
		}catch(Exception e) {
			save_result("execute_command read message exception:"+name+"	:	"+e.toString());
			e.printStackTrace();
		}
		
		try {
			reader.close();
		}catch(Exception e) {
			save_result("execute_command close console 3 exception:"+name+"	:	"+e.toString());
			e.printStackTrace();
		}
		try {
			isr.close();
		}catch(Exception e) {
			save_result("execute_command close console 2 exception:"+name+"	:	"+e.toString());
			e.printStackTrace();
		}
		try {
			is.close();
		}catch(Exception e) {
			save_result("execute_command close console 1 exception:"+name+"	:	"+e.toString());
			e.printStackTrace();
		}
		
		is=process.getErrorStream();
		isr=new InputStreamReader(is);
		reader = new BufferedReader(isr);
		
		try {
			for(String str;(str=reader.readLine())!= null;)
				save_result(str);
		}catch(Exception e) {
			save_result("execute_command read exception message exception:"+name+"	:	"+e.toString());
			e.printStackTrace();
		}
		try {
			reader.close();
		}catch(Exception e) {
			save_result("execute_command close exception 3 exception:"+name+"	:	"+e.toString());
			e.printStackTrace();
		}
		try {
			isr.close();
		}catch(Exception e) {
			save_result("execute_command close exception 2 exception:"+name+"	:	"+e.toString());
			e.printStackTrace();
		}
		try {
			is.close();
		}catch(Exception e) {
			save_result("execute_command close exception 1 exception:"+name+"	:	"+e.toString());
			e.printStackTrace();
		}
		try {
			process.waitFor();
		}catch(Exception e) {
			save_result("execute_command process.waitFor() exception:"+name+"	:	"+e.toString());
			e.printStackTrace();
		}
		
		finished_flag=true;
		save_result("execute_command do join		:	"+name);
		try {
			join();
		}catch(Exception e) {
			;
		}
		save_result("execute_command terminated	:	"+name);
	}
}
