package collisions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by one on 8/20/16.
 */
public class PairManager {

    /* use quickSort to sort the lists at the first frame
     * then use the fact that an object doesn't change its position dramatically,
     * use insertionSort to sort the EndPoint lists every frame after that
     */

    public static List<Box> boxes = new ArrayList<Box>();
    public static List<EndPoint> endPointsAlongX = new ArrayList<EndPoint>();
    public static List<EndPoint> endPointsAlongY = new ArrayList<EndPoint>();
    public static List<EndPoint> endPointsAlongZ = new ArrayList<EndPoint>();

    //public static Map<Box, List<Box>> overlappingPairs = new HashMap<Box, List<Box>>(); // maybe unnecessary?
    public static List<Box> collidingBoxes = new ArrayList<Box>();

    public static void initEndPoints() {
        for (Box box:boxes) {
            endPointsAlongX.add(box.getMin()[0]);
            endPointsAlongX.add(box.getMax()[0]);
            endPointsAlongY.add(box.getMin()[1]);
            endPointsAlongY.add(box.getMax()[1]);
            endPointsAlongZ.add(box.getMin()[2]);
            endPointsAlongZ.add(box.getMax()[2]);
        }
        QuickSort.sort(endPointsAlongX);
        QuickSort.sort(endPointsAlongY);
        QuickSort.sort(endPointsAlongZ);

    }

    public static void updateEndPoints() {

        InsertionSort.sort(endPointsAlongX);
        InsertionSort.sort(endPointsAlongY);
        InsertionSort.sort(endPointsAlongZ);

    }

    public static void updateOverlappingPairs() {
        for (int i = 0; i < endPointsAlongX.size(); i++) {
            EndPoint currentEndPoint = endPointsAlongX.get(i);
            if (currentEndPoint.isMin()) {
                if(currentEndPoint.getOwner() != currentEndPoint.getNext().getOwner()) {
                    // overlapping along x-axis is detected
                    // now check if they overlap along z-axis
                    if (currentEndPoint.getOwner().getMin()[2].getNext().getOwner() == currentEndPoint.getNext().getOwner()) {
                        // overlapping along z-axis is detected
                        // now check if they overlap along y-axis
                        if (currentEndPoint.getOwner().getMin()[1].getNext().getOwner() == currentEndPoint.getNext().getOwner()) {
                            // overlapping along y-axis is detected
                            // register the pair

                            /*
                            List<Box> batch = overlappingPairs.get(currentEndPoint.getOwner());
                            if (batch != null) {
                                batch.add(currentEndPoint.getNext().getOwner());
                            } else {
                                List<Box> newBatch = new ArrayList<Box>();
                                newBatch.add(currentEndPoint.getNext().getOwner());
                                overlappingPairs.put(currentEndPoint.getOwner(), newBatch);
                            }
                            */

                            collidingBoxes.add(currentEndPoint.getOwner());
                        }
                    }

                }
            } else {
                if (currentEndPoint.getOwner() == currentEndPoint.getPrevious().getOwner()) {
                    // un-overlapping along x-axis is detected
                    // remove the pair including this box if exists
                    collidingBoxes.remove(currentEndPoint.getOwner());
                } else if (currentEndPoint.getOwner().getMax()[2].getPrevious().getOwner() == currentEndPoint.getOwner()) {
                    // un-overlapping along y-axis is detected
                    // remove the pair including this box if exists
                    collidingBoxes.remove(currentEndPoint.getOwner());
                } else if (currentEndPoint.getOwner().getMax()[1].getPrevious().getOwner() == currentEndPoint.getOwner()) {
                    // un-overlapping along z-axis is detected
                    // remove the pair including this box if exists
                    collidingBoxes.remove(currentEndPoint.getOwner());
                }


            }
        }
    }







}
