package se.test1;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import se.endercrypt.neurnet.NeuralNetwork;

public class Main
{
	public static NeuralNetwork focusNeuralNetwork = new NeuralNetwork(3, 3, new int[] { 2, 2, 2 });
	public static JFrame jFrame;

	public static void main(String[] args) throws InterruptedException
	{
		// start gui
		startGUI();

		// wait a while
		Thread.sleep(1500);

		// simulation
		trainNetwork();
		System.out.println("Neural Network training complete!");

		//saveNetwork();
	}

	private static void startGUI()
	{
		jFrame = new JFrame();
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setTitle("Neural Network");
		JPanel jPanel = new JPanel()
		{
			@Override
			protected void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				BufferedImage bufferedImage = focusNeuralNetwork.screenshot(Color.WHITE, Color.BLACK);
				g2d.drawImage(bufferedImage, 16, 16, null);
			}
		};
		jPanel.setBackground(Color.WHITE);
		jPanel.setPreferredSize(new Dimension(1024, 512));
		jFrame.add(jPanel);
		jFrame.pack();
		jFrame.setVisible(true);
	}

	private static void trainNetwork() throws InterruptedException
	{
		int simulations = 0;
		double bestScore = Double.MAX_VALUE;
		while (bestScore > 0.001)
		{
			NeuralNetwork neuralNetwork = new NeuralNetwork(focusNeuralNetwork);
			neuralNetwork.randomize(0.5);
			simulations++;
			// run tests
			double score = 0.0;
			for (double x = 0.0; x < 1.0; x += 0.05)
			{
				double[] result = neuralNetwork.activate(new double[] { x, 0.0, 0.0 });
				score += Math.abs(result[0] - (x * 0.5));
			}
			// check
			if (score < bestScore)
			{
				focusNeuralNetwork = neuralNetwork;
				bestScore = score;
				System.out.println(simulations + ". Inaccuracy: " + (Math.floor(bestScore * 1000.0) / 1000.0));
				jFrame.repaint();
				Thread.sleep(20);
			}
		}
	}

	private static void saveNetwork()
	{
		BufferedImage bufferedImage = focusNeuralNetwork.screenshot(Color.WHITE, Color.BLACK);
		try
		{
			ImageIO.write(bufferedImage, "PNG", new File("./ANN.png"));
		}
		catch (IOException e)
		{
			System.err.println("Failed to create image!");
		}
	}
}
