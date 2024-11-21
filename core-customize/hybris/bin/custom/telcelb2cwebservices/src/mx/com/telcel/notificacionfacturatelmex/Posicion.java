
package mx.com.telcel.notificacionfacturatelmex;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for Posicion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Posicion"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Material" type="{http://ibcomercio/telcel.com/commerce/services/SAP/NotificacionFactura/}Material"/&gt;
 *         &lt;element name="SeriesIMEI" type="{http://ibcomercio/telcel.com/commerce/services/SAP/NotificacionFactura/}SeriesIMEI" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="SeriesICCID" type="{http://ibcomercio/telcel.com/commerce/services/SAP/NotificacionFactura/}SeriesICCID" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Posicion", propOrder = {
    "material",
    "seriesIMEI",
    "seriesICCID"
})
public class Posicion {

    @XmlElement(name = "Material", required = true)
    protected Material material;
    @XmlElement(name = "SeriesIMEI")
    protected List<SeriesIMEI> seriesIMEI;
    @XmlElement(name = "SeriesICCID")
    protected List<SeriesICCID> seriesICCID;
    @XmlAttribute(name = "id")
    protected String id;

    /**
     * Gets the value of the material property.
     * 
     * @return
     *     possible object is
     *     {@link Material }
     *     
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Sets the value of the material property.
     * 
     * @param value
     *     allowed object is
     *     {@link Material }
     *     
     */
    public void setMaterial(Material value) {
        this.material = value;
    }

    /**
     * Gets the value of the seriesIMEI property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the seriesIMEI property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSeriesIMEI().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SeriesIMEI }
     * 
     * 
     */
    public List<SeriesIMEI> getSeriesIMEI() {
        if (seriesIMEI == null) {
            seriesIMEI = new ArrayList<SeriesIMEI>();
        }
        return this.seriesIMEI;
    }

    /**
     * Gets the value of the seriesICCID property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the seriesICCID property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSeriesICCID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SeriesICCID }
     * 
     * 
     */
    public List<SeriesICCID> getSeriesICCID() {
        if (seriesICCID == null) {
            seriesICCID = new ArrayList<SeriesICCID>();
        }
        return this.seriesICCID;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}
