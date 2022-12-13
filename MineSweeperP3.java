/* Brandon Pfefferle
 * 300186786
 * Lab 8 - MS Part 3 COSC 121
 * The bonuses I got working was showing a MisFlag image when something is flagged 
 * improperly and Face-O image on mouse press. I was going to attempt timer and I did a little bit, but didn't 
 * have enough time to get it going. :(
 */
import java.util.ArrayList;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MineSweeperP3 extends Application {
	//Creating Variables
	protected int rowSize;
	protected int colSize;
	protected int numOfMines;
	protected smileButton sButton;
	//protected ChoiceBox<String> choiceBox = new ChoiceBox<>();
	protected String difficulty; 
	//protected String difficulty = "Easy"; //Easy, Medium, Hard difficulties all work, change the string in this line to change difficulty
	//int[][] numberBoard = generateMines(difficulty);
	protected int[][] numberBoard;
	//protected gameButtons[][] gameBoard = new gameButtons[rowSize][colSize];
	protected gameButtons[][] gameBoard;
	GridPane gp = new GridPane();
	BorderPane bp = new BorderPane();
	protected int timeElapsed = 0;
	protected HBox header;
	protected int count;
	protected boolean disableGrid;
	protected Timeline timer;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		sButton = new smileButton();
		ChoiceBox<String> choiceBox = new ChoiceBox<>();
		choiceBox.getItems().addAll("Easy", "Medium", "Hard");
		choiceBox.setValue("Easy");
//		choiceBox.setOnAction(e -> {
//			difficulty = choiceBox.getValue();
//			generateMines(difficulty);
//			gameBoard = new gameButtons[rowSize][colSize];
//		});
		//String x = choiceBox.getValue();
		
		//choiceBox.get(0)
		difficulty = "Easy";
		generateMines(difficulty);
		gameBoard = new gameButtons[rowSize][colSize];
		
		//Creating a game
		playGame(numberBoard);
		
		//Adding header using CustomPane method
		header = new HBox();
		header.getChildren().add(new CustomPane(numOfMines));
		header.getChildren().add(sButton);
		header.getChildren().add(new CustomPane(timeElapsed));
		header.setAlignment(Pos.CENTER);
		
		//Creating a new game by clicking on the smile button at any point
		//Regenerating mines and updating header to display correct amount of mines, and just printing board to see regeneration
		sButton.setOnMouseClicked(e -> {
			sButton.setGraphic(sButton.imageFaceSmile);
			this.numberBoard = generateMines(difficulty);
			playGame(numberBoard);
			header.getChildren().set(0, new CustomPane(numOfMines));
		});
		
		//Adding my Grid and HBox to borderpane, and setting scene
		gp.setAlignment(Pos.CENTER);
		bp.setTop(choiceBox);
		bp.setBottom(gp);
		bp.setCenter(header);
		Scene scene = new Scene(bp);
		primaryStage.setScene(scene);
		primaryStage.setTitle("MineSweeper");
		primaryStage.show();
	}
	
	//Play Game method that returns a gridPane of covered Mines
	public GridPane playGame(int[][] numberBoard) {
		this.disableGrid = false;
		for(int row = 0; row < rowSize; row++) {
			for (int col = 0; col < colSize; col++) {
				this.count = 0;
				gameButtons button = new gameButtons(numberBoard[row][col]);
				gameBoard[row][col] = button;
				
				int node = numberBoard[row][col];
				int r = row;
				int c = col;
				
				//setting GameButtons isBomb to true once they're generated
				if (node == 9) {
					button.isBomb = true;
				}else {
					button.isBomb = false;
				}
				
				System.out.print(numberBoard[row][col] + " ");
				
				button.setOnMousePressed(p -> {
					if (!disableGrid) {
						sButton.setGraphic(sButton.imageFaceO);
					}
				});
				button.setOnMouseReleased(m -> {
					if (!disableGrid) {
						sButton.setGraphic(sButton.imageFaceSmile);
					}
				});
				button.setOnMouseClicked(e -> {
					MouseButton mouse = e.getButton();
					System.out.println(mouse);
					
					//Setting graphic to the number behind cover
					if (!disableGrid) {
						if (mouse == MouseButton.PRIMARY && !button.isFlagged && button.covered) {
							System.out.println("in top");
							firstClick(gameBoard, r, c, count);
							//this.count++;
							openBoard(r, c);
							button.setGraphic(button.imageButton);
							button.setCovered(false);
							
							//printBombValue(gameBoard);
							
							//Ending the game if a bomb is clicked, displaying the other mines and disabling the board
							if (button.getNum() == 9 && count != 0) {
								button.setGraphic(button.imageBombHit);
								button.setBomb(true);
								button.setCovered(false);
								displayMines(gameBoard);
								sButton.setGraphic(sButton.imageFaceDead);
								this.disableGrid = true;
							}
							//Opening 8 spots around a tile if the correct amount of flags are touching any given number
						}else if (mouse == MouseButton.PRIMARY && button.getNum() != 9 && button.getNum() != 0 && !button.isFlagged){
							if (getFlagCount(r, c) == button.getNum()) {
								openOnNum(r, c);
								openBoard(r, c);
								gameBoard[r][c].setGraphic(gameBoard[r][c].imageButton);
								gameBoard[r][c].setCovered(false);
							}
						}
						//Checking if all the tiles have been uncovered except for bombs
						if (gameWin(gameBoard)) {
							sButton.setGraphic(sButton.imageFaceWin);
						}
						//this.count++;
						//Handling right clicks and flag placing and updating label.
						if (mouse == MouseButton.SECONDARY && button.covered) {
							if (!button.isFlagged) {
								button.setGraphic(button.imageFlag);
								//button.setCovered(false);
								button.isFlagged = true;
								numOfMines--;
								header.getChildren().set(0, new CustomPane(numOfMines));
							}else if(button.isFlagged) {
								button.setGraphic(button.imageCover);
								button.isFlagged = false;
								button.setCovered(true);
								numOfMines++;
								header.getChildren().set(0, new CustomPane(numOfMines));
							}
						}else if (mouse == MouseButton.SECONDARY && button.isFlagged) {
							button.setGraphic(button.imageCover);
							button.isFlagged = false;
							//button.setCovered(true);
							numOfMines++;
							header.getChildren().set(0, new CustomPane(numOfMines));
						}
					}
					this.count++;
				});
				gp.add(button, col, row);
			}
			System.out.println();
		}
		System.out.println();
		return gp;
	}
	//Randomly Generate mines, returning int[][] array depending on difficulty setting
	//Update board size and mine count also
	public int[][] generateMines(String difficulty) {
		if (difficulty.equalsIgnoreCase("Easy")) {
			numberBoard = new int[8][8];
			rowSize = 8;
			colSize = 8;
			numOfMines = 10;
		}else if(difficulty.equalsIgnoreCase("Medium")) {
			numberBoard = new int[16][16];
			rowSize = 16;
			colSize = 16;
			numOfMines = 40;
		}else if (difficulty.equalsIgnoreCase("Hard")) {
			numberBoard = new int[16][32];
			rowSize = 16;
			colSize = 32;
			numOfMines = 99;
		}else {
			numberBoard = new int[8][8];
			rowSize = 8;
			colSize = 8;
			numOfMines = 10;
		}
		numberBoard = new int[rowSize][colSize];
		int row;
		int col;
		for(int i = 0; i < numOfMines; i++) {
			do {
				row = (int) (Math.random() * rowSize);
				col = (int) (Math.random() * colSize);
			}while(numberBoard[row][col] == 9);
			numberBoard[row][col] = 9;
		}
		
		//increment numbers around the mines to display proper numbers
		for (int r = 0; r < rowSize; r++) {
			for (int c = 0; c < colSize; c++) {
				if (numberBoard[r][c] == 9) {
					//right
					
					if (isValid(r, c+1) && numberBoard[r][c+1] != 9) {
						numberBoard[r][c+1]++;
					}
					//down right
					if (isValid(r+1, c+1) && numberBoard[r+1][c+1] != 9) {
						numberBoard[r+1][c+1]++;
					}
					//down
					if (isValid(r+1, c) && numberBoard[r+1][c] != 9) {
						numberBoard[r+1][c]++;
					}
					//down left
					if (isValid(r+1, c-1) && numberBoard[r+1][c-1] != 9) {
						numberBoard[r+1][c-1]++;
					}
					//left
					if (isValid(r, c-1) && numberBoard[r][c-1] != 9) {
						numberBoard[r][c-1]++;
					}
					//up left
					if (isValid(r-1, c-1) && numberBoard[r-1][c-1] != 9) {
						numberBoard[r-1][c-1]++;
					}
					//up
					if (isValid(r-1, c) && numberBoard[r-1][c] != 9) {
						numberBoard[r-1][c]++;
					}
					//up right
					if (isValid(r-1, c+1) && numberBoard[r-1][c+1] != 9) {
						numberBoard[r-1][c+1]++;
					}
				}
			}
		}
		return numberBoard;
	}
	//Win game method to see if they have uncovered everything that is not a bomb
	private boolean gameWin(gameButtons[][] arr) {
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr.length; j++) {
				if (arr[i][j].covered && !arr[i][j].isBomb) {
					return false;
				}
			}
		}
		this.disableGrid = true;
		return true;
	}
	public void getChoice(ChoiceBox<String> c) {
		this.difficulty = c.getValue();
	}
	//This was just to debug, checking the covered properties of each tile to help fix my recursion
	public void printBombValue(gameButtons[][] buttons) {
		for (int i = 0; i < rowSize; i++) {
			for (int j = 0; j < colSize; j++) {
				//on covered values currently
				if (gameBoard[i][j].covered) {
					System.out.print("T ");
				}else {
					System.out.print("F ");
				}
			}
			System.out.println();
		}
	}
	//Main recursive method, being invalid as my main base case, or if you hit a number
	public void openBoard(int row, int col) {
		if (!isValid(row, col)) {
			return;
		}
		if (gameBoard[row][col].getNum() == 0 && gameBoard[row][col].covered && !gameBoard[row][col].isFlagged) {
			  gameBoard[row][col].unCover(gameBoard[row][col]);
	          openBoard(row, col+1); //right
	          openBoard(row+1, col+1); //down right
	          openBoard(row+1, col); //down
	          openBoard(row+1, col-1); //down left
	          openBoard(row, col-1); //left
	          openBoard(row-1, col-1); //up left
	          openBoard(row-1, col); //up
	          openBoard(row-1, col+1); //up right
	          //System.out.println("inside first if");
		}else if (gameBoard[row][col].getNum() != 0 && !gameBoard[row][col].isFlagged){
            gameBoard[row][col].unCover(gameBoard[row][col]);
		}else {
			return;
		}
	}
	//Method to open the surrounding tiles if the tile is surrounded by the appropriate amount of flags.
	public void openOnNum(int row, int col) {
		for (int r = row - 1; r <= row +1; r++) {
			for (int c = col - 1; c <= col+1; c++) {
				if (isValid(r, c)) {
					if (gameBoard[r][c].getNum() == 0 && gameBoard[r][c].covered) {
						openBoard(r, c);
					}else if(!gameBoard[r][c].isFlagged && gameBoard[r][c].covered && gameBoard[r][c].getNum() == 9) {
						gameBoard[r][c].setGraphic(gameBoard[r][c].imageBombHit);
						gameBoard[r][c].setCovered(false);
						this.disableGrid = true;
						sButton.setGraphic(sButton.imageFaceDead);
						gameBoard[r][c].setBomb(true);
						displayMines(gameBoard);
					}else if (!gameBoard[r][c].isFlagged && gameBoard[r][c].getNum() != 9) {
						if (gameBoard[r][c].covered) {
							gameBoard[r][c].unCover(gameBoard[r][c]);
						}
					}
				}
			}
		}
	}
	//Get the appropriate count of flags surrounding a specific uncovered tile, to then open the tiles around if the flag count matches the number
	public int getFlagCount(int row, int col) {
		int flagCount = 0;
		//use this for openBoard also, can have less code
		for (int r = row - 1; r <= row +1; r++) {
			for (int c = col - 1; c <= col+1; c++) {
				if (isValid(r, c)) {
					if (gameBoard[r][c].isFlagged) {
						flagCount++;
					}
				}
			}
		}
		return flagCount;
	}
	
	//Method to handle and make sure that the first click is always a zero, regenerating a new mine board otherwise.
	//Probably better to use a boolean rather than a count
	public void firstClick(gameButtons[][] arr, int row, int col, int count) {
		if (count < 1) {
			if (arr[row][col].getNum() != 0 || arr[row][col].getNum() == 9) {
				this.numberBoard = generateMines(difficulty);
				playGame(numberBoard);
				firstClick(arr, row, col, count);
			}
			arr[row][col].setGraphic(arr[row][col].imageButton);
		}
	}
	//Making sure to not go out of bounds
	private boolean isValid(int row, int col) {
		return row >= 0 && row < rowSize && col >= 0 && col < colSize;
	}
	//Upon a loss, display mines that are covered and NOT flagged, unless flagged incorrectly
	public void displayMines(gameButtons[][] arr) {
		for (int i = 0; i < rowSize; i++) {
			for (int j = 0; j < colSize; j++) {
				if (arr[i][j].covered && arr[i][j].isBomb && !arr[i][j].isFlagged) {
					arr[i][j].setGraphic(arr[i][j].imageBombGrey);
					arr[i][j].setCovered(false);
				}else if (arr[i][j].isFlagged && !arr[i][j].isBomb) {
					//Setting incorrectly flagged tiles to the MisFlag image upon clicking an actual mine
					arr[i][j].setGraphic(arr[i][j].imageMisFlag);
					arr[i][j].setCovered(false);
				}
			}
		}
	}
}
//Custom Label pane for bomb and time Labels
class CustomPane extends StackPane {
	public CustomPane(int word) {
		getChildren().add(new Label(String.format("%03d", word)));
		setPadding(new Insets(10, 45, 10, 45));
		setStyle("-fx-border-color: lightgrey");
	}
}

