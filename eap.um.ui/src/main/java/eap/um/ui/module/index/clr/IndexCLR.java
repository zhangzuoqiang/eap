package eap.um.ui.module.index.clr;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import eap.EapContext;
import eap.base.BaseController;
import eap.um.ui.common.P;

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
public class IndexCLR extends BaseController {
	
	@RequestMapping(value=P.INDEX)
	public String index(Model model) {
		String[] umServers = null;
		String umServersProp = EapContext.getEnv().getProperty("um.servers");
		if (umServersProp != null && umServersProp.length() > 0) {
			umServers = umServersProp.split(" ");
			model.addAttribute("firstUmServers", umServers[0]);
		} else {
			umServers = new String[0];
		}
		model.addAttribute("umServers", umServers);
		
		return "index";
	}
}