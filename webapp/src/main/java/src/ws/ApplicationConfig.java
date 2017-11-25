package ws;

import java.util.Set;
import javax.ws.rs.core.Application;

@javax.ws.rs.ApplicationPath("rest")
public class ApplicationConfig extends Application{

    @Override
    public Set<Class<?>> getClasses(){
        Set<Class<?>> resource = new java.util.HashSet<>();
        addRestResourceClasses(resource);
        return resource;
    }

    private void addRestResourceClasses(Set<Class<?>> resources){
        resources.add(ws.UserRestful.class);
    }
}