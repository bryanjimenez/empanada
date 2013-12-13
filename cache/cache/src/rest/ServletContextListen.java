package rest;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import cache_controller.FileReader;

public class ServletContextListen implements ServletContextListener {

    private FileReader fileCheck = null;
	private Thread checkerThread = null;

    public void contextInitialized(ServletContextEvent sce) {
    	// Start file checker
        if ((checkerThread == null) || (!checkerThread.isAlive())) {
        	fileCheck = new FileReader();
        	checkerThread = new Thread(fileCheck);
        	checkerThread.start();
        }
    }

    public void contextDestroyed(ServletContextEvent sce){
    	// Stop file checker
    	fileCheck.finish();
    	checkerThread.interrupt();
    }
}