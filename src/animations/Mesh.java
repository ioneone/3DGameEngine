package animations;

import java.util.List;

/**
 * Created by one on 8/12/16.
 */
public class Mesh {

    private List<Source> sources;
    private Vertices vertices;
    private List<PolyList> polyLists;

    public Mesh(List<Source> sources, Vertices vertices, List<PolyList> polyLists) {
        this.sources = sources;
        this.vertices = vertices;
        this.polyLists = polyLists;
    }

    public List<Source> getSources() {
        return sources;
    }

    public Vertices getVertices() {
        return vertices;
    }

    public List<PolyList> getPolyLists() {
        return polyLists;
    }
}
