#include "extract_mesh_data.h"

string extract_mesh_data::cut_special_char(string str)
{
    char ch;
    string ret_val = "";
    for (int i = 0, ni = (int)(str.length()); i < ni; i++)
        switch ((ch = str.at(i))) {
        case ' ':
        case '\t':
        case '\n':
        case '\r':
            break;
        default:
            ret_val += ch;
            break;
        }
    return ret_val;
}

extract_mesh_data::extract_mesh_data(
    struct aiMesh** p_mesh, unsigned int n_mesh, 
    string target_directory_name, mesh_set_collector& msc)
{
    ofstream f_list(target_directory_name + "part.list");
    for (int i = 0; i < msc.mesh_set_number; i++) {
        mesh_set* ms = msc.mesh_set_array[i];
        string target_file_name = target_directory_name + (ms->system_name) + ".mesh";

        f_list << msc.mesh_set_array[i]->user_name << endl;
        f_list << "  " << ms->system_name << endl;
        f_list << "  " << ms->system_name << ".mesh" << endl;
        f_list << "  " << ms->system_name << ".material" << endl;
        f_list << "  " << ms->system_name << ".description" << endl;
        f_list << "  " << ms->system_name << ".mp3" << endl;
        f_list << endl;

        cout << i << ".extract mesh: ";
        cout << "   " << msc.mesh_set_array[i]->user_name;
        cout << "   "<<ms->system_name;
        cout << "   " << n_mesh<<"  :";
        for (int j = 0; j < (int)n_mesh; j++)
            if (ms->mesh_id_array[j])
                cout << "   "<<j;
        cout<< endl;

        triangle_number = 0;
        line_vertex_number = 0;
        point_vertex_number = 0;
        max_texture_number = 0;
        max_color_number = 0;
        triangle_box.reset();
        edge_box.reset();
        point_box.reset();
        for (int j = 0, nj = sizeof(texture_box) / sizeof(texture_box[0]); j < nj; j++)
            texture_box[j].reset();
        for (int j = 0, nj = sizeof(color_box) / sizeof(color_box[0]); j < nj; j++)
            color_box[j].reset();

        for (int num,j = 0; j < (int)n_mesh; j++)
            if (ms->mesh_id_array[j]) {
                if ((num = p_mesh[j]->GetNumUVChannels()) > max_texture_number)
                    max_texture_number = num;
                if((num=p_mesh[j]->GetNumColorChannels())> max_color_number)
                    max_color_number = num;
            }
        ofstream f_triangles(target_file_name + ".face");
        ofstream f_lines(target_file_name + ".edge");
        ofstream f_points(target_file_name + ".point");

        for(int j=0, material_id=0;j< (int)n_mesh;j++)
            if (ms->mesh_id_array[j]) {
                create_part_mesh_triangles(p_mesh[j], material_id++,f_triangles);
                create_part_mesh_lines_points(p_mesh[j], f_lines, f_points);
            }
        f_triangles.close();
        f_lines.close();
        f_points.close();

        ofstream f_head(target_file_name);
        create_part_mesh_head(f_head);
        f_head.close();

        ofstream f_material(target_directory_name + (ms->system_name) + ".material");
        f_material << "\t\t\t\"material\"	:	[" ;

        string pre_str = "";
        for (int j = 0, material_id = 0; j < (int)n_mesh; j++)
            if (ms->mesh_id_array[j]) {
                f_material << pre_str << endl;
                pre_str = ",";
                f_material << "\t\t\t\t" << (p_mesh[j]->mMaterialIndex);
            }

        f_material << endl  << "\t\t\t]" << endl;
        f_material.close();
    }
    f_list.close();
}

