package domaines;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import beans.Admin;
import java.io.Serializable;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import services.AdminFacade;
import javax.faces.bean.ManagedBean;

@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    private String username;
    private String password;

    @Inject
    private AdminFacade adminFacade;

    public String login() {
        Admin admin = adminFacade.findByUserName(username);

        if (admin != null && admin.getPassword().equals(password)) {
            // Authentification réussie, redirigez vers la page d'accueil
            return "/web/machine/List?faces-redirect=true";
        } else {
            // Authentification échouée, affichez un message d'erreur
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login failed", "Invalid username or password"));
            return null;
        }
    }

    public String logout() {
        // Obtenez l'objet ExternalContext
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

        // Obtenez la session HTTP
        HttpSession session = (HttpSession) externalContext.getSession(false);

        // Invalidez la session (déconnexion)
        if (session != null) {
            session.invalidate();
        }
        System.err.println("deconnecté");
        // Redirigez vers la page de connexion
        return "login?faces-redirect=true";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AdminFacade getAdminFacade() {
        return adminFacade;
    }

    public void setAdminFacade(AdminFacade adminFacade) {
        this.adminFacade = adminFacade;
    }

}
