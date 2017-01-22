package systems.crigges.informaticup.gui;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;


public class AssetFactory {
	private static Map<String, Texture> textures = new HashMap<String, Texture>();
	private static Map<String, FreeTypeFontGenerator> fonts = new HashMap<String, FreeTypeFontGenerator>();
	private static Map<String, Animation> animations = new HashMap<String, Animation>();
	private static Map<String, Music> musics = new HashMap<String, Music>();
	private static Map<String, Sound> sounds = new HashMap<String, Sound>();
	private static TextButtonStyle defaultTextButtonStyle;
	private static TextFieldStyle defaultTextFieldStyle;
	private static ListStyle defaultListStyle;
	private static CheckBoxStyle defaultCheckBoxStyle;
	private static CheckBoxStyle micMuteBoxStyle;
	private static SliderStyle defaultSliderStyle;
	private static SelectBoxStyle defaultSelectBoxStyle;
	private static float viewportWidth = 1920;
	private static float viewportHeight = 1080;
		
	public static void loadAllRessources(){
		addFont("normal", "normal.otf");
		addTexture("button_normal", "button_normal.png");
		addTexture("button_disabled", "button_disabled.png");
		addTexture("button_pressed", "button_pressed.png");
		addTexture("button_focus", "button_focus.png");
		addTexture("textarea", "textarea.png");
		addTexture("text_focus", "text_focused.png");
		addTexture("list_background", "list_background.png");
		addTexture("checkbox_on", "checkbox_on.png");
		addTexture("checkbox_off", "checkbox_off.png");
		addTexture("mic_on", "mic_on.png");
		addTexture("mic_off", "mic_off.png");
		addTexture("slider_knob", "scrubber_knob.png");
		addTexture("slider_before", "scrubber_primary.png");
		addTexture("slider_after", "scrubber_secondary.png");
		addTexture("slider_bg", "scrubber_track.png");
		addTexture("select_normal", "select_normal.png");
		addTexture("select_focus", "select_focus.png");
		addTexture("select_selected", "select_selected.png");
		addTexture("background", "background.png");
		addTexture("selector_down", "selector_down.png");
		addTexture("selector_up", "selector_up.png");
		addTexture("quarder", "quarder.png");
		addTexture("circle", "circle.png");
		
		defaultTextButtonStyle = genDefaultButtonStyle();
		defaultTextFieldStyle = genDefaultTextFieldStyle();
		defaultListStyle = genDefaultListStyle();
		defaultCheckBoxStyle = genDefaultCheckBoxStyle();
		micMuteBoxStyle = genMicMuteBoxStyle();
		defaultSliderStyle = genDefaultSliderStyle();
		defaultSelectBoxStyle = genDefaultSelectBoxStyle();
	}
	
	private static SelectBoxStyle genDefaultSelectBoxStyle() {
		SelectBoxStyle style = new SelectBoxStyle();
		style.font = getFont("normal", 25);						//									left, 	right, 	top, 	bot						
		style.background = new NinePatchDrawable(new NinePatch(getTexture("select_normal"), 		10, 	32, 	30, 	34));
		style.backgroundOpen = new NinePatchDrawable(new NinePatch(getTexture("select_selected"), 	10, 	32, 	30, 	34));
		style.backgroundOver = new NinePatchDrawable(new NinePatch(getTexture("select_focus"), 		10, 	32, 	30, 	34));
		style.listStyle = defaultListStyle;
		ScrollPaneStyle scstyle = new ScrollPaneStyle();
		style.scrollStyle = scstyle;
		return style;
	}

	private static SliderStyle genDefaultSliderStyle(){
		SliderStyle style = new SliderStyle();
		style.knob = new TextureRegionDrawable(new TextureRegion(getTexture("slider_knob")));
		NinePatch p = new NinePatch(getTexture("slider_before"), 10, 10, 10, 10);
		style.knobBefore = new NinePatchDrawable(p);
		p = new NinePatch(getTexture("slider_after"), 10, 10, 0, 0);
		style.background = new NinePatchDrawable(p);
//		p = new NinePatch(getTexture("slider_bg"), 10, 10, 0, 0);
//		style.background = new NinePatchDrawable(p);
//		style.background.setMinHeight(110);
		return style;
	}	
	
	public static SliderStyle getDefaultSliderStyle() {
		return defaultSliderStyle;
	}
	
	public static CheckBoxStyle getMicMuteBoxStyle() {
		return micMuteBoxStyle;
	}
	
	public static SelectBoxStyle getDefaultSelectBoxStyle() {
		return defaultSelectBoxStyle;
	}
	
	private static CheckBoxStyle genMicMuteBoxStyle(){
		CheckBoxStyle style = new CheckBoxStyle();
		style.checkboxOn = new TextureRegionDrawable(new TextureRegion(getTexture("mic_off")));
		style.checkboxOff = new TextureRegionDrawable(new TextureRegion(getTexture("mic_on")));
		style.font = getFont("normal", 60);
		style.fontColor = Color.WHITE;
		style.checkedFontColor = Color.WHITE;
		return style;
	}	
	