void extract_mesh_data::create_part_mesh_triangles(struct aiMesh* p_mesh, int material_id,ofstream& f_triangles)
{
    if(((p_mesh->mPrimitiveTypes & 0x4) == 0)||(p_mesh->mNumFaces<=0))
        return;
    f_triangles << "/*   triangle_number:" << p_mesh->mNumFaces << " */" << endl;
    for (int i = 0, ni = p_mesh->mNumFaces; i < ni; i++) {
        triangle_number++;
        f_triangles << "/*  triangle:"<<i<<",material  */  0   0   0   "<< material_id <<"   ";
        f_triangles << "/*  vertex number  */  "<< p_mesh->mFaces[i].mNumIndices << endl;
        for (int j = 0, nj = p_mesh->mFaces[i].mNumIndices; j < nj; j++) {
            int my_id = p_mesh->mFaces[i].mIndices[j];
            {
                double x = p_mesh->mVertices[my_id].x;
                double y = p_mesh->mVertices[my_id].y;
                double z = p_mesh->mVertices[my_id].z;
                triangle_box.add_vertex(x, y, z,1);
                f_triangles << "/*		" << j << ".location		*/  ";
                f_triangles << x << "     " << y << "     " << z << "  1" << endl;
            }
            if (!(p_mesh->HasNormals()))
                f_triangles << "/*		" << j << ".no normal		*/  0   0   1   1" << endl; 
            else{
                double x = p_mesh->mNormals[my_id].x;
                double y = p_mesh->mNormals[my_id].y;
                double z = p_mesh->mNormals[my_id].z;
                f_triangles << "/*		" << j << ".normal		*/  ";
                f_triangles << x << "     " << y << "     " << z << "  1" << endl;
            }

            for (int k = 0; k < max_texture_number; k++) {
                f_triangles << "/*		" << j << ".texture:"<<k<<"     */  ";
                if (!(p_mesh->HasTextureCoords(k)))
                    f_triangles <<"0   0   0   1" << endl;
                else {
                    double x = p_mesh->mTextureCoords[k][my_id].x;
                    double y = p_mesh->mTextureCoords[k][my_id].y;
                    double z = p_mesh->mTextureCoords[k][my_id].z;
                    texture_box[k].add_vertex(x,y,z,1);
                    f_triangles << x << "     " << y << "     " << z << "  1" << endl;
                }
            }
            for (int k = 0; k < max_color_number; k++) {
                f_triangles << "/*		" << j << ".color:" << k << "     */  ";
                if (!(p_mesh->HasVertexColors(k)))
                    f_triangles << "0   0   0   1" << endl;
                else{
                    double x = p_mesh->mColors[k][my_id].r;
                    double y = p_mesh->mColors[k][my_id].g;
                    double z = p_mesh->mColors[k][my_id].b;
                    double w = p_mesh->mColors[k][my_id].a;
                    texture_box[k].add_vertex(x, y, z,w);
                    f_triangles << x << "     " << y << "     " << z << "  "<<w << endl;
                }
            }
        }
    }
}
void extract_mesh_data::create_part_mesh_lines_points(struct aiMesh* p_mesh, ofstream& f_lines, ofstream& f_points)
{
    if (((p_mesh->mPrimitiveTypes & 0x3) == 0)||(p_mesh->mNumFaces<=0))
        return;
    for (int i = 0, ni = p_mesh->mNumFaces; i < ni; i++) {
        for (int j = 0, nj = p_mesh->mFaces[i].mNumIndices; j < nj; j++) {
            int my_id = p_mesh->mFaces[i].mIndices[j];
            double x = p_mesh->mVertices[my_id].x;
            double y = p_mesh->mVertices[my_id].y;
            double z = p_mesh->mVertices[my_id].z;

            if ((p_mesh->mPrimitiveTypes & 0x2) != 0) {
                edge_box.add_vertex(x, y, z, 1);
                f_lines << "/*		" << (line_vertex_number++);
            }else{
                point_box.add_vertex(x, y, z, 1);
                f_points << "/*		" << (point_vertex_number++);
            }
            f_lines << ".location		*/  " << x << "     " << y << "     " << z;
            f_lines<<"  1	/*	material	*/	0	0	0	" << p_mesh->mMaterialIndex << endl;
        }
    }
}
void extract_mesh_data::create_part_mesh_head(ofstream& f_head)
{
    f_head << "/*	version					            */	2021.07.15"<<endl;
    f_head << "/*	origin material			            */	0  0  0  0" << endl;
    f_head << "/*	default material		            */	0  0  0  0" << endl;
    f_head << "/*	origin extra_data		            */	1" << endl;
    f_head << "/*	default vertex_location_extra_data	*/	1" << endl; 
    f_head << "/*	default vertex_normal_extra_data	*/	1" << endl;
    f_head << "/*	max_attribute_number	            */  ";
    f_head << (max_texture_number+max_color_number) << endl;

    for (int i = 0; i < max_texture_number; i++) {
        f_head << "/*      attribute_" << i << "	    */";
        if(texture_box[i].vertex_number<=0)
            f_head << "  0  0  0  1" << endl;
        else {
            f_head << "  " << (texture_box[i].sum_x / texture_box[i].vertex_number);
            f_head << "  " << (texture_box[i].sum_y / texture_box[i].vertex_number);
            f_head << "  " << (texture_box[i].sum_z / texture_box[i].vertex_number);
            f_head << "  " << (texture_box[i].sum_w / texture_box[i].vertex_number) << endl;
        }
    }
    for (int i = 0; i < max_color_number; i++) {
        f_head << "/*      attribute_" << (max_texture_number +i) << "	    */";
        if (color_box[i].vertex_number <= 0)
            f_head << "  0  0  0  1" << endl;
        else {
            f_head << "  " << (color_box[i].sum_x / color_box[i].vertex_number);
            f_head << "  " << (color_box[i].sum_y / color_box[i].vertex_number);
            f_head << "  " << (color_box[i].sum_z / color_box[i].vertex_number);
            f_head << "  " << (color_box[i].sum_w / color_box[i].vertex_number) << endl;
        }
    }

    f_head << endl;
    f_head << "/*  body_number              */	1" << endl;
    f_head << "    /*  body 0  name         */  assimp_body   /*  face_number */  1" << endl;
    f_head << "        /*  face 0  name     */  assimp_face" << endl << endl;
    f_head << "        /*  face type        */  unknown  /*  parameter number */  0  /*  parameter */" << endl;
    f_head << "        /*  total_face_primitive_number  */  "<< triangle_number << endl;
    f_head << "        /*  face_attribute_number        */  "<< (max_texture_number + max_color_number) << endl;
    f_head << "        /*  face_face_box                */";
    
    if(triangle_box.vertex_number<=0)
        f_head << "  nobox" << endl;
    else {
        f_head << "  " << triangle_box.min_x;
        f_head << "  " << triangle_box.min_y;
        f_head << "  " << triangle_box.min_z;
        f_head << "  " << triangle_box.max_x;
        f_head << "  " << triangle_box.max_y;
        f_head << "  " << triangle_box.max_z << endl;
    } 
    f_head << endl;

    if ((line_vertex_number+point_vertex_number) <= 0) {
        f_head << "        /*  face loop number        */  0" << endl << endl;
        return;
    }

    f_head << "        /*  face loop number             */  1" << endl;
    f_head << "          /*  Loop NO.0 edge number      */  2" << endl;

    f_head << "          /*  curve type                 */  segment" << endl;
    f_head << "          /*  parameter number           */  0  /*  parameter  */" << endl;
    f_head << "          start_not_effective            end_not_effective" << endl;
    f_head << "          /*  parameter_point_extra_data */  1" << endl;
    f_head << "          /*  parameter_point_material   */  0   0   0   0" << endl;
    f_head << "          /*  box definition             */";
    
    if (edge_box.vertex_number <= 0)
        f_head << "  nobox" << endl;
    else {
        f_head << "  " << edge_box.min_x;
        f_head << "  " << edge_box.min_y;
        f_head << "  " << edge_box.min_z;
        f_head << "  " << edge_box.max_x;
        f_head << "  " << edge_box.max_y;
        f_head << "  " << edge_box.max_z << endl;
    }
    f_head << "          /*  total_edge_primitive_number    */  " << line_vertex_number << endl;
    f_head << "          /*  total_point_primitive_number   */  0" << endl;
    f_head << endl;

    f_head << "          /*  curve type                 */  render_point_set" << endl;
    f_head << "          /*  parameter number           */  0  /*  parameter  */" << endl;
    f_head << "          start_not_effective            end_not_effective" << endl;
    f_head << "          /*  parameter_point_extra_data */  1" << endl;
    f_head << "          /*  parameter_point_material   */  0   0   0   0" << endl;
    f_head << "          /*  box definition             */";
   
    if (point_box.vertex_number <= 0)
        f_head << "  nobox" << endl;
    else {
        f_head << "  " << point_box.min_x;
        f_head << "  " << point_box.min_y;
        f_head << "  " << point_box.min_z;
        f_head << "  " << point_box.max_x;
        f_head << "  " << point_box.max_y;
        f_head << "  " << point_box.max_z << endl;
    }
    f_head << "          /*  total_edge_primitive_number    */  0"  << endl;
    f_head << "          /*  total_point_primitive_number   */  " << point_vertex_number << endl;
    f_head << endl << endl;
}