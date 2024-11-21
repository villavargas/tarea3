/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.backoffice.omsbackoffice.widgets.replacement.dtos;

/**
 * The type Warehouse selector dto.
 */
public class WarehouseSelectorDTO {
    /**
     * The Almacen.
     */
    String almacen;
    /**
     * The Centro.
     */
    String centro;
    /**
     * The Sim.
     */
    String sim = "";

    /**
     * Gets almacen.
     *
     * @return the almacen
     */
    public String getAlmacen() {
        return almacen;
    }

    /**
     * Sets almacen.
     *
     * @param almacen the almacen
     */
    public void setAlmacen(String almacen) {
        this.almacen = almacen;
    }

    /**
     * Gets centro.
     *
     * @return the centro
     */
    public String getCentro() {
        return centro;
    }

    /**
     * Sets centro.
     *
     * @param centro the centro
     */
    public void setCentro(String centro) {
        this.centro = centro;
    }

    /**
     * Gets sim.
     *
     * @return the sim
     */
    public String getSim() {
        return sim;
    }

    /**
     * Sets sim.
     *
     * @param sim the sim
     */
    public void setSim(String sim) {
        this.sim = sim;
    }
}
