/**
 * DisplayPannel.jsva
 */
package Oscilloscope;

import java.awt.AWTEvent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Math.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.apache.log4j.Logger;
import sdr.core.Format;
import sdr.core.FormatException;
import sdr.core.ISignalConsumer;
import sdr.core.ISignalSource;
import sdr.core.ISignalStreamListener;
import sdr.core.SignalStream;
import sdr.impl.AbstractSignalSource;

/**
 * Окно с отображаемым графиком.
 *
 * @author Anna
 *
 */
public class DisplayPannel extends javax.swing.JPanel 
                           implements MouseListener, ActionListener, ISignalConsumer,ISignalStreamListener {
    
    private static final Logger LOG = Logger.getLogger(DisplayPannel.class);
    // Количество отсчетов
    public int arraySize = 10;
    // Координаты нажатия мыши
    public int coordinatesOfMouseOnAxisX, coordinatesOfMouseOnAxisY;
    // Длина, ширина панели
    public static int panelHeight, panelWidth = 10;
    // Действительная часть
    public int[] realPart = new int[arraySize];
    // Мнимая часть
    public int[] imagePart = new int[arraySize];
    // Огибающая 
    public int[] envelope = new int[arraySize];
    // Ось Х  
    public int[] axisX = new int[arraySize];
    // Формат по умолчанию
    public  Format supportedFormat = new Format(-1, -1, -1);
    // Сигнал
    private SignalStream inputStream = null;
    // Перо
    BasicStroke thickPen = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    BasicStroke thinPen = new BasicStroke(0.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    // Действительная часть для анализатора спектра
    public double[] realPartForSpectrumAnalyzer = new double[arraySize];
    // Мнимая часть для анализатора спектра
    public double[] imagePartForSpectrumAnalyzer = new double[arraySize];
    // Данные анализатора спектра
    public double[] DataForSpectrumAnalyzer = new double[arraySize];
    // Данные для отображения анализатора спектра
    public int[] fftData = new int[arraySize];
    // Число для преобразования Фурье
    public int N;

    /**
     * Создание новой формы GraphicsPanel
     */
    public DisplayPannel() {
        initComponents();
        enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        addMouseListener(this);
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

    public void onSignalFrame(SignalStream source, double[] data) { 
        supportedFormat = new Format(source.getFormat().samplesPerSecond, 
                                     source.getFormat().channelCount, 
                                     (int) Math.round(GlobalVariables.scaleOfAxisX * (source.getFormat().samplesPerSecond / (panelWidth * 2)))); // Формат по умолчанию
        source.setFormat(supportedFormat);
        
        // Отображение для линейки       
        OscilloscopeMainPanel.jTextField7.setText(Integer.toString(GlobalVariables.rulerOfAxisX));
        if (GlobalVariables.displayWindow == 0) {
            OscilloscopeMainPanel.jTextField9.setText(Integer.toString(GlobalVariables.rulerOfAxisY));
        } else {
            OscilloscopeMainPanel.jTextField9.setText("");
        }
                
        // Коэффициенты сжатия/растяжения
        GlobalVariables.scaleOfAxisXInDecibels = (double) OscilloscopeMainPanel.jSpinner1.getValue();
        GlobalVariables.scaleOfAxisX = Math.pow(10, GlobalVariables.scaleOfAxisXInDecibels / 10);
        GlobalVariables.scaleOfAxisYInDecibels = (double) OscilloscopeMainPanel.jSpinner2.getValue();
        GlobalVariables.scaleOfAxisY = Math.pow(10, GlobalVariables.scaleOfAxisYInDecibels / 10);

        if (GlobalVariables.displayWindow == 0) {

            // Оcь Х
            axisX = new int[data.length];

            //Сдвиг 
            GlobalVariables.shiftAxisX = (int) Math.round((int) OscilloscopeMainPanel.jSpinner3.getValue());
            GlobalVariables.shiftAxisY = (int) Math.round((int) OscilloscopeMainPanel.jSpinner4.getValue() * GlobalVariables.scaleOfAxisY);

            // Синхронизация
//            if (OscilloscopeMainPanel.jToggleButton1.isSelected()) {
//
//                for (int i = 0; i < data.length ; i ++) {
//
//                    if ((data[i] < data[i + 1]) & (data[i] * data[i + 1] < 0)) {
//
//                        for (int j = 0; j < data.length ; j++) {
//                            axisX[j] = j;
//                        }
//                        for (int j = i + GlobalVariables.shiftAxisX; j < 2*data.length; j += 2) {
//                            realPart[i / 2] = panelHeight / 2 - (int) Math.round(data[i]);
//                        }
//                        for (int j = i + 1 + GlobalVariables.shiftAxisX; j < data.length; j += 2) {
//                            imagePart[(j - i - GlobalVariables.shiftAxisX) / 2] = panelHeight / 2 - (int) Math.round(data[j] * GlobalVariables.scaleOfAxisY) - GlobalVariables.shiftAxisY;
//                        }
//                        for (int j = i + GlobalVariables.shiftAxisX; j < data.length - 1; j += 2) {
//                            envelope[(j - i - GlobalVariables.shiftAxisX) / 2] = panelHeight / 2 - (int) Math.round(GlobalVariables.scaleOfAxisY * (Math.sqrt(data[j] * data[j] + data[j + 1] * data[j + 1]))) - GlobalVariables.shiftAxisY;
//                        }
//                }
//                }
//                // Без синхронизации    
//            } else {
                // Заполнение массивов данными
                for (int i = 0; i < data.length ; i++) {
                    axisX[i] = i;
                }
                if ( GlobalVariables.numberOfChannel == 0) {
                    arraySize = data.length ;
                    // Действительная часть
                    realPart = new int[arraySize];
                    
                    for (int i = GlobalVariables.shiftAxisX; i < data.length; i ++) {
                        realPart[(i - GlobalVariables.shiftAxisX) ] = panelHeight / 2 - (int) Math.round(data[i]);
                    }
                }
                else if ( GlobalVariables.numberOfChannel == 1) {
                    arraySize = data.length ;
                    // мнимая часть
                    imagePart = new int[arraySize];
                    
                    for (int i = GlobalVariables.shiftAxisX; i < data.length; i ++) {
                        imagePart[(i - GlobalVariables.shiftAxisX) ] = panelHeight / 2 - (int) Math.round(data[i]);
                    }
                }
                else if ( GlobalVariables.numberOfChannel == 2) {
                    arraySize = data.length / 2;
                    // Действительная и мнимая части
                    realPart = new int[arraySize];
                    imagePart = new int[arraySize];
                    
                    for (int i = GlobalVariables.shiftAxisX; i < data.length; i += 2) {
                        realPart[(i - GlobalVariables.shiftAxisX) / 2] = panelHeight / 2 - (int) Math.round(data[i]);
                        imagePart[(i - GlobalVariables.shiftAxisX+1) / 2] = panelHeight / 2 - (int) Math.round(data[i+1]);
                    }
                } else if ( GlobalVariables.numberOfChannel == 3) {
                    arraySize = data.length / 3;
                    // Действительная и мнимая части, и огибающая
                    realPart = new int[arraySize];
                    imagePart = new int[arraySize];
                    envelope = new int[arraySize];
                    
                    for (int i = GlobalVariables.shiftAxisX; i < data.length; i += 3) {
                        realPart[(i - GlobalVariables.shiftAxisX) / 3] = panelHeight / 2 - (int) Math.round(data[i]);
                        imagePart[(i - GlobalVariables.shiftAxisX+1) / 3] = panelHeight / 2 - (int) Math.round(data[i+1]);
                        envelope[(i - GlobalVariables.shiftAxisX+2) / 3] = panelHeight / 2 - (int) Math.round(data[i+2]);
                    }
                }
            

            // Размеры клетки        
            GlobalVariables.sizeOfCellOnAxisX = (int) Math.round(panelWidth / 10 / GlobalVariables.scaleOfAxisX);
            OscilloscopeMainPanel.jTextField3.setText(Integer.toString(GlobalVariables.sizeOfCellOnAxisX));
            GlobalVariables.sizeOfCellOnAxisY = (int) Math.round((panelHeight / 8) * ((maximumAmpl(realPart) - panelHeight / 2) / GlobalVariables.scaleOfAxisY) / (maximumAmpl(realPart) - panelHeight / 2));
            OscilloscopeMainPanel.jTextField5.setText(Integer.toString(GlobalVariables.sizeOfCellOnAxisY));
            
            // Перерисовка
            repaint();

            // Небольшая пауза
            try {
                Thread.sleep(100);
            } catch (InterruptedException E) {
            }


        } else if (GlobalVariables.displayWindow == 1) {

            int k = (int) (log(arraySize) / log(2));
            int N = (int) round(pow(2, k));
            

            realPartForSpectrumAnalyzer = new double[N];
            imagePartForSpectrumAnalyzer = new double[N];
            DataForSpectrumAnalyzer = new double[N];
            fftData = new int[N];
            axisX = new int[N];

            for (int i = 0; i < N * 2; i += 2) {
                realPartForSpectrumAnalyzer[i / 2] = data[i];
            }

            for (int i = 1; i < N * 2; i += 2) {
                imagePartForSpectrumAnalyzer[i / 2] = data[i];
            }
            
            for (int i = 0; i < N; i++) {
                axisX[i] = i;
            }
            
            double K = 0;

            int D = GlobalVariables.lengthOfInterval;
            int S = GlobalVariables.shiftOnIntervals;
            int P = (N - D)/S + 1;
            double [] w = new double [D];
            double T = 1.0 / N;//частота дикретизации
            
            if (GlobalVariables.numberOfChannel == 0) {

                // Преобразование Фурье
                FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
                Complex[] complexData = new Complex[N];
                for (int i = 0; i < N; i++) {
                    complexData[i] = new Complex(realPartForSpectrumAnalyzer[i], imagePartForSpectrumAnalyzer[i]);
                }
                Complex[] complexTransform = transformer.transform(complexData, TransformType.FORWARD);
                for (int i = 0; i < N; i++) {
                    DataForSpectrumAnalyzer[i] = complexTransform[N - i - 1].abs();
                }
                K = 0.008;
                
            } else if (GlobalVariables.numberOfChannel == 1) {

                // Преобразование Фурье
                FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
                Complex[][] complexTransform = new Complex [P][D];
//                for (int i = 0; i < N; i++) {
//                    complexData[i] = new Complex(panel.realPartForSpectrumAnalyzer[i], panel.imagePartForSpectrumAnalyzer[i]);
//                }
                for (int p = 0; p < P; p++){
                    double U = TriangleWindow(w, T, D);
                    Complex[] complexData = new Complex[D];
                    for (int i = 0; i < D; i++){
                        complexData[i] = new Complex(w[i] * realPartForSpectrumAnalyzer[i + p*S] / Math.sqrt(U*D*T), w[i] * imagePartForSpectrumAnalyzer[i + p*S] / Math.sqrt(U*D*T));
                    }
                complexTransform[p] = transformer.transform(complexData, TransformType.FORWARD);
                }
                
                //Complex[] complexTransform = transformer.transform(complexData, TransformType.FORWARD);
                for (int i = 0; i < D; i++) {
                    double sum = 0;
                    for (int p = 0; p < P; p++){
                        sum = sum + complexTransform[p][i].abs() * complexTransform[p][i].abs();
                    }
                    sum = sum / P;
                    DataForSpectrumAnalyzer[i] = 10 * log10(sum);
                }
                K = 500;
                
            } else if (GlobalVariables.numberOfChannel == 2) {

                // Преобразование Фурье
                FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
                Complex[] complexData = new Complex[N];
                for (int i = 0; i < N; i++) {
                    complexData[i] = new Complex(realPartForSpectrumAnalyzer[i], imagePartForSpectrumAnalyzer[i]);
                }
                Complex[] complexTransform = transformer.transform(complexData, TransformType.FORWARD);
                for (int i = 0; i < N; i++) {
                    DataForSpectrumAnalyzer[i] = complexTransform[N - i - 1].conjugate().multiply(complexTransform[N - i - 1]).divide(N).abs();
                }
                K = 0.000001;
                
            } else if (GlobalVariables.numberOfChannel == 3) {
                
                // Преобразование Фурье
                FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
                Complex[] complexData = new Complex[N];
                for (int i = 0; i < N; i++) {
                    complexData[i] = new Complex(realPartForSpectrumAnalyzer[i], imagePartForSpectrumAnalyzer[i]);
                }
                Complex[] complexTransform = transformer.transform(complexData, TransformType.FORWARD);
                for (int i = 0; i < N; i++) {
                   DataForSpectrumAnalyzer[i] = complexTransform[N - i - 1].abs();
                }
                int tmp[] = new int[N];
                for (int i = 0; i < N; i++) {
                    tmp[i] = (int) round(DataForSpectrumAnalyzer[i]);
                }
                
                K = 1;                
            }
            
            for (int i = 0; i < N; i++) {
                fftData[i] = panelHeight / 2 - (int) round((DataForSpectrumAnalyzer[i] * (GlobalVariables.scaleOfAxisY * K)));
               // System.out.println(K);
            
            }

            // Размеры клетки        
            GlobalVariables.sizeOfCellOnAxisX = (int) Math.round(48000 / N * panelWidth / 10 / GlobalVariables.scaleOfAxisX / 2);
            OscilloscopeMainPanel.jTextField3.setText(Integer.toString(GlobalVariables.sizeOfCellOnAxisX));
            GlobalVariables.sizeOfCellOnAxisY = -(int) Math.round((panelHeight / 8) * ((maximumAmpl(realPartForSpectrumAnalyzer) - panelHeight / 2) / (GlobalVariables.scaleOfAxisY * 0.01)) / (panelHeight / 2 - maximumAmpl(realPartForSpectrumAnalyzer)));
            OscilloscopeMainPanel.jTextField5.setText("");

            // Перерисовка
            repaint();

            // Небольшая пауза
            try {
                Thread.sleep(100);
            } catch (InterruptedException E) {
            }
        }
    }
    
    private AbstractSignalSource signalSource = null;
    
    @Override
    public void onConnect(SignalStream source) {
        // Создаем выходной поток
        // Формат аналогичен формату входного потока	
        signalSource = new AbstractSignalSource(source.getFormat().copy());
        // signalSource.getSignalStream().addListener(this);
        int size = source.getFormat().samplesPerSecond * source.getFormat().channelCount;
    }

    /**
     * @see ISignalStreamListener
     */
    @Override
    public void onFormatChange(SignalStream source) {
        // Устанавливаем формат выходного потока  
        // Формат аналогичен формату входного потока
        int size = source.getFormat().samplesPerSecond * source.getFormat().channelCount;
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
        if (inputStream == null)
            return;
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
    
    public double TriangleWindow(double [] w, double T, int D){
        for (int n = 0; n < D; n++){
            w[n] = 1 - 2 * Math.abs((n - (D - 1)/2)/(D - 1));
        }
        double sum = 0;
        for (int n = 0; n < D; n++){
            sum = sum + w[n] * w[n];
        }
        return sum * T;
    }
    // Отрисовка
    @Override
    public void paint(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        super.paint(g);
        
        // Высота и ширина панели
        panelHeight = GlobalVariables.panelHeight = getHeight();
        panelWidth = GlobalVariables.panelWidth = getWidth();

        
        // Сетка
        g2.setColor(Color.LIGHT_GRAY);
        for (int i = 1; i < 8; i++) {
            g2.drawLine(0, (panelHeight / 8) * i, panelWidth - 1, (panelHeight / 8) * i);            
        }
        for (int i = 1; i < 10; i++) {
            g2.drawLine((panelWidth / 10) * i, 0, (panelWidth / 10) * i, panelHeight - 1);             
        }

        // Линейка       
        g2.setColor(Color.BLACK);
        g2.drawLine(coordinatesOfMouseOnAxisX, 0, coordinatesOfMouseOnAxisX, panelHeight - 1);
        int[] XArray1 = {coordinatesOfMouseOnAxisX - 5, coordinatesOfMouseOnAxisX + 5, coordinatesOfMouseOnAxisX};
        int[] YArray1 = {0, 0, 5};
        g2.fillPolygon(XArray1, YArray1, 3);
        int[] XArray2 = {coordinatesOfMouseOnAxisX - 5, coordinatesOfMouseOnAxisX + 5, coordinatesOfMouseOnAxisX};
        int[] YArray2 = {panelHeight - 1, panelHeight - 1, panelHeight - 1 - 5};
        g2.fillPolygon(XArray2, YArray2, 3);

        if (GlobalVariables.displayWindow == 0) {
            // Отображение графика
            if (GlobalVariables.numberOfChannel == 0) {

                //Действительная часть
                g2.scale(GlobalVariables.scaleOfAxisX, 1);
                g2.setStroke(thickPen);
                g2.setColor(Color.blue);
                g2.drawPolyline(axisX, realPart, realPart.length);

            } else if (GlobalVariables.numberOfChannel == 1) {

                // Мнимая часть
                g2.scale(GlobalVariables.scaleOfAxisX, 1);
                g2.setStroke(thickPen);
                g2.setColor(Color.red);
                g2.drawPolyline(axisX, imagePart, imagePart.length);

            } else if (GlobalVariables.numberOfChannel == 2) {

                // Действительная и мнимая части
                g2.scale(GlobalVariables.scaleOfAxisX, 1);
                g2.setStroke(thickPen);
                g2.setColor(Color.blue);
                g2.drawPolyline(axisX, realPart, realPart.length);
                g2.setColor(Color.red);
                g2.drawPolyline(axisX, imagePart, imagePart.length);

            } else if (GlobalVariables.numberOfChannel == 3) {

                // Действительная, мнимая части и огибающая
                g2.scale(GlobalVariables.scaleOfAxisX, 1);
                g2.setStroke(thickPen);
                g2.setColor(Color.blue);
                g2.drawPolyline(axisX, realPart, realPart.length);
                g2.setColor(Color.red);
                g2.drawPolyline(axisX, imagePart, imagePart.length);
                g2.setColor(Color.black);
                g2.drawPolyline(axisX, envelope, envelope.length);

            }
        } else if (GlobalVariables.displayWindow == 1) {

            // Отображение спектрограммы
            g2.setStroke(thinPen);
            g2.scale(GlobalVariables.scaleOfAxisX * 2, 1);
            g2.setColor(Color.black);
            g2.drawPolyline(axisX, fftData, fftData.length);
        }
    }

//    @Override
//<<<<<<< HEAD
//=======
    /**
     * Делает перенос спектра на нулевую частоту.
     * Взят почти без изменений из зелёной.
     *
     * @param inData double[] данные для переноса
     */
    private void fftwShift(double[] inData) {
        double[] outData = new double[inData.length];
        int kk = inData.length / 2, iii = 0;

        for (int jj = 0; jj < inData.length; jj++) {
            if (jj == inData.length / 2) {
                iii = 0;
                kk = 0;
                outData[iii + kk] = inData[jj];
                iii++;
            } else {
                outData[iii + kk] = inData[jj];
                iii++;
            }
        }

        System.arraycopy(outData, 0, inData, 0, outData.length);
    }
    
    /**
     * Находим модули комплексных чисел без корня и умножаем на погрешность окна
     *
     * @param values значения для преобразования
     * @return мощность спектра
     */
    private double[] powerOfSpectrum(double[] values) {
        double[] power = new double[values.length / 2];

        for (int i = 0, j = 0; j < values.length; ++i, j += 2) {
            power[i] = (values[j] * values[j] + values[j + 1] * values[j + 1]);
        }

        return power;
    }
    
//    @Override
//>>>>>>> 076d33c9fec0f138a8a6a485275b75c620b88260
    public void mousePressed(MouseEvent e) {

        if (GlobalVariables.displayEnable == true) {
            if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) == 0) {
                return;
            }
            coordinatesOfMouseOnAxisX = e.getX();
            coordinatesOfMouseOnAxisY = e.getY();

            if (GlobalVariables.displayWindow == 0) {
                GlobalVariables.rulerOfAxisX = (int) Math.round(coordinatesOfMouseOnAxisX / GlobalVariables.scaleOfAxisX);
                GlobalVariables.rulerOfAxisY = (int) Math.round((panelHeight / 2 - realPart[(int) Math.round(coordinatesOfMouseOnAxisX / GlobalVariables.scaleOfAxisX)]) / GlobalVariables.scaleOfAxisY - GlobalVariables.shiftAxisY / GlobalVariables.scaleOfAxisY);
            } else if (GlobalVariables.displayWindow == 1) {
                GlobalVariables.rulerOfAxisX = (int) Math.round(96000 / N * coordinatesOfMouseOnAxisX / (GlobalVariables.scaleOfAxisX) / 2);
                GlobalVariables.rulerOfAxisY = (int) Math.round((panelHeight / 2 - realPartForSpectrumAnalyzer[(int) Math.round(coordinatesOfMouseOnAxisX / GlobalVariables.scaleOfAxisX)]) / (GlobalVariables.scaleOfAxisY * 0.01) - GlobalVariables.shiftAxisY / (GlobalVariables.scaleOfAxisY * 0.008));
            }
        }
    }

    public int maximumAmpl(int[] arr) {

        int max = 0;

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < max) {
                max = arr[i];
            }
        }
        return max;
    }

    public double maximumAmpl(double[] arr) {

        double max = 0;

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < max) {
                max = arr[i];
            }
        }
        return max;
    }

    public void CSVRecorder() {

        if (GlobalVariables.displayWindow == 0) {

            int i = 0;
            String str1, str2;
            while (i < realPartForSpectrumAnalyzer.length) {
                try {
                    FileWriter writer = new FileWriter("Signal" + new SimpleDateFormat("yyyy.MM.dd_HH-mm-ss").format(new Date()) + ".csv");
                    writer.append("Real;Image\n");
                    for (i = 0; i < realPartForSpectrumAnalyzer.length; i++) {
                        str1 = Integer.toString((int) round((panelHeight / 2 - realPart[i]) / GlobalVariables.scaleOfAxisY - GlobalVariables.shiftAxisY / GlobalVariables.scaleOfAxisY));
                        str2 = Integer.toString((int) round((panelHeight / 2 - imagePart[i]) / GlobalVariables.scaleOfAxisY - GlobalVariables.shiftAxisY / GlobalVariables.scaleOfAxisY));
                        writer.write(str1 + ";" + str2);
                        writer.append('\n');
                    }
                    LOG.info("Save signal: " + new SimpleDateFormat("yyyy.MM.dd_HH-mm-ss").format(new Date()) + ".csv");
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (GlobalVariables.displayWindow == 1) {
            int i = 0;
            String str;
            while (i < DataForSpectrumAnalyzer.length) {
                try {
                    FileWriter writer = new FileWriter("Spectr" + new SimpleDateFormat("yyyy.MM.dd_HH-mm-ss").format(new Date()) + ".csv");
                    writer.append("Spectrum\n");
                    for (i = 0; i < DataForSpectrumAnalyzer.length; i++) {
                        str = Integer.toString((int) round(DataForSpectrumAnalyzer[i]));
                        writer.write(str);
                        writer.append('\n');
                    }
                    LOG.info("Save spectrum: " + new SimpleDateFormat("yyyy.MM.dd_HH-mm-ss").format(new Date()) + ".csv");
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        setToolTipText("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 494, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 392, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables



    /**
     * @see ISignalStreamListener
     */

    /**
     * Источник данных.
     */
   // private AbstractSignalSource signalSource = null;

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
}
