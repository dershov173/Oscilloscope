/*
 * ReceiverSignalSource.java
 * 
 */

package additional;

import sdr.core.Format;

import sdr.rtp.RTP_Receiver;

/**
 * Приемник сигнала от Бригантина-ПРМ.
 *
 * @author pvp
 * 
 */
public class ReceiverSignalSource extends RTP_Receiver {

	/**
	 * Конструктор по умолчанию.
	 * 
	 */
	public ReceiverSignalSource() {

		super(new Format(96000, 2, 1500));
	}

	// Параметризованные конструкторы

	public ReceiverSignalSource(int port) {

		super(new Format(96000, 2, 1500), port);
	}

}
