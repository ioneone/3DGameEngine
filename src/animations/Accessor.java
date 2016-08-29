package animations;

import java.util.List;

/**
 * Created by one on 8/12/16.
 */
public class Accessor {

    private String source;
    private int count;
    private int stride;
    private List<Param> params;

    public Accessor(String source, int count, int stride, List<Param> params) {
        this.source = source;
        this.count = count;
        this.stride = stride;
        this.params = params;
    }

    public String getSource() {
        return source;
    }

    public int getCount() {
        return count;
    }

    public int getStride() {
        return stride;
    }

    public List<Param> getParams() {
        return params;
    }
}