//Smile Button class for Hbox
class smileButton extends Button{
	ImageView imageFaceSmile, imageFaceWin, imageFaceDead, imageFaceO;

	public smileButton() {
		double size = 50;

		setMinWidth(size);
		setMaxWidth(size);
		setMinHeight(size);
		setMaxHeight(size);
		setStyle("-fx-border-color: lightgrey");

		imageFaceDead = new ImageView(new Image("file:res/face-dead.png"));
		imageFaceSmile = new ImageView(new Image("file:res/face-smile.png"));
		imageFaceWin = new ImageView(new Image("file:res/face-win.png"));
		imageFaceO = new ImageView(new Image("file:res/face-O.png"));
		
		imageFaceDead.setFitHeight(size);
		imageFaceDead.setFitWidth(size);

		imageFaceSmile.setFitHeight(size);
		imageFaceSmile.setFitWidth(size);

		imageFaceWin.setFitHeight(size);
		imageFaceWin.setFitWidth(size);
		
		imageFaceO.setFitHeight(size);
		imageFaceO.setFitWidth(size);
		setGraphic(imageFaceSmile);
	}
}
//Main gameButtons class, default image is the cover
class gameButtons extends Button{
	ImageView imageCover, imageBombHit, imageFlag, imageMisFlag, imageBombGrey, imageButton;
	
