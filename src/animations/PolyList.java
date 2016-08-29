package animations;

import java.util.List;

/**
 * Created by one on 8/12/16.
 */
public class PolyList {

    private int count;
    private String material;
    private List<Input> inputs;
    private P p;

    public PolyList(int count, String material, List<Input> inputs, P p) {
        this.count = count;
        this.material = material;
        this.inputs = inputs;
        this.p = p;
    }

    public int getCount() {
        return count;
    }

    public String getMaterial() {
        return material;
    }

    public List<Input> getInputs() {
        return inputs;
    }

    public P getP() {
        return p;
    }
}
