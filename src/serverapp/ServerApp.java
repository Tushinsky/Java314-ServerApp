    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverapp;

import clientserver.JClient;
import clientserver.JServer;

/**
 *
 * @author Sergii.Tushinskyi
 */
public class ServerApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        JServer jServer = new JServer();
        JClient firstClient = new JClient();
        JClient secondClient = new JClient();
    }
    
}
