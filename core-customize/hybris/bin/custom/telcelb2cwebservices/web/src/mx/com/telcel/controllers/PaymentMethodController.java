package mx.com.telcel.controllers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import mx.com.telcel.core.daos.msiconfiguration.MSIConfigurationDao;
import mx.com.telcel.core.model.BankPromotionConfigModel;
import mx.com.telcel.core.model.PaymentMessageConfigModel;
import mx.com.telcel.models.msiconfiguration.PaymentDetail;
import mx.com.telcel.models.msiconfiguration.PaymentMethod;
import mx.com.telcel.models.msiconfiguration.PaymentOption;


@Controller
@RequestMapping(value = "/paymentmethod")
public class PaymentMethodController
{

	@Resource(name = "msiConfigurationDao")
	private MSIConfigurationDao msiConfigurationDao;

	private final DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");

	private static final Logger LOG = Logger.getLogger(PaymentMethodController.class);

	@ResponseBody
	@GetMapping(value = "/methods", produces = "application/json")
	public List<PaymentMethod> promotions()
	{
		LOG.info("Get Payment Methods");
		final List<PaymentMethod> methods = new ArrayList<>();
		final List<BankPromotionConfigModel> bankPromotions = msiConfigurationDao.getBankPromotions();
		final List<PaymentMessageConfigModel> payMsgsCash = msiConfigurationDao.getPayMessagesCash();
		final List<PaymentMessageConfigModel> payMsgsSpei = msiConfigurationDao.getPayMessagesSPEI();
		if (Objects.nonNull(bankPromotions))
		{
			final PaymentMethod method = new PaymentMethod();
			method.setMethod("Promociones <br> Bancarias");
			method.setOptions(bankPromotions(bankPromotions));
			methods.add(method);
		}
		if (Objects.nonNull(payMsgsCash))
		{
			final PaymentMethod method = new PaymentMethod();
			method.setMethod("Pago en <br> efectivo");
			method.setOptions(paymentMessages(payMsgsCash));
			methods.add(method);
		}
		if (Objects.nonNull(payMsgsSpei))
		{
			final PaymentMethod method = new PaymentMethod();
			method.setMethod("Transferencias <br> SPEI");
			method.setOptions(paymentMessages(payMsgsSpei));
			methods.add(method);
		}
		return methods;
	}

	private List<PaymentOption> bankPromotions(final List<BankPromotionConfigModel> promotions)
	{
		final List<PaymentOption> bankPromotions = new ArrayList<>();
		final List<String> banks = getBanks(promotions);
		final List<String> bankLogos = getBankLogos(promotions);
		int i = 0;
		for (final String bank : banks)
		{
			final PaymentOption header = new PaymentOption();
			final List<PaymentDetail> details = new ArrayList<>();
			for (final BankPromotionConfigModel prom : promotions)
			{
				if (prom.getBankName().equals(bank))
				{
					final PaymentDetail detail = new PaymentDetail();
					detail.setMonth(prom.getMonth());
					detail.setAmount(prom.getMinAmount());
					final String lbl = "<b>" + prom.getMonth() + " meses sin intereses" + "</b>" + ", " + "monto m&iacute;nimo " + "$"
							+ decimalFormat.format(prom.getMinAmount());
					detail.setLabel(lbl);
					details.add(detail);
				}
			}
			header.setOption(bank);
			header.setLogo(bankLogos.get(i++));
			header.setDetail(details);
			bankPromotions.add(header);
		}
		return bankPromotions;
	}

	private List<PaymentOption> paymentMessages(final List<PaymentMessageConfigModel> payMsgs)
	{
		final List<PaymentOption> bankPromotions = new ArrayList<>();
		final List<String> titles = getTitles(payMsgs);
		final List<String> msgLogos = getMsgLogos(payMsgs);
		int i = 0;
		for (final String title : titles)
		{
			final PaymentOption header = new PaymentOption();
			final List<PaymentDetail> details = new ArrayList<>();
			for (final PaymentMessageConfigModel msg : payMsgs)
			{
				if (msg.getTitle().equals(title))
				{
					final PaymentDetail detail = new PaymentDetail();
					detail.setContent(msg.getDescription());
					details.add(detail);
				}
			}
			header.setOption(title);
			header.setLogo(msgLogos.get(i++));
			header.setDetail(details);
			bankPromotions.add(header);
		}
		return bankPromotions;
	}

	private List<String> getBanks(final List<BankPromotionConfigModel> promotions)
	{
		return promotions.stream().map(BankPromotionConfigModel::getBankName).distinct().collect(Collectors.toList());
	}

	private List<String> getBankLogos(final List<BankPromotionConfigModel> promotions)
	{
		return promotions.stream().map(BankPromotionConfigModel::getLogo).distinct().collect(Collectors.toList());
	}

	private List<String> getTitles(final List<PaymentMessageConfigModel> messages)
	{
		return messages.stream().map(PaymentMessageConfigModel::getTitle).distinct().collect(Collectors.toList());
	}

	private List<String> getMsgLogos(final List<PaymentMessageConfigModel> messages)
	{
		return messages.stream().map(PaymentMessageConfigModel::getLogo).distinct().collect(Collectors.toList());
	}

}
