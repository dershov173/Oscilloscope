/*
Этот модуль подписывается на события класса генератора сигнала, обрабатывает их, извлекая данные из входного потока, и 
далее уже сам становится наблюдаемым, посылая обьекту класса DisplayPannel обработанные отсчеты сигнала 
 */

package Oscilloscope.SignalLogicalAnalyzer;

import sdr.core.Format;
import sdr.core.FormatException;
import sdr.core.ISignalConsumer;
import sdr.core.ISignalSource;
import sdr.core.ISignalStreamListener;
import sdr.core.SignalStream;
import sdr.impl.AbstractGenerator;
import sdr.impl.AbstractSignalSource;

/**
 *
 * @author Турнамент
 */
public abstract class SignalAnalyzer extends AbstractGenerator implements ISignalConsumer,ISignalStreamListener{

    public int arraySize = 0;
    // Действительная часть
    public int[] realPart;
    // Мнимая часть
    public int[] imagePart;
    // Огибающая 
    public int[] envelope;
    // Формат по умолчанию
    public  Format supportedFormat = new Format(-1, -1, -1);
    //Формат выходного сигнала
    public  Format f = new Format(-1, -1, -1);
   // Полученный сигнал
    protected SignalStream inputStream = null;
    
    public SignalAnalyzer(Format format){
        super(format);
        f = format;
        //supportedFormat = format;
    }
    
    @Override
    public void connect(ISignalSource source) throws FormatException {
                // Проверяем формат
        if (source.getSignalStream().getFormat().compareTo(supportedFormat) != 0) {
            throw new FormatException();
        }
        // Подключаемся
        inputStream = source.getSignalStream();
        // Подписываемся на события
        if (supportedFormat.getFrameLength() != -1) {
            // Если из поддерживаемого формата может быть определен размер кадра,
            // то используем этот размер...
            inputStream.addListener(this, supportedFormat.getFrameLength());
        } else {
            // ... противном случае, используем размер кадра источника сигнала.
            inputStream.addListener(this, inputStream.getFormat().getFrameLength());
        }
    }

    @Override
    public void disconnect() {
        // Отписываемся от событий
        inputStream.removeListener(this);
        // Отключаемся
        inputStream = null;
    }
      @Override
    public Format getSupportedFormat() {
        return supportedFormat;
    }

    @Override
    public SignalStream getInputStream() {
        return inputStream;
    }

    private AbstractSignalSource signalSource = null;
    @Override
    public void onConnect(SignalStream source) {
        // Создаем выходной поток
        // Формат аналогичен формату входного потока
        
        signalSource = new AbstractSignalSource(f);
    }

    @Override
    public void onFormatChange(SignalStream source) {
        // Устанавливаем формат выходного потока  
        // Формат аналогичен формату входного потока
        signalSource.getSignalStream().setFormat(f);
    }
    
    @Override
	public SignalStream getSignalStream() {

		return signalSource.getSignalStream();
	}
    
}