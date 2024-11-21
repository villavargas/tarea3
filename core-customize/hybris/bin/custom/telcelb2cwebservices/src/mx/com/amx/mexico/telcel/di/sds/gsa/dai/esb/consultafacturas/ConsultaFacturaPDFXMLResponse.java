
package mx.com.amx.mexico.telcel.di.sds.gsa.dai.esb.consultafacturas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.amx.mexico.telcel.esb.v1_1.ControlDataResponseType;
import com.amx.mexico.telcel.esb.v1_1.DetailResponseType;


/**
 * <p>
 * Java class for ConsultaFacturaPDFXMLResponse complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ConsultaFacturaPDFXMLResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="controlData" type="{http://amx.com/mexico/telcel/esb/v1_1}ControlDataResponseType"/&gt;
 *         &lt;element name="detailResponse" type="{http://amx.com/mexico/telcel/esb/v1_1}DetailResponseType"/&gt;
 *         &lt;element name="consultaFacturaPDFXMLResponse" type="{http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dai/esb/consultafacturas}ConsultaFacturaPDFXMLRespType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConsultaFacturaPDFXMLResponse", propOrder =
{ "controlData", "detailResponse", "consultaFacturaPDFXMLResponse" })
@XmlRootElement(name = "consultaFacturaPDFXMLResponse")
public class ConsultaFacturaPDFXMLResponse
{

	@XmlElement(required = true)
	protected ControlDataResponseType controlData;
	@XmlElement(required = true)
	protected DetailResponseType detailResponse;
	@XmlElement(required = true)
	protected ConsultaFacturaPDFXMLRespType consultaFacturaPDFXMLResponse;

	/**
	 * Gets the value of the controlData property.
	 * 
	 * @return possible object is {@link ControlDataResponseType }
	 * 
	 */
	public ControlDataResponseType getControlData()
	{
		return controlData;
	}

	/**
	 * Sets the value of the controlData property.
	 * 
	 * @param value
	 *           allowed object is {@link ControlDataResponseType }
	 * 
	 */
	public void setControlData(final ControlDataResponseType value)
	{
		this.controlData = value;
	}

	/**
	 * Gets the value of the detailResponse property.
	 * 
	 * @return possible object is {@link DetailResponseType }
	 * 
	 */
	public DetailResponseType getDetailResponse()
	{
		return detailResponse;
	}

	/**
	 * Sets the value of the detailResponse property.
	 * 
	 * @param value
	 *           allowed object is {@link DetailResponseType }
	 * 
	 */
	public void setDetailResponse(final DetailResponseType value)
	{
		this.detailResponse = value;
	}

	/**
	 * Gets the value of the consultaFacturaPDFXMLResponse property.
	 * 
	 * @return possible object is {@link ConsultaFacturaPDFXMLRespType }
	 * 
	 */
	public ConsultaFacturaPDFXMLRespType getConsultaFacturaPDFXMLResponse()
	{
		return consultaFacturaPDFXMLResponse;
	}

	/**
	 * Sets the value of the consultaFacturaPDFXMLResponse property.
	 * 
	 * @param value
	 *           allowed object is {@link ConsultaFacturaPDFXMLRespType }
	 * 
	 */
	public void setConsultaFacturaPDFXMLResponse(final ConsultaFacturaPDFXMLRespType value)
	{
		this.consultaFacturaPDFXMLResponse = value;
	}

}
