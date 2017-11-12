/*
 * ComplexSinGenerator.java
 *
 */

package additional;

import sdr.core.FormatFactory;

import sdr.impl.AbstractGenerator;

/**
 * Генератор комплексного синуса(2).
 *
 * @author pvp
 * 
 */
public class ComplexSinGenerator3 extends AbstractGenerator {

	/**
	 * Конструктор по умолчанию.
	 * 
	 */
	public ComplexSinGenerator3() {

		super(FormatFactory.createComplexFormat());
	}

	// Параметризованные конструкторы

	public ComplexSinGenerator3(int frequency, double amplitude, double initialPhase) {

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
        private double phase2 = 0.0;
        private double phase3 = 0.0;
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
                double phaseStep2 = period * getFrequency()*2 / getSignalStream().getFormat().samplesPerSecond;
                double phaseStep3 = period * getFrequency()*10 / getSignalStream().getFormat().samplesPerSecond;
		// Генерируем сигнал

		double[] result = new double[ samplesToGenerate ];

		int i = 0;

		while (i < samplesToGenerate) {

			// Генерируем

			double x = A * (Math.sin(phase) + Math.sin(phase2)+ Math.sin(phase3));
			double y = A * Math.cos(phase);

			// Сохраняем и двигаем индекс

			result[ i ++ ] = x;
			result[ i ++ ] = y;

			// Двигаем фазу

			phase += phaseStep;
                        phase2 += phaseStep2;
                        phase3 += phaseStep3;

			// Заворачиваем фазу с учетом периода

			if (phase > period) {

				phase -= period;
			}
		}

		getSignalStream().push(result);
	}

}
