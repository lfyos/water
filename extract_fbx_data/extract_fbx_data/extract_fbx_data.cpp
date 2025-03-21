﻿// extract_fbx_data.cpp : 此文件包含 "main" 函数。程序执行将在此处开始并结束。

#include<iostream>
#include<fstream>
#include<string.h>
#include<direct.h>
#include<fbxsdk.h>

#include"assemble.h"

int main(int argc, char* argv[])
{
    if (argc < 2) {
        std::cout << "No file present!" << std::endl;
        return -1;
    };

    std::string directory_name = std::string(argv[1]) + ".lfy_3d\\";

    system(std::string("rmdir /S /Q " + directory_name).c_str());
    system(std::string("mkdir  " + directory_name).c_str());

    FbxManager* lSdkManager = FbxManager::Create();
    FbxIOSettings* ios = FbxIOSettings::Create(lSdkManager, IOSROOT);

    lSdkManager->SetIOSettings(ios);
    FbxImporter* lImporter = FbxImporter::Create(lSdkManager, "");

    if (lImporter->Initialize(argv[1], -1, lSdkManager->GetIOSettings()))
        std::cout << "Open " << argv[1] << " success" << std::endl;
    else {
        std::cout << "Open " << argv[1] << " fail" << std::endl;
        lImporter->Destroy();
        ios->Destroy();
        lSdkManager->Destroy();
        return -2;
    }

    FbxScene* lScene = FbxScene::Create(lSdkManager, "myScene");
    if (lImporter->Import(lScene))
        std::cout << "import from " << argv[1] << " success" << std::endl;
    else {
        std::cout << "import from " << argv[1] << " fail" << std::endl;
        lScene->Destroy();
        lImporter->Destroy();
        ios->Destroy();
        lSdkManager->Destroy();
        return -3;
    }

    part part_collector(directory_name);
    std::ofstream f(directory_name + "assemble.assemble");
    assemble assem(part_collector, f, lScene->GetRootNode(), 0,"");
    f.close();

    lScene->Destroy();
    lImporter->Destroy();
    ios->Destroy();
    lSdkManager->Destroy();

    std::cout << std::endl<< "Completed!" << std::endl;

    return 0;
}