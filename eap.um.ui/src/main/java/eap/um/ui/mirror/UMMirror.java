package eap.um.ui.mirror;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eap.util.JsonUtil;

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
public class UMMirror {
	
	private static final Logger logger = LoggerFactory.getLogger(UMMirror.class);
	
	private CuratorFramework client;
	private NodeWatcher nodeWatcher;
	private Map<String, AppVO> umMirror = new ConcurrentHashMap<String, AppVO>();
	public static final String UM_ROOT = "/EAP_UM/APP";
	
	
	
	public UMMirror(String umServer) throws Exception {
		Integer retryNum = Integer.MAX_VALUE;
		Integer retryTimes = 3000;
		Integer connectionTimeoutMs = 10000;
		
		final CountDownLatch connectedLatch = new CountDownLatch(1);
		client = CuratorFrameworkFactory.builder()
			.connectString(umServer)
			.retryPolicy(new RetryNTimes(retryNum, retryTimes))
			.connectionTimeoutMs(connectionTimeoutMs).build();
		
		client.getCuratorListenable().addListener(new CuratorListener() {
			public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
				if (connectedLatch.getCount() > 0 && event.getWatchedEvent() != null && event.getWatchedEvent().getState() == KeeperState.SyncConnected) {
					connectedLatch.countDown();
				}
			}
		});
		client.getUnhandledErrorListenable().addListener(new UnhandledErrorListener() {
			public void unhandledError(String message, Throwable e) {
				logger.error(message, e);
			}
		});
		client.start();
		connectedLatch.await();
		
