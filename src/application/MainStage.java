package application;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainStage extends Application {

	public static Visualization visual;
	public static Stage stage;
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		visual = new Visualization(1600,800);
		visual.paint();
		stage = primaryStage;
		//stage.setWidth(1600);
		//stage.setHeight(800);
		
		BorderPane root = new BorderPane();
		VisualizationMenu menuBar = new VisualizationMenu(visual.cohort.getMajors(), visual.cohort.getDepartment(), visual, stage);
		root.setTop(menuBar);
		 root.setCenter(visual);
	      primaryStage.setScene(new Scene(root));
		//Scene s = new Scene(root, 1600, 800);
		//stage.setScene(s);
		primaryStage.show();
	}


	public static void main(String[] args) {
		launch(args);
	}
	
}
