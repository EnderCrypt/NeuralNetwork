package se.endercrypt.neurnet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import se.endercrypt.neurnet.exception.InputNeuronCountMismatchException;
import se.endercrypt.neurnet.exception.insufficientNeuralLayersException;

/**
 * Represents a full neural network with layer
 * @author EnderCrypt
 */
public class NeuralNetwork implements Serializable
{
	private static final long serialVersionUID = 6761739874062028399L;
	/**
	 * 
	 */
	private NeuralLayer[] neuralLayers;

	/**
	 * produces a copy of an existing neural network
	 * @param neuralNetwork
	 */
	public NeuralNetwork(NeuralNetwork neuralNetwork)
	{
		neuralLayers = new NeuralLayer[neuralNetwork.neuralLayers.length];
		NeuralLayer lastNeuralLayer = null;
		for (int i = neuralNetwork.neuralLayers.length - 1; i >= 0; i--)
		{
			neuralLayers[i] = new NeuralLayer(neuralNetwork.neuralLayers[i], lastNeuralLayer);
			lastNeuralLayer = neuralLayers[i];
		}
	}

	/**
	 * constructs a new neural network with input/output/hidden layer('s)
	 * each index in layerSize creates its own hidden layer, with the number as the amount of neurons.
	 * <br><br>
	 * for example, to create 2 hidden layers, the first having 5 neurons and the second having 3, you'd have to use the argument
	 * new int[]{5,3}
	 * 
	 * @param inputNeurons
	 * @param outputNeurons
	 * @param layerSize
	 */
	public NeuralNetwork(int inputNeurons, int outputNeurons, int[] layerSize)
	{
		if (layerSize.length < 0)
			throw new insufficientNeuralLayersException();
		int layers = layerSize.length + 2;
		neuralLayers = new NeuralLayer[layers];
		// add input layer
		neuralLayers[0] = new NeuralLayer(inputNeurons);
		// add layers
		for (int i = 0; i < layerSize.length; i++)
		{
			neuralLayers[i + 1] = new NeuralLayer(layerSize[i]);
		}
		// add output layer
		neuralLayers[neuralLayers.length - 1] = new NeuralLayer(outputNeurons);
		// link all layers
		linkLayers();
	}

	/**
	 * makes sure that all layers are properly linked to the next layer
	 */
	public void linkLayers()
	{
		for (int i = 0; i < neuralLayers.length - 1; i++)
		{
			NeuralLayer neuralLayer = neuralLayers[i];
			NeuralLayer nextNeuralLayer = neuralLayers[i + 1];
			neuralLayer.linkLayer(nextNeuralLayer);
		}
	}

	/**
	 * makes the neural network randomize depending on {@code effect}
	 * @param effect 0.0 to 1.0
	 */
	public void randomize(double effect)
	{
		for (int i = 0; i < neuralLayers.length - 1; i++)
		{
			NeuralLayer neuralLayer = neuralLayers[i];
			for (Neuron neuron : neuralLayer.neurons)
			{
				if (Math.random() < 0.25)
					neuron.randomize(effect);
			}
		}
	}

	/**
	 * activates the neural network with {@code inputs} as input into the input layer neurons
	 * the length of the array MUST be equal to the amount of input neurons
	 * @param inputs
	 * @return
	 */
	public double[] activate(double[] inputs)
	{
		// check that the input numbers match the amount of neurons in the input layer
		if (inputs.length != neuralLayers[0].neurons.length)
			throw new InputNeuronCountMismatchException();
		// set input
		Neuron[] inputNeurons = neuralLayers[0].neurons;
		for (int i = 0; i < inputNeurons.length; i++)
		{
			Neuron neuron = inputNeurons[i];
			neuron.totalInput = inputs[i];
			neuron.totalWeight = 1.0;
		}
		// calculate
		for (int i = 0; i < neuralLayers.length - 1; i++)
		{
			NeuralLayer neuralLayer = neuralLayers[i];
			neuralLayer.propogate();
		}
		// return result
		Neuron[] outputNeurons = neuralLayers[neuralLayers.length - 1].neurons;
		double[] results = new double[outputNeurons.length];
		for (int i = 0; i < outputNeurons.length; i++)
		{
			Neuron neuron = outputNeurons[i];
			results[i] = neuron.calculateOutput();
		}
		return results;
	}

	/**
		 * Creates an image of the neural network
		 * @param g2d
		 */
	public BufferedImage screenshot(Color bgColor, Color color)
	{
		// check max neurons
		int maxNeurons = 0;
		for (NeuralLayer neuralLayer : neuralLayers)
		{
			int neurons = neuralLayer.neurons.length;
			if (neurons > maxNeurons)
				maxNeurons = neurons;
		}
		// create image
		int width = 32 + (neuralLayers.length - 1) * 128 + 1;
		int height = 32 + (maxNeurons - 1) * 64 + 1;
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bufferedImage.createGraphics();
		g2d.setPaint(bgColor);
		g2d.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
		g2d.setPaint(color);
		// draw synapses
		for (int i = 0; i < neuralLayers.length - 1; i++)
		{
			NeuralLayer neuralLayer = neuralLayers[i];
			for (int ii = 0; ii < neuralLayer.neurons.length; ii++)
			{
				Neuron neuron = neuralLayer.neurons[ii];
				for (int iii = 0; iii < neuron.synapses.length; iii++)
				{
					double synapse = neuron.synapses[iii];
					g2d.setStroke(new BasicStroke((float) (synapse * 10)));
					g2d.drawLine(16 + i * 128, 16 + ii * 64, 16 + (i + 1) * 128, 16 + iii * 64);
				}
			}
		}
		// draw neurons
		g2d.setStroke(new BasicStroke(1.0f));
		for (int i = 0; i < neuralLayers.length; i++)
		{
			NeuralLayer neuralLayer = neuralLayers[i];
			for (int ii = 0; ii < neuralLayer.neurons.length; ii++)
			{
				//Neuron neuron = neuralLayer.neurons[ii];
				g2d.drawOval(i * 128, ii * 64, 32, 32);
			}
		}
		// finish
		g2d.dispose();
		return bufferedImage;
	}
}