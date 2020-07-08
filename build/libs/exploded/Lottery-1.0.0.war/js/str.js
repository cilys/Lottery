function strIsEmpty(str){
	return (str == null || str == "" || str == undefined || str == "undefined" || str.length == 0 || str.indexOf("null") > -1);
}

function strFomcat(str){
	return strIsEmpty(str) ? "" : str;
}

function strFomcatUserName(realName, userName){
	var r = strFomcat(realName);
	
	return strIsEmpty(r) ? userName : r;
}



function fomcatEnable(str){
	if(strIsEmpty(str)){
		return "禁用";
	}
	
	if(str == "0"){
		return "可用";
	}
	
	return "禁用";
}

function fomcatSex(str){
	if(strIsEmpty(str)){
		return "保密";
	}
	if(str == "0"){
		return "未知";
	}
	if(str == "1"){
		return "男";
	}
	if(str == "2"){
		return "女";
	}
	return "保密";
}

function getUrlParam(queryName) {
    var query = decodeURI(window.location.search.substring(1));
    var vars = query.split("&");
    for (var i = 0; i < vars.length; i++) {
        var pair = vars[i].split("=");
        if (pair[0] == queryName) { return pair[1]; }
    }
    return null;
}

function showToast(str){
	layer.msg(str)
}
