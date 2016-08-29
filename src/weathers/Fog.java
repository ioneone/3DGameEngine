package weathers;

/**
 * Created by one on 8/18/16.
 */
public class Fog {

    private float density; // thickness of the fog, increase this will decrease the general visibility of the scene
    private float gradient; // determine how quickly the visibility changes with distance

    public Fog(float density, float gradient) {
        this.density = density;
        this.gradient = gradient;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public float getGradient() {
        return gradient;
    }

    public void setGradient(float gradient) {
        this.gradient = gradient;
    }
}
