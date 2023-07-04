function component_init_function(component_init_fun_array,render)
{
	for(var i=0,ni=component_init_fun_array.length;i<ni;i++){
		if(typeof(component_init_fun_array[i])!="object")
			continue;
		var component_id=component_init_fun_array[i].component_id;
		var component_name=component_init_fun_array[i].component_name;
		var init_function=component_init_fun_array[i].initialization_function;
		
		if(typeof(init_function)!="string"){
			alert("component init_function program is not string:	"+component_name+"		"+component_id);
			alert(component_init_fun_array[i].initialization_function);
			continue;
		}
		if((init_function=init_function.trim()).length<=0){
			alert("component init_function program is empty:	"+component_name+"		"+component_id);
			alert(component_init_fun_array[i].initialization_function);
			continue;
		}
		try{
			init_function=(eval("["+init_function+"]"))[0];
		}catch(e){
			alert("Error compile component init_function:	"
				+component_name+"		"+component_id+"		"+e.toString());
			alert(component_init_fun_array[i].initialization_function);
			continue;
		}
		if(typeof(init_function)!="function"){
			alert("component init_function is NOT FUNCTION:	"
				+component_name+"		"+component_id+"		"+e.toString());
			alert(component_init_fun_array[i].initialization_function);
			continue;
		}
		try{
			init_function(component_name,component_id,render);
		}catch(e){
			alert("Error execute component init_function:	"
				+component_name+"		"+component_id+"		"+e.toString());
			alert(component_init_fun_array[i].initialization_function);
			continue;
		}
	}
}
