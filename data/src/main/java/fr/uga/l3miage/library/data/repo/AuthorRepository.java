package fr.uga.l3miage.library.data.repo;

import fr.uga.l3miage.library.data.domain.Author;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuthorRepository implements CRUDRepository<Long, Author> {

    private final EntityManager entityManager;

    @Autowired
    public AuthorRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Author save(Author author) {
        entityManager.persist(author);
        return author;
    }

    @Override
    public Author get(Long id) {
        return entityManager.find(Author.class, id);
    }


    @Override
    public void delete(Author author) {
        entityManager.remove(author);
    }

    /**
     * Renvoie tous les auteurs
     *
     * @return une liste d'auteurs trié par nom
     */
    @Override
    public List<Author> all() {
        String jpql = "SELECT a FROM Author a ORDER BY a.fullName ASC";
        List<Author> authors = entityManager.createQuery(jpql, Author.class)
                                            .getResultList();
        return authors;
    }                         
            


    /**
     * Recherche un auteur par nom (ou partie du nom) de façon insensible  à la casse.
     *
     * @param namePart tout ou partie du nomde l'auteur
     * @return une liste d'auteurs trié par nom
     */
    public List<Author> searchByName(String namePart) {
        String jpql = "SELECT a " +
                      "FROM Author a " + 
                      "WHERE LOWER(a.fullName) LIKE CONCAT('%',LOWER(:namePart),'%') " +
                      "ORDER BY a.fullName ASC";
        List<Author> authors = entityManager.createQuery(jpql, Author.class)
                                            .setParameter("namePart", namePart)
                                            .getResultList();
        return authors;
    }

    /**
     * Recherche si l'auteur a au moins un livre co-écrit avec un autre auteur
     *
     * @return true si l'auteur partage
     */
    public boolean checkAuthorByIdHavingCoAuthoredBooks(long authorId) {
        String jpql = "SELECT COUNT(DISTINCT b) " +
                      "FROM Book b " +
                      "JOIN b.authors a " +
                      "WHERE a.id = :authorId " +
                      "AND EXISTS (SELECT 1 FROM Book b2 JOIN b2.authors a2 " +
                                  "WHERE a2 <> a " +
                                  "AND b2.id = b.id)";
        Long count = entityManager.createQuery(jpql, Long.class)
                                  .setParameter("authorId", authorId)
                                  .getSingleResult();
        return count > 0;
    }
    

}