	ArrayList<ImageView> images = new ArrayList<>();

	boolean isBomb;
	boolean covered;
	boolean isFlagged;
	int num;

	public gameButtons(int num) {
		double size = 35;

		this.num = num;
		covered = true;
		isBomb = false;
		isFlagged = false;

		setMinWidth(size);
		setMaxWidth(size);
		setMinHeight(size);
		setMaxHeight(size);

		imageCover = new ImageView(new Image("file:res/cover.png"));
		imageButton = new ImageView(new Image("file:res/" + num + ".png"));
		imageBombHit = new ImageView(new Image("file:res/mine-red.png"));
		imageBombGrey = new ImageView(new Image("file:res/mine-grey.png"));
		imageMisFlag = new ImageView(new Image("file:res/mine-misflagged.png"));
		imageFlag = new ImageView(new Image("file:res/flag.png"));

		images.add(imageCover);
		images.add(imageButton);
		images.add(imageFlag);
		images.add(imageMisFlag);
		images.add(imageBombGrey);
		images.add(imageBombHit);

		for (ImageView x: images) {
			x.setFitHeight(size);
			x.setFitWidth(size);
		}
		setGraphic(imageCover);
	}
	
	public boolean isBomb() {
		return isBomb;
	}

	public void setBomb(boolean isBomb) {
		this.isBomb = isBomb;
	}

	public boolean isCovered() {
		return covered;
	}

	public void setCovered(boolean covered) {
		this.covered = covered;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
	public void unCover(gameButtons button) {
		button.setCovered(false);
		button.setGraphic(imageButton);
	}
}

