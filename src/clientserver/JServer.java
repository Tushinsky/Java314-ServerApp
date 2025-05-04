package clientserver;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;

public class JServer {
    private static final int PORT = 8192;// номер порта, который будет прослушивать сервер
    // шаблоны сообщений сервера
    private static final String MSG = "Клиент {'%d'} передал сообщение:\n\r";
    private static final String CLOSE_MESSAGE = "Клиент {'%d'} закрыл соединение\n\r";
    private static final String OPEN_MESSAGE = "\n\nПринят клиент ID={'%d'}\n\r";
    private static final LinkedList<ServerSomething> serverList = new LinkedList<>();
    
    
    public static void main(String[] args) {
        ServerSocket srvSocket = null;// создаём канал сервера
        try {
            try {
                // получаем IP адрес локальгого компьютера (если сервер расположен на локальной машине)
                InetAddress ia = InetAddress.getByName("localhost");
                srvSocket = new ServerSocket(PORT, 0, ia);// создаём канал
                System.out.println("Сервер запущен");
                // запускается бесконечный цикл ожидания подключения клиентов
                while (true) {
                    Socket socket = srvSocket.accept();// создаём канал для принятия данных
                    serverList.add(new ServerSomething(socket));
                    System.out.printf(OPEN_MESSAGE, 
                            serverList.get(serverList.size() - 1).getId());
                    
                }
            } catch (IOException ex) {
                System.out.println("Исключение: " + ex);
            }


        } finally {
            // в случае ошибки закрываем созданный канал
            try{
                if(srvSocket != null) {
                    srvSocket.close();
                }
            } catch (IOException ex) {
                System.out.println("Исключение: " + ex);
            }
        }
    }
    
    private static class ServerSomething extends Thread {
        private final Socket socket;// канал связи
        private final BufferedReader bufIn;// объект чтения из потока ввода
        private final BufferedWriter bufOut;// объект записи в поток вывода
        
        public ServerSomething(Socket socket) throws IOException {
            this.socket = socket;
            // если потоки ввода/вывода приведут к генерированию исключения, оно
            // пробросится дальше; для потоков задаём кодировку символов
            bufIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), 
                    Charset.forName("windows-1251")));
            bufOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), 
                    Charset.forName("windows-1251")));
            start();
        }

        @Override
        public void run() {
            String word;
            try {
                // первое - имя клиента
                word = bufIn.readLine();
                send("Hello, " + word);// выталкиваем данные и очищаем поток
                this.setName(word);// задаём имя потоку
                // передаём список подключенных соединений
                sendClientConnections();
                
                try {
                    while (true) {
                        word = bufIn.readLine();
                        System.out.printf(MSG, this.getId());
                        System.out.println(word);
                        if(word.equalsIgnoreCase("quit")) {
                            this.downService();
                            break;
                        }
                        /*
                        проверяем сообщение на индивидуальность - если передаётся
                        код контакта, то сообщение передаём только ему, иначе
                        передаём в общий чат
                        */
                        if(!sendToContactID(word)) {
                            
                            sendMSGEveryone(word);
                        }
                    }
                } catch (NullPointerException ex) {
                    
                }
            } catch (IOException ex) {
                
            }
        }

        /**
         * Отправляет сообщение
         * @param word текст сообщения
         */
        private void send(String word) {
            try {
                bufOut.write(word + "\n");
                bufOut.flush();
            } catch (IOException ex) {
                
            }
        }

        /**
         * Закрывает текущее соединение, прерывает выполнение потока
         */
        private void downService() {
            try {
                if(!socket.isClosed()) {
                    socket.close();
                    bufIn.close();
                    bufOut.close();
                    for(ServerSomething vr : JServer.serverList) {
                        // извещаем других пользователей о закрытии соединения
                        if(!vr.equals(this)) {
                            vr.send("\t" + this.getName() + " {" + this.getId() + "} закрыл соединение\n");
                            System.out.printf(CLOSE_MESSAGE, this.getId());
                        }
                        
                    }
                    this.interrupt();// прерываем поток
                    JServer.serverList.remove(this);// удаляем из списка
                    if(JServer.serverList.isEmpty()) {
                        System.exit(0);// завершаем работу
                    }
                }
            } catch (IOException ex) {
                
            }
        }
        
        /**
         * Передаёт список существующих подключений новому клиенту
         */
        private void sendClientConnections() {
            // передаём перечень клиентов, если в сети находистя больше одного
            if(JServer.serverList.size() > 1) {
                send("В сети:");
                for (ServerSomething vr : JServer.serverList) {
                    if(!vr.equals(this)) {
                        vr.send("Подключился:\t" + this.getName() + " ID=" + this.getId());
                        send("Name:\t" + vr.getName() + " ID=" + vr.getId());
                    }
                }
            }
        }
        
        /**
         * Передаёт сообщение выбранному контакту
         * @param word сообщение, которое передаётся
         * @return true, если сообщение передано удачно, иначе false
         */
        private boolean sendToContactID(String word) {
            int pos = word.indexOf(":");// получаем первое вхождение символа ":"
            try {
                long id = Long.parseLong(word.substring(0, pos));// код контакта (идентификатор потока)
                System.out.println("contactId=" + id);
                if(id == 0) {
                    // если код равен 0 (сообщение в общий чат)
                    return false;
                }
                for (ServerSomething vr : JServer.serverList) {
                    if(vr.getId() == id) {
                        // передаём сообщение в эту нить
                        vr.send("\t" + word.substring(pos + 1));
                        break;// завершаем цикл
                    }
                }
            } catch (NumberFormatException ex) {
                // ошибка может выскочить, если не передан код контакта
                // или передан неверный код, который не может быть преобразован
                return false;
            }
            return true;
        }
        
        /**
         * Передаёт сообщение в общий чат
         * @param word сообщение для передачи
         */
        private void sendMSGEveryone(String word) {
            // разбор сообщения
            int pos = word.indexOf(":");// получаем первое вхождение символа ":"
            /*
            0, 1, 2 - ник пользователя и время передачи сообщения, 4 - текст сообщения;
            в качестве признака сообщения для общего чата может быть передан 0
            ничего или любой набор символов
            */
            String message = word.substring(pos + 1);
            for (ServerSomething vr : JServer.serverList) {
                if(!vr.equals(this)) vr.send("\t" + message);
            }
        }
    }
}
