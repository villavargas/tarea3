/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.backoffice.omsbackoffice.widgets.replacement.renders;

import mx.com.telcel.backoffice.omsbackoffice.widgets.replacement.dtos.WarehouseSelectorDTO;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;


/**
 * The type Warehouse selector combobox render.
 */
public class WarehouseSelectorComboboxRender implements ComboitemRenderer {

    private static final String SPACE = " ";

    @Override
    public void render(Comboitem comboitem, Object o, int i) {
        WarehouseSelectorDTO warehouseSelectorDTO = (WarehouseSelectorDTO) o;
        comboitem.setLabel(warehouseSelectorDTO.getCentro() + SPACE + warehouseSelectorDTO.getAlmacen());
        comboitem.setTooltiptext(warehouseSelectorDTO.getAlmacen() + SPACE + warehouseSelectorDTO.getCentro());
        //comboitem.setDescription(warehouseSelectorDTO.getCentro());
        comboitem.setAttribute("warehouse", warehouseSelectorDTO);
    }
}
