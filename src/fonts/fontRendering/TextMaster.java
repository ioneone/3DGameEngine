package fonts.fontRendering;

import fonts.fontMeshCreator.FontType;
import fonts.fontMeshCreator.GUIText;
import fonts.fontMeshCreator.TextMeshData;
import loaders.Loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by one on 7/30/16.
 */
public class TextMaster {

    private static Loader loader;
    private static Map<FontType, List<GUIText>> texts = new HashMap<FontType, List<GUIText>>();
    private static FontRenderer fontRenderer;

    public static void init(Loader theLoader) {

        fontRenderer = new FontRenderer();
        loader = theLoader;
    }

    public static void render() {
        fontRenderer.render(texts);
    }

    public static void cleanUp() {
        fontRenderer.cleanUp();
    }

    public static void loadText(GUIText text) {

        FontType font = text.getFont();
        TextMeshData data = font.loadText(text);
        int vao = loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
        text.setMeshInfo(vao, data.getVertexCount());
        List<GUIText> textBatch = texts.get(font);
        if (textBatch == null) {
            textBatch = new ArrayList<GUIText>();
            texts.put(font, textBatch);
        }
        textBatch.add(text);

    }

    public static void removeText(GUIText text) {
        List<GUIText> textBatch = texts.get(text.getFont());
        texts.remove(text);
        if (textBatch.isEmpty()) {
            texts.remove(text.getFont());
        }
    }
}
