package animations;

import java.util.List;

/**
 * Created by one on 8/12/16.
 */
public class Vertices {

    private String ID;
    private List<Input> inputs;

    public Vertices(String ID, List<Input> inputs) {
        this.ID = ID;
        this.inputs = inputs;
    }

    public String getID() {
        return ID;
    }

    public List<Input> getInputs() {
        return inputs;
    }
}
