package se.endercrypt.neurnet;

import java.io.Serializable;

public class NeuralLayer implements Serializable
{
	private static final long serialVersionUID = -1537485106601712999L;
	/**
	 * 
	 */
	protected Neuron[] neurons;
	private NeuralLayer nextLayer;

	/**
	 * makes a copy of an existing neural layer, linking it to {@code nextLayer}
	 * @param neuralLayer
	 * @param nextLayer
	 */
	protected NeuralLayer(NeuralLayer neuralLayer, NeuralLayer nextLayer)
	{
		int size = neuralLayer.neurons.length;
		neurons = new Neuron[size];
		for (int i = 0; i < size; i++)
		{
			neurons[i] = new Neuron(neuralLayer.neurons[i]);
		}
		this.nextLayer = nextLayer;
	}

	/**
	 * creates a new neuralLayer with the {@code size} as amount of neurons
	 * @param size
	 */
	protected NeuralLayer(int size)
	{
		neurons = new Neuron[size];
		for (int i = 0; i < size; i++)
		{
			neurons[i] = new Neuron();
		}
	}

	/**
	 * links all neurons in this layer to all other neurons in the next layer
	 * @param nextLayer
	 */
	public void linkLayer(NeuralLayer nextLayer)
	{
		this.nextLayer = nextLayer;
		if (nextLayer != null)
		{
			for (Neuron neuron : neurons)
			{
				neuron.linkLayer(nextLayer);
			}
		}
	}

	/**
	 * makes the neurons in this layer calculate its inputs and propogate the output into the inputs of the linked layer
	 */
	public void propogate()
	{
		// reset next layers neurons
		for (Neuron neuron : nextLayer.neurons)
		{
			neuron.reset();
		}
		// propogate
		for (Neuron neuron : neurons)
		{
			neuron.propogate(nextLayer.neurons);
		}
	}
}
