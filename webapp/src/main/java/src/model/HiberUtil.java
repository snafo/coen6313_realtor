package model;

import org.hibernate.*;
import org.hibernate.boot.*;
import org.hibernate.boot.registry.*;

public class HiberUtil{

    private static final SessionFactory sessionFactory;

    static {
        try {
            StandardServiceRegistry standardServiceRegistry =  new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
            Metadata metadata = new MetadataSources(standardServiceRegistry).getMetadataBuilder().build();
            sessionFactory = metadata.getSessionFactoryBuilder().build();
        } catch (Throwable th){
            throw new ExceptionInInitializerError(th);
        }
    }
    public  static SessionFactory getSessionFactory(){
        return sessionFactory;
    }


}