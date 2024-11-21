package mx.com.telcel.controllers;

import com.google.gson.Gson;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import mx.com.telcel.facades.externalorder.data.Order;
import mx.com.telcel.facades.orders.TelcelOrdersFacade;
import mx.com.telcel.response.models.MessageResponse;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/external/order")
@CacheControl(directive = CacheControlDirective.NO_STORE)
@Api(tags = "Codigos Postales Telcel")
public class TelcelExternalOrdersController {

    @Resource
    private BusinessProcessService businessProcessService;

    @Resource
    private ModelService modelService;

    @Resource(name = "telcelOrdersFacade")
    private TelcelOrdersFacade telcelOrdersFacade;

    private static final Logger LOG = Logger.getLogger(TelcelExternalOrdersController.class);
    private static final String ORDER_PROCESS_TELMEX = "order-process-telmex";

    @PostMapping( produces = MediaType.APPLICATION_JSON_VALUE )
    @ResponseBody
    @ApiOperation(nickname = "createOrder", value = "Crear orden de servicios externos.",
            notes = "Creacion de ordenes para servicios externos.",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public MessageResponse createOrder(@RequestBody Order order) {

        LOG.info("External Order Controller " + new Gson().toJson(order) );

        final OrderModel orderModel = telcelOrdersFacade.externalOrderCreate(order);
        startOrderProcess(orderModel);
        MessageResponse response = new MessageResponse();
        response.setResponseCode("200");
        response.setResponseMessage("success");

        return response;
    }

    private void startOrderProcess(OrderModel orderModel) {
        final String processCode = ORDER_PROCESS_TELMEX + "-"+ orderModel.getCode() + "-" + System.currentTimeMillis();
        final OrderProcessModel businessProcessModel = businessProcessService.createProcess(processCode,
                ORDER_PROCESS_TELMEX);
        businessProcessModel.setOrder(orderModel);
        modelService.save(businessProcessModel);
        businessProcessService.startProcess(businessProcessModel);
        if (LOG.isInfoEnabled())
        {
            LOG.info(String.format("Started the telmex process %s", processCode));
        }
    }

}
