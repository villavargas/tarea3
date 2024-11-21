
package mx.com.telcel.notificacionfacturatelmextelcel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>
 * Java class for anonymous complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="codigo" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="descripcion" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{ "codigo", "descripcion" })
@XmlRootElement(name = "NotificacionFacturaTelmexResponse")
public class NotificacionFacturaTelmexResponse
{

	@XmlElement(required = true)
	protected String codigo;
	@XmlElement(required = true)
	protected String descripcion;

	/**
	 * Gets the value of the codigo property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodigo()
	{
		return codigo;
	}

	/**
	 * Sets the value of the codigo property.
	 * 
	 * @param value
	 *           allowed object is {@link String }
	 * 
	 */
	public void setCodigo(final String value)
	{
		this.codigo = value;
	}

	/**
	 * Gets the value of the descripcion property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDescripcion()
	{
		return descripcion;
	}

	/**
	 * Sets the value of the descripcion property.
	 * 
	 * @param value
	 *           allowed object is {@link String }
	 * 
	 */
	public void setDescripcion(final String value)
	{
		this.descripcion = value;
	}

}
