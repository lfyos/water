#include "mesh.h"

void mesh::extract_material_id()
{
	int material_counter=pMesh->GetElementMaterialCount();
	material_id=new triangle_material_id[triangle_number];
	for (int i = 0; i < 4; i++) {
		default_material[i]=0;
		if (i >= material_counter)
			continue;
		FbxGeometryElementMaterial* lMaterialElement;
		if ((lMaterialElement = pMesh->GetElementMaterial(i)) == NULL)
			continue;
		switch (lMaterialElement->GetMappingMode()) {
		case FbxGeometryElement::eByPolygon:
			for (int j = 0; j < triangle_number; j++) {
				material_id[j].material_id[i] = lMaterialElement->GetIndexArray().GetAt(j);
				default_material[i]+= material_id[j].material_id[i];
			}
			default_material[i] =std::round(default_material[i]/triangle_number);
			break;
		case FbxGeometryElement::eAllSame:
			int my_material_id = lMaterialElement->GetIndexArray().GetAt(0);
			for (int j = 0; j < triangle_number; j++)
				material_id[j].material_id[i] = my_material_id;
			default_material[i] = my_material_id;
			break;
		}
	}
}
void mesh::extract_vertex()
{
	vertex = new attribute_vertex_container(1,triangle_number);
	FbxVector4* lControlPoints = pMesh->GetControlPoints();
	for (int j = 0; j < triangle_number; j++)
		for (int lPolygonSize = pMesh->GetPolygonSize(j),k = 0; k < 3; k++)
			if(k>=lPolygonSize)
				vertex->attr[0][j].set(k,0,0,0);
			else{
				int lControlPointIndex= pMesh->GetPolygonVertex(j, k);
				if (lControlPointIndex < 0)
					lControlPointIndex = 0;
				vertex->attr[0][j].set(k, lControlPoints[lControlPointIndex]);
			}
}
void mesh::extract_normal()
{
	int attribute_number = pMesh->GetElementNormalCount();
	normal = new attribute_vertex_container(attribute_number,triangle_number);
	for (int i = 0; i < attribute_number; i++) {
		FbxGeometryElementNormal* leNormal ;
		if ((leNormal = pMesh->GetElementNormal(i)) == NULL)
			continue;
		for (int vertexId = 0, j = 0; j < triangle_number; j++) {
			for (int k = 0; k < 3; k++, vertexId++) {
				normal->attr[i][j].vertex[k].Set(0, 0, 0);
				int lControlPointIndex = pMesh->GetPolygonVertex(j, k);
				switch (leNormal->GetMappingMode()) {
				case FbxGeometryElement::eByControlPoint:
					switch (leNormal->GetReferenceMode()) {
					case FbxGeometryElement::eDirect:
						normal->attr[i][j].set(k,leNormal->GetDirectArray().GetAt(lControlPointIndex));
						break;
					case FbxGeometryElement::eIndexToDirect:
						normal->attr[i][j].set(k, leNormal->GetDirectArray().
							GetAt(leNormal->GetIndexArray().GetAt(lControlPointIndex)));
						break;
					}
					break;
				case FbxGeometryElement::eByPolygonVertex:
					switch (leNormal->GetReferenceMode()) {
					case FbxGeometryElement::eDirect:
						normal->attr[i][j].set(k, leNormal->GetDirectArray().GetAt(vertexId));
						break;
					case FbxGeometryElement::eIndexToDirect:
						normal->attr[i][j].set(k, leNormal->GetDirectArray().
							GetAt(leNormal->GetIndexArray().GetAt(vertexId)));
						break;
					}
					break;
				}
			}
		}
	}
}
void mesh::extract_color()
{
	int attribute_number = pMesh->GetElementVertexColorCount();
	color = new attribute_vertex_container(attribute_number, triangle_number);
	for (int i = 0; i < attribute_number; i++) {
		FbxGeometryElementVertexColor* lecolor ;
		if ((lecolor = pMesh->GetElementVertexColor(i)) == NULL)
			continue;
		for (int vertexId = 0, j = 0; j < triangle_number; j++) {
			for (int k = 0; k < 3; k++, vertexId++) {
				color->attr[i][j].vertex[k].Set(0, 0, 0);
				int lControlPointIndex = pMesh->GetPolygonVertex(j, k);
				switch (lecolor->GetMappingMode()) {
				case FbxGeometryElement::eByControlPoint:
					switch (lecolor->GetReferenceMode()) {
					case FbxGeometryElement::eDirect:
						color->attr[i][j].set(k,lecolor->GetDirectArray().GetAt(lControlPointIndex));
						break;
					case FbxGeometryElement::eIndexToDirect:
						color->attr[i][j].set(k, lecolor->GetDirectArray().
							GetAt(lecolor->GetIndexArray().GetAt(lControlPointIndex)));
						break;
					}
					break;
				case FbxGeometryElement::eByPolygonVertex:
					switch (lecolor->GetReferenceMode()) {
					case FbxGeometryElement::eDirect:
						color->attr[i][j].set(k, lecolor->GetDirectArray().GetAt(vertexId));
						break;
					case FbxGeometryElement::eIndexToDirect:
						color->attr[i][j].set(k, lecolor->GetDirectArray().
							GetAt(lecolor->GetIndexArray().GetAt(vertexId)));
						break;
					}
					break;
				}
			}
		}
	}
}

