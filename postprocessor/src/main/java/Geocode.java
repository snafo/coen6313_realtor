import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
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
    private static Pattern pattern;

    public static void main(String[] args) throws InterruptedException, ApiException, IOException {
        pattern = Pattern.compile(ROOM_REGEX);
        gson = new GsonBuilder().setPrettyPrinting().create();
//        context = new GeoApiContext.Builder()
//                .apiKey(key)
//                .build();

        try(
                BufferedReader bf = new BufferedReader(new FileReader(new File(fileName)))
        ){
            String propertyStr;
            PropertyEntity property;
            while((propertyStr = bf.readLine()) != null){
                try{
                    property = gson.fromJson(propertyStr, PropertyEntity.class);
//                    geocoding(property);
                    roomCoding(property);
//                    writeJson(gson.toJson(property),outFileName);
                }catch (Exception e){

                }
            }
        }
    }

    private static void geocoding(PropertyEntity property) throws InterruptedException, ApiException, IOException {
        String address = property.getAddress().split("Neighbour")[0].trim();

        GeocodingResult[] results =  GeocodingApi.geocode(context,address).await();
        property.setAddress(results[0].formattedAddress);
        for (AddressComponent ac : results[0].addressComponents){
            List<AddressComponentType> typeList = Arrays.asList(ac.types);
            if (typeList.contains(AddressComponentType.NEIGHBORHOOD)){
                property.setNeighbour(ac.longName);
            }else if(typeList.contains(AddressComponentType.SUBLOCALITY)){
                if (property.getSublocality() != null){
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

        List<String> rooms = findMatchers(roomDescrip);
        if (!rooms.isEmpty()){
            for (String room : rooms){
                int number = getRoomNumber(room);
                if (room.contains("Rooms")){
                    property.getRooms().setTotal(number);
                } else if (room.contains("Bedrooms")){
                    property.getRooms().setBedroom(number);
                } else if (room.contains("Bathrooms")){
                    property.getRooms().setBathroom(number);
                }
            }
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
        if(matcher.find()) {
            roomNumber += Integer.valueOf(matcher.group(0));
        }
        return roomNumber;
    }

    private static List<String> findMatchers(String input){
        List<String> output = new ArrayList<>();
        Matcher m = pattern.matcher(input);
        while(m.find()){
            output.add(m.group(0));
        }
        return output;
    }

    private static void writeJson(String jsonString, String fileName)  {
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(fileName, true))){
            printWriter.println(jsonString);
        } catch (IOException e) {}
    }
}
