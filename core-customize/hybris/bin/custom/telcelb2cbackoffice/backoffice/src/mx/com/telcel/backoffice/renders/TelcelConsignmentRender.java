/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.backoffice.renders;

import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.AbstractPanel;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.Attribute;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.CustomPanel;
import com.hybris.cockpitng.core.model.ModelObserver;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.util.YTestTools;
import com.hybris.cockpitng.widgets.editorarea.renderer.impl.DefaultEditorAreaPanelRenderer;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Div;

/**
 * The type Telcel consignment render.
 */
public class TelcelConsignmentRender extends DefaultEditorAreaPanelRenderer {

    private static final Logger LOG = LoggerFactory.getLogger(TelcelConsignmentRender.class);

    private String resultValue = "";
    private Editor editor;
    private TypeFacade typeFacade;
    private LabelService labelService;

    /**
     * Instantiates a new Telcel consignment render.
     */
    public TelcelConsignmentRender() {
    }

    @Override
    public void render(Component component, AbstractPanel abstractPanelConfiguration, Object object, DataType dataType, WidgetInstanceManager widgetInstanceManager) {
        if (abstractPanelConfiguration instanceof CustomPanel && object instanceof ReturnRequestModel) {
            try {
                Attribute attribute = new Attribute();
                attribute.setLabel("customersupportbackoffice.consigments.order");
                attribute.setQualifier("code");
                attribute.setReadonly(Boolean.TRUE);
                DataType returnRequest = this.getTypeFacade().load("Consignment");
                boolean canReadObject = this.getPermissionFacade().canReadInstanceProperty(returnRequest.getClazz(), "code");
                if (!canReadObject) {
                    Div attributeContainer = new Div();
                    attributeContainer.setSclass("yw-editorarea-tabbox-tabpanels-tabpanel-groupbox-ed");
                    this.renderNotReadableLabel(attributeContainer, attribute, dataType, this.getLabelService().getAccessDeniedLabel(attribute));
                    attributeContainer.setParent(component);
                    return;
                }

                this.createAttributeRenderer().render(component, attribute, returnRequest.getClazz(), returnRequest, widgetInstanceManager);
                final ReturnRequestModel returnRequestModel = (ReturnRequestModel) object;
                if (CollectionUtils.isNotEmpty(returnRequestModel.getReturnEntries()) && this.editor != null &&
                        returnRequestModel.getReturnEntries().get(0) instanceof RefundEntryModel) {
                    final RefundEntryModel refundEntryModel = (RefundEntryModel) returnRequestModel.getReturnEntries().get(0);
                    final ConsignmentModel consignmentModel = refundEntryModel.getConsignment();
                    if (consignmentModel != null) {
                        this.editor.setInitialValue(consignmentModel.getCode());
                    }
                }
            } catch (TypeNotFoundException var10) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn(var10.getMessage(), var10);
                }
            }

        }


    }

    @Override
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
            String referencedModelProperty = "consignment." + attribute.getQualifier();
            Editor newEditor = new Editor();
            newEditor.setReadOnly(!editable);
            newEditor.setLocalized(Boolean.FALSE);
            newEditor.setWidgetInstanceManager(widgetInstanceManager);
            newEditor.setType(this.resolveEditorType(genericAttribute));
            newEditor.setOptional(!genericAttribute.isMandatory());
            YTestTools.modifyYTestId(newEditor, "editor_consignment." + qualifier);
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
            newEditor.setInitialValue(this.resultValue);
            this.editor = newEditor;
            return newEditor;
        }
    }

    /**
     * Is reference editor boolean.
     *
     * @param genericAttribute the generic attribute
     * @return the boolean
     */
    protected boolean isReferenceEditor(DataAttribute genericAttribute) {
        return genericAttribute.getValueType() != null && !genericAttribute.getValueType().isAtomic();
    }

    /**
     * Gets type facade.
     *
     * @return the type facade
     */
    protected TypeFacade getTypeFacade() {
        return this.typeFacade;
    }

    /**
     * Sets type facade.
     *
     * @param typeFacade the type facade
     */
    @Required
    public void setTypeFacade(TypeFacade typeFacade) {
        this.typeFacade = typeFacade;
    }

    protected LabelService getLabelService() {
        return this.labelService;
    }

    @Required
    public void setLabelService(LabelService labelService) {
        this.labelService = labelService;
    }
}
