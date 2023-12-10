/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author ADMIN
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

@ManagedBean
@SessionScoped
public class LanguageBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String selectedLanguage;
    private List<Locale> supportedLocales;

    public LanguageBean() {
        // Initialise la liste des langues prises en charge
        supportedLocales = new ArrayList<>();
        supportedLocales.add(new Locale("en", "US")); // Exemple avec l'anglais
        supportedLocales.add(new Locale("es", "ES")); // Exemple avec l'espagnol
        supportedLocales.add(new Locale("fr", "FR")); // Exemple avec l'espagnol
        supportedLocales.add(new Locale("ar", "AR")); // Exemple avec l'espagnol

        // Définir la langue par défaut
        selectedLanguage = "es";
    }

    // Getter et Setter pour la langue sélectionnée
    public String getSelectedLanguage() {
        return selectedLanguage;
    }

    public void setSelectedLanguage(String selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
    }

    // Getter pour la liste des langues prises en charge
    public List<Locale> getSupportedLocales() {
        return supportedLocales;
    }

    public void changeLocale() {
        System.out.println("Selected Language: " + selectedLanguage);

        if (selectedLanguage != null) {
            FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale(selectedLanguage));
        } else {
            System.out.println("WARNING: selectedLanguage is null!");
        }
    }

}
