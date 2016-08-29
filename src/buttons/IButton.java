package buttons;

import guis.GUITexture;
import mouse.MouseInput;

import java.util.List;

/**
 * Created by one on 8/16/16.
 */
public interface IButton {

    void onClick(IButton button);

    void onStartHover(IButton button);

    void onStopHover(IButton button);

    void whileHovering(IButton button);

    void show(List<GUITexture> guiTextureList);

    void hide(List<GUITexture> guiTextureList);

    void playHoverAnimation(float scaleFactor);

    void resetScale();

    void update(MouseInput mouseInput);


}
