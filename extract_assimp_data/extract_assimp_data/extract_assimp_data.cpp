// extract_assimp_data.cpp : 此文件包含 "main" 函数。程序执行将在此处开始并结束。
//

#include <string>
#include <iostream>
#include <filesystem>
#include <assimp/Importer.hpp>      // C++ importer interface
#include <assimp/scene.h>           // Output data structure
#include <assimp/postprocess.h>     // Post processing flags
#include "extract_mesh_data.h"
#include "extract_material_data.h"
#include "extract_assemble_data.h"
#include "default_parameter.h"
#include "mesh_set.h"

using namespace std;
using namespace std::filesystem;

int main(int argc,char *argv[])
{
    if (argc < 1) {
        cout << "No extract source file" << endl;
        return 1;
    }

    string parameter_file_name;
    if (argc > 2)
        parameter_file_name= argv[2];
    else{
        parameter_file_name = argv[0];
        for (int i = (int)(parameter_file_name.size()) - 1; i >= 0; i--)
            switch (parameter_file_name.at(i)) {
            case '/':
            case '\\':
                parameter_file_name = parameter_file_name.substr(0, i + 1);
                i = 0;
                break;
            }
        parameter_file_name += "parameter.txt";
    }
    default_parameter default_par(parameter_file_name);
   
    string source_file_name = argv[1];
    string target_directory_name = source_file_name+".lfy_3d\\";

    remove_all(target_directory_name);
    create_directory(target_directory_name);

    cout << "extract source file:  "<< source_file_name << endl;

    Assimp::Importer importer;

    // And have it read the given file with some example postprocessing
    // Usually - if speed is not the most important aspect for you - you'll
    // probably to request more postprocessing than we do in this example.
    const aiScene* scene = importer.ReadFile(
        source_file_name,
        aiProcess_CalcTangentSpace |
        aiProcess_Triangulate |
        aiProcess_JoinIdenticalVertices |
        aiProcess_SortByPType);

    if (scene == NULL) {
        cout << "read source file fail:  " << source_file_name << endl;
        return 2;
    }
    mesh_set_collector msc;
    ofstream fw(target_directory_name + "assemble.assemble");
    extract_assemble_data assemble(scene->mRootNode, fw, "",msc, scene->mNumMeshes);
    fw.close();

    extract_mesh_data mesh(scene->mMeshes, scene->mNumMeshes, target_directory_name,msc);
    extract_material_data material(scene->mMeshes, scene->mNumMeshes,
        scene->mMaterials, scene->mNumMaterials, 
        target_directory_name, default_par,msc);

    cout << "Finish extracting from   " << source_file_name <<  endl;

    return 0;
}
