package shell;

import java.io.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GUI extends Application {
	private static String textToShow;
	private static TextArea top = new TextArea();
	private static TextField bottom = new TextField();
	private static Button close;
	private static Button minimize;
	private static Button maximize;
	private PipedInputStream inp = new PipedInputStream();
	private PipedOutputStream out = new PipedOutputStream();
	private StringBuilder outStringBuilder = new StringBuilder();
	private OutputStream outStream;
	private int len = 0;

	public static void main(String[] args) throws IOException {
		Shell.boot();
		launch(args);
	}

	public static void clearTerminal() {
		top.setText("");
		bottom.clear();
	}

	/*private void addTextToTop() {
		if (outStringBuilder.length() > 0) {
			top.appendText(outStringBuilder.toString());
			outStringBuilder = new StringBuilder();
		}
	}*/
	
	private void addTextToTop() {
		if (outStringBuilder.length() > 0) {
			String output = outStringBuilder.toString();
			outStringBuilder = new StringBuilder();
			javafx.application.Platform.runLater(() -> {
				top.appendText(output);
			});
		}
	}


	@Override
	public void start(Stage primaryStage) throws Exception {

		inp.connect(out);
		textToShow = "";

		close = new Button("X");
		close.setPrefSize(5, 5);
		minimize = new Button("_");
		minimize.setPrefSize(5, 5);
		maximize = new Button("❐");

		HBox buttons = new HBox(5);
		buttons.setAlignment(Pos.TOP_RIGHT);

		buttons.getChildren().addAll(minimize, maximize, close);

		top = new TextArea();
		top.setMinSize(900, 450);
		top.setEditable(false);
		top.setText("Welcome to the OS emulator!\n");

		bottom = new TextField();
		bottom.setMinSize(900, 62);

		close.setOnAction(e -> {
			System.exit(0);
		});

		minimize.setOnAction(e -> {
			Stage stage = (Stage) minimize.getScene().getWindow();
			stage.setIconified(true);
			bottom.requestFocus();
		});

		maximize.setOnAction(e -> {
			Stage stage = (Stage) maximize.getScene().getWindow();
			if (!stage.isMaximized())
				stage.setMaximized(true);
			else
				stage.setMaximized(false);
			bottom.requestFocus();
		});

		bottom.setOnAction(e -> {
			try {
				byte array[] = bottom.getText().getBytes();
				out.write(array);
				len = array.length;

				Commands.readACommand(inp, len);

				textToShow += ">" + bottom.getText() + "\n";
				top.appendText(textToShow);
				Commands.returnCommand();

				bottom.clear();
				textToShow = "";

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		bottom.setOnKeyPressed((e) -> {
			if (e.getCode().equals(KeyCode.UP)) {
				String last = Commands.previous();
				if (!last.equals("")) {
					bottom.setText(last);
					bottom.positionCaret(last.length());
				}
				e.consume();
			} else if (e.getCode().equals(KeyCode.DOWN)) {
				String next = Commands.next();
				if (!next.equals("")) {
					bottom.setText(next);
					bottom.positionCaret(next.length());
				}
				e.consume();
			}
		});

		outStream = new OutputStream() {
			public void write(int b) throws IOException {
				outStringBuilder.append((char) b);
				if (((char) b) == '\n')
					addTextToTop();
			}
		};

		Commands.setOut(outStream);

		VBox root = new VBox(15);
		root.setPadding(new Insets(10, 30, 30, 30));
		root.getChildren().setAll(buttons, top, bottom);
		VBox.setVgrow(top, Priority.ALWAYS);
		Scene scene = new Scene(root, 1200, 700);
		scene.getStylesheets().add("application.css");

		primaryStage.setScene(scene);
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.show();

		bottom.requestFocus();
	}
}