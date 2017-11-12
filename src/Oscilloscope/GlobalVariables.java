/**
 * GlobalVars.java
 */
package Oscilloscope;

import additional.ComplexSinGenerator2;
import additional.ComplexSinGenerator3;
import sdr.beans.ComplexSinGenerator;
import additional.ComplexSinGeneratorN;
import sdr.core.Format;

/**
 * Глобальные переменные.
 *
 * @author Anna
 *
 */
public class GlobalVariables {
          
    // Номер канала
    public static int numberOfChannel;
    
    //Метод получения сигнала, 0 - реальный источник, 1 - абстрактгный генератор синуса
    public static int GenerationMethod;
    
    public static int panelWidth;
    public static int panelHeight;
  
    public static int priority;
    // Масштаб по х 
    public static double scaleOfAxisX = 1.5;
    public static double scaleOfAxisXInDecibels = 10 * Math.log10(scaleOfAxisX);
    
    // Масштаб по у
    public static double scaleOfAxisY = 0.002;
    public static double scaleOfAxisYInDecibels = 10 * Math.log10(scaleOfAxisY); 
       
    // Линейка
    public static int rulerOfAxisX = 0;
    public static int rulerOfAxisY = 0;
    
    // Сдвиг
    public static int shiftAxisX = 0;
    public static int shiftAxisY = 0;
    
    // Размер ячейки
    public static int sizeOfCellOnAxisX = 0;
    public static int sizeOfCellOnAxisY = 0;    
    
    // Отображение окна: Осциллограф = 0; Анализатор спектра = 1;
    public static int displayWindow = 0; 
    
    public static ComplexSinGenerator2 generator;
    public static OscilloscopeFrame frame;  
    public static Format format;
    public static Thread currentDispatchThread;
    
    public static boolean displayEnable;
    
    public static int shiftOnIntervals = 32;  //сдвиг на сегментах
    public static int lengthOfInterval = 64;  //длина сегмента
}
