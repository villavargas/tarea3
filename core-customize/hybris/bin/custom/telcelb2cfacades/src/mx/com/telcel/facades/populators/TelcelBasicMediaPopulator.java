/*
 *
 * [y] hybris Platform
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package mx.com.telcel.facades.populators;

import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.cmsfacades.media.populator.BasicMediaPopulator;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class TelcelBasicMediaPopulator extends BasicMediaPopulator {


    @Override
    public void populate(final MediaModel source, final MediaData target) throws ConversionException
    {
        super.populate(source,target);
        target.setTitleText(source.getTitleText());
    }
}
