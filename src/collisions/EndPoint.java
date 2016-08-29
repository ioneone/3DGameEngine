package collisions;

/**
 * Created by one on 8/20/16.
 */
public class EndPoint {

    private Box owner;
    private float value;
    private boolean isMin;

    private EndPoint previous;
    private EndPoint next;

    public EndPoint(Box owner, float value, boolean isMin) {
        this.owner = owner;
        this.value = value;
        this.isMin = isMin;
    }

    public EndPoint getPrevious() {
        return previous;
    }

    public void setPrevious(EndPoint previous) {
        this.previous = previous;
    }

    public EndPoint getNext() {
        return next;
    }

    public void setNext(EndPoint next) {
        this.next = next;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public Box getOwner() {
        return owner;
    }

    public boolean isMin() {
        return isMin;
    }
}
