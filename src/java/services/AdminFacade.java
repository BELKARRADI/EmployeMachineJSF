package services;

import beans.Admin;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author ADMIN
 */
@Stateless
public class AdminFacade extends AbstractFacade<Admin> {
    @PersistenceContext(unitName = "jsf2002PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AdminFacade() {
        super(Admin.class);
    }

    public Admin findByUserName(String userName) {
        try {
            Query query = em.createNamedQuery("Admin.findByUserName");
            query.setParameter("userName", userName);
            return (Admin) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
