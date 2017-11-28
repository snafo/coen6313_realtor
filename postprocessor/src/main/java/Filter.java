import com.google.gson.Gson;
import crawler.entity.PropertyEntity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by qinyu on 2017-11-28.
 */
public class Filter {
    public static void main(String[] args) throws IOException {
        String source = "duproprio";
        String fileName = "";
        Gson gson = new Gson();
        String regex = "\\d";
        int count = 0;
        Pattern pattern = Pattern.compile(regex);
        if(source.equals("centris")){
            fileName = "centris_info_processed.csv";
        }else if (source.equals("duproprio")){
            fileName = "duproprio_info_processed.json";
        }
        try(
                BufferedReader bf = new BufferedReader(new FileReader(new File(fileName)))
        ){
//            String propertyStr = "{\"source\":\"centris\",\"url\":\"https://www.centris.ca/en/condos~for-sale~le-plateau-mont-royal-montreal/14089392?view\\u003dSummary\",\"type\":\"Condo\",\"address\":\"4519, Avenue Christophe-Colomb, apt. B2, Le Plateau-Mont-Royal (Montréal), Neighbourhood Le Plateau-Mont-Royal\",\"unparsedAddress\":\"4519, Avenue Christophe-Colomb, apt. B2, Le Plateau-Mont-Royal (Montréal), Neighbourhood Le Plateau-Mont-Royal\",\"price\":299000.0,\"room\":\"3 Rooms 0+1 Bedroom 1+0 Bathroom/Powder room\",\"discription\":\"LOCALIZATION IN THE FIRST LODGE: In the heart of the Plateau, the GENÉREUX offers an incredible quality of life by the proximity of the famous Mont-Royal Street, services and especially its modern spaces. Its cutting out 3 blocks keeping a scale of district that allows gardens spaces in continuity with the existing courtyards.\",\"area\":646.0,\"brokers\":[{\"name\":\"Philippe Doyon\",\"firm\":\"KELLER WILLIAMS URBAIN\"}],\"firm\":\"KELLER WILLIAMS URBAIN\",\"image\":\"https://mspublic.centris.ca/media.ashx?id\\u003dADDD250D4EB9598DD3A679DD1B\\u0026t\\u003dpi\\u0026w\\u003d480\\u0026h\\u003d360\\u0026sm\\u003dc\"}\n";
            String propertyStr;
            PropertyEntity property;
            while((propertyStr = bf.readLine()) != null){
                try{
                    property = gson.fromJson(propertyStr, PropertyEntity.class);
                    Matcher m = pattern.matcher(property.getAddress());
                    if (!m.find()){
                        System.out.println(property.getAddress());
                        System.out.println(property.getUnparsedAddress());
                        count++;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        System.out.print(count);
    }
}
