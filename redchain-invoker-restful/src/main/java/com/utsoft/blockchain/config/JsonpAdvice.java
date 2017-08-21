package com.utsoft.blockchain.config;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractJsonpResponseBodyAdvice;
/**
 * 
 * @author <a href="flyskyhunter@gmail.com">王波</a> 
 * @date  2017年7月17日
 * @version 1.0.0
 */
@ControllerAdvice(basePackages = "com.utsoft.blockchain.api.controller")  
public class JsonpAdvice extends AbstractJsonpResponseBodyAdvice {

	public JsonpAdvice() {  
        super("callback","jsonp");  
    }  
}
