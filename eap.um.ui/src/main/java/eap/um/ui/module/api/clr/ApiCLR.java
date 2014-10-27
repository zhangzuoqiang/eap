package eap.um.ui.module.api.clr;

import java.util.Collections;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eap.base.BaseController;
import eap.um.ui.common.P;
import eap.um.ui.mirror.UMMirror;
import eap.um.ui.mirror.UMMirrorRepository;

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
@Controller
public class ApiCLR extends BaseController {

	@Resource(name="umMirrorRepository")
	private UMMirrorRepository umMirrorRepository;
	private UMMirror getUmMirror(String umServer) {
		return umMirrorRepository.getMirror(umServer, true);
	}
	
	@RequestMapping(value=P.API_APP_LIST, method=RequestMethod.GET)
	@ResponseBody
	public Object appList(@RequestParam("umServer") String umServer) {
		UMMirror umMirror = getUmMirror(umServer);
		if (umMirror == null) {
			return Collections.EMPTY_LIST;
		}
		
		return umMirror.getAppList();
	}
	
	@RequestMapping(value=P.API_APP_SERVER_LIST, method=RequestMethod.GET)
	@ResponseBody
	public Object appServerList(
		@RequestParam("umServer") String umServer, 
		@RequestParam("appName") String appName) 
	{
		UMMirror umMirror = getUmMirror(umServer);
		if (umMirror == null || appName == null || appName.length() == 0) {
			return Collections.EMPTY_LIST;
		}
		
		return umMirror.getAppServerList(appName);
	}
	
	@RequestMapping(value=P.API_APP_CONFIG, method=RequestMethod.GET)
	@ResponseBody
	public Object appConfig(
		@RequestParam("umServer") String umServer, 
		@RequestParam("appName") String appName, 
		@RequestParam("appVersion") String appVersion)
	{
		UMMirror umMirror = getUmMirror(umServer);
		if (umMirror == null 
			|| appName == null || appName.length() == 0
			|| appVersion == null || appVersion.length() == 0) {
			return Collections.EMPTY_LIST;
		}
		
		return umMirror.getAppConfig(appName, appVersion);
	}
	
	@RequestMapping(value=P.API_APP_VERSION_LIST, method=RequestMethod.GET)
	@ResponseBody
	public Object appVersionList(
		@RequestParam("umServer") String umServer, 
		@RequestParam("appName") String appName) 
	{
		UMMirror umMirror = getUmMirror(umServer);
		if (umMirror == null || appName == null || appName.length() == 0) {
			return Collections.EMPTY_LIST;
		}
		
		return umMirror.getAppVersionList(appName);
	}
	
	@RequestMapping(value=P.API_APP_SERVER_CLI_LIST, method=RequestMethod.GET)
	@ResponseBody
	public Object appServerCliList(
		@RequestParam("umServer") String umServer, 
		@RequestParam("appName") String appName) 
	{
		UMMirror umMirror = getUmMirror(umServer);
		if (umMirror == null || appName == null || appName.length() == 0) {
			return Collections.EMPTY_LIST;
		}
		
		return umMirror.getAppServerCliList(appName);
	}
	
	@RequestMapping(value=P.API_EXEC_CMD, method=RequestMethod.POST)
	@ResponseBody
	public Object execCmd(
		@RequestParam("umServer") String umServer, 
		@RequestParam("appName") String appName,
		@RequestParam("servers") String[] servers,
		HttpServletRequest request)
	{
		String cmd = request.getParameter("cmd");
		
		UMMirror umMirror = getUmMirror(umServer);
		if (umMirror == null || appName == null || appName.length() == 0 || servers == null || servers.length == 0) {
			return false;
		}
		
		umMirror.execCmd(appName, servers, cmd);
		return true;
	}
	
	@RequestMapping(value=P.API_ADD_APP, method=RequestMethod.POST)
	@ResponseBody
	public Object addApp(
		@RequestParam("umServer") String umServer, 
		@RequestParam("appName") String appName,
		HttpServletRequest request)
	{
		UMMirror umMirror = getUmMirror(umServer);
		if (umMirror == null || appName == null || appName.length() == 0) {
			return false;
		}
		
		umMirror.addApp(appName);
		return true;
	}
	
	@RequestMapping(value=P.API_ADD_APP_VERSION, method=RequestMethod.POST)
	@ResponseBody
	public Object addAppVersion(
		@RequestParam("umServer") String umServer, 
		@RequestParam("appName") String appName,
		@RequestParam("appVersion") String appVersion,
		HttpServletRequest request)
	{
		UMMirror umMirror = getUmMirror(umServer);
		if (umMirror == null || appName == null || appName.length() == 0 || appVersion == null || appVersion.length() == 0) {
			return false;
		}
		
		umMirror.addAppVersion(appName, appVersion);
		return true;
	}
	
	@RequestMapping(value=P.API_SET_APP_CONFIG, method=RequestMethod.POST)
	@ResponseBody
	public Object setAppConfig(
		@RequestParam("umServer") String umServer, 
		@RequestParam("appName") String appName,
		@RequestParam("appVersion") String appVersion,
		@RequestParam("key") String key,
		@RequestParam("value") String value,
		HttpServletRequest request)
	{
		UMMirror umMirror = getUmMirror(umServer);
		if (umMirror == null 
				|| appName == null || appName.length() == 0 
				|| appVersion == null || appVersion.length() == 0
				|| key == null || key.length() == 0
				|| value == null || value.length() == 0) {
			return false;
		}
		
		umMirror.setAppConfig(appName, appVersion, key, value);
		return true;
	}
	
	@RequestMapping(value=P.API_DELETE_APP_CONFIG, method=RequestMethod.POST)
	@ResponseBody
	public Object deleteAppConfig(
		@RequestParam("umServer") String umServer, 
		@RequestParam("appName") String appName,
		@RequestParam("appVersion") String appVersion,
		@RequestParam("key") String key,
		HttpServletRequest request)
	{
		UMMirror umMirror = getUmMirror(umServer);
		if (umMirror == null 
				|| appName == null || appName.length() == 0 
				|| appVersion == null || appVersion.length() == 0
				|| key == null || key.length() == 0) {
			return false;
		}
		
		umMirror.deleteAppConfig(appName, appVersion, key);
		return true;
	}
}
