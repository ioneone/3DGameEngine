package terrains;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.joml.Vector2f;
import org.joml.Vector3f;
import loaders.Loader;
import renderEngine.RawModel;
import toolBox.Maths;

import java.nio.ByteBuffer;

/**
 * Created by one on 7/28/16.
 */
public class Terrain {

    public static final float SIZE = 800;
    private static final float MAX_HEIGHT = 20;
    private static final float MAX_PIXEL_COLOUR = 256 * 256 * 256;

    private float x;
    private float z;
    private RawModel rawModel;
    private TerrainTexturePack terrainTexturePack;
    private TerrainTexture blendMap;

    private float shineDamper = 0.5f;
    private float reflectivity = 0.5f;

    private float[][] heights;

    public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack terrainTexturePack, TerrainTexture blendMap, String heightMapPath) {
        this.terrainTexturePack = terrainTexturePack;
        this.blendMap = blendMap;
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.rawModel = generateTerrain(loader, heightMapPath);

    }

    public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack terrainTexturePack, TerrainTexture blendMap) {
        this.terrainTexturePack = terrainTexturePack;
        this.blendMap = blendMap;
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.rawModel = generateTerrain(loader);

    }

    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }

    public RawModel getRawModel() {
        return rawModel;
    }

    public TerrainTexturePack getTerrainTexturePack() {
        return terrainTexturePack;
    }

    public TerrainTexture getBlendMap() {
        return blendMap;
    }

    private RawModel generateTerrain(Loader loader, String heightMapPath) {

        float[][] imageData = new float[0][0];
        int VERTEX_COUNT = 0;
        try {
            PNGDecoder decoder = new PNGDecoder(Terrain.class.getResourceAsStream(heightMapPath));

            VERTEX_COUNT = decoder.getHeight();
            // Load texture contents into a byte buffer
            ByteBuffer buf = ByteBuffer.allocateDirect(
                    4 * VERTEX_COUNT * VERTEX_COUNT);
            decoder.decode(buf, VERTEX_COUNT * 4, PNGDecoder.Format.RGBA);
            buf.flip();
            int limit = buf.limit();
            imageData = new float[VERTEX_COUNT][VERTEX_COUNT];

            int heightPointer = 0;
            int widthPointer = 0;

            int count = 0;


            for (int i = 0; i < limit; i += 4) {

                imageData[heightPointer][widthPointer] =
                        (float)((buf.get(i) < 0 ? -buf.get(i)+256 : buf.get(i))
                                * (buf.get(i + 1) < 0 ? buf.get(i+1)+256 : buf.get(i+1))
                                * (buf.get(i + 2) < 0 ? buf.get(i+2)+256 : buf.get(i+2)));


                count++;

                if (widthPointer < VERTEX_COUNT - 1) {
                    widthPointer++;
                } else {
                   heightPointer++;
                    widthPointer = 0;
                }

            }

        } catch (Exception e) {
            System.out.println("Failed to load " + heightMapPath);
            e.printStackTrace();
        }

        heights = new float[VERTEX_COUNT][VERTEX_COUNT];
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count*2];
        int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
        int vertexPointer = 0;
        for(int i=0;i<VERTEX_COUNT;i++){
            for(int j=0;j<VERTEX_COUNT;j++){
                vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
                float height = getHeight(j ,i, imageData);
                heights[j][i] = height;
                vertices[vertexPointer*3+1] = height;
                vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
                Vector3f normal = calculateNormal(j, i, imageData);
                normals[vertexPointer*3] = normal.x;
                normals[vertexPointer*3+1] = normal.y;
                normals[vertexPointer*3+2] = normal.z;
                textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
                textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for(int gz=0;gz<VERTEX_COUNT-1;gz++){
            for(int gx=0;gx<VERTEX_COUNT-1;gx++){
                int topLeft = (gz*VERTEX_COUNT)+gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return loader.loadToVAO(vertices, textureCoords, normals, indices);
    }

    private RawModel generateTerrain(Loader loader) {

        HeightGenerator heightGenerator = new HeightGenerator();

        int VERTEX_COUNT = 128;
        heights = new float[VERTEX_COUNT][VERTEX_COUNT];
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count*2];
        int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
        int vertexPointer = 0;
        for(int i=0;i<VERTEX_COUNT;i++){
            for(int j=0;j<VERTEX_COUNT;j++){
                vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
                float height = getHeight(j ,i, heightGenerator);
                heights[j][i] = height;
                vertices[vertexPointer*3+1] = height;
                vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
                Vector3f normal = calculateNormal(j, i, heightGenerator);
                normals[vertexPointer*3] = normal.x;
                normals[vertexPointer*3+1] = normal.y;
                normals[vertexPointer*3+2] = normal.z;
                textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
                textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for(int gz=0;gz<VERTEX_COUNT-1;gz++){
            for(int gx=0;gx<VERTEX_COUNT-1;gx++){
                int topLeft = (gz*VERTEX_COUNT)+gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return loader.loadToVAO(vertices, textureCoords, normals, indices);
    }

    private float getHeight(int x, int z, float[][] imageData) {

        if (x < 0 || x >= imageData[0].length || z < 0 || z >= imageData[0].length) {
            return 0;
        }

        float height = imageData[z][x];
        height /= MAX_PIXEL_COLOUR;
        height -= 0.5f;
        height *= 2;
        height *= MAX_HEIGHT;
        return height;

    }

    private float getHeight(int x, int z, HeightGenerator heightGenerator) {

        return heightGenerator.generateHeight(x, z);

    }

    private Vector3f calculateNormal(int x, int z, float[][] imageData) {
        float heightL = getHeight(x-1, z, imageData);
        float heightR = getHeight(x+1, z, imageData);
        float heightD = getHeight(x, z-1, imageData);
        float heightU = getHeight(x, z+1, imageData);
        Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD-heightU);
        normal.normalize();
        return normal;
    }

    private Vector3f calculateNormal(int x, int z, HeightGenerator heightGenerator) {
        float heightL = getHeight(x-1, z, heightGenerator);
        float heightR = getHeight(x+1, z, heightGenerator);
        float heightD = getHeight(x, z-1, heightGenerator);
        float heightU = getHeight(x, z+1, heightGenerator);
        Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD-heightU);
        normal.normalize();
        return normal;
    }

    public float getShineDamper() {
        return shineDamper;
    }

    public void setShineDamper(float shineDamper) {
        this.shineDamper = shineDamper;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity(float reflectivity) {
        this.reflectivity = reflectivity;
    }

    public float getHeightOfTerrain(float worldX, float worldZ) {

        float terrainX = worldX - this.x;
        float terrainZ = worldZ - this.z;
        float gridSquareSize = SIZE / ((float)heights.length - 1);
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);


        if (gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0) {
            return 0;
        }

        float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
        float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;

        float answer;
        if (xCoord <= (1-zCoord)) {
            answer = Maths
                    .barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
                            heights[gridX + 1][gridZ], 0), new Vector3f(0,
                            heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        } else {
            answer = Maths
                    .barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
                            heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
                            heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        }

        return answer;
    }





}
