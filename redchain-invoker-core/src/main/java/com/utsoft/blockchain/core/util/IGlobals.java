package com.utsoft.blockchain.core.util;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
/**
 * 全局参数配置
 * @author hunterfox
 * @date 2016年7月22日
 * @version 1.0.0
 */
public class IGlobals {
	
	//private static final Logger Log = LoggerFactory.getLogger(IGlobals.class);
    private static Locale locale = null;
    private  Properties properties ;
	private IGlobals() {
		properties = new Properties();   
 	}
	public final Properties getProperties() {
		return properties;
	}
	private  static IGlobals confGlobals = new IGlobals();
	public static IGlobals getInstance() {
	  return  confGlobals;
    }
	
	public static  String getProperty(String name) {
	     return IGlobals.getInstance().properties.getProperty(name);
	}
	
	public static Properties getPeerProperties(String name) {
		Properties dbproperties = new Properties();
		
		for (Map.Entry<Object,Object>  entry: IGlobals.getInstance().properties.entrySet())
		{
			String key = (String)entry.getKey();
			if (key!=null&&key.startsWith(name)) {
				dbproperties.put(entry.getKey(), entry.getValue());
			}
		}
		return dbproperties;
	}
	
	
	
	/**
     * Returns a  property. If the specified property doesn't exist, the
     * <tt>defaultValue</tt> will be returned.
     *
     * @param name the name of the property to return.
     * @param defaultValue value returned if the property doesn't exist.
     * @return the property value specified by name.
     */
    public static String getProperty(String name, String defaultValue) {
        String value = getProperty(name);
        if (value != null) {
            return value;
        }
        else {
            return defaultValue;
        }
    }

    public void setProperties(Properties properties) {
		this.properties = properties;
	}

	/**
     * Returns an integer value xinge property. If the specified property doesn't exist, the
     * <tt>defaultValue</tt> will be returned.
     *
     * @param name the name of the property to return.
     * @param defaultValue value returned if the property doesn't exist or was not
     *      a number.
     * @return the property value specified by name or <tt>defaultValue</tt>.
     */
    public static  int getIntProperty(String name, int defaultValue) {
        String value = getProperty(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            }
            catch (NumberFormatException nfe) {
                // Ignore.
            }
        }
        return defaultValue;
    }

    /**
     * Returns a long value xinge property. If the specified property doesn't exist, the
     * <tt>defaultValue</tt> will be returned.
     *
     * @param name the name of the property to return.
     * @param defaultValue value returned if the property doesn't exist or was not
     *      a number.
     * @return the property value specified by name or <tt>defaultValue</tt>.
     */
    public static long getLongProperty(String name, long defaultValue) {
        String value = getProperty(name);
        if (value != null) {
            try {
                return Long.parseLong(value);
            }
            catch (NumberFormatException nfe) {
                // Ignore.
            }
        }
        return defaultValue;
    }

    /**
     * Returns a boolean value xinge property.
     *
     * @param name the name of the property to return.
     * @return true if the property value exists and is set to <tt>"true"</tt> (ignoring case).
     *      Otherwise <tt>false</tt> is returned.
     */
    public static boolean getBooleanProperty(String name) {
        return Boolean.valueOf(getProperty(name));
    }

    /**
     * Returns a boolean value property. If the property doesn't exist, the <tt>defaultValue</tt>
     * will be returned.
     *
     * If the specified property can't be found, or if the value is not a number, the
     * <tt>defaultValue</tt> will be returned.
     *
     * @param name the name of the property to return.
     * @param defaultValue value returned if the property doesn't exist.
     * @return true if the property value exists and is set to <tt>"true"</tt> (ignoring case).
     *      Otherwise <tt>false</tt> is returned.
     */
    public static boolean getBooleanProperty(String name, boolean defaultValue) {
        String value = getProperty(name);
        if (value != null) {
            return Boolean.valueOf(value);
        }
        else {
            return defaultValue;
        }
    }
	  
    public static Locale getLocale() {
        if (locale == null) {
            if (IGlobals.getInstance().properties != null) {
                String [] localeArray;
                String localeProperty = IGlobals.getInstance().properties.getProperty("locale");
                if (localeProperty != null) {
                    localeArray = localeProperty.split("_");
                }
                else {
                    localeArray = new String[] {"", ""};
                }

                String language = localeArray[0];
                if (language == null) {
                    language = "";
                }
                String country = "";
                if (localeArray.length == 2) {
                    country = localeArray[1];
                }
                // If no locale info is specified, return the system default Locale.
                if (language.equals("") && country.equals("")) {
                    locale = Locale.getDefault();
                }
                else {
                    locale = new Locale(language, country);
                }
            }
            else {
                return Locale.getDefault();
            }
        }
        return locale;
    }
}
