package com.utsoft.blockchain.core.util;
import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;
import org.hyperledger.fabric.sdk.helper.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import com.utsoft.blockchain.api.util.SdkUtil;
/**
 * 简单工具
 * 
 * @author <a href="flyskyhunter@gmail.com">王波</a>
 * @date 2017年7月27日
 * @version 1.0.0
 */
public class CommonUtil {

	private static final Logger Log = LoggerFactory.getLogger(IGlobals.class);
	 private static final String Algorithm = "Blowfish"; //定义加密算法,可用 DES,DESede,Blowfish  
	  
	 public static DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
	 private CommonUtil() {
	}

    public static String dateConvertToText(Date date){
			try {
			  return sdf.format(date);
			} catch (Exception e) {
				e.printStackTrace();
			}
		 return "";
	 }
	
	public static String getSha1(String data) {
		return DigestUtils.sha1Hex(data);
	}

	public static boolean isNotEmpty(String text) {
		if (text != null && !"".equals(text)) {
			return true;
		}
		return false;
	}
	
	public static boolean isEmpty(String ... ssss) {	
		for (String ss: ssss) {
			if (!isNotEmpty(ss)) return true;
		}
		return false;
	}

	public static boolean isNullOrEmpty(String str) {
		return !isNotEmpty(str);
	}

	public static boolean isObjectNotEmpty(Object obj) {
		return obj != null;
	}

	public static boolean isEmail(String str) {
		 String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		 return match(regex, str);
      }
	
	 public static boolean IsUrl(String str) {
		 String regex = "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+(\\?{0,1}(([A-Za-z0-9-~]+\\={0,1})([A-Za-z0-9-~]*)\\&{0,1})*)$"; //http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?
	     return match(regex, str);
     }
	
	 private static boolean match(String regex, String str) {
		 Pattern pattern = Pattern.compile(regex);
		 Matcher matcher = pattern.matcher(str);
		 return matcher.matches();
	 }
	 
	/**
	 * 集合是否为空
	 * 
	 * @param list
	 * @return
	 */
	public static <T> boolean isCollectNotEmpty(Collection<T> list) {
		return list != null && !list.isEmpty();
	}

	/**
	 * 为空
	 * 
	 * @param list
	 * @return
	 */
	public static <T> boolean isCollectEmpty(Collection<T> list) {
		return list == null || list.isEmpty();
	}

	/**
	 * tls 替换
	 * 
	 * @param location
	 * @return
	 */
	public static String grpcTLSify(String location) {

		location = location.trim();
		Exception e = Utils.checkGrpcUrl(location);
		if (e != null) {
			throw new RuntimeException(String.format("Bad TEST parameters for grpc url %s", location), e);
		}
		return IGlobals.getBooleanProperty("connect_fabricTLS", false) ? location.replaceFirst("^grpc://", "grpcs://")
				: location;
	}

	/**
	 * https 替换
	 * 
	 * @param location
	 * @return
	 **/
	public static String httpTLSify(String location) {

		location = location.trim();
		return IGlobals.getBooleanProperty("runningFabricCATLS", false) ? location.replaceFirst("^http://", "https://")
				: location;
	}
	
   public static File findFileSk(File directory,String rootPath){

        File[] matches = directory.listFiles((dir, name) -> name.endsWith("_sk"));

        if (null == matches) {
            throw new RuntimeException(format("Matches returned null does %s directory exist?", directory.getAbsoluteFile().getName()));
        }

        if (matches.length != 1) {
        	Log.warn(rootPath+format(":Expected in %s only 1 sk file but found %d", directory.getAbsoluteFile().getName(), matches.length));
        }
        return matches[0];
    }
   
   /**
    *  peer support tls and cert configuration
    * @param name
    * @return
    */
   public  static Properties getPeerProperties(String name) {
       return getEndPointProperties("peer", name);
   }

   public static Properties getOrdererProperties(String name) {
       return getEndPointProperties("orderer", name);
   }

