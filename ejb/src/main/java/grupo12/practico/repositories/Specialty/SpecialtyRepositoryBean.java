package grupo12.practico.repositories.Specialty;

import java.util.List;

import grupo12.practico.models.Specialty;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Stateless
@Local(SpecialtyRepositoryLocal.class)
@Remote(SpecialtyRepositoryRemote.class)
public class SpecialtyRepositoryBean implements SpecialtyRepositoryRemote {

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

    @Override
    public Specialty add(Specialty specialty) {
        if (specialty == null) {
            return null;
        }
        em.persist(specialty);
        return specialty;
    }

    @Override
    public Specialty findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return em.find(Specialty.class, id);
    }

    @Override
    public List<Specialty> findAll() {
        TypedQuery<Specialty> query = em.createQuery("SELECT s FROM Specialty s", Specialty.class);
        return query.getResultList();
    }

    @Override
    public List<Specialty> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return findAll();
        }
        TypedQuery<Specialty> query = em.createQuery(
                "SELECT s FROM Specialty s WHERE LOWER(s.name) LIKE LOWER(:name)", Specialty.class);
        query.setParameter("name", "%" + name.trim() + "%");
        return query.getResultList();
    }
}
