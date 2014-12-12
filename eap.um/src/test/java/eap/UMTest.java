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
		System.setProperty("app.UMServer", "10.110.1.111:2181"); // 127.0.0.1
		System.setProperty("app.name", "test");
		System.setProperty("app.id", "1");
		System.setProperty("app.version", "1.0.0");
		
		UM.start();
		
		UM.setNodeData(UM.envPath, "{\"a\":\"2\"}".getBytes());
		
//		UM.addListener(UM.envPath, new UM.NodeListener() {
//			public void nodeChanged(CuratorFramework client, ChildData childData) throws Exception {
//				byte[] data = childData != null ? childData.getData() : null;
//				Map<String, String> envMap = null;
//				if (data != null && data.length > 0) {
//					System.out.println(new String(data));
//					envMap = JsonUtil.parseJson(new String(data), LinkedHashMap.class);
//				} else {
//					envMap = new LinkedHashMap<String, String>();
//				}
//				System.out.println(envMap);
//			}
//		});
		
//		System.out.println(111111111);
//		UM.setNodeData(UM.serverCliPath, "eap.UMTest echo a \"b c\" d e".getBytes());
		
		Thread.sleep(6000000);
		
		UM.stop();
		
//		UM.setNodeData(UM.serverCliPath, "".getBytes());
	}
}