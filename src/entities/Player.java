package entities;

import audio.AudioMaster;
import audio.Source;
import mouse.MousePicker;
import org.joml.Vector2f;
import org.joml.Vector3f;
import particles.Particle;
import particles.ParticleTexture;
import renderEngine.Window;
import terrains.Terrain;
import textures.TexturedModel;
import toolBox.Timer;

import static org.lwjgl.glfw.GLFW.*;
/**
 * Created by one on 7/28/16.
 */
public class Player extends Entity {

    private static final float RUN_SPEED = 30;
    private static final float TURN_SPEED = 20;
    public static final float GRAVITY = -500;
    private static final float JUMP_POWER = 900;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;


    private float upwardsSpeed = 0;

    private boolean isInAir = false;
    private boolean isShooting = false;
    private boolean isUnderAttack = false;

    //private static final int JUMP_BUFFER = AudioMaster.loadSound("/audios/jump.wav");
    //private static final int SHOT_BUFFER = AudioMaster.loadSound("/audios/gunshot.wav");
    //private static final int BACKGROUND_BUFFER = AudioMaster.loadSound("/audios/Game-Menu.wav");

    //private Source[] sources;
    //private Source ear;
    //private static final int MAX_SOURCES = 10;

    private int shootIntervalCount = 0;
    private static final int SHOOT_INTERVAL = 15;

    private final float ATTACK_RANGE = 300;

    private int nextSource = 0;

    public Player(TexturedModel texturedModel, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(texturedModel, position, rotX, rotY, rotZ, scale);

        //sources = new Source[MAX_SOURCES];
        //for (int i = 0; i < MAX_SOURCES; i++) {
        //    sources[i] = new Source();
        //    sources[i].setPosition(position.x, position.y, position.z);
        //}
        //ear = new Source();
        //ear.setPosition(position.x, position.y, position.z);
        //ear.setLooping(true);
        //ear.play(BACKGROUND_BUFFER);

    }

    public void move(Terrain terrain) {

        float delta = Timer.getDelta();
        super.increaseRotation(0, currentTurnSpeed * delta, 0);
        //float distance2 = currentTurnSpeed * delta;
        float distance = currentSpeed * delta;
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
        //float dx = distance2;
        //float dz = distance;
        super.increasePostion(dx, 0, dz);
        upwardsSpeed += GRAVITY * delta;
        super.increasePostion(0, upwardsSpeed * delta, 0);
        float terrainHeight =  terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
        if(super.getPosition().y<terrainHeight) {
            upwardsSpeed = 0;
            isInAir = false;
            super.getPosition().y = terrainHeight;
        }

    }

    public void shoot(ParticleTexture particleTexture) {



//        if (isShooting) {


            new Particle(particleTexture, new Vector3f(getPosition()),
                    new Vector3f(0, 200, 0), 1, 5, 0, 20);



            isShooting = false;
//        }

    }


    public void checkInputs(Window window, MousePicker mousePicker, Camera camera) {



        if (window.isKeyPressed(GLFW_KEY_W)) {
            this.currentSpeed = RUN_SPEED;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            this.currentSpeed = -RUN_SPEED;
        } else {
            this.currentSpeed = 0;
        }

        if (window.isKeyPressed(GLFW_KEY_A)) {
            this.currentTurnSpeed = TURN_SPEED;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            this.currentTurnSpeed= -TURN_SPEED;
        } else {
            this.currentTurnSpeed = 0;
        }

        if (window.isKeyPressed(GLFW_KEY_SPACE)) {



            if (!isInAir) {
                this.upwardsSpeed = JUMP_POWER;
                isInAir = true;
                //sources[nextSource++].play(JUMP_BUFFER);
                //nextSource = nextSource < MAX_SOURCES ? nextSource : 0;


            }

        }

        if (window.isKeyPressed(GLFW_KEY_Y)) {
            if (shootIntervalCount == 0) {
                isShooting = true;
                //sources[nextSource++].play(SHOT_BUFFER);
                //nextSource = nextSource < MAX_SOURCES ? nextSource : 0;

            }
            shootIntervalCount++;
            shootIntervalCount = shootIntervalCount < SHOOT_INTERVAL ? shootIntervalCount : 0;

        } else {
            shootIntervalCount = 0;
        }
    }




    public void cleanUp() {
//        for (int i = 0; i < MAX_SOURCES; i++) {
//            sources[i].delete();
//        }
//        ear.delete();

    }



}
