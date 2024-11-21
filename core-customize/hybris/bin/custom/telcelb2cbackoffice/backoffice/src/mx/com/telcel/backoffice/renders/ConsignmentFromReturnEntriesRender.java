/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.backoffice.renders;

import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListColumn;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.common.AbstractWidgetComponentRenderer;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import org.apache.commons.collections.CollectionUtils;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Label;

import java.util.List;

public class ConsignmentFromReturnEntriesRender extends AbstractWidgetComponentRenderer<Listcell, ListColumn, Object> {

    @Override
    public void render(Listcell parent, ListColumn configuration, Object object, DataType dataType, WidgetInstanceManager widgetInstanceManager) {
        if (object instanceof ReturnRequestModel) {
            final ReturnRequestModel returnRequestModel = (ReturnRequestModel) object;
            final List<ReturnEntryModel> returnEntryModels = returnRequestModel.getReturnEntries();
            if (CollectionUtils.isNotEmpty(returnEntryModels)) {
                final RefundEntryModel entryModel = (RefundEntryModel) returnEntryModels.get(0);
                final ConsignmentModel consignmentModel = entryModel.getConsignment();
                if (consignmentModel != null) {
                    this.renderComponents(parent, configuration, object, consignmentModel.getCode());
                }
            }
        }
    }


    protected void renderComponents(final Listcell parent, final ListColumn configuration, final Object object,
                                    final String labelText) {
        final Label label = new Label(labelText);
        label.setAttribute("hyperlink-candidate", Boolean.TRUE);
        parent.appendChild(label);
        this.fireComponentRendered(label, parent, configuration, object);
        this.fireComponentRendered(parent, configuration, object);
    }
}
