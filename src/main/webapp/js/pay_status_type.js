function fomcatPayStatus(payStatus){
	if(strIsEmpty(payStatus)){
		return "";
	}
	if(payStatus == 0){
		return "已支付";
	}
	if(payStatus == 1){
		return "待支付";
	}
	if(payStatus == 2){
		return "已退款";
	}
	return payStatus;
}
function fomcatPayStatusIcon(payStatus){
	if(strIsEmpty(payStatus)){
		return "";
	}
	if(payStatus == 0){
		return "&#xe605;";
	}
	if(payStatus == 1){
		return "&#x1006;";
	}
	if(payStatus == 2){
		return "&#xe603;";
	}
	return payStatus;
}

function fomcatPayType(payType){
	if(strIsEmpty(payType)){
		return "";
	}
	if(payType == 1){
		return "余额支付";
	}
	if(payType == 2){
		return "微信支付";
	}
	if(payType == 3){
		return "支付宝支付";
	}
	if(payType == 4){
		return "银联支付";
	}
	if(payType == 5){
		return "现金支付";
	}
	if(payType == 6){
		return "系统退款";
	}
	if(payType == 7){
		return "系统充值";
	}
	if(payType == 8){
		return "更新余额";
	}
	if(payType == 9){
		return "所得奖金";
	}
	return payType;
}