package animations;


import java.util.List;

/**
 * Created by one on 8/12/16.
 */
public class GeometryLibrary {

    private List<Geometry> geometries;

    public GeometryLibrary(List<Geometry> geometries) {
        this.geometries = geometries;
    }

    public List<Geometry> getGeometries() {
        return geometries;
    }
}
