#include "extract_assemble_data.h"

extract_assemble_data::extract_assemble_data(aiNode* p,ofstream& fw,
			string pre_str,mesh_set_collector& msc,int max_mesh_number)
{
	fw << pre_str << "/*	component_name	*/	" << p->mName.C_Str() << endl;
	fw << pre_str << "/*	part_name		*/	" << msc.register_mesh(max_mesh_number,p) << endl;

	fw << pre_str << "/*	location		*/";

	fw << "	" << p->mTransformation.a1;
	fw << "	" << p->mTransformation.b1;
	fw << "	" << p->mTransformation.c1;
	fw << "	" << p->mTransformation.d1;

	fw << "	" << p->mTransformation.a2;
	fw << "	" << p->mTransformation.b2;
	fw << "	" << p->mTransformation.c2;
	fw << "	" << p->mTransformation.d2;

	fw << "	" << p->mTransformation.a3;
	fw << "	" << p->mTransformation.b3;
	fw << "	" << p->mTransformation.c3;
	fw << "	" << p->mTransformation.d3;

	fw << "	" << p->mTransformation.a4;
	fw << "	" << p->mTransformation.b4;
	fw << "	" << p->mTransformation.c4;
	fw << "	" << p->mTransformation.d4;

	fw << endl;
	
	fw << pre_str << "/*	child_number	*/	" << p->mNumChildren << endl ;

	pre_str += "\t";
	for (unsigned int i = 0; i < p->mNumChildren; i++)
		extract_assemble_data (p->mChildren[i], fw, pre_str, msc, max_mesh_number);
}
