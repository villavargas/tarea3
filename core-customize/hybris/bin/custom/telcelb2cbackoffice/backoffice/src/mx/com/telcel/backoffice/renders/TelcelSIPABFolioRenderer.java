/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.backoffice.renders;

import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.AbstractPanel;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.Attribute;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.CustomPanel;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.util.YTestTools;
import com.hybris.cockpitng.widgets.editorarea.renderer.impl.DefaultEditorAreaPanelRenderer;
import de.hybris.platform.returns.model.ReturnRequestModel;
import mx.com.telcel.core.model.SipabbModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Div;

/**
 * The type Telcel releaseresources renderer.
 */
public class TelcelSIPABFolioRenderer extends DefaultEditorAreaPanelRenderer {
    private static final Logger LOG = LoggerFactory.getLogger(TelcelSIPABFolioRenderer.class);
    public static final String RESULT = "";
    public static final String PIPE = " | ";

    private Editor editor;
    private TypeFacade typeFacade;
    private LabelService labelService;
    private String releaseResourceValue = RESULT;

    /**
     * Instantiates a new Telcel releaseresources renderer.
     */
    public TelcelSIPABFolioRenderer() {
    }

    public void render(Component component, AbstractPanel abstractPanelConfiguration, Object object, DataType dataType, WidgetInstanceManager widgetInstanceManager) {
        if (abstractPanelConfiguration instanceof CustomPanel && object instanceof ReturnRequestModel) {
            try {
                Attribute attribute = new Attribute();
                attribute.setLabel("customersupportbackoffice.sipab.order");
                attribute.setQualifier("sipab");
                attribute.setReadonly(Boolean.TRUE);
                DataType returnRequest = this.getTypeFacade().load("ReturnRequest");
                boolean canReadObject = this.getPermissionFacade().canReadInstanceProperty(returnRequest.getClazz(), "sipab");
                if (!canReadObject) {
                    Div attributeContainer = new Div();
                    attributeContainer.setSclass("yw-editorarea-tabbox-tabpanels-tabpanel-groupbox-ed");
                    this.renderNotReadableLabel(attributeContainer, attribute, dataType, this.getLabelService().getAccessDeniedLabel(attribute));
                    attributeContainer.setParent(component);
                    return;
                }

                this.createAttributeRenderer().render(component, attribute, returnRequest.getClazz(), returnRequest, widgetInstanceManager);
                final ReturnRequestModel returnRequestModel = (ReturnRequestModel) object;
                if (returnRequestModel.getSipab() != null && this.editor != null) {
                    this.editor.setInitialValue(getSipabValues(returnRequestModel.getSipab()));
                }
            } catch (TypeNotFoundException var10) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn(var10.getMessage(), var10);
                }
            }

        }
    }

    private String getSipabValues(SipabbModel sipab) {
        String result = RESULT;
        if (StringUtils.isNotEmpty(sipab.getFolioSipab())) {
            result += sipab.getFolioSipab() + PIPE;
        }
        if (StringUtils.isNotEmpty(sipab.getAnio())) {
            result += sipab.getAnio() + PIPE;
        }
        if (StringUtils.isNotEmpty(sipab.getEstatus())) {
            result += sipab.getEstatus() + PIPE;
        }
        if (StringUtils.isNotEmpty(sipab.getImporte())) {
            result += sipab.getImporte() + PIPE;
        }
        if (StringUtils.isNotEmpty(sipab.getNumeroDeOrdenCommerce())) {
            result += sipab.getNumeroDeOrdenCommerce() + PIPE;
        }
        if (StringUtils.isNotEmpty(sipab.getItemCommerce())) {
            result += sipab.getItemCommerce() + PIPE;
        }
        if (StringUtils.isNotEmpty(sipab.getFechaDeCreacion())) {
            result += sipab.getFechaDeCreacion() + PIPE;
        }
        if (StringUtils.isNotEmpty(sipab.getFechaDeCierreDeFolio())) {
            result += sipab.getFechaDeCierreDeFolio() + PIPE;
        }
        if (StringUtils.isNotEmpty(sipab.getFechaDeActualizacionEstatus())) {
            result += sipab.getFechaDeActualizacionEstatus() + PIPE;
        }
        if (StringUtils.isNotEmpty(sipab.getFacturaTelcel())) {
            result += sipab.getFacturaTelcel()+ PIPE;
        }
        if (StringUtils.isNotEmpty(sipab.getConsignments())) {
            result += sipab.getConsignments();
        }

        return result;
    }

    protected Editor createEditor(DataType genericType, WidgetInstanceManager widgetInstanceManager, Attribute attribute, Object object) {
        DataAttribute genericAttribute = genericType.getAttribute(attribute.getQualifier());
        if (genericAttribute == null) {
            return null;
        } else {
            String editorSClass = "yw-editorarea-tabbox-tabpanels-tabpanel-groupbox-ed-editor";
            boolean editable = !attribute.isReadonly() && this.canChangeProperty(genericAttribute, object);
            if (!editable) {
                editorSClass = "ye-default-editor-readonly";
            }

            String qualifier = genericAttribute.getQualifier();
            String referencedModelProperty = "sipab." + attribute.getQualifier();
            Editor newEditor = new Editor();
            newEditor.setReadOnly(!editable);
            newEditor.setLocalized(Boolean.FALSE);
            newEditor.setWidgetInstanceManager(widgetInstanceManager);
            newEditor.setType(this.resolveEditorType(genericAttribute));
            newEditor.setOptional(!genericAttribute.isMandatory());
            YTestTools.modifyYTestId(newEditor, "editor_sipab." + qualifier);
            newEditor.setAttribute("parentObject", object);
            newEditor.setWritableLocales(this.getPermissionFacade().getWritableLocalesForInstance(object));
            newEditor.setReadableLocales(this.getPermissionFacade().getReadableLocalesForInstance(object));
            if (genericAttribute.isLocalized()) {
                newEditor.addParameter("localizedEditor.attributeDescription", this.getAttributeDescription(genericType, attribute));
            }

            newEditor.setProperty(referencedModelProperty);
            if (StringUtils.isNotBlank(attribute.getEditor())) {
                newEditor.setDefaultEditor(attribute.getEditor());
            }

            newEditor.setPartOf(genericAttribute.isPartOf());
            newEditor.setOrdered(genericAttribute.isOrdered());
            newEditor.afterCompose();
            newEditor.setSclass(editorSClass);
            newEditor.setInitialValue(this.releaseResourceValue);
            this.editor = newEditor;
            return newEditor;
        }
    }

    /**
     * Gets type facade.
     *
     * @return the type facade
     */
    public TypeFacade getTypeFacade() {
        return typeFacade;
    }

    /**
     * Sets type facade.
     *
     * @param typeFacade the type facade
     */
    public void setTypeFacade(TypeFacade typeFacade) {
        this.typeFacade = typeFacade;
    }

    @Override
    public LabelService getLabelService() {
        return labelService;
    }

    @Override
    public void setLabelService(LabelService labelService) {
        this.labelService = labelService;
    }
}