   public static Properties getEventHubProperties(String name) {
      return getEndPointProperties("peer", name); //uses same as named peer
   }
   
   private  static String getDomainName(final String name) {
       int dot = name.indexOf(".");
       if (-1 == dot) {
           return null;
       } else {
           return name.substring(dot + 1);
       }
   }
   
   public static Properties getEndPointProperties(final String type, final String name) {

         final String orderRootPath = IGlobals.getProperty("fabric.order_root_path");
         final String domainName = getDomainName(name);
		 String fullpath =  Paths.get(orderRootPath.replace("orderer", type), domainName, type + "s",
			               name, "tls/server.crt").toString();
        File cert =null;
		try {
			cert = getFilepath(fullpath);
		} catch (Exception e) {
			e.printStackTrace();
		} 
        if (cert==null || !cert.exists()) {
             throw new RuntimeException(String.format("Missing cert file for: %s. Could not find at location: %s", name,
                   cert.getAbsolutePath()));
          }
        //  ret.setProperty("trustServerCertificate", "true"); 
        //testing environment only NOT FOR PRODUCTION!
       Properties ret = new Properties();
       ret.setProperty("pemFile", cert.getAbsolutePath());
       ret.setProperty("hostnameOverride", name);
       ret.setProperty("sslProvider", "openSSL");
       ret.setProperty("negotiationType", "TLS");
       return ret;
   }
   
  public static String printableString(final String string) {
       int maxLogStringLength = 64;
       if (string == null || string.length() == 0) {
           return string;
       }

       String ret = string.replaceAll("[^\\p{Print}]", "?");

       ret = ret.substring(0, Math.min(ret.length(), maxLogStringLength)) + (ret.length() > maxLogStringLength ? "..." : "");

       return ret;
   }
  
