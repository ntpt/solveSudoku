
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
	private static int[][] grid = new int[9][9];

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
			if (inputData(false) == true) {
				boolean isSolved = solveSudoku(false);
				if (isSolved) {
					showResult();
				} else {
					System.out.println("Cannot solve this sudoku");
				}
			}

			break;
		case 2:
			if (inputData(true) == true) {
				boolean isSolved = solveSudoku(true);
				if (isSolved) {
					showResult();
				} else {
					System.out.println("Cannot solve this sudoku");
				}
			}
			break;
		case 3:
			System.out.println("Exit program...");
			break;
		}
	}

	private static boolean inputData(boolean isCheckDiagonal) throws IOException {
		String input;
		sc.nextLine();

		do {
			System.out.print("Enter filename to open: ");
			input = sc.nextLine();

			if (!checkFilenameExist(input)) {
				System.out.println("Invalid filename! Try again");
			} else {
				break;
			}
		} while (true);
		readFileInput(input);
		
		if (inputDataToGrid(input) && checkInputData(isCheckDiagonal)) {
			return true;
		} else {
			System.out.println("Invalid input at (r,c): " + invalidRow + "," + invalidCol);
			return false;
		}
	}
	
	private static boolean inputDataToGrid(String filename) {
		File file = new File(filename);
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			int rowIndex = 0;

			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) {
					continue;
				}
				
				if (rowIndex == 9) {
					invalidRow = 9;
					invalidCol = 0;
					return false;
				}

				String[] strs = line.trim().split("\\s+");
				if (strs.length != 9) {
					invalidRow = rowIndex;
					invalidCol = 9;
					return false;
				}

				int i = 0;
				try {
					for (i = 0; i < 9; i++) {
						grid[rowIndex][i] = Integer.parseInt(strs[i]);
					}
				} catch (NumberFormatException e) {
					invalidRow = rowIndex;
					invalidCol = i;
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

	private static void showResult() {
		System.out.println("====Result=====");
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
			if (isPossible(avaiableRow, avaiableCol, number, isCheckDiagonal)) { // 1, 2, 3
				grid[avaiableRow][avaiableCol] = number;

				showResult();

				if (solveSudoku(isCheckDiagonal)) {
					return true;
				}

			}
			grid[avaiableRow][avaiableCol] = 0;
		}

		return false;
	}

	private static boolean isPossible(int avaiableRow, int avaiableCol, int number, boolean isCheckDiagonal) {
		grid[avaiableRow][avaiableCol] = number;
		if (!checkRow(avaiableRow, avaiableCol) || !checkCol(avaiableRow, avaiableCol)
				|| !checkSquare(avaiableRow, avaiableCol)) {
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

	private static void readFileInput(String a) throws IOException {

		FileReader fr = new FileReader(a);
		BufferedReader br = new BufferedReader(fr);

		System.out.println("Data input");

		String s;
		while ((s = br.readLine()) != null && !s.isEmpty()) {
			System.out.println(s);
		}
		fr.close();

	}
}
