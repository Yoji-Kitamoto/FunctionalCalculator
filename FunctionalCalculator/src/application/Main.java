package application;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
	// TODO: BigDecimal 型に変更
	private static double result = 0.0;
	private static double answer = 0.0;

	public static void calculate(ActionEvent actionEvent, TextField textField) {
		String formula = textField.getText();
		System.out.println(formula);

		if(formula.matches(".*Ans.*")) {
			formula = formula.replaceAll("Ans", String.valueOf(answer));
			System.out.println(formula);
		}

		// TODO フォーマット確認
		if(formula.matches(".*[A-Za-z].*")) {
			//return;
		}

		var nums = new ArrayList<Double>(Stream.of(formula.split("\\+|-|\\*|\\/")).map(Double::parseDouble).collect(Collectors.toList()));
		System.out.println(nums);
		var operands = new ArrayList<String>(Arrays.asList(formula.split("\\d+(\\.\\d+)?")));
		if(operands.size() > 0) {
			operands.remove(0);
		}
		System.out.println(operands);

		// () 内の式を先に計算

		// 乗算, 除算を先に計算
		for(int i = 0; i < operands.size(); i++) {
			if(operands.get(i).equals("*")) {
				nums.add(i, nums.get(i) * nums.get(i + 1));
				nums.remove(i + 1);
				nums.remove(i + 1);
				operands.remove(i);
			} else if (operands.get(i).equals("/")) {
				nums.add(i, nums.get(i) / nums.get(i + 1));
				nums.remove(i + 1);
				nums.remove(i + 1);
				operands.remove(i);
			}
		}
		System.out.println(nums);
		System.out.println(operands);

		if(operands.size() == 0) {
			result = nums.get(0);
			if(result == (long)result) {
				textField.setText(String.format("%d", (long)result));
			} else {
				textField.setText(String.format("%g", result));
			}
			answer = result;
			return;
		}

		result = nums.get(0);
		for(int i = 0; i < operands.size(); i++) {
			switch(operands.get(i)) {
				case "+":
					result += nums.get(i + 1);
					break;

				case "-":
					result -= nums.get(i + 1);
					break;

				case "*":
					result *= nums.get(i + 1);
					break;

				case "/":
					result /= nums.get(i + 1);
					break;

				default:
					result += 0;
					break;
			}
		}

		// 計算結果を文字列変換して textField に書き込む
		if(result == (long)result) {
			textField.setText(String.format("%d", (long)result));
		} else {
			textField.setText(String.format("%g", result));
		}
		answer = result;
	}

	public static void input(ActionEvent actionEvent, TextField textField) {
		var nowPressedButton = (Button)actionEvent.getSource();
		String key = nowPressedButton.getText();

		// AC ボタンの処理
		switch(key) {
		case "AC":
			result = 0;
			textField.setText("0");
			return;
		}

		String textFieldContent = textField.getText();

		switch(key) {
			case "DEL":
				if(textFieldContent.matches(".")) {
					textField.setText("0");
				} else {
					textField.setText(textFieldContent.substring(0, textFieldContent.length() - 1));
				}
				break;

			case "×":
				if(!textFieldContent.matches("0")) {
					textField.setText(textFieldContent + "*");
				} else {
					textField.setText("*");
				}
				break;

			case "÷":
				if(!textFieldContent.matches("0")) {
					textField.setText(textFieldContent + "/");
				} else {
					textField.setText("/");
				}
				break;

			case ".": // 小数点
				if(!textFieldContent.matches("0")) {
					textField.setText(textFieldContent + ".");
				} else {
					textField.setText("0.");
				}
				break;

			case "00":
				if(!textFieldContent.matches("0")) {
					textField.setText(textFieldContent + "00");
				} else {
					textField.setText("0");
				}
				break;

			default: // 数字ボタン (0-9), Ans
				if(!textFieldContent.matches("0")) {
					textField.setText(textFieldContent + key);
				} else {
					textField.setText(key);
				}
				break;
		}
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			var titleLabel = new Label("Calculator");

			var textField = new TextField("0");
			textField.setEditable(false);   // 編集不可に設定
			textField.setPrefSize(200, 50); // 高さ

			/* 未実装機能
			 * CONTRAST
			 *
			 * 括弧 (スタック使用)
			 *
			 * S A M STO RCL STAT CMPLX MAT VCT D R G FIX SCI Math v ^ Disp
			 */

			// ボタンの作成
			String[] upperButtonLabel = {"SHIFT", "ALPHA", "←",  "↑",  "MODE", "ON",
										 "CALC",  "∫",    "↓",  "→",  "1/x",  "log_a b",
										 "x/y",   "√",    "x^2", "x^n", "log",  "ln",
										 "(-)",   "時",    "hyp", "sin", "cos",  "tan",
										 "RCL",   "ENG",   "(",   ")",   "S⇔D", "M+"};

			String[] lowerButtonLabel = {"7", "8",  "9", "DEL", "AC",
										 "4", "5",  "6", "×",  "÷",
										 "1", "2",  "3", "+",   "-",
										 "0", "00", ".", "Ans", "="};

			// SHIFT (YELLOW)
			String[] upperButtonShiftLabel = {"SHIFT", "ALPHA", "←",  "↑",   "SETUP",  "",
										 	  "SOLVE", "d/dx",  "↓",  "→",   "x!",     "Σ",
										 	  "a x/y", "3√",   "x^3", "n√x", "10^n",   "e^n",
										 	  "C:∠",  "←",    "Abs", "asin", "acos",   "atan",
										 	  "STO",   "←",    "%",   ",",    "帯⇔仮", "M-"};

			String[] lowerButtonShiftLabel = {"",      "",      "CLR",   "INS",  "OFF",
											  "",      "",      "",      "nPr",  "nCr",
											  "W:STT", "C:CPX", "G:BSE", "Pol",  "Rec",
											  "Rnd",   "Ran#",  "π",    "DRG▶", ""};

			// ALPHA (RED)
			String[] upperButtonAlphaLabel = {"SHIFT", "ALPHA", "←",  "↑",  "",  "",
											  "=",     ":",     "↓",  "→",  "",  "",
											  "",      "",      "",    "",    "",  "",
											  "A",     "B",     "C",   "D",   "E", "F",
											  "",      "",      "",    "X",   "Y", "M"};

			String[] lowerButtonAlphaLabel = {"", "▶Conv",  "",  "", "",
											  "", "",       "",  "", "",
											  "", "",       "",  "", "",
											  "", "RanInt", "e", "", ""};

			// CMPLX (BLUE)
			String[] upperButtonCmplxLabel = {"SHIFT", "ALPHA", "←", "↑", "", "",
										 	  "",      "",      "↓", "→", "", "",
										 	  "",      "",      "",   "",   "", "",
										 	  "",      "",      "",   "",   "", "",
										 	  "",      "i",     "",   "",   "", ""};

			String[] lowerButtonCmplxLabel = {"", "", "", "", "",
											  "", "", "", "", "",
											  "", "", "", "", "",
											  "", "", "", "", ""};

			// BASE-N (GREEN)
			String[] upperButtonBaseNLabel = {"SHIFT", "ALPHA", "←",  "↑",  "MODE", "ON",
										 	  "",      "",      "↓",  "→",  "",     "",
										 	  "",      "",      "DEC", "HEX", "BIN",  "OCT",
										 	  "",      "",      "",    "",    "",     "",
										 	  "",      "",      "",    "",    "",     ""};

			String[] lowerButtonBaseNLabel = {"", "", "", "", "",
										 	  "", "", "", "", "",
										 	  "", "", "", "", "",
										 	  "", "", "", "", ""};

			var upperButton = new Button[30];
			for(int i = 0; i < upperButton.length; i++) {
				upperButton[i] = new Button(upperButtonLabel[i]);
				upperButton[i].setMinSize(65, 30);    // 最小サイズ

				/*
				{"SHIFT", "ALPHA", "←",  "↑",  "MODE", "ON",
					 "CALC",  "∫",    "↓",  "→",  "1/x",  "log_a b",
					 "x/y",   "√",    "x^2", "x^n", "log",  "ln",
					 "(-)",   "時",    "hyp", "sin", "cos",  "tan",
					 "RCL",   "ENG",   "(",   ")",   "S⇔D", "M+"}
				*/

				// 機能未設定のキーを灰色に設定
				switch(upperButtonLabel[i]) {
					case "SHIFT":
					case "ALPHA":
					case "←":
					case "↑":
					case "MODE":
					case "ON":
					case "CALC":
					case "∫":
					case "↓":
					case "→":
					case "1/x":
					case "log_a b":
					case "x/y":
					case "√":
					case "x^2":
					case "x^n":
					case "log":
					case "ln":
					case "(-)":
					case "時":
					case "hyp":
					case "sin":
					case "cos":
					case "tan":
					case "RCL":
					case "ENG":
					case "(":
					case ")":
					case "S⇔D":
					case "M+":
						// upperButton[i].getStyleClass().add("no-func-button");
						upperButton[i].setDisable(true);
						break;

					default:
						break;
				}
			}

			var lowerButton = new Button[20];
			for(int i = 0; i < lowerButton.length; i++) {
				lowerButton[i] = new Button(lowerButtonLabel[i]);
				lowerButton[i].setMinSize(78, 40);    // 最小サイズ

				if(lowerButtonLabel[i].equals("=")) {
					// 計算処理
					lowerButton[i].setOnAction((actionEvent) -> {
						calculate(actionEvent, textField);
					});
				} else {
					// textField に文字列を追加
					lowerButton[i].setOnAction((actionEvent) -> {
						input(actionEvent, textField);
					});
				}
			}

			var upperGridPane = new GridPane();
			for(int i = 0; i < 5; i++) {
				for(int j = 0; j < 6; j++) {
					upperGridPane.add(upperButton[6 * i + j], j, i);
				}
			}

			var lowerGridPane = new GridPane();
			for(int i = 0; i < 4; i++) {
				for(int j = 0; j < 5; j++) {
					lowerGridPane.add(lowerButton[5 * i + j], j, i);
				}
			}

			var labelHBox = new HBox(20, titleLabel);
			labelHBox.setAlignment(Pos.CENTER);

			var vBox = new VBox(6, labelHBox, textField, upperGridPane, lowerGridPane);
			vBox.setPadding(new Insets(6));

			var cssFile = new File("src/application/application.css");

			var scene = new Scene(vBox);
			// var scene = new Scene(vBox, 400, 400);
			scene.getStylesheets().add(cssFile.toURI().toString());

			primaryStage.setScene(scene);
			primaryStage.setTitle("Calculator");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
