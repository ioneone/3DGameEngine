package textures;

/**
 * Created by one on 7/24/16.
 */

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import org.lwjgl.opengl.*;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.*;

public class Texture {

    private final int id;

    private final int width;

    private final int height;

    private int numRows = 1;

    //private int numCols = 1; // The texture should always be square, so numRows always equals to numCols

    private boolean hasTransparency = false;


    /**
     * Creates an empty texture.
     *
     * @param width Width of the texture
     * @param height Height of the texture
     * @param pixelFormat Specifies the format of the pixel data (GL_RGBA, etc.)
     * @throws Exception
     */

    public Texture(String fileName, boolean isGameTexture) throws Exception {
        this(Texture.class.getResourceAsStream(fileName), isGameTexture);
    }

    public Texture(String fileName, int numRows, boolean isGameTexture) throws Exception  { // use TextureAtlas
        this(fileName, isGameTexture);
        //this.numCols = numCols;
        this.numRows = numRows;
    }

    public Texture(InputStream is, boolean isGameTexture) throws Exception {
        try {
            // Load Texture file
            PNGDecoder decoder = new PNGDecoder(is);

            this.width = decoder.getWidth();
            this.height = decoder.getHeight();

            // Load texture contents into a byte buffer
            ByteBuffer buf = ByteBuffer.allocateDirect(
                    4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
            buf.flip();

            // Create a new OpenGL texture
            this.id = glGenTextures();
            // Bind the texture
            glBindTexture(GL_TEXTURE_2D, this.id);

            // Tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte size
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            // Upload the texture data
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);

            // Generate Mip Map
            glGenerateMipmap(GL_TEXTURE_2D);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            if (isGameTexture) {
                glTexParameterf(GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4f); // LOD (level of details)
            } else {
                glTexParameterf(GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0); // LOD (level of details)
            }


            // set anisotropic filtering
            GLCapabilities caps = GL.getCapabilities();
            if (caps.GL_EXT_texture_filter_anisotropic) {
                float amount = Math.min(4f, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
            } else {
                System.out.println("anisotropic not supported");

            }


            is.close();
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    //public int getNumCols() {
    //    return numCols;
    //}

    public int getNumRows() {
        return numRows;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public int getId() {
        return id;
    }

    public void cleanup() {
        glDeleteTextures(id);
    }

    public boolean isHasTransparency() {
        return hasTransparency;
    }

    public void setHasTransparency(boolean hasTransparency) {
        this.hasTransparency = hasTransparency;
    }



}