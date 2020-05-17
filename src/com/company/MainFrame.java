package com.company;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Pattern;
import javax.swing.*;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

    private static final String FRAME_TITLE = "Клиент мгновенных сообщений";
    private static final int FRAME_MINIMUM_WIDTH = 500;
    private static final int FRAME_MINIMUM_HEIGHT = 500;
    private static final int FROM_FIELD_DEFAULT_COLUMNS = 10;
    private static final int TO_FIELD_DEFAULT_COLUMNS = 20;
    private static final int INCOMING_AREA_DEFAULT_ROWS = 10;
    private static final int OUTGOING_AREA_DEFAULT_ROWS = 5;
    private static final int SMALL_GAP = 5;
    private static final int MEDIUM_GAP = 10;
    private static final int LARGE_GAP = 15;
    private static final int SERVER_PORT = 4567;
    private static final String IP_Adres =
            "^([01]?\\d\\d?|2[0-4]\\d[0-5])\\"+
            ".([01]?\\d\\d?|[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."+
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    private final JTextField textFieldFrom;
    private final JTextField textFieldTo;
    private final JTextArea textAreaIncoming;
    private final JTextArea textAreaOutgoing;
    private JCheckBox bold;
    private JCheckBox italic;
    private static final int FONTSIZE = 12;

    public MainFrame() {
        super(FRAME_TITLE);

        KeyEventDispatcher keyEventDispatcher = new KeyEventDispatcher() {
            @Override
           public boolean dispatchKeyEvent(final KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER&& e.isControlDown()) {

                    sendMessage();
                }
                return false;
            }
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventDispatcher);

        setMinimumSize(
                new Dimension(FRAME_MINIMUM_WIDTH, FRAME_MINIMUM_HEIGHT));

        addKeyListener(new KeyAdapter() {

            public void keyReleased(KeyEvent event) {
                if (event.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
                    sendMessage();
                }

            }
        });

// Центрирование окна
        final Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - getWidth()) / 2,
                (kit.getScreenSize().height - getHeight()) / 2);
// Текстовая область для отображения полученных сообщений
        textAreaIncoming = new JTextArea(INCOMING_AREA_DEFAULT_ROWS, 0);
        textAreaIncoming.setEditable(false);
// Контейнер, обеспечивающий прокрутку текстовой области
        final JScrollPane scrollPaneIncoming =
                new JScrollPane(textAreaIncoming);
// Подписи полей
        final JLabel labelFrom = new JLabel("От");
        final JLabel labelTo = new JLabel("Получатель");
// Поля ввода имени пользователя и адреса получателя
        textFieldFrom = new JTextField(FROM_FIELD_DEFAULT_COLUMNS);

        textFieldTo = new JTextField(TO_FIELD_DEFAULT_COLUMNS);
// Текстовая область для ввода сообщения
        textAreaOutgoing = new JTextArea(OUTGOING_AREA_DEFAULT_ROWS, 0);
// Контейнер, обеспечивающий прокрутку текстовой области
        final JScrollPane scrollPaneOutgoing =
                new JScrollPane(textAreaOutgoing);

// Панель ввода сообщения
        final JPanel messagePanel = new JPanel();


        messagePanel.setBorder(
                BorderFactory.createTitledBorder("Сообщение"));
// Кнопка отправки сообщения


        ActionListener listener = new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                int mode = 0;
                if(bold.isSelected()) mode += Font.BOLD;
                if(italic.isSelected()) mode += Font.ITALIC;
                textAreaIncoming.setFont(new Font("Serif", mode, FONTSIZE));
            }
        };

        final JCheckBox bold = new JCheckBox("Bold");
        bold.addActionListener(listener);

        final JCheckBox italic = new JCheckBox("Italic");
        italic.addActionListener(listener);

        final JButton sendButton = new JButton("Отправить");
        sendButton.setFocusable(false);


    sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
