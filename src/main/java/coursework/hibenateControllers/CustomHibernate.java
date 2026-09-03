package coursework.hibenateControllers;

import coursework.model.Comment;
import coursework.model.Publication;
import coursework.model.User;
import coursework.model.enums.PublicationStatus;
import coursework.utils.FxUtils;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.List;

public class CustomHibernate extends GenericHibernate{

    public CustomHibernate(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

    // Tikrinti ar useri
    public User getUserByCredentials(String username, String password) {
    User user = null; // Issitraukiame useri
        try {
            entityManager = entityManagerFactory.createEntityManager(); // sesija
            CriteriaBuilder cb = entityManager.getCriteriaBuilder(); // leidzia kviest metodus - and, like, ... (paieskoj sql)
            CriteriaQuery<User> query = cb.createQuery(User.class); // indikatorius kad rasysim salyga -: select ....(sql)
            Root<User> root = query.from(User.class); // select from User
            // pilnas sql query
            query.select(root).where(
                    cb.equal(root.get("login"), username));

            Query q;
            q = entityManager.createQuery(query);
            user = (User) q.getSingleResult();

        } catch (NoResultException e) {
            System.out.println("No user found for the given username.");
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Login Failed", "Invalid username or password.");
        } catch (Exception e) {
            e.printStackTrace();
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Hibernate Error", "Error during ID QUERY operation");
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return user;
    }

    public User getUserByLogin(String username) {
        User user = null; // Issitraukiame useri
        try {
            entityManager = entityManagerFactory.createEntityManager(); // sesija
            CriteriaBuilder cb = entityManager.getCriteriaBuilder(); // leidzia kviest metodus - and, like, ... (paieskoj sql)
            CriteriaQuery<User> query = cb.createQuery(User.class); // indikatorius kad rasysim salyga -: select ....(sql)
            Root<User> root = query.from(User.class); // select from User
            // pilnas sql query
            query.select(root).where(cb.equal(root.get("login"), username));

            Query q;
            q = entityManager.createQuery(query);
            user = (User) q.getSingleResult();

        } catch (NoResultException e) {
            System.out.println("No user found for the given credentials.");
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Login Failed", "Invalid username or password.");
        } catch (Exception e) {
            e.printStackTrace();
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Hibernate Error", "Error during ID QUERY operation");
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return user;
    }

    public List<Publication> getAvailablePublications(User user) {
        List<Publication> publications = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager(); // sesija
            CriteriaBuilder cb = entityManager.getCriteriaBuilder(); // leidzia kviest metodus - and, like, ... (paieskoj sql)
            CriteriaQuery<Publication> query = cb.createQuery(Publication.class); // indikatorius kad rasysim salyga -: select ....(sql)
            Root<Publication> root = query.from(Publication.class); // select from User
            // pilnas sql query
            query.select(root).where(cb.and(cb.equal(root.get("publicationStatus"), PublicationStatus.AVAILABLE), cb.notEqual(root.get("owner"), user)));
            Query q;
            q = entityManager.createQuery(query);
            publications = q.getResultList();

        }
         catch (Exception e) {
            e.printStackTrace();
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Hibernate Error", "Error during ID QUERY operation");
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return publications;
    }

    public void deleteComment(int id) {
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            var comment = entityManager.find(Comment.class, id);

            if (comment != null) {
                // Handle parent comment's replies if applicable
                if (comment.getParentComment() != null) {
                    Comment parentComment = entityManager.find(Comment.class, comment.getParentComment().getId());
                    parentComment.getReplies().remove(comment);
                    entityManager.merge(parentComment);
                }

                // Clear replies of the comment
                comment.getReplies().clear();

                // Remove the main comment
                entityManager.remove(comment);

                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().commit();
                    System.out.println("Transaction committed.");
                } else {
                    System.out.println("Transaction was not active. No changes were committed.");
                }
                FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Success", "Comment deleted successfully.");
            }
            else{
                FxUtils.generateAlert(Alert.AlertType.WARNING, "Not Found", "Comment not found in the database.");
            }
        } catch (Exception e) {
            e.printStackTrace();

            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", "Failed to delete the comment: " + e.getMessage());
        } finally {
            if (entityManager != null) entityManager.close();
        }
    }


}





