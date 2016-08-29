package animations;

/**
 * Created by one on 8/12/16.
 */
public class Source {

    private String ID;
    private Float_Array float_array;
    private Technique_Common technique_common;


    public Source(String ID, Float_Array float_array, Technique_Common technique_common) {
        this.ID = ID;
        this.float_array = float_array;
        this.technique_common = technique_common;
    }

    public String getID() {
        return ID;
    }

    public Float_Array getFloat_array() {
        return float_array;
    }

    public Technique_Common getTechnique_common() {
        return technique_common;
    }
}
