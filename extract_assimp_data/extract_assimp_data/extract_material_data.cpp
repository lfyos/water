#include "extract_material_data.h"
#include "extract_mesh_data.h"

using namespace std;

void extract_material_data::do_extract_routine(int vertex_color_type,
	struct aiMaterial* m,ofstream& fw, string pre_str, default_parameter& par)
{
	static aiTextureType my_type[] = {
		aiTextureType::aiTextureType_DIFFUSE ,
		aiTextureType::aiTextureType_SPECULAR,
		aiTextureType::aiTextureType_AMBIENT,
		aiTextureType::aiTextureType_EMISSIVE,
		aiTextureType::aiTextureType_HEIGHT,
		aiTextureType::aiTextureType_NORMALS ,
		aiTextureType::aiTextureType_SHININESS,
		aiTextureType::aiTextureType_OPACITY,
		aiTextureType::aiTextureType_DISPLACEMENT ,
		aiTextureType::aiTextureType_LIGHTMAP,
		aiTextureType::aiTextureType_REFLECTION
	};
	
	int fragment_color_type = 0;
	fw << pre_str << "\"texture\"				:	[" << endl;
	for (int texture_number,i = 0, ni = sizeof(my_type) / sizeof(my_type[0]); i < ni; i++)
		if ((texture_number=m->GetTextureCount(my_type[i])) > 0) 
			for (int j = 0; j < texture_number; j++) {
				static aiString path;
				static aiTextureMapping mapping;
				static unsigned int uvindex;
				static ai_real blend;
				static aiTextureOp op;
				static aiTextureMapMode mapmode;
				static aiUVTransform t;
				if (m->GetTexture(my_type[i], j, &path, &mapping, &uvindex, &blend, &op, &mapmode) == aiReturn_SUCCESS) {
					fragment_color_type = 1;
					fw << pre_str << "\t{" << endl;
					fw << pre_str << "\t\t\"texture_file\"	:	\"" << (path.C_Str()) << "\"," << endl;
					switch (mapmode) {
					case aiTextureMapMode_Wrap:
						fw << pre_str << "\t\t\"u_wrapmode\"	:	\"repeat\"," << endl;
						fw << pre_str << "\t\t\"v_wrapmode\"	:	\"repeat\"," << endl;
						break;
					case aiTextureMapMode_Clamp:
						fw << pre_str << "\t\t\"u_wrapmode\"	:	\"clamp_to_edge\"," << endl;
						fw << pre_str << "\t\t\"v_wrapmode\"	:	\"clamp_to_edge\"," << endl;
						break;
					case aiTextureMapMode_Mirror:
					default:
						fw << pre_str << "\t\t\"u_wrapmode\"	:	\"mirrored_repeat\"," << endl;
						fw << pre_str << "\t\t\"v_wrapmode\"	:	\"mirrored_repeat\"," << endl;
						break;
					}
					fw << pre_str << "\t\t\"mag_filter\"	:	\"linear\"," << endl;
					fw << pre_str << "\t\t\"min_filter\"	:	\"linear\"," << endl;
					
					if (m->Get(AI_MATKEY_UVTRANSFORM(my_type[i], j), t) != aiReturn_SUCCESS)
						t = aiUVTransform();

					fw << pre_str << "\t\t\"matrix\"		:	[" 
						<< t.mTranslation.x << "," << t.mTranslation.y << ","
						<< (t.mRotation *180/PI) << ","
						<< t.mScaling.x << "," << t.mScaling.y << "]" << endl;
					i = ni;
					fw << pre_str << "\t}" << endl;
					break;
				}
			}
	fw << pre_str << "]," << endl;

	fw << pre_str << "\"vertex_color_type\"		:	" << vertex_color_type<<","<<endl;
	fw << pre_str << "\"vertex_color_parameter\":	[0,0,0,0]," << endl;
	fw << pre_str << "\"fragment_color_type\"	:	" << fragment_color_type << "," << endl;

	aiColor4D ambient, diffuse, specular, emissive;
	double shininess;

	fw << pre_str << "\"color\"		:	[";
	fw << par.color[0] << "," << par.color[1] << "," << par.color[2] << "," << par.color[3] << "]," << endl;

	fw << pre_str << "\"ambient\"	:	[";
	if (m->Get(AI_MATKEY_COLOR_AMBIENT, ambient) == aiReturn_SUCCESS)
		fw << ambient.r << "," << ambient.g << "," << ambient.b << "," << ambient.a << "]," << endl;
	else
		fw << par.ambient[0] << "," << par.ambient[1] << "," << par.ambient[2] << "," << par.ambient[3] << "]," << endl;


	fw << pre_str << "\"diffuse\"	:	[";
	if (m->Get(AI_MATKEY_COLOR_DIFFUSE, diffuse) == aiReturn_SUCCESS)
		fw << diffuse.r << "," << diffuse.g << "," << diffuse.b << "," << diffuse.a << "]," << endl;
	else
		fw << par.diffuse[0] << "," << par.diffuse[1] << "," << par.diffuse[2] << "," << par.diffuse[3] << "]," << endl;

	fw << pre_str << "\"specular\"	:	[";
	if (m->Get(AI_MATKEY_COLOR_SPECULAR, specular) == aiReturn_SUCCESS)
		fw << specular.r << "," << specular.g << "," << specular.b << "," << specular.a << "]," << endl;
	else
		fw << par.specular[0] << "," << par.specular[1] << "," << par.specular[2] << "," << par.specular[3] << "]," << endl;

	fw << pre_str << "\"emission\"	:	[";
	if (m->Get(AI_MATKEY_COLOR_EMISSIVE, emissive) == aiReturn_SUCCESS)
		fw << emissive.r << "," << emissive.g << "," << emissive.b << "," << emissive.a << "]," << endl;
	else
		fw << par.emissive[0] << "," << par.emissive[1] << "," << par.emissive[2] << "," << par.emissive[3] << "]," << endl;

	fw << pre_str << "\"shininess\"	:	";
	if (m->Get(AI_MATKEY_SHININESS, shininess) == aiReturn_SUCCESS)
		fw << shininess << endl;
	else
		fw <<  par.shininess << endl;

	return;
}
extract_material_data::extract_material_data(
	struct aiMesh** p_mesh, unsigned int n_mesh,
	struct aiMaterial** mMaterials,unsigned int mNumMaterials, 
	string target_directory_name, default_parameter& par,
	mesh_set_collector &msc)
{
	int *flag=new int[mNumMaterials];
	for (unsigned int i = 0; i < mNumMaterials; i++)
		flag[i] = 0;
	for (unsigned int i = 0; i < n_mesh; i++)
		if(p_mesh[i]->GetNumColorChannels()>0)
			flag[p_mesh[i]->mMaterialIndex]++;

	string pre_str="\t\t\t";
	ofstream fw(target_directory_name+"shader_material.txt");
	fw << pre_str << "\"material\"	:	[" << endl;
	for (unsigned int i = 0; i < mNumMaterials; i++) {
		fw<< pre_str << "\t{" << endl;
		do_extract_routine((flag[i] > 0) ? 0 : 2,mMaterials[i], fw,pre_str+"\t\t",par);
		fw << pre_str << ((i==(mNumMaterials-1))?"\t}":"\t},") << endl;
	}
	fw << pre_str << "]" << endl;
	fw.close();

	delete[]flag;
}
