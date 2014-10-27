package eap;

import java.util.Arrays;

import junit.framework.TestCase;
import eap.um.UM;

public class UMTest extends TestCase {
	
	public void echo(String[] args) {
		System.out.println(Arrays.asList(args));
	}
	public void echo() {
		System.out.println("echo");
	}
	
	public void testStartApp1() throws Exception {
		System.setProperty("app.UMServer", "127.0.0.1:2181");
		System.setProperty("app.name", "test3");
		System.setProperty("app.id", "1");
		System.setProperty("app.version", "1.0.3");
		
		UM.start();
		
		UM.setNodeData(UM.envPath, "{\"a\":\"2\"}".getBytes());
		
//		System.out.println(111111111);
//		UM.setNodeData(UM.serverCliPath, "eap.UMTest echo a \"b c\" d e".getBytes());
		
		Thread.sleep(60000);
		
		UM.stop();
		
//		UM.setNodeData(UM.serverCliPath, "".getBytes());
	}
}