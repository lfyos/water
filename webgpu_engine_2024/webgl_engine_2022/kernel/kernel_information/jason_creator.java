package kernel_information;

import kernel_common_class.jason_string;
import kernel_network.client_request_response;

public class jason_creator
{
	private client_request_response out;
	private String tab_space;
	private int number;
	
	public void print(String name,String value)
	{
		String str=(value==null)?"":value;
		
		if((number++)!=0)
			out.println(",");
		out.print  (tab_space,	"\"");
		out.print  (name,		"\" : ");
		out.print  (jason_string.change_string(str));
	}
	public void print(String name,boolean value)
	{
		if((number++)!=0)
			out.println(",");
		
		out.print  (tab_space,	"\"");
		out.print  (name,		"\" : ");
		out.print  (value?"true":"false");
	}
	public void print(String name,int value)
	{
		if((number++)!=0)
			out.println(",");
		
		out.print  (tab_space,	"\"");
		out.print  (name,		"\" : ");
		out.print  (value);
	}
	public void print(String name,long value)
	{
		if((number++)!=0)
			out.println(",");
		out.print  (tab_space,	"\"");
		out.print  (name,		"\" : ");
		out.print  (value);
	}
	public void print(String name,double value)
	{
		if((number++)!=0)
			out.println(",");
		out.print  (tab_space,"\"");
		out.print  (name,"\" : ");
		out.print  (value);
	}
	public void print(String name,String value[])
	{
		if((number++)!=0)
			out.println(",");
		out.print  (tab_space,	"\"");
		out.print  (name,		"\" : [");
		if(value!=null)
			for(int i=0;i<value.length;i++) {
				String str=(value[i]==null)?"":value[i];
				out.print((i>0)?",":"",jason_string.change_string(str));
			}
		out.print  ("]");
	}
	public void print(String name,boolean value[])
	{
		if((number++)!=0)
			out.println(",");
		out.print  (tab_space,"\"");
		out.print(name,"\" : [");
		for(int i=0,n=value.length;i<n;i++) {
			if(i>0)
				out.print(",");
			out.print(value[i]?"true":"false");
		}
		out.print("]");
	}
	public void print(String name,int value[])
	{
		if((number++)!=0)
			out.println(",");
		out.print  (tab_space,	"\"");
		out.print  (name,		"\" : [");
		
		if(value!=null)
			for(int i=0;i<value.length;i++)
				if(i>0)
					out.print(",",value[i]);
				else
					out.print(value[i]);
		
		out.print("]");
	}
	public void print(String name,long value[])
	{
		if((number++)!=0)
			out.println(",");
		out.print  (tab_space,	"\"");
		out.print  (name,		"\" : [");
		
		if(value!=null)
			for(int i=0;i<value.length;i++)
				if(i>0)
					out.print(",",value[i]);
				else
					out.print(value[i]);
		
		out.print("]");
	}
	public void print(String name,double value[])
	{
		if((number++)!=0)
			out.println(",");
		out.print  (tab_space,	"\"");
		out.print  (name,		"\" : [");
		
		if(value!=null)
			for(int i=0;i<value.length;i++)
				if(i>0)
					out.print(",",value[i]);
				else
					out.print(value[i]);
		
		out.print("]");
	}
	public void print(String name,String value[][])
	{
		if((number++)!=0)
			out.println(",");
		out.print  (tab_space,	"\"");
		out.println(name,		"\" : [");
		
		if(value!=null)
			for(int i=0,ni=value.length;i<ni;i++){
				out.print(tab_space,"		[");
				if(value[i]!=null)
					for(int j=0,nj=value[i].length;j<nj;j++) {
						String str=(value[i][j]==null)?"":value[i][j];
						out.print((j>0)?",":"",jason_string.change_string(str));
					}
				out.println((i<(ni-1))?"],":"]");
			}
		out.print(tab_space,"]");
	}
	public void print(String name,boolean value[][])
	{
		if((number++)!=0)
			out.println(",");
		out.print  (tab_space,	"\"");
		out.println(name,		"\" : [");
		
		if(value!=null)
			for(int i=0,ni=value.length;i<ni;i++){
				out.print(tab_space,"		[");
				
				if(value[i]!=null)
					for(int j=0,nj=value[i].length;j<nj;j++) {
						if(j>0)
							out.print(",");
						out.print(value[i][j]?"true":"false");
					}
				out.println((i<(ni-1))?"],":"]");
			}
		out.print(tab_space,"]");
	}
	
	public void print(String name,int value[][])
	{
		if((number++)!=0)
			out.println(",");
		out.print  (tab_space,	"\"");
		out.println(name,		"\" : [");
		
		if(value!=null)
			for(int i=0,ni=value.length;i<ni;i++){
				out.print(tab_space,"		[");
				
				if(value[i]!=null)
					for(int j=0,nj=value[i].length;j<nj;j++)
						if(j>0)
							out.print(",",value[i][j]);
						else
							out.print(value[i][j]);
				
				out.println((i<(ni-1))?"],":"]");
			}
		out.print(tab_space,"]");
	}
	public void print(String name,long value[][])
	{
		if((number++)!=0)
			out.println(",");
		out.print  (tab_space,	"\"");
		out.println(name,		"\" : [");
		
		if(value!=null)
			for(int i=0,ni=value.length;i<ni;i++){
				out.print(tab_space,"		[");
				
				if(value[i]!=null)
					for(int j=0,nj=value[i].length;j<nj;j++)
						if(j>0)
							out.print(",",value[i][j]);
						else
							out.print(value[i][j]);
				
				out.println((i<(ni-1))?"],":"]");
			}
		out.print(tab_space,"]");
	}
	public void print(String name,double value[][])
	{
		if((number++)!=0)
			out.println(",");
		out.print  (tab_space,	"\"");
		out.println(name,		"\" : [");
		
		if(value!=null)
			for(int i=0,ni=value.length;i<ni;i++){
				out.print(tab_space,"		[");
				
				if(value[i]!=null)
					for(int j=0,nj=value[i].length;j<nj;j++)
						if(j>0)
							out.print(",",value[i][j]);
						else
							out.print(value[i][j]);
				
				out.println((i<(ni-1))?"],":"]");
			}
		out.print(tab_space,"]");
	}
	public void print(String name,jason_creator creator)
	{
		if((number++)!=0)
			out.println(",");
		out.print  (tab_space,"\"");
		out.println(name,"\" :");
		
		String old_tab_space=creator.tab_space;
		creator.tab_space=tab_space+"\t";
		creator.output();
		creator.tab_space=old_tab_space;
	}
	public void print(String name,jason_creator creator[])
	{
		if((number++)!=0)
			out.println(",");
		out.print  (tab_space,"\"");
		out.println(name,"\" :[");
		
		if(creator!=null)
			for(int i=0,ni=creator.length;i<ni;i++){
				if(i>0)
					out.println(",");
				String old_tab_space=creator[i].tab_space;
				creator[i].tab_space=tab_space+"\t";
				creator[i].output();
				creator[i].tab_space=old_tab_space;
			}
		out.println();
		out.print(tab_space,"]");
	}
	public void print()
	{
	}
	public jason_creator(client_request_response my_out)
	{
		out=my_out;
		tab_space="";
		number=0;
	}
	public void output()
	{
		out.println(tab_space,"{");
		
		String old_tab_space=tab_space;
		tab_space+="\t";
		print();
		out.println();
		tab_space=old_tab_space;
		
		out.print  (tab_space,"}");
	}
}