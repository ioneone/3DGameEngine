package animations;

/**
 * Created by one on 8/12/16.
 */
public class Float_Array {

    private String ID;
    private int count;
    private float[] floats;

    public Float_Array(String ID, int count, float[] floats) {
        this.ID = ID;
        this.count = count;
        this.floats = floats;
    }

    public String getID() {
        return ID;
    }

    public int getCount() {
        return count;
    }

    public float[] getFloats() {
        return floats;
    }
}
