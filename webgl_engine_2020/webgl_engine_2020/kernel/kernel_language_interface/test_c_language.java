package kernel_language_interface;

//1.����Ŀ¼ E:\water_all\webgl_engine_2020\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\webgl_engine_2020\WEB-INF\classes
//2.ִ��javah  -jni kernel_language_interface.test_c_language
//3.��1.����ʾ��Ŀ¼�У���������ͷ�ļ�(.h�ļ�) #include"kernel_language_interface_test_c_language.h"
//4.��visual studio 2019�д���һ����̬���ӿ���Ŀ������Ŀ��includeĿ¼�м�����������Ŀ¼
//		C:\Program Files\Java\jdk1.8.0_221\include
//		C:\Program Files\Java\jdk1.8.0_221\include\win32
//5.���κ�һ��.cpp�ļ��м���#include"kernel_language_interface_test_c_language.h"����ʵ������ĺ������������Ӽ������ɶ�̬���ӿ�
//6.���ɶ�̬���ӿ�ʱ������������64ƽ̨�Ĵ���
//7.�������ڻ�������path������һ��ָ������̬���ӿ�����Ŀ¼

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
