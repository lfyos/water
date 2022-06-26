#include "part.h"
#include <exception>  
#include "mesh.h"

void part::output_part_material(FbxMesh* fm, FbxNode* fn)
{
	std::ofstream f_material(directory_name + "part_" + std::to_string(register_number) + ".material");

	int materialCount = fn->GetMaterialCount();

	f_material<<"			" << "\"material\"	: [" << std::endl;

	for (int materialIndex = 0; materialIndex < materialCount; materialIndex++)
	{
		FbxSurfaceMaterial* pSurfaceMaterial = fn->GetMaterial(materialIndex);

		FbxDouble3 Ambient(0, 0, 0), Diffuse(0, 0, 0), Specular(0, 0, 0), Emissive(0, 0, 0);
		FbxDouble Shininess = 0;
		std::string name = pSurfaceMaterial->GetName();

		fbxsdk::FbxClassId phong_id, lambert_id;

		// Lambert material

		if (pSurfaceMaterial->GetClassId().Is(FbxSurfaceLambert::ClassId))
		{
			FbxSurfaceLambert* p = (FbxSurfaceLambert*)pSurfaceMaterial;
			Ambient = p->Ambient;
			Diffuse = p->Diffuse;
			Emissive = p->Emissive;
		}
		// Phong material
		if (pSurfaceMaterial->GetClassId().Is(FbxSurfacePhong::ClassId))
		{
			FbxSurfacePhong* p = ((FbxSurfacePhong*)pSurfaceMaterial);
			Ambient = p->Ambient;
			Diffuse = p->Diffuse;
			Specular = p->Specular;
			Emissive = p->Emissive;
			Shininess = p->Shininess;
		}


//		int lTextureIndex;
//		FBXSDK_FOR_EACH_TEXTURE(lTextureIndex)
//		{
//			FbxProperty lProperty = pSurfaceMaterial->FindProperty(FbxLayerElement::sTextureChannelNames[lTextureIndex]);
//			if (lProperty.IsValid()) {
//				FindAndDisplayTextureInfoByProperty(lProperty, lDisplayHeader, lMaterialIndex);
//			}
//		}



		f_material << "			{" << std::endl;

		f_material << "				\"ambient\"	:	[";
		f_material << Ambient[0] << "," << Ambient[1] << "," << Ambient[2] << ",1],"<< std::endl;
		f_material << "				\"diffuse\"	:	[";
		f_material << Diffuse[0] << "," << Diffuse[1] << "," << Diffuse[2] << ",1]," << std::endl;
		f_material << "				\"specular\"	:	[";
		f_material << Specular[0] << "," << Specular[1] << "," << Specular[2] << ",1]," << std::endl;
		f_material << "				\"emission\"	:	[";
		f_material << Emissive[0] << "," << Emissive[1] << "," << Emissive[2] << ",1]," << std::endl;
		f_material << "				\"shininess\"	:	"<< Shininess << std::endl;

		f_material << "			}" << ((materialIndex < (materialCount - 1)) ? "," : "") << std::endl;
	}

	f_material << "]" << std::endl;
	f_material.close();

	std::cout << "	Material counter:	" << materialCount << std::endl;
}
