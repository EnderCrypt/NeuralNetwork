package se.endercrypt.neurnet;

import java.io.Serializable;

public class Neuron implements Serializable
{
	private static final long serialVersionUID = -945098470618059269L;
	/**
	 * 
	 */
	protected double[] synapses;
	protected double totalInput;
	protected double totalWeight;

	/**
	 * generates a new neuron
	 */
	protected Neuron()
	{

	}

	/**
	 * generates a copy of another neuron
	 */
	protected Neuron(Neuron otherNeuron)
	{
		if (otherNeuron.synapses != null)
		{
			int size = otherNeuron.synapses.length;
			synapses = new double[size];
			for (int i = 0; i < size; i++)
			{
				synapses[i] = otherNeuron.synapses[i];
			}
		}
	}

	/**
	 * connects the neuron to all neurons on the {@code nextLayer}
	 * @param nextLayer
	 */
	public void linkLayer(NeuralLayer nextLayer)
	{
		synapses = new double[nextLayer.neurons.length];
		for (int i = 0; i < synapses.length; i++)
		{
			//Neuron linkedNeuron = nextLayer.neurons[i];
			synapses[i] = Math.random();
		}
	}

	/**
	 * resets this neuron to an empty state
	 */
	public void reset()
	{
		totalInput = 0;
		totalWeight = 0;
	}

	/**
	 * input new values into this neuron
	 * @param input
	 * @param weight
	 */
	public void input(double input, double weight)
	{
		totalInput += input * weight;
		totalWeight += weight;
	}

	/**
	 * calculates the output value of this neuron
	 * @return
	 */
	public double calculateOutput()
	{
		return (1.0 / totalWeight) * totalInput;
	}

	/**
	 * makes this neuron randomize its output weights slightly
	 * @param effect
	 */
	public void randomize(double effect)
	{
		for (int i = 0; i < synapses.length; i++)
		{
			double weight = synapses[i];
			double modify = ((effect * 2) * Math.random()) - effect;
			weight += modify;
			if (weight < 0.0)
			{
				weight = 0 - weight;
			}
			if (weight > 1.0)
			{
				weight = 1 - (weight % 1.0);
			}
			synapses[i] = weight;
		}
	}

	/**
	 * activates this neuron to output into the input of all other neurons on the next layer
	 */
	public void propogate(Neuron[] conectedNeurons)
	{
		double output = calculateOutput();
		for (int i = 0; i < synapses.length; i++)
		{
			Neuron neuron = conectedNeurons[i];
			double weight = synapses[i];
			neuron.input(output, weight);
		}
	}
}
