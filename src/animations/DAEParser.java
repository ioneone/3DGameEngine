package animations;

import loaders.OBJLoader;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import renderEngine.RawModel;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by one on 8/12/16.
 */
public class DAEParser {

    public static void loadDAE(String filePath) {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse("res"+filePath);
            doc.normalize();

            // get the root node <COLLADA>
            NodeList rootNodes = doc.getElementsByTagName("COLLADA");
            if (rootNodes.getLength() == 0) {
                System.out.println("Couldn't find tag <COLLADA>");
                System.exit(0);
            }
            if (rootNodes.getLength() > 1) {
                System.out.println("There are more than one tag <COLLADA>");
                System.exit(0);
            }
            Node rootNode = rootNodes.item(0);
            Element rootElement = (Element) rootNode;

            // **** get the library nodes ***** //
            // get the node <library_geometries>
            NodeList library_geometries_lists = rootElement.getElementsByTagName("library_geometries");
            if (library_geometries_lists.getLength() == 0) {
                System.out.println("Couldn't find tag <library_geometries>");
                System.exit(0);
            }
            Node library_geometries = library_geometries_lists.item(0);
            Element root_library_geometries = (Element) library_geometries;

            // get the node <geometry>
            NodeList geometry_lists = root_library_geometries.getElementsByTagName("geometry");
            if (geometry_lists.getLength() == 0) {
                System.out.println("Couldn't find tag <geometry>");
                System.exit(0);
            }
            Node geometry_list = geometry_lists.item(0);
            Element root_geometry = (Element) geometry_list;

            // get id and name attributes of <geometry>
            String geometry_id = root_geometry.getAttribute("id");
            String geometry_name = root_geometry.getAttribute("name");

            // get the node <mesh>
            NodeList mesh_lists = root_geometry.getElementsByTagName("mesh");
            if (mesh_lists.getLength() == 0) {
                System.out.println("Couldn't find tag <mesh>");
                System.exit(0);
            }
            Node mesh_list = mesh_lists.item(0);
            Element root_mesh = (Element) mesh_list;

            // ******** get the nodes <source> ******** //
            NodeList source_lists = root_mesh.getElementsByTagName("source");

            // **** get the node <source> (positions) **** //
            Node source_list_positions = source_lists.item(0);
            Element root_source_list_positions = (Element) source_list_positions;
            // get id attribute of <source> (positions)
            String source_id_position = root_source_list_positions.getAttribute("id");

            // get the info of the node <float_array>
            Node float_array = root_source_list_positions.getElementsByTagName("float_array").item(0);
            Element root_float_array = (Element) float_array;
            String float_array_id = root_float_array.getAttribute("id");
            String float_array_count = root_float_array.getAttribute("count");
            String[] float_array_tokens = root_float_array.getTextContent().split("\\s+");

            // get the node <technique_common>
            Node technique_common = root_source_list_positions.getElementsByTagName("technique_common").item(0);
            Element root_technique_common = (Element) technique_common;

            // get the info of the node <accessor>
            Node accessor = root_technique_common.getElementsByTagName("accessor").item(0);
            Element root_accessor = (Element) accessor;
            String accessor_source = root_accessor.getAttribute("source");
            String accessor_count = root_accessor.getAttribute("count");
            String accessor_stride = root_accessor.getAttribute("stride");

            // get the info of the nodes <param>
            NodeList params = root_technique_common.getElementsByTagName("param");
            Node xParam = params.item(0);
            Node yParam = params.item(1);
            Node zParam = params.item(2);
            Element root_xParam = (Element) xParam;
            Element root_yParam = (Element) yParam;
            Element root_zParam = (Element) zParam;
            String xParam_name = root_xParam.getAttribute("name");
            String xParam_type = root_xParam.getAttribute("type");
            String yParam_name = root_yParam.getAttribute("name");
            String yParam_type = root_yParam.getAttribute("type");
            String zParam_name = root_zParam.getAttribute("name");
            String zParam_type = root_zParam.getAttribute("type");

            // get the info of the node <vertices>
            Node vertices = root_mesh.getElementsByTagName("vertices").item(0);
            Element root_vertices = (Element) vertices;
            String vertices_id = root_vertices.getAttribute("id");
            Node vertices_input = root_vertices.getElementsByTagName("input").item(0);
            Element root_vertices_input = (Element) vertices_input;
            String vertices_input_semantic = root_vertices_input.getAttribute("semantic");
            String vertices_input_source = root_vertices_input.getAttribute("source");

            // get the node <polylist>
            Node polylist = root_mesh.getElementsByTagName("polylist").item(0);
            Element root_polylist = (Element) polylist;
            NodeList polylist_input_list = root_polylist.getElementsByTagName("input");
            Node polylist_input_vertex = polylist_input_list.item(0);
            Node polylist_input_normal = polylist_input_list.item(1);
            Node polylist_p = root_polylist.getElementsByTagName("p").item(0);
            Element root_polylist_input_vertex = (Element) polylist_input_vertex;
            Element root_polylist_input_normal = (Element) polylist_input_normal;
            Element root_polylist_p = (Element) polylist_p;
            String polylist_input_vertex_semantic = root_polylist_input_vertex.getAttribute("semantic");
            String polylist_input_vertex_source = root_polylist_input_vertex.getAttribute("source");
            String polylist_input_vertex_offset = root_polylist_input_vertex.getAttribute("offset");
            String polylist_input_normal_semantic = root_polylist_input_normal.getAttribute("semantic");
            String polylist_input_normal_source = root_polylist_input_normal.getAttribute("source");
            String polylist_input_normal_offset = root_polylist_input_normal.getAttribute("offset");
            String[] polylist_p_tokens = root_polylist_p.getTextContent().split("\\s+");
            for (int i = 0; i < polylist_p_tokens.length; i++) {
                System.out.println(polylist_p_tokens[i]);
            }















        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
