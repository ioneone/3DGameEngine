package entities;

import org.joml.Vector3f;
import mouse.MouseInput;

/**
 * Created by one on 7/24/16.
 */
public class Camera {

    private Vector3f position = new Vector3f(0, 0, 0);

    private float distanceFromPlayer = 150;
    private float angleAroundPlayer = 0;

    private float roll = 0;
    private float pitch = 70;
    private float yaw = 0;

    private Player player;


    public Camera(Player player) {
        this.player = player;
    }


    public void increasePosition(float dx, float dy, float dz) {
        position.x += dx;
        position.y += dy;
        position.z += dz;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void move(MouseInput mouseInput) {
        calculateZoom(mouseInput);
        calculatePitch(mouseInput);
        calculateAngleAroundPlayer(mouseInput);
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
        //this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
        this.yaw = 180 - angleAroundPlayer;

    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    public Vector3f getRotation() {
        return new Vector3f(roll, pitch, yaw);
    }

    private void calculateZoom(MouseInput mouseInput) {
        float zoomLevel = (float) mouseInput.getScrollValue();
        mouseInput.setScrollValue(0);
        distanceFromPlayer -= zoomLevel;
    }

    private void calculatePitch(MouseInput mouseInput) {
        if (mouseInput.isRightButtonPressed()) {
            float pitchChange = mouseInput.getDisplVec().x;
            pitch -= pitchChange;
        }
    }

    private void calculateAngleAroundPlayer(MouseInput mouseInput) {
        if (mouseInput.isLeftButtonPressed()) {
            float angleChange = mouseInput.getDisplVec().y;
            angleAroundPlayer -= angleChange;
        }
    }

    private float calculateHorizontalDistance() {
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
    }

    private float calculateVerticalDistance() {
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
    }

    private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
        //float theta = player.getRotY() + angleAroundPlayer;
        float theta = angleAroundPlayer;
        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
        position.x = player.getPosition().x - offsetX;
        position.z = player.getPosition().z - offsetZ;
        position.y = player.getPosition().y + verticalDistance;

    }

    public void invertPitch() {
        this.pitch = -pitch;
    }
}
