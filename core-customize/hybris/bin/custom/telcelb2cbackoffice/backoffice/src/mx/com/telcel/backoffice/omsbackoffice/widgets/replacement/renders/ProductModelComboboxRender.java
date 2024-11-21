/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.backoffice.omsbackoffice.widgets.replacement.renders;

import de.hybris.platform.core.model.product.ProductModel;
import mx.com.telcel.core.model.ColorModel;
import mx.com.telcel.core.model.TelcelPoVariantModel;
import mx.com.telcel.core.model.TelcelSimpleProductOfferingModel;
import org.apache.commons.lang3.StringUtils;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;

import java.util.Locale;

/**
 * The type Product model combobox render.
 */
public class ProductModelComboboxRender implements ComboitemRenderer {


    @Override
    public void render(Comboitem comboitem, Object o, int i) throws Exception {
        ProductModel productModel = (ProductModel) o;
        comboitem.setLabel(productModel.getSku());
        String name = productModel.getName();
        if (StringUtils.isEmpty(name)) {
            name = productModel.getName(new Locale("es", "MX"));
        }
        if (productModel instanceof TelcelPoVariantModel) {
            final TelcelPoVariantModel telcelPoVariantModel = (TelcelPoVariantModel) productModel;
            String tooltip = "";
            if (telcelPoVariantModel.getTmaBasePo() != null) {
                final TelcelSimpleProductOfferingModel tmaSimpleProductOfferingModel = (TelcelSimpleProductOfferingModel) telcelPoVariantModel.getTmaBasePo();
                final String marca = tmaSimpleProductOfferingModel.getMarca();
                final String modelo = tmaSimpleProductOfferingModel.getModelo();
                if (StringUtils.isNotEmpty(marca)) {
                    tooltip += marca;
                }
                if (StringUtils.isNotEmpty(modelo)) {
                    tooltip = StringUtils.isEmpty(tooltip) ? modelo : (tooltip + "|" + modelo);
                }
            }
            final ColorModel colorModel = telcelPoVariantModel.getColor();
            if (colorModel != null) {
                name = StringUtils.isEmpty(name) ? colorModel.getCode() : (name + "|" + colorModel.getCode());
            }
            comboitem.setTooltiptext(tooltip);
        }
        comboitem.setDescription(name);
        comboitem.setAttribute("product", productModel);
    }
}