		syncUmMirror();
	}
	
	private void syncUmMirror() throws Exception {
		refresh();
		
		nodeWatcher = new NodeWatcher(client, new NodeWatcher.NodeListener() {
			@Override
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event, NodeWatcher watcher) throws Exception {
//				System.out.println("childEvent ->" + event.getData().getPath());
				String path = event.getData().getPath();
				if (PathChildrenCacheEvent.Type.CHILD_REMOVED == event.getType()) {
					watcher.unwatch(path);
					
//					String[] pathArr = null;
//					if (path.matches("^/EAP_UM/APP/[^/]+/runtime/loader/[^/]+$")) {
//						
//					} else if (path.matches("^/EAP_UM/APP/[^/]+/runtime/cli/[^/]+$")) {
//						
//					} else if (path.matches("^/EAP_UM/APP/[^/]+/runtime/server/[^/]+$")) {
//						
//					} else if (path.matches("^/EAP_UM/APP/[^/]+/config/[^/]+/[^/]+$")) { 
//						pathArr = path.split("/");
//						refresh_APP_CONFIG_ATTRS(pathArr[3], pathArr[5], pathArr[6], null, true);
//					} else if (path.matches("^/EAP_UM/APP/[^/]+/config/[^/]+$")) {
//						pathArr = path.split("/");
//						refresh_APP_CONFIG(pathArr[3], pathArr[5], true);
//					} else if (path.matches("^/EAP_UM/APP/[^/]+$")) {
//						pathArr = path.split("/");
//						refresh_APP(pathArr[3], true);
//					}
					refresh();
				} else if (PathChildrenCacheEvent.Type.CHILD_ADDED == event.getType()) {
					watcher.watch(path);
					
//					String[] pathArr = null;
//					if (path.matches("^/EAP_UM/APP/[^/]+/config/[^/]+$")) {
//						pathArr = path.split("/");
//						refresh_APP_CONFIG(pathArr[3], pathArr[5], false);
//					} else if (path.matches("^/EAP_UM/APP/[^/]+$")) {
//						pathArr = path.split("/");
//						refresh_APP(pathArr[3], false);
//					}
					refresh();
				} else if (PathChildrenCacheEvent.Type.CHILD_UPDATED == event.getType()) {
					// nothing
				}
			}
			@Override
			public void nodeChanged(CuratorFramework client, String path, ChildData childData, NodeWatcher watcher) throws Exception {
//				System.out.println("nodeChanged ->" + path);
//				String[] pathArr = null;
//				if (path.matches("^/EAP_UM/APP/[^/]+/config/[^/]+/[^/]+$")) {
//					pathArr = path.split("/");
//					refresh_APP_CONFIG_ATTRS(pathArr[3], pathArr[5], pathArr[6], 
//						(childData != null && childData.getData() != null) ? new String(childData.getData()) : null, 
//						false);
//				}
				refresh();
			}
		});
		nodeWatcher.watch(UM_ROOT);
	}
	private void refresh() throws Exception {
		umMirror.clear();
		
		List<String> _appList = client.getChildren().forPath(UM_ROOT);
		if (_appList != null && _appList.size() > 0) {
			for (String _app : _appList) {
				refresh_APP(_app, false);
			}
		}
	}
	
	private void refresh_APP(String _app, boolean deleteAction) throws Exception {
		if (deleteAction) {
			umMirror.remove(_app);
			return;
		}
		if (umMirror.get(_app) != null) {
			return;
		}
		
		AppVO appVO = new AppVO();
		appVO.setName(_app);
		
		ConfigVO config = new ConfigVO();
		config.setVersions(new HashMap<String, ConfigVersionVO>());
		appVO.setConfig(config);
		
		RuntimeVO runtimeVO = new RuntimeVO();
		appVO.setRuntime(runtimeVO);
		
		umMirror.put(appVO.getName(), appVO);
		
		String _APP_PATH = UM_ROOT + "/" + _app;
		
		List<String> _configList = client.getChildren().forPath(_APP_PATH + "/config");
		if (_configList != null && _configList.size() > 0) {
			for (String _version : _configList) {
				refresh_APP_CONFIG(_app, _version, false);
			}
		}
		
		String _APP_RUNTIME_PATH = _APP_PATH + "/runtime";
		try {
			List<String> _loaderList = client.getChildren().forPath(_APP_RUNTIME_PATH + "/loader");
			if (_loaderList != null && _loaderList.size() > 0) {
				byte[] _loaderByte = client.getData().forPath(_APP_RUNTIME_PATH + "/loader/" + _loaderList.get(0));
				runtimeVO.setLoader(_loaderByte != null ? new String(_loaderByte) : null);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		try {
			List<String> _cliList = client.getChildren().forPath(_APP_RUNTIME_PATH + "/cli");
			Map<String, String> cliList = new ConcurrentHashMap<String, String>();
			if (_cliList != null && _cliList.size() > 0) {
				for (String _server : _cliList) {
					byte[] _cliByte = client.getData().forPath(_APP_RUNTIME_PATH + "/cli/" + _server);
					cliList.put(_server, _cliByte != null ? new String(_cliByte) : null);
				}
			}
			runtimeVO.setCliList(cliList);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		try {
			List<String> _serverList = client.getChildren().forPath(_APP_RUNTIME_PATH + "/server");
			Map<String, Map<String, String>> serverList = new ConcurrentHashMap<String, Map<String,String>>();
			if (_serverList != null && _serverList.size() > 0) {
				for (String _server : _serverList) {
					Map<String, String> attrs = Collections.EMPTY_MAP;
					byte[] _attrsByte = client.getData().forPath(_APP_RUNTIME_PATH + "/server/" + _server);
					if (_attrsByte != null && _attrsByte.length > 0) {
						attrs = JsonUtil.parseJson(new String(_attrsByte), HashMap.class);
					}
					serverList.put(_server, attrs);
				}
			}
			runtimeVO.setServerList(serverList);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		
	}
	private void refresh_APP_CONFIG(String _app, String _version, boolean deleteAction) throws Exception {
		AppVO appVO = umMirror.get(_app);
		if (appVO == null) {
			return;
		}
		
		if (deleteAction) {
			appVO.getConfig().getVersions().remove(_version);
			return;
		}
		if (appVO.getConfig().getVersions().get(_version) != null) {
			return;
		}
		
		
		String _APP_CONFIG_VERSION_PATH = UM_ROOT + "/" + _app + "/config/" + _version;
		
		ConfigVersionVO version = new ConfigVersionVO();
		Map<String, String> attrs = new HashMap<String, String>();
		version.setAttrs(attrs);
		version.setVersion(_version);
		appVO.getConfig().getVersions().put(_version, version);
		
		List<String> _keyList = client.getChildren().forPath(_APP_CONFIG_VERSION_PATH);
		if (_keyList != null && _keyList.size() > 0) {
			for (String _key : _keyList) {
				String _APP_CONFIG_VERSION_KEY_PATH = _APP_CONFIG_VERSION_PATH + "/" + _key;
				byte[] _valueByte = client.getData().forPath(_APP_CONFIG_VERSION_KEY_PATH);
				refresh_APP_CONFIG_ATTRS(_app, _version, _key, _valueByte != null ? new String(_valueByte) : null, deleteAction);
			}
		}
	}
	private void refresh_APP_CONFIG_ATTRS(String _app, String _version, String _key, String _value, boolean deleteAction) throws Exception {
		AppVO appVO = umMirror.get(_app);
		if (appVO == null) {
			return;
		}
		ConfigVersionVO configVersionVO = appVO.getConfig().getVersions().get(_version);
		if (configVersionVO == null) {
			return;
		}
		
		if (deleteAction || _value == null) {
			configVersionVO.getAttrs().remove(_key);
			return;
		}
		
		configVersionVO.getAttrs().put(_key, _value);
	}

	public void shutdown() {
		if (client != null && client.getState() == CuratorFrameworkState.STARTED) {
			client.close();
		}
	}
	
	/* 
	 * ====================
	 * API
	 * ====================
	 */
	public List<Map<String, Object>> getAppList() {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		for (AppVO appVO : umMirror.values()) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("appName", appVO.getName());
			if (appVO.getRuntime() != null && appVO.getRuntime().getServerList() != null) {
				item.put("serverCount", appVO.getRuntime().getServerList().size());
			} else {
				item.put("serverCount", 0);
			}
			list.add(item);
		}
		return list;
	}
	public List<Map<String, Object>> getAppServerList(String appName) {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		AppVO appVO = umMirror.get(appName);
		if (appVO != null) {
			Map<String, Map<String, String>> serverList = appVO.getRuntime().getServerList();
			if (serverList != null && serverList.size() > 0) {
				for (Entry<String, Map<String, String>> entry : serverList.entrySet()) {
					Map<String, Object> item = new HashMap<String, Object>();
					item.put("server", entry.getKey());
					Map<String, String> attrs = entry.getValue();
					if (attrs != null && attrs.size() > 0) {
						item.put("appId", attrs.get("app.id"));
						item.put("appVersion", attrs.get("app.version"));
						item.put("startTime", attrs.get("app.startTime"));
					}
					list.add(item);
				}
			}
		}
		return list;
	}
	public List<Map<String, String>> getAppConfig(String appName, String appVersion) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		AppVO appVO = umMirror.get(appName);
		if (appVO != null) {
			ConfigVersionVO configVersionVO = appVO.getConfig().getVersions().get(appVersion);
			if (configVersionVO != null) {
				Map<String, String> attrs = configVersionVO.getAttrs();
				if (attrs != null && attrs.size() > 0) {
					for (Entry<String, String> attr : attrs.entrySet()) {
						Map<String, String> item = new HashMap<String, String>();
						item.put("key", attr.getKey());
						item.put("value", attr.getValue());
						list.add(item);
					}
				}
			}
		}
		return list;
	}
	public List<Map<String, String>> getAppVersionList(String appName) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		AppVO appVO = umMirror.get(appName);
		if (appVO != null) {
			for (String version : appVO.getConfig().getVersions().keySet()) {
				Map<String, String> item = new HashMap<String, String>();
				item.put("version", version);
				list.add(item);
			}
		}
		return list;
	}
	public List<Map<String, Object>> getAppServerCliList(String appName) {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		AppVO appVO = umMirror.get(appName);
		if (appVO != null) {
			Map<String, Map<String, String>> serverList = appVO.getRuntime().getServerList();
			if (serverList != null && serverList.size() > 0) {
				for (Entry<String, Map<String, String>> entry : serverList.entrySet()) {
					Map<String, Object> item = new HashMap<String, Object>();
					item.put("server", entry.getKey());
					Map<String, String> attrs = entry.getValue();
					if (attrs != null && attrs.size() > 0) {
						item.put("appId", attrs.get("app.id"));
						item.put("appVersion", attrs.get("app.version"));
						item.put("startTime", attrs.get("app.startTime"));
						
						String cli = appVO.getRuntime().getCliList().get(entry.getKey());
						if (cli != null && cli.length() > 0) {
							String[] cliArray = cli.split("\r\n");
							if (cliArray.length == 1) {
								item.put("cliExecStatus", "InProgress");
							} else {
								item.put("cliExecStatus", cliArray[1]);
							}
							item.put("cliContent", cli);
						} else {
							item.put("cliExecStatus", "Idle");
							item.put("cliContent", "");
						}
					}
					list.add(item);
				}
			}
		}
		return list;
	}
	public void execCmd(String appName, String[] servers, String cmd) {
		if (servers == null || servers.length == 0 || cmd == null || cmd.length() == 0) {
			return;
		}
		
		AppVO appVO = umMirror.get(appName);
		if (appVO != null) {
			for (String server : servers) {
				try {
					client.setData().forPath("/EAP_UM/APP/" + appName + "/runtime/cli/" + server, cmd.getBytes());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}
	/* 
	 * ====================
	 * VO
	 * ====================
	 */
	
	class AppVO {
		private String name;
		private ConfigVO config;
		private RuntimeVO runtime;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public ConfigVO getConfig() {
			return config;
		}
		public void setConfig(ConfigVO config) {
			this.config = config;
		}
		public RuntimeVO getRuntime() {
			return runtime;
		}
		public void setRuntime(RuntimeVO runtime) {
			this.runtime = runtime;
		}
		public String toString() {
			return "[CONFIG => " + config + ", RUNTIME => " + runtime + "]"; 
		}
	}
	class ConfigVO {
		private Map<String, ConfigVersionVO> versions;

		public Map<String, ConfigVersionVO> getVersions() {
			return versions;
		}
		public void setVersions(Map<String, ConfigVersionVO> versions) {
			this.versions = versions;
		}

		public String toString() {
			return "" + versions;
		}
	}
	class ConfigVersionVO {
		private String version;
		private Map<String, String> attrs;
		
		public String getVersion() {
			return version;
		}
		public void setVersion(String version) {
			this.version = version;
		}
		public Map<String, String> getAttrs() {
			return attrs;
		}
		public void setAttrs(Map<String, String> attrs) {
			this.attrs = attrs;
		}
		
		public String toString() {
			return attrs + "";
		}
	}
	class RuntimeVO {
		private String loader;
		private Map<String, String> cliList;
		private Map<String, Map<String, String>> serverList;
		
		public String getLoader() {
			return loader;
		}
		public void setLoader(String loader) {
			this.loader = loader;
		}
		public Map<String, String> getCliList() {
			return cliList;
		}
		public void setCliList(Map<String, String> cliList) {
			this.cliList = cliList;
		}
		public Map<String, Map<String, String>> getServerList() {
			return serverList;
		}
		public void setServerList(Map<String, Map<String, String>> serverList) {
			this.serverList = serverList;
		}
		
		public String toString() {
			return "loader -> " + loader + ", cli -> " + cliList + ", server -> " + serverList; 
		}
	}
	
	public static void main(String[] args) throws Exception {
		String s = "/EAP_UM/APP/com.enci.open/runtime/loader/a-b-c:123";
		if (s.matches("^/EAP_UM/APP/[^/]+/runtime/loader/[^/]+$")) { // \u0088
			System.out.println("123");
		}
		System.out.println((int)'-');
		System.out.println(Integer.parseInt("45", 16));
		
//		UMMirror mirror = new UMMirror("10.1.43.153:2181");
//		System.out.println(mirror.umMirror);
//		System.out.println(mirror.getAppConfig("com.enci.open", "1.0.0"));;
		
	}
}