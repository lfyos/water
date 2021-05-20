using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using System.IO;

using SolidWorks.Interop.sldworks;
using SolidWorks.Interop.swconst;

namespace extract_solidworks_data
{
    class solidworks
    {
        
        private void do_extract(string source_file_name,string target_directory_name,string charset,int tessellation_quality,part_collector collector)
        {
            Console.WriteLine("Starting Solidworks......");

            string solidworks_name = "SldWorks.Application";

            ISldWorks sw = null;
            bool terminated_flag=false;
            try
            {
                sw = System.Runtime.InteropServices.Marshal.GetActiveObject(solidworks_name) as SolidWorks.Interop.sldworks.ISldWorks;
            }
            catch (Exception)
            {
                sw = null;
            }
            if(sw==null)
                try
                {
                    sw = System.Activator.CreateInstance(System.Type.GetTypeFromProgID(solidworks_name)) as SolidWorks.Interop.sldworks.ISldWorks;
                    terminated_flag =true;
                }
                catch (Exception)
                {
                    sw = null;
                }

            if (sw == null)
            {
                Console.WriteLine("Can NOT start or connect to Solidworks");
                return;
            }

            sw.Visible = true;
            sw.CommandInProgress = true;

            Console.WriteLine("Start loading Solidworks document: " + source_file_name);

            IModelDoc2 doc = null;
            int longstatus = 0, longwarnings = 0;

            if (!(System.IO.File.Exists(source_file_name)))
            {
                if((doc = (ModelDoc2)(sw.ActiveDoc)) == null)
                {
                    Console.WriteLine("NO active Solidworks document exists");
                    return;
                }
            }
            else
            {
                if ((doc = (IModelDoc2)(sw.OpenDoc6(source_file_name, (int)(swDocumentTypes_e.swDocASSEMBLY), 0, "", ref longstatus, ref longwarnings))) == null)
                    if ((doc = (IModelDoc2)(sw.OpenDoc6(source_file_name, (int)(swDocumentTypes_e.swDocPART), 0, "", ref longstatus, ref longwarnings))) == null)
                        if ((doc = (ModelDoc2)(sw.LoadFile4(source_file_name, "", null, ref longstatus))) == null)
                        {
                            Console.WriteLine("Can NOT open Solidworks document: " + source_file_name);
                            return;
                        }
            }
            if (doc.GetType() == (int)(swDocumentTypes_e.swDocASSEMBLY))
            {
                Console.WriteLine("Starting assemble ResolveAllLightweight: " + doc.GetPathName());
                ((IAssemblyDoc)doc).ResolveAllLightweight();
            }

            if(tessellation_quality>0)
                doc.SetTessellationQuality(tessellation_quality);

            Console.WriteLine("Starting ForceRebuild3: " + doc.GetPathName());
            doc.ForceRebuild3(false);
            doc.ViewZoomtofit2();
            doc.GraphicsRedraw2();
            Console.WriteLine("ForceRebuild3 finished : " + doc.GetPathName());

            FileStream assemble_stream = new FileStream(target_directory_name + "assemble.assemble", FileMode.Create, FileAccess.Write);
            StreamWriter assemble_writer = new StreamWriter(assemble_stream, Encoding.GetEncoding(charset));

            if (doc.GetType() == (int)(swDocumentTypes_e.swDocASSEMBLY))
                new assemble(doc, collector, target_directory_name).root_tree_node.write("", assemble_writer);
            else if (doc.GetType() == (int)(swDocumentTypes_e.swDocPART))
            {
                collector.register(doc, 1);
                assemble_writer.WriteLine("solidworks_root_component");
                assemble_writer.WriteLine("solidworks_part_0");
                assemble_writer.WriteLine("1.0	0.0	0.0	0.0	0.0	1.0	0.0	0.0	0.0	0.0	1.0	0.0	0.0	0.0	0.0	1.0");
                assemble_writer.WriteLine("0");
            }
            else
                Console.WriteLine("Can NOT get Solidworks document type: " + doc.GetPathName());

            assemble_writer.Close();
            assemble_stream.Close();

            sw.QuitDoc(doc.GetTitle());
            sw.CommandInProgress = false;

            if(terminated_flag)
                sw.ExitApp();
        }
        public solidworks(string source_file_name,string target_directory_name,string charset,
           int tessellation_quality, double ChordTolerance, double LengthTolerance,part_collector collector)
        {
            Console.WriteLine("Extract_solidworks_data: source_file_name:" + source_file_name);
            Console.WriteLine("Extract_solidworks_data: target_directory_name:" + target_directory_name);
            Console.WriteLine("Extract_solidworks_data: charset:" + charset);
            Console.WriteLine("Extract_solidworks_data: tessellation_quality:" + tessellation_quality);
            Console.WriteLine("Extract_solidworks_data: ChordTolerance:" + ChordTolerance);
            Console.WriteLine("Extract_solidworks_data: LengthTolerance:" + LengthTolerance);

            do_extract(source_file_name,target_directory_name,charset,tessellation_quality, collector);

            Console.WriteLine("End extract_solidworks_data: source_file_name:" + source_file_name);
        }
    }
}
