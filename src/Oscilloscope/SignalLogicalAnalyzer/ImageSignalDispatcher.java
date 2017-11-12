    /*
Этот модуль подписывается на события класса генератора сигнала, обрабатывает их, извлекая данные из входного потока, и 
далее уже сам становится наблюдаемым, посылая обьекту класса DisplayPannel мнимые отсчеты сигнала 
 */

package Oscilloscope.SignalLogicalAnalyzer;

import Oscilloscope.GlobalVariables;
import Oscilloscope.OscilloscopeMainPanel;
import sdr.core.Format;
import sdr.core.SignalStream;

/**
 *
 * @author Турнамент
 */
public class ImageSignalDispatcher extends SignalAnalyzer{

    public ImageSignalDispatcher(Format format){
        super(format);
     //   supportedFormat = format;
    }
    
    @Override
    public void generate(int samplesToGenerate) {
       if (arraySize==0) {
           return;
        }
//       try{
            double [] result;
            result = new double [samplesToGenerate];
       
            for (int j = 0; j < samplesToGenerate; j++){
                result[j] = imagePart[j];
            }
       
            getSignalStream().push(result);
//       } catch (java.util.ConcurrentModificationException E) {
//           stop();
//       }
    }
    
    @Override
    public void onSignal(SignalStream source) {
        // По умолчанию реализуем политику обработки целых кадров
        while (true) {
            // Проверяем достаточно ли данных
            if (source.available() < source.getFormat().getFrameLength()) {
                break;
            }
            // Обрабатываем один кадр    
            onSignalFrame(source, source.get(source.getFormat().getFrameLength()));            
        }
    }
    
    public void onSignalFrame(SignalStream source, double[] data){
        supportedFormat = new Format(source.getFormat().samplesPerSecond, 
                                     source.getFormat().channelCount, 
                                     (int) Math.round(GlobalVariables.scaleOfAxisX * (source.getFormat().samplesPerSecond / (GlobalVariables.panelWidth * 2)))); // Формат по умолчанию
      
        source.setFormat(supportedFormat);
        arraySize = data.length / 2;
 
        // Мнимая часть
        imagePart = new int[arraySize];

            // Синхронизация
        if (OscilloscopeMainPanel.jToggleButton1.isSelected()) {

           for (int i = 0; i < data.length / 2; i += 2) {
               // Мнимая часть кмоплексного сигнала - нечетные отсчеты массива data
             if ((data[i] < data[i + 2]) & (data[i] * data[i + 2] < 0)) {
                   for (int j = i + 1 + GlobalVariables.shiftAxisX; j < data.length; j += 2) {
                       imagePart[(j - i - GlobalVariables.shiftAxisX) / 2] = (int) Math.round(data[j] * GlobalVariables.scaleOfAxisY) - GlobalVariables.shiftAxisY;
                       }
                }
           }
        }    
             else {
                // Мнимая часть кмоплексного сигнала - нечетные отсчеты массива data
                for (int i = GlobalVariables.shiftAxisX + 1; i < data.length; i += 2) {
                    imagePart[(i - GlobalVariables.shiftAxisX) / 2] = (int) Math.round(data[i] * GlobalVariables.scaleOfAxisY) - GlobalVariables.shiftAxisY;
                }
        }
         // Небольшая пауза
            try {
                Thread.sleep(100);
            } catch (InterruptedException E) {
            }
    }
    
}