	private static CheckBoxStyle genDefaultCheckBoxStyle(){
		CheckBoxStyle style = new CheckBoxStyle();
		NinePatch p = new NinePatch(getTexture("checkbox_on"), 10, 10, 10, 10);
		style.checkboxOn = new NinePatchDrawable(p);
		p = new NinePatch(getTexture("checkbox_off"), 10, 10, 10, 10);
		style.checkboxOff = new NinePatchDrawable(p);
		style.font = getFont("normal", 60);
		style.fontColor = Color.WHITE;
		style.checkedFontColor = Color.WHITE;
		return style;
	}
	
	public static CheckBoxStyle getDefaultCheckBoxStyle() {
		return defaultCheckBoxStyle;
	}
	
	public static ListStyle getDefaultListStyle() {
		return defaultListStyle;
	}

	private static ListStyle genDefaultListStyle(){
		ListStyle style = new ListStyle();
		NinePatch p = new NinePatch(getTexture("text_focus"), 10, 10, 10, 10);
		style.background = new NinePatchDrawable(p);
		p = new NinePatch(getTexture("textarea"), 10, 10, 10, 10);
		style.selection = new NinePatchDrawable(p);
		style.fontColorSelected = Color.WHITE;
		style.fontColorUnselected = Color.WHITE;
		style.font = getFont("normal", 20);
		return style;
	}
	
	public static TextButtonStyle getDefaultButtonStyle(){
		return defaultTextButtonStyle;
	}
	
	private static TextButtonStyle genDefaultButtonStyle(){
		TextButtonStyle style = new TextButtonStyle();
		NinePatch p = new NinePatch(getTexture("button_normal"), 20, 20, 20, 20);
		style.up = new NinePatchDrawable(p);
		style.fontColor = Color.WHITE;
		p = new NinePatch(getTexture("button_disabled"), 20, 20, 20, 20);
		style.disabled = new NinePatchDrawable(p);
		style.disabledFontColor = Color.GRAY;
		p = new NinePatch(getTexture("button_pressed"), 20, 20, 20, 20);
		style.down = new NinePatchDrawable(p);
		p = new NinePatch(getTexture("button_focus"), 20, 20, 20, 20);
		style.over = new NinePatchDrawable(p);
		BitmapFont font = getFont("normal", 30);
		style.font = font;
		return style;
	}
	
	public static TextFieldStyle getDefaultTextFieldStyle(){
		return defaultTextFieldStyle;
	}
	
	private static TextFieldStyle genDefaultTextFieldStyle(){
		TextFieldStyle style = new TextFieldStyle();
		style.background = new NinePatchDrawable(new NinePatch(getTexture("textarea"), 10, 10, 10, 10));
		style.font = getFont("normal", 36);
		style.fontColor = Color.WHITE;
		return style;
	}
	
	public static Drawable getTextureDrawable(String name){
		return new TextureRegionDrawable(new TextureRegion(AssetFactory.getTexture(name)));
	}

	public static Color getDefaultRed() {
		return new Color(245f / 255f, 168f / 255f, 1f / 255f, 1);
	}

	private static void addTexture(String title, String name) {
		Texture t = new Texture("textures/" + name);
		t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		textures.put(title, t);
	}
	
	public static Texture getTexture(String title)	{
		return textures.get(title);
	}
	
	private static void addFont(String title, String path)	{
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/" + path));
		fonts.put(title, gen);
	}
	
	public static void setViewportBounds(int width, int height){
		viewportWidth = width;
		viewportHeight = height;
	}
	
	public static float getFontScaleX(){
		return (viewportWidth / Gdx.graphics.getWidth());
	}
	
	public static float getFontScaleY(){
		return (viewportHeight / Gdx.graphics.getHeight());
	}
	
	public static float getFontScale(){
		return ((viewportWidth / Gdx.graphics.getWidth()) + (viewportHeight / Gdx.graphics.getHeight())) / 2;
	}

	public static BitmapFont getFont(String title, int size)	{
		FreeTypeFontGenerator gen = fonts.get(title);
		FreeTypeFontParameter param = new FreeTypeFontParameter();
		param.magFilter = TextureFilter.Linear;
		param.minFilter = TextureFilter.Linear;
//		param.shadowColor = new Color(0, 0, 0, 1);
//		param.shadowOffsetX = shaddowOffset;
//		param.shadowOffsetY = shaddowOffset;
		//Adjust for real screen size
		param.size = (int) (size / getFontScale());
		BitmapFont font = gen.generateFont(param);
		font.getData().setScale(getFontScaleX(), getFontScaleY());
		return font;
	}
	
	private static void addMusic(String title, Music music){
		musics.put(title, music);
	}
	
	public static Music getMusic(String title){
		return musics.get(title);
	}
	
	private static void addSound(String title, Sound sound){
		sounds.put(title, sound);
	}
	
	public static Sound getSound(String title){
		return sounds.get(title);
	}
	
	private static void addAnimation(String title, Animation anim){
		animations.put(title, anim);
	}
	
	public static Animation getAnimation(String title){
		return animations.get(title);
	}

}
