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
    class part_collector
    {
        private string directory_name,charset;
        private double ChordTolerance, LengthTolerance,color_scale;

        private string []path_name;
        private string[] part_name;

        private FileStream part_list_stream;
        private StreamWriter part_list_writer;

        public part_collector(string my_directory_name, string my_charset,
            double my_ChordTolerance, double my_LengthTolerance, double my_color_scale)
        {
            directory_name = my_directory_name;
            charset = my_charset;
            ChordTolerance = my_ChordTolerance;
            LengthTolerance = my_LengthTolerance;
            color_scale = my_color_scale;
            path_name = new string[0];
            part_name = new string[0];

            part_list_stream = new FileStream(directory_name + "part.list", FileMode.Create, FileAccess.Write);
            part_list_writer = new StreamWriter(part_list_stream, Encoding.GetEncoding(charset));
        }

        public void close()
        {
            part_list_writer.Close();
            part_list_stream.Close();
        }
        public string register(IModelDoc2 my_doc,int Component_number)
        {
            String unknown_part_name = "solidworks_unknown_part_name";

            if (my_doc == null)
                return unknown_part_name;
            if (my_doc.GetType() != (int)(swDocumentTypes_e.swDocPART))
                return unknown_part_name;
            String my_path_name = my_doc.GetPathName();
            if (my_path_name == null)
                return unknown_part_name;
            for (int i = 0, ni = path_name.Length; i < ni; i++)
                if(string.Compare(path_name[i], my_path_name) ==0)
                    return part_name[i];

            string my_part_name = my_doc.GetTitle().Replace(" ", "").Replace("\t", "").Replace("\r", "").Replace("\n", "");
            for (int i = 0, ni = part_name.Length; i < ni; i++)
                if (string.Compare(part_name[i],my_part_name) == 0)
                {
                    Console.WriteLine("Find same title part,title:" + my_part_name + ",file 1:" + my_path_name + ",file 2:" + path_name[i]);

                    return part_name[i];
                }

            string[] bak=path_name;
            path_name = new string[bak.Length + 1];
            for (int i = 0, ni = bak.Length; i < ni; i++)
                path_name[i] = bak[i];
            path_name[path_name.Length - 1] = my_path_name;

            bak = part_name;
            part_name = new string[bak.Length + 1];
            for (int i = 0, ni = bak.Length; i < ni; i++)
                part_name[i] = bak[i];
            part_name[part_name.Length - 1] = my_part_name;

            string my_last_str = ".sldprt", my_part_user_name= my_part_name;
            int my_last_str_length= my_last_str.Length, my_part_name_length= my_part_name.Length;
            if (my_last_str_length < my_part_name_length)
                if (my_part_name.Substring(my_part_name_length - my_last_str_length).ToLower().CompareTo(my_last_str) == 0)
                    my_part_user_name = my_part_name.Substring(0, my_part_name_length - my_last_str_length);

            part_list_writer.WriteLine(my_part_user_name);
            part_list_writer.WriteLine("    " + my_part_name);
            part_list_writer.WriteLine("    " + my_part_name + ".mesh");
            part_list_writer.WriteLine("    " + my_part_name + ".material");
            part_list_writer.WriteLine("    " + my_part_name + ".description");
            part_list_writer.WriteLine("    " + my_part_name + ".mp3");
            part_list_writer.WriteLine();

            Console.WriteLine("Component_number:" + Component_number
                            + ",part number:" +(part_name.Length)
                            + ",title:"+ my_part_name
                            + ",file:"+ my_path_name);

            new part(my_doc, charset,
                directory_name + my_part_name + ".mesh",
                directory_name + my_part_name + ".material",
                ChordTolerance, LengthTolerance,color_scale);

            return my_part_name;
        }
    }
}