void mesh::extract_texture()
{
	int attribute_number = pMesh->GetElementUVCount();
	texture = new attribute_vertex_container(attribute_number, triangle_number);
	for (int i = 0; i < attribute_number; i++) {
		FbxGeometryElementUV* letexture;
		if ((letexture = pMesh->GetElementUV(i)) == NULL)
			continue;
		for (int vertexId = 0, j = 0; j < triangle_number; j++){
			for (int k = 0; k < 3; k++, vertexId++) {
				texture->attr[i][j].vertex[k].Set(0, 0, 0);
				int lControlPointIndex = pMesh->GetPolygonVertex(j, k);
				switch (letexture->GetMappingMode()) {
				case FbxGeometryElement::eByControlPoint:
					switch (letexture->GetReferenceMode()) {
					case FbxGeometryElement::eDirect:
						texture->attr[i][j].set(k, letexture->GetDirectArray().GetAt(lControlPointIndex));
						break;
					case FbxGeometryElement::eIndexToDirect:
						texture->attr[i][j].set(k, letexture->GetDirectArray().
							GetAt(letexture->GetIndexArray().GetAt(lControlPointIndex)));
						break;
					}
					break;
				case FbxGeometryElement::eByPolygonVertex:
					switch (letexture->GetReferenceMode()){
					case FbxGeometryElement::eDirect:
						texture->attr[i][j].set(k, letexture->GetDirectArray().GetAt(vertexId));
						break;
					case FbxGeometryElement::eIndexToDirect:
						texture->attr[i][j].set(k, letexture->GetDirectArray().
							GetAt(letexture->GetIndexArray().GetAt(vertexId)));
						break;
					}
					break;
				}
			}
		}
	}
}

void mesh::extract_tangent()
{
	int attribute_number = pMesh->GetElementTangentCount();
	tangent = new attribute_vertex_container(attribute_number, triangle_number);
	for (int i = 0; i < attribute_number; i++) {
		fbxsdk::FbxGeometryElementTangent *letangent;
		if ((letangent = pMesh->GetElementTangent(i)) == NULL)
			continue;
		for (int vertexId = 0, j = 0; j < triangle_number; j++) {
			for (int k = 0; k < 3; k++, vertexId++) {
				tangent->attr[i][j].vertex[k].Set(0, 0, 0);
				int lControlPointIndex = pMesh->GetPolygonVertex(j, k);
				switch (letangent->GetMappingMode()) {
				case FbxGeometryElement::eByControlPoint:
					switch (letangent->GetReferenceMode()) {
					case FbxGeometryElement::eDirect:
						tangent->attr[i][j].set(k, letangent->GetDirectArray().GetAt(lControlPointIndex));
						break;
					case FbxGeometryElement::eIndexToDirect:
						tangent->attr[i][j].set(k, letangent->GetDirectArray().
							GetAt(letangent->GetIndexArray().GetAt(lControlPointIndex)));
						break;
					}
					break;
				case FbxGeometryElement::eByPolygonVertex:
					switch (letangent->GetReferenceMode()) {
					case FbxGeometryElement::eDirect:
						tangent->attr[i][j].set(k, letangent->GetDirectArray().GetAt(vertexId));
						break;
					case FbxGeometryElement::eIndexToDirect:
						tangent->attr[i][j].set(k, letangent->GetDirectArray().
							GetAt(letangent->GetIndexArray().GetAt(vertexId)));
						break;
					}
					break;
				}
			}
		}
	}
}
void mesh::write_mesh_head(std::string head_file_name)
{
	std::string attribute_number_string = std::to_string(
		((normal->attribute_number <= 0) ? 0 : (normal->attribute_number - 1)) +
		(color->attribute_number) + (texture->attribute_number) + (tangent->attribute_number));

	std::ofstream f_head(head_file_name);

	std::string str_1[]{
		"/*	version								*/	2021.07.15",
		"/*	origin material						*/	" + std::to_string(default_material[3]) + " "
														+ std::to_string(default_material[2]) + " "
														+ std::to_string(default_material[1]) + " "
														+ std::to_string(default_material[0]),
		"/*	default material					*/	" + std::to_string(default_material[3]) + " "
														+ std::to_string(default_material[2]) + " "
														+ std::to_string(default_material[1]) + " "
														+ std::to_string(default_material[0]),
		"/*	origin  vertex_location_extra_data	*/	1",
		"/*	default vertex_location_extra_data	*/	1",
		"/*	default vertex_normal_extra_data	*/	1",
		"/*	max_attribute_number				*/	" + attribute_number_string ,
	};

	for (int i = 0, ni = sizeof(str_1) / sizeof(str_1[0]); i < ni; i++)
		f_head << str_1[i] << std::endl;

	for (int i = 1, ni = normal->attribute_number; i < ni; i++)
		f_head << "/*		normal:" << i << "	*/	" << normal->caculate_average_string(i) << std::endl;
	for (int i = 0, ni = texture->attribute_number; i < ni; i++)
		f_head << "/*		texture:" << i << "	*/	" << texture->caculate_average_string(i) << std::endl;
	for (int i = 0, ni = color->attribute_number; i < ni; i++)
		f_head << "/*		color:" << i << "	*/	" << color->caculate_average_string(i) << std::endl;
	for (int i = 0, ni = tangent->attribute_number; i < ni; i++)
		f_head << "/*		tangent:" << i << "	*/	" << tangent->caculate_average_string(i) << std::endl;

	std::string str_2[]{
		"",
		"/*	body_number			*/	1",
		"/*	body    0  name		*/  fbx_body	/*  face_number */	1",
		"	/*	body 0 face 0  name	*/	fbx_face",
		"		/*	face type		*/	unknown	/*  parameter number	*/	0	/*  parameter	*/",
		"		/*	total_face_primitive_number	*/  " + std::to_string(triangle_number),
		"		/*	face_attribute_number		*/  " + attribute_number_string,
		"		/*	face_face_box               */	" + vertex->caculate_box_string(),
		"		/*  loop number					*/	0"
	};

	for (int i = 0, ni = sizeof(str_2) / sizeof(str_2[0]); i < ni; i++)
		f_head << str_2[i] << std::endl;

	f_head.close();
}

