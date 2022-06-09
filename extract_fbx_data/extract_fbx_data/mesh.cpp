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
void mesh::write_mesh_head(std::string directory_name)
{

	std::string str_1[]{
		"/*	version								*/	2021.07.15",
		"/*	origin material						*/	"	+ std::to_string(default_material[0]) + " "
														+ std::to_string(default_material[1]) + " " 
														+ std::to_string(default_material[2]) + " " 
														+ std::to_string(default_material[3]),
		"/*	default material					*/	"	+ std::to_string(default_material[0]) + " "
														+ std::to_string(default_material[1]) + " "
														+ std::to_string(default_material[2]) + " "
														+ std::to_string(default_material[3]),
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
		"		/*	total_face_primitive_number	*/  " + std::to_string(lPolygonCount),
		"		/*	face_attribute_number		*/  2",
		"		/*	face_face_box               */	"
			+ std::to_string(box[0]) + "	" + std::to_string(box[1]) + "	" + std::to_string(box[2]) + "	"
			+ std::to_string(box[3]) + "	" + std::to_string(box[4]) + "	" + std::to_string(box[5]),
		"		/*  loop number		*/	0",
		""
	};

	std::ofstream f_head(directory_name + "part_" + std::to_string(register_number) + ".mesh");

	for (int i = 0, ni = sizeof(head_str) / sizeof(head_str[0]); i < ni; i++)
		f_head << head_str[i] << std::endl;

	f_head.close();

	delete[]material_id;
}
