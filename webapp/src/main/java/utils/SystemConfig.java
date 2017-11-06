//package utils;
//
//import org.apache.commons.configuration2.ConfigurationConverter;
//import org.apache.commons.configuration2.PropertiesConfiguration;
//
//import javax.naming.ConfigurationException;
//import java.util.Properties;
//
///**
// * Created by qinyu on 2017-11-01.
// */
//public class SystemConfig {
//    private static Properties prop = null;
//
//    public static Properties getProperties(){
//        if (prop == null){
//            try {
////                PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration("db.properties");
//                PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
//                prop = ConfigurationConverter.getProperties(propertiesConfiguration);
//            } catch (ConfigurationException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return prop;
//    }
//}
