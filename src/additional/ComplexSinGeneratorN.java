/*
 * ComplexSinGenerator.java
 *
 */

package additional;

import sdr.core.FormatFactory;

import sdr.impl.AbstractGenerator;

/**
 * Генератор комплексного синуса(N).
 *
 * @author pvp
 * 
 */
public class ComplexSinGeneratorN extends AbstractGenerator {

	/**
	 * Конструктор по умолчанию.
	 * 
	 */
	public ComplexSinGeneratorN() {

		super(FormatFactory.createComplexFormat());
	}

	// Параметризованные конструкторы

	public ComplexSinGeneratorN(int frequency, double amplitude, double initialPhase) {

		super(FormatFactory.createComplexFormat());

		this.frequency = frequency; this.amplitude = amplitude; this.phase = initialPhase;
	}


	/**
	 * Частота.
	 * 
	 */
	private int frequency = 1000;

	public synchronized int getFrequency() {

		return frequency;
	}

	public synchronized void setFrequency(int frequency) {

		this.frequency = frequency;
	}


	/**
	 * Амплитуда.
	 * 
	 */
	private double amplitude = 100.0;

	public synchronized double getAmplitude() {

		return amplitude;
	}

	public synchronized void setAmplitude(double amplitude) {

		this.amplitude = amplitude;
	}


	/**
	 * Фаза сигнала.
	 *
	 */
	private double phase = 0.0;
        
	/**
	 * Период сигнала.
	 *
	 */
	private static final double period = 2.0 * Math.PI;


	/**
	 * @see ISignalGenerator
	 *
	 */
	@Override
	public void generate(int samplesToGenerate) {

		// Кешируем амплитуду

		double A = getAmplitude();

		// Расчитываем приращение фазы

		double phaseStep = period * getFrequency() / getSignalStream().getFormat().samplesPerSecond;
                
		// Генерируем сигнал

		double[] result = new double[ samplesToGenerate ];

		int i = 0;

		while (i < samplesToGenerate) {

                    double random1 = Math.random();
                    double random2 = Math.random();
                    double random3 = Math.random();
                    
			// Генерируем

			double x = A * (Math.sin(phase * random1) + Math.sin(phase * random2)+ Math.sin(phase * random3));
			double y = A * (Math.cos(phase * random1) + Math.cos(phase * random2) + Math.cos(phase * random3));

			// Сохраняем и двигаем индекс

			result[ i ++ ] = x;
			result[ i ++ ] = y;

			// Двигаем фазу

			phase += phaseStep ;
                        

			// Заворачиваем фазу с учетом периода

			if (phase > period) {

				phase -= period;
			}
		}

		getSignalStream().push(result);
	}

}
