import java.io.*;
import java.util.Scanner;

public class SeamCarver {

	public static void main(String[] args) throws FileNotFoundException {
		if (args.length != 3) { // Only accept three arguments
			System.out.println(
					"Incorrect syntax:\n\nUsage:\n\tSeamCarver [filepath] [horizontal lines to remove] [vertical lines to remove]");
			return;
		}
		File file = new File(args[0]);
		if (!file.exists()) {
			System.out.println("The file specified does not exist.");
			return;
		}
		Scanner input = new Scanner(file);
		String format = getNextToken(input);
		if ("P2".equals(format)) {
			System.out.println("Incorrect PGM file format used, only P2 is supported.");
		}
		int width = Integer.parseInt(getNextToken(input));
		System.out.println("Max Width: " + width);
		int height = Integer.parseInt(getNextToken(input));
		System.out.println("Max Height: " + height);
		int maxGrayValue = Integer.parseInt(getNextToken(input));
		int[][] image = getImage(input, width, height);
		int[][] energyMatrix = getEnergyMatrix(image, width, height);
		int[][] cumulativeEnergyMatrix = getCumulativeEnergyMatrix(energyMatrix, width, height);
		String newFilePath = args[0].substring(0, args[0].length() - 4) + "_processed.pgm";
		saveImage(newFilePath, width, height, maxGrayValue, cumulativeEnergyMatrix);
		input.close();
	}

	public static int[][] getEnergyMatrix(int[][] image, int width, int height) {
		int[][] matrix = new int[height][width];
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++)
				matrix[h][w] = getEnergy(image, h, w, width, height);
		}
		return matrix;

	}

	public static int[][] getCumulativeEnergyMatrix(int[][] energyMatrix, int width, int height) {
		int[][] matrix = new int[height][width];
		int outsideBounds = 5000000;
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++)
				if (h == 0)
					matrix[h][w] = energyMatrix[h][w];
				else
					matrix[h][w] = energyMatrix[h][w]
							+ Math.min((h == 0 || w == 0 ? outsideBounds : matrix[h - 1][w - 1]),
									Math.min((h == 0 ? outsideBounds : matrix[h - 1][w]),
											(h == 0 || w == width - 1 ? outsideBounds : matrix[h - 1][w + 1])));
		}
		return matrix;

	}

	public static int getEnergy(int[][] image, int x, int y, int width, int height) {
		// e(i,j) = |v(i,j)-v(i-1,j)|+ |v(i,j)-v(i+1,j)|+ |v(i,j)-v(i,j-1)|+
		// |v(i,j)-v(i,j+1)|,
		return (x == 0 ? 0 : Math.abs(image[x][y] - image[x - 1][y]))
				+ (x == height - 1 ? 0 : Math.abs(image[x][y] - image[x + 1][y]))
				+ (y == 0 ? 0 : Math.abs(image[x][y] - image[x][y - 1])) + (y == width - 1 ? 0
						: Math.abs(image[x][y] - image[x][y + 1]));
	}

	public static int[][] getImage(Scanner input, int maxWidth, int maxHeight) {
		int width = 0;
		int height = 0;
		int[][] image = new int[maxHeight][maxWidth];
		while (input.hasNext()) {
			image[height][width] = Integer.parseInt(getNextToken(input));
			if (width < maxWidth - 1)
				width++;
			else {
				width = 0;
				height++;
			}
		}
		return image;
	}

	public static String getNextToken(Scanner input) {
		while (input.hasNext()) {
			String nextToken = input.next();
			if (nextToken.equalsIgnoreCase("#")) {
				input.nextLine();
				continue;
			}
			return nextToken;
		}
		return null;
	}

	public static void saveImage(String filepath, int width, int height, int maxGrayValue, int[][] image) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))) {
			bw.write("P2");
			bw.newLine();
			bw.write(String.valueOf(width));
			bw.newLine();
			bw.write(String.valueOf(height));
			bw.newLine();
			bw.write(String.valueOf(maxGrayValue));
			bw.newLine();
			for (int h = 0; h < height; h++) {
				for (int w = 0; w < width; w++)
					bw.write(image[h][w] + " ");
				bw.newLine();
			}

		} catch (IOException e) {

			e.printStackTrace();

		}

	}
}
