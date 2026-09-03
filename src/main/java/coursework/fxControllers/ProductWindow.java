package coursework.fxControllers;

import coursework.hibenateControllers.GenericHibernate;
import coursework.persistence.Database;
import jakarta.persistence.EntityManagerFactory;

public class ProductWindow {

    EntityManagerFactory entityManagerFactory = Database.entityManagerFactory();
    GenericHibernate hibernate = new GenericHibernate(entityManagerFactory);


}
