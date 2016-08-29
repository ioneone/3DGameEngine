package particles;


import entities.Camera;
import entities.Player;
import org.joml.Vector2f;
import org.joml.Vector3f;
import toolBox.Timer;

import java.sql.Time;

public class Particle {

    private Vector3f position;
    private Vector3f velocity;
    private float gravityEffect;
    private float lifeLength;
    private float rotation;
    private float scale;

    private float elapsedTime = 0;

    private float distance;


    private ParticleTexture texture;

    private Vector2f currentTexOffset = new Vector2f();
    private Vector2f nextTexOffset = new Vector2f();
    private float blend;

    public Particle(ParticleTexture texture, Vector3f position, Vector3f velocity, float gravityEffect, float lifeLength, float rotation, float scale) {
        this.texture = texture;
        this.position = position;
        this.velocity = velocity;
        this.gravityEffect = gravityEffect;
        this.lifeLength = lifeLength;
        this.rotation = rotation;
        this.scale = scale;
        ParticleMaster.addParticle(this);
    }

    protected boolean update(Camera camera) {
        //System.out.println(elapsedTime);
        float delta = Timer.getDelta() * 10;
        velocity.y += Player.GRAVITY * gravityEffect * delta;
        Vector3f change = new Vector3f(velocity);
        change.mul(delta);
        position.add(change);
        distance = new Vector3f(camera.getPosition()).sub(position).lengthSquared();
        addTextureCoordInfo();
        elapsedTime += delta;
        //System.out.println(elapsedTime);


        return elapsedTime < lifeLength;


    }

    protected Vector3f getPosition() {
        return position;
    }

    protected float getRotation() {
        return rotation;
    }

    protected float getScale() {
        return scale;
    }

    public ParticleTexture getTexture() {
        return texture;
    }

    public Vector2f getCurrentTexOffset() {
        return currentTexOffset;
    }

    public Vector2f getNextTexOffset() {
        return nextTexOffset;
    }

    public float getBlend() {
        return blend;
    }

    public float getDistance() {
        return distance;
    }

    private void addTextureCoordInfo() {
        float lifeFactor = elapsedTime / lifeLength;
        int stageCount = texture.getNumberOfRows() * texture.getNumberOfRows();
        float atlasProgression = lifeFactor * stageCount;
        int currentIndex = (int) Math.floor(atlasProgression);
        int nextIndex = currentIndex < stageCount - 1 ? currentIndex + 1 : currentIndex;
        this.blend = atlasProgression % 1;
        setTextureOffset(currentTexOffset, currentIndex);
        setTextureOffset(nextTexOffset, nextIndex);
    }

    private void setTextureOffset(Vector2f offset, int index) {
        int column = index % texture.getNumberOfRows();
        int row = index / texture.getNumberOfRows();
        offset.x = (float) column / texture.getNumberOfRows();
        offset.y = (float) row / texture.getNumberOfRows();

    }

}