void mesh::write_mesh_data(std::string data_file_name)
{
	std::ofstream f_data(data_file_name);

	for (int triangle_id = 0; triangle_id < triangle_number; triangle_id++) {
		f_data << std::endl;
		f_data << "/*	triangle:" << triangle_id <<  "	material	*/";
		f_data << "	" << material_id[triangle_id].material_id[3];
		f_data << "	" << material_id[triangle_id].material_id[2];
		f_data << "	" << material_id[triangle_id].material_id[1];
		f_data << "	" << material_id[triangle_id].material_id[0]<<std::endl;
		f_data << "/*	vertex number			*/	3" << std::endl;
		for (int vertex_id = 0; vertex_id < 3; vertex_id++) {
			for (int attribute_id = 0; attribute_id < 1; attribute_id++) {
				FbxVector4 p = vertex->attr[attribute_id][triangle_id].vertex[vertex_id];
				f_data << "/*		" << vertex_id << ".location.	"<< attribute_id <<"		*/";
				for (int coordinator_id = 0; coordinator_id < 4; coordinator_id++)
					f_data << "	" << p[coordinator_id];
				f_data << std::endl;
			}
			if (normal->attribute_number > 0) 
				for (int attribute_id = 0; attribute_id < normal->attribute_number; attribute_id++) {
					FbxVector4 p = normal->attr[attribute_id][triangle_id].vertex[vertex_id];
					f_data << "/*		" << vertex_id << ".normal.	" << attribute_id << "		*/";
					for (int coordinator_id = 0; coordinator_id < 4; coordinator_id++)
						f_data << "	" << p[coordinator_id];
					f_data << std::endl;
				}
			else{
				double p[]{ 0,0,1,1 };
				f_data << "/*		" << vertex_id << ".normal.	0		*/";
				for (int coordinator_id = 0; coordinator_id < 4; coordinator_id++)
					f_data << "	" << p[coordinator_id];
				f_data << std::endl;
			}
			for (int attribute_id = 0; attribute_id < texture->attribute_number; attribute_id++) {
				FbxVector4 p = texture->attr[attribute_id][triangle_id].vertex[vertex_id];
				f_data << "/*		" << vertex_id << ".texture.	" << attribute_id << "		*/";
				for (int coordinator_id = 0; coordinator_id < 4; coordinator_id++)
					f_data << "	" << p[coordinator_id];
				f_data << std::endl;
			}
			for (int attribute_id = 0; attribute_id < color->attribute_number; attribute_id++) {
				FbxVector4 p = color->attr[attribute_id][triangle_id].vertex[vertex_id];
				f_data << "/*		" << vertex_id << ".color.	" << attribute_id << "		*/";
				for (int coordinator_id = 0; coordinator_id < 4; coordinator_id++)
					f_data << "	" << p[coordinator_id];
				f_data << std::endl;
			}
			for (int attribute_id = 0; attribute_id < tangent->attribute_number; attribute_id++) {
				FbxVector4 p = tangent->attr[attribute_id][triangle_id].vertex[vertex_id];
				f_data << "/*		" << vertex_id << ".tangent.	" << attribute_id << "		*/";
				for (int coordinator_id = 0; coordinator_id < 4; coordinator_id++)
					f_data << "	" << p[coordinator_id];
				f_data << std::endl;
			}
		}
	}
	f_data.close();
}