/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.backoffice.renders;

import com.hybris.cockpitng.widgets.editorarea.renderer.impl.DefaultEditorAreaPanelRenderer;
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
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Div;

public class TelcelRefundAmountRenderer extends DefaultEditorAreaPanelRenderer {


    private static final Logger LOG = LoggerFactory.getLogger(TelcelRefundAmountRenderer.class);
    protected static final String REFUND_AMOUNT_OBSERVER_ID = "refundAmountObserver";
    protected static final String REFUND_ENTRY = "RefundEntry";
    protected static final String QUALIFIER = "amount";
    protected static final String CURRENT_OBJECT = "currentObject";
    private Editor editor;
    private TypeFacade typeFacade;
    private BigDecimal totalRefundAmount;
    private LabelService labelService;

    public TelcelRefundAmountRenderer() {
    }

    public void render(Component component, AbstractPanel abstractPanelConfiguration, Object object, DataType dataType, WidgetInstanceManager widgetInstanceManager) {
        if (abstractPanelConfiguration instanceof CustomPanel && object instanceof ReturnRequestModel) {
            this.totalRefundAmount = this.getOrderRefundAmount((ReturnRequestModel) object);

            try {
                Attribute attribute = new Attribute();
                attribute.setLabel("customersupportbackoffice.returnentry.totalrefundamount");
                attribute.setQualifier("amount");
                attribute.setReadonly(Boolean.TRUE);
                DataType refundEntry = this.getTypeFacade().load("RefundEntry");
                boolean canReadObject = this.getPermissionFacade().canReadInstanceProperty(refundEntry.getClazz(), "amount");
                if (!canReadObject) {
                    Div attributeContainer = new Div();
                    attributeContainer.setSclass("yw-editorarea-tabbox-tabpanels-tabpanel-groupbox-ed");
                    this.renderNotReadableLabel(attributeContainer, attribute, dataType, this.getLabelService().getAccessDeniedLabel(attribute));
                    attributeContainer.setParent(component);
                    return;
                }

                this.createAttributeRenderer().render(component, attribute, refundEntry.getClazz(), refundEntry, widgetInstanceManager);
                final WidgetModel widgetInstanceModel = widgetInstanceManager.getModel();
                widgetInstanceModel.removeObserver("refundAmountObserver");
                widgetInstanceModel.addObserver("currentObject", new ModelObserver() {
                    public void modelChanged() {
                        if (ReturnRequestModel.class.equals(widgetInstanceModel.getValueType("currentObject"))) {
                            ReturnRequestModel currentReturnRequest = (ReturnRequestModel) widgetInstanceModel.getValue("currentObject", ReturnRequestModel.class);
                            if (currentReturnRequest != null) {
                                TelcelRefundAmountRenderer.this.totalRefundAmount = TelcelRefundAmountRenderer.this.getOrderRefundAmount(currentReturnRequest);
                                TelcelRefundAmountRenderer.this.editor.setInitialValue(TelcelRefundAmountRenderer.this.totalRefundAmount);
                            }

                        }
                    }

                    public String getId() {
                        return "refundAmountObserver";
                    }
                });
            } catch (TypeNotFoundException var10) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn(var10.getMessage(), var10);
                }
            }

        }
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
            String referencedModelProperty = "RefundEntry." + attribute.getQualifier();
            Editor newEditor = this.createEditor(genericAttribute, widgetInstanceManager.getModel(), referencedModelProperty);
            newEditor.setReadOnly(!editable);
            newEditor.setLocalized(Boolean.FALSE);
            newEditor.setWidgetInstanceManager(widgetInstanceManager);
            newEditor.setType(this.resolveEditorType(genericAttribute));
            newEditor.setOptional(!genericAttribute.isMandatory());
            YTestTools.modifyYTestId(newEditor, "editor_RefundEntry." + qualifier);
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
            newEditor.setInitialValue(this.totalRefundAmount);
            this.editor = newEditor;
            return newEditor;
        }
    }

    protected Editor createEditor(DataAttribute genericAttribute, final WidgetModel model, final String referencedModelProperty) {
        if (this.isReferenceEditor(genericAttribute)) {
            final ModelObserver referenceObserver = new ModelObserver() {
                public void modelChanged() {
                }
            };
            model.addObserver(referencedModelProperty, referenceObserver);
            return new Editor() {
                public void destroy() {
                    super.destroy();
                    model.removeObserver(referencedModelProperty, referenceObserver);
                }
            };
        } else {
            return new Editor();
        }
    }

    protected BigDecimal getOrderRefundAmount(ReturnRequestModel returnRequest) {
        BigDecimal refundAmount = (BigDecimal) returnRequest.getReturnEntries().stream().map((returnEntry) -> {
            return this.getRefundEntryAmount(returnEntry);
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (returnRequest.getRefundDeliveryCost() != null && returnRequest.getRefundDeliveryCost()) {
            refundAmount = refundAmount.add(BigDecimal.valueOf(returnRequest.getOrder().getDeliveryCost()));
        }
        if (returnRequest.getRefundAdditionalService() && returnRequest.getAdditionalServiceValue() != null &&
                returnRequest.getAdditionalServiceValue() > 0D) {
            refundAmount = refundAmount.add(BigDecimal.valueOf(returnRequest.getAdditionalServiceValue()));
        }

        return refundAmount.setScale(returnRequest.getOrder().getCurrency().getDigits(), 3);
    }

    protected BigDecimal getRefundEntryAmount(ReturnEntryModel returnEntryModel) {
        ReturnRequestModel returnRequest = returnEntryModel.getReturnRequest();
        BigDecimal refundEntryAmount = BigDecimal.ZERO;
        if (returnEntryModel instanceof RefundEntryModel) {
            RefundEntryModel refundEntry = (RefundEntryModel) returnEntryModel;
            if (refundEntry.getAmount() != null) {
                refundEntryAmount = refundEntry.getAmount();
                refundEntryAmount = refundEntryAmount.setScale(returnRequest.getOrder().getCurrency().getDigits(), RoundingMode.HALF_DOWN);
            }
        }

        return refundEntryAmount;
    }

    protected boolean isReferenceEditor(DataAttribute genericAttribute) {
        return genericAttribute.getValueType() != null && !genericAttribute.getValueType().isAtomic();
    }

    protected TypeFacade getTypeFacade() {
        return this.typeFacade;
    }

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
