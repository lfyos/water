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
        

        public void save(StreamWriter material_writer)
        {
            string texture_file_name;

            if (string.Compare(texture_file, "") == 0)
            {
                material_writer.WriteLine("     /*	    caculate_color_method                                       */      0");
                texture_file_name = "notexture";
            }
            else
            {
                material_writer.WriteLine("     /*	    caculate_color_method                                       */      2");
                int index_id;
                if ((index_id = texture_file.IndexOf('\\')) >= 0)
                    texture_file_name = texture_file.Substring(index_id + 1);
                else if ((index_id = texture_file.IndexOf('/')) >= 0)
                    texture_file_name = texture_file.Substring(index_id + 1);
                else
                    texture_file_name = texture_file;
            }
            material_writer.WriteLine("     /*	    caculate_color_parameter                                    */      "+ red+"    "+green+"   "+blue+"    1");
            material_writer.WriteLine("     /*	    ambient,diffuse,specular,shininess,transparency,emission    */      "
                + ambient + "   " + diffuse + " " + specular + "    " + shininess + "   " + transparency + "    " + emission);
            material_writer.WriteLine("     /*	    texture file name                                           */      " + texture_file_name);
            material_writer.WriteLine("     /*	    texture_move_x,texture_move_y,texture_alf,texture_scale     */      "
                + trans_x + "   " + trans_y + "  " + rotate_angle + "    " + scale_factor);
            material_writer.WriteLine();
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
                    if(string.Compare(texture_file.Trim(),"")!=0){
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
            if (p.red != red)
                return false;
            if (p.green != green)
                return false;
            if (p.blue != blue)
                return false;
            if (p.ambient != ambient)
                return false;
            if (p.diffuse != diffuse)
                return false;
            if (p.specular != specular)
                return false;
            if (p.shininess != shininess)
                return false;
            if (p.transparency != transparency)
                return false;
            if (p.emission != emission)
                return false;

            if (p.blend_flag ^ blend_flag)
                return false;

            if (string.Compare(p.texture_file.Trim(),texture_file.Trim())==0)
                return false;

            if (p.scale_factor != scale_factor)
                return false;
            if (p.rotate_angle != rotate_angle)
                return false;
            if (p.trans_x!= trans_x)
                return false;
            if (p.trans_y != trans_y)
                return false;
           
            return true;
        }
    }
}