	 private static byte[] encryptMode(byte[] keybyte,byte[] src){  
	       try {  
	          //生成密钥  
	          SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);  
	          Cipher c1 = Cipher.getInstance(Algorithm);  
	          c1.init(Cipher.ENCRYPT_MODE, deskey);  
	          return c1.doFinal(src);
	      } catch (java.security.NoSuchAlgorithmException e1) {  
	           e1.printStackTrace();  
	      }catch(javax.crypto.NoSuchPaddingException e2){  
	          e2.printStackTrace();  
	      }catch(java.lang.Exception e3){  
	          e3.printStackTrace();  
	      }  
	     return null;  
	  }  
    
	 private static byte[] decryptMode(byte[] keybyte,byte[] src){  
      try {  
          SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);  
          Cipher c1 = Cipher.getInstance(Algorithm);  
          c1.init(Cipher.DECRYPT_MODE, deskey);  
          return c1.doFinal(src);  
      } catch (java.security.NoSuchAlgorithmException e1) {  
          e1.printStackTrace();  
      }catch(javax.crypto.NoSuchPaddingException e2){  
          e2.printStackTrace();  
      }catch(java.lang.Exception e3){  
          e3.printStackTrace();  
      }  
       return null;          
    } 
  
     public static String encryptText(String key,String data) {
    	 byte[] keybyte = key.getBytes();
    	 byte[] src = data.getBytes();
    	 return SdkUtil.encodeHexString(encryptMode(keybyte,src));
     }
     
     public static String decrypText(String key,String data) {
    	 byte[] afterContent = SdkUtil.decodeHexStrig(data);
    	 byte[] keybyte = key.getBytes();
    	 return new String(decryptMode(keybyte,afterContent));
     }
   
    
     public static File getFilepath(String path)  {
    	 File cf =null;
		 try {
			cf = ResourceUtils.getFile("classpath:"+path);
		  }  catch (IOException e ) {
			  Resource resource = new ClassPathResource(path);
		     try {
				cf= resource.getFile();
			    } catch (IOException e1) {
			 }
		  } 
		  if (cf==null || !cf.exists())  {
			 // path = CommonUtil.class.getResource("/").getFile()+path;
			 // System.out.println("................."+System.getProperty("user.dir")+File.separator+path);
			  cf = new File(System.getProperty("user.dir")+File.separator+path);
		  }
		 if (!cf.exists()) {
             throw new RuntimeException("loading is missing  file " + cf.getAbsolutePath());
         }
          return cf;
     }
     
	   /**
     * 获取内网IP
     * @return
     */
    public static String getInnerIPAddress() {
    	try {  
         	Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();  
             while (allNetInterfaces.hasMoreElements()) {  
                 NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();  

                 // 去除回环接口，子接口，未运行和接口
                 if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {  
                     continue;  
                 }
                 Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                 while (addresses.hasMoreElements()) {  
                     InetAddress ip = addresses.nextElement(); 
                     if (ip != null) {
                         if (ip instanceof Inet4Address) {
                         	String ipAddress = ip.getHostAddress();
                             if (internalIp(ipAddress)) {  
                                 return ipAddress;
                             }
                         }
                     }
                 }
             }
         } catch (SocketException e) {
             System.err.println("Error  getting host ip inner address"+ e.getMessage());
         } 
    	InetAddress addr;
  		try {
  			addr = (InetAddress) InetAddress.getLocalHost();
  			return addr.getHostAddress().toString(); 
  		} catch (UnknownHostException e) {
  			e.printStackTrace();
  		} 
  		return null;
    }
    
    /**
     * 获取外网IP
     * @return
     */
    public  static String  getPublicIPAddress() {  
        try {  
         	Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();  
             while (allNetInterfaces.hasMoreElements()) {  
                 NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();  

                 // 去除回环接口，子接口，未运行和接口
                 if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {  
                     continue;  
                 }
                 Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                 while (addresses.hasMoreElements()) {  
                     InetAddress ip = addresses.nextElement(); 
                     if (ip != null) {
                         if (ip instanceof Inet4Address) {
                         	String ipAddress = ip.getHostAddress();
                             if (!internalIp(ipAddress)) {  
                                 return ipAddress;
                             }
                         }
                     }
                 }
             }
         } catch (SocketException e) {
             System.err.println("Error when getting host ip address"+ e.getMessage());
         } 
         return null;
     }
    
    /**
	 * Class A 10.0.0.0-10.255.255.255 | 255.0.0.0
	 * Class B 172.16.0.0-172.31.255.255 | 255.240.0.0 
	 * Class C 192.168.0.0-192.168.255.255 | 255.255.0.0
     * @param ip
     * @return true is inneral ip Address
     */
     public static boolean internalIp(String ip) {
        byte[] addr = null;
		try {
			addr = ipToBytes(ip);
		} catch (UnknownHostException e) {
			return false;
		}   //IPAddressUtil.textToNumericFormatV4(ip);
        return internalIp(addr);
    }

    private static byte[] ipToBytes(String ip) throws UnknownHostException {
        return InetAddress.getByName(ip).getAddress();
    }
        
    public static boolean internalIp(byte[] addr) {
        final byte b0 = addr[0];
        final byte b1 = addr[1];
        //10.x.x.x/8
        final byte SECTION_1 = 0x0A;
        //172.16.x.x/12
        final byte SECTION_2 = (byte) 0xAC;
        final byte SECTION_3 = (byte) 0x10;
        final byte SECTION_4 = (byte) 0x1F;
        //192.168.x.x/16
        final byte SECTION_5 = (byte) 0xC0;
        final byte SECTION_6 = (byte) 0xA8;
        switch (b0) {
            case SECTION_1:
                return true;
            case SECTION_2:
                if (b1 >= SECTION_3 && b1 <= SECTION_4) {
                    return true;
                }
            case SECTION_5:
                switch (b1) {
                    case SECTION_6:
                        return true;
                }
            default:
                return false;
        }
    }

}
