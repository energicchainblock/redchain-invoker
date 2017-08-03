package com.utsoft.blockchain.api;
import java.util.Locale;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
/**
 * 基础类
 * @author hunterfox
 * @date 2017年7月17日
 * @version 1.0.0
 */
public abstract class AbstractController {

	@Resource
	private MessageSource messageSource;

	public String formatMsg(String code, Object... args) {
		if (args.length == 0)
			return messageSource.getMessage(code, null, Locale.CHINESE);
		return messageSource.getMessage(code, args, Locale.CHINESE);
	}

	public String formatMsg(HttpServletRequest req, String code, Object... args) {
		if (args.length == 0)
			return messageSource.getMessage(code, null, req.getLocale());
		return messageSource.getMessage(code, args, req.getLocale());
	}
}
