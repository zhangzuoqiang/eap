package eap.um.ui.mirror;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Service;

/**
 * <p> Title: </p>
 * <p> Description: </p>
 * @作者 chiknin@gmail.com
 * @创建时间 
 * @版本 1.00
 * @修改记录
 * <pre>
 * 版本       修改人         修改时间         修改内容描述
 * ----------------------------------------
 * 
 * ----------------------------------------
 * </pre>
 */
@Service("umMirrorRepository")
public class UMMirrorRepository {
	
	private Map<String, UMMirror> umMirrors = new ConcurrentHashMap<String, UMMirror>();
	private ReentrantLock lock = new ReentrantLock();
	
	public UMMirror getMirror(String umServer, boolean createForNotFound) {
		if (umServer == null || umServer.length() == 0) {
			return null;
		}
		
		try {
			lock.lock();
			UMMirror umMirror = umMirrors.get(umServer);
			if (umMirror == null) {
				if (createForNotFound) {
					try {
						umMirror = new UMMirror(umServer);
						umMirrors.put(umServer, umMirror);
					} catch (Exception e) {
						throw new IllegalArgumentException(e.getMessage(), e);
					}
				}
			}
			return umMirror;
		} finally {
			lock.unlock();
		}
	}
	
	public void shutdownMirror(String umServer) {
		UMMirror umMirror = umMirrors.get(umServer);
		if (umMirror != null) {
			umMirror.shutdown();
		}
	}
	
	@PreDestroy
	public void shutdown() {
		for (UMMirror umMirror : umMirrors.values()) {
			umMirror.shutdown();
		}
	}
}
