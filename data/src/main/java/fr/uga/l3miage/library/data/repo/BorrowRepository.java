package fr.uga.l3miage.library.data.repo;

import fr.uga.l3miage.library.data.domain.Borrow;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BorrowRepository implements CRUDRepository<String, Borrow> {

    private final EntityManager entityManager;

    @Autowired
    public BorrowRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Borrow save(Borrow entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public Borrow get(String id) {
        return entityManager.find(Borrow.class, id);
    }

    @Override
    public void delete(Borrow entity) {
        entityManager.remove(entity);
    }

    @Override
    public List<Borrow> all() {
        return entityManager.createQuery("from Borrow", Borrow.class).getResultList();
    }

    /**
     * Trouver des emprunts en cours pour un emprunteur donné
     *
     * @param userId l'id de l'emprunteur
     * @return la liste des emprunts en cours
     */
    public List<Borrow> findInProgressByUser(String userId) {

         String query = "SELECT b FROM Borrow b WHERE b.borrower.id = :userId AND b.finished = FALSE";

         List<Borrow> res = this.entityManager.createQuery(query, Borrow.class)
                                .setParameter("userId", userId)         
                                .getResultList();

        return res;
    }

    /**
     * Compte le nombre total de livres emprunté par un utilisateur.
     *
     * @param userId l'id de l'emprunteur
     * @return le nombre de livre
     */
    public int countBorrowedBooksByUser(String userId) {
        String query = "SELECT b FROM Borrow b WHERE b.borrower.id = :userId";

         List<Borrow> brws =  this.entityManager.createQuery(query, Borrow.class)
                                .setParameter("userId", userId)
                                .getResultList();

        int count = 0;

        for (Borrow borrow : brws) {
            count += borrow.getBooks().size();
        }
 

        return count;
    }

    /**
     * Compte le nombre total de livres non rendu par un utilisateur.
     *
     * @param userId l'id de l'emprunteur
     * @return le nombre de livre
     */
    public int countCurrentBorrowedBooksByUser(String userId) {
        String query = "SELECT b FROM Borrow b WHERE b.borrower.id = :userId AND b.finished = FALSE";

        List<Borrow> brws =  this.entityManager.createQuery(query, Borrow.class)
                               .setParameter("userId", userId)
                               .getResultList();

       int count = 0;

       for (Borrow borrow : brws) {
           count += borrow.getBooks().size();
       }


       return count;
    }

    /**
     * Recherche tous les emprunt en retard trié
     *
     * @return la liste des emprunt en retard
     */
    public List<Borrow> foundAllLateBorrow() {
        String query = "SELECT b FROM Borrow b WHERE b.finished = FALSE";
        LocalDate curDate = LocalDate.now(); 

        List<Borrow> brws = this.entityManager.createQuery(query, Borrow.class)       
                               .getResultList();

         List<Borrow> res = new ArrayList<Borrow>();

        for (Borrow borrow : brws) {
            LocalDate temp = LocalDate.ofInstant(borrow.getRequestedReturn().toInstant(),  ZoneId.systemDefault());
            int diff = Period.between(curDate, temp).getDays();

            if (diff < 0) {
                res.add(borrow);
            }
        }
        
        return res;
    }

    /**
     * Calcul les emprunts qui seront en retard entre maintenant et x jours.
     *
     * @param days le nombre de jour avant que l'emprunt soit en retard
     * @return les emprunt qui sont bientôt en retard
     */
    public List<Borrow> findAllBorrowThatWillLateWithin(int days) {
    
        String query = "SELECT b FROM Borrow b WHERE b.finished = FALSE";

        LocalDate curDate = LocalDate.now(); 

        List<Borrow> brws = this.entityManager.createQuery(query, Borrow.class)       
                               .getResultList();
        
        List<Borrow> res = new ArrayList<Borrow>();

        for (Borrow borrow : brws) {
            LocalDate temp = LocalDate.ofInstant(borrow.getRequestedReturn().toInstant(),  ZoneId.systemDefault());
            int diff = Period.between(curDate, temp).getDays();

            if (diff < days) {
                res.add(borrow);
            }
        }

        return res;
    }

}
