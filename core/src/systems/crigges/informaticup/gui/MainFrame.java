package systems.crigges.informaticup.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import systems.crigges.informaticup.crawling.FileType;
import systems.crigges.informaticup.crawling.RepositoryCrawler;
import systems.crigges.informaticup.crawling.ZipballGrabber;

public class MainFrame extends ApplicationAdapter implements CrawlerListener {
	private static final int viewportWidth = 1920;
	private static final int viewportHeight = 1080;

	private OrthographicCamera camera;
	private StretchViewport viewport;
	private Stage stage;
	private Label downloadLabel;
	private Image downloadImage;
	private Label downloadProgressLabel;
	private long maxDownloadProgress = 10000000;
	private long currentDownloadProgress = 1;
	private Queue<Label> bufferLabels = new Queue<>();
	private HashMap<FileType, Integer> typeCount = new HashMap<>();
	private ArrayList<Actor> toBeRemoved = new ArrayList<>();
	private int totalFiles = 1;
	private long lastFileLabel = System.currentTimeMillis();
	private Label calcWordCountLabel;
	private Label extensionSpreadingLabel;
	private Label networkInputLabel;
	private Label classificationLabel;

	@Override
	public void create() {
		camera = new OrthographicCamera();
		camera.zoom = 1f;
		camera.position.set(viewportWidth / 2, viewportHeight / 2, camera.position.z);
		viewport = new StretchViewport(viewportWidth, viewportHeight, camera);
		stage = new Stage(viewport);
		Gdx.input.setInputProcessor(stage);
		AssetFactory.loadAllRessources();
		createSideLabels();
		for (int i = 1; i <= 200; i++) {
			Label label = new Label("",
					new LabelStyle(AssetFactory.getFont("normal", (int) (25)), Color.WHITE));
			bufferLabels.addFirst(label);
			stage.addActor(label);
		}
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					new RepositoryCrawler("https://github.com/DataScienceSpecialization/courses", MainFrame.this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
		
//		WordCloud cloud = new WordCloud(stage);
//		for (int i = 0; i < 150; i++) {
//			for (int j = i; j < 150; j++) {
//				cloud.wordAdded(j + "");
//			}
//		}
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// try {
		// ZipballGrabber.grabVirtual("Crigges/Clickwars", MainFrame.this);
		//
		// //ZipballGrabber.grabVirtual("DataScienceSpecialization/courses",
		// MainFrame.this);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		// }).start();

	}

	private void createSideLabels() {
		LabelStyle stlye = new LabelStyle(AssetFactory.getFont("normal", 30), Color.WHITE);
		stlye.background = AssetFactory.getDefaultButtonStyle().up;
		downloadLabel = new Label("                                    Zipball Download", stlye);
		downloadLabel.setBounds(-200, 980, 520, 100);
		stage.addActor(downloadLabel);
		stlye = new LabelStyle(AssetFactory.getFont("normal", 30), Color.WHITE);
		stlye.background = AssetFactory.getDefaultButtonStyle().up;
		calcWordCountLabel = new Label("                                    Calc Word Count", stlye);
		calcWordCountLabel.setBounds(-200, 880, 520, 100);
		stage.addActor(calcWordCountLabel);
		stlye = new LabelStyle(AssetFactory.getFont("normal", 30), Color.WHITE);
		stlye.background = AssetFactory.getDefaultButtonStyle().up;
		extensionSpreadingLabel = new Label("                                   Extension Spreading", stlye);
		extensionSpreadingLabel.setBounds(-200, 780, 520, 100);
		stage.addActor(extensionSpreadingLabel);
		stlye = new LabelStyle(AssetFactory.getFont("normal", 30), Color.WHITE);
		stlye.background = AssetFactory.getDefaultButtonStyle().up;
		networkInputLabel = new Label("                                    Network Input", stlye);
		networkInputLabel.setBounds(-200, 680, 520, 100);
		stage.addActor(networkInputLabel);
		stlye = new LabelStyle(AssetFactory.getFont("normal", 30), Color.WHITE);
		stlye.background = AssetFactory.getDefaultButtonStyle().up;
		classificationLabel = new Label("                                    Classification", stlye);
		classificationLabel.setBounds(-200, 580, 520, 100);
		stage.addActor(classificationLabel);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
	}

	@Override
	public void dispose() {

	}

	@Override
	public void setMaxDownloadProgress(long max) {
		maxDownloadProgress = max + 1;
	}

	@Override
	public void setCurrentDownloadProgres(long current) {
		currentDownloadProgress = current + 1;
	}

	@Override
	public void extractedEntryFromZipBall(String name, FileType type) {
		Integer count = typeCount.get(type);
		if (count == null || count == 0) {
			typeCount.put(type, 1);
		} else {
			typeCount.put(type, count + 1);
		}
		totalFiles++;
		long curTime = System.currentTimeMillis();
		if (curTime - lastFileLabel < 25) {
			return;
		}
		lastFileLabel = curTime;
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				if (bufferLabels.size == 0) {
					return;
				}
				Label label = bufferLabels.removeLast();
				label.setText(name);
				label.setBounds(1000, 800, 200, 30);
				label.addAction(new Action() {

					float velX = (float) ((Math.random() - 0.5) * 1500);
					float velY = (float) Math.abs((Math.random() * 600));

					@Override
					public boolean act(float delta) {
						label.setPosition(label.getX() + velX * delta, label.getY() + velY * delta);
						velX *= 0.98f;
						velY -= 10;
						if (label.getY() <= -30) {
							label.removeAction(this);
							bufferLabels.addFirst(label);
						}
						return false;
					}
				});
				stage.addActor(label);
			}

		});
	}

	public class LabelSlideAction extends Action {
		private Label toMove;
		private boolean forward;

		public LabelSlideAction(Label toMove, boolean forward) {
			this.toMove = toMove;
			this.forward = forward;
		}

		@Override
		public boolean act(float delta) {
			double targetX;
			if (forward) {
				targetX = -100;
			} else {
				targetX = -200;
			}
			float diff = (float) (targetX - toMove.getX());
			if (Math.abs(diff) < 5) {
				toMove.removeAction(this);
			} else {
				toMove.setPosition(toMove.getX() + diff * delta * 1.5f, toMove.getY());
			}
			return false;
		}

	}

	@Override
	public void downloadStarted() {
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				downloadImage = new Image(AssetFactory.getTexture("quarder"));
				downloadImage.setBounds(400, 0, 1450, 50);
				downloadImage.setColor(Color.LIME);
				downloadImage.addAction(new Action() {

					@Override
					public boolean act(float delta) {
						double targetHeight = 200 * ((double) currentDownloadProgress / (double) maxDownloadProgress);
						float diff = (float) (targetHeight - (downloadImage.getHeight() - 50));
						downloadImage.setSize(1450, downloadImage.getHeight() + diff * delta * 1.5f);
						return false;
					}
				});
				stage.addActor(downloadImage);
				toBeRemoved.add(downloadImage);

				downloadProgressLabel = new Label("0%",
						new LabelStyle(AssetFactory.getFont("normal", 35), Color.WHITE));
				downloadProgressLabel.setBounds(1050, downloadImage.getHeight() - 50, 100, 50);
				downloadProgressLabel.addAction(new Action() {

					@Override
					public boolean act(float delta) {
						downloadProgressLabel.setBounds(1050, downloadImage.getHeight() - 50, 100, 50);
						downloadProgressLabel.setText(String.format("%.2f",
								100 * ((double) currentDownloadProgress / (double) maxDownloadProgress)) + "%");
						return false;
					}
				});
				stage.addActor(downloadProgressLabel);
				toBeRemoved.add(downloadProgressLabel);
				Color[] colors = new Color[FileType.values().length];
				colors[0] = Color.GOLD;
				colors[1] = Color.CORAL;
				colors[2] = Color.MAGENTA;
				colors[3] = Color.NAVY;
				colors[4] = Color.GRAY;
				colors[5] = Color.OLIVE;
				colors[6] = Color.TEAL;
				colors[7] = Color.FIREBRICK;
				int c = 0;
				ArrayList<Image> fileBars = new ArrayList<>();
				for (FileType t : FileType.values()) {
					Image image = new Image(AssetFactory.getTexture("quarder"));
					image.setBounds(400 + 182f * c, downloadImage.getHeight() + 5, 175, 100);
					image.setColor(colors[c]);
					int cref = c;
					image.addAction(new Action() {

						@Override
						public boolean act(float delta) {
							Integer count = typeCount.get(t);
							if (count == null) {
								count = 0;
							}
							double targetHeight = 300 * ((double) count / (double) totalFiles);
							float diff = (float) (targetHeight - (image.getHeight() - 100));
							image.setBounds(400 + 182f * cref, downloadImage.getHeight() + 5, 175,
									image.getHeight() + diff * delta * 1.5f);
							return false;
						}
					});

					stage.addActor(image);
					toBeRemoved.add(image);
					c++;

					Label barProgressLabel = new Label("0%",
							new LabelStyle(AssetFactory.getFont("normal", 35), Color.WHITE));
					barProgressLabel.setAlignment(Align.center);
					barProgressLabel.setBounds(image.getX() + 40, image.getY() + image.getHeight() - 50, 100, 50);
					barProgressLabel.addAction(new Action() {

						@Override
						public boolean act(float delta) {
							Integer count = typeCount.get(t);
							if (count == null) {
								count = 0;
							}
							barProgressLabel.setBounds(image.getX() + 40, image.getY() + image.getHeight() - 50, 100,
									50);
							barProgressLabel
									.setText(String.format("%.2f", 100 * ((double) count / (double) totalFiles)) + "%");
							return false;
						}
					});
					stage.addActor(barProgressLabel);
					toBeRemoved.add(barProgressLabel);

					Label barNameLabel = new Label(t.toString(),
							new LabelStyle(AssetFactory.getFont("normal", 35), Color.WHITE));
					barNameLabel.setAlignment(Align.center);
					barNameLabel.setBounds(image.getX() + 40, image.getY(), 100, 50);
					barNameLabel.addAction(new Action() {

						@Override
						public boolean act(float delta) {
							barNameLabel.setBounds(image.getX() + 40, image.getY(), 100, 50);
							return false;
						}
					});
					stage.addActor(barNameLabel);
					toBeRemoved.add(barNameLabel);
				}
			}
		});
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				downloadLabel.getStyle().background = AssetFactory.getDefaultButtonStyle().over;
				downloadLabel.addAction(new LabelSlideAction(downloadLabel, true));
			}
		});
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void downloadFinished() {
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				downloadLabel.getStyle().background = AssetFactory.getDefaultButtonStyle().down;
				downloadLabel.addAction(new LabelSlideAction(downloadLabel, false));
			}
		});
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				for (Actor a : toBeRemoved) {
					a.remove();
				}
			}
		});

	}
	
	WordCloud cloud;

	@Override
	public void wordCountStarted() {
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				calcWordCountLabel.getStyle().background = AssetFactory.getDefaultButtonStyle().over;
				calcWordCountLabel.addAction(new LabelSlideAction(calcWordCountLabel, true));
				cloud = new WordCloud(stage);
			}
		});
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void wordCountFinished() {
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				calcWordCountLabel.getStyle().background = AssetFactory.getDefaultButtonStyle().down;
				calcWordCountLabel.addAction(new LabelSlideAction(calcWordCountLabel, false));
			}
		});
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cloud.clear();
	}

	@Override
	public void wordAdded(String w) {
		cloud.wordAdded(w);
	}
}
