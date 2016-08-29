package toolBox;

import entities.Camera;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Created by one on 7/24/16.
 *
 *
 * A class for conversion of transformation to 4 * 4 Matrix
 *
 */
public class Maths {

    public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(new Vector3f(translation, 0.0f));
        matrix.scale(new Vector3f(scale.x, scale.y, 1f));
        return matrix;
    }

    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
        return new Matrix4f()
                .identity()
                .translate(translation)
                .rotate((float)Math.toRadians(rx), new Vector3f(1,0,0))
                .rotate((float)Math.toRadians(ry), new Vector3f(0,1,0))
                .rotate((float)Math.toRadians(rz), new Vector3f(0,0,1))
                .scale(scale);

    }

    public static Matrix4f createViewMatrix(Camera camera) {
        Vector3f cameraPosition = camera.getPosition();
        Vector3f negativeCameraPosition = new Vector3f(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);
        return new Matrix4f()
                .identity()
                .rotate((float)Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0))
                .rotate((float)Math.toRadians(camera.getRoll()), new Vector3f(0, 0, 1))
                .translate(negativeCameraPosition);

    }

    // A function to calculate the height of terrain given the triangle the player is standing on
    public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }



}
