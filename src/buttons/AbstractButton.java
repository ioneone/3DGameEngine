package buttons;

import guis.GUITexture;
import loaders.Loader;
import mouse.MouseInput;
import mouse.MousePicker;
import org.joml.Vector2f;

import java.util.List;

/**
 * Created by one on 8/16/16.
 */
public abstract class AbstractButton implements IButton {

    private GUITexture guiTexture;

    private Vector2f scale;

    private boolean isHidden = true;
    private boolean isHovering = false;

    public AbstractButton(int textureId, Vector2f position, Vector2f scale) {

        guiTexture = new GUITexture(textureId, position, scale);
        this.scale = scale;

    }

    @Override
    public void update(MouseInput mouseInput) {

        if (!isHidden) {
            Vector2f location = guiTexture.getPosition();
            Vector2f scale = guiTexture.getScale();

            Vector2f mouseCoordinates = MousePicker.getNormalizedDeviceCoords();

            if (location.y + scale.y > mouseCoordinates.y
                    && location.y - scale.y < mouseCoordinates.y
                    && location.x + scale.x > mouseCoordinates.x
                    && location.x - scale.x < mouseCoordinates.x) {

                whileHovering(this);
                if (!isHovering) {
                    isHovering = true;
                    onStartHover(this);
                }
                if (mouseInput.isLeftButtonPressed()) {
                    onClick(this);
                }

            } else {
                if (isHovering) {
                    isHovering = false;
                    onStopHover(this);
                }

            }
        }

    }


    @Override
    public void show(List<GUITexture> guiTextureList) {

        if (isHidden) {
            guiTextureList.add(guiTexture);
            isHidden = false;
        }

    }

    @Override
    public void hide(List<GUITexture> guiTextureList) {

        if (!isHidden) {
            guiTextureList.remove(guiTexture);
            isHidden = true;
        }

    }

    @Override
    public void resetScale() {
        guiTexture.setScale(scale);
    }

    @Override
    public void playHoverAnimation(float scaleFactor) {
        guiTexture.setScale(new Vector2f(scale.x+scaleFactor, scale.y+scaleFactor));
    }

    public boolean isHidden() {
        return isHidden;
    }

}
