package domaines;

import beans.Machine;

import beans.Employe;
import domaines.util.JsfUtil;
import domaines.util.JsfUtil.PersistAction;
import services.EmployeFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.AjaxBehaviorEvent;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartModel;
import org.primefaces.model.chart.ChartSeries;

@ManagedBean(name = "employeController")
@SessionScoped
public class EmployeController implements Serializable {

    @EJB
    private services.EmployeFacade ejbFacade;
    private List<Employe> items = null;
    private Employe selected;

    private Employe employeSelectionne;
    private List<Machine> listeMachines;

    public EmployeController() {
    }

    public Employe getSelected() {
        return selected;
    }

    public void setSelected(Employe selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private EmployeFacade getFacade() {
        return ejbFacade;
    }

    public Employe prepareCreate() {
        selected = new Employe();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("EmployeCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("EmployeUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("EmployeDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Employe> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public List<Employe> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Employe> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Employe.class)
    public static class EmployeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            EmployeController controller = (EmployeController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "employeController");
            return controller.getFacade().find(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Employe) {
                Employe o = (Employe) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Employe.class.getName()});
                return null;
            }
        }

    }

    public Employe getEmployeSelectionne() {
        return employeSelectionne;
    }

    public void setEmployeSelectionne(Employe employeSelectionne) {
        this.employeSelectionne = employeSelectionne;
    }

    public List<Machine> getListeMachines() {
        return listeMachines;
    }

    public void setListeMachines(List<Machine> listeMachines) {
        this.listeMachines = listeMachines;
    }

    public void chargerMachinesListener() {
        System.out.println("Employe selectionné : " + employeSelectionne);

        if (employeSelectionne != null) {
            listeMachines = employeSelectionne.getMachineList();
            System.out.println("Nombre de machines chargées : " + listeMachines.size());
            System.out.println("Employe selectionné : " + employeSelectionne.getNom());
        } else {
            listeMachines = new ArrayList<>();
        }
    }

    public ChartModel initBarModel() {
        CartesianChartModel model = new CartesianChartModel();

        // Map pour stocker le nombre de machines par année
        Map<Integer, Integer> machinesParAnnee = new HashMap<>();

        // Remplir la map avec les données
        for (Employe employe : getItemsAvailableSelectMany()) {
            for (Machine m : employe.getMachineList()) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(m.getDateAchat());

                int annee = calendar.get(Calendar.YEAR);

                // Mettre à jour le nombre de machines pour cette année
                machinesParAnnee.put(annee, machinesParAnnee.getOrDefault(annee, 0) + 1);
            }
        }

        // Créer la série de données pour le graphique
        ChartSeries machinesParAnneeSeries = new ChartSeries();
        machinesParAnneeSeries.setLabel("Machines par Année");

        for (Map.Entry<Integer, Integer> entry : machinesParAnnee.entrySet()) {
            machinesParAnneeSeries.set(entry.getKey().toString(), entry.getValue());
        }

        // Ajouter la série de données au modèle
        model.addSeries(machinesParAnneeSeries);

        
        return model;
    }

}
