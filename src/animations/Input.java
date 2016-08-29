package animations;

/**
 * Created by one on 8/12/16.
 */
public class Input {

    private String semantic;
    private String source;
    private String offset;

    public Input(String semantic, String source, String offset) {
        this.semantic = semantic;
        this.source = source;
        this.offset = offset;
    }

    public String getSemantic() {
        return semantic;
    }

    public String getSource() {
        return source;
    }

    public String getOffset() {
        return offset;
    }
}
