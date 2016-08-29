package engineTester;

import audio.AudioMaster;
import audio.Source;
import buttons.AbstractButton;
import buttons.IButton;
import entities.*;
import fonts.fontMeshCreator.FontType;
import fonts.fontMeshCreator.GUIText;
import fonts.fontRendering.TextMaster;
import guis.GUIRenderer;
import guis.GUITexture;
import lights.DirectionalLight;
import lights.PointLight;
import lights.SpotLight;
import loaders.Loader;
import loaders.OBJLoader;

import mouse.MousePicker;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import renderEngine.*;

import terrains.Terrain;
import terrains.TerrainTexture;
import terrains.TerrainTexturePack;
import textures.Texture;
import textures.TexturedModel;
import mouse.MouseInput;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;
import weathers.Fog;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by one on 7/24/16.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Game implements GameLogic {

    private MousePicker mousePicker;

    private WaterShader waterShader;
    private WaterRenderer waterRenderer;

    private ParticleSystem particleSystem;
    private ParticleTexture particleTexture;


    private final GUIRenderer guiRenderer;
    private final MasterRenderer renderer;
    private final Loader loader;

    private Camera camera;

    private List<Entity> entities = new ArrayList<Entity>();
    //private List<Terrain> terrains = new ArrayList<Terrain>();
    private List<GUITexture> guis = new ArrayList<GUITexture>();
    private List<WaterTile> waters = new ArrayList<WaterTile>();
    private Terrain[][] terrains = new Terrain[2][2];


    //private Terrain terrain;
    //private Terrain terrain2;
    //private Terrain terrain3;
    //private Terrain terrain4;

    private Player player;

    private List<PointLight> pointLights = new ArrayList<PointLight>();

    private WaterFrameBuffers fbos;

    private float lightAngle = 0;

    private DirectionalLight directionalLight;
    private Fog fog;

    private SpotLight spotLight;
    TexturedModel barrelModel;

    private Fbo multiSampleFbo;
    private Fbo outputFbo;

    private AbstractButton path;

    private String bgMusic = "/audios/bg.wav";
    private Source bgPlayer;

    GameStates gameState = GameStates.MAIN_MENU;



    public Game() {

        renderer = new MasterRenderer();
        loader = new Loader();
        guiRenderer = new GUIRenderer();


    }

    @Override
    public void init(Window window) throws Exception {

        // **** initialization **** //
        AudioMaster.init();

        RawModel rawModel = OBJLoader.loadMesh("/models/person.obj", loader);
        Texture texture = new Texture("/textures/playerTexture.png", false);

        TexturedModel texturedModel = new TexturedModel(rawModel, texture);
        texturedModel.setReflectivity(1);
        texturedModel.setShineDamper(0.5f);


        player = new Player(texturedModel, new Vector3f(250, 0, 250), 0, 0, 0, 2);
        entities.add(player);

        AudioMaster.setListenerData(player.getPosition().x, player.getPosition().y, player.getPosition().z);
        AL10.alDistanceModel(AL11.AL_LINEAR_DISTANCE_CLAMPED);

        camera = new Camera(player);


        guiRenderer.init(loader);
        renderer.init(window, loader, camera);
        TextMaster.init(loader);
        ParticleMaster.init(loader, renderer.getProjectionMatrix());

        waterShader = new WaterShader();
        fbos = new WaterFrameBuffers();
        waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);

        mousePicker = new MousePicker(camera, renderer.getProjectionMatrix(), terrains);
        // ***************************** //



        // **** Terrain Information **** //
        TerrainTexture backgroundTexture = new TerrainTexture(new Texture("/textures/grassy.png", true).getId());
        TerrainTexture rTexture = new TerrainTexture(new Texture("/textures/mud.png", true).getId());
        TerrainTexture gTexture = new TerrainTexture(new Texture("/textures/grassFlowers.png", true).getId());
        TerrainTexture bTexture = new TerrainTexture(new Texture("/textures/path.png", true).getId());

        TerrainTexturePack terrainTexturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(new Texture("/textures/blendMap.png", true).getId());


        //terrain = new Terrain(0, 0, loader, terrainTexturePack, blendMap, "/textures/heightmap.png");
        //terrain = new Terrain(0, 0, loader, terrainTexturePack, blendMap);
        //terrains.add(terrain);
        //terrain2 = new Terrain(-1, 0, loader, terrainTexturePack, blendMap, "/textures/heightmap.png");
        //terrains.add(terrain2);
        //terrain3 = new Terrain(0, -1, loader, terrainTexturePack, blendMap, "/textures/heightmap.png");
        //terrain4 = new Terrain(-1, -1, loader, terrainTexturePack, blendMap, "/textures/heightmap.png");
        // ***************************** //

        // **** multiple terrains test **** //
        terrains[0][0] = new Terrain(0, 0, loader, terrainTexturePack, blendMap, "/textures/heightmap0.png");
        terrains[0][1] = new Terrain(0, 1, loader, terrainTexturePack, blendMap, "/textures/heightmap.png");
        terrains[1][0] = new Terrain(1, 0, loader, terrainTexturePack, blendMap, "/textures/heightmap.png");
        terrains[1][1] = new Terrain(1, 1, loader, terrainTexturePack, blendMap, "/textures/heightmap.png");
        // ******************************** //

        // **** model test **** //
        TexturedModel fern = new TexturedModel(new OBJLoader().loadMesh("/models/fern.obj", loader), new Texture("/textures/fern.png", 2, true));

        TexturedModel dragon = new TexturedModel(new OBJLoader().loadMesh("/models/tree.obj", loader), new Texture("/textures/tree.png", true));


        barrelModel = new TexturedModel(OBJLoader.loadMesh("/models/zombie.obj", loader), new Texture("/textures/zombie.png", true));
        barrelModel.setShineDamper(1f);
        barrelModel.setReflectivity(1f);
        //barrelModel.setNormalMap(new Texture("/textures/barrelNormal.png"));

        entities.add(new Entity(barrelModel, new Vector3f(400, terrains[(int)(400/Terrain.SIZE)][(int)(400/Terrain.SIZE)].getHeightOfTerrain(400, 400), 400), 0, 0, 0, 10));

        entities.add(new Entity(barrelModel, new Vector3f(300, terrains[(int)(300/Terrain.SIZE)][(int)(500/Terrain.SIZE)].getHeightOfTerrain(300, 500), 500), 0, 45, 0, 10));



        Random random = new Random(676452);

        for (int i = 0; i < 100; i++) {
            if (i % 2 == 0 || i % 5 == 0) {
                float x = random.nextFloat() * 800;
                float z = random.nextFloat() * 600;
                float y = terrains[(int)(x/Terrain.SIZE)][(int)(z/Terrain.SIZE)].getHeightOfTerrain(x, z);
                entities.add(new Entity(barrelModel, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 10f));
            }
        }

        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0 || i % 5 == 0) {
                float x = random.nextFloat() * 800;
                float z = random.nextFloat() * 600;
                float y = terrains[(int)(x/Terrain.SIZE)][(int)(z/Terrain.SIZE)].getHeightOfTerrain(x, z);
                entities.add(new Entity(dragon, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 50f));
            }
        }
        // ******************************** //


        // **** GUITexture test **** //
        GUITexture gui = new GUITexture(new Texture("/textures/health.png", true).getId(), new Vector2f(0.7f, 0.9f), new Vector2f(0.25f, 0.25f));
        GUITexture gui2 = new GUITexture(new Texture("/textures/thinmatrix.png", true).getId(), new Vector2f(-0.7f, -0.9f), new Vector2f(0.25f, .25f));
        guis.add(gui);
        guis.add(gui2);
        // ************************ //


        // **** point pointLights test **** //
        PointLight pointLight = new PointLight(new Vector3f(10000, 15000, -10000), new Vector3f(0.6f, 0.6f, 0.6f));
        PointLight pointLight2 = new PointLight(player.getPosition(), new Vector3f(20,0,0));
        PointLight pointLight3 = new PointLight(new Vector3f(150, terrains[(int)(150/Terrain.SIZE)][(int)(150/Terrain.SIZE)].getHeightOfTerrain(150, 150), 150), new Vector3f(0,50,0), new Vector3f(1, 0.01f, 0.03f));
        PointLight pointLight4 = new PointLight(new Vector3f(300, terrains[(int)(300/Terrain.SIZE)][(int)(300/Terrain.SIZE)].getHeightOfTerrain(300, 300), 300), new Vector3f(0,0,50), new Vector3f(1, 0.01f, 0.03f));
        pointLights.add(pointLight);
        //pointLights.add(pointLight2);
        pointLights.add(pointLight3);
        pointLights.add(pointLight4);

        TexturedModel lamp = new TexturedModel(new OBJLoader().loadMesh("/models/fence.obj", loader), new Texture("/textures/rock.png", true));
        entities.add(new Entity(lamp, new Vector3f(0, terrains[(int)(0/Terrain.SIZE)][(int)(0/Terrain.SIZE)].getHeightOfTerrain(0,0), 0), 0, 0, 0, 40f));
        entities.add(new Entity(lamp, new Vector3f(150, terrains[(int)(150/Terrain.SIZE)][(int)(150/Terrain.SIZE)].getHeightOfTerrain(150, 150), 150), 0, 0, 0, 40f));
        entities.add(new Entity(lamp, new Vector3f(300, terrains[(int)(300/Terrain.SIZE)][(int)(300/Terrain.SIZE)].getHeightOfTerrain(300, 300), 300), 0, 0, 0, 40f));
        // ************************** //


        // **** GUIText test **** //
        FontType fontType = new FontType(new Texture("/fonts/ricky.png", false).getId(), new File("res/fonts/ricky.fnt"));
        GUIText text = new GUIText("Zombie Game!!", 5f, fontType, new Vector2f(0f, 0f), 1f, true);
        GUIText text2 = new GUIText("Start", 1f, fontType, new Vector2f(0, 0f), 1f, false);
        GUIText text3 = new GUIText("Quit", 1f, fontType, new Vector2f(0.5f, 0.5f), 1f, false);
        text.setColour(0, 0, 1);
        // ********************** //

        // **** water test **** //
        waters.add(new WaterTile(400, 400, terrains[(int)(400/Terrain.SIZE)][(int)(400/Terrain.SIZE)].getHeightOfTerrain(150, 150)+2));
        //GUITexture refraction = new GUITexture(fbos.getRefractionTexture(), new Vector2f(-0.5f, 0.5f), new Vector2f(0.2f, 0.2f));
        //GUITexture reflection = new GUITexture(fbos.getReflectionTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.2f, 0.2f));
        //guis.add(refraction);
        //guis.add(reflection);
        // ******************** //


        particleTexture = new ParticleTexture(new Texture("/textures/particle_anim.png", true).getId(), 4);

        particleSystem = new ParticleSystem(particleTexture, 50, 1000, 1f, 0.05f, 1);


        directionalLight = new DirectionalLight(new Vector3f(30, -30, 30), new Vector3f(0, 0, 0), 0);
        directionalLight.setShadowPosMult(100);
        directionalLight.setOrthoCoords(-10.0f, 10.0f, -10.0f, 10.0f, -1.0f, 20.0f);

        spotLight = new SpotLight(new Vector3f(0, 0, 0), new Vector3f(1, 0, 0), new Vector3f((float)Math.sin(player.getRotY()), 0, (float) Math.cos(player.getRotY())), (float)Math.cos(Math.toRadians(10)), new Vector3f(1, 0.001f, 0.0001f));



        // **** shadow test **** //
        //GUITexture shadowMap = new GUITexture(renderer.getShadowMapTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 0.5f));
        //guis.add(shadowMap);
        // ********************* //


        RawModel zombieRawModel = OBJLoader.loadMesh("/models/zombie.obj", loader);
        Texture zombieTexture = new Texture("/textures/zombie.png", true);
        TexturedModel zombieTexturedModel = new TexturedModel(zombieRawModel,zombieTexture);
        zombieTexturedModel.setReflectivity(1);
        zombieTexturedModel.setShineDamper(0.5f);

        TexturedModel texturedFence = new TexturedModel(new OBJLoader().loadMesh("/models/fence.obj", loader), new Texture("/textures/rock.png", true));


        // **** post processing **** //
        multiSampleFbo = new Fbo(Window.getWidth(), Window.getHeight());
        outputFbo = new Fbo(Window.getWidth(), Window.getHeight(), Fbo.DEPTH_TEXTURE);
        PostProcessing.init(loader);
        // ************************ //


        // **** model test **** //
        TexturedModel coolBarrelModel = new TexturedModel(OBJLoader.loadMesh("/models/barrel.obj", loader), new Texture("/textures/barrel.png", true));
        //coolBarrelModel.setNormalMap(new Texture("/textures/barrelNormal.png"));
        coolBarrelModel.setSpecularMap(new Texture("/textures/barrelSpecular.png", true));
        coolBarrelModel.setShineDamper(10);
        coolBarrelModel.setReflectivity(0.5f);

        entities.add(new Entity(coolBarrelModel, new Vector3f(300, terrains[(int)(300/Terrain.SIZE)][(int)(140/Terrain.SIZE)].getHeightOfTerrain(300, 140)+10, 140), 0, 0, 0, 1f));

        TexturedModel wedge = new TexturedModel(OBJLoader.loadMesh("/models/wedge.obj", loader), new Texture("/textures/rock.png", true));
        entities.add(new Entity(wedge, new Vector3f(410, terrains[(int)(410/Terrain.SIZE)][(int)(320/Terrain.SIZE)].getHeightOfTerrain(410, 320), 320), 0, 0, 0, 10));

        TexturedModel parascope = new TexturedModel(OBJLoader.loadMesh("/models/parascope.obj", loader), new Texture("/textures/rock.png", true));
        entities.add(new Entity(parascope, new Vector3f(460, terrains[(int)(460/Terrain.SIZE)][(int)(320/Terrain.SIZE)].getHeightOfTerrain(460, 320), 320), 0, 0, 0, 10));

        TexturedModel plane = new TexturedModel(OBJLoader.loadMesh("/models/plane.obj", loader), new Texture("/textures/zombie.png", true));
        plane.setShineDamper(0.5f);
        plane.setReflectivity(0.5f);
        entities.add(new Entity(plane, new Vector3f(460, terrains[(int)(460/Terrain.SIZE)][(int)(320/Terrain.SIZE)].getHeightOfTerrain(460, 320)+100, 320), 0, 0, 0, 10));

        TexturedModel pyramid = new TexturedModel(OBJLoader.loadMesh("/models/pyramid.obj", loader), new Texture("/textures/rock.png", true));
        entities.add(new Entity(pyramid, new Vector3f(350, terrains[(int)(350/Terrain.SIZE)][(int)(500/Terrain.SIZE)].getHeightOfTerrain(350, 500), 500), 0, -90, 0, 10));
        // **************** //


        // ***** Button **** //

        path = new AbstractButton(new Texture("/textures/path.png", true).getId(), new Vector2f(-1f, 1f), new Vector2f(0.2f, 0.2f)) {
            @Override
            public void onClick(IButton button) {
                System.out.println("button clicked!");
            }

            @Override
            public void onStartHover(IButton button) {
                button.playHoverAnimation(0.092f);
            }

            @Override
            public void onStopHover(IButton button) {
                button.resetScale();
            }

            @Override
            public void whileHovering(IButton button) {

            }
        };

        path.show(guis);

        // ***************** //

        // **** model test **** //
        TexturedModel stall = new TexturedModel(OBJLoader.loadMesh("/models/stall.obj", loader), new Texture("/textures/stallTexture.png", true));
        entities.add(new Entity(stall, new Vector3f(200, terrains[(int)(200/Terrain.SIZE)][(int)(230/Terrain.SIZE)].getHeightOfTerrain(200, 230), 230), 0, 0, 0, 10));

        TexturedModel lowPolyTree = new TexturedModel(OBJLoader.loadMesh("/models/lowPolyTree.obj", loader), new Texture("/textures/lowPolyTree.png", true));
        entities.add(new Entity(lowPolyTree, new Vector3f(20, terrains[(int)(20/Terrain.SIZE)][(int)(230/Terrain.SIZE)].getHeightOfTerrain(20, 230), 230), 0, 0, 0, 5));

        TexturedModel pine = new TexturedModel(OBJLoader.loadMesh("/models/pine.obj", loader), new Texture("/textures/pine.png", true));
        entities.add(new Entity(pine, new Vector3f(50, terrains[(int)(50/Terrain.SIZE)][(int)(230/Terrain.SIZE)].getHeightOfTerrain(50, 230), 230), 0, 0, 0, 5));
        // ********************* //

        // **** fog test **** //
        fog = new Fog(0.0035f, 5.0f);
        // ****************** //

        // **** audio test **** //
        int bgBuffer = AudioMaster.loadSound(bgMusic);
        bgPlayer = new Source();
        bgPlayer.setVolume(0.5f);
        bgPlayer.setPosition(player.getPosition().x, player.getPosition().y, player.getPosition().z);
        bgPlayer.setLooping(true);
        //bgPlayer.play(bgBuffer);
        // ******************** //

    }

    @Override
    public void input(Window window) {

        player.checkInputs(window, mousePicker, camera);

        if (window.isKeyPressed(GLFW_KEY_P)) {
            gameState = GameStates.OPTIONS;
        }
        if (window.isKeyPressed(GLFW_KEY_O)) {
            gameState = GameStates.PLAYING;
        }

    }

    @Override
    public void update(float interval, MouseInput mouseInput, Window window) {

        path.update(mouseInput);

        //System.out.println(camera.getYaw());
        float mouseX = (float) mouseInput.getCurrentPosition().x;
        float mouseY = (float) mouseInput.getCurrentPosition().y;
        Vector2f normalizedMousePos = mousePicker.getNormalizedDeviceCoords(window, mouseX, mouseY);
        //System.out.println(mouseX + ", " + mouseY);
        //System.out.println(normalizedMousePos.x + ", " + normalizedMousePos.y);

        spotLight.setPosition(new Vector3f(player.getPosition().x, player.getPosition().y+10, player.getPosition().z));
        spotLight.setDirection(new Vector3f((float)Math.sin(Math.toRadians(player.getRotY())), 0, (float) Math.cos(Math.toRadians(player.getRotY()))));


        // **** sun simulation (directional light) **** //
        /*
        lightAngle += 1.1f;
        if (lightAngle > 90) {
            directionalLight.setIntensity(0);
            if (lightAngle >= 360) {
                lightAngle = -90;
            }

        } else if (lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 - (float)(Math.abs(lightAngle) - 80)/ 10.0f;
            directionalLight.setIntensity(factor);
            directionalLight.getColour().y = Math.max(factor, 0.9f);
            directionalLight.getColour().z = Math.max(factor, 0.5f);
        } else {
            directionalLight.setIntensity(1);
            directionalLight.getColour().x = 1;
            directionalLight.getColour().y = 1;
            directionalLight.getColour().z = 1;
        }
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);
        */
        // ******************************************* //



        player.move(terrains[(int)(player.getPosition().x/Terrain.SIZE)][(int)(player.getPosition().z/Terrain.SIZE)]);
        //player.shoot(particleTexture);
        player.shoot(particleTexture);

        camera.move(mouseInput);
        mousePicker.update(window, mouseInput);

        Vector3f terrainPoint = mousePicker.getCurrentTerrainPoint();
        if (terrainPoint != null) {
            //System.out.println(terrainPoint.x+", "+terrainPoint.y+", "+terrainPoint.z);
        }






//        if (window.isKeyPressed(GLFW_KEY_P)) {
//            new Particle(new Vector3f(player.getPosition()), new Vector3f(0, 30, 0), 1, 4, 0, 1);
//        }



        //particleSystem.generateParticles(player.getPosition());
        //particleSystem.generateParticles(new Vector3f(0, 0, 0));

        ParticleMaster.update(camera);

    }

    @Override
    public void render(Window window) {

        //System.out.println(mousePicker.getCurrentRay().x+", "+mousePicker.getCurrentRay().y+", "+mousePicker.getCurrentRay().z);

        renderer.renderShadowMap(entities, pointLights.get(0));

        GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

        // render reflection texture
        fbos.bindReflectionFrameBuffer();
        float distance = 2 * (camera.getPosition().y - waters.get(0).getHeight());
        camera.getPosition().y -= distance;
        camera.invertPitch();
        renderer.renderScene(entities, terrains, pointLights, camera, new Vector4f(0, 1, 0, -waters.get(0).getHeight()+1f),directionalLight, spotLight, fog);
        camera.getPosition().y += distance;
        camera.invertPitch();

        // render refraction texture
        fbos.bindRefractionFrameBuffer();
        renderer.renderScene(entities, terrains, pointLights, camera, new Vector4f(0, -1, 0, waters.get(0).getHeight()), directionalLight, spotLight, fog);

        // render to screen
        fbos.unbindCurrentFrameBuffer();
        if (gameState == GameStates.OPTIONS) {
            multiSampleFbo.bindFrameBuffer();
        }
        renderer.renderScene(entities, terrains, pointLights, camera, new Vector4f(0, -1, 0, 10000), directionalLight, spotLight, fog);

        //waterRenderer.render(waters, camera, pointLights.get(0));

        ParticleMaster.renderParticles(camera);

        if (gameState == GameStates.OPTIONS) {
            multiSampleFbo.unbindFrameBuffer();
            multiSampleFbo.resolveToFbo(outputFbo);
            PostProcessing.doPostProcessing(outputFbo.getColourTexture());
            guiRenderer.render(guis);
            TextMaster.render();
        }

    }

    @Override
    public void cleanUp() {

        fbos.cleanUp();
        waterShader.cleanUp();
        ParticleMaster.cleanUp();
        guiRenderer.cleanUp();
        TextMaster.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        player.cleanUp();
        AudioMaster.cleanUp();
        multiSampleFbo.cleanUp();
        outputFbo.cleanUp();
        PostProcessing.cleanUp();
        
    }
}
