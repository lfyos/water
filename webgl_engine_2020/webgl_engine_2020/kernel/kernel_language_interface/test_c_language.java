package kernel_language_interface;

//1.进入目录 E:\water_all\webgl_engine_2020\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\webgl_engine_2020\WEB-INF\classes
//2.执行javah  -jni kernel_language_interface.test_c_language
//3.在1.中所示的目录中，即可生成头文件(.h文件) #include"kernel_language_interface_test_c_language.h"
//4.在visual studio 2019中创建一个动态链接库项目，在项目的include目录中加入如下两个目录
//		C:\Program Files\Java\jdk1.8.0_221\include
//		C:\Program Files\Java\jdk1.8.0_221\include\win32
//5.在任何一个.cpp文件中加入#include"kernel_language_interface_test_c_language.h"，并实现里面的函数，编译连接即可生成动态链接库
//6.生成动态连接库时，别忘了生成64平台的代码
//7.别忘了在环境变量path中增加一项指向您动态链接库所在目录

public class test_c_language
{
	public native void test();
	
	static {
		System.loadLibrary("test_c_language");
	}
	
	public static void main(String[] args)
	{
		test_c_language t = new test_c_language();
		
		t.test();
	}
}
