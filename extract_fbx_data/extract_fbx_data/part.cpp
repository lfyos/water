#include "part.h"
#include <exception>  

part::part(std::string my_directory_name)
{
	directory_name = my_directory_name;
	part_list_f =new std::ofstream(directory_name+"part.list");
	register_number = 0;
}

part::~part()
{
	part_list_f->close();
}

std::string part::process_name(std::string name)
{
	std::string ret_val = std::string(name);
	for (int i = 0, ni = (int)(ret_val.length()); i < ni; i++)
		switch (ret_val[i]) {
		case ' ':
		case '\t':
		case '\n':
		case '\r':
			ret_val[i] = '_';
			break;
		}
	return ret_val;
}
std::string part::register_node(FbxNode* assemble_node)
{
	if(assemble_node->GetNodeAttribute())
		switch(assemble_node->GetNodeAttribute()->GetAttributeType()){
		case FbxNodeAttribute::EType::eMesh:
			FbxMesh *fm = assemble_node->GetMesh();
			part_id_array[register_number]=fm->GetClassId();
			for (int i = 0; i < register_number; i++)
				if (part_id_array[i] == part_id_array[register_number])
					return "cad_part_" + std::to_string(i);

			std::string part_name = process_name(fm->GetClassId().GetName());
			(*part_list_f) << part_name << std::endl;
			(*part_list_f) << "cad_part_" << std::to_string(register_number) << std::endl;
			(*part_list_f) << "part_" << register_number << ".mesh" << std::endl;
			(*part_list_f) << "part_" << register_number << ".material" << std::endl;
			(*part_list_f) << "part_" << register_number << ".description" << std::endl;
			(*part_list_f) << "part_" << register_number << ".mp3" << std::endl;
			(*part_list_f) << std::endl;

			std::cout << "Begin extract mesh data for part "<< part_name <<" (NO. " << register_number<<")" << std::endl;
			this->output_part_material(fm,assemble_node);
			output_part_mesh(fm);
			std::cout << "End extract mesh data for part " << part_name << " (NO. " << register_number<< ")" << std::endl;

			return "cad_part_" + std::to_string(register_number++);
		}
	return std::string("NOT_exist_fbx_part_name");
}

