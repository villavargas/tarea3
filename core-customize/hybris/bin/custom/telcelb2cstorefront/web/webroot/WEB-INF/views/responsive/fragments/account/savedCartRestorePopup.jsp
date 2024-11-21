<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:htmlEscape defaultHtmlEscape="true"/>

<spring:url var="restoreSavedCartPostUrl"
       value="/my-account/saved-carts/{/savedCartCode}/restore" htmlEscape="false">
    <spring:param name="savedCartCode" value="${commerceSaveCartResultData.savedCartData.code}"/>
</spring:url>

<div id="popup_confirm_savedcart_restore" class="savedcart_restore_confirm_modal js-savedcart_restore_confirm_modal">

    <p><spring:theme code="text.account.savedcart.to.activecart"/></p>

    <div class="modal-details row">
        <span class="col-xs-6"><spring:theme code="text.account.savedcart.cart.name"/>:</span>
        <span class="col-xs-6"><b>${fn:escapeXml(commerceSaveCartResultData.savedCartData.name)}</b></span>
        <span class="col-xs-6"><spring:theme code="text.account.savedcart.cart.id"/>:</span>
        <span class="col-xs-6"><b>${fn:escapeXml(commerceSaveCartResultData.savedCartData.code)}</b></span>
        <span class="col-xs-6"><spring:theme code="text.account.savedcart.numberofproducts"/>:</span>
        <span class="col-xs-6"><b>${fn:escapeXml(commerceSaveCartResultData.savedCartData.totalUnitCount)}</b></span>
    </div>
    <br/>

    <label for="keepRestoredCart">
        <input type="checkbox" id="keepRestoredCart" name="keepRestoredCart" class="js-keep-restored-cart" checked="checked">
        <spring:theme code="text.account.savedcart.retain.savedcart"/>
    </label>

    <c:if test="${hasSessionCart eq true}">
        <div class="restore-current-cart-form js-restore-current-cart-form">
            <div class="form-group">
                <p><spring:theme code="text.account.savedcart.activecart.msg"/></p>
                <input type="text" id="activeCartName" name="activeCartName" class="text form-control js-current-cart-name" value="${fn:escapeXml(autoGeneratedName)}" maxlength="255"/>
                <div class="js-restore-error-container help-block" />
            </div>
            <label for="preventSaveActiveCart">
                <input type="checkbox" id="preventSaveActiveCart" name="preventSaveActiveCart" class="js-prevent-save-active-cart">
                <spring:theme code="text.account.savedcart.save.activecart"/>
            </label>
        </div>
    </c:if>
    <div class="modal-actions">
        <div class="row">
            <div class="col-xs-12 col-sm-6 col-sm-push-6">
                <button type="button" class="js-save-cart-restore-btn btn btn-primary btn-block"
                        data-restore-url="${fn:escapeXml(restoreSavedCartPostUrl)}">
                    <spring:theme code="text.account.savedcart.restore.btn"/>
                </button>
            </div>
            <div class="col-xs-12 col-sm-6 col-sm-pull-6">
                <button type="button" class="js-cancel-restore-btn btn btn-default btn-block">
                    <spring:theme code="text.button.cancel"/>
                </button>
            </div>
        </div>
    </div>
</div>