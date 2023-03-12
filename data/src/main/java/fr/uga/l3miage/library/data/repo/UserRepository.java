package fr.uga.l3miage.library.data.repo;

import fr.uga.l3miage.library.data.domain.User;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository implements CRUDRepository<String, User> {

    private final EntityManager entityManager;

    @Autowired
    public UserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public User save(User entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public User get(String id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public void delete(User entity) {
        entityManager.remove(entity);
    }

    @Override
    public List<User> all() {
        return entityManager.createQuery("from User", User.class).getResultList();
    }

    /**
     * Trouve tous les utilisateurs ayant plus de l'age passé
     * @param age l'age minimum de l'utilisateur
     * @return
     */
    public List<User> findAllOlderThan(int age) {
        LocalDate curDate = LocalDate.now(); 
     
        String query = "SELECT u FROM User u";

       List<User> users = entityManager.createQuery(query, User.class).getResultList();
       List<User> res = new ArrayList<>();

       for (User user : users) {
           
           LocalDate temp = LocalDate.ofInstant(user.getBirth().toInstant(),  ZoneId.systemDefault());
           int ageUsr = Period.between(temp, curDate).getYears();

           if (ageUsr > age) {
               res.add(user);
           }
       }

       return res;
    }

}
