import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.ComponentFilter;
import com.google.maps.model.GeocodingResult;
import crawler.entity.Location;
import crawler.entity.PropertyEntity;
import crawler.entity.Room;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by qinyu on 2017-11-25.
 */
public class Geocode {
    private static final String key = "AIzaSyD3SYQsdQHKQMqLYSLjApEQTLysxkI7h_k";
    private static final String fileName = "centris_info.csv";
    private static final String outFileName = "centris_info_processed.csv";
    private static GeoApiContext context;
    private static Gson gson;
    private static final String ROOM_REGEX = "[0-9]+(\\+[0-9]+)? +[a-zA-Z\\/ ]+";
    private static final String KEY_REGEX = "(?<=\\/)\\d+(?=\\?)";
    private static Pattern roomPattern;
    private static Pattern keyPattern;

    public static void main(String[] args) throws InterruptedException, ApiException, IOException {
        int count = 0;
        roomPattern = Pattern.compile(ROOM_REGEX);
        keyPattern = Pattern.compile(KEY_REGEX);
//        gson = new GsonBuilder().setPrettyPrinting().create();
        gson = new GsonBuilder().create();
        context = new GeoApiContext.Builder()
                .apiKey(key)
                .build();

        try(
                BufferedReader bf = new BufferedReader(new FileReader(new File(fileName)))
        ){
//            String propertyStr = "{\"source\":\"centris\",\"url\":\"https://www.centris.ca/en/condos~for-sale~le-plateau-mont-royal-montreal/14089392?view\\u003dSummary\",\"type\":\"Condo\",\"address\":\"4519, Avenue Christophe-Colomb, apt. B2, Le Plateau-Mont-Royal (Montréal), Neighbourhood Le Plateau-Mont-Royal\",\"unparsedAddress\":\"4519, Avenue Christophe-Colomb, apt. B2, Le Plateau-Mont-Royal (Montréal), Neighbourhood Le Plateau-Mont-Royal\",\"price\":299000.0,\"room\":\"3 Rooms 0+1 Bedroom 1+0 Bathroom/Powder room\",\"discription\":\"LOCALIZATION IN THE FIRST LODGE: In the heart of the Plateau, the GENÉREUX offers an incredible quality of life by the proximity of the famous Mont-Royal Street, services and especially its modern spaces. Its cutting out 3 blocks keeping a scale of district that allows gardens spaces in continuity with the existing courtyards.\",\"area\":646.0,\"brokers\":[{\"name\":\"Philippe Doyon\",\"firm\":\"KELLER WILLIAMS URBAIN\"}],\"firm\":\"KELLER WILLIAMS URBAIN\",\"image\":\"https://mspublic.centris.ca/media.ashx?id\\u003dADDD250D4EB9598DD3A679DD1B\\u0026t\\u003dpi\\u0026w\\u003d480\\u0026h\\u003d360\\u0026sm\\u003dc\"}\n";
            String propertyStr;
            PropertyEntity property;
            while((propertyStr = bf.readLine()) != null){
                try{
                    property = gson.fromJson(propertyStr, PropertyEntity.class);
                    idCoding(property);
                    geocoding(property);
                    roomCoding(property);
//                    String output = gson.toJson(property);
//                    System.out.print(output);
                    if (property.getLocation() == null){
                        System.out.println(property.getUrl());
                    }
                    writeJson(gson.toJson(property),outFileName);
                }catch (Exception e){

                }

                System.out.println(count);
                if (++count>=1000){
                    break;
                }

                Thread.sleep(20);
            }
        }
    }

    private static void geocoding(PropertyEntity property){
        try {
            String address = property.getAddress().split("Neighbour")[0].trim();
//            String address = property.getAddress();

            GeocodingResult[] results = GeocodingApi
                    .geocode(context, address)
                    .components(
                            ComponentFilter.administrativeArea ("Communauté-Urbaine-de-Montréal"),
                            ComponentFilter.country("CA")
                    )
                    .language("en")
                    .await();

            property.setAddress(results[0].formattedAddress);
            for (AddressComponent ac : results[0].addressComponents) {
                List<AddressComponentType> typeList = Arrays.asList(ac.types);
                if (typeList.contains(AddressComponentType.NEIGHBORHOOD)) {
                    property.setNeighbourhood(ac.longName);
                } else if (typeList.contains(AddressComponentType.SUBLOCALITY)) {
                    if (property.getSublocality() == null) {
                        property.setSublocality(ac.longName);
                    }
                }
            }

            Location location = new Location();
            location.setLat(results[0].geometry.location.lat);
            location.setLng(results[0].geometry.location.lng);
            property.setLocation(location);
            property.setGeocode(results[0]);
            property.setPlaceId(results[0].placeId);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(property.getUrl());
        }
    }

    private static void roomCoding(PropertyEntity property){
        String roomDescrip;
        if (property.getRooms()!=null && property.getRooms().getDescription()!=null){
            roomDescrip = property.getRooms().getDescription();
        }else {
            roomDescrip = property.getRoom();
            property.setRooms(new Room());
            property.getRooms().setDescription(roomDescrip);
        }

        List<String> rooms = findRoomNumbers(roomDescrip);
        if (!rooms.isEmpty()){
            for (String room : rooms){
                int number = getRoomNumber(room);
                if (room.contains("Rooms")){
                    property.getRooms().setTotal(number);
                } else if (room.contains("Bedroom")){
                    property.getRooms().setBedroom(number);
                } else if (room.contains("Bathroom")){
                    property.getRooms().setBathroom(number);
                }
            }
        }
    }

    private static void idCoding(PropertyEntity propertyEntity){
        String code = findKey(propertyEntity.getUrl());
        if (code != null){
            propertyEntity.setPropertyId(propertyEntity.getSource() + "_" + code);
        }
    }

    private static Integer getRoomNumber(String input){
        int roomNumber = 0;
        if (input == null || input.isEmpty()){
            return roomNumber;
        }

        String regex = "(\\d+(?:\\.\\d+)?)";
        input = input.replaceAll(" +"," ").replaceAll(",","");
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        while(matcher.find()) {
            roomNumber += Integer.valueOf(matcher.group(0));
        }
        return roomNumber;
    }

    private static List<String> findRoomNumbers(String input){
        List<String> output = new ArrayList<>();
        Matcher m = roomPattern.matcher(input);
        while(m.find()){
            output.add(m.group(0));
        }
        return output;
    }

    private static String findKey(String input){
        Matcher m = keyPattern.matcher(input);
        if(m.find()){
            return m.group(0);
        }
        return null;
    }

    private static void writeJson(String jsonString, String fileName)  {
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(fileName, true))){
            printWriter.println(jsonString);
        } catch (IOException e) {}
    }
}
