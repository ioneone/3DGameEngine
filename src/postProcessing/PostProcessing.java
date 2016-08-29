package postProcessing;

import bloom.BrightFilter;
import bloom.CombineFilter;
import gaussianBlur.HorizontalBlur;
import gaussianBlur.VerticalBlur;
import loaders.Loader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.RawModel;
import renderEngine.Window;


public class PostProcessing {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static RawModel quad;
	private static ContrastChanger contrastChanger;
	private static HorizontalBlur hBlur;
	private static VerticalBlur vBlur;
	private static HorizontalBlur hBlur2;
	private static VerticalBlur vBlur2;
	private static BrightFilter brightFilter;
	private static CombineFilter combineFilter;

	public static void init(Loader loader){
		quad = loader.loadToVAO(POSITIONS, 2);
		contrastChanger = new ContrastChanger();
		hBlur = new HorizontalBlur(Window.getWidth()/5, Window.getHeight()/5);
		vBlur = new VerticalBlur(Window.getWidth()/5, Window.getHeight()/5);
		hBlur2 = new HorizontalBlur(Window.getWidth()/2, Window.getHeight()/2);
		vBlur2 = new VerticalBlur(Window.getWidth()/2, Window.getHeight()/2);
		brightFilter = new BrightFilter(Window.getWidth()/2, Window.getHeight()/2);
		combineFilter = new CombineFilter();
	}
	
	public static void doPostProcessing(int colourTexture){
		start();
		brightFilter.render(colourTexture);
		hBlur2.render(colourTexture);
		vBlur2.render(hBlur2.getOutputTexture());
		hBlur.render(brightFilter.getOutputTexture());
		vBlur.render(hBlur.getOutputTexture());
		//contrastChanger.render(vBlur.getOutputTexture());
		combineFilter.render(vBlur2.getOutputTexture(), vBlur.getOutputTexture());
		end();

	}
	
	public static void cleanUp(){
		contrastChanger.cleanUp();
		hBlur.cleanUp();
		vBlur.cleanUp();
		hBlur2.cleanUp();
		vBlur2.cleanUp();
		brightFilter.cleanUp();
		combineFilter.cleanUp();
	}
	
	private static void start(){
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	private static void end(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}


}
