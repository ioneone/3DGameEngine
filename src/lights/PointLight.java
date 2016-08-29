package lights;

import org.joml.Vector3f;

/**
 * Created by one on 7/25/16.
 */

/************************
 * The actual calculation for the lighting will be
 * taken care of by shaders
 * This class contains information of the light,
 * which will be passed in as a parameter for lighting calculation
 * in vertexShader.vs as an uniform variable "lightPosition"
 * and "lightColour" in fragmentShader.fs
 *************************/

public class PointLight { // PointLight

    private Vector3f position;
    private Vector3f colour;
    private Vector3f attenuation = new Vector3f(1, 0, 0); // No attenuation by Default


    public PointLight(Vector3f position, Vector3f colour) {
        this.position = position;
        this.colour = colour;
    }

    public PointLight(Vector3f position, Vector3f colour, Vector3f attenuation) {
        this.position = position;
        this.colour = colour;
        this.attenuation = attenuation;
    }

    public Vector3f getAttenuation() {
        return attenuation;
    }



    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }

}
