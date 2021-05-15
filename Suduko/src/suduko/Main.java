
package suduko;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {
	private static final int SIZE = 9;

	private static Scanner sc = new Scanner(System.in);
	private static int[][] grid = new int[SIZE][SIZE];
	
	private static int invalidRow;
	private static int invalidCol;

	public static void main(String[] args) throws IOException {
		int choice = 0;

		System.out.println("SOLVE SUDOKU");
		System.out.println("1. Normal rule");
		System.out.println("2. Diagonal rule");
		System.out.println("3. Exit");
		System.out.println("===================");

		do {
			System.out.print("Input your choice: ");
			choice = sc.nextInt();
			if (choice < 1 || choice > 3) {
				System.out.println("Invalid choice! ");
			} else {
				break;
			}
		} while (true);

		switch (choice) {
		case 1:
			if (inputData(false) == false) {
				System.out.println("Invalid input at (r,c): " + invalidRow + "," + invalidCol);
				break;
			}
			
			boolean isSolved = solveSudoku(false);
			if (isSolved) {
				showResult();
			} else {
				System.out.println("Cannot solve this sudoku");
			}

			break;
		case 2:
			if (inputData(true) == false) {
				System.out.println("Invalid input at (r,c): " + invalidRow + "," + invalidCol);
				break;
			}
			
			isSolved = solveSudoku(true);
			if (isSolved) {
				showResult();
			} else {
				System.out.println("Cannot solve this sudoku");
			}
			break;
		case 3:
			System.out.println("Exit program...");
			break;
		}
	}
	
	private static boolean inputData(boolean isCheckDiagonal) throws IOException {
		String filename;

		sc.nextLine(); // clear cache data
		
		do {
			System.out.print("Enter filename to open: ");
			filename = sc.nextLine();
			
			if (!checkFilenameExist(filename)) {
				System.out.println("Invalid filename! Try again");
			} else {
				break;
			}
		} while (true);
		
		fileReaderDemo(filename);
		
		return readFileToGrid(filename) && checkInputData(isCheckDiagonal);
	}

	private static void showResult() {
		System.out.println("Result: ");
		for (int[] row : grid) {
			for (int number : row) {
				System.out.print(number + " ");
			}

			System.out.println();
		}
	}

	private static boolean solveSudoku(boolean isCheckDiagonal) {
		int[] availableSquare = findUnassignedLocation();
		if (availableSquare == null) {
			return true;
		}

		int avaiableRow = availableSquare[0];
		int avaiableCol = availableSquare[1];

		for (int number = 1; number <= 9; number++) {
			if (isPossible(avaiableRow, avaiableCol, number, isCheckDiagonal)) {
				grid[avaiableRow][avaiableCol] = number;
				if (solveSudoku(isCheckDiagonal))
					return true;

				grid[avaiableRow][avaiableCol] = 0;
			}
		}

		return false;
	}

	private static boolean isPossible(int avaiableRow, int avaiableCol, int number, boolean isCheckDiagonal) {
		grid[avaiableRow][avaiableCol] = number;
		if (!checkRow(avaiableRow, avaiableCol) || !checkCol(avaiableRow, avaiableCol)
				|| !checkSquare(avaiableRow, avaiableCol)) {
			grid[avaiableRow][avaiableCol] = 0;
			return false;
		}

		if (isCheckDiagonal && !checkDiagonal(avaiableRow, avaiableCol)) {
			return false;
		}

		return true;
	}

	private static int[] findUnassignedLocation() {
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (grid[i][j] == 0) {
					return new int[] { i, j };
				}
			}
		}

		return null;
	}

	

	private static boolean checkFilenameExist(String inputFilename) {
		File file = new File(inputFilename);
		return file.exists();
	}

	// check duplicated in row:
	private static boolean checkRow(int row, int col) {
		final int number = grid[row][col];
		for (int k = 0; k < SIZE; k++) {
			if (k != col && grid[row][k] == number) {
				return false;
			}
		}

		return true;
	}

	private static boolean checkCol(int row, int col) {
		final int number = grid[row][col];
		for (int k = 0; k < SIZE; k++) {
			if (k != row && grid[k][col] == number) {
				return false;
			}
		}

		return true;
	}

	private static boolean checkSquare(int row, int col) {
		final int number = grid[row][col];
		int startRow = row / 3 * 3;
		int startCol = col / 3 * 3;

		for (int r = startRow; r < startRow + 3; r++) {
			for (int c = startCol; c < startCol + 3; c++) {
				if (r != row && c != col && grid[r][c] == number) {
					return false;
				}
			}
		}

		return true;
	}

	private static boolean checkDiagonal(int row, int col) {
		final int number = grid[row][col];
		if (row == col) {
			for (int i = 0; i < SIZE; i++) {
				if (i != row && grid[i][i] == number) {
					return false;
				}
			}
		}

		if (row + col == SIZE - 1) {
			for (int i = 0; i < SIZE; i++) {
				if (i != row && grid[i][(SIZE - 1 - i)] == number) {
					return false;
				}
			}
		}

		return true;
	}

	private static boolean checkInputData(boolean isCheckDiagonal) {
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				final int number = grid[i][j];
				if (number < 0 || number > 9) {
					invalidRow = i;
					invalidCol = j;
					return false;
				}

				if (number != 0) {
					if (!checkRow(i, j) || !checkCol(i, j) || !checkSquare(i, j)) {
						invalidRow = i;
						invalidCol = j;
						return false;
					}

					if (isCheckDiagonal && !checkDiagonal(i, j)) {
						invalidRow = i;
						invalidCol = j;
						return false;
					}
				}
			}
		}

		return true;
	}

	private static boolean readFileToGrid(String filename) {
		File file = new File(filename);
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			int rowIndex = 0;

			while ((line = reader.readLine()) != null) {
				if (rowIndex == SIZE) {
					return false;
				}

				String[] numbers = line.trim().split("\\s+");
				if (numbers.length != SIZE) {
					return false;
				}

				try {
					for (int i = 0; i < 9; i++) {
						grid[rowIndex][i] = Integer.parseInt(numbers[i]);
					}
				} catch (Exception e) {
					System.out.println("Has another character not a number in line!");
					return false;
				}

				rowIndex++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
			}
		}
		return false;
	}
	
	private static  void fileReaderDemo(String a) throws IOException {
			FileReader fr = new FileReader(a);
			BufferedReader br = new BufferedReader(fr);
			
			String s;
			System.out.println("Data from input file");
			while((s = br.readLine()) !=null && !s.isEmpty()) {
				System.out.println(s);
			}
			fr.close();	
			System.out.println();
	}
}



