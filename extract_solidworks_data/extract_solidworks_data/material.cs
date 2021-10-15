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
    class material
    {
        public int meterial_id;

        public double red, green, blue, ambient, diffuse, specular, shininess, transparency, emission;

        public bool blend_flag;

        public string texture_file;
        public double scale_factor,rotate_angle,trans_x, trans_y;

        public void save(StreamWriter material_writer,Boolean last_flag)
        {
            string pre_str = "                  ";
            Boolean texture_flag = (string.Compare(texture_file, "") == 0);

            material_writer.WriteLine("               {");

           material_writer.WriteLine(pre_str+"\"vertex_color_type\"    :	1,");
            material_writer.WriteLine(pre_str + "\"fragment_color_type\"  :	" + (texture_flag ? 0 : 1) + ",");

            material_writer.WriteLine(pre_str + "\"color_parameter\"      :	[0,0,0,0],");

            material_writer.WriteLine(pre_str + "\"texture_parameter\"    :	[" + trans_x + "," + trans_y + ","
                 + scale_factor * Math.Cos(rotate_angle * Math.PI /180.0) + ","
                 + scale_factor * Math.Sin(rotate_angle * Math.PI /180.0) + "],");

            material_writer.WriteLine(pre_str + "\"texture_file\"         :	" + (texture_flag ? "null" : ("\"" + 
                texture_file.Replace("\\", "/").Replace("\n", "").Replace("\"", "").Replace("\r", "") + "\"")) + ",");

            material_writer.WriteLine(pre_str + "\"color\"                :	["
                + red + "," + green + "," + blue + ",1]," );
            material_writer.WriteLine(pre_str + "\"ambient\"              :	[" 
                + ambient + "," + ambient + "," + ambient + ",1],");
            material_writer.WriteLine(pre_str + "\"diffuse\"              :	[" 
                + diffuse + "," + diffuse + "," + diffuse + ",1],");
            material_writer.WriteLine(pre_str + "\"specular\"             :	[" 
                + specular + "," + specular + "," + specular + ",1],");
            material_writer.WriteLine(pre_str + "\"emission\"             :	[" 
                + emission + "," + emission + "," + emission + ",1],");
            material_writer.WriteLine(pre_str + "\"shininess\"            :	" 
                + shininess);

            material_writer.WriteLine("               }"+(last_flag?"":","));
        }
        private void init()
        {
            meterial_id = 0;

            red = 0.5;
            green = 0.5;
            blue = 0.5;
            ambient = 0.1;
            diffuse = 0.25;
            specular = 0.5;
            shininess = 4;
            transparency = 0;
            emission = 0.1;

            blend_flag = false;
            texture_file = "";
            blend_flag = false;
            scale_factor = 1.0;
            rotate_angle = 0;
            trans_x = 0;
            trans_y = 0;
        }
        
        public material()
        {
            init();
        }
        public material(double[] color_parameter, Texture my_texture)
        {
            init();

            red         = color_parameter[0];
            green       = color_parameter[1];
            blue        = color_parameter[2];
            ambient     = color_parameter[3];
            diffuse     = color_parameter[4];
            specular    = color_parameter[5];
            shininess   = color_parameter[6];
            transparency= color_parameter[7];
            emission    = color_parameter[8];

            if (my_texture != null)
                if ((texture_file = my_texture.MaterialName.Trim()) != null)
                    if(string.Compare(texture_file,"")!=0){
                        string pre_str;
                        if (texture_file.IndexOf(pre_str = "<SystemTexture>/") == 0)
                            texture_file = texture_file.Substring(pre_str.Length);
                        if (texture_file.IndexOf(pre_str = "<SystemTexture>\\") == 0)
                            texture_file = texture_file.Substring(pre_str.Length);

                        blend_flag = my_texture.BlendColor;
                        scale_factor = my_texture.ScaleFactor;
                        rotate_angle = my_texture.Angle;
                        trans_x = my_texture.TransX;
                        trans_y = my_texture.TransY;

                        return;
                    };
            texture_file = "";
        }

        public bool compare(material p)
        {
            double min_value = 0.0001;
            if (Math.Abs(p.ambient - ambient) > min_value)
                return false;
            if (Math.Abs(p.diffuse - diffuse) > min_value)
                return false;
            if (Math.Abs(p.specular - specular) > min_value)
                return false;
            if (Math.Abs(p.shininess - shininess) > min_value)
                return false;
            if (Math.Abs(p.transparency - transparency) > min_value)
                return false;
            if (Math.Abs(p.emission - emission) > min_value)
                return false;

            if (p.blend_flag ^ blend_flag)
                return false;

            if (string.Compare(p.texture_file, texture_file) != 0)
                return false;

            if (Math.Abs(p.scale_factor - scale_factor) > min_value)
                return false;
            if (Math.Abs(p.rotate_angle - rotate_angle) > min_value)
                return false;
            if (Math.Abs(p.trans_x - trans_x) > min_value)
                return false;
            if (Math.Abs(p.trans_y - trans_y) > min_value)
                return false;

            return true;
        }
    }
}
