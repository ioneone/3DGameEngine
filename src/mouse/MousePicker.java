package mouse;

import entities.Camera;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.CallbackI;
import renderEngine.Window;
import terrains.Terrain;
import toolBox.Maths;

/**
 * Created by one on 7/30/16.
 */
public class MousePicker {

    // **** properties for terrain picking **** //
    private static final int RECURSION_COUNT = 200;
    private static final float RAY_RANGE = 600;
    // **************************************** //

    private Vector3f currentRay = new Vector3f();

    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    private Camera camera;

    private Terrain[][] terrains;
    private Vector3f currentTerrainPoint;

    public MousePicker() {

    }

    public MousePicker(Camera camera, Matrix4f projectionMatrix, Terrain[][] terrain) {
        this.camera = camera;
        this.projectionMatrix = projectionMatrix;
        this.viewMatrix = Maths.createViewMatrix(camera);
        this.terrains = terrain;

    }

    public Vector3f getCurrentRay() {
        return currentRay;
    }

    private Vector3f calculateMouseRay(Window window, MouseInput mouseInput) {
        // Viewport space
        float mouseX = (float) mouseInput.getCurrentPosition().x;
        float mouseY = (float) mouseInput.getCurrentPosition().y;

        // OpenGL coords System
        Vector2f normalizedCoords = getNormalizedDeviceCoords(window, mouseX, mouseY);

        Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1, 1);

        Vector4f eyeCoords = toEyeCoords(clipCoords);

        Vector3f worldRay = toWorldCoords(eyeCoords);

        return worldRay;

    }

    private Vector3f toWorldCoords(Vector4f eyeCoords) {
        Matrix4f invertedViewMatrix = viewMatrix.invert();
        Vector4f rayWorld = invertedViewMatrix.transform(eyeCoords);
        Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
        mouseRay.normalize();
        return mouseRay;
    }

    private Vector4f toEyeCoords(Vector4f clipCoords) {
        Matrix4f invertedProjectionMatrix = projectionMatrix.invert();
        Vector4f eyeCoords = invertedProjectionMatrix.transform(clipCoords);
        return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
    }

    public Vector2f getNormalizedDeviceCoords(Window window, float mouseX, float mouseY) { // OpenGL coords System

        float x = 2f * (mouseX / window.getWidth()) - 1f;
        float y = 2f * (mouseY / window.getHeight()) - 1f;


        return new Vector2f(x, -y);
    }

    public static Vector2f getNormalizedDeviceCoords() {
        float mouseX = (float) MouseInput.getCurrentPosition().x;
        float mouseY = (float) MouseInput.getCurrentPosition().y;

        float x = 2f * (mouseX / Window.getWidth()) - 1f;
        float y = 2f * (mouseY / Window.getHeight()) - 1f;

        return new Vector2f(x, -y);
    }






    // **** pick the point on the terrain **** //
    private Vector3f getPointOnRay(Vector3f ray, float distance) {
        Vector3f camPos = camera.getPosition();
        Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
        Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
        return start.add(scaledRay);
    }

    private Vector3f binarySearch(int count, float start, float finish, Vector3f ray) {
        float half = start + ((finish - start) / 2f);
        if (count >= RECURSION_COUNT) {
            Vector3f endPoint = getPointOnRay(ray, half);
            Terrain terrain = getTerrain(endPoint.x, endPoint.z);
            if (terrain != null) {
                return endPoint;
            } else {
                return null;
            }
        }
        if (intersectionInRange(start, half, ray)) {
            return binarySearch(count + 1, start, half, ray);
        } else {
            return binarySearch(count + 1, half, finish, ray);
        }
    }

    private boolean intersectionInRange(float start, float finish, Vector3f ray) {
        Vector3f startPoint = getPointOnRay(ray, start);
        Vector3f endPoint = getPointOnRay(ray, finish);
        if (!isUnderGround(startPoint) && isUnderGround(endPoint)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isUnderGround(Vector3f testPoint) {
        Terrain terrain = getTerrain(testPoint.x, testPoint.z);
        float height = 0;
        if (terrain != null) {
            height = terrain.getHeightOfTerrain(testPoint.x, testPoint.z);
        }
        if (testPoint.y < height) {
            return true;
        } else {
            return false;
        }
    }

    private Terrain getTerrain(float worldX, float worldZ) {
        Terrain currentTerrain = terrains[(int)(worldX/Terrain.SIZE)][(int)(worldZ/Terrain.SIZE)];
        return currentTerrain;

    }

    public Vector3f getCurrentTerrainPoint() {
        return currentTerrainPoint;
    }


    public void update(Window window, MouseInput mouseInput) {
        viewMatrix = Maths.createViewMatrix(camera);
        currentRay = calculateMouseRay(window, mouseInput);
        if (intersectionInRange(0, RAY_RANGE, currentRay)) {
            currentTerrainPoint = binarySearch(0, 0, RAY_RANGE, currentRay);
        } else {
            currentTerrainPoint = null;
        }
    }

    // *************************************** //
}
