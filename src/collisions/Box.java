package collisions;

/**
 * Created by one on 8/20/16.
 */
public class Box {

    private EndPoint[] min = new EndPoint[3];
    private EndPoint[] max = new EndPoint[3];

    public Box(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        min[0] = new EndPoint(this, minX, true);
        min[1] = new EndPoint(this, minY, true);
        min[2] = new EndPoint(this, minY, true);
        max[0] = new EndPoint(this, maxX, false);
        max[1] = new EndPoint(this, maxY, false);
        max[2] = new EndPoint(this, maxZ, false);
        PairManager.boxes.add(this);
    }

    public EndPoint[] getMin() {
        return min;
    }

    public EndPoint[] getMax() {
        return max;
    }


}