// Компоновка элементов панели "Сообщение"
        final GroupLayout layout2 = new GroupLayout(messagePanel);
        messagePanel.setLayout(layout2);
        messagePanel.setFocusable(false);


        layout2.setHorizontalGroup(layout2.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout2.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addGroup(layout2.createSequentialGroup()
                                .addComponent(labelFrom)
                                .addGap(SMALL_GAP)
                                .addComponent(textFieldFrom)
                                .addGap(LARGE_GAP)
                                .addComponent(labelTo)
                                .addGap(SMALL_GAP)
                                .addComponent(textFieldTo))
                        .addComponent(bold)
                        .addComponent(italic)
                        .addComponent(scrollPaneOutgoing)
                        .addComponent(sendButton))
                .addContainerGap());
        layout2.setVerticalGroup(layout2.createSequentialGroup()
                .addContainerGap()

                .addGroup(layout2.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelFrom)
                        .addComponent(textFieldFrom)
                        .addComponent(labelTo)
                        .addComponent(textFieldTo))
                .addComponent(bold  )
                .addComponent(italic)

                .addGap(MEDIUM_GAP)

                .addComponent(scrollPaneOutgoing)

                .addGap(MEDIUM_GAP)
                .addComponent(sendButton)

                .addContainerGap());
// Компоновка элементов фрейма
        final GroupLayout layout1 = new GroupLayout(getContentPane());
        setLayout(layout1);
        layout1.setHorizontalGroup(layout1.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout1.createParallelGroup()
                        .addComponent(scrollPaneIncoming)
                        .addComponent(messagePanel))
                .addContainerGap());
        layout1.setVerticalGroup(layout1.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneIncoming)
                .addGap(MEDIUM_GAP)
                .addComponent(messagePanel)
                .addContainerGap());
// Создание и запуск потока-обработчика запросов
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ServerSocket serverSocket =
                            new ServerSocket(SERVER_PORT);
                    while (!Thread.interrupted()) {
                        final Socket socket = serverSocket.accept();
                        final DataInputStream in = new DataInputStream(
                                socket.getInputStream());
// Читаем имя отправителя
                        final String senderName = in.readUTF();
// Читаем сообщение
                        final String message = in.readUTF();

// Закрываем соединение
                        socket.close();
// Выделяем IP-адрес
                        final String address =
                                ((InetSocketAddress) socket
                                        .getRemoteSocketAddress())
                                        .getAddress()
                                        .getHostAddress();
// Выводим сообщение в текстовую область


                        textAreaIncoming.append(senderName +
                                " (" + address + "): " +
                                message + "\n");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Ошибка в работе сервера", "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }).start();
    }

    private void sendMessage() {
        try {
// Получаем необходимые параметры
            final String senderName = textFieldFrom.getText();
            final String destinationAddress = textFieldTo.getText();
            final String message = textAreaOutgoing.getText();
// Убеждаемся, что поля не пустые
            if (senderName.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Введите имя отправителя", "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (destinationAddress.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Введите адрес узла-получателя", "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (message.isEmpty()) {
               // JOptionPane.showMessageDialog(this,
                     //   "Введите текст сообщения", "Опа!",
                   //     JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (Pattern.matches(IP_Adres,destinationAddress) == false){
                JOptionPane.showMessageDialog(this,
                        "Неправильный IP", "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
            }
// Создаем сокет для соединения
            final Socket socket =
                    new Socket(destinationAddress, SERVER_PORT);
// Открываем поток вывода данных
            final DataOutputStream out =
                    new DataOutputStream(socket.getOutputStream());
// Записываем в поток имя
            out.writeUTF(senderName);
// Записываем в поток сообщение
            out.writeUTF(message);
// Закрываем сокет
            socket.close();

// Помещаем сообщения в текстовую область вывода
            textAreaIncoming.append("Я -> " + destinationAddress + ": "
                    + message+ "\n");
// Очищаем текстовую область ввода сообщения
            textAreaOutgoing.setText("");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.this,
                    "Не удалось отправить сообщение: узел-адресат не найден",

                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(MainFrame.this,
                    "Не верный адрес получателя, возможно вы хотели указать 127.0.0.1?", "Вот это прикол",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                final MainFrame frame = new MainFrame();

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);


            }
        });
    }
}