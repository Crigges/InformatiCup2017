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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import systems.crigges.informaticup.crawling.FileType;
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
	private int totalFiles = 1;

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
		for(int i = 1; i <= 200; i++){
			Label label = new Label("",
					new LabelStyle(AssetFactory.getFont("normal", (int) (15 + Math.random() * 10)), Color.WHITE));
			bufferLabels.addFirst(label);
			stage.addActor(label);
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					ZipballGrabber.grabVirtual("DataScienceSpecialization/courses", MainFrame.this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
	}

	private void createSideLabels() {
		LabelStyle stlye = new LabelStyle(AssetFactory.getFont("normal", 30), Color.WHITE);
		stlye.background = AssetFactory.getDefaultButtonStyle().up;
		downloadLabel = new Label("                                    Zipball Download", stlye);
		downloadLabel.setBounds(-200, 980, 500, 100);
		stage.addActor(downloadLabel);
		// label.setBackground(AssetFactory.getDefaultButtonStyle().down);
		// stage.addActor(textButton);
		// textButton = new TextButton(" Calc Word Count",
		// AssetFactory.getDefaultButtonStyle());
		// textButton.setBounds(-200, 880, 500, 100);
		// stage.addActor(textButton);
		// textButton = new TextButton(" Zipball Download",
		// AssetFactory.getDefaultButtonStyle());
		// textButton.setBounds(-200, 780, 500, 100);
		// stage.addActor(textButton);
		// textButton = new TextButton(" Zipball Download",
		// AssetFactory.getDefaultButtonStyle());
		// textButton.setBounds(-200, 680, 500, 100);
		// stage.addActor(textButton);
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
		if(count == null || count == 0){
			typeCount.put(type, 1);
		}else{
			typeCount.put(type, count + 1);
		}
		totalFiles++;
		if(Math.random() < 0.5){
			return;
		}
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				if(bufferLabels.size == 0){
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
						if(label.getY() <= -30){
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

	@Override
	public void downloadStarted() {
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				downloadLabel.getStyle().background = AssetFactory.getDefaultButtonStyle().over;
				downloadImage = new Image(AssetFactory.getTexture("quarder"));
				downloadImage.setBounds(400, 0, 1450, 50);
				// downloadImage.setColor(new Color(41f / 255f, 145f / 255f,
				// 184f / 255f, 1));
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
				for(FileType t : FileType.values()){
					Image image = new Image(AssetFactory.getTexture("quarder"));
					image.setBounds(400 + 182f * c, downloadImage.getHeight() + 5, 175, 100);
					image.setColor(colors[c]);
					int cref = c;
					image.addAction(new Action() {
						
						@Override
						public boolean act(float delta) {
							Integer count = typeCount.get(t);
							if(count == null){
								count = 0;
							}
							double targetHeight = 300 * ((double) count / (double) totalFiles);
							float diff = (float) (targetHeight - (image.getHeight() - 100));
							image.setBounds(400 + 182f * cref, downloadImage.getHeight() + 5, 175, image.getHeight() + diff * delta * 1.5f);
							return false;
						}
					});
					
					stage.addActor(image);
					c++;

					Label barProgressLabel = new Label("0%", new LabelStyle(AssetFactory.getFont("normal", 35), Color.WHITE));
					barProgressLabel.setAlignment(Align.center);
					barProgressLabel.setBounds(image.getX() + 40, image.getY() + image.getHeight() - 50, 100, 50);
					barProgressLabel.addAction(new Action() {
						
						@Override
						public boolean act(float delta) {
							Integer count = typeCount.get(t);
							if(count == null){
								count = 0;
							}
							barProgressLabel.setBounds(image.getX() + 40, image.getY() + image.getHeight() - 50, 100, 50);
							barProgressLabel.setText(String.format("%.2f", 100 * ((double) count / (double) totalFiles)) + "%");
							return false;
						}
					});
					stage.addActor(barProgressLabel);
					
					Label barNameLabel = new Label(t.toString(), new LabelStyle(AssetFactory.getFont("normal", 35), Color.WHITE));
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
				}
			}
		});
	}

	@Override
	public void downloadFinished() {
		// TODO Auto-generated method stub

	}
}
