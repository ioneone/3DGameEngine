package animations;

import textures.TexturedModel;

/**
 * Created by one on 8/12/16.
 */
public class Geometry {

    private Mesh mesh;

    public Geometry(Mesh mesh) {
        this.mesh = mesh;
    }

    public Mesh getMesh() {
        return mesh;
    }
}
