function isMoney(arg){//22,111,22.11   判断是否是金额
	if(arg == null || arg == undefined || arg.length < 1){
		return false;
	}
	arg = arg.toString();
	argChar = "0123456789.,";
	var beginArg = arg.substring(0,1);
	if(beginArg == "." || beginArg==","){
		return false;
	}
	
	
	for(var i = 0; i < arg.length; i ++){
		if(argChar.indexOf(arg.substring(i,i+1)) == -1){
			return false;
		}
		return true;
	}
}

function checkMoney(money){
	var reg = /(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)/;
	if (reg.test(money)) {
		return true;
	} else{
		return false;
	}
}
