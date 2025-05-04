package clientserver;


import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JClient {
    private static final int SERVER_PORT = 8192;// номер канала для связи с сервером
    private static final String LOCAL_HOST = "127.0.0.1";// IP адрес компьютера
    
    public static void main(String[] args) {
        try {
            new ClientSomething(SERVER_PORT, LOCAL_HOST);
        } catch (IOException ex) {
            Logger.getLogger(JClient.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }
    
    
    
    
    static class ClientSomething {
        private int SERVER_PORT;// номер канала для связи с сервером
        private String LOCAL_HOST;// IP адрес компьютера
        private Socket socket;
        private BufferedReader dis;
        private BufferedWriter dos;
        private BufferedReader keyBoard;
        private String nickName;// имя клиента
        private Date time;// время передачи сообщения
        private String dtime;// строковое представление времени передачи
        private SimpleDateFormat dtl;// формат времени
        
        public ClientSomething(int SERVER_PORT, String LOCAL_HOST) throws IOException {
            this.SERVER_PORT = SERVER_PORT;
            this.LOCAL_HOST = LOCAL_HOST;
            System.out.println("Добро пожаловать на клиентскую сторону\n");
            InputStreamReader isr = new InputStreamReader(System.in, 
                    Charset.forName("windows-1251"));
            keyBoard = new BufferedReader(isr);// объект чтения с клавиатуры
            String line;
            System.out.println("Напишите и нажмите Enter");
            line = keyBoard.readLine();// читаем с клавиатуры
            if(line.equalsIgnoreCase("open")) {
                // открываем соединение с сервером
                if(openConnectToServer()) {
                    // если соединение открыто, запуыскаем обмен данными
                    try {
                        // создаём потоки для чтения/записи в канал связи и классы для них
                        dis = new BufferedReader(new InputStreamReader(socket.getInputStream(), 
                                Charset.forName("windows-1251")));
                        dos = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), 
                                Charset.forName("windows-1251")));
                        this.pressNickname();// запрос на ввод имени
                        new ReadMsg().start();// нить, читающая сообщения из сокета
                        new WriteMsg().start();// нить, пишущая сообщения в сокет
                    } catch (IOException ex) {
                        // выводим отладочную информацию
                        Logger.getLogger(JClient.class.getName()).log(Level.SEVERE, null, ex);
                        // сокет должен быть закрыт при любой ошибке
                        ClientSomething.this.downService();
                    }
                }
            } else if(line.equalsIgnoreCase("quit")) {
                System.exit(0);// заканчиваем работу приложения
            }
            
        }
        
        /**
         * ОТкрывает соединение с сервером
         * @param socket канал соединения
         * @throws IOException исключительная ситуация
         */
        private boolean openConnectToServer() throws IOException {
            try {
                System.out.println("Подключение к серверу\n\t(IP адрес " + LOCAL_HOST + ")" +
                            " порт" + SERVER_PORT + ")");
                InetAddress ipAddress = InetAddress.getByName(LOCAL_HOST);
                socket = new Socket(ipAddress, SERVER_PORT);
                if(socket.isConnected()) {
                    // проверка соединения
                    System.out.println("Соединение с сервером установлено:");
                    System.out.println("\tАдрес хоста = " + socket.getInetAddress().getHostAddress() +
                            "\tРазмер буфера = " + socket.getReceiveBufferSize());
                    return true;
                }
                return false;

            } catch (UnknownHostException ex) {
                Logger.getLogger(JClient.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Ошибка подключения к серверу\n\t(IP адрес " + LOCAL_HOST + ")" +
                            " порт" + SERVER_PORT + ")");
                return false;
            }



        }
        
        /**
        * просьба ввести имя,
        * и отсылка эхо с приветсвием на сервер
        */

       private void pressNickname() {
           System.out.print("Press your nick: ");
           try {
               nickName = keyBoard.readLine();
               dos.write(nickName + "\n");
               dos.flush();
           } catch (IOException ignored) {
           }

       }
        private void downService() {
            try {
                if(!socket.isClosed()) {
                    System.out.println("Закрываем соединение с сервером...");
                    socket.close();
                    System.out.println("Соединение закрыто.");
                    dis.close();
                    dos.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(JClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private class ReadMsg extends Thread {
        
            @Override
            public void run() {
                String word;
                try {
                    while(true) {
                        if(!socket.isClosed()) {
                            word = dis.readLine();
                            if(word.equalsIgnoreCase("quit")) {
                                ClientSomething.this.downService();
                                break;
                            }
                            System.out.println(word);
                        } else {
                            break;
                        }
                    }
                } catch (IOException ex) {
//                    Logger.getLogger(JClient.class.getName()).log(Level.SEVERE, null, ex);
                    ClientSomething.this.downService();
                }
            }

        }

        private class WriteMsg extends Thread {

            @Override
            public void run() {
                while(true) {
                    String userWord;
                    try {
                        time = new Date();
                        dtl = new SimpleDateFormat("HH:mm:ss");// берём только время до секунд
                        dtime = dtl.format(time);// получаем время
                        userWord = keyBoard.readLine();// читаем с консоли
                        if(userWord.equalsIgnoreCase("quit")) {
                            dos.write(userWord + "\n");
                            dos.flush();
                            ClientSomething.this.downService();
                            break;// выход из цикла
                        } else {
                            dos.write(getFormatMessage(userWord));// отправляем на сервер
                            dos.flush();// чистим
                        }

                    } catch (IOException ex) {
                        Logger.getLogger(JClient.class.getName()).log(Level.SEVERE, null, ex);
                        ClientSomething.this.downService();
                    }
                }
                
            }

            /**
             * Преобразует сообщение, извлекая код контакта, которому оно передаётся.
             * Найденный код контакта ставится первым в передаваемом сообщении
             * @param userWord сообщение для форматирования
             * @return преобразованное сообщение для передачи
             */
            private String getFormatMessage(String userWord) {
                int pos = userWord.indexOf(":");// первое вхождение символа ":"
                // если вхождение есть, получаем код, в противном случае он равен "0"
                String id;
                StringBuilder retVal = new StringBuilder();
                if(pos == -1) {
                    id = "0:";
                    retVal.append(id).append(nickName).append("(")
                            .append(dtime).append("):\t")
                            .append(userWord).append("\n");
                } else {
                    id = userWord.substring(0, pos) + ":";
                    retVal.append(id).append(nickName).append("(")
                            .append(dtime).append("):\t")
                            .append(userWord.substring(pos + 1)).append("\n");
                }
                
                return retVal.toString();
            }
        }


    }
}
