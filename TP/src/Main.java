import java.io.File;
import java.util.Scanner;

import WavFile.WavFile;

public class Main {

	public static void main(String[] args) {
		WavFile input = null;
		double min = 0, max = 0;
		try {
			input = WavFile.openWavFile(new File("inputs/xtine.wav"));

			System.out.println("Duration: " + (double) input.getNumFrames() / (double) input.getSampleRate() + "s");

			double[] minMax = getMinMax(input);

			min = minMax[0];
			max = minMax[1];

		} catch (Exception e) {

		}

		for (int i = 8; i >= 1; i--) {
			createNewSample(i, min, max);
		}
	}

	public static double classifyBits(double value, int bits, double min, double max) {
		double range = (max - min) / 2;
		double center = (max + min) / 2;

		double result = center + Math.ceil((value - center) / (max - center) * bits) / bits;

		return result;
	}

	public static double[] getMinMax(WavFile file) {

		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;

		// Get the number of audio channels in the wav file
		int numChannels = file.getNumChannels();

		// Create a fileBuffer of 100 frames
		double[] fileBuffer = new double[100 * numChannels];

		int framesRead;
		try {
			do {
				// Read frames into fileBuffer
				framesRead = file.readFrames(fileBuffer, 100);

				// Loop through frames and look for minimum and maximum value
				for (int s = 0; s < framesRead * numChannels; s++) {
					if (fileBuffer[s] > max)
						max = fileBuffer[s];
					if (fileBuffer[s] < min)
						min = fileBuffer[s];
				}
			} while (framesRead != 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		double[] result = { min, max };
		return result;
	}

	public static void createNewSample(int bitsNb, double min, double max) {
		try {
			WavFile input = WavFile.openWavFile(new File("inputs/xtine.wav"));
			WavFile output = WavFile.newWavFile(new File("outputs/xtine_" + bitsNb + "bits.wav"),
					input.getNumChannels(), input.getNumFrames(), input.getValidBits(), input.getSampleRate());

			double[] inputBuffer = new double[100 * input.getNumChannels()];
			double[] outputBuffer = new double[100 * input.getNumChannels()];
			int framesRead;

			do {
				// Read frames into inputBuffer
				framesRead = input.readFrames(inputBuffer, 100);

				// Loop through frames and output it in the new file
				for (int s = 0; s < framesRead * input.getNumChannels(); s++) {
					outputBuffer[s] = classifyBits(inputBuffer[s], bitsNb, min, max);
				}

				output.writeFrames(outputBuffer, framesRead);
			} while (framesRead != 0);

			// Close the wavFile
			input.close();
			output.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
