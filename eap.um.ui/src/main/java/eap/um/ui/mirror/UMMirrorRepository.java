package eap.um.ui.mirror;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
//	private ReentrantLock lock = new ReentrantLock();
	private ReadWriteLock lock = new ReentrantReadWriteLock();
	
	public UMMirror getMirror(String umServer, boolean createForNotFound) {
		if (umServer == null || umServer.length() == 0) {
			return null;
		}
		
		UMMirror umMirror = null;
		try {
			
			lock.readLock().lock();
			umMirror = umMirrors.get(umServer);
		} finally {
			lock.readLock().unlock();
		}
		if (umMirror != null) {
			return umMirror;
		}
		
		try {
			lock.writeLock().lock();
			if (createForNotFound) {
				try {
					umMirror = new UMMirror(umServer);
					umMirrors.put(umServer, umMirror);
				} catch (Exception e) {
					throw new IllegalArgumentException(e.getMessage(), e);
				}
			}
		} finally {
			lock.writeLock().unlock();
		}
		
		return umMirror;
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
