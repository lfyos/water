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
    class assemble_tree_node
    {
        public string component_name, part_name;
        public double [] loca_data;
        public assemble_tree_node[] children;

        private  string cut_space(string str)
        {
            str = str.Replace(" ", "");
            str = str.Replace("\t", "");
            str = str.Replace(" ", "");
            str = str.Replace("\r", "");
            str = str.Replace("\n", "");
            return str;
        }
        public void write(string pre_str, StreamWriter assemble_writer)
        {
            assemble_writer.WriteLine(pre_str + "/*   component name    */   " + cut_space(component_name));       
            assemble_writer.WriteLine(pre_str + "/*   part name         */   " + cut_space(part_name));

            assemble_writer.Write(pre_str + "/*   location          */  ");
            for (int i = 0, ni = loca_data.Length; i < ni; i++)
                assemble_writer.Write(" " + loca_data[i]);
            assemble_writer.WriteLine("");

            assemble_writer.WriteLine(pre_str + "/*   children number   */   " + children.Length);
            assemble_writer.WriteLine("");

            pre_str += "    ";
            for (int i = 0, ni = children.Length; i < ni; i++)
                children[i].write(pre_str, assemble_writer);
        }
        public assemble_tree_node(string my_component_name,string my_part_name, double[] my_loca_data)
        {
            component_name = my_component_name;
            part_name = my_part_name;
            loca_data = my_loca_data;

            children = new assemble_tree_node[0];
        }
    }
    class assemble
    {
        private assemble_tree_node traverse_component_tree(Component2 comp,
            location negative_parent_absulate_loca, part_collector collector)
        {
            String component_name = comp.Name2;
            string part_name=collector.register((IModelDoc2)(comp.GetModelDoc2()),++component_number);
            double[] loca_data;
            do
            {
                try
                {
                    if (comp.Transform2 != null)
                        if (comp.Transform2.ArrayData != null)
                        {
                            loca_data = (double[])(comp.Transform2.ArrayData);
                            loca_data = new double[]
                            {
                                loca_data[0], loca_data[1], loca_data[2], loca_data[13] ,
                                loca_data[3], loca_data[4], loca_data[5], loca_data[14] ,
                                loca_data[6], loca_data[7], loca_data[8], loca_data[15] ,
                                loca_data[9], loca_data[10],loca_data[11],loca_data[12]
                            };
                            break;
                        }
                }catch(Exception )
                {
                    ;
                }
                loca_data = new double[] { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };
            } while (false);
            
            location absulate_loca = new location(loca_data);
            if(negative_parent_absulate_loca==null)
                loca_data =absulate_loca.get_data();
            else
                loca_data = negative_parent_absulate_loca.multiply(absulate_loca).get_data();

            assemble_tree_node ret_val = new assemble_tree_node(component_name, part_name, loca_data);

            System.Object[] children;

            try
            {
                children = (System.Object[])(comp.GetChildren());
                if (children.Length <= 0)
                {
                    part_component_number++;
                    return ret_val;
                }
            }
            catch(Exception )
            {
                return ret_val;
            }
            ret_val.children = new assemble_tree_node[children.Length];
            negative_parent_absulate_loca = absulate_loca.negative();
            for (int i = 0, ni = children.Length; i < ni; i++)
                ret_val.children[i]=traverse_component_tree(
                    (Component2)(children[i]),negative_parent_absulate_loca, collector);

            return ret_val;
        }

        public assemble_tree_node root_tree_node;
        public int part_component_number, component_number;
        public assemble(IModelDoc2 doc,part_collector collector,String target_directory_name)
        {
            part_component_number = 0;
            component_number = 0;

            ConfigurationManager config_manager = (ConfigurationManager)(doc.ConfigurationManager);
            Component2 root_component = (Component2)(((Configuration)(config_manager.ActiveConfiguration)).GetRootComponent());

            Console.WriteLine("Start travel assemble tree: " + doc.GetPathName());

            root_tree_node = traverse_component_tree(root_component, null, collector);

            Console.WriteLine("Total component_number: " + component_number + ",Total part_component_number: " + part_component_number);
        }
    }
}