void part::output_part_mesh(FbxMesh* pMesh)
{
	int average_number = 0,vertexId = 0,lPolygonCount = pMesh->GetPolygonCount(),*material_id=new int[lPolygonCount];
	double average_color[3]{ 0,0,0 }, average_texture[2]{ 0,0 }, box[6]{0,0,0,0,0,0};

	std::cout << "	Polygon counter:	" << lPolygonCount << std::endl;

	FbxGeometryElementMaterial* lMaterialElement = pMesh->GetElementMaterial(0);
	if (lMaterialElement == NULL) {
		std::cout << "	NO material" << std::endl;
		for (int i = 0; i < lPolygonCount; i++)
			material_id[i] = 0;
	}else if (lMaterialElement->GetMappingMode() == FbxGeometryElement::eByPolygon) {
		std::cout << "	FbxGeometryElement::eByPolygon material" << std::endl;
		for (int i = 0; i < lPolygonCount; i++)
			material_id[i] = lMaterialElement->GetIndexArray().GetAt(i);
	}else if (lMaterialElement->GetMappingMode() == FbxGeometryElement::eAllSame) {
		std::cout << "	FbxGeometryElement::eAllSame" << std::endl;
		int lMatId = lMaterialElement->GetIndexArray().GetAt(0);
		for (int i = 0; i < lPolygonCount; i++)
			material_id[i] = lMatId;
	}else {
		std::cout << "	NO material" << std::endl;
		for (int i = 0; i < lPolygonCount; i++)
			material_id[i] = 0;
	}

	std::ofstream f_mesh(directory_name + "part_" + std::to_string(register_number) + ".mesh.face");
	FbxVector4* lControlPoints = pMesh->GetControlPoints();
	for (int i = 0; i < lPolygonCount; i++) {
		int lPolygonSize = pMesh->GetPolygonSize(i);
		FbxVector4* vertex = new FbxVector4[lPolygonSize];
		FbxVector4* normal = new FbxVector4[lPolygonSize];
		FbxVector2* texture = new FbxVector2[lPolygonSize];
		FbxColor *color = new FbxColor[lPolygonSize];
		
		for (int lControlPointIndex,j = 0; j < lPolygonSize; j++) {
			if((lControlPointIndex = pMesh->GetPolygonVertex(i, j)) < 0)
				lControlPointIndex=0;

			vertex[j] = lControlPoints[lControlPointIndex];

			fbxsdk::FbxGeometryElementNormal * leNormal = pMesh->GetElementNormal(0);

			normal[j] = FbxVector4(0, 0, 1, 1);
			if(leNormal!=NULL)
				switch (leNormal->GetMappingMode()){
				case FbxGeometryElement::eByControlPoint:
					switch (leNormal->GetReferenceMode())
					{
					case FbxGeometryElement::eDirect:
						normal[j] = leNormal->GetDirectArray().GetAt(lControlPointIndex);
						break;
					case FbxGeometryElement::eIndexToDirect:
						normal[j] = leNormal->GetDirectArray().GetAt(leNormal->GetIndexArray().GetAt(lControlPointIndex));
						break;
					}
					break;
				case FbxGeometryElement::eByPolygonVertex:
					switch (leNormal->GetReferenceMode())
					{
					case FbxGeometryElement::eDirect:
						normal[j] = leNormal->GetDirectArray().GetAt(vertexId);
						break;
					case FbxGeometryElement::eIndexToDirect:
						normal[j] = leNormal->GetDirectArray().GetAt(leNormal->GetIndexArray().GetAt(vertexId));
						break;
					}
					break;
				}

			fbxsdk::FbxGeometryElementUV* leUV = pMesh->GetElementUV(0);
			texture[j] = FbxVector2(0, 0);
			if(leUV!=NULL)
				switch (leUV->GetMappingMode()) {
				case FbxGeometryElement::eByControlPoint:
					switch (leUV->GetReferenceMode()) {
					case FbxGeometryElement::eDirect:
						texture[j] = leUV->GetDirectArray().GetAt(lControlPointIndex);
						break;
					case FbxGeometryElement::eIndexToDirect:
						texture[j] = leUV->GetDirectArray().GetAt(leUV->GetIndexArray().GetAt(lControlPointIndex));
						break;
					}
					break;
				case FbxGeometryElement::eByPolygonVertex:
					switch (leUV->GetReferenceMode())
					{
					case FbxGeometryElement::eDirect:
					case FbxGeometryElement::eIndexToDirect:
						texture[j] = leUV->GetDirectArray().GetAt(pMesh->GetTextureUVIndex(i, j));
						break;
					}
					break;
				}

			fbxsdk::FbxGeometryElementVertexColor* leVtxc = pMesh->GetElementVertexColor(0);
			color[j] = FbxColor(1, 1, 1, 1);
			if(leVtxc!=NULL)
				switch (leVtxc->GetMappingMode())
				{
				case FbxGeometryElement::eByControlPoint:
					switch (leVtxc->GetReferenceMode())
					{
					case FbxGeometryElement::eDirect:
						color[j] = leVtxc->GetDirectArray().GetAt(lControlPointIndex);
						break;
					case FbxGeometryElement::eIndexToDirect:
						color[j] = leVtxc->GetDirectArray().GetAt(leVtxc->GetIndexArray().GetAt(lControlPointIndex));
						break;
					}
					break;
				case FbxGeometryElement::eByPolygonVertex:
					switch (leVtxc->GetReferenceMode())
					{
					case FbxGeometryElement::eDirect:
						color[j] = leVtxc->GetDirectArray().GetAt(vertexId);
						break;
					case FbxGeometryElement::eIndexToDirect:
						color[j] = leVtxc->GetDirectArray().GetAt(leVtxc->GetIndexArray().GetAt(vertexId));
						break;
					}
					break;
				}
			vertexId++;
		}

		double red = 0, green = 0, blue = 0;
		for (int j = 0; j < lPolygonSize; j++) {
			red += color[j].mRed;
			green += color[j].mGreen;
			blue += color[j].mBlue;
		}
		
		f_mesh << "/*	" << i << ".material			*/";
		f_mesh << "	" << red / lPolygonSize << "	" << green / lPolygonSize << "	" << blue / lPolygonSize;
		f_mesh << "	" << material_id[i]<<std::endl;
		f_mesh << "/*	" << i << ".vertex number		*/	" << lPolygonSize << std::endl;
		for (int j = 0; j < lPolygonSize; j++) {
			f_mesh << "/*		" << j << ".location		*/	";
			f_mesh << vertex[j][0] << "	" << vertex[j][1] << "	" << vertex[j][2] << "	1" << std::endl;
			
			f_mesh << "/*		" << j << ".normal		*/	";
			f_mesh << normal[j][0] << "	" << normal[j][1] << "	" << normal[j][2] << "	1" << std::endl;
				
			f_mesh << "/*		" << j << ".texture		*/	";
			f_mesh << texture[j][0] << "	" << texture[j][1] << "	0	1" << std::endl;

			f_mesh << "/*		" << j << ".color			*/";
			f_mesh << "	" << color[j].mRed << "	" << color[j].mGreen;
			f_mesh << "	" << color[j].mBlue << "	" << color[j].mAlpha << std::endl;

			f_mesh << std::endl;

			if (average_number <= 0)
			{
				box[0] = vertex[j][0];
				box[1] = vertex[j][1];
				box[2] = vertex[j][2];
				box[3] = vertex[j][0];
				box[4] = vertex[j][1];
				box[5] = vertex[j][2];
			}
			else {
				if (vertex[j][0] < box[0])
					box[0] = vertex[j][0];
				if (vertex[j][1] < box[1])
					box[1] = vertex[j][1];
				if (vertex[j][2] < box[2])
					box[2] = vertex[j][2];

				if (vertex[j][0] > box[3])
					box[3] = vertex[j][0];
				if (vertex[j][1] > box[4])
					box[4] = vertex[j][1];
				if (vertex[j][2] > box[5])
					box[3] = vertex[j][2];
			}

			average_color[0] += color[j].mRed;
			average_color[1] += color[j].mGreen;
			average_color[2] += color[j].mBlue;
			average_texture[0]+= texture[j][0];
			average_texture[1] += texture[j][1];
			average_number++;
		}
		f_mesh << std::endl;

		delete[] vertex ;
		delete[] normal;
		delete[] texture;
		delete[] color;
	}
	f_mesh.close();


	std::string head_str[]{ 
		"/*	version								*/	2021.07.15",
		"/*	origin material						*/	0.5	0.5	0.5	1",
		"/*	default material					*/	0.5	0.5	0.5	0",
		"/*	origin  vertex_location_extra_data	*/	1",
		"/*	default vertex_location_extra_data	*/	1",
		"/*	default vertex_normal_extra_data	*/	1",
		"/*	max_attribute_number				*/	2",
		"/*		0.attribute:	*/	" 
				+ std::to_string(average_texture[0] / average_number) + "	"
				+ std::to_string(average_texture[1] / average_number) + "	0	1",
		"/*		1.attribute:	*/	"
				+ std::to_string(average_color[0] / average_number) + "	"
				+ std::to_string(average_color[1] / average_number) + "	"
				+ std::to_string(average_color[2] / average_number) + "	1",
		"",
		"/*	body_number			*/	1",
		"/*	body    0  name		*/  body	/*  face_number */	1",
		"	/*	body 0 face 0  name	*/	face",
		"		/*	face type		*/	unknown	/*  parameter number	*/	0	/*  parameter	*/",
		"		/*	total_face_primitive_number	*/  "+ std::to_string(lPolygonCount),
		"		/*	face_attribute_number		*/  2",
		"		/*	face_face_box               */	"
			+ std::to_string(box[0] ) + "	"	+ std::to_string(box[1] ) + "	"	+ std::to_string(box[2] ) + "	"
			+ std::to_string(box[3] ) + "	"	+ std::to_string(box[4] ) + "	"	+ std::to_string(box[5] ),
		"		/*  loop number		*/	0",
		""
	};
	
	std::ofstream f_head(directory_name + "part_" + std::to_string(register_number) + ".mesh");

	for (int i = 0, ni = sizeof(head_str) / sizeof(head_str[0]); i < ni; i++)
		f_head << head_str[i] << std::endl;

	f_head.close();

	delete[]material_id;
}
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

		FbxClassId phong_id, lambert_id;

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


		int lTextureIndex;
		FBXSDK_FOR_EACH_TEXTURE(lTextureIndex)
		{
			FbxProperty lProperty = pSurfaceMaterial->FindProperty(FbxLayerElement::sTextureChannelNames[lTextureIndex]);
			if (lProperty.IsValid()) {
				FindAndDisplayTextureInfoByProperty(lProperty, lDisplayHeader, lMaterialIndex);
			}
		}



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
