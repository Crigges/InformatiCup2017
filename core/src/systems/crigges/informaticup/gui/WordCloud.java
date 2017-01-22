package systems.crigges.informaticup.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Queue;
import systems.crigges.informaticup.crawling.FileType;

public class WordCloud {
	private static final int maxWords = 65;
	private static final float minScale = 110;
	private static final float addScale = 8000;
	private static final float centerX = 1000;
	private static final float centerY = 460;
	private static Color[] colors = new Color[FileType.values().length];
	private static Random colRan = new Random();

	private ValueTreeMap<String, Word> wordCount = new ValueTreeMap<>();
	private HashMap<String, WordBubble> displayedWords = new HashMap<>();
	private Queue<WordBubble> wordBuffer = new Queue<>(maxWords);
	private ArrayList<WordBubble> activeWords = new ArrayList<>();
	private Stage stage;
	private long totalWordCount;
	private Action mainLoop;
	private boolean active = true;

	static {
		colors[0] = Color.GOLD;
		colors[1] = Color.CORAL;
		colors[2] = Color.MAGENTA;
		colors[3] = Color.NAVY;
		colors[4] = Color.GRAY;
		colors[5] = Color.OLIVE;
		colors[6] = Color.TEAL;
		colors[7] = Color.FIREBRICK;
	}
	
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
	    return map.entrySet()
	              .stream()
	              .sorted(Map.Entry.comparingByValue())
	              .collect(Collectors.toMap(
	                Map.Entry::getKey, 
	                Map.Entry::getValue, 
	                (e1, e2) -> e1, 
	                LinkedHashMap::new
	              ));
	}
	
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueReverse(Map<K, V> map) {
	    return map.entrySet()
	              .stream()
	              .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
	              .collect(Collectors.toMap(
	                Map.Entry::getKey, 
	                Map.Entry::getValue, 
	                (e1, e2) -> e1, 
	                LinkedHashMap::new
	              ));
	}
	
	public void clear(){
		wordCount.clear();
		displayedWords.clear();
		wordBuffer.clear();
		for(WordBubble bub: activeWords){
			bub.remove();
		}
		activeWords.clear();
	}

	public WordCloud(Stage stage) {

		this.stage = stage;
		for (int i = 0; i < maxWords; i++) {
			wordBuffer.addFirst(new WordBubble());
		}
		mainLoop = new Action() {

			@Override
			public synchronized boolean act(float delta) {
				if(!active){
					return true;
				}
				synchronized (displayedWords) {
					synchronized (wordCount) {
						int c = 0;
						if (totalWordCount < 200) {
							return false;
						}

						int swapped = 0;
						for (Word w : wordCount) {
							if (displayedWords.get(w.name) == null) {
								if (wordBuffer.size > 0) {
									WordBubble bub = wordBuffer.removeLast();
									bub.setActive(w, (float) (Math.random() * 5000) - 1500,
											(float) (Math.random() * 3000) - 500, stage);
									if(w != bub.word){
										System.out.println("balg");
									}
									displayedWords.put(w.name, bub);
									activeWords.add(bub);
								} else {
									Collections.sort(activeWords);
									WordBubble bub = activeWords.get(0);
									displayedWords.remove(bub.word.name);
									bub.setWord(w, 100, 100);
									displayedWords.put(bub.word.name, bub);
								}
							}
							c++;
							if (c >= maxWords) {
								break;
							}
						}
						for (WordBubble bub : activeWords) {
							bub.updateSize();
						}
						for (int i = 0; i < activeWords.size(); i++) {
							for (int j = i; j < activeWords.size(); j++) {
								WordBubble a = activeWords.get(i);
								WordBubble b = activeWords.get(j);
								if (checkCollision(a, b)) {
									collide(a, b);
								}
							}
						}
						return false;
					}
				}
			}
		};
		stage.addAction(mainLoop);

	}

	public void wordAdded(String s) {
		synchronized (wordCount) {
			Word w = wordCount.get(s);
			if (w == null) {
				wordCount.put(s, new Word(s, 1));
			} else {
				w.count++;
				wordCount.put(s, w);
			}
		}
		totalWordCount++;
	}

	public static boolean checkCollision(WordBubble a, WordBubble b) {
		if (a == b) {
			return false;
		}
		float x = a.getCenterX() - b.getCenterX();
		float y = a.getCenterY() - b.getCenterY();
		return Math.sqrt((x * x) + (y * y)) < a.getRadius() + b.getRadius();
	}

	public static void collide(WordBubble a, WordBubble b) {
		float collX = a.getCenterX() - b.getCenterX();
		float collY = a.getCenterY() - b.getCenterY();
		float distance = (float) Math.sqrt(collX * collX + collY * collY);
		float mtdX = collX * (((a.getRadius() + b.getRadius()) - distance) / distance);
		float mtdY = collY * (((a.getRadius() + b.getRadius()) - distance) / distance);

		a.setPosition(a.getX() + (mtdX * 0.5f), a.getY() + (mtdY * 0.5f));
		b.setPosition(b.getX() - (mtdX * 0.5f), b.getY() - (mtdY * 0.5f));

		float vX = a.getVelocityX() - b.getVelocityX();
		float vY = a.getVelocityY() - b.getVelocityY();
		float vn = new Vector2(vX, vY).dot(new Vector2(mtdX, mtdY).nor());
		if (vn > 0.0f)
			return;
		float i = 0.05f;// (-(1.0f - 0.94f) * vn) / (im1 + im2);
		float impulseX = mtdX * i;
		float impulseY = mtdY * i;

		a.setVelocityX(a.getVelocityX() + (impulseX));
		a.setVelocityY(a.getVelocityY() + (impulseY));

		b.setVelocityX(b.getVelocityX() - (impulseX));
		b.setVelocityY(b.getVelocityY() - (impulseY));
	}

	public class WordBubble extends Actor implements Comparable<WordBubble> {
		private Image bubble;
		private Label label;
		private float velX, velY;
		private boolean isActive = false;
		private Word word;

		public WordBubble() {
			bubble = new Image(AssetFactory.getTexture("circle"));
			label = new Label("", new Label.LabelStyle(AssetFactory.getFont("normal", 30), Color.WHITE));
			bubble.setColor(colors[colRan.nextInt(colors.length)]);
			label.setAlignment(Align.center);
		}

		public void setWord(Word w, float x, float y) {
			setPosition(x, y);
			label.setText(w.name);
			this.word = w;
		}

		public void updateSize() {
			float targetWidth = minScale + addScale * ((float) word.count / (float) totalWordCount);
			targetWidth = Math.min(targetWidth, 350);
			float fontScale = Math.min(2.0f, ((float) word.count / (float) totalWordCount) * 200);
			label.setFontScale(1 + fontScale);
			bubble.setSize(targetWidth, targetWidth);
		}

		@Override
		public void setPosition(float x, float y) {
			if(x == 0 && y == 0){
				x = 100;
				y = 100;
			}
			bubble.setPosition(x, y);
			label.setPosition(x + bubble.getWidth() / 2f, y + bubble.getWidth() / 2f);
			super.setPosition(x, y);
		}

		public void setActive(Word word, float x, float y, Stage target) {
			this.word = word;
			this.isActive = true;
			setPosition(x, y);
			bubble.setSize(minScale, minScale);
			label.setText(word.name);
			target.addActor(this);
		}

		@Override
		protected void setStage(Stage stage) {
			if(stage != null){
				stage.addActor(bubble);
				stage.addActor(label);
			}
			super.setStage(stage);
		}

		public void setVelocityX(float velX) {
			this.velX = velX;
		}

		public void setVelocityY(float velY) {
			this.velY = velY;
		}

		private float getCenterX() {
			return getX() + bubble.getWidth() / 2;
		}

		private float getCenterY() {
			return getY() + bubble.getWidth() / 2;
		}

		private float getRadius() {
			return bubble.getWidth() / 2 * 0.90f;
		}

		private float getVelocityX() {
			return velX;
		}

		private float getVelocityY() {
			return velY;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof WordBubble)) {
				return false;
			} else {
				return obj == this || ((WordBubble) obj).word.name.equals(word.name);
			}
		}

		@Override
		public void act(float delta) {
			if (isActive) {
				float targetX = centerX - getX();
				float targetY = centerY - getY();
				float length = (float) Math.sqrt(targetX * targetX + targetY * targetY);
				targetX /= length;
				targetY /= length;
				velX += targetX * delta * 3.5f;
				velY += targetY * delta * 10;
				velX *= Math.pow(0.8, delta);
				velY *= Math.pow(0.8, delta);
				setPosition(getX() + velX, getY() + velY);
				super.act(delta);
			}
		}

		@Override
		public int compareTo(WordBubble o) {
			if (this == o || o.word.count == word.count) {
				return 0;
			}
			if (o.word.count < word.count) {
				return 1;
			} else {
				return -1;
			}
		}
		
		@Override
		public String toString() {
			return word.toString();
		}
		
		@Override
		public boolean remove() {
			label.remove();
			bubble.remove();
			return super.remove();
		}
	}

	public static class Word implements Comparable<Word> {
		private String name;
		private int count = 1;

		public Word(String name, int count) {
			this.name = name;
			this.count = count;
		}
		
		@Override
		public boolean equals(Object obj) {
			return obj == this || name.equals(((Word)obj).name);
		}

		@Override
		public int compareTo(Word o) {
			if (this == o || o.count == count) {
				return 0;
			}
			if (count < o.count) {
				return 1;
			} else {
				return -1;
			}
		}

		@Override
		public String toString() {
			return "Word [name=" + name + ", count=" + count + "]";
		}

	}

}
